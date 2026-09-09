/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.crash;

public class CrashConstants {

    /**
     * Deployed Cloudflare Worker Gateway URL
     */
    public static final String DEFAULT_GATEWAY_URL = "https://mku-crash-reporter.dev2k6.workers.dev/api/crash";

    public static final String PREF_NAME = "mku_crash_reporter_pref";
    public static final String KEY_CUSTOM_ENDPOINT = "custom_gateway_url";
    public static final String KEY_PENDING_QUEUE = "pending_crash_queue";

    public static final int CONNECT_TIMEOUT_SECONDS = 5;
    public static final int READ_TIMEOUT_SECONDS = 5;
    public static final int FATAL_DISPATCH_TIMEOUT_SECONDS = 3;
}
