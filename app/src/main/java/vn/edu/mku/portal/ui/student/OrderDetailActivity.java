package vn.edu.mku.portal.ui.student;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.network.model.OrderDetailItem;
import vn.edu.mku.portal.data.network.model.OrderInfoRequest;
import vn.edu.mku.portal.data.network.model.OrderInfoResponse;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class OrderDetailActivity extends BaseStudentActivity {

    private static final String PREF_DECLARATION = "InvoiceDeclarationPref";
    private static final String KEY_ORG_NAME = "org_name";
    private static final String KEY_ORG_ADDRESS = "org_address";
    private static final String KEY_TAX_CODE = "tax_code";
    private static final String KEY_EMAIL = "declaration_email";
    private static final String KEY_BUDGET_CODE = "budget_code";

    private TabLayout tabLayoutOrderDetail;
    private LinearLayout containerTabDetails;
    private LinearLayout containerTabDeclaration;

    // Tab 1 Views
    private AutoCompleteTextView spinnerYearOrder;
    private AutoCompleteTextView spinnerTermOrder;
    private LinearLayout containerOrderRows;

    // Tab 2 Views
    private TextInputEditText etOrgName;
    private TextInputEditText etOrgAddress;
    private TextInputEditText etTaxCode;
    private TextInputEditText etDeclarationEmail;
    private TextInputEditText etBudgetCode;
    private MaterialButton btnSaveDeclaration;
    private MaterialButton btnClearDeclaration;

    private final List<OrderDetailItem> allOrderItems = new ArrayList<>();
    private String selectedYear = "Tất cả";
    private String selectedTerm = "Tất cả";

    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setupCommonUi();
        initViews();
        setupTabListeners();
        setupDeclarationForm();
        fetchOrderDetailsData();
    }

    private void initViews() {
        tabLayoutOrderDetail = findViewById(R.id.tabLayoutOrderDetail);
        containerTabDetails = findViewById(R.id.containerTabDetails);
        containerTabDeclaration = findViewById(R.id.containerTabDeclaration);

        spinnerYearOrder = findViewById(R.id.spinnerYearOrder);
        spinnerTermOrder = findViewById(R.id.spinnerTermOrder);
        containerOrderRows = findViewById(R.id.containerOrderRows);

        etOrgName = findViewById(R.id.etOrgName);
        etOrgAddress = findViewById(R.id.etOrgAddress);
        etTaxCode = findViewById(R.id.etTaxCode);
        etDeclarationEmail = findViewById(R.id.etDeclarationEmail);
        etBudgetCode = findViewById(R.id.etBudgetCode);
        btnSaveDeclaration = findViewById(R.id.btnSaveDeclaration);
        btnClearDeclaration = findViewById(R.id.btnClearDeclaration);
    }

    private void setupTabListeners() {
        if (tabLayoutOrderDetail != null) {
            tabLayoutOrderDetail.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        containerTabDetails.setVisibility(View.VISIBLE);
                        containerTabDeclaration.setVisibility(View.GONE);
                    } else {
                        containerTabDetails.setVisibility(View.GONE);
                        containerTabDeclaration.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    private void setupDeclarationForm() {
        loadDeclarationData();

        if (btnSaveDeclaration != null) {
            btnSaveDeclaration.setOnClickListener(v -> saveDeclarationData());
        }

        if (btnClearDeclaration != null) {
            btnClearDeclaration.setOnClickListener(v -> clearDeclarationData());
        }
    }

    private void loadDeclarationData() {
        SharedPreferences prefs = getSharedPreferences(PREF_DECLARATION, Context.MODE_PRIVATE);
        if (etOrgName != null) etOrgName.setText(prefs.getString(KEY_ORG_NAME, ""));
        if (etOrgAddress != null) etOrgAddress.setText(prefs.getString(KEY_ORG_ADDRESS, ""));
        if (etTaxCode != null) etTaxCode.setText(prefs.getString(KEY_TAX_CODE, ""));
        if (etDeclarationEmail != null) etDeclarationEmail.setText(prefs.getString(KEY_EMAIL, ""));
        if (etBudgetCode != null) etBudgetCode.setText(prefs.getString(KEY_BUDGET_CODE, ""));

        studentRepository.fetchOrderInfo(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(OrderInfoRequest result) {
                if (result != null) {
                    if (etOrgName != null && !TextUtils.isEmpty(result.getThongTinKhachHang())) {
                        etOrgName.setText(result.getThongTinKhachHang());
                    }
                    if (etOrgAddress != null && !TextUtils.isEmpty(result.getDiaChi())) {
                        etOrgAddress.setText(result.getDiaChi());
                    }
                    if (etTaxCode != null && !TextUtils.isEmpty(result.getMst())) {
                        etTaxCode.setText(result.getMst());
                    }
                    if (etDeclarationEmail != null && !TextUtils.isEmpty(result.getEmail())) {
                        etDeclarationEmail.setText(result.getEmail());
                    }
                    if (etBudgetCode != null && !TextUtils.isEmpty(result.getBudgetCode())) {
                        etBudgetCode.setText(result.getBudgetCode());
                    }
                    saveLocalPrefs(
                            result.getThongTinKhachHang(),
                            result.getDiaChi(),
                            result.getMst(),
                            result.getEmail(),
                            result.getBudgetCode()
                    );
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void saveLocalPrefs(String orgName, String orgAddress, String taxCode, String email, String budgetCode) {
        SharedPreferences prefs = getSharedPreferences(PREF_DECLARATION, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_ORG_NAME, orgName != null ? orgName : "")
                .putString(KEY_ORG_ADDRESS, orgAddress != null ? orgAddress : "")
                .putString(KEY_TAX_CODE, taxCode != null ? taxCode : "")
                .putString(KEY_EMAIL, email != null ? email : "")
                .putString(KEY_BUDGET_CODE, budgetCode != null ? budgetCode : "")
                .apply();
    }

    private void saveDeclarationData() {
        String orgName = etOrgName != null && etOrgName.getText() != null ? etOrgName.getText().toString().trim() : "";
        String orgAddress = etOrgAddress != null && etOrgAddress.getText() != null ? etOrgAddress.getText().toString().trim() : "";
        String taxCode = etTaxCode != null && etTaxCode.getText() != null ? etTaxCode.getText().toString().trim() : "";
        String email = etDeclarationEmail != null && etDeclarationEmail.getText() != null ? etDeclarationEmail.getText().toString().trim() : "";
        String budgetCode = etBudgetCode != null && etBudgetCode.getText() != null ? etBudgetCode.getText().toString().trim() : "";

        OrderInfoRequest request = new OrderInfoRequest(orgName, orgAddress, taxCode, email, budgetCode);

        if (btnSaveDeclaration != null) btnSaveDeclaration.setEnabled(false);

        studentRepository.submitOrderInfo(request, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(OrderInfoResponse result) {
                if (btnSaveDeclaration != null) btnSaveDeclaration.setEnabled(true);
                saveLocalPrefs(orgName, orgAddress, taxCode, email, budgetCode);

                String msg = (result != null && !TextUtils.isEmpty(result.getMessage()) && !"1".equals(result.getMessage()))
                        ? result.getMessage() : getString(R.string.msg_declaration_saved);
                Snackbar.make(findViewById(R.id.mainCoordinator), msg, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                if (btnSaveDeclaration != null) btnSaveDeclaration.setEnabled(true);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void clearDeclarationData() {
        OrderInfoRequest request = new OrderInfoRequest("", "", "", "", "");

        if (btnClearDeclaration != null) btnClearDeclaration.setEnabled(false);

        studentRepository.deleteOrderInfo(request, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(OrderInfoResponse result) {
                if (btnClearDeclaration != null) btnClearDeclaration.setEnabled(true);

                SharedPreferences prefs = getSharedPreferences(PREF_DECLARATION, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();

                if (etOrgName != null) etOrgName.setText("");
                if (etOrgAddress != null) etOrgAddress.setText("");
                if (etTaxCode != null) etTaxCode.setText("");
                if (etDeclarationEmail != null) etDeclarationEmail.setText("");
                if (etBudgetCode != null) etBudgetCode.setText("");

                String msg = (result != null && !TextUtils.isEmpty(result.getMessage()))
                        ? result.getMessage() : getString(R.string.msg_declaration_cleared);
                Snackbar.make(findViewById(R.id.mainCoordinator), msg, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                if (btnClearDeclaration != null) btnClearDeclaration.setEnabled(true);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchOrderDetailsData() {
        showSkeletonRows();
        studentRepository.fetchOrderDetails(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<OrderDetailItem> result) {
                SkeletonHelper.stopPulseAnimation(containerOrderRows);
                allOrderItems.clear();
                if (result != null) {
                    allOrderItems.addAll(result);
                }
                setupFiltersAndPopulateTable();
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerOrderRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerOrderRows == null) return;
        containerOrderRows.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerOrderRows, false);
            containerOrderRows.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerOrderRows);
    }

    private void setupFiltersAndPopulateTable() {
        Set<String> yearSet = new LinkedHashSet<>();
        yearSet.add(getString(R.string.label_all));

        Set<String> termSet = new LinkedHashSet<>();
        termSet.add(getString(R.string.label_all));

        for (OrderDetailItem item : allOrderItems) {
            if (!TextUtils.isEmpty(item.getNamHoc())) {
                yearSet.add(item.getNamHoc());
            }
            if (!TextUtils.isEmpty(item.getHocKy())) {
                termSet.add(formatTermDisplayName(item.getHocKy()));
            }
        }

        List<String> yearList = new ArrayList<>(yearSet);
        List<String> termList = new ArrayList<>(termSet);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yearList);
        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, termList);

        if (spinnerYearOrder != null) {
            spinnerYearOrder.setAdapter(yearAdapter);
            if (!yearList.isEmpty()) {
                selectedYear = yearList.get(0);
                spinnerYearOrder.setText(selectedYear, false);
            }
            spinnerYearOrder.setOnItemClickListener((parent, view, position, id) -> {
                selectedYear = yearList.get(position);
                filterAndRenderTable();
            });
        }

        if (spinnerTermOrder != null) {
            spinnerTermOrder.setAdapter(termAdapter);
            if (!termList.isEmpty()) {
                selectedTerm = termList.get(0);
                spinnerTermOrder.setText(selectedTerm, false);
            }
            spinnerTermOrder.setOnItemClickListener((parent, view, position, id) -> {
                selectedTerm = termList.get(position);
                filterAndRenderTable();
            });
        }

        filterAndRenderTable();
    }

    private String formatTermDisplayName(String termCode) {
        if (termCode == null) return "";
        if ("HK01".equalsIgnoreCase(termCode) || "1".equals(termCode)) return "Học kỳ 1";
        if ("HK02".equalsIgnoreCase(termCode) || "2".equals(termCode)) return "Học kỳ 2";
        if ("HK03".equalsIgnoreCase(termCode) || "3".equals(termCode)) return "Học kỳ 3";
        return termCode;
    }

    private void filterAndRenderTable() {
        if (containerOrderRows == null) return;
        containerOrderRows.removeAllViews();

        String labelAll = getString(R.string.label_all);

        List<OrderDetailItem> filteredList = new ArrayList<>();
        for (OrderDetailItem item : allOrderItems) {
            boolean matchesYear = labelAll.equalsIgnoreCase(selectedYear)
                    || (item.getNamHoc() != null && item.getNamHoc().equalsIgnoreCase(selectedYear));

            boolean matchesTerm = labelAll.equalsIgnoreCase(selectedTerm)
                    || (item.getHocKy() != null && (item.getHocKy().equalsIgnoreCase(selectedTerm)
                    || formatTermDisplayName(item.getHocKy()).equalsIgnoreCase(selectedTerm)));

            if (matchesYear && matchesTerm) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setPadding(32, 32, 32, 32);
            tvEmpty.setText(R.string.text_no_new_notifications);
            tvEmpty.setTextColor(getResources().getColor(R.color.mku_text_sub, getTheme()));
            tvEmpty.setGravity(android.view.Gravity.CENTER);
            containerOrderRows.addView(tvEmpty);
            return;
        }

        for (OrderDetailItem item : filteredList) {
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_order_detail_row, containerOrderRows, false);

            TextView tvSoHoaDon = rowView.findViewById(R.id.tvSoHoaDon);
            TextView tvNoiDungThu = rowView.findViewById(R.id.tvNoiDungThu);
            TextView tvNgayDong = rowView.findViewById(R.id.tvNgayDong);
            TextView tvHinhThuc = rowView.findViewById(R.id.tvHinhThuc);
            TextView tvThanhTien = rowView.findViewById(R.id.tvThanhTien);
            TextView tvLinkHoaDon = rowView.findViewById(R.id.tvLinkHoaDon);
            TextView btnDetail = rowView.findViewById(R.id.btnDetail);

            if (tvSoHoaDon != null) tvSoHoaDon.setText(formatValue(item.getSoHoaDon()));
            if (tvNoiDungThu != null) tvNoiDungThu.setText(formatValue(item.getNoiDungThu()));
            if (tvNgayDong != null) tvNgayDong.setText(formatDate(item.getNgayDong()));
            if (tvHinhThuc != null) tvHinhThuc.setText(formatValue(item.getTenHinhThucThanhToan()));

            double amount = item.getDaDong() != null ? item.getDaDong() : (item.getTongTien() != null ? item.getTongTien() : 0);
            if (tvThanhTien != null) tvThanhTien.setText(currencyFormatter.format(amount));

            if (tvLinkHoaDon != null) {
                if (!TextUtils.isEmpty(item.getLinkHoaDon())) {
                    tvLinkHoaDon.setText(getString(R.string.label_download_invoice));
                    tvLinkHoaDon.setTextColor(getResources().getColor(R.color.mku_primary_blue, getTheme()));
                    tvLinkHoaDon.setOnClickListener(v -> openInvoiceUrl(item.getLinkHoaDon()));
                } else {
                    tvLinkHoaDon.setText("-");
                    tvLinkHoaDon.setTextColor(getResources().getColor(R.color.mku_text_sub, getTheme()));
                    tvLinkHoaDon.setOnClickListener(null);
                }
            }

            if (btnDetail != null) {
                btnDetail.setOnClickListener(v -> showOrderDetailDialog(item));
            }

            containerOrderRows.addView(rowView);
        }
    }

    private void openInvoiceUrl(String url) {
        if (TextUtils.isEmpty(url)) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở liên kết hóa đơn", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrderDetailDialog(OrderDetailItem item) {
        if (item == null) return;

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_order_detail, null);
        TextView tvDialogDetailInfo = dialogView.findViewById(R.id.tvDialogDetailInfo);
        MaterialButton btnDialogDownloadInvoice = dialogView.findViewById(R.id.btnDialogDownloadInvoice);
        MaterialButton btnDialogClose = dialogView.findViewById(R.id.btnDialogClose);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.label_col_invoice_no)).append(": ").append(formatValue(item.getSoHoaDon())).append("\n");
        sb.append(getString(R.string.label_symbol)).append(": ").append(formatValue(item.getKyHieu())).append("\n");
        sb.append(getString(R.string.label_col_payment_date)).append(": ").append(formatDate(item.getNgayDong())).append("\n");
        sb.append(getString(R.string.label_year_study)).append(" - ").append(getString(R.string.label_term)).append(": ")
                .append(formatValue(item.getNamHoc())).append(" - ").append(formatTermDisplayName(item.getHocKy())).append("\n");
        sb.append(getString(R.string.label_col_payment_content)).append(": ").append(formatValue(item.getNoiDungThu())).append("\n");
        sb.append(getString(R.string.label_col_payment_method)).append(": ").append(formatValue(item.getTenHinhThucThanhToan())).append("\n");
        sb.append(getString(R.string.label_fee_code)).append(" - ").append(getString(R.string.label_fee_type)).append(": ")
                .append(formatValue(item.getMaPhi())).append(" - ").append(formatValue(item.getTenPhi())).append("\n");

        double total = item.getTongTien() != null ? item.getTongTien() : 0;
        double paid = item.getDaDong() != null ? item.getDaDong() : 0;
        sb.append(getString(R.string.label_col_amount)).append(": ").append(currencyFormatter.format(total)).append(" VNĐ\n");
        sb.append(getString(R.string.label_paid_amount)).append(": ").append(currencyFormatter.format(paid)).append(" VNĐ");

        if (tvDialogDetailInfo != null) {
            tvDialogDetailInfo.setText(sb.toString());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (btnDialogDownloadInvoice != null) {
            if (!TextUtils.isEmpty(item.getLinkHoaDon())) {
                btnDialogDownloadInvoice.setVisibility(View.VISIBLE);
                btnDialogDownloadInvoice.setOnClickListener(v -> {
                    dialog.dismiss();
                    openInvoiceUrl(item.getLinkHoaDon());
                });
            } else {
                btnDialogDownloadInvoice.setVisibility(View.GONE);
            }
        }

        if (btnDialogClose != null) {
            btnDialogClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private String formatDate(String isoDateStr) {
        if (TextUtils.isEmpty(isoDateStr)) return "";
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = parseFormat.parse(isoDateStr);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception ignored) {}
        return isoDateStr;
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedStrings();
    }

    private void applyLocalizedStrings() {
        vn.edu.mku.portal.data.local.LanguageManager lm = vn.edu.mku.portal.data.local.LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("FinanceComponent", "OrderDetail", getString(R.string.title_order_detail)));
        }
    }
}
