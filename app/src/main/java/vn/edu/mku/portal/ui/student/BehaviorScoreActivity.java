package vn.edu.mku.portal.ui.student;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.BehaviorScoreItem;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class BehaviorScoreActivity extends BaseStudentActivity {

    private LinearLayout containerBehaviorRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_score);

        setupCommonUi();

        containerBehaviorRows = findViewById(R.id.containerBehaviorRows);

        fetchBehaviorScoresData();
    }

    private void fetchBehaviorScoresData() {
        showSkeletonRows();
        studentRepository.fetchBehaviorScores(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<BehaviorScoreItem> result) {
                SkeletonHelper.stopPulseAnimation(containerBehaviorRows);
                populateBehaviorTable(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerBehaviorRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerBehaviorRows == null) return;
        containerBehaviorRows.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerBehaviorRows, false);
            containerBehaviorRows.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerBehaviorRows);
    }

    private void populateBehaviorTable(List<BehaviorScoreItem> list) {
        if (containerBehaviorRows == null) return;
        containerBehaviorRows.removeAllViews();

        if (list == null || list.isEmpty()) return;

        // Group items by YearStudy
        Map<String, List<BehaviorScoreItem>> yearMap = new LinkedHashMap<>();
        for (BehaviorScoreItem item : list) {
            String year = !TextUtils.isEmpty(item.getYearStudy()) ? item.getYearStudy() : "";
            if (!yearMap.containsKey(year)) {
                yearMap.put(year, new ArrayList<>());
            }
            List<BehaviorScoreItem> yearList = yearMap.get(year);
            if (yearList != null) {
                yearList.add(item);
            }
        }

        int sttCounter = 1;
        BehaviorScoreItem lastSampleItem = null;

        for (Map.Entry<String, List<BehaviorScoreItem>> entry : yearMap.entrySet()) {
            String year = entry.getKey();
            List<BehaviorScoreItem> itemsInYear = entry.getValue();
            if (itemsInYear.isEmpty()) continue;

            BehaviorScoreItem sampleInYear = itemsInYear.get(0);
            lastSampleItem = sampleInYear;

            // Add Light Blue Year Banner
            TextView tvYearBanner = new TextView(this);
            String tongDiemStr = sampleInYear.getTongDiem() != null ? String.valueOf(sampleInYear.getTongDiem()) : "";
            String xepLoaiStr = formatValue(sampleInYear.getXepLoai());
            tvYearBanner.setText(getString(R.string.label_year_behavior_banner, year, tongDiemStr, xepLoaiStr));
            tvYearBanner.setTextColor(Color.BLACK);
            tvYearBanner.setTextSize(13);
            tvYearBanner.setLineSpacing(dpToPx(2), 1.0f);
            tvYearBanner.setBackgroundColor(Color.parseColor("#90CAF9"));
            tvYearBanner.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            containerBehaviorRows.addView(tvYearBanner);

            // Add Semester Rows
            for (BehaviorScoreItem item : itemsInYear) {
                View rowView = LayoutInflater.from(this).inflate(R.layout.item_behavior_score_row, containerBehaviorRows, false);
                TextView tvStt = rowView.findViewById(R.id.tvStt);
                TextView tvTerm = rowView.findViewById(R.id.tvTerm);
                TextView tvScore = rowView.findViewById(R.id.tvScore);
                TextView tvRank = rowView.findViewById(R.id.tvRank);

                if (tvStt != null) tvStt.setText(String.valueOf(sttCounter++));
                if (tvTerm != null) tvTerm.setText(formatValue(item.getTermId()));
                if (tvScore != null) tvScore.setText(String.valueOf((int) item.getLastScore()));
                if (tvRank != null) tvRank.setText(formatValue(item.getBehaviorScoreRank()));

                containerBehaviorRows.addView(rowView);
            }
        }

        // Add Gold/Yellow Overall Summary Banner at Bottom
        if (lastSampleItem != null) {
            String tongDiemTkStr = lastSampleItem.getTongDiemTK() != null ? String.valueOf(lastSampleItem.getTongDiemTK()) : "";
            String xepLoaiTkStr = formatValue(lastSampleItem.getXepLoaiTK());

            TextView tvOverallBanner = new TextView(this);
            tvOverallBanner.setText(getString(R.string.label_overall_behavior_banner, tongDiemTkStr, xepLoaiTkStr));
            tvOverallBanner.setTextColor(Color.BLACK);
            tvOverallBanner.setTextSize(13);
            tvOverallBanner.setTypeface(null, Typeface.BOLD);
            tvOverallBanner.setLineSpacing(dpToPx(2), 1.0f);
            tvOverallBanner.setBackgroundColor(Color.parseColor("#FFE082"));
            tvOverallBanner.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            containerBehaviorRows.addView(tvOverallBanner);
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
            tvHeaderTitle.setText(lm.getString("BehaviorComponent", "BehaviorScore", getString(R.string.title_behavior_score_student)));
        }
    }
}