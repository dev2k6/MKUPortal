/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class DecisionItem {

    @SerializedName("YearStudy")
    private String yearStudy;

    @SerializedName("TermID")
    private String termId;

    @SerializedName("StudentID")
    private String studentId;

    @SerializedName("DecisionNumber")
    private String decisionNumber;

    @SerializedName("DecisionTypeID")
    private int decisionTypeId;

    @SerializedName("DecisionName")
    private String decisionName;

    @SerializedName("FullText")
    private String fullText;

    @SerializedName("SignDate")
    private String signDate;

    public String getYearStudy() { return yearStudy; }
    public String getTermId() { return termId; }
    public String getStudentId() { return studentId; }
    public String getDecisionNumber() { return decisionNumber; }
    public int getDecisionTypeId() { return decisionTypeId; }
    public String getDecisionName() { return decisionName; }
    public String getFullText() { return fullText; }
    public String getSignDate() { return signDate; }
}