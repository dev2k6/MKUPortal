package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class LoginApiResponse {

    @SerializedName(value = "Id", alternate = {"id"})
    private String id;

    @SerializedName(value = "FirstName", alternate = {"firstName"})
    private String firstName;

    @SerializedName(value = "LastName", alternate = {"lastName"})
    private String lastName;

    @SerializedName(value = "FullName", alternate = {"fullName", "fullname"})
    private String fullName;

    @SerializedName(value = "Token", alternate = {"token", "accessToken", "access_token"})
    private String token;

    @SerializedName(value = "Role", alternate = {"role"})
    private String role;

    @SerializedName(value = "GraduateLevel", alternate = {"graduateLevel"})
    private String graduateLevel;

    @SerializedName(value = "Expire", alternate = {"expire"})
    private String expire;

    @SerializedName(value = "message", alternate = {"Message", "msg", "Msg"})
    private String message;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getGraduateLevel() {
        return graduateLevel;
    }

    public String getExpire() {
        return expire;
    }

    public String getMessage() {
        return message;
    }
}