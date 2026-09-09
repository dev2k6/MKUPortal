package vn.edu.mku.portal.data.model;

public class LoginRequest {
    private final String username;
    private final String password;
    private final String captchaInput;
    private final String captchaExpected;

    public LoginRequest(String username, String password, String captchaInput, String captchaExpected) {
        this.username = username;
        this.password = password;
        this.captchaInput = captchaInput;
        this.captchaExpected = captchaExpected;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCaptchaInput() {
        return captchaInput;
    }

    public String getCaptchaExpected() {
        return captchaExpected;
    }
}