package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetailItem {

    @SerializedName("KyHieu")
    private String kyHieu;

    @SerializedName("SoHoaDon")
    private String soHoaDon;

    @SerializedName("NgayDong")
    private String ngayDong;

    @SerializedName("NamHoc")
    private String namHoc;

    @SerializedName("HocKy")
    private String hocKy;

    @SerializedName("NoiDungThu")
    private String noiDungThu;

    @SerializedName("TenHinhThucThanhToan")
    private String tenHinhThucThanhToan;

    @SerializedName("TongTien")
    private Double tongTien;

    @SerializedName("MaPhi")
    private String maPhi;

    @SerializedName("TenPhi")
    private String tenPhi;

    @SerializedName("DaDong")
    private Double daDong;

    @SerializedName("LinkHoaDon")
    private String linkHoaDon;

    public String getKyHieu() {
        return kyHieu;
    }

    public void setKyHieu(String kyHieu) {
        this.kyHieu = kyHieu;
    }

    public String getSoHoaDon() {
        return soHoaDon;
    }

    public void setSoHoaDon(String soHoaDon) {
        this.soHoaDon = soHoaDon;
    }

    public String getNgayDong() {
        return ngayDong;
    }

    public void setNgayDong(String ngayDong) {
        this.ngayDong = ngayDong;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public String getHocKy() {
        return hocKy;
    }

    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }

    public String getNoiDungThu() {
        return noiDungThu;
    }

    public void setNoiDungThu(String noiDungThu) {
        this.noiDungThu = noiDungThu;
    }

    public String getTenHinhThucThanhToan() {
        return tenHinhThucThanhToan;
    }

    public void setTenHinhThucThanhToan(String tenHinhThucThanhToan) {
        this.tenHinhThucThanhToan = tenHinhThucThanhToan;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public String getMaPhi() {
        return maPhi;
    }

    public void setMaPhi(String maPhi) {
        this.maPhi = maPhi;
    }

    public String getTenPhi() {
        return tenPhi;
    }

    public void setTenPhi(String tenPhi) {
        this.tenPhi = tenPhi;
    }

    public Double getDaDong() {
        return daDong;
    }

    public void setDaDong(Double daDong) {
        this.daDong = daDong;
    }

    public String getLinkHoaDon() {
        return linkHoaDon;
    }

    public void setLinkHoaDon(String linkHoaDon) {
        this.linkHoaDon = linkHoaDon;
    }
}
