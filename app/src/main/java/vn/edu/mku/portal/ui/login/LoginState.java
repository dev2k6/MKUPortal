package vn.edu.mku.portal.ui.login;

import vn.edu.mku.portal.domain.model.ValidationResult;
import vn.edu.mku.portal.ui.common.Event;

public class LoginState {
    private final boolean isLoading;
    private final boolean isCaptchaLoading;
    private final ValidationResult validationResult;
    private final String captchaImageBase64;
    private final String captchaToken;
    private final Event<String> successMessageEvent;
    private final Event<String> errorMessageEvent;

    public LoginState(boolean isLoading,
                      boolean isCaptchaLoading,
                      ValidationResult validationResult,
                      String captchaImageBase64,
                      String captchaToken,
                      Event<String> successMessageEvent,
                      Event<String> errorMessageEvent) {
        this.isLoading = isLoading;
        this.isCaptchaLoading = isCaptchaLoading;
        this.validationResult = validationResult;
        this.captchaImageBase64 = captchaImageBase64;
        this.captchaToken = captchaToken;
        this.successMessageEvent = successMessageEvent;
        this.errorMessageEvent = errorMessageEvent;
    }

    public static LoginState idle() {
        return new LoginState(false, false, null, null, null, null, null);
    }

    public static LoginState captchaLoading() {
        return new LoginState(false, true, null, null, null, null, null);
    }

    public static LoginState captchaLoaded(String imageBase64, String token) {
        return new LoginState(false, false, null, imageBase64, token, null, null);
    }

    public static LoginState validationFailed(ValidationResult result, String currentBase64, String currentToken) {
        return new LoginState(false, false, result, currentBase64, currentToken, null, null);
    }

    public static LoginState loading(String currentBase64, String currentToken) {
        return new LoginState(true, false, null, currentBase64, currentToken, null, null);
    }

    public static LoginState success(String message) {
        return new LoginState(false, false, null, null, null, new Event<>(message), null);
    }

    public static LoginState error(String message, String currentBase64, String currentToken) {
        return new LoginState(false, false, null, currentBase64, currentToken, null, new Event<>(message));
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isCaptchaLoading() {
        return isCaptchaLoading;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public String getCaptchaImageBase64() {
        return captchaImageBase64;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public Event<String> getSuccessMessageEvent() {
        return successMessageEvent;
    }

    public Event<String> getErrorMessageEvent() {
        return errorMessageEvent;
    }
}