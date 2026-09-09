package vn.edu.mku.portal.ui.forgotpassword;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.domain.usecase.ValidateResetPasswordUseCase;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.getInstance().wrapContext(newBase));
    }

    private ForgotPasswordViewModel viewModel;

    private TextView tvResetTitle;
    private TextView tvResetSubtitle;
    private TextInputLayout tilUsername;
    private TextInputLayout tilEmail;
    private TextInputEditText etUsername;
    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

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
        tvResetTitle = findViewById(R.id.tvResetTitle);
        tvResetSubtitle = findViewById(R.id.tvResetSubtitle);
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
        viewModel.getState().observe(this, this::handleState);
        LanguageManager.getInstance().getLanguageLiveData().observe(this, map -> applyLocalizedStrings());
    }

    private void applyLocalizedStrings() {
        LanguageManager lm = LanguageManager.getInstance();
        if (tvResetTitle != null) {
            tvResetTitle.setText(lm.getString("GuestPageComponent", "ForgotPassword", getString(R.string.reset_pwd_header)));
        }
        if (tvResetSubtitle != null) {
            tvResetSubtitle.setText(lm.getString("GuestPageComponent", "NewPasswordWillBeSentToYourEmail", getString(R.string.reset_pwd_subtitle)));
        }
        if (tilUsername != null) {
            tilUsername.setHint(lm.getString("GuestPageComponent", "Username", getString(R.string.label_username)));
        }
        if (tilEmail != null) {
            tilEmail.setHint(lm.getString("StudentComponent", "EmailAddress", getString(R.string.label_email)));
        }
        if (btnResetPassword != null) {
            btnResetPassword.setText(lm.getString("GuestPageComponent", "ForgotPassword", getString(R.string.btn_reset_password)));
        }
        if (tvBackToLogin != null) {
            tvBackToLogin.setText(lm.getString("GuestPageComponent", "BackToLoginPage", getString(R.string.btn_back_to_login)));
        }
    }

    private void setupListeners() {
        tvBackToLogin.setOnClickListener(v -> finish());

        View fabAction = findViewById(R.id.fabAction);
        if (fabAction != null) {
            fabAction.setOnClickListener(v -> showSettingsDialog());
        }

        clearErrorOnType(etUsername, tilUsername);
        clearErrorOnType(etEmail, tilEmail);

        btnResetPassword.setOnClickListener(v -> performReset());
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

    private void performReset() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        viewModel.resetPassword(username, email);
    }

    private void handleState(ForgotPasswordState state) {
        if (state == null) return;

        btnResetPassword.setEnabled(!state.isLoading());

        ValidateResetPasswordUseCase.Result val = state.getValidationResult();
        if (val != null) {
            applyValidationError(tilUsername, val.getUsernameErrorResId(), "UsernameRequired");
            String emailKey = (val.getEmailErrorResId() != null && val.getEmailErrorResId() == R.string.err_email_invalid)
                    ? "MalformedEmail" : "EmailRequired";
            applyValidationError(tilEmail, val.getEmailErrorResId(), emailKey);
        }

        if (state.getErrorMessageEvent() != null) {
            String errorMsg = state.getErrorMessageEvent().getContentIfNotHandled();
            if (errorMsg != null) {
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        if (state.getSuccessMessageEvent() != null) {
            String successMsg = state.getSuccessMessageEvent().getContentIfNotHandled();
            if (successMsg != null) {
                Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();
            }
        }
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