package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ContactSubmitRequest {

    @SerializedName("PhongBan")
    private String phongBan;

    @SerializedName("TieuDe")
    private String tieuDe;

    @SerializedName("NoiDung")
    private String noiDung;

    public ContactSubmitRequest() {}

    public ContactSubmitRequest(String phongBan, String tieuDe, String noiDung) {
        this.phongBan = phongBan;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
    }

    public String getPhongBan() {
        return phongBan;
    }

    public void setPhongBan(String phongBan) {
        this.phongBan = phongBan;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}
