/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.edu.mku.portal.data.model.LoginRequest;
import vn.edu.mku.portal.data.model.LoginResult;
import vn.edu.mku.portal.data.network.model.CaptchaResponse;
import vn.edu.mku.portal.data.repository.AuthRepository;
import vn.edu.mku.portal.data.repository.AuthRepositoryImpl;
import vn.edu.mku.portal.domain.model.ValidationResult;
import vn.edu.mku.portal.domain.usecase.ValidateLoginUseCase;

public class LoginViewModel extends ViewModel {

    private final ValidateLoginUseCase validateLoginUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(LoginState.idle());

    private String currentCaptchaToken = "";
    private String currentCaptchaBase64 = "";

    public LoginViewModel() {
        this.validateLoginUseCase = new ValidateLoginUseCase();
        this.authRepository = new AuthRepositoryImpl();
        loadCaptcha();
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void loadCaptcha() {
        loginState.setValue(LoginState.captchaLoading());
        authRepository.fetchCaptcha(new AuthRepository.ApiCallback<CaptchaResponse>() {
            @Override
            public void onSuccess(CaptchaResponse result) {
                currentCaptchaBase64 = result.getImg();
                currentCaptchaToken = result.getToken();
                loginState.setValue(LoginState.captchaLoaded(currentCaptchaBase64, currentCaptchaToken));
            }

            @Override
            public void onError(String errorMessage) {
                loginState.setValue(LoginState.error(errorMessage, currentCaptchaBase64, currentCaptchaToken));
            }
        });
    }

    public void login(String username, String password, String captchaInput) {
        ValidationResult validationResult = validateLoginUseCase.execute(username, password, captchaInput);

        if (!validationResult.isValid()) {
            loginState.setValue(LoginState.validationFailed(validationResult, currentCaptchaBase64, currentCaptchaToken));
            return;
        }

        loginState.setValue(LoginState.loading(currentCaptchaBase64, currentCaptchaToken));

        LoginRequest request = new LoginRequest(username, password, captchaInput, currentCaptchaToken);
        authRepository.login(request, new AuthRepository.ApiCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult result) {
                if (result.isSuccess()) {
                    loginState.setValue(LoginState.success(result.getMessage()));
                } else {
                    loginState.setValue(LoginState.error(result.getMessage(), currentCaptchaBase64, currentCaptchaToken));
                    loadCaptcha(); // Tải lại captcha mới sau khi thất bại
                }
            }

            @Override
            public void onError(String errorMessage) {
                loginState.setValue(LoginState.error(errorMessage, currentCaptchaBase64, currentCaptchaToken));
                loadCaptcha(); // Tải lại captcha mới khi lỗi
            }
        });
    }
}