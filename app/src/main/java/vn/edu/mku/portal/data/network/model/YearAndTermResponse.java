package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YearAndTermResponse {

    @SerializedName("YearStudy")
    private List<String> yearStudy;

    @SerializedName("Terms")
    private List<TermItem> terms;

    @SerializedName("CurrentYear")
    private String currentYear;

    @SerializedName("CurrentTerm")
    private String currentTerm;

    public List<String> getYearStudy() { return yearStudy; }
    public List<TermItem> getTerms() { return terms; }
    public String getCurrentYear() { return currentYear; }
    public String getCurrentTerm() { return currentTerm; }

    public static class TermItem {
        @SerializedName("TermID")
        private String termId;

        @SerializedName("TermName")
        private String termName;

        public String getTermId() { return termId; }
        public String getTermName() { return termName; }
    }
}