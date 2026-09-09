package vn.edu.mku.portal.ui.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.AppCacheManager;
import vn.edu.mku.portal.data.network.model.MarkCourseItem;
import vn.edu.mku.portal.data.network.model.MarkSemesterGroup;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.ui.student.MarksActivity;

public class GradeNotificationManager {

    public static final String CHANNEL_ID = "mku_grades_channel";
    private static final int NOTIFICATION_ID = 2001;
    private static final String SNAPSHOT_KEY_PREFIX = "grade_snapshot_";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thông báo điểm học phần";
            String description = "Nhận thông báo tự động khi giảng viên công bố điểm mới";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 200, 300});

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static synchronized void processAndNotify(Context context, String studentId, List<MarkYearGroup> marksList) {
        if (context == null || TextUtils.isEmpty(studentId) || marksList == null || marksList.isEmpty()) {
            return;
        }

        createNotificationChannel(context);

        String cacheKey = SNAPSHOT_KEY_PREFIX + studentId;
        Type snapshotType = new TypeToken<HashMap<String, String>>() {}.getType();
        Map<String, String> oldSnapshot = AppCacheManager.getInstance().getOrStale(cacheKey, snapshotType);

        Map<String, String> currentSnapshot = new HashMap<>();
        List<MarkCourseItem> newOrUpdatedGrades = new ArrayList<>();

        for (MarkYearGroup yearGroup : marksList) {
            if (yearGroup == null || yearGroup.getDanhSachDiem() == null) continue;
            for (MarkSemesterGroup semesterGroup : yearGroup.getDanhSachDiem()) {
                if (semesterGroup == null || semesterGroup.getDanhSachDiemHK() == null) continue;
                for (MarkCourseItem course : semesterGroup.getDanhSachDiemHK()) {
                    if (course == null) continue;

                    String score10 = course.getDiemTK10();
                    // Check if score is published
                    if (!TextUtils.isEmpty(score10) && !"-".equals(score10.trim()) && !"null".equalsIgnoreCase(score10)) {
                        String courseKey = !TextUtils.isEmpty(course.getScheduleStudyUnitId())
                                ? course.getScheduleStudyUnitId()
                                : course.getCurriculumId();
                        if (TextUtils.isEmpty(courseKey)) {
                            courseKey = course.getCurriculumName();
                        }

                        String scoreLetter = course.getDiemTKChu() != null ? course.getDiemTKChu() : "";
                        String snapshotVal = score10.trim() + "|" + scoreLetter.trim();
                        currentSnapshot.put(courseKey, snapshotVal);

                        if (oldSnapshot != null) {
                            if (!oldSnapshot.containsKey(courseKey)) {
                                // Newly added course with grade
                                newOrUpdatedGrades.add(course);
                            } else {
                                String oldVal = oldSnapshot.get(courseKey);
                                if (!snapshotVal.equals(oldVal)) {
                                    // Grade was updated/modified
                                    newOrUpdatedGrades.add(course);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Save new snapshot to persistent cache (no expiry, stays until cleared)
        AppCacheManager.getInstance().put(cacheKey, currentSnapshot, -1);

        // If this is the initial run (oldSnapshot == null), don't trigger notification barrage
        if (oldSnapshot == null) {
            return;
        }

        // If new or updated grades detected, fire notification
        if (!newOrUpdatedGrades.isEmpty()) {
            sendGradeNotification(context, newOrUpdatedGrades);
        }
    }

    private static void sendGradeNotification(Context context, List<MarkCourseItem> changedCourses) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent intent = new Intent(context, MarksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        String title;
        String contentText;
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if (changedCourses.size() == 1) {
            MarkCourseItem item = changedCourses.get(0);
            title = "MKU Portal - Có điểm mới!";
            String gradeDisplay = item.getDiemTK10();
            if (!TextUtils.isEmpty(item.getDiemTKChu())) {
                gradeDisplay += " (" + item.getDiemTKChu() + ")";
            }
            contentText = item.getCurriculumName() + ": " + gradeDisplay;
            inboxStyle.addLine(contentText);
        } else {
            title = "MKU Portal - Có " + changedCourses.size() + " điểm mới!";
            contentText = "Giảng viên vừa công bố điểm các học phần mới. Nhấn để xem chi tiết.";
            for (MarkCourseItem item : changedCourses) {
                String gradeDisplay = item.getDiemTK10();
                if (!TextUtils.isEmpty(item.getDiemTKChu())) {
                    gradeDisplay += " (" + item.getDiemTKChu() + ")";
                }
                inboxStyle.addLine(item.getCurriculumName() + ": " + gradeDisplay);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_grade)
                .setContentTitle(title)
                .setContentText(contentText)
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {}
    }

    public static void schedulePeriodicGradeCheck(Context context) {
        if (context == null) return;
        try {
            android.app.job.JobScheduler scheduler =
                    (android.app.job.JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler == null) return;

            int jobId = 1001;
            // Check if job already scheduled
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (scheduler.getPendingJob(jobId) != null) {
                    return;
                }
            }

            android.content.ComponentName serviceComponent =
                    new android.content.ComponentName(context, vn.edu.mku.portal.service.GradeCheckJobService.class);

            android.app.job.JobInfo.Builder builder = new android.app.job.JobInfo.Builder(jobId, serviceComponent)
                    .setRequiredNetworkType(android.app.job.JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(60 * 60 * 1000L); // every 1 hour

            scheduler.schedule(builder.build());
        } catch (Exception ignored) {}
    }

    public static void cancelPeriodicGradeCheck(Context context) {
        if (context == null) return;
        try {
            android.app.job.JobScheduler scheduler =
                    (android.app.job.JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null) {
                scheduler.cancel(1001);
            }
        } catch (Exception ignored) {}
    }
}
