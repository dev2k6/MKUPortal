package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordApiResponse {

    @SerializedName(value = "Message", alternate = {"message"})
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}