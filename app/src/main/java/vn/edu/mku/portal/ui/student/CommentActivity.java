package vn.edu.mku.portal.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.CommentItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class CommentActivity extends BaseStudentActivity {

    private AutoCompleteTextView spinnerYearComment;
    private AutoCompleteTextView spinnerTermComment;
    private LinearLayout containerCommentRows;

    private String selectedYear = null;
    private String selectedTermId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        setupCommonUi();

        spinnerYearComment = findViewById(R.id.spinnerYearComment);
        spinnerTermComment = findViewById(R.id.spinnerTermComment);
        containerCommentRows = findViewById(R.id.containerCommentRows);

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
                    fetchCommentsData();
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

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, termNames);

        if (spinnerYearComment != null) {
            spinnerYearComment.setAdapter(yearAdapter);
            spinnerYearComment.setText(selectedYear, false);
            spinnerYearComment.setOnItemClickListener((parent, view, position, id) -> {
                selectedYear = years.get(position);
                fetchCommentsData();
            });
        }

        if (spinnerTermComment != null) {
            spinnerTermComment.setAdapter(termAdapter);
            int selectedTermIndex = getTermIndex(terms, selectedTermId);
            if (selectedTermIndex >= 0 && selectedTermIndex < termNames.size()) {
                spinnerTermComment.setText(termNames.get(selectedTermIndex), false);
            }
            spinnerTermComment.setOnItemClickListener((parent, view, position, id) -> {
                selectedTermId = terms.get(position).getTermId();
                fetchCommentsData();
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

    private void fetchCommentsData() {
        showSkeletonRows();
        studentRepository.fetchComments(selectedYear, selectedTermId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<CommentItem> result) {
                SkeletonHelper.stopPulseAnimation(containerCommentRows);
                populateCommentsTable(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerCommentRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerCommentRows == null) return;
        containerCommentRows.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerCommentRows, false);
            containerCommentRows.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerCommentRows);
    }

    private void populateCommentsTable(List<CommentItem> list) {
        if (containerCommentRows == null) return;
        containerCommentRows.removeAllViews();

        if (list == null || list.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setPadding(32, 24, 32, 24);
            tvEmpty.setText(R.string.text_no_data);
            tvEmpty.setTextColor(getColor(R.color.mku_text_header));
            tvEmpty.setTextSize(14f);
            containerCommentRows.addView(tvEmpty);
            return;
        }

        int index = 1;
        for (CommentItem item : list) {
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_comment_row, containerCommentRows, false);

            TextView tvStt = rowView.findViewById(R.id.tvStt);
            TextView tvMaHocPhan = rowView.findViewById(R.id.tvMaHocPhan);
            TextView tvTenHocPhan = rowView.findViewById(R.id.tvTenHocPhan);
            TextView tvStc = rowView.findViewById(R.id.tvStc);
            TextView tvThongTin = rowView.findViewById(R.id.tvThongTin);
            TextView tvGiangVien = rowView.findViewById(R.id.tvGiangVien);
            TextView tvDanhSach = rowView.findViewById(R.id.tvDanhSach);
            TextView tvThaoLuan = rowView.findViewById(R.id.tvThaoLuan);

            if (tvStt != null) tvStt.setText(String.valueOf(index++));
            if (tvMaHocPhan != null) tvMaHocPhan.setText(formatValue(item.getMaHocPhan()));
            if (tvTenHocPhan != null) tvTenHocPhan.setText(formatValue(item.getTenHocPhan()));
            if (tvStc != null) tvStc.setText(item.getStc() != null ? String.valueOf(item.getStc()) : "-");
            if (tvThongTin != null) tvThongTin.setText(formatValue(item.getThongTin()));
            if (tvGiangVien != null) tvGiangVien.setText(formatValue(item.getGiangVien()));
            if (tvDanhSach != null) tvDanhSach.setText(formatValue(item.getDanhSach()));
            if (tvThaoLuan != null) tvThaoLuan.setText(formatValue(item.getThaoLuan()));

            containerCommentRows.addView(rowView);
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
            tvHeaderTitle.setText(lm.getString("CommentComponent", "Comment", getString(R.string.title_discussion_header)));
        }
    }
}
