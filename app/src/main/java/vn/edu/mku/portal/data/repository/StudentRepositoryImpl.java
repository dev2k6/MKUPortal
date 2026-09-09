/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.repository;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.AppCacheManager;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.data.network.ApiClient;
import vn.edu.mku.portal.data.network.model.AccountFeeItem;
import vn.edu.mku.portal.data.network.model.BehaviorScoreItem;
import vn.edu.mku.portal.data.network.model.CommentItem;
import vn.edu.mku.portal.data.network.model.ContactSubmitRequest;
import vn.edu.mku.portal.data.network.model.DecisionItem;
import vn.edu.mku.portal.data.network.model.DepartmentItem;
import vn.edu.mku.portal.data.network.model.DrawingScheduleResponse;
import vn.edu.mku.portal.data.network.model.ExamItem;
import vn.edu.mku.portal.data.network.model.FooterInfoItem;
import vn.edu.mku.portal.data.network.model.MarkDetailItem;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.OrderDetailItem;
import vn.edu.mku.portal.data.network.model.OrderInfoRequest;
import vn.edu.mku.portal.data.network.model.OrderInfoResponse;
import vn.edu.mku.portal.data.network.model.PeriodScheduleResponse;
import vn.edu.mku.portal.data.network.model.StudentInfoResponse;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.network.model.StudyProgramDetailResponse;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.data.network.model.WeekItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;
import vn.edu.mku.portal.ui.common.NetworkMonitor;

public class StudentRepositoryImpl implements StudentRepository {

    private final AppCacheManager cacheManager;
    private final NetworkMonitor networkMonitor;
    private final SessionManager sessionManager;

    public StudentRepositoryImpl() {
        this.cacheManager = AppCacheManager.getInstance();
        this.networkMonitor = NetworkMonitor.getInstance();
        this.sessionManager = SessionManager.getInstance();
    }

    private String studentKey(String suffix) {
        String id = sessionManager.getStudentId();
        if (TextUtils.isEmpty(id)) {
            id = "guest";
        }
        return id + "_" + suffix;
    }

    private interface OnSuccessHook<T> {
        void onHook(T body);
    }

    private <T> void executeCached(
            String cacheKey,
            Type typeToken,
            long ttlMillis,
            Call<T> apiCall,
            ApiCallback<T> callback,
            String errorMessagePrefix,
            OnSuccessHook<T> successHook
    ) {
        // 1. Check valid in-memory / disk cache
        T cached = cacheManager.get(cacheKey, typeToken);
        if (cached != null) {
            if (successHook != null) successHook.onHook(cached);
            callback.onSuccess(cached);
            return;
        }

        // 2. If offline, attempt stale cache fallback
        if (!networkMonitor.isOnline()) {
            T stale = cacheManager.getOrStale(cacheKey, typeToken);
            if (stale != null) {
                if (successHook != null) successHook.onHook(stale);
                callback.onSuccess(stale);
                return;
            }
            callback.onError(LanguageManager.getInstance().getAppString(R.string.text_no_network_no_cache));
            return;
        }

        // 3. Fetch from remote API
        apiCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    T body = response.body();
                    cacheManager.put(cacheKey, body, ttlMillis);
                    if (successHook != null) successHook.onHook(body);
                    callback.onSuccess(body);
                } else {
                    // Fallback to stale cache if server returns error
                    T stale = cacheManager.getOrStale(cacheKey, typeToken);
                    if (stale != null) {
                        if (successHook != null) successHook.onHook(stale);
                        callback.onSuccess(stale);
                    } else {
                        callback.onError(errorMessagePrefix + " (" + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                // Fallback to stale cache on network failure
                T stale = cacheManager.getOrStale(cacheKey, typeToken);
                if (stale != null) {
                    if (successHook != null) successHook.onHook(stale);
                    callback.onSuccess(stale);
                } else {
                    callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void fetchStudentInfo(ApiCallback<StudentInfoResponse> callback) {
        String key = studentKey("info");
        executeCached(
                key,
                StudentInfoResponse.class,
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getStudentInfo(),
                callback,
                "Không thể lấy thông tin sinh viên",
                body -> {
                    if (body != null && body.getFirstObj1() != null && !TextUtils.isEmpty(body.getFirstObj1().getStudentName())) {
                        sessionManager.updateStudentName(body.getFirstObj1().getStudentName());
                    }
                }
        );
    }

    @Override
    public void fetchMessages(ApiCallback<List<StudentMessage>> callback) {
        String key = studentKey("messages");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, StudentMessage.class).getType(),
                AppCacheManager.TTL_MINUTES_15,
                ApiClient.getApiService().getMessages(),
                callback,
                "Không thể lấy danh sách thông báo",
                null
        );
    }

    @Override
    public void fetchMenu(String langCode, ApiCallback<List<MenuItem>> callback) {
        String key = studentKey("menu_" + langCode);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, MenuItem.class).getType(),
                AppCacheManager.TTL_DAY,
                ApiClient.getApiService().getMenu(langCode),
                callback,
                "Không thể lấy menu",
                null
        );
    }

