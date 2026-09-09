package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudyProgramDetailResponse {

    @SerializedName("tbStudyPrograms")
    private List<CurriculumItem> tbStudyPrograms;

    public List<CurriculumItem> getTbStudyPrograms() {
        return tbStudyPrograms;
    }
}