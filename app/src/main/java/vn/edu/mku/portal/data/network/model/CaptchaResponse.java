package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class CaptchaResponse {
    @SerializedName("img")
    private String img;

    @SerializedName("token")
    private String token;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}