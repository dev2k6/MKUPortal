package vn.edu.mku.portal.domain.model;

public class ValidationResult {
    private final Integer usernameErrorResId;
    private final Integer passwordErrorResId;
    private final Integer captchaErrorResId;
    private final boolean isValid;

    public ValidationResult(Integer usernameErrorResId, Integer passwordErrorResId, Integer captchaErrorResId) {
        this.usernameErrorResId = usernameErrorResId;
        this.passwordErrorResId = passwordErrorResId;
        this.captchaErrorResId = captchaErrorResId;
        this.isValid = usernameErrorResId == null && passwordErrorResId == null && captchaErrorResId == null;
    }

    public Integer getUsernameErrorResId() {
        return usernameErrorResId;
    }

    public Integer getPasswordErrorResId() {
        return passwordErrorResId;
    }

    public Integer getCaptchaErrorResId() {
        return captchaErrorResId;
    }

    public boolean isValid() {
        return isValid;
    }
}