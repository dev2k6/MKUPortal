package vn.edu.mku.portal.ui.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.local.SessionManager;
import vn.edu.mku.portal.data.local.SettingsManager;
import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.data.repository.StudentRepositoryImpl;
import vn.edu.mku.portal.ui.login.LoginActivity;

public abstract class BaseStudentActivity extends AppCompatActivity {

    protected StudentRepository studentRepository;

    protected DrawerLayout drawerLayout;
    protected ImageView btnMenu;
    protected ImageView imgFlag;
    protected RelativeLayout btnNotification;
    protected TextView tvNotificationBadge;
    protected ImageView btnAccount;

    protected TextView tvDrawerStudentName;
    protected TextView tvDrawerStudentSub;
    protected LinearLayout containerDrawerMenu;
    protected FloatingActionButton fabAction;

    protected List<StudentMessage> cachedMessages;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.getInstance().wrapContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Required authentication check
        if (!SessionManager.getInstance().isLoggedIn()) {
            redirectToLogin();
            return;
        }

        SettingsManager.getInstance().applySettingsToActivity(this);
        EdgeToEdge.enable(this);

        studentRepository = new StudentRepositoryImpl();
    }

    protected void setupCommonUi() {
        View mainCoord = findViewById(R.id.mainCoordinator);
        if (mainCoord != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainCoord, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
                int bottomPadding = Math.max(systemBars.bottom, imeInsets.bottom);
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
                return insets;
            });
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        imgFlag = findViewById(R.id.imgFlag);
        btnNotification = findViewById(R.id.btnNotification);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        btnAccount = findViewById(R.id.btnAccount);

        tvDrawerStudentName = findViewById(R.id.tvDrawerStudentName);
        tvDrawerStudentSub = findViewById(R.id.tvDrawerStudentSub);
        containerDrawerMenu = findViewById(R.id.containerDrawerMenu);
        fabAction = findViewById(R.id.fabAction);

        if (imgFlag != null) {
            String currentLang = LanguageManager.getInstance().getCurrentLanguage();
            if (LanguageManager.LANG_EN.equalsIgnoreCase(currentLang)) {
                imgFlag.setImageResource(R.drawable.ic_flag_en);
            } else {
                imgFlag.setImageResource(R.drawable.ic_flag_vn);
            }
            imgFlag.setOnClickListener(v -> showLanguagePopup());
        }

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationPopup(cachedMessages));
        }

        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> showAccountPopup());
        }

        if (fabAction != null) {
            fabAction.setOnClickListener(v -> showSettingsDialog());
        }

        // Header drawer initial text from Session
        SessionManager session = SessionManager.getInstance();
        updateDrawerHeaderText(session.getStudentName(), session.getStudentId());

        loadCommonData();

        LanguageManager.getInstance().getLanguageLiveData().observe(this, map -> onLanguageChanged());
    }

    protected void updateDrawerHeaderText(String name, String id) {
        if (tvDrawerStudentName != null) {
            if (!TextUtils.isEmpty(name) && !name.equalsIgnoreCase(id)) {
                tvDrawerStudentName.setText(name);
            } else if (!TextUtils.isEmpty(id)) {
                tvDrawerStudentName.setText(id);
            } else {
                tvDrawerStudentName.setText("-");
            }
        }
        if (tvDrawerStudentSub != null) {
            tvDrawerStudentSub.setText(!TextUtils.isEmpty(id) ? getString(R.string.subtitle_student_id, id) : "");
        }
    }

    protected void loadCommonData() {
        fetchStudentHeaderInfo();
        fetchCommonMessages();
        fetchCommonMenuData();
    }

    private void fetchStudentHeaderInfo() {
        SessionManager session = SessionManager.getInstance();
        if (TextUtils.isEmpty(session.getStudentName()) || session.getStudentName().equalsIgnoreCase(session.getStudentId())) {
            studentRepository.fetchStudentInfo(new StudentRepository.ApiCallback<>() {
                @Override
                public void onSuccess(vn.edu.mku.portal.data.network.model.StudentInfoResponse result) {
                    if (result != null && result.getFirstObj1() != null) {
                        String realName = result.getFirstObj1().getStudentName();
                        if (!TextUtils.isEmpty(realName)) {
                            SessionManager.getInstance().updateStudentName(realName);
                            updateDrawerHeaderText(realName, session.getStudentId());
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {}
            });
        }
    }

    private void fetchCommonMessages() {
        studentRepository.fetchMessages(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudentMessage> result) {
                cachedMessages = result;
                int unread = 0;
                if (result != null) {
                    for (StudentMessage msg : result) {
                        if (msg.getIsRead() == 0) unread++;
                    }
                }
                if (tvNotificationBadge != null) {
                    if (unread > 0) {
                        tvNotificationBadge.setVisibility(View.VISIBLE);
                        tvNotificationBadge.setText(String.valueOf(unread));
                    } else {
                        tvNotificationBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Ignore
            }
        });
    }

    private void fetchCommonMenuData() {
        String lang = LanguageManager.getInstance().getCurrentLanguage();
        studentRepository.fetchMenu(lang, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<MenuItem> result) {
                populateDrawerMenu(result);
            }

            @Override
            public void onError(String errorMessage) {
                // Ignore
            }
        });
    }

    protected void populateDrawerMenu(List<MenuItem> menuList) {
        if (menuList == null || containerDrawerMenu == null) return;
        containerDrawerMenu.removeAllViews();

        for (MenuItem group : menuList) {
            View groupView = LayoutInflater.from(this).inflate(R.layout.item_drawer_menu_group, containerDrawerMenu, false);
            TextView tvGroup = groupView.findViewById(R.id.tvGroupTitle);
            if (tvGroup != null && group.getTenChucNang() != null) {
                tvGroup.setText(group.getTenChucNang().toUpperCase());
            }
            containerDrawerMenu.addView(groupView);

            if (group.getChildMenu() != null) {
                for (MenuItem child : group.getChildMenu()) {
                    View childView = LayoutInflater.from(this).inflate(R.layout.item_drawer_menu_child, containerDrawerMenu, false);
                    View rootLayout = childView.findViewById(R.id.layoutChildItemRoot);
                    TextView tvChild = childView.findViewById(R.id.tvChildTitle);
                    ImageView imgIcon = childView.findViewById(R.id.imgChildIcon);

                    boolean isActive = isMenuActive(child);

                    if (tvChild != null) {
                        tvChild.setText(getLocalizedMenuTitle(child.getLienKet(), child.getTenChucNang()));
                        if (isActive) {
                            tvChild.setTextColor(getColor(R.color.mku_title_blue));
                            tvChild.setTypeface(null, Typeface.BOLD);
                        } else {
                            tvChild.setTextColor(getColor(R.color.mku_text_sub));
                            tvChild.setTypeface(null, Typeface.NORMAL);
                        }
                    }

                    if (imgIcon != null) {
                        setDrawerMenuIcon(imgIcon, child.getDoHoaDeThuong(), child.getLienKet());
                        if (isActive) {
                            imgIcon.setColorFilter(getColor(R.color.mku_title_blue));
                        } else {
                            imgIcon.setColorFilter(getColor(R.color.mku_text_sub));
                        }
                    }

                    if (rootLayout != null && isActive) {
                        rootLayout.setBackgroundResource(R.drawable.bg_active_menu_item);
                    }

                    childView.setOnClickListener(v -> {
                        if (drawerLayout != null) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        handleMenuNavigation(child);
                    });

                    containerDrawerMenu.addView(childView);
                }
            }
        }
    }

    protected String getLocalizedMenuTitle(String link, String serverTitle) {
        String url = link != null ? link.toLowerCase(java.util.Locale.ROOT) : "";
        String title = serverTitle != null ? serverTitle.toLowerCase(java.util.Locale.ROOT) : "";

        if (url.contains("info") || title.contains("cá nhân") || title.contains("personal")) return getString(R.string.menu_personal_info);
        if (url.contains("index") || title.contains("thông báo") || title.contains("notification")) return getString(R.string.menu_notifications);
        if (url.contains("studyprograms") || title.contains("chương trình đào tạo") || title.contains("academic program")) return getString(R.string.menu_academic_programs);
        if ((url.contains("schedules") && !url.contains("exam")) || title.contains("lịch học") || title.equalsIgnoreCase("schedule")) return getString(R.string.menu_schedule);
        if (url.contains("exam") || title.contains("lịch thi") || title.contains("exam")) return getString(R.string.menu_exam_schedule);
        if (url.contains("decisions") || title.contains("quyết định") || title.contains("decision")) return getString(R.string.menu_student_decisions);
        if (url.contains("xemdiemrenluyen") || url.contains("behaviorgrade") || url.contains("renluyen") || title.contains("rèn luyện") || title.contains("behavior")) return getString(R.string.menu_behavior_score);
        if (url.contains("marks") || url.contains("academicresult") || url.contains("hoctap") || title.contains("học tập") || title.contains("grade")) return getString(R.string.menu_student_grade);
        if (url.contains("finance") || url.contains("taichinh") || title.contains("tài chính") || title.contains("finance")) return getString(R.string.menu_student_finance);
        if (url.contains("invoicedetail") || url.contains("hoadon") || title.contains("hóa đơn") || title.contains("invoice")) return getString(R.string.menu_invoice_details);
        if (url.contains("discussion") || url.contains("thaoluan") || title.contains("thảo luận") || title.contains("discussion")) return getString(R.string.menu_discussion);
        if (url.contains("contact") || url.contains("gopy") || url.contains("lienhe") || title.contains("liên hệ") || title.contains("góp ý") || title.contains("contact")) return getString(R.string.menu_contact_feedback);

        return formatValue(serverTitle);
    }

    protected String formatTermDisplayName(String termId, String termName) {
        boolean isEn = LanguageManager.LANG_EN.equalsIgnoreCase(LanguageManager.getInstance().getCurrentLanguage());
        String name = formatValue(termName);
        String id = formatValue(termId);

        if (isEn) {
            if (id.equalsIgnoreCase("HK01") || name.contains("Học kỳ 1") || name.equalsIgnoreCase("HK01")) return "Term 1";
            if (id.equalsIgnoreCase("HK02") || name.contains("Học kỳ 2") || name.equalsIgnoreCase("HK02")) return "Term 2";
            if (id.equalsIgnoreCase("HK03") || name.contains("Học kỳ 3") || name.equalsIgnoreCase("HK03")) return "Term 3";
            if (name.startsWith("Học kỳ ")) return name.replace("Học kỳ ", "Term ");
            if (name.startsWith("Học Kỳ ")) return name.replace("Học Kỳ ", "Term ");
        } else {
            if (id.equalsIgnoreCase("HK01") && (TextUtils.isEmpty(name) || name.equalsIgnoreCase("HK01"))) return "Học kỳ 1";
            if (id.equalsIgnoreCase("HK02") && (TextUtils.isEmpty(name) || name.equalsIgnoreCase("HK02"))) return "Học kỳ 2";
            if (id.equalsIgnoreCase("HK03") && (TextUtils.isEmpty(name) || name.equalsIgnoreCase("HK03"))) return "Học kỳ 3";
        }
        return !TextUtils.isEmpty(name) ? name : id;
    }

    protected boolean isMenuActive(MenuItem child) {
        if (child == null || child.getLienKet() == null) return false;
        String link = child.getLienKet();
        String activityName = getClass().getSimpleName();

        if (activityName.equals("StudentActivity") && link.contains("info")) return true;
        if (activityName.equals("NotificationIndexActivity") && link.contains("index")) return true;
        if (activityName.equals("StudyProgramsActivity") && link.contains("studyprograms")) return true;
        if (activityName.equals("SchedulesActivity") && link.contains("schedules")) return true;
        if (activityName.equals("ExamActivity") && link.contains("exam")) return true;
        if (activityName.equals("DecisionsActivity") && link.contains("decisions")) return true;
        if (activityName.equals("BehaviorScoreActivity") && (link.contains("xemdiemrenluyen") || link.contains("behaviorgrade") || link.contains("renluyen"))) return true;
        if (activityName.equals("MarksActivity") && (link.contains("marks") || link.contains("academicresult") || link.contains("hoctap"))) return true;
        if (activityName.equals("AccountFeesActivity") && (link.contains("finance") || link.contains("accountfees") || link.contains("taichinh"))) return true;
        if (activityName.equals("OrderDetailActivity") && (link.contains("orderdetail") || link.contains("chitiethoadon") || link.contains("hoadon") || link.contains("invoicedetail"))) return true;
        if (activityName.equals("CommentActivity") && (link.contains("comment") || link.contains("discussion") || link.contains("thaoluan") || link.contains("y-kien-thao-luan"))) return true;
        if (activityName.equals("ContactActivity") && (link.contains("contact") || link.contains("gopy") || link.contains("lienhe"))) return true;

        return false;
    }

    protected void handleMenuNavigation(MenuItem item) {
        if (item == null || item.getLienKet() == null) return;
        String link = item.getLienKet();

        if (link.contains("info") && !getClass().getSimpleName().equals("StudentActivity")) {
            startActivity(new Intent(this, StudentActivity.class));
            finish();
        } else if (link.contains("index") && !getClass().getSimpleName().equals("NotificationIndexActivity")) {
            startActivity(new Intent(this, NotificationIndexActivity.class));
            finish();
        } else if (link.contains("studyprograms") && !getClass().getSimpleName().equals("StudyProgramsActivity")) {
            startActivity(new Intent(this, StudyProgramsActivity.class));
            finish();
        } else if (link.contains("schedules") && !getClass().getSimpleName().equals("SchedulesActivity")) {
            startActivity(new Intent(this, SchedulesActivity.class));
            finish();
        } else if (link.contains("exam") && !getClass().getSimpleName().equals("ExamActivity")) {
            startActivity(new Intent(this, ExamActivity.class));
            finish();
        } else if (link.contains("decisions") && !getClass().getSimpleName().equals("DecisionsActivity")) {
            startActivity(new Intent(this, DecisionsActivity.class));
            finish();
        } else if ((link.contains("xemdiemrenluyen") || link.contains("behaviorgrade") || link.contains("renluyen")) && !getClass().getSimpleName().equals("BehaviorScoreActivity")) {
            startActivity(new Intent(this, BehaviorScoreActivity.class));
            finish();
        } else if ((link.contains("marks") || link.contains("academicresult") || link.contains("hoctap")) && !getClass().getSimpleName().equals("MarksActivity")) {
            startActivity(new Intent(this, MarksActivity.class));
            finish();
        } else if ((link.contains("finance") || link.contains("accountfees") || link.contains("taichinh")) && !getClass().getSimpleName().equals("AccountFeesActivity")) {
            startActivity(new Intent(this, AccountFeesActivity.class));
            finish();
        } else if ((link.contains("orderdetail") || link.contains("chitiethoadon") || link.contains("hoadon") || link.contains("invoicedetail")) && !getClass().getSimpleName().equals("OrderDetailActivity")) {
            startActivity(new Intent(this, OrderDetailActivity.class));
            finish();
        } else if ((link.contains("comment") || link.contains("discussion") || link.contains("thaoluan") || link.contains("y-kien-thao-luan")) && !getClass().getSimpleName().equals("CommentActivity")) {
            startActivity(new Intent(this, CommentActivity.class));
            finish();
        } else if ((link.contains("contact") || link.contains("gopy") || link.contains("lienhe")) && !getClass().getSimpleName().equals("ContactActivity")) {
            startActivity(new Intent(this, ContactActivity.class));
            finish();
        } else if (!link.contains("info") && !link.contains("index") && !link.contains("studyprograms") && !link.contains("schedules")
                && !link.contains("exam") && !link.contains("decisions") && !link.contains("renluyen") && !link.contains("hoctap") && !link.contains("marks") && !link.contains("finance")
                && !link.contains("orderdetail") && !link.contains("chitiethoadon") && !link.contains("hoadon") && !link.contains("invoicedetail")
                && !link.contains("comment") && !link.contains("discussion") && !link.contains("thaoluan")
                && !link.contains("contact") && !link.contains("gopy") && !link.contains("lienhe")) {
            Toast.makeText(this, item.getTenChucNang(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setDrawerMenuIcon(ImageView imageView, String iconType, String link) {
        String type = iconType != null ? iconType : "";
        String url = link != null ? link : "";

        if ("User".equalsIgnoreCase(type) || url.contains("info")) {
            imageView.setImageResource(R.drawable.ic_menu_person);
        } else if ("Bell".equalsIgnoreCase(type) || url.contains("index")) {
            imageView.setImageResource(R.drawable.ic_menu_bell);
        } else if ("ClipboardList".equalsIgnoreCase(type) || url.contains("studyprograms") || url.contains("decisions")) {
            imageView.setImageResource(R.drawable.ic_menu_clipboard);
        } else if ("Calendar".equalsIgnoreCase(type) || url.contains("schedules") || url.contains("exam")) {
            imageView.setImageResource(R.drawable.ic_menu_calendar);
        } else if ("Award".equalsIgnoreCase(type) || url.contains("behaviorgrade") || url.contains("renluyen") || url.contains("xemdiemrenluyen")) {
            imageView.setImageResource(R.drawable.ic_menu_award);
        } else if ("Grade".equalsIgnoreCase(type) || url.contains("academicresult") || url.contains("hoctap") || url.contains("marks")) {
            imageView.setImageResource(R.drawable.ic_menu_grade);
        } else if ("CreditCard".equalsIgnoreCase(type) || url.contains("finance") || url.contains("taichinh")) {
            imageView.setImageResource(R.drawable.ic_menu_card);
        } else if ("FileText".equalsIgnoreCase(type) || url.contains("invoicedetail") || url.contains("hoadon")) {
            imageView.setImageResource(R.drawable.ic_menu_invoice);
        } else if ("MessageSquare".equalsIgnoreCase(type) || url.contains("discussion") || url.contains("thaoluan")) {
            imageView.setImageResource(R.drawable.ic_menu_discussion);
        } else if ("Mail".equalsIgnoreCase(type) || url.contains("contact") || url.contains("gopy")) {
            imageView.setImageResource(R.drawable.ic_menu_email);
        } else {
            imageView.setImageResource(R.drawable.ic_menu_clipboard);
        }
    }

    protected void showNotificationPopup(List<StudentMessage> messages) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_notification_popup, null);

        TextView tvSubject = popupView.findViewById(R.id.tvNotificationSubject);
        TextView tvSeeMore = popupView.findViewById(R.id.tvNotificationSeeMore);

        if (messages != null && !messages.isEmpty()) {
            StudentMessage latest = messages.get(0);
            tvSubject.setText(formatValue(latest.getMessageSubject()));
        } else {
            tvSubject.setText(getString(R.string.text_no_new_notifications));
        }

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                dpToPx(300),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setElevation(16f);
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvSeeMore.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (!getClass().getSimpleName().contains("NotificationIndexActivity")) {
                startActivity(new Intent(this, NotificationIndexActivity.class));
            }
        });

        if (btnNotification != null) {
            popupWindow.showAsDropDown(btnNotification, -dpToPx(240), dpToPx(8));
        }
    }

    protected void showAccountPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_account_popup, null);

        TextView tvHeader = popupView.findViewById(R.id.tvAccountStudentHeader);
        LinearLayout btnMyProfile = popupView.findViewById(R.id.btnAccountMyProfile);
        LinearLayout btnChangePassword = popupView.findViewById(R.id.btnAccountChangePassword);
        MaterialButton btnLogout = popupView.findViewById(R.id.btnAccountLogout);

        SessionManager session = SessionManager.getInstance();
        String name = session.getStudentName();
        String id = session.getStudentId();
        String headerText;
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id) && !name.equalsIgnoreCase(id)) {
            headerText = name + "-" + id;
        } else if (!TextUtils.isEmpty(name)) {
            headerText = name;
        } else if (!TextUtils.isEmpty(id)) {
            headerText = id;
        } else {
            headerText = "";
        }
        if (tvHeader != null) {
            tvHeader.setText(headerText);
        }

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                dpToPx(260),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setElevation(16f);
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnMyProfile.setOnClickListener(v -> {
            popupWindow.dismiss();
            if (!getClass().getSimpleName().contains("StudentActivity")) {
                startActivity(new Intent(this, StudentActivity.class));
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            popupWindow.dismiss();
            showChangePasswordDialog();
        });

        btnLogout.setOnClickListener(v -> {
            popupWindow.dismiss();
            SessionManager.getInstance().clearSession();
            Toast.makeText(this, getString(R.string.btn_logout), Toast.LENGTH_SHORT).show();
            redirectToLogin();
        });

        if (btnAccount != null) {
            popupWindow.showAsDropDown(btnAccount, -dpToPx(210), dpToPx(8));
        }
    }

    protected void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

        com.google.android.material.textfield.TextInputLayout tilOldPassword = dialogView.findViewById(R.id.tilOldPassword);
        com.google.android.material.textfield.TextInputLayout tilNewPassword = dialogView.findViewById(R.id.tilNewPassword);
        com.google.android.material.textfield.TextInputLayout tilConfirmNewPassword = dialogView.findViewById(R.id.tilConfirmNewPassword);

        com.google.android.material.textfield.TextInputEditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        com.google.android.material.textfield.TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        com.google.android.material.textfield.TextInputEditText etConfirmNewPassword = dialogView.findViewById(R.id.etConfirmNewPassword);

        MaterialButton btnSubmit = dialogView.findViewById(R.id.btnSubmitChangePassword);

        clearErrorOnTextChange(etOldPassword, tilOldPassword);
        clearErrorOnTextChange(etNewPassword, tilNewPassword);
        clearErrorOnTextChange(etConfirmNewPassword, tilConfirmNewPassword);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                if (tilOldPassword != null) tilOldPassword.setError(null);
                if (tilNewPassword != null) tilNewPassword.setError(null);
                if (tilConfirmNewPassword != null) tilConfirmNewPassword.setError(null);

                String oldPwd = etOldPassword != null && etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
                String newPwd = etNewPassword != null && etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
                String confirmPwd = etConfirmNewPassword != null && etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

                boolean hasError = false;

                if (TextUtils.isEmpty(oldPwd)) {
                    if (tilOldPassword != null) tilOldPassword.setError(getString(R.string.err_old_password_required));
                    hasError = true;
                }

                if (TextUtils.isEmpty(newPwd)) {
                    if (tilNewPassword != null) tilNewPassword.setError(getString(R.string.err_new_password_required));
                    hasError = true;
                }

                if (TextUtils.isEmpty(confirmPwd)) {
                    if (tilConfirmNewPassword != null) tilConfirmNewPassword.setError(getString(R.string.err_confirm_password_required));
                    hasError = true;
                } else if (!newPwd.equals(confirmPwd)) {
                    if (tilConfirmNewPassword != null) tilConfirmNewPassword.setError(getString(R.string.err_confirm_password_mismatch));
                    hasError = true;
                }

                if (hasError) return;

                btnSubmit.setEnabled(false);

                vn.edu.mku.portal.data.network.model.ChangePasswordApiRequest request =
                        new vn.edu.mku.portal.data.network.model.ChangePasswordApiRequest(oldPwd, newPwd);

                studentRepository.changePassword(request, new StudentRepository.ApiCallback<>() {
                    @Override
                    public void onSuccess(vn.edu.mku.portal.data.network.model.ChangePasswordApiResponse result) {
                        btnSubmit.setEnabled(true);
                        String msg = result != null && !TextUtils.isEmpty(result.getMessage()) ? result.getMessage() : "Cập nhật mật khẩu thành công...";
                        Toast.makeText(BaseStudentActivity.this, msg, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        btnSubmit.setEnabled(true);
                        if (errorMessage != null && (errorMessage.toLowerCase().contains("cũ") || errorMessage.toLowerCase().contains("old"))) {
                            if (tilOldPassword != null) tilOldPassword.setError(errorMessage);
                        } else {
                            Toast.makeText(BaseStudentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            });
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            int dpWidth = dpToPx(340);
            dialog.getWindow().setLayout(dpWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void clearErrorOnTextChange(com.google.android.material.textfield.TextInputEditText editText, com.google.android.material.textfield.TextInputLayout inputLayout) {
        if (editText == null || inputLayout == null) return;
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputLayout.getError() != null) {
                    inputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    protected void showLanguagePopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_language_popup, null);

        LinearLayout btnEnglish = popupView.findViewById(R.id.btnLangEnglish);
        LinearLayout btnVietnamese = popupView.findViewById(R.id.btnLangVietnamese);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                dpToPx(200),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setElevation(16f);
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnEnglish.setOnClickListener(v -> {
            popupWindow.dismiss();
            LanguageManager.getInstance().setCurrentLanguage(LanguageManager.LANG_EN);
            Toast.makeText(this, getString(R.string.lang_switched_en), Toast.LENGTH_SHORT).show();
            recreate();
        });

        btnVietnamese.setOnClickListener(v -> {
            popupWindow.dismiss();
            LanguageManager.getInstance().setCurrentLanguage(LanguageManager.LANG_VI);
            Toast.makeText(this, getString(R.string.lang_switched_vi), Toast.LENGTH_SHORT).show();
            recreate();
        });

        if (imgFlag != null) {
            popupWindow.showAsDropDown(imgFlag, -dpToPx(150), dpToPx(8));
        }
    }

    protected void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);

        AutoCompleteTextView spinnerTheme = dialogView.findViewById(R.id.spinnerTheme);
        com.google.android.material.materialswitch.MaterialSwitch switchRtl = dialogView.findViewById(R.id.switchRtl);
        com.google.android.material.materialswitch.MaterialSwitch switchResponsiveFont = dialogView.findViewById(R.id.switchResponsiveFont);
        com.google.android.material.materialswitch.MaterialSwitch switchCompact = dialogView.findViewById(R.id.switchCompact);
        com.google.android.material.materialswitch.MaterialSwitch switchRoundedCorners = dialogView.findViewById(R.id.switchRoundedCorners);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSaveSettings);

        SettingsManager sm = SettingsManager.getInstance();

        String[] themes = new String[]{getString(R.string.theme_light), getString(R.string.theme_dark), getString(R.string.theme_system)};
        if (spinnerTheme != null) {
            String currentTheme = sm.getTheme();
            int selectedIndex = SettingsManager.THEME_DARK.equalsIgnoreCase(currentTheme) ? 1 :
                    (SettingsManager.THEME_SYSTEM.equalsIgnoreCase(currentTheme) ? 2 : 0);

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
                String savedTheme = (getString(R.string.theme_dark).equalsIgnoreCase(selectedThemeStr) || SettingsManager.THEME_DARK.equalsIgnoreCase(selectedThemeStr))
                        ? SettingsManager.THEME_DARK
                        : ((getString(R.string.theme_system).equalsIgnoreCase(selectedThemeStr) || SettingsManager.THEME_SYSTEM.equalsIgnoreCase(selectedThemeStr))
                        ? SettingsManager.THEME_SYSTEM : SettingsManager.THEME_LIGHT);

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
            dialog.getWindow().setLayout(dpToPx(340), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    protected void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected abstract void onLanguageChanged();

    protected int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    protected String formatValue(String val) {
        return (!TextUtils.isEmpty(val) && !"null".equalsIgnoreCase(val)) ? val : "";
    }

    protected void renderHtmlText(TextView textView, String text) {
        if (textView == null) return;
        String val = formatValue(text);
        if (val.contains("<") && val.contains(">")) {
            textView.setText(android.text.Html.fromHtml(val, android.text.Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(val);
        }
    }
}