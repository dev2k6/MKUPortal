package vn.edu.mku.portal.data.repository;

import android.text.TextUtils;
import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.mku.portal.data.network.ApiClient;
import vn.edu.mku.portal.data.network.model.AccountFeeItem;
import vn.edu.mku.portal.data.network.model.BehaviorScoreItem;
import vn.edu.mku.portal.data.network.model.DecisionItem;
import vn.edu.mku.portal.data.network.model.DrawingScheduleResponse;
import vn.edu.mku.portal.data.network.model.ExamItem;
import vn.edu.mku.portal.data.network.model.FooterInfoItem;
import vn.edu.mku.portal.data.network.model.MarkDetailItem;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.PeriodScheduleResponse;
import vn.edu.mku.portal.data.network.model.StudentInfoResponse;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.network.model.StudyProgramDetailResponse;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.data.network.model.WeekItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;

public class StudentRepositoryImpl implements StudentRepository {

    @Override
    public void fetchStudentInfo(ApiCallback<StudentInfoResponse> callback) {
        ApiClient.getApiService().getStudentInfo().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<StudentInfoResponse> call, @NonNull Response<StudentInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentInfoResponse body = response.body();
                    if (body.getFirstObj1() != null && !TextUtils.isEmpty(body.getFirstObj1().getStudentName())) {
                        vn.edu.mku.portal.data.local.SessionManager.getInstance().updateStudentName(body.getFirstObj1().getStudentName());
                    }
                    callback.onSuccess(body);
                } else {
                    callback.onError("Không thể lấy thông tin sinh viên (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentInfoResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchMessages(ApiCallback<List<StudentMessage>> callback) {
        ApiClient.getApiService().getMessages().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<StudentMessage>> call, @NonNull Response<List<StudentMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy danh sách thông báo (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudentMessage>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchMenu(String langCode, ApiCallback<List<MenuItem>> callback) {
        ApiClient.getApiService().getMenu(langCode).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuItem>> call, @NonNull Response<List<MenuItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy menu (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MenuItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchStudyProgramHeaders(ApiCallback<List<StudyProgramHeader>> callback) {
        ApiClient.getApiService().getStudyProgramHeaders().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<StudyProgramHeader>> call, @NonNull Response<List<StudyProgramHeader>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy danh sách chương trình đào tạo (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudyProgramHeader>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchStudyProgramDetail(String studyProgramId, ApiCallback<StudyProgramDetailResponse> callback) {
        ApiClient.getApiService().getStudyProgramDetail(studyProgramId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<StudyProgramDetailResponse> call, @NonNull Response<StudyProgramDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy chi tiết chương trình đào tạo (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<StudyProgramDetailResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchYearAndTerm(ApiCallback<YearAndTermResponse> callback) {
        ApiClient.getApiService().getYearAndTerm().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<YearAndTermResponse> call, @NonNull Response<YearAndTermResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thông tin năm học / học kỳ (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<YearAndTermResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchWeekSchedule(String year, String term, ApiCallback<List<WeekItem>> callback) {
        ApiClient.getApiService().getWeekSchedule(year, term).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<WeekItem>> call, @NonNull Response<List<WeekItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy danh sách tuần học (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WeekItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchDrawingSchedules(String year, String term, int week, ApiCallback<DrawingScheduleResponse> callback) {
        ApiClient.getApiService().getDrawingSchedules(year, term, week).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DrawingScheduleResponse> call, @NonNull Response<DrawingScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thời khóa biểu tuần (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrawingScheduleResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchPeriodSchedules(String year, String term, ApiCallback<PeriodScheduleResponse> callback) {
        ApiClient.getApiService().getPeriodSchedules(year, term).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PeriodScheduleResponse> call, @NonNull Response<PeriodScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thời khóa biểu thứ - tiết (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PeriodScheduleResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchExams(String year, String term, ApiCallback<List<ExamItem>> callback) {
        ApiClient.getApiService().getExams(year, term).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ExamItem>> call, @NonNull Response<List<ExamItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy danh sách lịch thi (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ExamItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchDecisions(ApiCallback<List<DecisionItem>> callback) {
        ApiClient.getApiService().getDecisions().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<DecisionItem>> call, @NonNull Response<List<DecisionItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy quyết định sinh viên (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DecisionItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchBehaviorScores(ApiCallback<List<BehaviorScoreItem>> callback) {
        ApiClient.getApiService().getBehaviorScores().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<BehaviorScoreItem>> call, @NonNull Response<List<BehaviorScoreItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy điểm rèn luyện (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BehaviorScoreItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchMarks(String programId, String type, ApiCallback<List<MarkYearGroup>> callback) {
        ApiClient.getApiService().getMarks(programId, type).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MarkYearGroup>> call, @NonNull Response<List<MarkYearGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy điểm học tập (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MarkYearGroup>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchMarkDetail(String scheduleStudyUnitId, ApiCallback<List<MarkDetailItem>> callback) {
        ApiClient.getApiService().getMarkDetail(scheduleStudyUnitId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MarkDetailItem>> call, @NonNull Response<List<MarkDetailItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy chi tiết điểm (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MarkDetailItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchFooterInfo(ApiCallback<List<FooterInfoItem>> callback) {
        ApiClient.getApiService().getFooterInfo().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FooterInfoItem>> call, @NonNull Response<List<FooterInfoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thông tin chân trang (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FooterInfoItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchAccountFees(ApiCallback<List<AccountFeeItem>> callback) {
        ApiClient.getApiService().getAccountFees().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<AccountFeeItem>> call, @NonNull Response<List<AccountFeeItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy tài chính sinh viên (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AccountFeeItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchOrderDetails(ApiCallback<List<vn.edu.mku.portal.data.network.model.OrderDetailItem>> callback) {
        ApiClient.getApiService().getOrderDetails().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<vn.edu.mku.portal.data.network.model.OrderDetailItem>> call, @NonNull Response<List<vn.edu.mku.portal.data.network.model.OrderDetailItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy chi tiết hóa đơn (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<vn.edu.mku.portal.data.network.model.OrderDetailItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchOrderInfo(ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoRequest> callback) {
        ApiClient.getApiService().getOrderInfo().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoRequest> call, @NonNull Response<vn.edu.mku.portal.data.network.model.OrderInfoRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy thông tin khai báo (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoRequest> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void submitOrderInfo(vn.edu.mku.portal.data.network.model.OrderInfoRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoResponse> callback) {
        ApiClient.getApiService().submitOrderInfo(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> call, @NonNull Response<vn.edu.mku.portal.data.network.model.OrderInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Cập nhật thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void deleteOrderInfo(vn.edu.mku.portal.data.network.model.OrderInfoRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoResponse> callback) {
        ApiClient.getApiService().deleteOrderInfo(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> call, @NonNull Response<vn.edu.mku.portal.data.network.model.OrderInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Xóa thất bại (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> call, @NonNull Throwable t) {
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
    public void fetchComments(String year, String term, ApiCallback<List<vn.edu.mku.portal.data.network.model.CommentItem>> callback) {
        ApiClient.getApiService().getComments(year, term).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<vn.edu.mku.portal.data.network.model.CommentItem>> call, @NonNull Response<List<vn.edu.mku.portal.data.network.model.CommentItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy dữ liệu ý kiến thảo luận (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<vn.edu.mku.portal.data.network.model.CommentItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void fetchDepartments(ApiCallback<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> callback) {
        ApiClient.getApiService().getDepartments().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> call, @NonNull Response<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể lấy danh sách phòng ban (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void submitContact(vn.edu.mku.portal.data.network.model.ContactSubmitRequest request, ApiCallback<String> callback) {
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