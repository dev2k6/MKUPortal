package vn.edu.mku.portal.ui.forgotpassword;

import vn.edu.mku.portal.domain.usecase.ValidateResetPasswordUseCase;
import vn.edu.mku.portal.ui.common.Event;

public class ForgotPasswordState {
    private final boolean isLoading;
    private final ValidateResetPasswordUseCase.Result validationResult;
    private final Event<String> successMessageEvent;
    private final Event<String> errorMessageEvent;

    public ForgotPasswordState(boolean isLoading,
                               ValidateResetPasswordUseCase.Result validationResult,
                               Event<String> successMessageEvent,
                               Event<String> errorMessageEvent) {
        this.isLoading = isLoading;
        this.validationResult = validationResult;
        this.successMessageEvent = successMessageEvent;
        this.errorMessageEvent = errorMessageEvent;
    }

    public static ForgotPasswordState idle() {
        return new ForgotPasswordState(false, null, null, null);
    }

    public static ForgotPasswordState loading() {
        return new ForgotPasswordState(true, null, null, null);
    }

    public static ForgotPasswordState validationFailed(ValidateResetPasswordUseCase.Result result) {
        return new ForgotPasswordState(false, result, null, null);
    }

    public static ForgotPasswordState success(String message) {
        return new ForgotPasswordState(false, null, new Event<>(message), null);
    }

    public static ForgotPasswordState error(String message) {
        return new ForgotPasswordState(false, null, null, new Event<>(message));
    }

    public boolean isLoading() {
        return isLoading;
    }

    public ValidateResetPasswordUseCase.Result getValidationResult() {
        return validationResult;
    }

    public Event<String> getSuccessMessageEvent() {
        return successMessageEvent;
    }

    public Event<String> getErrorMessageEvent() {
        return errorMessageEvent;
    }
}