package com.example.clonewhatsapp.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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

    private MediaPlayer activeMediaPlayer;
    private ImageButton activePlayButton;
    private SeekBar activeSeekBar;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

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
        return messageList.get(position).getSenderId().equals(currentUserId) ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(viewType == TYPE_SENT ? R.layout.item_message_sent : R.layout.item_message_received, parent, false);
        return viewType == TYPE_SENT ? new SentViewHolder(v) : new ReceivedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messageList.get(position);
        String timeStr = msg.getCreatedAt().length() >= 16 ? msg.getCreatedAt().substring(11, 16) : msg.getCreatedAt();

        if (holder instanceof SentViewHolder) {
            SentViewHolder h = (SentViewHolder) holder;
            if ("audio".equals(msg.getType())) {
                h.tvContent.setVisibility(View.GONE);
                h.layoutAudio.setVisibility(View.VISIBLE);
                h.btnPlayAudio.setOnClickListener(v -> toggleAudio(msg.getContent(), h.btnPlayAudio, h.seekBarAudio, h.tvAudioDuration));
            } else {
                h.tvContent.setVisibility(View.VISIBLE);
                h.tvContent.setText(msg.getContent());
                h.layoutAudio.setVisibility(View.GONE);
            }
            h.tvTime.setText(timeStr);
            h.tvStatus.setText("read".equals(msg.getStatus()) ? " ✔✔" : " ✔");
        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder h = (ReceivedViewHolder) holder;
            if ("audio".equals(msg.getType())) {
                h.tvContent.setVisibility(View.GONE);
                h.layoutAudio.setVisibility(View.VISIBLE);
                h.btnPlayAudio.setOnClickListener(v -> toggleAudio(msg.getContent(), h.btnPlayAudio, h.seekBarAudio, h.tvAudioDuration));
            } else {
                h.tvContent.setVisibility(View.VISIBLE);
                h.tvContent.setText(msg.getContent());
                h.layoutAudio.setVisibility(View.GONE);
            }
            h.tvTime.setText(timeStr);
        }
    }

    private void toggleAudio(String url, ImageButton playBtn, SeekBar seekBar, TextView durationTv) {
        if (activeMediaPlayer != null && activeMediaPlayer.isPlaying()) {
            activeMediaPlayer.stop();
            activeMediaPlayer.release();
            activeMediaPlayer = null;
            if (activePlayButton != null) activePlayButton.setImageResource(android.R.drawable.ic_media_play);
            if (progressRunnable != null) progressHandler.removeCallbacks(progressRunnable);
            if (activePlayButton == playBtn) return;
        }

        try {
            activeMediaPlayer = new MediaPlayer();
            activePlayButton = playBtn;
            activeSeekBar = seekBar;

            activeMediaPlayer.setDataSource(url);
            activeMediaPlayer.prepareAsync();

            activeMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                playBtn.setImageResource(android.R.drawable.ic_media_pause);
                seekBar.setMax(mp.getDuration());

                progressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (activeMediaPlayer != null && activeMediaPlayer.isPlaying()) {
                            int current = activeMediaPlayer.getCurrentPosition();
                            seekBar.setProgress(current);

                            int mins = (current / 1000) / 60;
                            int secs = (current / 1000) % 60;
                            durationTv.setText(String.format("🎙️ %02d:%02d", mins, secs));

                            progressHandler.postDelayed(this, 200);
                        }
                    }
                };
                progressHandler.post(progressRunnable);
            });

            activeMediaPlayer.setOnCompletionListener(mp -> {
                playBtn.setImageResource(android.R.drawable.ic_media_play);
                seekBar.setProgress(0);
                durationTv.setText("🎙️ Nota de voz");
                progressHandler.removeCallbacks(progressRunnable);
            });

        } catch (Exception e) {
            Toast.makeText(context, "Error al reproducir nota de voz", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime, tvStatus, tvAudioDuration;
        LinearLayout layoutAudio;
        ImageButton btnPlayAudio;
        SeekBar seekBarAudio;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvSentContent);
            tvTime = itemView.findViewById(R.id.tvSentTime);
            tvStatus = itemView.findViewById(R.id.tvSentStatus);
            layoutAudio = itemView.findViewById(R.id.layoutAudioSent);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudioSent);
            seekBarAudio = itemView.findViewById(R.id.seekBarAudioSent);
            tvAudioDuration = itemView.findViewById(R.id.tvAudioDurationSent);
        }
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime, tvAudioDuration;
        LinearLayout layoutAudio;
        ImageButton btnPlayAudio;
        SeekBar seekBarAudio;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvReceivedContent);
            tvTime = itemView.findViewById(R.id.tvReceivedTime);
            layoutAudio = itemView.findViewById(R.id.layoutAudioReceived);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudioRecv);
            seekBarAudio = itemView.findViewById(R.id.seekBarAudioRecv);
            tvAudioDuration = itemView.findViewById(R.id.tvAudioDurationRecv);
        }
    }
}