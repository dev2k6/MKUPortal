/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class MarkCourseItem {

    @SerializedName("StudentID")
    private String studentId;

    @SerializedName("CurriculumID")
    private String curriculumId;

    @SerializedName("CurriculumName")
    private String curriculumName;

    @SerializedName("Credits")
    private String credits;

    @SerializedName("DiemTK_10")
    private String diemTK10;

    @SerializedName("DiemTK_4")
    private String diemTK4;

    @SerializedName("DiemTK_Chu")
    private String diemTKChu;

    @SerializedName("IsPass")
    private String isPass;

    @SerializedName("ScheduleStudyUnitID")
    private String scheduleStudyUnitId;

    @SerializedName("Dat_HK")
    private String datHk;

    @SerializedName("TB_HK")
    private String tbHk;

    @SerializedName("TB_HK4")
    private String tbHk4;

    @SerializedName("Dat_TL_HK")
    private String datTlHk;

    @SerializedName("TongTC_DK_HK")
    private String tongTcDkHk;

    @SerializedName("TB_TL_HK")
    private String tbTlHk;

    @SerializedName("TB_TL_HK4")
    private String tbTlHk4;

    @SerializedName("DiemRenLuyenHK")
    private String diemRenLuyenHk;

    public String getStudentId() { return studentId; }
    public String getCurriculumId() { return curriculumId; }
    public String getCurriculumName() { return curriculumName; }
    public String getCredits() { return credits; }
    public String getDiemTK10() { return diemTK10; }
    public String getDiemTK4() { return diemTK4; }
    public String getDiemTKChu() { return diemTKChu; }
    public String getIsPass() { return isPass; }
    public String getScheduleStudyUnitId() { return scheduleStudyUnitId; }
    public String getDatHk() { return datHk; }
    public String getTbHk() { return tbHk; }
    public String getTbHk4() { return tbHk4; }
    public String getDatTlHk() { return datTlHk; }
    public String getTongTcDkHk() { return tongTcDkHk; }
    public String getTbTlHk() { return tbTlHk; }
    public String getTbTlHk4() { return tbTlHk4; }
    public String getDiemRenLuyenHk() { return diemRenLuyenHk; }
}