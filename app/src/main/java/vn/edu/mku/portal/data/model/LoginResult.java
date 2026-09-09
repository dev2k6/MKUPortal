package vn.edu.mku.portal.data.model;

public class LoginResult {
    private final boolean success;
    private final String message;
    private final String token;

    public LoginResult(boolean success, String message) {
        this(success, message, null);
    }

    public LoginResult(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}