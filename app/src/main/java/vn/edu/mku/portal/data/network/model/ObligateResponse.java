/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ObligateResponse {

    @SerializedName("isKhaoSat")
    private boolean isKhaoSat;

    @SerializedName("classID")
    private String classId;

    @SerializedName("Md5")
    private String md5;

    public boolean isKhaoSat() { return isKhaoSat; }
    public String getClassId() { return classId; }
    public String getMd5() { return md5; }
}