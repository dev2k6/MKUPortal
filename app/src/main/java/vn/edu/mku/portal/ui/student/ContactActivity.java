package vn.edu.mku.portal.ui.student;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.ContactSubmitRequest;
import vn.edu.mku.portal.data.network.model.DepartmentItem;
import vn.edu.mku.portal.data.repository.StudentRepository;

public class ContactActivity extends BaseStudentActivity {

    private TextInputLayout tilDepartment;
    private AutoCompleteTextView spinnerDepartment;
    private TextInputLayout tilContactSubject;
    private TextInputEditText etContactSubject;
    private EditText etContactContent;
    private MaterialButton btnSubmitContact;

    private final List<DepartmentItem> departmentList = new ArrayList<>();
    private String selectedDepartmentId = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        setupCommonUi();
        initViews();
        setupFormatToolbar();
        fetchDepartmentsData();
    }

    private void initViews() {
        tilDepartment = findViewById(R.id.tilDepartment);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        tilContactSubject = findViewById(R.id.tilContactSubject);
        etContactSubject = findViewById(R.id.etContactSubject);
        etContactContent = findViewById(R.id.etContactContent);
        btnSubmitContact = findViewById(R.id.btnSubmitContact);

        if (etContactSubject != null) {
            etContactSubject.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (tilContactSubject != null && tilContactSubject.getError() != null) {
                        tilContactSubject.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnSubmitContact != null) {
            btnSubmitContact.setOnClickListener(v -> submitContactData());
        }
    }

    private void setupFormatToolbar() {
        TextView btnBold = findViewById(R.id.btnFormatBold);
        TextView btnItalic = findViewById(R.id.btnFormatItalic);
        TextView btnUnderline = findViewById(R.id.btnFormatUnderline);
        TextView btnLink = findViewById(R.id.btnFormatLink);
        TextView btnOrderedList = findViewById(R.id.btnFormatOrderedList);
        TextView btnBulletList = findViewById(R.id.btnFormatBulletList);
        TextView btnClear = findViewById(R.id.btnFormatClear);

        if (btnBold != null) btnBold.setOnClickListener(v -> toggleStyleSpan(Typeface.BOLD));
        if (btnItalic != null) btnItalic.setOnClickListener(v -> toggleStyleSpan(Typeface.ITALIC));
        if (btnUnderline != null) btnUnderline.setOnClickListener(v -> toggleUnderlineSpan());
        if (btnLink != null) btnLink.setOnClickListener(v -> toggleLinkSpan());
        if (btnOrderedList != null) btnOrderedList.setOnClickListener(v -> toggleOrderedList());
        if (btnBulletList != null) btnBulletList.setOnClickListener(v -> toggleBulletList());
        if (btnClear != null) btnClear.setOnClickListener(v -> clearFormatting());
    }

    private void toggleStyleSpan(int style) {
        if (etContactContent == null) return;
        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();
        if (start < 0 || end < 0 || start == end) return;

        Editable editable = etContactContent.getText();
        if (editable == null) return;

        StyleSpan[] existingSpans = editable.getSpans(start, end, StyleSpan.class);
        boolean hasSpan = false;
        for (StyleSpan span : existingSpans) {
            if (span.getStyle() == style) {
                editable.removeSpan(span);
                hasSpan = true;
            }
        }

        if (!hasSpan) {
            editable.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void toggleUnderlineSpan() {
        if (etContactContent == null) return;
        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();
        if (start < 0 || end < 0 || start == end) return;

        Editable editable = etContactContent.getText();
        if (editable == null) return;

        UnderlineSpan[] existingSpans = editable.getSpans(start, end, UnderlineSpan.class);
        if (existingSpans != null && existingSpans.length > 0) {
            for (UnderlineSpan span : existingSpans) {
                editable.removeSpan(span);
            }
        } else {
            editable.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void toggleLinkSpan() {
        if (etContactContent == null) return;
        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();
        if (start < 0 || end < 0 || start == end) return;

        Editable editable = etContactContent.getText();
        if (editable == null) return;

        URLSpan[] existingSpans = editable.getSpans(start, end, URLSpan.class);
        if (existingSpans != null && existingSpans.length > 0) {
            for (URLSpan span : existingSpans) {
                editable.removeSpan(span);
            }
        } else {
            editable.setSpan(new URLSpan("https://portal.mku.edu.vn"), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void toggleBulletList() {
        if (etContactContent == null) return;
        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();

        Editable editable = etContactContent.getText();
        if (editable == null) return;

        if (start == end) {
            editable.insert(start, "• ");
        } else {
            String selected = editable.subSequence(start, end).toString();
            String[] lines = selected.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].startsWith("• ")) {
                    sb.append("• ").append(lines[i]);
                } else {
                    sb.append(lines[i].replaceFirst("• ", ""));
                }
                if (i < lines.length - 1) sb.append("\n");
            }
            editable.replace(start, end, sb.toString());
        }
    }

    private void toggleOrderedList() {
        if (etContactContent == null) return;
        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();

        Editable editable = etContactContent.getText();
        if (editable == null) return;

        if (start == end) {
            editable.insert(start, "1. ");
        } else {
            String selected = editable.subSequence(start, end).toString();
            String[] lines = selected.split("\n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                sb.append(i + 1).append(". ").append(lines[i].replaceAll("^\\d+\\.\\s*", ""));
                if (i < lines.length - 1) sb.append("\n");
            }
            editable.replace(start, end, sb.toString());
        }
    }

    private void clearFormatting() {
        if (etContactContent == null) return;
        Editable editable = etContactContent.getText();
        if (editable == null) return;

        int start = etContactContent.getSelectionStart();
        int end = etContactContent.getSelectionEnd();

        if (start < 0 || end < 0 || start == end) {
            start = 0;
            end = editable.length();
        }

        Object[] spans = editable.getSpans(start, end, Object.class);
        for (Object span : spans) {
            editable.removeSpan(span);
        }
    }

    private void fetchDepartmentsData() {
        studentRepository.fetchDepartments(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<DepartmentItem> result) {
                departmentList.clear();
                if (result != null && !result.isEmpty()) {
                    departmentList.addAll(result);
                }

                // Add standard departments to ensure complete dropdown choices
                addDepartmentIfNotExists("01", "PSC TEST");

                populateDepartmentSpinner();
            }

            @Override
            public void onError(String errorMessage) {
                setupDefaultDepartments();
                populateDepartmentSpinner();
            }
        });
    }

    private void addDepartmentIfNotExists(String id, String name) {
        for (DepartmentItem item : departmentList) {
            if (item.getTenPhongBan() != null && item.getTenPhongBan().equalsIgnoreCase(name)) {
                return;
            }
        }
        departmentList.add(new DepartmentItem(id, name));
    }

    private void setupDefaultDepartments() {
        departmentList.clear();
        departmentList.add(new DepartmentItem("01", "PSC TEST"));
    }

    private void populateDepartmentSpinner() {
        if (spinnerDepartment == null || departmentList.isEmpty()) return;

        List<String> names = new ArrayList<>();
        for (DepartmentItem item : departmentList) {
            names.add(item.getTenPhongBan() != null ? item.getTenPhongBan() : "");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
        spinnerDepartment.setAdapter(adapter);

        selectedDepartmentId = departmentList.get(0).getId();
        spinnerDepartment.setText(names.get(0), false);

        spinnerDepartment.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < departmentList.size()) {
                selectedDepartmentId = departmentList.get(position).getId();
                if (tilDepartment != null) {
                    tilDepartment.setError(null);
                }
            }
        });
    }

    private void submitContactData() {
        if (tilContactSubject != null) tilContactSubject.setError(null);
        if (tilDepartment != null) tilDepartment.setError(null);

        String subject = etContactSubject != null && etContactSubject.getText() != null ? etContactSubject.getText().toString().trim() : "";
        Editable editableContent = etContactContent != null ? etContactContent.getText() : null;
        String rawContent = editableContent != null ? editableContent.toString().trim() : "";

        boolean hasError = false;

        if (TextUtils.isEmpty(selectedDepartmentId)) {
            if (tilDepartment != null) tilDepartment.setError(getString(R.string.err_department_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(subject)) {
            if (tilContactSubject != null) tilContactSubject.setError(getString(R.string.err_subject_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(rawContent)) {
            Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.err_content_required), Snackbar.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) return;

        String formattedContent = editableContent != null ? Html.toHtml(editableContent, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL).trim() : "";

        if (btnSubmitContact != null) btnSubmitContact.setEnabled(false);

        String departmentIdToSend = selectedDepartmentId;
        String selectedName = spinnerDepartment != null ? spinnerDepartment.getText().toString() : "";
        if (TextUtils.isEmpty(departmentIdToSend) || "PSC TEST".equalsIgnoreCase(selectedName)) {
            departmentIdToSend = "01";
        }

        ContactSubmitRequest request = new ContactSubmitRequest(departmentIdToSend, subject, formattedContent);

        studentRepository.submitContact(request, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(String result) {
                if (btnSubmitContact != null) btnSubmitContact.setEnabled(true);

                String msg = !TextUtils.isEmpty(result) ? result : getString(R.string.msg_contact_success);
                Snackbar.make(findViewById(R.id.mainCoordinator), msg, Snackbar.LENGTH_LONG).show();

                if (etContactSubject != null) etContactSubject.setText("");
                if (etContactContent != null) etContactContent.setText("");
            }

            @Override
            public void onError(String errorMessage) {
                if (btnSubmitContact != null) btnSubmitContact.setEnabled(true);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
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
            tvHeaderTitle.setText(lm.getString("ContactComponent", "Contact", getString(R.string.title_contact_header)));
        }
    }
}
