package com.example.clonewhatsapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.models.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final Context context;
    private List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    public void updateList(List<Message> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messageList.get(position);
        String timeStr = msg.getCreatedAt().length() >= 16 ? msg.getCreatedAt().substring(11, 16) : msg.getCreatedAt();

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.tvContent.setText(msg.getContent());
            sentHolder.tvTime.setText(timeStr);

            // Fase 8: Estado de mensaje
            String status = msg.getStatus();
            if ("read".equals(status)) {
                sentHolder.tvStatus.setText(" ✔✔");
                sentHolder.tvStatus.setTextColor(Color.parseColor("#34B7F1")); // Azul (Leído)
            } else if ("delivered".equals(status)) {
                sentHolder.tvStatus.setText(" ✔✔");
                sentHolder.tvStatus.setTextColor(Color.GRAY);
            } else {
                sentHolder.tvStatus.setText(" ✔");
                sentHolder.tvStatus.setTextColor(Color.GRAY);
            }
        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder recvHolder = (ReceivedViewHolder) holder;
            recvHolder.tvContent.setText(msg.getContent());
            recvHolder.tvTime.setText(timeStr);
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime, tvStatus;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvSentContent);
            tvTime = itemView.findViewById(R.id.tvSentTime);
            tvStatus = itemView.findViewById(R.id.tvSentStatus);
        }
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvReceivedContent);
            tvTime = itemView.findViewById(R.id.tvReceivedTime);
        }
    }
}