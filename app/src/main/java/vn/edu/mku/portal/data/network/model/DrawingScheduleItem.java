package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class DrawingScheduleItem {

    @SerializedName("CurriculumName")
    private String curriculumName;

    @SerializedName("Credits")
    private double credits;

    @SerializedName("PeriodID")
    private Integer periodId;

    @SerializedName("NumberOfPeriods")
    private Integer numberOfPeriods;

    @SerializedName("DayOfWeek")
    private Integer dayOfWeek;

    @SerializedName("Thu")
    private String thu;

    @SerializedName("Date")
    private String date;

    @SerializedName("RoomID")
    private String roomId;

    @SerializedName("ProfessorName")
    private String professorName;

    @SerializedName("TKHHienThi")
    private String tkhHienThi;

    @SerializedName("Color")
    private String color;

    public String getCurriculumName() { return curriculumName; }
    public double getCredits() { return credits; }
    public Integer getPeriodId() { return periodId; }
    public Integer getNumberOfPeriods() { return numberOfPeriods; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public String getThu() { return thu; }
    public String getDate() { return date; }
    public String getRoomId() { return roomId; }
    public String getProfessorName() { return professorName; }
    public String getTkhHienThi() { return tkhHienThi; }
    public String getColor() { return color; }
}