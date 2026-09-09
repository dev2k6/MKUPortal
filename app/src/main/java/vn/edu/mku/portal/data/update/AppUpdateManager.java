/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vn.edu.mku.portal.BuildConfig;
import vn.edu.mku.portal.R;

public class AppUpdateManager {

    private static final String TAG = "AppUpdateManager";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Handler mainHandler;
    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Gson gson = new Gson();

    private static synchronized Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    public interface UpdateCheckCallback {
        void onCheckFinished(boolean isUpdateAvailable, GitHubReleaseModel release, String errorMessage);
    }

    /**
     * Tự động kiểm tra bản cập nhật khi vào app (chạy ngầm, có throttle 4h)
     */
    public static void checkUpdateAuto(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        SharedPreferences pref = activity.getSharedPreferences(AppUpdateConfig.PREF_UPDATE_NAME, Context.MODE_PRIVATE);
        long lastCheck = pref.getLong(AppUpdateConfig.KEY_LAST_CHECK_TIME, 0);
        long now = System.currentTimeMillis();

        if (now - lastCheck < AppUpdateConfig.AUTO_CHECK_INTERVAL_MS) {
            Log.d(TAG, "Skipping auto update check (within cooldown interval).");
            return;
        }

        fetchLatestRelease((release, errorMessage) -> {
            if (activity.isFinishing()) return;

            if (release != null) {
                pref.edit().putLong(AppUpdateConfig.KEY_LAST_CHECK_TIME, System.currentTimeMillis()).apply();

                String currentVer = BuildConfig.VERSION_NAME;
                String remoteVer = release.getCleanVersion();
                String ignoredVer = pref.getString(AppUpdateConfig.KEY_IGNORED_VERSION, "");

                if (isNewerVersion(currentVer, remoteVer)) {
                    if (ignoredVer.equalsIgnoreCase(remoteVer)) {
                        Log.d(TAG, "Version " + remoteVer + " was ignored by user.");
                        return;
                    }
                    showUpdateDialog(activity, release);
                }
            } else {
                Log.w(TAG, "Auto update check failed: " + errorMessage);
            }
        });
    }

