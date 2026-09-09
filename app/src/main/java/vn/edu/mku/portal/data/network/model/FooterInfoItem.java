/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
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