package vn.edu.mku.portal.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SessionManager {

    private static final String PREF_NAME = "mku_student_session";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_STUDENT_NAME = "student_name";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager is not initialized. Call SessionManager.init(context) first.");
        }
        return instance;
    }

    public void saveSession(String token, String studentId, String studentName) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_STUDENT_ID, studentId)
                .putString(KEY_STUDENT_NAME, studentName)
                .apply();
    }

    public void updateStudentName(String studentName) {
        if (!TextUtils.isEmpty(studentName)) {
            prefs.edit().putString(KEY_STUDENT_NAME, studentName).apply();
        }
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public String getStudentId() {
        return prefs.getString(KEY_STUDENT_ID, "");
    }

    public String getStudentName() {
        return prefs.getString(KEY_STUDENT_NAME, "");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getToken());
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}