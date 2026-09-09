/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.student;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.DrawingScheduleItem;
import vn.edu.mku.portal.data.network.model.DrawingScheduleResponse;
import vn.edu.mku.portal.data.network.model.PeriodScheduleItem;
import vn.edu.mku.portal.data.network.model.PeriodScheduleResponse;
import vn.edu.mku.portal.data.network.model.WeekItem;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class SchedulesActivity extends BaseStudentActivity {

    private TabLayout tabLayoutSchedules;
    private LinearLayout containerTabWeek;
    private LinearLayout containerTabPeriod;

    // Tab 1 Views
    private AutoCompleteTextView spinnerYearWeek;
    private AutoCompleteTextView spinnerTermWeek;
    private AutoCompleteTextView spinnerWeekSchedule;
    private MaterialButton btnPrevWeek;
    private MaterialButton btnCurrentWeek;
    private MaterialButton btnNextWeek;
    private LinearLayout containerWeekGrid;

    // Tab 2 Views
    private AutoCompleteTextView spinnerYearPeriod;
    private AutoCompleteTextView spinnerTermPeriod;
    private TextView tvSummaryPeriod;
    private LinearLayout containerPeriodRows;

    private List<WeekItem> weekList;
    private int selectedWeekIndex = 0;

    private String selectedYear = null;
    private String selectedTermId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        setupCommonUi();

        tabLayoutSchedules = findViewById(R.id.tabLayoutSchedules);
        containerTabWeek = findViewById(R.id.containerTabWeek);
        containerTabPeriod = findViewById(R.id.containerTabPeriod);

        spinnerYearWeek = findViewById(R.id.spinnerYearWeek);
        spinnerTermWeek = findViewById(R.id.spinnerTermWeek);
        spinnerWeekSchedule = findViewById(R.id.spinnerWeekSchedule);
        btnPrevWeek = findViewById(R.id.btnPrevWeek);
        btnCurrentWeek = findViewById(R.id.btnCurrentWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);
        containerWeekGrid = findViewById(R.id.containerWeekGrid);

        spinnerYearPeriod = findViewById(R.id.spinnerYearPeriod);
        spinnerTermPeriod = findViewById(R.id.spinnerTermPeriod);
        tvSummaryPeriod = findViewById(R.id.tvSummaryPeriod);
        containerPeriodRows = findViewById(R.id.containerPeriodRows);

        setupTabListeners();
        fetchYearAndTerm();
    }

    private void setupTabListeners() {
        if (tabLayoutSchedules != null) {
            tabLayoutSchedules.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        containerTabWeek.setVisibility(View.VISIBLE);
                        containerTabPeriod.setVisibility(View.GONE);
                        fetchWeekSchedules();
                    } else {
                        containerTabWeek.setVisibility(View.GONE);
                        containerTabPeriod.setVisibility(View.VISIBLE);
                        fetchPeriodSchedules();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        if (btnPrevWeek != null) {
            btnPrevWeek.setOnClickListener(v -> {
                if (weekList != null && selectedWeekIndex > 0) {
                    selectedWeekIndex--;
                    updateWeekSpinnerSelection();
                }
            });
        }

        if (btnNextWeek != null) {
            btnNextWeek.setOnClickListener(v -> {
                if (weekList != null && selectedWeekIndex < weekList.size() - 1) {
                    selectedWeekIndex++;
                    updateWeekSpinnerSelection();
                }
            });
        }

        if (btnCurrentWeek != null) {
            btnCurrentWeek.setOnClickListener(v -> {
                if (weekList != null) {
                    selectedWeekIndex = 0;
                    updateWeekSpinnerSelection();
                }
            });
        }
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
                    fetchWeekSchedules();
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

        if (spinnerYearWeek != null) {
            spinnerYearWeek.setAdapter(yearAdapter);
            spinnerYearWeek.setText(selectedYear, false);
            spinnerYearWeek.setOnItemClickListener((parent, view, position, id) -> {
                selectedYear = years.get(position);
                fetchWeekSchedules();
            });
        }

        if (spinnerTermWeek != null) {
            spinnerTermWeek.setAdapter(termAdapter);
            int selectedTermIndex = getTermIndex(terms, selectedTermId);
            if (selectedTermIndex >= 0 && selectedTermIndex < termNames.size()) {
                spinnerTermWeek.setText(termNames.get(selectedTermIndex), false);
            }
            spinnerTermWeek.setOnItemClickListener((parent, view, position, id) -> {
                selectedTermId = terms.get(position).getTermId();
                fetchWeekSchedules();
            });
        }

        if (spinnerYearPeriod != null) {
            spinnerYearPeriod.setAdapter(yearAdapter);
            spinnerYearPeriod.setText(selectedYear, false);
            spinnerYearPeriod.setOnItemClickListener((parent, view, position, id) -> {
                selectedYear = years.get(position);
                fetchPeriodSchedules();
            });
        }

        if (spinnerTermPeriod != null) {
            spinnerTermPeriod.setAdapter(termAdapter);
            int selectedTermIndex = getTermIndex(terms, selectedTermId);
            if (selectedTermIndex >= 0 && selectedTermIndex < termNames.size()) {
                spinnerTermPeriod.setText(termNames.get(selectedTermIndex), false);
            }
            spinnerTermPeriod.setOnItemClickListener((parent, view, position, id) -> {
                selectedTermId = terms.get(position).getTermId();
                fetchPeriodSchedules();
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

    private void fetchWeekSchedules() {
        studentRepository.fetchWeekSchedule(selectedYear, selectedTermId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<WeekItem> result) {
                weekList = result;
                if (result != null && !result.isEmpty()) {
                    List<String> weekDisplayList = new ArrayList<>();
                    for (WeekItem w : result) {
                        weekDisplayList.add(w.getDisplayRange());
                    }

                    android.widget.ArrayAdapter<String> weekAdapter = new android.widget.ArrayAdapter<>(
                            SchedulesActivity.this, android.R.layout.simple_dropdown_item_1line, weekDisplayList
                    );
                    if (spinnerWeekSchedule != null) {
                        spinnerWeekSchedule.setAdapter(weekAdapter);
                        selectedWeekIndex = 0;
                        spinnerWeekSchedule.setText(weekDisplayList.get(0), false);

                        spinnerWeekSchedule.setOnItemClickListener((parent, view, position, id) -> {
                            selectedWeekIndex = position;
                            fetchDrawingSchedules(weekList.get(position).getWeek());
                        });
                    }

                    fetchDrawingSchedules(result.get(0).getWeek());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateWeekSpinnerSelection() {
        if (weekList == null || selectedWeekIndex < 0 || selectedWeekIndex >= weekList.size()) return;
        WeekItem item = weekList.get(selectedWeekIndex);
        if (spinnerWeekSchedule != null) {
            spinnerWeekSchedule.setText(item.getDisplayRange(), false);
        }
        fetchDrawingSchedules(item.getWeek());
    }

    private void fetchDrawingSchedules(int weekNumber) {
        showWeekGridSkeleton();
        studentRepository.fetchDrawingSchedules(selectedYear, selectedTermId, weekNumber, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(DrawingScheduleResponse result) {
                SkeletonHelper.stopPulseAnimation(containerWeekGrid);
                if (result != null && result.getResultDataSchedule() != null) {
                    populateWeekGrid(result.getResultDataSchedule());
                } else {
                    populateWeekGrid(new ArrayList<>());
                }
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerWeekGrid);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showWeekGridSkeleton() {
        if (containerWeekGrid == null) return;
        containerWeekGrid.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerWeekGrid, false);
            containerWeekGrid.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerWeekGrid);
    }

    private void populateWeekGrid(List<DrawingScheduleItem> schedules) {
        if (containerWeekGrid == null) return;
        containerWeekGrid.removeAllViews();

        boolean isEn = LanguageManager.LANG_EN.equalsIgnoreCase(LanguageManager.getInstance().getCurrentLanguage());
        String[] daysHeader = isEn ?
                new String[]{getString(R.string.label_period), "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"} :
                new String[]{getString(R.string.label_period), "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};

        int cellWidth = dpToPx(130);
        int periodColWidth = dpToPx(50);
        int cellHeight = dpToPx(60);

        // Header Row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setBackgroundColor(getColor(R.color.mku_primary_dark));

        for (int i = 0; i < daysHeader.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(daysHeader[i]);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(13);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    i == 0 ? periodColWidth : cellWidth,
                    dpToPx(44)
            );
            tv.setLayoutParams(lp);
            headerRow.addView(tv);
        }
        containerWeekGrid.addView(headerRow);

        // 16 Period Rows
        for (int period = 1; period <= 16; period++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Col 0: Period Number
            TextView tvPeriod = new TextView(this);
            tvPeriod.setText(String.valueOf(period));
            tvPeriod.setTextColor(getColor(R.color.mku_text_header));
            tvPeriod.setTextSize(13);
            tvPeriod.setGravity(Gravity.CENTER);
            tvPeriod.setBackgroundResource(R.drawable.bg_badge_blue);
            tvPeriod.setLayoutParams(new LinearLayout.LayoutParams(periodColWidth, cellHeight));
            row.addView(tvPeriod);

            // Cols 1 to 7: Days 1 to 7
            for (int day = 1; day <= 7; day++) {
                DrawingScheduleItem matchItem = findScheduleItem(schedules, day, period);

                TextView cell = new TextView(this);
                cell.setLayoutParams(new LinearLayout.LayoutParams(cellWidth, cellHeight));
                cell.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
                cell.setTextSize(11);

                if (matchItem != null && !TextUtils.isEmpty(matchItem.getTkhHienThi())) {
                    cell.setText(Html.fromHtml(matchItem.getTkhHienThi(), Html.FROM_HTML_MODE_LEGACY));
                    cell.setBackgroundColor(Color.parseColor("#B0E0E6"));
                } else {
                    cell.setBackgroundColor(getColor(R.color.mku_card_bg));
                }

                row.addView(cell);
            }

            containerWeekGrid.addView(row);
        }
    }

    private DrawingScheduleItem findScheduleItem(List<DrawingScheduleItem> schedules, int dayOfWeek, int period) {
        if (schedules == null) return null;
        for (DrawingScheduleItem item : schedules) {
            if (item.getDayOfWeek() != null && item.getDayOfWeek() == dayOfWeek) {
                if (item.getPeriodId() != null && item.getNumberOfPeriods() != null) {
                    int start = item.getPeriodId();
                    int end = start + item.getNumberOfPeriods() - 1;
                    if (period >= start && period <= end) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private void fetchPeriodSchedules() {
        studentRepository.fetchPeriodSchedules(selectedYear, selectedTermId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(PeriodScheduleResponse result) {
                if (tvSummaryPeriod != null) {
                    tvSummaryPeriod.setText(getString(R.string.summary_year_term, selectedYear, selectedTermId));
                }
                if (result != null && result.getResult() != null) {
                    populatePeriodTable(result.getResult());
                } else {
                    populatePeriodTable(new ArrayList<>());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void populatePeriodTable(List<PeriodScheduleItem> list) {
        if (containerPeriodRows == null) return;
        containerPeriodRows.removeAllViews();

        if (list == null || list.isEmpty()) return;

        int sttCounter = 1;
        for (PeriodScheduleItem item : list) {
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_period_schedule_row, containerPeriodRows, false);
            TextView tvStt = rowView.findViewById(R.id.tvStt);
            TextView tvLhpName = rowView.findViewById(R.id.tvLhpName);
            TextView tvRoom = rowView.findViewById(R.id.tvRoom);
            TextView tvWeeks = rowView.findViewById(R.id.tvWeeks);
            TextView tvCampus = rowView.findViewById(R.id.tvCampus);
            TextView tvAddress = rowView.findViewById(R.id.tvAddress);

            if (tvStt != null) tvStt.setText(String.valueOf(sttCounter++));
            if (tvLhpName != null) {
                String nameStr = !TextUtils.isEmpty(item.getTkbHienThi1()) ? item.getTkbHienThi1() : item.getTenHP();
                tvLhpName.setText(Html.fromHtml(formatValue(nameStr), Html.FROM_HTML_MODE_LEGACY));
            }
            if (tvRoom != null) tvRoom.setText(formatValue(item.getPhong()));
            if (tvWeeks != null) tvWeeks.setText(formatValue(item.getTuanHoc()));
            if (tvCampus != null) tvCampus.setText(formatValue(item.getCampusName()));
            if (tvAddress != null) tvAddress.setText(formatValue(item.getCampusAddress()));

            containerPeriodRows.addView(rowView);
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
            tvHeaderTitle.setText(lm.getString("ScheduleComponent", "Schedules", getString(R.string.title_schedules)));
        }
    }
}