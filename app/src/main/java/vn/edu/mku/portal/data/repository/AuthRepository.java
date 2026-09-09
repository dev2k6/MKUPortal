package vn.edu.mku.portal.data.repository;

import vn.edu.mku.portal.data.model.LoginRequest;
import vn.edu.mku.portal.data.model.LoginResult;
import vn.edu.mku.portal.data.network.model.CaptchaResponse;

public interface AuthRepository {

    interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    void fetchCaptcha(ApiCallback<CaptchaResponse> callback);

    void login(LoginRequest request, ApiCallback<LoginResult> callback);

    void resetPassword(String username, String email, ApiCallback<String> callback);
}