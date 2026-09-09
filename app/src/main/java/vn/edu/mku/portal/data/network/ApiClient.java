package vn.edu.mku.portal.data.network;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import vn.edu.mku.portal.MKUApplication;
import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.ui.login.LoginActivity;

public class ApiClient {

    private static ApiService apiService;
    private static final AtomicBoolean isHandlingUnauthorized = new AtomicBoolean(false);

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            if (vn.edu.mku.portal.BuildConfig.DEBUG) {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request original = chain.request();
                            String sysUserAgent = System.getProperty("http.agent");
                            String userAgent = (sysUserAgent != null && !sysUserAgent.isEmpty())
                                    ? sysUserAgent
                                    : "Android/" + android.os.Build.VERSION.RELEASE + " " + android.os.Build.MODEL;

                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("accept", "application/json, text/plain, */*")
                                    .header("apikey", ApiConstants.API_KEY)
                                    .header("clientid", ApiConstants.CLIENT_ID)
                                    .header("origin", ApiConstants.ORIGIN)
                                    .header("referer", ApiConstants.REFERER)
                                    .header("user-agent", userAgent);

                            if (SessionManager.getInstance().isLoggedIn()) {
                                requestBuilder.header("authorization", "Bearer " + SessionManager.getInstance().getToken());
                            }

                            Response response = chain.proceed(requestBuilder.build());

                            if (response.code() == 401) {
                                handleUnauthorized();
                            }

                            return response;
                        }
                    })
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    private static void handleUnauthorized() {
        if (isHandlingUnauthorized.compareAndSet(false, true)) {
            Context context = MKUApplication.getAppContext();
            if (context != null) {
                vn.edu.mku.portal.ui.common.GradeNotificationManager.cancelPeriodicGradeCheck(context);
            }
            vn.edu.mku.portal.data.local.AppCacheManager.getInstance().clearMemory();
            SessionManager.getInstance().clearSession();

            if (context != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    String msg = vn.edu.mku.portal.data.local.LanguageManager.getInstance().getAppString(R.string.msg_session_expired);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);

                    isHandlingUnauthorized.set(false);
                });
            } else {
                isHandlingUnauthorized.set(false);
            }
        }
    }
}