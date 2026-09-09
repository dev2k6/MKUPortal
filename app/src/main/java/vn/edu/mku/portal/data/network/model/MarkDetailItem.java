package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class MarkDetailItem {

    @SerializedName("AssignmentName")
    private String assignmentName;

    @SerializedName("CurriculumName")
    private String curriculumName;

    @SerializedName("Assignmentdetail")
    private String assignmentDetail;

    @SerializedName("FirstMark")
    private String firstMark;

    @SerializedName("SecondMark")
    private String secondMark;

    @SerializedName("ThirdMark")
    private String thirdMark;

    public String getAssignmentName() { return assignmentName; }
    public String getCurriculumName() { return curriculumName; }
    public String getAssignmentDetail() { return assignmentDetail; }
    public String getFirstMark() { return firstMark; }
    public String getSecondMark() { return secondMark; }
    public String getThirdMark() { return thirdMark; }
}