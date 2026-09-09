package vn.edu.mku.portal.data.crash;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.edu.mku.portal.BuildConfig;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.ui.common.NetworkMonitor;

public class CrashReporter implements Thread.UncaughtExceptionHandler {

    private static CrashReporter instance;

    private final Context appContext;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final ExecutorService backgroundExecutor;
    private final SharedPreferences prefs;

    private volatile String currentActivityName = "Unknown";

    private CrashReporter(Application application) {
        this.appContext = application.getApplicationContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.gson = new Gson();
        this.backgroundExecutor = Executors.newSingleThreadExecutor();
        this.prefs = appContext.getSharedPreferences(CrashConstants.PREF_NAME, Context.MODE_PRIVATE);

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(CrashConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(CrashConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        // 1. Monitor active activity name for crash context
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivityName = activity.getClass().getSimpleName();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });

        // 2. Set as default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(this);

        // 3. Flush any previously queued crashes (offline fallback)
        flushPendingCrashesAsync();
    }

    public static synchronized void init(Application application) {
        if (instance == null) {
            instance = new CrashReporter(application);
        }
    }

    public static synchronized CrashReporter getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CrashReporter is not initialized. Call CrashReporter.init(application) first.");
        }
        return instance;
    }

    /**
     * Configure a custom gateway URL (e.g. deployed Cloudflare worker domain)
     */
    public void setCustomGatewayUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            prefs.edit().putString(CrashConstants.KEY_CUSTOM_ENDPOINT, url.trim()).apply();
        } else {
            prefs.edit().remove(CrashConstants.KEY_CUSTOM_ENDPOINT).apply();
        }
    }

    public String getGatewayUrl() {
        return prefs.getString(CrashConstants.KEY_CUSTOM_ENDPOINT, CrashConstants.DEFAULT_GATEWAY_URL);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        try {
            // 1. Build comprehensive crash payload
            CrashReportPayload payload = buildCrashPayload(thread, throwable, true, null);

            // 2. Enqueue locally to guarantee persistence (in case device is offline)
            enqueueCrashLocally(payload);

            // 3. Attempt synchronous dispatch before process termination
            Thread dispatchThread = new Thread(() -> {
                boolean sent = sendPayloadToGateway(payload);
                if (sent) {
                    dequeueCrashLocally(payload.getTimestamp());
                }
            });
            dispatchThread.start();
            dispatchThread.join(CrashConstants.FATAL_DISPATCH_TIMEOUT_SECONDS * 1000L);
        } catch (Exception ignored) {
        } finally {
            // 4. Delegate to Android default uncaught exception handler
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        }
    }

    /**
     * Report non-fatal captured exception asynchronously
     */
    public void logHandledException(Throwable throwable, String customTag) {
        if (throwable == null) return;
        backgroundExecutor.execute(() -> {
            try {
                CrashReportPayload payload = buildCrashPayload(Thread.currentThread(), throwable, false, customTag);
                boolean sent = sendPayloadToGateway(payload);
                if (!sent) {
                    enqueueCrashLocally(payload);
                }
            } catch (Exception ignored) {}
        });
    }

    private CrashReportPayload buildCrashPayload(Thread thread, Throwable throwable, boolean isFatal, String customTag) {
        CrashReportPayload p = new CrashReportPayload();
        p.setAppName("MKUPortal");
        p.setVersionName(BuildConfig.VERSION_NAME);
        p.setVersionCode(BuildConfig.VERSION_CODE);
        p.setBuildType(BuildConfig.BUILD_TYPE);

        String sId = SessionManager.getInstance().getStudentId();
        p.setStudentId(!TextUtils.isEmpty(sId) ? sId : "Chưa đăng nhập");

        p.setDeviceManufacturer(Build.MANUFACTURER);
        p.setDeviceModel(Build.MODEL);
        p.setAndroidVersion(Build.VERSION.RELEASE);
        p.setSdkInt(Build.VERSION.SDK_INT);

        p.setTimestamp(System.currentTimeMillis());
        p.setActiveScreen(currentActivityName);
        p.setThreadName(thread != null ? thread.getName() : "unknown");

        p.setExceptionClass(throwable.getClass().getName());
        String msg = throwable.getMessage();
        if (!TextUtils.isEmpty(customTag)) {
            msg = "[" + customTag + "] " + (msg != null ? msg : "");
        }
        p.setExceptionMessage(!TextUtils.isEmpty(msg) ? msg : "Không có thông điệp lỗi");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        p.setStackTrace(sw.toString());

        // Memory info
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.getMemoryInfo(mi);
                p.setAvailableRamMb(mi.availMem / (1024 * 1024));
                p.setTotalRamMb(mi.totalMem / (1024 * 1024));
            }
        } catch (Exception ignored) {}

        // Network state
        boolean isOnline = NetworkMonitor.getInstance().isOnline();
        p.setNetworkType(isOnline ? "Có kết nối mạng" : "Ngoại tuyến (Offline)");
        p.setFatal(isFatal);

        return p;
    }

    /**
     * Send HTTP POST to Cloudflare Worker Gateway
     */
    private boolean sendPayloadToGateway(CrashReportPayload payload) {
        try {
            String json = gson.toJson(payload);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(getGatewayUrl())
                    .post(body)
                    .header("User-Agent", "MKUPortal-CrashReporter/1.0 (" + Build.MODEL + ")")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            return false;
        }
    }

    // --- Offline Persistent Queue Management ---

    private synchronized void enqueueCrashLocally(CrashReportPayload payload) {
        try {
            List<CrashReportPayload> queue = getLocalQueue();
            queue.add(payload);
            // Limit queue size to 20 to avoid bloat
            if (queue.size() > 20) {
                queue = queue.subList(queue.size() - 20, queue.size());
            }
            prefs.edit().putString(CrashConstants.KEY_PENDING_QUEUE, gson.toJson(queue)).commit();
        } catch (Exception ignored) {}
    }

    private synchronized void dequeueCrashLocally(long timestamp) {
        try {
            List<CrashReportPayload> queue = getLocalQueue();
            queue.removeIf(item -> item.getTimestamp() == timestamp);
            prefs.edit().putString(CrashConstants.KEY_PENDING_QUEUE, gson.toJson(queue)).apply();
        } catch (Exception ignored) {}
    }

    private synchronized List<CrashReportPayload> getLocalQueue() {
        String json = prefs.getString(CrashConstants.KEY_PENDING_QUEUE, null);
        if (!TextUtils.isEmpty(json)) {
            try {
                Type type = new TypeToken<List<CrashReportPayload>>() {}.getType();
                List<CrashReportPayload> list = gson.fromJson(json, type);
                if (list != null) return new ArrayList<>(list);
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }

    private void flushPendingCrashesAsync() {
        backgroundExecutor.execute(() -> {
            try {
                if (!NetworkMonitor.getInstance().isOnline()) return;

                List<CrashReportPayload> queue = getLocalQueue();
                if (queue.isEmpty()) return;

                List<CrashReportPayload> remaining = new ArrayList<>();
                for (CrashReportPayload report : queue) {
                    boolean sent = sendPayloadToGateway(report);
                    if (!sent) {
                        remaining.add(report);
                    }
                }
                prefs.edit().putString(CrashConstants.KEY_PENDING_QUEUE, gson.toJson(remaining)).apply();
            } catch (Exception ignored) {}
        });
    }
}
