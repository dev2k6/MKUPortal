package vn.edu.mku.portal.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.CurriculumItem;
import vn.edu.mku.portal.data.network.model.StudyProgramDetailResponse;
import vn.edu.mku.portal.data.network.model.StudyProgramHeader;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class StudyProgramsActivity extends BaseStudentActivity {

    private AutoCompleteTextView spinnerStudyProgram;
    private LinearLayout containerCurriculumTable;

    private List<StudyProgramHeader> programHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_programs);

        setupCommonUi();

        spinnerStudyProgram = findViewById(R.id.spinnerStudyProgram);
        containerCurriculumTable = findViewById(R.id.containerCurriculumTable);

        fetchProgramHeaders();
    }

    private void fetchProgramHeaders() {
        studentRepository.fetchStudyProgramHeaders(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudyProgramHeader> result) {
                programHeaders = result;
                if (result != null && !result.isEmpty()) {
                    List<String> names = new ArrayList<>();
                    for (StudyProgramHeader h : result) {
                        names.add(formatValue(h.getStudyProgramName()));
                    }

                    android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                            StudyProgramsActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );
                    if (spinnerStudyProgram != null) {
                        spinnerStudyProgram.setAdapter(adapter);
                        spinnerStudyProgram.setText(names.get(0), false);

                        spinnerStudyProgram.setOnItemClickListener((parent, view, position, id) -> {
                            if (position >= 0 && position < programHeaders.size()) {
                                fetchProgramDetail(programHeaders.get(position).getStudyProgramId());
                            }
                        });
                    }

                    // Fetch detail for first program
                    fetchProgramDetail(result.get(0).getStudyProgramId());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void fetchProgramDetail(String programId) {
        if (TextUtils.isEmpty(programId)) return;
        showSkeletonRows();
        studentRepository.fetchStudyProgramDetail(programId, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(StudyProgramDetailResponse result) {
                SkeletonHelper.stopPulseAnimation(containerCurriculumTable);
                if (result != null && result.getTbStudyPrograms() != null) {
                    populateCurriculumTable(result.getTbStudyPrograms());
                }
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerCurriculumTable);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerCurriculumTable == null) return;
        containerCurriculumTable.removeAllViews();
        for (int i = 0; i < 6; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerCurriculumTable, false);
            containerCurriculumTable.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerCurriculumTable);
    }

    private void populateCurriculumTable(List<CurriculumItem> items) {
        if (containerCurriculumTable == null) return;
        containerCurriculumTable.removeAllViews();

        if (items == null || items.isEmpty()) return;

        // Group by Semester (HocKy) -> Category (BatBuoc)
        Map<String, Map<String, List<CurriculumItem>>> groupedMap = new LinkedHashMap<>();

        for (CurriculumItem item : items) {
            String sem = !TextUtils.isEmpty(item.getHocKy()) ? item.getHocKy() : "Khác";
            String cat = !TextUtils.isEmpty(item.getBatBuoc()) ? item.getBatBuoc() : "Tự chọn";

            if (!groupedMap.containsKey(sem)) {
                groupedMap.put(sem, new LinkedHashMap<>());
            }
            Map<String, List<CurriculumItem>> catMap = groupedMap.get(sem);
            if (catMap != null) {
                if (!catMap.containsKey(cat)) {
                    catMap.put(cat, new ArrayList<>());
                }
                List<CurriculumItem> courseList = catMap.get(cat);
                if (courseList != null) {
                    courseList.add(item);
                }
            }
        }

        int sttCounter = 1;

        for (Map.Entry<String, Map<String, List<CurriculumItem>>> semEntry : groupedMap.entrySet()) {
            String semName = semEntry.getKey();

            // Add Semester Header Row
            View semView = LayoutInflater.from(this).inflate(R.layout.item_curriculum_semester_header, containerCurriculumTable, false);
            TextView tvSem = semView.findViewById(R.id.tvSemesterName);
            if (tvSem != null) {
                tvSem.setText(formatTermDisplayName(semName, semName));
            }
            containerCurriculumTable.addView(semView);

            Map<String, List<CurriculumItem>> catMap = semEntry.getValue();

            for (Map.Entry<String, List<CurriculumItem>> catEntry : catMap.entrySet()) {
                String catName = catEntry.getKey();

                // Add Category Header Row
                View catView = LayoutInflater.from(this).inflate(R.layout.item_curriculum_category_header, containerCurriculumTable, false);
                TextView tvCat = catView.findViewById(R.id.tvCategoryName);
                if (tvCat != null) {
                    if ("Bắt Buộc".equalsIgnoreCase(catName) || "Bắt buộc".equalsIgnoreCase(catName) || "Compulsory".equalsIgnoreCase(catName)) {
                        tvCat.setText(getLang("Compulsory", getString(R.string.label_compulsory)));
                    } else {
                        tvCat.setText(getLang("ElectiveCourses", getString(R.string.label_elective)));
                    }
                }
                containerCurriculumTable.addView(catCatView(catView));

                // Add Course Rows
                List<CurriculumItem> courseList = catEntry.getValue();
                for (CurriculumItem course : courseList) {
                    View rowView = LayoutInflater.from(this).inflate(R.layout.item_curriculum_course_row, containerCurriculumTable, false);
                    TextView tvStt = rowView.findViewById(R.id.tvStt);
                    TextView tvMaHp = rowView.findViewById(R.id.tvMaHp);
                    TextView tvTenHp = rowView.findViewById(R.id.tvTenHp);

                    if (tvStt != null) tvStt.setText(String.valueOf(sttCounter++));
                    if (tvMaHp != null) tvMaHp.setText(formatValue(course.getMaHP()));
                    if (tvTenHp != null) {
                        String name = formatValue(course.getTenHP());
                        if (name.contains("<")) {
                            tvTenHp.setText(android.text.Html.fromHtml(name, android.text.Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            tvTenHp.setText(name);
                        }
                    }

                    containerCurriculumTable.addView(rowView);
                }

                // Add Subtotal Row
                View subtotalView = LayoutInflater.from(this).inflate(R.layout.item_curriculum_subtotal_row, containerCurriculumTable, false);
                TextView tvSubtotal = subtotalView.findViewById(R.id.tvSubtotalLabel);
                if (tvSubtotal != null) {
                    if ("Bắt Buộc".equalsIgnoreCase(catName) || "Bắt buộc".equalsIgnoreCase(catName)) {
                        tvSubtotal.setText(getLang("NumberofRequiredCourses", getString(R.string.label_sum_compulsory)));
                    } else {
                        tvSubtotal.setText(getLang("NumberofElectiveCourses", getString(R.string.label_sum_elective)));
                    }
                }
                containerCurriculumTable.addView(subtotalView);
            }
        }
    }

    private View catCatView(View view) {
        return view;
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedStrings();
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("CurriculumComponent", "Curriculum", getString(R.string.title_study_programs)));
        }
        TextView tvColStt = findViewById(R.id.tvColStt);
        if (tvColStt != null) {
            tvColStt.setText(lm.getString("CurriculumComponent", "No", getString(R.string.label_stt)));
        }
        TextView tvColCourseId = findViewById(R.id.tvColCourseId);
        if (tvColCourseId != null) {
            tvColCourseId.setText(lm.getString("CurriculumComponent", "CourseID", getString(R.string.label_course_id)));
        }
        TextView tvColCourseName = findViewById(R.id.tvColCourseName);
        if (tvColCourseName != null) {
            tvColCourseName.setText(lm.getString("CurriculumComponent", "CourseName", getString(R.string.label_course_name)));
        }
        TextView tvNoteDetail = findViewById(R.id.tvNoteDetail);
        if (tvNoteDetail != null) {
            tvNoteDetail.setText(lm.getString("CurriculumComponent", "NoteDetail", getString(R.string.note_curriculum)));
        }
    }

    private String getLang(String keyLanguage, String defaultFallback) {
        return LanguageManager.getInstance().getString("CurriculumComponent", keyLanguage, defaultFallback);
    }
}