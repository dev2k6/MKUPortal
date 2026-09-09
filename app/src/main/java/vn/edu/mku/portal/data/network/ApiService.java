package vn.edu.mku.portal.data.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.edu.mku.portal.data.network.model.CaptchaResponse;
import vn.edu.mku.portal.data.network.model.LoginApiRequest;
import vn.edu.mku.portal.data.network.model.LoginApiResponse;

import vn.edu.mku.portal.data.network.model.AccountFeeItem;
import vn.edu.mku.portal.data.network.model.BehaviorScoreItem;
import vn.edu.mku.portal.data.network.model.DecisionItem;
import vn.edu.mku.portal.data.network.model.DrawingScheduleResponse;
import vn.edu.mku.portal.data.network.model.MarkDetailItem;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.data.network.model.ExamItem;
import vn.edu.mku.portal.data.network.model.FooterInfoItem;
import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.ObligateResponse;
import vn.edu.mku.portal.data.network.model.PeriodScheduleResponse;
import vn.edu.mku.portal.data.network.model.ResetPasswordApiRequest;
import vn.edu.mku.portal.data.network.model.ResetPasswordApiResponse;
import vn.edu.mku.portal.data.network.model.ResourceLanguageItem;
import vn.edu.mku.portal.data.network.model.StudentInfoResponse;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.network.model.StudyProgramDetailResponse;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.data.network.model.WeekItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;

public interface ApiService {

    @GET("api/authenticate/GetCaptcha")
    Call<CaptchaResponse> getCaptcha();

    @POST("api/authenticate/authpsc")
    Call<LoginApiResponse> login(@Body LoginApiRequest request);

    @POST("api/authenticate/ResetPassword")
    Call<ResetPasswordApiResponse> resetPassword(@Body ResetPasswordApiRequest request);

    @GET("api/guest/GetResourceLanguage")
    Call<List<ResourceLanguageItem>> getResourceLanguage(@Query("param") String langParam);

    @GET("api/student/info")
    Call<StudentInfoResponse> getStudentInfo();

    @GET("api/student/GetMessagesByReceiverID")
    Call<List<StudentMessage>> getMessages();

    @GET("api/authenticate/getmenu")
    Call<List<MenuItem>> getMenu(@Query("language") String langParam);

    @GET("api/authenticate/getObligate")
    Call<ObligateResponse> getObligate();

    @GET("api/guest/footerinfor")
    Call<List<FooterInfoItem>> getFooterInfo();

    @GET("api/student/getstudyprogram")
    Call<List<StudyProgramHeader>> getStudyProgramHeaders();

    @GET("api/student/studyProgram")
    Call<StudyProgramDetailResponse> getStudyProgramDetail(@Query("StudyProgramID") String studyProgramId);

    @GET("api/student/yearandterm")
    Call<YearAndTermResponse> getYearAndTerm();

    @GET("api/student/WeekSchedule")
    Call<List<WeekItem>> getWeekSchedule(@Query("namhoc") String year, @Query("hocky") String term);

    @GET("api/student/DrawingSchedules")
    Call<DrawingScheduleResponse> getDrawingSchedules(@Query("namhoc") String year, @Query("hocky") String term, @Query("tuan") int week);

    @GET("api/student/DrawingStudentSchedule_Perior")
    Call<PeriodScheduleResponse> getPeriodSchedules(@Query("namhoc") String year, @Query("hocky") String term);

    @GET("api/student/exam")
    Call<List<ExamItem>> getExams(@Query("namhoc") String year, @Query("hocky") String term);

    @GET("api/student/decision")
    Call<List<DecisionItem>> getDecisions();

    @GET("api/student/behaviorscoretotal")
    Call<List<BehaviorScoreItem>> getBehaviorScores();

    @GET("api/student/marks")
    Call<List<MarkYearGroup>> getMarks(@Query("ctdt") String programId, @Query("loai") String type);

    @GET("api/student/showmarkdetail")
    Call<List<MarkDetailItem>> getMarkDetail(@Query("id") String scheduleStudyUnitId);

    @GET("api/student/AccountFeeHocPhan")
    Call<List<AccountFeeItem>> getAccountFees();

    @GET("api/student/orderdetail")
    Call<List<vn.edu.mku.portal.data.network.model.OrderDetailItem>> getOrderDetails();

    @POST("api/student/OrderInfo_Submit")
    Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> submitOrderInfo(@Body vn.edu.mku.portal.data.network.model.OrderInfoRequest request);

    @POST("api/student/OrderInfo_Del")
    Call<vn.edu.mku.portal.data.network.model.OrderInfoResponse> deleteOrderInfo(@Body vn.edu.mku.portal.data.network.model.OrderInfoRequest request);

    @GET("api/student/GetOrderInfo")
    Call<vn.edu.mku.portal.data.network.model.OrderInfoRequest> getOrderInfo();

    @POST("api/authenticate/ChangePassword")
    Call<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> changePassword(@Body vn.edu.mku.portal.data.network.model.ChangePasswordApiRequest request);

    @GET("api/student/Comment")
    Call<List<vn.edu.mku.portal.data.network.model.CommentItem>> getComments(@Query("namhoc") String year, @Query("hocky") String term);

    @GET("api/student/LienHe")
    Call<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> getDepartments();

    @POST("api/student/LienHe")
    Call<okhttp3.ResponseBody> submitContact(@Body vn.edu.mku.portal.data.network.model.ContactSubmitRequest request);
}