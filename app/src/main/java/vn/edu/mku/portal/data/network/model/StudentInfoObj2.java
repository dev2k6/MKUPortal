package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class StudentInfoObj2 {

    @SerializedName("StudentID")
    private String studentId;

    @SerializedName("StudentName")
    private String studentName;

    @SerializedName("StudentEmail")
    private String studentEmail;

    @SerializedName("ClassStudentName")
    private String classStudentName;

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getClassStudentName() { return classStudentName; }
}