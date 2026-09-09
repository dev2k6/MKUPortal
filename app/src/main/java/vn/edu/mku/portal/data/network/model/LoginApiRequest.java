/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class LoginApiRequest {

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("MaXacThuc")
    private String maXacThuc;

    @SerializedName("Token")
    private String token;

    public LoginApiRequest(String username, String password, String maXacThuc, String token) {
        this.username = username;
        this.password = password;
        this.maXacThuc = maXacThuc;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMaXacThuc() {
        return maXacThuc;
    }

    public String getToken() {
        return token;
    }
}