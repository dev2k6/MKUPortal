package vn.edu.mku.portal.data.crash;

public class CrashConstants {

    /**
     * Default Cloudflare Worker Gateway URL
     * Deploy the worker in /cloudflare-worker/ to get your active domain.
     */
    public static final String DEFAULT_GATEWAY_URL = "https://mku-crash-reporter.workers.dev/api/crash-report";

    public static final String PREF_NAME = "mku_crash_reporter_pref";
    public static final String KEY_CUSTOM_ENDPOINT = "custom_gateway_url";
    public static final String KEY_PENDING_QUEUE = "pending_crash_queue";

    public static final int CONNECT_TIMEOUT_SECONDS = 5;
    public static final int READ_TIMEOUT_SECONDS = 5;
    public static final int FATAL_DISPATCH_TIMEOUT_SECONDS = 3;
}
