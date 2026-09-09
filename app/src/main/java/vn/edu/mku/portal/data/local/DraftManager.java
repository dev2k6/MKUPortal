package vn.edu.mku.portal.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class DraftManager {

    private static final String PREF_DRAFT_NAME = "mku_app_drafts";

    // Form keys
    public static final String FORM_LOGIN = "form_login";
    public static final String FORM_FORGOT_PASSWORD = "form_forgot_password";
    public static final String FORM_CONTACT = "form_contact";

    // Field keys
    public static final String FIELD_USERNAME = "field_username";
    public static final String FIELD_EMAIL = "field_email";
    public static final String FIELD_SUBJECT = "field_subject";
    public static final String FIELD_CONTENT = "field_content";
    public static final String FIELD_DEPT_ID = "field_dept_id";

    private static DraftManager instance;
    private final SharedPreferences prefs;

    private DraftManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_DRAFT_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new DraftManager(context);
        }
    }

    public static synchronized DraftManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DraftManager is not initialized. Call init(context) first.");
        }
        return instance;
    }

    // Global drafts (e.g. login, forgot password)
    public void saveDraft(String formKey, String fieldKey, String value) {
        String key = formKey + "_" + fieldKey;
        if (value == null) {
            prefs.edit().remove(key).apply();
        } else {
            prefs.edit().putString(key, value).apply();
        }
    }

    public String getDraft(String formKey, String fieldKey, String defaultValue) {
        String key = formKey + "_" + fieldKey;
        return prefs.getString(key, defaultValue);
    }

    public void clearDraft(String formKey) {
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(formKey + "_")) {
                editor.remove(key);
            }
        }
        editor.apply();
    }

    // Student-scoped drafts (e.g. contact/feedback, declaration per student)
    private String buildStudentPrefix(String studentId, String formKey, String fieldKey) {
        String sId = !TextUtils.isEmpty(studentId) ? studentId : "guest";
        return sId + "_" + formKey + "_" + fieldKey;
    }

    public void saveStudentDraft(String studentId, String formKey, String fieldKey, String value) {
        String key = buildStudentPrefix(studentId, formKey, fieldKey);
        if (value == null) {
            prefs.edit().remove(key).apply();
        } else {
            prefs.edit().putString(key, value).apply();
        }
    }

    public String getStudentDraft(String studentId, String formKey, String fieldKey, String defaultValue) {
        String key = buildStudentPrefix(studentId, formKey, fieldKey);
        return prefs.getString(key, defaultValue);
    }

    public void clearStudentDraft(String studentId, String formKey) {
        String prefix = (!TextUtils.isEmpty(studentId) ? studentId : "guest") + "_" + formKey + "_";
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(prefix)) {
                editor.remove(key);
            }
        }
        editor.apply();
    }
}
