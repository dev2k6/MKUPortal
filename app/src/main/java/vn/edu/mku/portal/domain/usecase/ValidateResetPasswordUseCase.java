/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.domain.usecase;

import android.text.TextUtils;
import android.util.Patterns;

import vn.edu.mku.portal.R;

public class ValidateResetPasswordUseCase {

    public static class Result {
        private final Integer usernameErrorResId;
        private final Integer emailErrorResId;
        private final boolean isValid;

        public Result(Integer usernameErrorResId, Integer emailErrorResId) {
            this.usernameErrorResId = usernameErrorResId;
            this.emailErrorResId = emailErrorResId;
            this.isValid = usernameErrorResId == null && emailErrorResId == null;
        }

        public Integer getUsernameErrorResId() {
            return usernameErrorResId;
        }

        public Integer getEmailErrorResId() {
            return emailErrorResId;
        }

        public boolean isValid() {
            return isValid;
        }
    }

    public Result execute(String username, String email) {
        Integer usernameError = null;
        Integer emailError = null;

        if (TextUtils.isEmpty(username)) {
            usernameError = R.string.err_username_required;
        }

        if (TextUtils.isEmpty(email)) {
            emailError = R.string.err_email_required;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = R.string.err_email_invalid;
        }

        return new Result(usernameError, emailError);
    }
}