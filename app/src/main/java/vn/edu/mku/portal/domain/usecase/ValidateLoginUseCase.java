/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.domain.usecase;

import android.text.TextUtils;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.domain.model.ValidationResult;

public class ValidateLoginUseCase {

    public ValidationResult execute(String username, String password, String captchaInput) {
        Integer usernameError = null;
        Integer passwordError = null;
        Integer captchaError = null;

        if (TextUtils.isEmpty(username)) {
            usernameError = R.string.err_username_required;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError = R.string.err_password_required;
        }

        if (TextUtils.isEmpty(captchaInput)) {
            captchaError = R.string.err_captcha_required;
        }

        return new ValidationResult(usernameError, passwordError, captchaError);
    }
}