    @Override
    public void fetchStudyProgramHeaders(ApiCallback<List<StudyProgramHeader>> callback) {
        String key = studentKey("study_programs");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, StudyProgramHeader.class).getType(),
                AppCacheManager.TTL_DAY,
                ApiClient.getApiService().getStudyProgramHeaders(),
                callback,
                "Không thể lấy danh sách chương trình đào tạo",
                null
        );
    }

    @Override
    public void fetchStudyProgramDetail(String studyProgramId, ApiCallback<StudyProgramDetailResponse> callback) {
        String key = studentKey("study_program_" + studyProgramId);
        executeCached(
                key,
                StudyProgramDetailResponse.class,
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getStudyProgramDetail(studyProgramId),
                callback,
                "Không thể lấy chi tiết chương trình đào tạo",
                null
        );
    }

    @Override
    public void fetchYearAndTerm(ApiCallback<YearAndTermResponse> callback) {
        String key = studentKey("year_and_term");
        executeCached(
                key,
                YearAndTermResponse.class,
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getYearAndTerm(),
                callback,
                "Không thể lấy thông tin năm học / học kỳ",
                null
        );
    }

    @Override
    public void fetchWeekSchedule(String year, String term, ApiCallback<List<WeekItem>> callback) {
        String key = studentKey("week_schedule_" + year + "_" + term);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, WeekItem.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getWeekSchedule(year, term),
                callback,
                "Không thể lấy danh sách tuần học",
                null
        );
    }

    @Override
    public void fetchDrawingSchedules(String year, String term, int week, ApiCallback<DrawingScheduleResponse> callback) {
        String key = studentKey("drawing_schedules_" + year + "_" + term + "_" + week);
        executeCached(
                key,
                DrawingScheduleResponse.class,
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getDrawingSchedules(year, term, week),
                callback,
                "Không thể lấy thời khóa biểu tuần",
                null
        );
    }

    @Override
    public void fetchPeriodSchedules(String year, String term, ApiCallback<PeriodScheduleResponse> callback) {
        String key = studentKey("period_schedules_" + year + "_" + term);
        executeCached(
                key,
                PeriodScheduleResponse.class,
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getPeriodSchedules(year, term),
                callback,
                "Không thể lấy thời khóa biểu thứ - tiết",
                null
        );
    }

    @Override
    public void fetchExams(String year, String term, ApiCallback<List<ExamItem>> callback) {
        String key = studentKey("exams_" + year + "_" + term);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, ExamItem.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getExams(year, term),
                callback,
                "Không thể lấy danh sách lịch thi",
                null
        );
    }

    @Override
    public void fetchDecisions(ApiCallback<List<DecisionItem>> callback) {
        String key = studentKey("decisions");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, DecisionItem.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getDecisions(),
                callback,
                "Không thể lấy quyết định sinh viên",
                null
        );
    }

    @Override
    public void fetchBehaviorScores(ApiCallback<List<BehaviorScoreItem>> callback) {
        String key = studentKey("behavior_scores");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, BehaviorScoreItem.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getBehaviorScores(),
                callback,
                "Không thể lấy điểm rèn luyện",
                null
        );
    }

    @Override
    public void fetchMarks(String programId, String type, ApiCallback<List<MarkYearGroup>> callback) {
        String key = studentKey("marks_" + programId + "_" + type);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, MarkYearGroup.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getMarks(programId, type),
                callback,
                "Không thể lấy điểm học tập",
                null
        );
    }

    @Override
    public void fetchMarkDetail(String scheduleStudyUnitId, ApiCallback<List<MarkDetailItem>> callback) {
        String key = studentKey("mark_detail_" + scheduleStudyUnitId);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, MarkDetailItem.class).getType(),
                AppCacheManager.TTL_HOURS_2,
                ApiClient.getApiService().getMarkDetail(scheduleStudyUnitId),
                callback,
                "Không thể lấy chi tiết điểm",
                null
        );
    }

    @Override
    public void fetchFooterInfo(ApiCallback<List<FooterInfoItem>> callback) {
        String key = "footer_info";
        executeCached(
                key,
                TypeToken.getParameterized(List.class, FooterInfoItem.class).getType(),
                AppCacheManager.TTL_DAY,
                ApiClient.getApiService().getFooterInfo(),
                callback,
                "Không thể lấy thông tin chân trang",
                null
        );
    }

    @Override
    public void fetchAccountFees(ApiCallback<List<AccountFeeItem>> callback) {
        String key = studentKey("account_fees");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, AccountFeeItem.class).getType(),
                AppCacheManager.TTL_MINUTES_30,
                ApiClient.getApiService().getAccountFees(),
                callback,
                "Không thể lấy tài chính sinh viên",
                null
        );
    }

    @Override
    public void fetchOrderDetails(ApiCallback<List<OrderDetailItem>> callback) {
        String key = studentKey("order_details");
        executeCached(
                key,
                TypeToken.getParameterized(List.class, OrderDetailItem.class).getType(),
                AppCacheManager.TTL_MINUTES_30,
                ApiClient.getApiService().getOrderDetails(),
                callback,
                "Không thể lấy chi tiết hóa đơn",
                null
        );
    }

    @Override
    public void fetchOrderInfo(ApiCallback<OrderInfoRequest> callback) {
        String key = studentKey("order_info");
        executeCached(
                key,
                OrderInfoRequest.class,
                AppCacheManager.TTL_MINUTES_30,
                ApiClient.getApiService().getOrderInfo(),
                callback,
                "Không thể lấy thông tin khai báo",
                null
        );
    }

    @Override
    public void submitOrderInfo(OrderInfoRequest request, ApiCallback<OrderInfoResponse> callback) {
        ApiClient.getApiService().submitOrderInfo(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderInfoResponse> call, @NonNull Response<OrderInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Invalidate student specific caches
                    cacheManager.remove(studentKey("order_info"));
                    cacheManager.remove(studentKey("order_details"));
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Cập nhật thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderInfoResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void deleteOrderInfo(OrderInfoRequest request, ApiCallback<OrderInfoResponse> callback) {
        ApiClient.getApiService().deleteOrderInfo(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OrderInfoResponse> call, @NonNull Response<OrderInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Invalidate student specific caches
                    cacheManager.remove(studentKey("order_info"));
                    cacheManager.remove(studentKey("order_details"));
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Xóa thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderInfoResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void changePassword(vn.edu.mku.portal.data.network.model.ChangePasswordApiRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> callback) {
        ApiClient.getApiService().changePassword(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> call, @NonNull Response<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.errorBody() != null) {
                    try {
                        String errorStr = response.errorBody().string();
                        if (errorStr.contains("Message")) {
                            com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(errorStr).getAsJsonObject();
                            if (obj.has("Message")) {
                                callback.onError(obj.get("Message").getAsString());
                                return;
                            }
                        }
                    } catch (Exception ignored) {}
                    callback.onError("Đổi mật khẩu thất bại (" + response.code() + ")");
                } else {
                    callback.onError("Đổi mật khẩu thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchComments(String year, String term, ApiCallback<List<CommentItem>> callback) {
        String key = studentKey("comments_" + year + "_" + term);
        executeCached(
                key,
                TypeToken.getParameterized(List.class, CommentItem.class).getType(),
                AppCacheManager.TTL_MINUTES_15,
                ApiClient.getApiService().getComments(year, term),
                callback,
                "Không thể lấy dữ liệu ý kiến thảo luận",
                null
        );
    }

    @Override
    public void fetchDepartments(ApiCallback<List<DepartmentItem>> callback) {
        String key = "departments";
        executeCached(
                key,
                TypeToken.getParameterized(List.class, DepartmentItem.class).getType(),
                AppCacheManager.TTL_DAY,
                ApiClient.getApiService().getDepartments(),
                callback,
                "Không thể lấy danh sách phòng ban",
                null
        );
    }

    @Override
    public void submitContact(ContactSubmitRequest request, ApiCallback<String> callback) {
        ApiClient.getApiService().submitContact(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Gửi thông tin liên hệ - góp ý thành công!");
                } else {
                    callback.onError("Gửi thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }
}