package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class WeekItem {

    @SerializedName("Year")
    private int year;

    @SerializedName("Week")
    private int week;

    @SerializedName("BeginDate")
    private String beginDate;

    @SerializedName("EndDate")
    private String endDate;

    @SerializedName("YearStudy")
    private String yearStudy;

    @SerializedName("TermID")
    private String termId;

    @SerializedName("DisPlayWeek")
    private int disPlayWeek;

    public int getYear() { return year; }
    public int getWeek() { return week; }
    public String getBeginDate() { return beginDate; }
    public String getEndDate() { return endDate; }
    public String getYearStudy() { return yearStudy; }
    public String getTermId() { return termId; }
    public int getDisPlayWeek() { return disPlayWeek; }

    public String getDisplayRange() {
        return (beginDate != null ? beginDate : "") + " - " + (endDate != null ? endDate : "");
    }
}