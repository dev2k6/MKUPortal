package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordApiRequest {

    @SerializedName("p1")
    private final String username;

    @SerializedName("p2")
    private final String email;

    public ResetPasswordApiRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}