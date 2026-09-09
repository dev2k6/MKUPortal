/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class CurriculumItem {

    @SerializedName("MaCTDT")
    private String maCTDT;

    @SerializedName("TenCTDT")
    private String tenCTDT;

    @SerializedName("HocKy")
    private String hocKy;

    @SerializedName("BatBuoc")
    private String batBuoc;

    @SerializedName("MaHP")
    private String maHP;

    @SerializedName("TenHP")
    private String tenHP;

    @SerializedName("STC")
    private Integer stc;

    @SerializedName("LT")
    private Integer lt;

    @SerializedName("TH")
    private Integer th;

    @SerializedName("YearStudy")
    private String yearStudy;

    @SerializedName("TermID")
    private String termId;

    @SerializedName("SemesterName")
    private String semesterName;

    public String getMaCTDT() { return maCTDT; }
    public String getTenCTDT() { return tenCTDT; }
    public String getHocKy() { return hocKy; }
    public String getBatBuoc() { return batBuoc; }
    public String getMaHP() { return maHP; }
    public String getTenHP() { return tenHP; }
    public Integer getStc() { return stc; }
    public Integer getLt() { return lt; }
    public Integer getTh() { return th; }
    public String getYearStudy() { return yearStudy; }
    public String getTermId() { return termId; }
    public String getSemesterName() { return semesterName; }
}