package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class DepartmentItem {

    @SerializedName(value = "ID", alternate = {"id", "MaPhongBan", "maPhongBan"})
    private String id;

    @SerializedName(value = "TenPhongBan", alternate = {"tenPhongBan", "Name", "name", "DepartmentName"})
    private String tenPhongBan;

    public DepartmentItem() {}

    public DepartmentItem(String id, String tenPhongBan) {
        this.id = id;
        this.tenPhongBan = tenPhongBan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    @Override
    public String toString() {
        return tenPhongBan != null ? tenPhongBan : "";
    }
}
