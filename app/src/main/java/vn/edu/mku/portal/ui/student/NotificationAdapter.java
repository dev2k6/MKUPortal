package vn.edu.mku.portal.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import vn.edu.mku.portal.R;
import vn.edu.mku.portal.data.network.model.StudentMessage;

public class NotificationAdapter extends ListAdapter<StudentMessage, NotificationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(StudentMessage item);
    }

    private final OnItemClickListener listener;

    public NotificationAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<StudentMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull StudentMessage oldItem, @NonNull StudentMessage newItem) {
            if (oldItem.getMessageId() != 0 && newItem.getMessageId() != 0) {
                return oldItem.getMessageId() == newItem.getMessageId();
            }
            return Objects.equals(oldItem.getMessageSubject(), newItem.getMessageSubject())
                    && Objects.equals(oldItem.getCreationDate(), newItem.getCreationDate());
        }

        @Override
        public boolean areContentsTheSame(@NonNull StudentMessage oldItem, @NonNull StudentMessage newItem) {
            return oldItem.getMessageId() == newItem.getMessageId()
                    && Objects.equals(oldItem.getMessageSubject(), newItem.getMessageSubject())
                    && Objects.equals(oldItem.getSenderName(), newItem.getSenderName())
                    && Objects.equals(oldItem.getCreationDate(), newItem.getCreationDate())
                    && oldItem.getIsRead() == newItem.getIsRead();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentMessage item = getItem(position);
        holder.bind(item, listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSubject;
        private final TextView tvSender;
        private final TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvMessageSubject);
            tvSender = itemView.findViewById(R.id.tvSenderName);
            tvDate = itemView.findViewById(R.id.tvCreationDate);
        }

        public void bind(StudentMessage item, OnItemClickListener listener) {
            if (item == null) return;
            String subj = (item.getMessageSubject() != null && !item.getMessageSubject().trim().isEmpty())
                    ? item.getMessageSubject() : "-";
            String sender = (item.getSenderName() != null && !item.getSenderName().trim().isEmpty())
                    ? item.getSenderName() : "-";
            String date = (item.getCreationDate() != null && !item.getCreationDate().trim().isEmpty())
                    ? item.getCreationDate() : "-";

            if (tvSubject != null) {
                tvSubject.setText(subj);
                tvSubject.setOnClickListener(v -> {
                    if (listener != null) listener.onItemClick(item);
                });
            }
            if (tvSender != null) {
                tvSender.setText(sender);
            }
            if (tvDate != null) {
                tvDate.setText(date);
            }
        }
    }
}
