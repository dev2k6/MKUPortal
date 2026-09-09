/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.DecisionItem;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class DecisionsActivity extends BaseStudentActivity {

    private LinearLayout containerDecisionRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decisions);

        setupCommonUi();

        containerDecisionRows = findViewById(R.id.containerDecisionRows);

        fetchDecisionsData();
    }

    private void fetchDecisionsData() {
        showSkeletonRows();
        studentRepository.fetchDecisions(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<DecisionItem> result) {
                SkeletonHelper.stopPulseAnimation(containerDecisionRows);
                populateDecisionTable(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerDecisionRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerDecisionRows == null) return;
        containerDecisionRows.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerDecisionRows, false);
            containerDecisionRows.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerDecisionRows);
    }

    private void populateDecisionTable(List<DecisionItem> list) {
        if (containerDecisionRows == null) return;
        containerDecisionRows.removeAllViews();

        if (list == null || list.isEmpty()) return;

        for (DecisionItem item : list) {
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_decision_row, containerDecisionRows, false);
            TextView tvYear = rowView.findViewById(R.id.tvYear);
            TextView tvTerm = rowView.findViewById(R.id.tvTerm);
            TextView tvNumber = rowView.findViewById(R.id.tvNumber);
            TextView tvType = rowView.findViewById(R.id.tvType);
            TextView tvContent = rowView.findViewById(R.id.tvContent);
            TextView tvSignDate = rowView.findViewById(R.id.tvSignDate);

            if (tvYear != null) tvYear.setText(formatValue(item.getYearStudy()));
            if (tvTerm != null) tvTerm.setText(formatValue(item.getTermId()));
            if (tvNumber != null) tvNumber.setText(String.valueOf(item.getDecisionTypeId()));
            if (tvType != null) tvType.setText(formatValue(item.getDecisionName()));
            if (tvContent != null) tvContent.setText(formatValue(item.getFullText()));
            if (tvSignDate != null) tvSignDate.setText(formatValue(item.getSignDate()));

            containerDecisionRows.addView(rowView);
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
            tvHeaderTitle.setText(lm.getString("DecisionComponent", "Decisions", getString(R.string.title_decisions)));
        }
    }
}