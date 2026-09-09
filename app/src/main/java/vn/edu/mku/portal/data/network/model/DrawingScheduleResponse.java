package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DrawingScheduleResponse {

    @SerializedName("ResultDataSchedule")
    private List<DrawingScheduleItem> resultDataSchedule;

    @SerializedName("TimeSchedule")
    private String timeSchedule;

    public List<DrawingScheduleItem> getResultDataSchedule() { return resultDataSchedule; }
    public String getTimeSchedule() { return timeSchedule; }
}