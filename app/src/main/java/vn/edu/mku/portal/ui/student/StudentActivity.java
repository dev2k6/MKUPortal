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

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.StudentInfoObj1;

import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class StudentActivity extends BaseStudentActivity {

    private StudentViewModel viewModel;

    private TextView tvProfileStudentName;
    private LinearLayout containerInfoStudent;
    private LinearLayout containerInfoContact;
    private LinearLayout containerInfoCourse;
    private LinearLayout containerInfoParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        setupCommonUi();

        tvProfileStudentName = findViewById(R.id.tvProfileStudentName);
        containerInfoStudent = findViewById(R.id.containerInfoStudent);
        containerInfoContact = findViewById(R.id.containerInfoContact);
        containerInfoCourse = findViewById(R.id.containerInfoCourse);
        containerInfoParent = findViewById(R.id.containerInfoParent);

        View layoutSkeleton = findViewById(R.id.layoutSkeleton);
        View containerProfileCards = findViewById(R.id.containerProfileCards);
        if (layoutSkeleton != null) {
            layoutSkeleton.setVisibility(View.VISIBLE);
            SkeletonHelper.startPulseAnimation(layoutSkeleton);
        }
        if (containerProfileCards != null) {
            containerProfileCards.setVisibility(View.GONE);
        }

        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        viewModel.getState().observe(this, state -> {
            if (state != null) {
                if (state.getStudentInfo() != null) {
                    bindStudentInfo(state.getStudentInfo());
                }
            }
        });
    }

    private void bindStudentInfo(StudentInfoObj1 info) {
        if (info == null) return;

        View layoutSkeleton = findViewById(R.id.layoutSkeleton);
        View containerProfileCards = findViewById(R.id.containerProfileCards);
        if (layoutSkeleton != null) {
            SkeletonHelper.stopPulseAnimation(layoutSkeleton);
            layoutSkeleton.setVisibility(View.GONE);
        }
        if (containerProfileCards != null) {
            containerProfileCards.setVisibility(View.VISIBLE);
        }

        if (tvProfileStudentName != null) {
            tvProfileStudentName.setText(formatValue(info.getStudentName()));
        }

        populateCardRows(containerInfoStudent, new String[][]{
                {getLang("StudentCode", getString(R.string.label_student_id)), formatValue(info.getStudentId())},
                {getLang("Gender", getString(R.string.label_gender)), formatValue(info.getGenders())},
                {getLang("DateOfBirth", getString(R.string.label_date_of_birth)), formatValue(info.getBirthDay())},
                {getLang("Ethnic", getString(R.string.label_ethnicity)), formatValue(info.getEthnicName())},
                {getLang("Religion", getString(R.string.label_religion)), formatValue(info.getReligionName())},
                {getLang("PlaceOfBirth", getString(R.string.label_place_of_birth)), formatValue(info.getBirthPlace())},
                {getLang("IDCardNumber", getString(R.string.label_id_card)), formatValue(info.getIdCard())}
        });

        populateCardRows(containerInfoContact, new String[][]{
                {getLang("Phone", getString(R.string.label_phone)), formatValue(info.getHomePhone())},
                {getLang("Mobile", getString(R.string.label_mobile)), formatValue(info.getMobilePhone())},
                {getLang("Email", getString(R.string.label_email)), formatValue(info.getEmail2())},
                {getLang("ContactAddress", getString(R.string.label_contact_address)), formatValue(info.getContactAddress())},
                {getLang("PermanentAddress", getString(R.string.label_permanent_address)), formatValue(info.getProvinceName())}
        });

        populateCardRows(containerInfoCourse, new String[][]{
                {getLang("Class", getString(R.string.label_class)), formatValue(info.getClassStudentName())},
                {getLang("Faculty", getString(R.string.label_faculty)), formatValue(info.getDepartmentName())},
                {getLang("Major", getString(R.string.label_major)), formatValue(info.getOlogyName())},
                {getLang("AcademicCourse", getString(R.string.label_course)), formatValue(info.getCourseName())},
                {getLang("TrainingType", getString(R.string.label_training_type)), formatValue(info.getStudyTypeName())}
        });

        populateCardRows(containerInfoParent, new String[][]{
                {getLang("FatherFullName", getString(R.string.label_father_name)), formatValue(info.getFatherName())},
                {getLang("MotherFullName", getString(R.string.label_mother_name)), formatValue(info.getMotherName())},
                {getLang("GuardianAddress", getString(R.string.label_parents_address)), formatValue(info.getContactPersonAdd())}
        });
    }

    private void populateCardRows(LinearLayout container, String[][] rows) {
        if (container == null) return;
        container.removeAllViews();

        for (int i = 0; i < rows.length; i++) {
            String label = rows[i][0];
            String value = rows[i][1];

            View rowView = LayoutInflater.from(this).inflate(R.layout.item_student_info_row, container, false);
            TextView tvLabel = rowView.findViewById(R.id.tvRowLabel);
            TextView tvValue = rowView.findViewById(R.id.tvRowValue);

            if (tvLabel != null) tvLabel.setText(label);
            if (tvValue != null) tvValue.setText(value);

            View divider = rowView.findViewById(R.id.rowDivider);
            if (divider != null && i == rows.length - 1) {
                divider.setVisibility(View.GONE);
            }

            container.addView(rowView);
        }
    }

    @Override
    protected void onLanguageChanged() {
        applyLocalizedTitles();
        if (viewModel != null && viewModel.getState().getValue() != null && viewModel.getState().getValue().getStudentInfo() != null) {
            bindStudentInfo(viewModel.getState().getValue().getStudentInfo());
        }
    }

    private void applyLocalizedTitles() {
        LanguageManager lm = LanguageManager.getInstance();
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(lm.getString("StudentComponent", "StudentInfo", getString(R.string.title_student_info)));
        }
        TextView tvSectionStudentInfo = findViewById(R.id.tvSectionStudentInfo);
        if (tvSectionStudentInfo != null) {
            tvSectionStudentInfo.setText(lm.getString("StudentComponent", "StudentInfo", getString(R.string.section_student_info)));
        }
        TextView tvSectionContactInfo = findViewById(R.id.tvSectionContactInfo);
        if (tvSectionContactInfo != null) {
            tvSectionContactInfo.setText(lm.getString("StudentComponent", "ContactInfo", getString(R.string.section_contact_info)));
        }
        TextView tvSectionCourseInfo = findViewById(R.id.tvSectionCourseInfo);
        if (tvSectionCourseInfo != null) {
            tvSectionCourseInfo.setText(lm.getString("StudentComponent", "CourseInfo", getString(R.string.section_course_info)));
        }
        TextView tvSectionParentInfo = findViewById(R.id.tvSectionParentInfo);
        if (tvSectionParentInfo != null) {
            tvSectionParentInfo.setText(lm.getString("StudentComponent", "ContactPersonInfo", getString(R.string.section_parent_info)));
        }
    }

    private String getLang(String keyLanguage, String defaultFallback) {
        return LanguageManager.getInstance().getString("StudentComponent", keyLanguage, defaultFallback);
    }
}