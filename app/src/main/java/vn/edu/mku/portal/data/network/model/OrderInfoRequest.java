/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class OrderInfoRequest {

    @SerializedName("ThongTinKhachHang")
    private String thongTinKhachHang;

    @SerializedName("DiaChi")
    private String diaChi;

    @SerializedName("MST")
    private String mst;

    @SerializedName("Email")
    private String email;

    @SerializedName("BudgetCode")
    private String budgetCode;

    public OrderInfoRequest() {}

    public OrderInfoRequest(String thongTinKhachHang, String diaChi, String mst, String email, String budgetCode) {
        this.thongTinKhachHang = thongTinKhachHang;
        this.diaChi = diaChi;
        this.mst = mst;
        this.email = email;
        this.budgetCode = budgetCode;
    }

    public String getThongTinKhachHang() {
        return thongTinKhachHang;
    }

    public void setThongTinKhachHang(String thongTinKhachHang) {
        this.thongTinKhachHang = thongTinKhachHang;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getMst() {
        return mst;
    }

    public void setMst(String mst) {
        this.mst = mst;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }
}
