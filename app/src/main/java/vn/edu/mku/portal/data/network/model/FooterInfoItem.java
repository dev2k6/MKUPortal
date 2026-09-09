package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class FooterInfoItem {

    @SerializedName("SettingName")
    private String settingName;

    @SerializedName("SettingStringData")
    private String settingStringData;

    public String getSettingName() { return settingName; }
    public String getSettingStringData() { return settingStringData; }
}