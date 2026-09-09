package vn.edu.mku.portal.ui.student;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.data.network.model.AccountFeeItem;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class AccountFeesActivity extends BaseStudentActivity {

    private MaterialButton btnOpenPaymentDialog;
    private TextView tvFeeStudentInfo;
    private TextView tvGrandDebt;
    private TextView tvGrandPaid;
    private TextView tvGrandDiscount;
    private TextView tvGrandConNo;
    private LinearLayout containerFeeTable;

    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_fees);

        setupCommonUi();

        btnOpenPaymentDialog = findViewById(R.id.btnOpenPaymentDialog);
        tvFeeStudentInfo = findViewById(R.id.tvFeeStudentInfo);
        tvGrandDebt = findViewById(R.id.tvGrandDebt);
        tvGrandPaid = findViewById(R.id.tvGrandPaid);
        tvGrandDiscount = findViewById(R.id.tvGrandDiscount);
        tvGrandConNo = findViewById(R.id.tvGrandConNo);
        containerFeeTable = findViewById(R.id.containerFeeTable);

        if (btnOpenPaymentDialog != null) {
            btnOpenPaymentDialog.setOnClickListener(v -> showPaymentConfirmationDialog());
        }

        fetchAccountFeesData();
    }

    private void fetchAccountFeesData() {
        showSkeletonRows();
        studentRepository.fetchAccountFees(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<AccountFeeItem> result) {
                SkeletonHelper.stopPulseAnimation(containerFeeTable);
                populateFeeTable(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerFeeTable);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerFeeTable == null) return;
        containerFeeTable.removeAllViews();
        for (int i = 0; i < 6; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerFeeTable, false);
            containerFeeTable.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerFeeTable);
    }

    private void populateFeeTable(List<AccountFeeItem> list) {
        if (containerFeeTable == null) return;
        containerFeeTable.removeAllViews();

        if (list == null || list.isEmpty()) return;

        // Populate Student Header Info on Card 1
        AccountFeeItem first = list.get(0);
        if (tvFeeStudentInfo != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Họ và tên: ").append(formatValue(first.getHoTen())).append("\n");
            sb.append("Điện thoại: ").append(formatValue(first.getDienThoai())).append("\n");
            sb.append("Hệ: ").append(formatValue(first.getHe())).append("\n\n");
            sb.append("Mã sinh viên: ").append(formatValue(first.getAccountId())).append("\n");
            sb.append("Lớp: ").append(formatValue(first.getLop())).append("\n");
            sb.append("Loại hình: ").append(formatValue(first.getLoaiHinh()));
            tvFeeStudentInfo.setText(sb.toString());
        }

        // Calculate Grand Totals
        double grandDebt = 0;
        double grandPaid = 0;
        double grandDiscount = 0;
        double grandConNo = 0;

        for (AccountFeeItem item : list) {
            grandDebt += item.getDebtAmount();
            grandPaid += item.getPaidAmount();
            grandDiscount += item.getDaGiam();
            grandConNo += item.getConNo();
        }

        if (tvGrandDebt != null) tvGrandDebt.setText(formatMoney(grandDebt));
        if (tvGrandPaid != null) tvGrandPaid.setText(formatMoney(grandPaid));
        if (tvGrandDiscount != null) tvGrandDiscount.setText(formatMoney(grandDiscount));
        if (tvGrandConNo != null) tvGrandConNo.setText(formatMoney(grandConNo));

        // Group Fees by "YearStudy - TermID"
        Map<String, List<AccountFeeItem>> groupMap = new LinkedHashMap<>();
        for (AccountFeeItem item : list) {
            String key = formatValue(item.getYearStudy()) + " - " + formatValue(item.getTermId());
            if (!groupMap.containsKey(key)) {
                groupMap.put(key, new ArrayList<>());
            }
            List<AccountFeeItem> groupList = groupMap.get(key);
            if (groupList != null) {
                groupList.add(item);
            }
        }

        for (Map.Entry<String, List<AccountFeeItem>> entry : groupMap.entrySet()) {
            String groupKey = entry.getKey();
            List<AccountFeeItem> itemsInGroup = entry.getValue();
            if (itemsInGroup.isEmpty()) continue;

            // Pink Year-Term Header Banner
            TextView tvGroupHeader = new TextView(this);
            tvGroupHeader.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(950), ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGroupHeader.setText("Năm học: " + groupKey);
            tvGroupHeader.setTextColor(Color.BLACK);
            tvGroupHeader.setTextSize(13);
            tvGroupHeader.setTypeface(null, Typeface.BOLD);
            tvGroupHeader.setBackgroundColor(Color.parseColor("#FFCDD2"));
            tvGroupHeader.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            containerFeeTable.addView(tvGroupHeader);

            double subDebt = 0;
            double subPaid = 0;
            double subDiscount = 0;
            double subConNo = 0;

            for (AccountFeeItem fee : itemsInGroup) {
                subDebt += fee.getDebtAmount();
                subPaid += fee.getPaidAmount();
                subDiscount += fee.getDaGiam();
                subConNo += fee.getConNo();

                View rowView = LayoutInflater.from(this).inflate(R.layout.item_account_fee_row, containerFeeTable, false);
                TextView tvFeeName = rowView.findViewById(R.id.tvFeeName);
                TextView tvRegistType = rowView.findViewById(R.id.tvRegistType);
                TextView tvDebtAmount = rowView.findViewById(R.id.tvDebtAmount);
                TextView tvPaidAmount = rowView.findViewById(R.id.tvPaidAmount);
                TextView tvDaGiam = rowView.findViewById(R.id.tvDaGiam);
                TextView tvConNo = rowView.findViewById(R.id.tvConNo);
                TextView tvPaidDate = rowView.findViewById(R.id.tvPaidDate);
                TextView tvNoiDungThu = rowView.findViewById(R.id.tvNoiDungThu);

                String displayName = !TextUtils.isEmpty(fee.getFeeId()) ? fee.getFeeId() + "- " + fee.getFeeName() : "-" + fee.getFeeName();
                if (tvFeeName != null) tvFeeName.setText(displayName);
                if (tvRegistType != null) tvRegistType.setText(formatValue(fee.getRegistType()));
                if (tvDebtAmount != null) tvDebtAmount.setText(formatMoney(fee.getDebtAmount()));
                if (tvPaidAmount != null) tvPaidAmount.setText(formatMoney(fee.getPaidAmount()));
                if (tvDaGiam != null) tvDaGiam.setText(formatMoney(fee.getDaGiam()));
                if (tvConNo != null) tvConNo.setText(formatMoney(fee.getConNo()));
                if (tvPaidDate != null) tvPaidDate.setText(formatValue(fee.getPaidDate()));
                if (tvNoiDungThu != null) tvNoiDungThu.setText(formatValue(fee.getNoiDungThu()));

                containerFeeTable.addView(rowView);
            }

            // Subtotal Yellow Banner
            LinearLayout subtotalRow = new LinearLayout(this);
            subtotalRow.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(950), ViewGroup.LayoutParams.WRAP_CONTENT));
            subtotalRow.setOrientation(LinearLayout.HORIZONTAL);
            subtotalRow.setBackgroundColor(Color.parseColor("#FFE082"));
            subtotalRow.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));

            TextView tvSubLabel = new TextView(this);
            tvSubLabel.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(268), ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSubLabel.setText("Tổng học phí:" + groupKey);
            tvSubLabel.setTextColor(Color.BLACK);
            tvSubLabel.setTextSize(13);
            tvSubLabel.setTypeface(null, Typeface.BOLD);
            subtotalRow.addView(tvSubLabel);

            TextView tvSubDebt = new TextView(this);
            tvSubDebt.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSubDebt.setGravity(Gravity.END);
            tvSubDebt.setText(formatMoney(subDebt));
            tvSubDebt.setTextColor(Color.BLACK);
            tvSubDebt.setTextSize(13);
            tvSubDebt.setTypeface(null, Typeface.BOLD);
            subtotalRow.addView(tvSubDebt);

            TextView tvSubPaid = new TextView(this);
            LinearLayout.LayoutParams lpPaid = new LinearLayout.LayoutParams(dpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT);
            lpPaid.setMarginStart(dpToPx(8));
            tvSubPaid.setLayoutParams(lpPaid);
            tvSubPaid.setGravity(Gravity.END);
            tvSubPaid.setText(formatMoney(subPaid));
            tvSubPaid.setTextColor(Color.BLACK);
            tvSubPaid.setTextSize(13);
            tvSubPaid.setTypeface(null, Typeface.BOLD);
            subtotalRow.addView(tvSubPaid);

            TextView tvSubDiscount = new TextView(this);
            LinearLayout.LayoutParams lpDisc = new LinearLayout.LayoutParams(dpToPx(80), ViewGroup.LayoutParams.WRAP_CONTENT);
            lpDisc.setMarginStart(dpToPx(8));
            tvSubDiscount.setLayoutParams(lpDisc);
            tvSubDiscount.setGravity(Gravity.END);
            tvSubDiscount.setText(formatMoney(subDiscount));
            tvSubDiscount.setTextColor(Color.BLACK);
            tvSubDiscount.setTextSize(13);
            tvSubDiscount.setTypeface(null, Typeface.BOLD);
            subtotalRow.addView(tvSubDiscount);

            TextView tvSubConNo = new TextView(this);
            LinearLayout.LayoutParams lpConNo = new LinearLayout.LayoutParams(dpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT);
            lpConNo.setMarginStart(dpToPx(8));
            tvSubConNo.setLayoutParams(lpConNo);
            tvSubConNo.setGravity(Gravity.END);
            tvSubConNo.setText(formatMoney(subConNo));
            tvSubConNo.setTextColor(Color.BLACK);
            tvSubConNo.setTextSize(13);
            tvSubConNo.setTypeface(null, Typeface.BOLD);
            subtotalRow.addView(tvSubConNo);

            containerFeeTable.addView(subtotalRow);
        }
    }

    private void showPaymentConfirmationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_payment, null);

        MaterialButton btnDoPay = dialogView.findViewById(R.id.btnDoPayTuition);
        MaterialButton btnExit = dialogView.findViewById(R.id.btnExitPayment);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        if (btnExit != null) {
            btnExit.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnDoPay != null) {
            btnDoPay.setOnClickListener(v -> {
                dialog.dismiss();
                String studentId = SessionManager.getInstance().getStudentId();
                String url = "https://e-bills.vn/pay/mku?customer=" + (studentId != null ? studentId : "");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            });
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(dpToPx(340), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private String formatMoney(double val) {
        return currencyFormatter.format(val);
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedStrings();
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("FinanceComponent", "Finance", getString(R.string.menu_student_finance)));
        }
    }
}