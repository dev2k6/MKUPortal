/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarkSemesterGroup {

    @SerializedName("HocKy")
    private String hocKy;

    @SerializedName("DanhSachDiemHK")
    private List<MarkCourseItem> danhSachDiemHK;

    public String getHocKy() { return hocKy; }
    public List<MarkCourseItem> getDanhSachDiemHK() { return danhSachDiemHK; }
}