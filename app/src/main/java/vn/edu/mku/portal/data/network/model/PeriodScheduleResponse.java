package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PeriodScheduleResponse {

    @SerializedName("result")
    private List<PeriodScheduleItem> result;

    public List<PeriodScheduleItem> getResult() { return result; }
}