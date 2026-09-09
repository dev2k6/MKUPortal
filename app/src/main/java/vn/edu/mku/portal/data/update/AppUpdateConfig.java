/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.update;

public class AppUpdateConfig {

    /**
     * GitHub Username (Owner) của Repository
     */
    public static final String GITHUB_OWNER = "dev2k6";

    /**
     * Tên GitHub Repository của dự án
     */
    public static final String GITHUB_REPO = "MKUPortal";

    /**
     * Endpoint GitHub REST API lấy thông tin bản phát hành mới nhất
     */
    public static String getLatestReleaseApiUrl() {
        return "https://api.github.com/repos/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";
    }

    /**
     * Đường dẫn trang Releases trên trình duyệt web
     */
    public static String getReleasesWebUrl() {
        return "https://github.com/" + GITHUB_OWNER + "/" + GITHUB_REPO + "/releases/latest";
    }

    /**
     * SharedPreferences keys
     */
    public static final String PREF_UPDATE_NAME = "mku_app_update_pref";
    public static final String KEY_LAST_CHECK_TIME = "key_last_check_time";
    public static final String KEY_IGNORED_VERSION = "key_ignored_version";

    /**
     * Khoảng thời gian tối thiểu giữa 2 lần tự động kiểm tra ngầm (4 giờ)
     * Tránh chạm ngưỡng giới hạn (Rate Limit) 60 requests/giờ của GitHub Public API
     */
    public static final long AUTO_CHECK_INTERVAL_MS = 4 * 60 * 60 * 1000L;
}
