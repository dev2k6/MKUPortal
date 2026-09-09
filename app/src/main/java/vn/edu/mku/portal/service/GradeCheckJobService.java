package vn.edu.mku.portal.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.data.network.ApiClient;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.ui.common.GradeNotificationManager;

public class GradeCheckJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
            return false;
        }

        String studentId = session.getStudentId();

        // 1. Fetch study programs to get current program ID
        ApiClient.getApiService().getStudyProgramHeaders().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<StudyProgramHeader>> call, @NonNull Response<List<StudyProgramHeader>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String programId = response.body().get(0).getStudyProgramId();
                    fetchMarksAndCheck(params, studentId, programId);
                } else {
                    jobFinished(params, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StudyProgramHeader>> call, @NonNull Throwable t) {
                jobFinished(params, false);
            }
        });

        return true;
    }

    private void fetchMarksAndCheck(JobParameters params, String studentId, String programId) {
        ApiClient.getApiService().getMarks(programId, "SV").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<MarkYearGroup>> call, @NonNull Response<List<MarkYearGroup>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GradeNotificationManager.processAndNotify(getApplicationContext(), studentId, response.body());
                }
                jobFinished(params, false);
            }

            @Override
            public void onFailure(@NonNull Call<List<MarkYearGroup>> call, @NonNull Throwable t) {
                jobFinished(params, false);
            }
        });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true; // reschedule if cancelled
    }
}
