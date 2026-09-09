package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MenuItem {

    @SerializedName("Id")
    private int id;

    @SerializedName("TenChucNang")
    private String tenChucNang;

    @SerializedName("ThuTu")
    private int thuTu;

    @SerializedName("LienKet")
    private String lienKet;

    @SerializedName("DoHoaDeThuong")
    private String doHoaDeThuong;

    @SerializedName("childMenu")
    private List<MenuItem> childMenu;

    public int getId() { return id; }
    public String getTenChucNang() { return tenChucNang; }
    public int getThuTu() { return thuTu; }
    public String getLienKet() { return lienKet; }
    public String getDoHoaDeThuong() { return doHoaDeThuong; }
    public List<MenuItem> getChildMenu() { return childMenu; }
}