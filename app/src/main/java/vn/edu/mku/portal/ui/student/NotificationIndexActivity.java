/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
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

    private LinearLayout layoutSkeletonNotifications;
    private androidx.recyclerview.widget.RecyclerView rvNotifications;
    private TextView tvEmptyNotifications;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_index);

        setupCommonUi();

        layoutSkeletonNotifications = findViewById(R.id.layoutSkeletonNotifications);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);

        notificationAdapter = new NotificationAdapter(this::showMessageDetailDialog);
        if (rvNotifications != null) {
            rvNotifications.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            rvNotifications.setAdapter(notificationAdapter);
        }

        fetchNotificationList();
    }

    private void fetchNotificationList() {
        showSkeletonRows();
        studentRepository.fetchMessages(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudentMessage> result) {
                if (layoutSkeletonNotifications != null) {
                    SkeletonHelper.stopPulseAnimation(layoutSkeletonNotifications);
                }
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
                if (layoutSkeletonNotifications != null) {
                    SkeletonHelper.stopPulseAnimation(layoutSkeletonNotifications);
                    layoutSkeletonNotifications.setVisibility(View.GONE);
                }
                Snackbar.make(findViewById(R.id.mainCoordinator), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showSkeletonRows() {
        if (layoutSkeletonNotifications == null) return;
        layoutSkeletonNotifications.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View skeletonRow = LayoutInflater.from(this).inflate(R.layout.item_notification_skeleton_row, layoutSkeletonNotifications, false);
            layoutSkeletonNotifications.addView(skeletonRow);
        }
        layoutSkeletonNotifications.setVisibility(View.VISIBLE);
        if (rvNotifications != null) rvNotifications.setVisibility(View.GONE);
        if (tvEmptyNotifications != null) tvEmptyNotifications.setVisibility(View.GONE);
        SkeletonHelper.startPulseAnimation(layoutSkeletonNotifications);
    }

    private void populateNotificationRows(List<StudentMessage> list) {
        if (layoutSkeletonNotifications != null) {
            SkeletonHelper.stopPulseAnimation(layoutSkeletonNotifications);
            layoutSkeletonNotifications.setVisibility(View.GONE);
        }

        if (list == null || list.isEmpty()) {
            if (rvNotifications != null) rvNotifications.setVisibility(View.GONE);
            if (tvEmptyNotifications != null) tvEmptyNotifications.setVisibility(View.VISIBLE);
            if (notificationAdapter != null) notificationAdapter.submitList(null);
            return;
        }

        if (tvEmptyNotifications != null) tvEmptyNotifications.setVisibility(View.GONE);
        if (rvNotifications != null) {
            rvNotifications.setVisibility(View.VISIBLE);
        }
        if (notificationAdapter != null) {
            notificationAdapter.submitList(list);
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