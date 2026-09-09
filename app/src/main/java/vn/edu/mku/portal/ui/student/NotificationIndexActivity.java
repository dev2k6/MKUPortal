package vn.edu.mku.portal.ui.student;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.ui.common.SkeletonHelper;

public class NotificationIndexActivity extends BaseStudentActivity {

    private LinearLayout containerNotificationRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_index);

        setupCommonUi();

        containerNotificationRows = findViewById(R.id.containerNotificationRows);

        fetchNotificationList();
    }

    private void fetchNotificationList() {
        showSkeletonRows();
        studentRepository.fetchMessages(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudentMessage> result) {
                SkeletonHelper.stopPulseAnimation(containerNotificationRows);
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

                populateNotificationRows(result);
            }

            @Override
            public void onError(String errorMessage) {
                SkeletonHelper.stopPulseAnimation(containerNotificationRows);
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (containerNotificationRows == null) return;
        containerNotificationRows.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, containerNotificationRows, false);
            containerNotificationRows.addView(skeletonRow);
        }
        SkeletonHelper.startPulseAnimation(containerNotificationRows);
    }

    private void populateNotificationRows(List<StudentMessage> list) {
        if (containerNotificationRows == null) return;
        containerNotificationRows.removeAllViews();

        if (list == null || list.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            tvEmpty.setText(getString(R.string.text_no_new_notifications));
            tvEmpty.setTextColor(getColor(R.color.mku_text_sub));
            containerNotificationRows.addView(tvEmpty);
            return;
        }

        for (StudentMessage msg : list) {
            View rowView = LayoutInflater.from(this).inflate(R.layout.item_notification_row, containerNotificationRows, false);
            TextView tvSubject = rowView.findViewById(R.id.tvMessageSubject);
            TextView tvSender = rowView.findViewById(R.id.tvSenderName);
            TextView tvDate = rowView.findViewById(R.id.tvCreationDate);

            if (tvSubject != null) {
                tvSubject.setText(formatValue(msg.getMessageSubject()));
                tvSubject.setOnClickListener(v -> showMessageDetailDialog(msg));
            }
            if (tvSender != null) {
                tvSender.setText(formatValue(msg.getSenderName()));
            }
            if (tvDate != null) {
                tvDate.setText(formatValue(msg.getCreationDate()));
            }

            containerNotificationRows.addView(rowView);
        }
    }

    private void showMessageDetailDialog(StudentMessage msg) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notification_detail, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDetailTitle);
        TextView tvSub = dialogView.findViewById(R.id.tvDetailSub);
        TextView tvBody = dialogView.findViewById(R.id.tvDetailBody);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseDetail);

        if (tvTitle != null) {
            tvTitle.setText(formatValue(msg.getMessageSubject()));
        }
        if (tvSub != null) {
            String sender = formatValue(msg.getSenderName());
            String date = formatValue(msg.getCreationDate());
            tvSub.setText(getString(R.string.msg_sender_date, sender, date));
        }
        if (tvBody != null) {
            String bodyHtml = formatValue(msg.getMessageBody());
            if (!TextUtils.isEmpty(bodyHtml)) {
                tvBody.setText(Html.fromHtml(bodyHtml, Html.FROM_HTML_MODE_LEGACY));
                tvBody.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvBody.setText("");
            }
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(dpToPx(340), ViewGroup.LayoutParams.WRAP_CONTENT);
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
            tvHeaderTitle.setText(lm.getString("NotificationComponent", "Notification", getString(R.string.title_general_notifications)));
        }
        TextView tvColTitle = findViewById(R.id.tvColTitle);
        if (tvColTitle != null) {
            tvColTitle.setText(lm.getString("NotificationComponent", "Title", getString(R.string.label_notification_title)));
        }
        TextView tvColSender = findViewById(R.id.tvColSender);
        if (tvColSender != null) {
            tvColSender.setText(lm.getString("NotificationComponent", "Sender", getString(R.string.label_notification_sender)));
        }
        TextView tvColSentTime = findViewById(R.id.tvColSentTime);
        if (tvColSentTime != null) {
            tvColSentTime.setText(lm.getString("NotificationComponent", "SentTime", getString(R.string.label_notification_sent_time)));
        }
    }
}