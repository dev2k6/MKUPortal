/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class AccountFeeItem {

    @SerializedName("HoTen")
    private String hoTen;

    @SerializedName("Lop")
    private String lop;

    @SerializedName("DienThoai")
    private String dienThoai;

    @SerializedName("He")
    private String he;

    @SerializedName("LoaiHinh")
    private String loaiHinh;

    @SerializedName("AccountID")
    private String accountId;

    @SerializedName("TransactionID")
    private long transactionId;

    @SerializedName("FeeID")
    private String feeId;

    @SerializedName("FeeName")
    private String feeName;

    @SerializedName("DebtAmount")
    private double debtAmount;

    @SerializedName("PaidDate")
    private String paidDate;

    @SerializedName("PaidAmount")
    private double paidAmount;

    @SerializedName("DaGiam")
    private double daGiam;

    @SerializedName("YearStudy")
    private String yearStudy;

    @SerializedName("TermID")
    private String termId;

    @SerializedName("SoHoaDon")
    private String soHoaDon;

    @SerializedName("RegistType")
    private String registType;

    @SerializedName("TenHinhThucThanhToan")
    private String tenHinhThucThanhToan;

    @SerializedName("NoiDungThu")
    private String noiDungThu;

    @SerializedName("ConNo")
    private double conNo;

    public String getHoTen() { return hoTen; }
    public String getLop() { return lop; }
    public String getDienThoai() { return dienThoai; }
    public String getHe() { return he; }
    public String getLoaiHinh() { return loaiHinh; }
    public String getAccountId() { return accountId; }
    public long getTransactionId() { return transactionId; }
    public String getFeeId() { return feeId; }
    public String getFeeName() { return feeName; }
    public double getDebtAmount() { return debtAmount; }
    public String getPaidDate() { return paidDate; }
    public double getPaidAmount() { return paidAmount; }
    public double getDaGiam() { return daGiam; }
    public String getYearStudy() { return yearStudy; }
    public String getTermId() { return termId; }
    public String getSoHoaDon() { return soHoaDon; }
    public String getRegistType() { return registType; }
    public String getTenHinhThucThanhToan() { return tenHinhThucThanhToan; }
    public String getNoiDungThu() { return noiDungThu; }
    public double getConNo() { return conNo; }
}