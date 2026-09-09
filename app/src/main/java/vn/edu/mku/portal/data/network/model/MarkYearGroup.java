/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarkYearGroup {

    @SerializedName("NamHoc")
    private String namHoc;

    @SerializedName("DanhSachDiem")
    private List<MarkSemesterGroup> danhSachDiem;

    public String getNamHoc() { return namHoc; }
    public List<MarkSemesterGroup> getDanhSachDiem() { return danhSachDiem; }
}