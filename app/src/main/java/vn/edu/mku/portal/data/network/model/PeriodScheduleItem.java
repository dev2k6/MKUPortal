/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class PeriodScheduleItem {

    @SerializedName("MaSV")
    private String maSV;

    @SerializedName("MaLHP")
    private String maLHP;

    @SerializedName("TenHP")
    private String tenHP;

    @SerializedName("SoTC")
    private int soTC;

    @SerializedName("Thu")
    private String thu;

    @SerializedName("Phong")
    private String phong;

    @SerializedName("TuanHoc")
    private String tuanHoc;

    @SerializedName("HoTenGV")
    private String hoTenGV;

    @SerializedName("CampusName")
    private String campusName;

    @SerializedName("CampusAddress")
    private String campusAddress;

    @SerializedName("TKBHienThi1")
    private String tkbHienThi1;

    @SerializedName("TKBHienThi")
    private String tkbHienThi;

    public String getMaSV() { return maSV; }
    public String getMaLHP() { return maLHP; }
    public String getTenHP() { return tenHP; }
    public int getSoTC() { return soTC; }
    public String getThu() { return thu; }
    public String getPhong() { return phong; }
    public String getTuanHoc() { return tuanHoc; }
    public String getHoTenGV() { return hoTenGV; }
    public String getCampusName() { return campusName; }
    public String getCampusAddress() { return campusAddress; }
    public String getTkbHienThi1() { return tkbHienThi1; }
    public String getTkbHienThi() { return tkbHienThi; }
}