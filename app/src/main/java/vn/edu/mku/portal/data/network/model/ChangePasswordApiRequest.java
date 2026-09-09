/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordApiRequest {

    @SerializedName("p1")
    private String p1;

    @SerializedName("p2")
    private String p2;

    public ChangePasswordApiRequest() {}

    public ChangePasswordApiRequest(String oldPassword, String newPassword) {
        this.p1 = oldPassword;
        this.p2 = newPassword;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }
}
