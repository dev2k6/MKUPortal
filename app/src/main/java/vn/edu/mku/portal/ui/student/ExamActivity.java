/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.student;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.ExamItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class ExamActivity extends BaseStudentActivity {

    private AutoCompleteTextView spinnerYearExam;
    private AutoCompleteTextView spinnerTermExam;
    private LinearLayout containerUpcomingExamRows;
    private LinearLayout containerAllExamRows;

    private String selectedYear = null;
    private String selectedTermId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        setupCommonUi();

        spinnerYearExam = findViewById(R.id.spinnerYearExam);
        spinnerTermExam = findViewById(R.id.spinnerTermExam);
        containerUpcomingExamRows = findViewById(R.id.containerUpcomingExamRows);
        containerAllExamRows = findViewById(R.id.containerAllExamRows);

        fetchYearAndTerm();
    }

    private void fetchYearAndTerm() {
        studentRepository.fetchYearAndTerm(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(YearAndTermResponse result) {
                if (result != null) {
                    if (!TextUtils.isEmpty(result.getCurrentYear())) {
                        selectedYear = result.getCurrentYear();
                    }
                    if (!TextUtils.isEmpty(result.getCurrentTerm())) {
                        selectedTermId = result.getCurrentTerm();
                    }

                    setupYearAndTermSpinners(result);
                    fetchExamsData();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupYearAndTermSpinners(YearAndTermResponse data) {
        if (data == null) return;

        List<String> years = data.getYearStudy() != null ? data.getYearStudy() : new ArrayList<>();
        List<YearAndTermResponse.TermItem> terms = data.getTerms() != null ? data.getTerms() : new ArrayList<>();

        List<String> termNames = new ArrayList<>();
        for (YearAndTermResponse.TermItem t : terms) {
            termNames.add(formatTermDisplayName(t.getTermId(), t.getTermName()));
        }

        android.widget.ArrayAdapter<String> yearAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, years
        );
        android.widget.ArrayAdapter<String> termAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, termNames
        );

        if (spinnerYearExam != null) {
            spinnerYearExam.setAdapter(yearAdapter);
            spinnerYearExam.setText(selectedYear, false);
            spinnerYearExam.setOnItemClickListener((parent, view, position, id) -> {
                selectedYear = years.get(position);
                fetchExamsData();
            });
        }

        if (spinnerTermExam != null) {
            spinnerTermExam.setAdapter(termAdapter);
            int selectedTermIndex = getTermIndex(terms, selectedTermId);
            if (selectedTermIndex >= 0 && selectedTermIndex < termNames.size()) {
                spinnerTermExam.setText(termNames.get(selectedTermIndex), false);
            }
            spinnerTermExam.setOnItemClickListener((parent, view, position, id) -> {
                selectedTermId = terms.get(position).getTermId();
                fetchExamsData();
            });
        }
    }

    private int getTermIndex(List<YearAndTermResponse.TermItem> terms, String termId) {
        if (terms == null) return 0;
        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i).getTermId() != null && terms.get(i).getTermId().equalsIgnoreCase(termId)) {
                return i;
            }
        }
        return 0;
    }

    private void fetchExamsData() {
        showSkeletonRows();
        studentRepository.fetchExams(selectedYear, selectedTermId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<ExamItem> result) {
                SkeletonHelper.stopPulseAnimation(containerUpcomingExamRows);
                SkeletonHelper.stopPulseAnimation(containerAllExamRows);
                populateExamTables(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerUpcomingExamRows);
                SkeletonHelper.stopPulseAnimation(containerAllExamRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerUpcomingExamRows != null) {
            containerUpcomingExamRows.removeAllViews();
            for (int i = 0; i < 3; i++) {
                View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerUpcomingExamRows, false);
                containerUpcomingExamRows.addView(skeletonRow);
            }
            SkeletonHelper.startPulseAnimation(containerUpcomingExamRows);
        }

        if (containerAllExamRows != null) {
            containerAllExamRows.removeAllViews();
            for (int i = 0; i < 5; i++) {
                View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerAllExamRows, false);
                containerAllExamRows.addView(skeletonRow);
            }
            SkeletonHelper.startPulseAnimation(containerAllExamRows);
        }
    }

    private void populateExamTables(List<ExamItem> list) {
        if (containerUpcomingExamRows != null) containerUpcomingExamRows.removeAllViews();
        if (containerAllExamRows != null) containerAllExamRows.removeAllViews();

        if (list == null || list.isEmpty()) {
            addEmptyMessage(containerUpcomingExamRows, "Không có lịch chưa thi.");
            addEmptyMessage(containerAllExamRows, "Không có lịch thi.");
            return;
        }

        List<ExamItem> upcomingList = new ArrayList<>();
        for (ExamItem item : list) {
            if (!item.isQuaHan()) {
                upcomingList.add(item);
            }
        }

        // 1. Render Upcoming Exams
        if (upcomingList.isEmpty()) {
            addEmptyMessage(containerUpcomingExamRows, "Không có lịch chưa thi.");
        } else {
            for (ExamItem item : upcomingList) {
                View rowView = createExamRowView(item, containerUpcomingExamRows);
                if (containerUpcomingExamRows != null) {
                    containerUpcomingExamRows.addView(rowView);
                }
            }
        }

        // 2. Render All Exams
        for (ExamItem item : list) {
            View rowView = createExamRowView(item, containerAllExamRows);
            if (containerAllExamRows != null) {
                containerAllExamRows.addView(rowView);
            }
        }
    }

    private void addEmptyMessage(LinearLayout container, String message) {
        if (container == null) return;
        TextView tvEmpty = new TextView(this);
        tvEmpty.setPadding(32, 24, 32, 24);
        tvEmpty.setText(message);
        tvEmpty.setTextColor(getColor(R.color.mku_text_sub));
        tvEmpty.setGravity(android.view.Gravity.CENTER);
        container.addView(tvEmpty);
    }

    private View createExamRowView(ExamItem item, LinearLayout container) {
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_exam_table_row, container, false);

        TextView tvCourseName = rowView.findViewById(R.id.tvCourseName);
        TextView tvSbd = rowView.findViewById(R.id.tvSbd);
        TextView tvLanThi = rowView.findViewById(R.id.tvLanThi);
        TextView tvNgayThi = rowView.findViewById(R.id.tvNgayThi);
        TextView tvGioThi = rowView.findViewById(R.id.tvGioThi);
        TextView tvPhongThi = rowView.findViewById(R.id.tvPhongThi);
        TextView tvHinhThuc = rowView.findViewById(R.id.tvHinhThuc);
        TextView tvKyThi = rowView.findViewById(R.id.tvKyThi);
        TextView tvGhiChu = rowView.findViewById(R.id.tvGhiChu);
        TextView tvVangThi = rowView.findViewById(R.id.tvVangThi);
        TextView tvLink = rowView.findViewById(R.id.tvLink);

        if (tvCourseName != null) {
            tvCourseName.setText(formatValue(item.getDisplayName()));

            // Text color coding
            if (item.isCamThi()) {
                tvCourseName.setTextColor(Color.parseColor("#D32F2F")); // Red for forbidden
            } else if (!item.isQuaHan()) {
                tvCourseName.setTextColor(Color.parseColor("#0066CC")); // Blue for upcoming
            } else {
                tvCourseName.setTextColor(getColor(R.color.mku_text_header)); // Black for past
            }
        }

        if (tvSbd != null) tvSbd.setText(formatValue(item.getSbd()));
        if (tvLanThi != null) tvLanThi.setText(formatValue(item.getLanThi()));
        if (tvNgayThi != null) tvNgayThi.setText(formatValue(item.getNgayThi()));
        if (tvGioThi != null) tvGioThi.setText(formatValue(item.getGioThi()));
        if (tvPhongThi != null) tvPhongThi.setText(formatValue(item.getPhongThi()));
        if (tvHinhThuc != null) tvHinhThuc.setText(formatValue(item.getHinhThucThi()));
        if (tvKyThi != null) tvKyThi.setText(formatValue(item.getKyThi()));
        if (tvGhiChu != null) tvGhiChu.setText(formatValue(item.getGhiChu()));
        if (tvVangThi != null) tvVangThi.setText(formatValue(item.getVangThi()));

        if (tvLink != null) {
            if (!TextUtils.isEmpty(item.getLink())) {
                tvLink.setText(getString(R.string.label_col_link));
                tvLink.setTextColor(Color.parseColor("#0066CC"));
                tvLink.setOnClickListener(v -> openUrl(item.getLink()));
            } else {
                tvLink.setText("-");
                tvLink.setTextColor(getColor(R.color.mku_text_sub));
                tvLink.setOnClickListener(null);
            }
        }

        rowView.setOnClickListener(v -> showExamDetailDialog(item));

        return rowView;
    }

    private void openUrl(String url) {
        if (TextUtils.isEmpty(url)) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở liên kết", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExamDetailDialog(ExamItem item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exam_detail, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvExamTitle);
        TextView tvDetails = dialogView.findViewById(R.id.tvExamDetails);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseExam);

        if (tvTitle != null) {
            tvTitle.setText(formatValue(item.getCurriculumName()));
        }
        if (tvDetails != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Mã LHP: ").append(formatValue(item.getScheduleStudyUnitId())).append("\n");
            sb.append("Lần thi: ").append(formatValue(item.getLanThi())).append("\n");
            sb.append("Ngày thi: ").append(formatValue(item.getNgayThi())).append("\n");
            sb.append("Giờ thi: ").append(formatValue(item.getGioThi())).append("\n");
            sb.append("Phòng thi: ").append(formatValue(item.getPhongThi())).append("\n");
            sb.append("Cơ sở: ").append(formatValue(item.getDiaDiem())).append("\n");
            sb.append("Kỳ thi: ").append(formatValue(item.getKyThi())).append("\n");
            sb.append("SBD: ").append(formatValue(item.getSbd())).append("\n");
            sb.append("Ghi chú: ").append(formatValue(item.getGhiChu())).append("\n");
            sb.append("Thạng thái: ").append(item.isCamThi() ? "Bị cấm thi" : (item.isQuaHan() ? "Đã qua" : "Sắp tới"));
            tvDetails.setText(sb.toString());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(dpToPx(340), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedStrings();
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("ExamComponent", "Exams", getString(R.string.title_exam)));
        }
    }
}
