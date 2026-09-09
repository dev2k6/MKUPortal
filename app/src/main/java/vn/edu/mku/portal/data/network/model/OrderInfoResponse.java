package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class OrderInfoResponse {

    @SerializedName("Status")
    private int status;

    @SerializedName("Message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
