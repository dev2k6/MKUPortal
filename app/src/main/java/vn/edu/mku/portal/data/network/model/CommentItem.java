package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class CommentItem {

    @SerializedName(value = "MaHocPhan", alternate = {"maHocPhan", "CurriculumID", "curriculumId"})
    private String maHocPhan;

    @SerializedName(value = "TenHocPhan", alternate = {"tenHocPhan", "CurriculumName", "curriculumName"})
    private String tenHocPhan;

    @SerializedName(value = "STC", alternate = {"stc", "Credits", "credits"})
    private Integer stc;

    @SerializedName(value = "ThongTin", alternate = {"thongTin", "Information", "information"})
    private String thongTin;

    @SerializedName(value = "GiangVien", alternate = {"giangVien", "LecturerName", "lecturerName"})
    private String giangVien;

    @SerializedName(value = "DanhSach", alternate = {"danhSach"})
    private String danhSach;

    @SerializedName(value = "ThaoLuan", alternate = {"thaoLuan", "Comment", "comment"})
    private String thaoLuan;

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getTenHocPhan() {
        return tenHocPhan;
    }

    public void setTenHocPhan(String tenHocPhan) {
        this.tenHocPhan = tenHocPhan;
    }

    public Integer getStc() {
        return stc;
    }

    public void setStc(Integer stc) {
        this.stc = stc;
    }

    public String getThongTin() {
        return thongTin;
    }

    public void setThongTin(String thongTin) {
        this.thongTin = thongTin;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(String giangVien) {
        this.giangVien = giangVien;
    }

    public String getDanhSach() {
        return danhSach;
    }

    public void setDanhSach(String danhSach) {
        this.danhSach = danhSach;
    }

    public String getThaoLuan() {
        return thaoLuan;
    }

    public void setThaoLuan(String thaoLuan) {
        this.thaoLuan = thaoLuan;
    }
}
