/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.forgotpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.edu.mku.portal.data.repository.AuthRepository;
import vn.edu.mku.portal.data.repository.AuthRepositoryImpl;
import vn.edu.mku.portal.domain.usecase.ValidateResetPasswordUseCase;

public class ForgotPasswordViewModel extends ViewModel {

    private final ValidateResetPasswordUseCase validateUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<ForgotPasswordState> state = new MutableLiveData<>(ForgotPasswordState.idle());

    public ForgotPasswordViewModel() {
        this.validateUseCase = new ValidateResetPasswordUseCase();
        this.authRepository = new AuthRepositoryImpl();
    }

    public LiveData<ForgotPasswordState> getState() {
        return state;
    }

    public void resetPassword(String username, String email) {
        ValidateResetPasswordUseCase.Result validation = validateUseCase.execute(username, email);

        if (!validation.isValid()) {
            state.setValue(ForgotPasswordState.validationFailed(validation));
            return;
        }

        state.setValue(ForgotPasswordState.loading());

        authRepository.resetPassword(username, email, new AuthRepository.ApiCallback<String>() {
            @Override
            public void onSuccess(String resultMsg) {
                state.setValue(ForgotPasswordState.success(resultMsg));
            }

            @Override
            public void onError(String errorMessage) {
                state.setValue(ForgotPasswordState.error(errorMessage));
            }
        });
    }
}