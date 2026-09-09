package vn.edu.mku.portal.ui.student;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.MarkCourseItem;
import vn.edu.mku.portal.data.network.model.MarkDetailItem;
import vn.edu.mku.portal.data.network.model.MarkSemesterGroup;
import vn.edu.mku.portal.data.network.model.MarkYearGroup;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.data.network.model.YearAndTermResponse;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class MarksActivity extends BaseStudentActivity {

    private AutoCompleteTextView spinnerProgramMarks;
    private AutoCompleteTextView spinnerYearMarks;
    private AutoCompleteTextView spinnerTermMarks;
    private LinearLayout containerMarksTable;

    private List<StudyProgramHeader> programHeaders;
    private String selectedProgramId = null;

    private List<MarkYearGroup> allYearMarks;
    private String selectedFilterYear = null; // null = Tất cả
    private String selectedFilterTerm = null; // null = Tất cả

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        setupCommonUi();

        spinnerProgramMarks = findViewById(R.id.spinnerProgramMarks);
        spinnerYearMarks = findViewById(R.id.spinnerYearMarks);
        spinnerTermMarks = findViewById(R.id.spinnerTermMarks);
        containerMarksTable = findViewById(R.id.containerMarksTable);

        fetchProgramHeaders();
        fetchYearAndTerm();
    }

    private void fetchYearAndTerm() {
        studentRepository.fetchYearAndTerm(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(YearAndTermResponse result) {
                if (result != null) {
                    setupFilterSpinners(result);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Ignore
            }
        });
    }

    private void setupFilterSpinners(YearAndTermResponse data) {
        String labelAll = getString(R.string.label_all);

        List<String> yearOptions = new ArrayList<>();
        yearOptions.add(labelAll);
        if (data.getYearStudy() != null) {
            yearOptions.addAll(data.getYearStudy());
        }

        List<String> termOptions = new ArrayList<>();
        termOptions.add(labelAll);
        List<YearAndTermResponse.TermItem> terms = data.getTerms() != null ? data.getTerms() : new ArrayList<>();
        for (YearAndTermResponse.TermItem t : terms) {
            termOptions.add(formatTermDisplayName(t.getTermId(), t.getTermName()));
        }

        android.widget.ArrayAdapter<String> yearAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, yearOptions
        );
        android.widget.ArrayAdapter<String> termAdapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, termOptions
        );

        if (spinnerYearMarks != null) {
            spinnerYearMarks.setAdapter(yearAdapter);
            spinnerYearMarks.setText(labelAll, false);
            spinnerYearMarks.setOnItemClickListener((parent, view, position, id) -> {
                selectedFilterYear = position == 0 ? null : yearOptions.get(position);
                applyFilterAndPopulate();
            });
        }

        if (spinnerTermMarks != null) {
            spinnerTermMarks.setAdapter(termAdapter);
            spinnerTermMarks.setText(labelAll, false);
            spinnerTermMarks.setOnItemClickListener((parent, view, position, id) -> {
                selectedFilterTerm = position == 0 ? null : terms.get(position - 1).getTermId();
                applyFilterAndPopulate();
            });
        }
    }

    private void fetchProgramHeaders() {
        studentRepository.fetchStudyProgramHeaders(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudyProgramHeader> result) {
                programHeaders = result;
                if (result != null && !result.isEmpty()) {
                    selectedProgramId = result.get(0).getStudyProgramId();
                    List<String> names = new ArrayList<>();
                    for (StudyProgramHeader h : result) {
                        names.add(formatValue(h.getStudyProgramName()));
                    }

                    android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                            MarksActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    if (spinnerProgramMarks != null) {
                        spinnerProgramMarks.setAdapter(adapter);
                        spinnerProgramMarks.setText(names.get(0), false);

                        spinnerProgramMarks.setOnItemClickListener((parent, view, position, id) -> {
                            if (position >= 0 && position < programHeaders.size()) {
                                selectedProgramId = programHeaders.get(position).getStudyProgramId();
                                fetchMarksData();
                            }
                        });
                    }

                    fetchMarksData();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchMarksData() {
        showSkeletonRows();
        studentRepository.fetchMarks(selectedProgramId, "SV", new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<MarkYearGroup> result) {
                SkeletonHelper.stopPulseAnimation(containerMarksTable);
                allYearMarks = result;
                applyFilterAndPopulate();
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerMarksTable);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void applyFilterAndPopulate() {
        if (allYearMarks == null) {
            populateMarksTable(new ArrayList<>());
            return;
        }

        if (selectedFilterYear == null && selectedFilterTerm == null) {
            populateMarksTable(allYearMarks);
            return;
        }

        List<MarkYearGroup> filteredList = new ArrayList<>();
        for (MarkYearGroup yg : allYearMarks) {
            if (selectedFilterYear != null && !selectedFilterYear.equalsIgnoreCase(yg.getNamHoc())) {
                continue;
            }

            if (selectedFilterTerm == null) {
                filteredList.add(yg);
            } else {
                boolean hasSem = false;
                if (yg.getDanhSachDiem() != null) {
                    for (MarkSemesterGroup sg : yg.getDanhSachDiem()) {
                        if (selectedFilterTerm.equalsIgnoreCase(sg.getHocKy())) {
                            hasSem = true;
                            break;
                        }
                    }
                }
                if (hasSem) {
                    filteredList.add(yg);
                }
            }
        }

        populateMarksTable(filteredList);
    }

    private void showSkeletonRows() {
        if (containerMarksTable == null) return;
        containerMarksTable.removeAllViews();
        for (int i = 0; i < 6; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerMarksTable, false);
            containerMarksTable.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerMarksTable);
    }

    private void populateMarksTable(List<MarkYearGroup> yearGroups) {
        if (containerMarksTable == null) return;
        containerMarksTable.removeAllViews();

        if (yearGroups == null || yearGroups.isEmpty()) return;

        int sttCounter = 1;

        for (MarkYearGroup yearGroup : yearGroups) {
            // Add Year Header Banner (Pink/Red Banner)
            TextView tvYearBanner = new TextView(this);
            tvYearBanner.setText(getString(R.string.label_academic_year_prefix, formatValue(yearGroup.getNamHoc())));
            tvYearBanner.setTextColor(Color.BLACK);
            tvYearBanner.setTextSize(14);
            tvYearBanner.setTypeface(null, Typeface.BOLD);
            tvYearBanner.setBackgroundColor(Color.parseColor("#FFCDD2"));
            tvYearBanner.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
            containerMarksTable.addView(tvYearBanner);

            if (yearGroup.getDanhSachDiem() != null) {
                for (MarkSemesterGroup semGroup : yearGroup.getDanhSachDiem()) {
                    if (selectedFilterTerm != null && !selectedFilterTerm.equalsIgnoreCase(semGroup.getHocKy())) {
                        continue;
                    }

                    // Add Semester Header Banner (Green Banner)
                    TextView tvSemBanner = new TextView(this);
                    tvSemBanner.setText(getString(R.string.label_term_prefix, formatValue(semGroup.getHocKy())));
                    tvSemBanner.setTextColor(Color.BLACK);
                    tvSemBanner.setTextSize(14);
                    tvSemBanner.setTypeface(null, Typeface.BOLD);
                    tvSemBanner.setBackgroundColor(Color.parseColor("#C8E6C9"));
                    tvSemBanner.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
                    containerMarksTable.addView(tvSemBanner);

                    if (semGroup.getDanhSachDiemHK() != null && !semGroup.getDanhSachDiemHK().isEmpty()) {
                        MarkCourseItem sampleItem = semGroup.getDanhSachDiemHK().get(0);

                        for (MarkCourseItem course : semGroup.getDanhSachDiemHK()) {
                            View rowView = LayoutInflater.from(this).inflate(R.layout.item_mark_course_row, containerMarksTable, false);
                            TextView tvStt = rowView.findViewById(R.id.tvStt);
                            TextView tvCode = rowView.findViewById(R.id.tvCourseCode);
                            TextView tvTitle = rowView.findViewById(R.id.tvCourseTitle);
                            TextView tvCredits = rowView.findViewById(R.id.tvCredits);
                            TextView tvGrade10 = rowView.findViewById(R.id.tvGrade10);
                            TextView tvGrade4 = rowView.findViewById(R.id.tvGrade4);
                            TextView tvGradeChar = rowView.findViewById(R.id.tvGradeChar);
                            ImageView imgPass = rowView.findViewById(R.id.imgPass);
                            ImageView btnExpand = rowView.findViewById(R.id.btnExpandDetail);
                            LinearLayout containerDetail = rowView.findViewById(R.id.containerDetailBreakdown);

                            if (tvStt != null) tvStt.setText(String.valueOf(sttCounter++));
                            if (tvCode != null) tvCode.setText(formatValue(course.getCurriculumId()));
                            renderHtmlText(tvTitle, course.getCurriculumName());
                            if (tvCredits != null) tvCredits.setText(formatValue(course.getCredits()));
                            if (tvGrade10 != null) tvGrade10.setText(formatValue(course.getDiemTK10()));
                            if (tvGrade4 != null) tvGrade4.setText(formatValue(course.getDiemTK4()));
                            if (tvGradeChar != null) tvGradeChar.setText(formatValue(course.getDiemTKChu()));

                            if (imgPass != null) {
                                if ("1".equals(course.getIsPass())) {
                                    imgPass.setImageResource(R.drawable.ic_check_circle);
                                    imgPass.setVisibility(View.VISIBLE);
                                } else {
                                    imgPass.setVisibility(View.INVISIBLE);
                                }
                            }

                            if (btnExpand != null && containerDetail != null) {
                                btnExpand.setImageResource(R.drawable.ic_chevron_down);
                                btnExpand.setOnClickListener(v -> {
                                    if (containerDetail.getVisibility() == View.VISIBLE) {
                                        containerDetail.setVisibility(View.GONE);
                                        btnExpand.setImageResource(R.drawable.ic_chevron_down);
                                    } else {
                                        containerDetail.setVisibility(View.VISIBLE);
                                        btnExpand.setImageResource(R.drawable.ic_chevron_up);
                                        fetchAndShowMarkDetail(containerDetail, course.getScheduleStudyUnitId());
                                    }
                                });
                            }

                            containerMarksTable.addView(rowView);
                        }

                        // Semester Summary Yellow Banner
                        LinearLayout summaryBanner = new LinearLayout(this);
                        summaryBanner.setOrientation(LinearLayout.VERTICAL);
                        summaryBanner.setBackgroundColor(Color.parseColor("#FFE082"));
                        summaryBanner.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));

                        TextView tvSummaryText = new TextView(this);
                        tvSummaryText.setTextColor(Color.BLACK);
                        tvSummaryText.setTextSize(13);
                        tvSummaryText.setTypeface(null, Typeface.BOLD);
                        String summaryContent = getString(
                                R.string.summary_marks_banner,
                                formatValue(sampleItem.getDatHk()),
                                formatValue(sampleItem.getTbHk()),
                                formatValue(sampleItem.getTbHk4()),
                                formatValue(sampleItem.getDatTlHk()),
                                formatValue(sampleItem.getTongTcDkHk()),
                                formatValue(sampleItem.getTbTlHk()),
                                formatValue(sampleItem.getTbTlHk4()),
                                formatValue(sampleItem.getDiemRenLuyenHk())
                        );
                        tvSummaryText.setText(summaryContent);
                        summaryBanner.addView(tvSummaryText);

                        containerMarksTable.addView(summaryBanner);
                    }
                }
            }
        }
    }

    private void fetchAndShowMarkDetail(LinearLayout container, String scheduleStudyUnitId) {
        if (container == null || TextUtils.isEmpty(scheduleStudyUnitId)) return;
        container.removeAllViews();

        TextView tvLoading = new TextView(this);
        tvLoading.setText(getString(R.string.text_loading_mark_details));
        tvLoading.setTextColor(getColor(R.color.mku_text_sub));
        container.addView(tvLoading);

        studentRepository.fetchMarkDetail(scheduleStudyUnitId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<MarkDetailItem> result) {
                container.removeAllViews();
                if (result != null && !result.isEmpty()) {
                    for (MarkDetailItem item : result) {
                        TextView tvRow = new TextView(MarksActivity.this);
                        renderHtmlText(tvRow, formatValue(item.getAssignmentName()) + " (" + formatValue(item.getAssignmentDetail()) + "): " + formatValue(item.getFirstMark()));
                        tvRow.setTextColor(getColor(R.color.mku_text_header));
                        tvRow.setTextSize(13);
                        tvRow.setPadding(0, dpToPx(4), 0, dpToPx(4));
                        container.addView(tvRow);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                container.removeAllViews();
            }
        });
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedStrings();
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("AcademicResultComponent", "AcademicResult", getString(R.string.title_marks)));
        }
    }
}