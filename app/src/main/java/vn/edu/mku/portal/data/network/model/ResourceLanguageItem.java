package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class ResourceLanguageItem {
    @SerializedName("ComponentID")
    private String componentId;

    @SerializedName("KeyLanguage")
    private String keyLanguage;

    @SerializedName("Description")
    private String description;

    public String getComponentId() {
        return componentId;
    }

    public String getKeyLanguage() {
        return keyLanguage;
    }

    public String getDescription() {
        return description;
    }
}