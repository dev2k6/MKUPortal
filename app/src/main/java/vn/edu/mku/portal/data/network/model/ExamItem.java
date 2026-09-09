/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

public class ExamItem {

    @SerializedName("MaSV")
    private String maSV;

    @SerializedName("HoTen")
    private String hoTen;

    @SerializedName("Lop")
    private String lop;

    @SerializedName("NamHoc")
    private String namHoc;

    @SerializedName("HocKy")
    private String hocKy;

    @SerializedName("ScheduleStudyUnitID")
    private String scheduleStudyUnitId;

    @SerializedName("CurriculumID")
    private String curriculumId;

    @SerializedName("CurriculumName")
    private String curriculumName;

    @SerializedName("NgayThi")
    private String ngayThi;

    @SerializedName("GioThi")
    private String gioThi;

    @SerializedName("PhongThi")
    private String phongThi;

    @SerializedName("DiaDiem")
    private String diaDiem;

    @SerializedName("Credits")
    private int credits;

    @SerializedName("KyThi")
    private String kyThi;

    @SerializedName("LanThi")
    private String lanThi;

    @SerializedName("SBD")
    private String sbd;

    @SerializedName("Mark")
    private String mark;

    @SerializedName("QuaHan")
    private boolean quaHan;

    @SerializedName("IsNoPhi")
    private int isNoPhi;

    @SerializedName("Status")
    private int status;

    @SerializedName("HinhThucThi")
    private String hinhThucThi;

    @SerializedName("GhiChu")
    private String ghiChu;

    @SerializedName("VangThi")
    private String vangThi;

    @SerializedName("Link")
    private String link;

    @SerializedName("LinkThi")
    private String linkThi;

    @SerializedName("IsCamThi")
    private boolean isCamThi;

    public String getMaSV() { return maSV; }
    public String getHoTen() { return hoTen; }
    public String getLop() { return lop; }
    public String getNamHoc() { return namHoc; }
    public String getHocKy() { return hocKy; }
    public String getScheduleStudyUnitId() { return scheduleStudyUnitId; }
    public String getCurriculumId() { return curriculumId; }
    public String getCurriculumName() { return curriculumName; }
    public String getNgayThi() { return ngayThi; }
    public String getGioThi() { return gioThi; }
    public String getPhongThi() { return phongThi; }
    public String getDiaDiem() { return diaDiem; }
    public int getCredits() { return credits; }
    public String getKyThi() { return kyThi; }
    public String getLanThi() { return lanThi; }
    public String getSbd() { return sbd; }
    public String getMark() { return mark; }
    public boolean isQuaHan() { return quaHan; }
    public int getIsNoPhi() { return isNoPhi; }
    public int getStatus() { return status; }
    public String getHinhThucThi() { return hinhThucThi; }
    public String getGhiChu() { return ghiChu; }
    public String getVangThi() { return vangThi; }
    public String getLink() { return !TextUtils.isEmpty(link) ? link : linkThi; }
    public boolean isCamThi() { return isCamThi || status == -1; }

    public String getDisplayName() {
        String code = scheduleStudyUnitId != null ? scheduleStudyUnitId : "";
        String name = curriculumName != null ? curriculumName : "";
        return !TextUtils.isEmpty(code) ? code + "-" + name : name;
    }
}