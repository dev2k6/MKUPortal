package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordApiResponse {

    @SerializedName("Message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