    /**
     * Kiểm tra bản cập nhật chủ động khi người dùng bấm nút trong cài đặt/menu
     */
    public static void checkUpdateManual(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        Toast.makeText(activity, activity.getString(R.string.msg_checking_update), Toast.LENGTH_SHORT).show();

        fetchLatestRelease((release, errorMessage) -> {
            if (activity.isFinishing()) return;

            if (release != null) {
                String currentVer = BuildConfig.VERSION_NAME;
                String remoteVer = release.getCleanVersion();

                if (isNewerVersion(currentVer, remoteVer)) {
                    showUpdateDialog(activity, release);
                } else {
                    Toast.makeText(activity, 
                        activity.getString(R.string.msg_app_is_latest, "v" + currentVer), 
                        Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(activity, 
                    activity.getString(R.string.msg_check_update_failed), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gọi API GitHub lấy thông tin bản Release mới nhất
     */
    public static void fetchLatestRelease(UpdateCallback callback) {
        executor.execute(() -> {
            String url = AppUpdateConfig.getLatestReleaseApiUrl();
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "MKUPortal-Android-App")
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    GitHubReleaseModel release = gson.fromJson(json, GitHubReleaseModel.class);
                    getMainHandler().post(() -> callback.onResult(release, null));
                } else {
                    String err = "HTTP " + response.code() + ": " + response.message();
                    getMainHandler().post(() -> callback.onResult(null, err));
                }
            } catch (IOException e) {
                getMainHandler().post(() -> callback.onResult(null, e.getMessage()));
            }
        });
    }

    public interface UpdateCallback {
        void onResult(GitHubReleaseModel release, String errorMessage);
    }

    /**
     * Hiển thị hộp thoại thông báo cập nhật giao diện chuyên nghiệp Material 3
     */
    public static void showUpdateDialog(Activity activity, GitHubReleaseModel release) {
        if (activity == null || activity.isFinishing()) return;

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_app_update, null);

        TextView tvReleaseName = dialogView.findViewById(R.id.tvReleaseName);
        TextView tvCurrentVersionValue = dialogView.findViewById(R.id.tvCurrentVersionValue);
        TextView tvNewVersionValue = dialogView.findViewById(R.id.tvNewVersionValue);
        TextView tvUpdateMeta = dialogView.findViewById(R.id.tvUpdateMeta);
        TextView tvChangelogBody = dialogView.findViewById(R.id.tvChangelogBody);

        MaterialButton btnUpdateNow = dialogView.findViewById(R.id.btnUpdateNow);
        MaterialButton btnUpdateLater = dialogView.findViewById(R.id.btnUpdateLater);
        MaterialButton btnIgnoreVersion = dialogView.findViewById(R.id.btnIgnoreVersion);

        // Đổ dữ liệu
        tvReleaseName.setText(release.getName());
        tvCurrentVersionValue.setText("v" + BuildConfig.VERSION_NAME);
        tvNewVersionValue.setText("v" + release.getCleanVersion());

        // Định dạng ngày phát hành & dung lượng
        StringBuilder metaBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(release.getPublishedAt())) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = isoFormat.parse(release.getPublishedAt());
                if (date != null) {
                    metaBuilder.append(activity.getString(R.string.update_meta_published, displayFormat.format(date)));
                }
            } catch (Exception ignored) {
                metaBuilder.append(activity.getString(R.string.update_meta_recent));
            }
        }
        String apkSize = release.getApkSizeFormatted();
        if (!TextUtils.isEmpty(apkSize)) {
            if (metaBuilder.length() > 0) metaBuilder.append(" • ");
            metaBuilder.append(activity.getString(R.string.update_meta_size, apkSize));
        }
        tvUpdateMeta.setText(metaBuilder.toString());

        // Changelog text
        String body = release.getBody();
        if (!TextUtils.isEmpty(body)) {
            tvChangelogBody.setText(body.trim());
        } else {
            tvChangelogBody.setText(activity.getString(R.string.update_changelog_default));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Cập nhật ngay: Mở link tải APK hoặc link GitHub Release trên trình duyệt
        btnUpdateNow.setOnClickListener(v -> {
            String downloadUrl = release.getApkDownloadUrl();
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                activity.startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(activity, activity.getString(R.string.msg_cannot_open_update_link), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        // Để sau: Đóng hộp thoại
        btnUpdateLater.setOnClickListener(v -> dialog.dismiss());

        // Bỏ qua phiên bản này: Lưu vào SharedPreferences không nhắc lại version này
        btnIgnoreVersion.setOnClickListener(v -> {
            SharedPreferences pref = activity.getSharedPreferences(AppUpdateConfig.PREF_UPDATE_NAME, Context.MODE_PRIVATE);
            pref.edit().putString(AppUpdateConfig.KEY_IGNORED_VERSION, release.getCleanVersion()).apply();
            Toast.makeText(activity, activity.getString(R.string.msg_version_ignored, release.getCleanVersion()), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Thuật toán so khớp Semantic Versioning (ví dụ: 1.0.1 > 1.0)
     * Trả về true nếu remoteVersion lớn hơn currentVersion
     */
    public static boolean isNewerVersion(String currentVersion, String remoteVersion) {
        if (remoteVersion == null || remoteVersion.trim().isEmpty()) return false;
        if (currentVersion == null || currentVersion.trim().isEmpty()) return true;

        String cleanCurrent = cleanVersionString(currentVersion);
        String cleanRemote = cleanVersionString(remoteVersion);

        String[] currentParts = cleanCurrent.split("\\.");
        String[] remoteParts = cleanRemote.split("\\.");

        int length = Math.max(currentParts.length, remoteParts.length);
        for (int i = 0; i < length; i++) {
            int currentNum = 0;
            int remoteNum = 0;

            if (i < currentParts.length) {
                currentNum = parseLeadingInt(currentParts[i]);
            }
            if (i < remoteParts.length) {
                remoteNum = parseLeadingInt(remoteParts[i]);
            }

            if (remoteNum > currentNum) {
                return true;
            } else if (remoteNum < currentNum) {
                return false;
            }
        }

        return false;
    }

    private static String cleanVersionString(String ver) {
        ver = ver.trim();
        if (ver.startsWith("v") || ver.startsWith("V")) {
            ver = ver.substring(1).trim();
        }
        // Loại bỏ suffix sau dấu '-' hoặc '+' nếu có (vd: 1.0.1-beta -> 1.0.1)
        int dashIdx = ver.indexOf('-');
        if (dashIdx > 0) {
            ver = ver.substring(0, dashIdx);
        }
        int plusIdx = ver.indexOf('+');
        if (plusIdx > 0) {
            ver = ver.substring(0, plusIdx);
        }
        return ver;
    }

    private static int parseLeadingInt(String part) {
        if (part == null || part.trim().isEmpty()) return 0;
        StringBuilder sb = new StringBuilder();
        for (char c : part.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        try {
            return sb.length() > 0 ? Integer.parseInt(sb.toString()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
