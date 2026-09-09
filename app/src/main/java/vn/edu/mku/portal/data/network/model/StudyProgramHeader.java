/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class StudyProgramHeader {

    @SerializedName("StudentID")
    private String studentId;

    @SerializedName("StudyProgramID")
    private String studyProgramId;

    @SerializedName("StudyProgramName")
    private String studyProgramName;

    @SerializedName("Type")
    private int type;

    public String getStudentId() { return studentId; }
    public String getStudyProgramId() { return studyProgramId; }
    public String getStudyProgramName() { return studyProgramName; }
    public int getType() { return type; }
}