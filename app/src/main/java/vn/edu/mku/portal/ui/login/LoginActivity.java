package vn.edu.mku.portal.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.domain.model.ValidationResult;
import vn.edu.mku.portal.ui.forgotpassword.ForgotPasswordActivity;
import vn.edu.mku.portal.ui.student.StudentActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.getInstance().wrapContext(newBase));
    }

    private LoginViewModel viewModel;

    private TextView tvLoginTitle;
    private TextView tvLoginSubtitle;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputLayout tilCaptcha;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etCaptcha;
    private ImageView imgCaptcha;
    private ImageButton btnRefreshCaptcha;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.getInstance().isLoggedIn()) {
            navigateToStudentScreen();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCoordinator), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomPadding = Math.max(systemBars.bottom, imeInsets.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
            return insets;
        });

        initViews();
        initViewModel();
        setupListeners();
    }

    private void initViews() {
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilCaptcha = findViewById(R.id.tilCaptcha);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etCaptcha = findViewById(R.id.etCaptcha);

        imgCaptcha = findViewById(R.id.imgCaptcha);
        btnRefreshCaptcha = findViewById(R.id.btnRefreshCaptcha);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getLoginState().observe(this, this::handleLoginState);
        LanguageManager.getInstance().getLanguageLiveData().observe(this, map -> applyLocalizedStrings());
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        if (tvLoginTitle != null) {
            tvLoginTitle.setText(lm.getString("GuestPageComponent", "Login", getString(R.string.login_header)));
        }
        if (tvLoginSubtitle != null) {
            tvLoginSubtitle.setText(lm.getString("GuestPageComponent", "AcademicPortal", getString(R.string.login_subtitle)));
        }
        if (tilUsername != null) {
            tilUsername.setHint(lm.getString("GuestPageComponent", "Username", getString(R.string.label_username)));
        }
        if (tilPassword != null) {
            tilPassword.setHint(lm.getString("GuestPageComponent", "Password", getString(R.string.label_password)));
        }
        if (btnLogin != null) {
            btnLogin.setText(lm.getString("GuestPageComponent", "Login", getString(R.string.btn_login)));
        }
        if (tvForgotPassword != null) {
            tvForgotPassword.setText(lm.getString("GuestPageComponent", "ForgotPassword", getString(R.string.forgot_password)));
        }
    }

    private void setupListeners() {
        btnRefreshCaptcha.setOnClickListener(v -> {
            viewModel.loadCaptcha();
            etCaptcha.setText("");
            tilCaptcha.setError(null);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        View fabAction = findViewById(R.id.fabAction);
        if (fabAction != null) {
            fabAction.setOnClickListener(v -> showSettingsDialog());
        }

        clearErrorOnType(etUsername, tilUsername);
        clearErrorOnType(etPassword, tilPassword);
        clearErrorOnType(etCaptcha, tilCaptcha);

        // Auto-save username draft as user types
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vn.edu.mku.portal.data.local.DraftManager.getInstance().saveDraft(
                        vn.edu.mku.portal.data.local.DraftManager.FORM_LOGIN,
                        vn.edu.mku.portal.data.local.DraftManager.FIELD_USERNAME,
                        s != null ? s.toString().trim() : ""
                );
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Restore draft username if exists
        String savedUser = vn.edu.mku.portal.data.local.DraftManager.getInstance().getDraft(
                vn.edu.mku.portal.data.local.DraftManager.FORM_LOGIN,
                vn.edu.mku.portal.data.local.DraftManager.FIELD_USERNAME,
                ""
        );
        if (!TextUtils.isEmpty(savedUser) && TextUtils.isEmpty(etUsername.getText())) {
            etUsername.setText(savedUser);
            etUsername.setSelection(savedUser.length());
        }

        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void clearErrorOnType(TextInputEditText editText, TextInputLayout inputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputLayout.getError() != null) {
                    inputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performLogin() {
        if (!vn.edu.mku.portal.ui.common.NetworkMonitor.getInstance().isOnline()) {
            Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.text_no_network_no_cache), Snackbar.LENGTH_LONG).show();
            return;
        }

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String captchaInput = etCaptcha.getText() != null ? etCaptcha.getText().toString().trim() : "";

        viewModel.login(username, password, captchaInput);
    }

    private void handleLoginState(LoginState state) {
        if (state == null) return;

        btnLogin.setEnabled(!state.isLoading());

        // Display base64 Captcha
        if (!TextUtils.isEmpty(state.getCaptchaImageBase64())) {
            displayBase64Captcha(state.getCaptchaImageBase64());
        }

        ValidationResult validationResult = state.getValidationResult();
        if (validationResult != null) {
            applyValidationError(tilUsername, validationResult.getUsernameErrorResId(), "UsernameRequired");
            applyValidationError(tilPassword, validationResult.getPasswordErrorResId(), "PasswordRequired");
            applyValidationError(tilCaptcha, validationResult.getCaptchaErrorResId(), null);
        }

        if (state.getErrorMessageEvent() != null) {
            String errorMsg = state.getErrorMessageEvent().getContentIfNotHandled();
            if (errorMsg != null) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMsg, Snackbar.LENGTH_LONG).show();
                etCaptcha.setText("");
            }
        }

        if (state.getSuccessMessageEvent() != null) {
            String successMsg = state.getSuccessMessageEvent().getContentIfNotHandled();
            if (successMsg != null) {
                Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                navigateToStudentScreen();
            }
        }
    }

    private void navigateToStudentScreen() {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void displayBase64Captcha(String base64Data) {
        try {
            String cleanBase64 = base64Data.contains(",") ? base64Data.split(",")[1] : base64Data;
            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (bitmap != null) {
                imgCaptcha.setImageBitmap(bitmap);
            }
        } catch (Exception ignored) {}
    }

    private void applyValidationError(TextInputLayout layout, Integer errorResId, String keyLanguage) {
        if (errorResId != null) {
            String serverMsg = keyLanguage != null ? LanguageManager.getInstance().getString("GuestPageComponent", keyLanguage, null) : null;
            String finalMsg = serverMsg != null ? serverMsg : getString(errorResId);
            layout.setError(finalMsg);
        } else {
            layout.setError(null);
        }
    }

    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);

        AutoCompleteTextView spinnerTheme = dialogView.findViewById(R.id.spinnerTheme);
        com.google.android.material.materialswitch.MaterialSwitch switchRtl = dialogView.findViewById(R.id.switchRtl);
        com.google.android.material.materialswitch.MaterialSwitch switchResponsiveFont = dialogView.findViewById(R.id.switchResponsiveFont);
        com.google.android.material.materialswitch.MaterialSwitch switchCompact = dialogView.findViewById(R.id.switchCompact);
        com.google.android.material.materialswitch.MaterialSwitch switchRoundedCorners = dialogView.findViewById(R.id.switchRoundedCorners);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveSettings);

        vn.edu.mku.portal.data.local.SettingsManager sm = vn.edu.mku.portal.data.local.SettingsManager.getInstance();

        String[] themes = new String[]{getString(R.string.theme_light), getString(R.string.theme_dark), getString(R.string.theme_system)};
        if (spinnerTheme != null) {
            String currentTheme = sm.getTheme();
            int selectedIndex = vn.edu.mku.portal.data.local.SettingsManager.THEME_DARK.equalsIgnoreCase(currentTheme) ? 1 :
                    (vn.edu.mku.portal.data.local.SettingsManager.THEME_SYSTEM.equalsIgnoreCase(currentTheme) ? 2 : 0);

            spinnerTheme.setText(themes[selectedIndex], false);
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    themes
            );
            spinnerTheme.setAdapter(adapter);
        }

        if (switchRtl != null) switchRtl.setChecked(sm.isRtl());
        if (switchResponsiveFont != null) switchResponsiveFont.setChecked(sm.isResponsiveFont());
        if (switchCompact != null) switchCompact.setChecked(sm.isCompact());
        if (switchRoundedCorners != null) switchRoundedCorners.setChecked(sm.isRoundedCorners());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                dialog.dismiss();

                String selectedThemeStr = spinnerTheme != null ? spinnerTheme.getText().toString() : themes[0];
                String savedTheme = (getString(R.string.theme_dark).equalsIgnoreCase(selectedThemeStr) || vn.edu.mku.portal.data.local.SettingsManager.THEME_DARK.equalsIgnoreCase(selectedThemeStr))
                        ? vn.edu.mku.portal.data.local.SettingsManager.THEME_DARK
                        : ((getString(R.string.theme_system).equalsIgnoreCase(selectedThemeStr) || vn.edu.mku.portal.data.local.SettingsManager.THEME_SYSTEM.equalsIgnoreCase(selectedThemeStr))
                        ? vn.edu.mku.portal.data.local.SettingsManager.THEME_SYSTEM : vn.edu.mku.portal.data.local.SettingsManager.THEME_LIGHT);

                boolean rtl = switchRtl != null && switchRtl.isChecked();
                boolean responsive = switchResponsiveFont != null && switchResponsiveFont.isChecked();
                boolean compact = switchCompact != null && switchCompact.isChecked();
                boolean rounded = switchRoundedCorners != null && switchRoundedCorners.isChecked();

                sm.saveSettings(savedTheme, rtl, responsive, compact, rounded);
                sm.applySettingsToActivity(this);

                Toast.makeText(this, getString(R.string.btn_save_settings), Toast.LENGTH_SHORT).show();
                recreate();
            });
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            int dpWidth = Math.round(340 * getResources().getDisplayMetrics().density);
            dialog.getWindow().setLayout(dpWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}