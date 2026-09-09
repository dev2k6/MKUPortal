package vn.edu.mku.portal.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.mku.portal.data.model.LoginRequest;
import vn.edu.mku.portal.data.model.LoginResult;
import vn.edu.mku.portal.data.network.ApiClient;
import vn.edu.mku.portal.data.network.model.CaptchaResponse;
import vn.edu.mku.portal.data.network.model.LoginApiRequest;
import vn.edu.mku.portal.data.network.model.LoginApiResponse;
import vn.edu.mku.portal.data.network.model.ResetPasswordApiRequest;
import vn.edu.mku.portal.data.network.model.ResetPasswordApiResponse;

public class AuthRepositoryImpl implements AuthRepository {

    private final Gson gson = new Gson();

    @Override
    public void fetchCaptcha(ApiCallback<CaptchaResponse> callback) {
        ApiClient.getApiService().getCaptcha().enqueue(new Callback<CaptchaResponse>() {
            @Override
            public void onResponse(@NonNull Call<CaptchaResponse> call, @NonNull Response<CaptchaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không lấy được captcha từ hệ thống (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CaptchaResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void login(LoginRequest request, ApiCallback<LoginResult> callback) {
        LoginApiRequest apiRequest = new LoginApiRequest(
                request.getUsername(),
                request.getPassword(),
                request.getCaptchaInput(),
                request.getCaptchaExpected()
        );

        ApiClient.getApiService().login(apiRequest).enqueue(new Callback<LoginApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginApiResponse> call, @NonNull Response<LoginApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginApiResponse body = response.body();
                    String token = body.getToken();
                    if (!TextUtils.isEmpty(token)) {
                        String studentId = !TextUtils.isEmpty(body.getId()) ? body.getId() : request.getUsername();
                        String fullName = !TextUtils.isEmpty(body.getFullName()) ? body.getFullName() : "";
                        String msg = !TextUtils.isEmpty(body.getMessage()) ? body.getMessage() : "Đăng nhập thành công!";

                        // Save session details securely
                        vn.edu.mku.portal.data.local.SessionManager.getInstance().saveSession(token, studentId, fullName);

                        callback.onSuccess(new LoginResult(true, msg, token));
                    } else {
                        String errorMsg = body.getMessage() != null ? body.getMessage() : "Đăng nhập thất bại!";
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Đăng nhập thất bại (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            LoginApiResponse errorResponse = gson.fromJson(errorJson, LoginApiResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage();
                            }
                        } catch (Exception ignored) {}
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginApiResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void resetPassword(String username, String email, ApiCallback<String> callback) {
        ResetPasswordApiRequest request = new ResetPasswordApiRequest(username, email);

        ApiClient.getApiService().resetPassword(request).enqueue(new Callback<ResetPasswordApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ResetPasswordApiResponse> call, @NonNull Response<ResetPasswordApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();
                    if (msg == null) msg = "Yêu cầu khôi phục mật khẩu đã được xử lý!";
                    callback.onSuccess(msg);
                } else {
                    String errorMsg = "Gửi yêu cầu thất bại (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            ResetPasswordApiResponse errorResp = gson.fromJson(errorJson, ResetPasswordApiResponse.class);
                            if (errorResp != null && errorResp.getMessage() != null) {
                                errorMsg = errorResp.getMessage();
                            }
                        } catch (Exception ignored) {}
                    }
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResetPasswordApiResponse> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối máy chủ: " + t.getLocalizedMessage());
            }
        });
    }
}