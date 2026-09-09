package vn.edu.mku.portal.data.repository;

import java.util.List;

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

import vn.edu.mku.portal.data.network.model.OrderDetailItem;

public interface StudentRepository {

    interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    void fetchStudentInfo(ApiCallback<StudentInfoResponse> callback);

    void fetchMessages(ApiCallback<List<StudentMessage>> callback);

    void fetchMenu(String langCode, ApiCallback<List<MenuItem>> callback);

    void fetchStudyProgramHeaders(ApiCallback<List<StudyProgramHeader>> callback);

    void fetchStudyProgramDetail(String studyProgramId, ApiCallback<StudyProgramDetailResponse> callback);

    void fetchYearAndTerm(ApiCallback<YearAndTermResponse> callback);

    void fetchWeekSchedule(String year, String term, ApiCallback<List<WeekItem>> callback);

    void fetchDrawingSchedules(String year, String term, int week, ApiCallback<DrawingScheduleResponse> callback);

    void fetchPeriodSchedules(String year, String term, ApiCallback<PeriodScheduleResponse> callback);

    void fetchExams(String year, String term, ApiCallback<List<ExamItem>> callback);

    void fetchDecisions(ApiCallback<List<DecisionItem>> callback);

    void fetchBehaviorScores(ApiCallback<List<BehaviorScoreItem>> callback);

    void fetchMarks(String programId, String type, ApiCallback<List<MarkYearGroup>> callback);

    void fetchMarkDetail(String scheduleStudyUnitId, ApiCallback<List<MarkDetailItem>> callback);

    void fetchFooterInfo(ApiCallback<List<FooterInfoItem>> callback);

    void fetchAccountFees(ApiCallback<List<AccountFeeItem>> callback);

    void fetchOrderDetails(ApiCallback<List<OrderDetailItem>> callback);

    void fetchOrderInfo(ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoRequest> callback);

    void submitOrderInfo(vn.edu.mku.portal.data.network.model.OrderInfoRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoResponse> callback);

    void deleteOrderInfo(vn.edu.mku.portal.data.network.model.OrderInfoRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.OrderInfoResponse> callback);

    void changePassword(vn.edu.mku.portal.data.network.model.ChangePasswordApiRequest request, ApiCallback<vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse> callback);

    void fetchComments(String year, String term, ApiCallback<List<vn.edu.mku.portal.data.network.model.CommentItem>> callback);

    void fetchDepartments(ApiCallback<List<vn.edu.mku.portal.data.network.model.DepartmentItem>> callback);

    void submitContact(vn.edu.mku.portal.data.network.model.ContactSubmitRequest request, ApiCallback<String> callback);
}