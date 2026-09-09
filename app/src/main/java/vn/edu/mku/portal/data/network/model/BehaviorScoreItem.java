package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class BehaviorScoreItem {

    @SerializedName("ClassStudentName")
    private String classStudentName;

    @SerializedName("YearStudy")
    private String yearStudy;

    @SerializedName("TermID")
    private String termId;

    @SerializedName("LastScore")
    private double lastScore;

    @SerializedName("BehaviorScoreRank")
    private String behaviorScoreRank;

    @SerializedName("StudentID")
    private String studentId;

    @SerializedName("TongDiem")
    private Double tongDiem;

    @SerializedName("XepLoai")
    private String xepLoai;

    @SerializedName("TongDiemTK")
    private Double tongDiemTK;

    @SerializedName("XepLoaiTK")
    private String xepLoaiTK;

    public String getClassStudentName() { return classStudentName; }
    public String getYearStudy() { return yearStudy; }
    public String getTermId() { return termId; }
    public double getLastScore() { return lastScore; }
    public String getBehaviorScoreRank() { return behaviorScoreRank; }
    public String getStudentId() { return studentId; }
    public Double getTongDiem() { return tongDiem; }
    public String getXepLoai() { return xepLoai; }
    public Double getTongDiemTK() { return tongDiemTK; }
    public String getXepLoaiTK() { return xepLoaiTK; }
}