package com.example.clonewhatsapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.adapters.MessageAdapter;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.models.Message;
import com.example.clonewhatsapp.utils.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String conversationId, otherUserId;
    private SessionManager sessionManager;
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText etInput;
    private TextView tvChatName, tvChatStatus, tvRecordingTimer;
    private ImageView imgChatAvatar, imgRecordingDot;
    private LinearLayout layoutTextInput, layoutRecordingInput;
    private ImageButton btnAudioRecord, btnStopRecord;

    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private int recordSeconds = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private ObjectAnimator pulseAnimatorX, pulseAnimatorY;

    private WebSocket webSocket;
    private final Handler handler = new Handler();
    private Runnable pollingRunnable, typingAnimationRunnable;
    private int typingDotCount = 0;
    private boolean isOtherUserTyping = false;
    private int previousMessageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);

        conversationId = getIntent().getStringExtra("CONVERSATION_ID");
        otherUserId = getIntent().getStringExtra("OTHER_USER_ID");
        String otherUserName = getIntent().getStringExtra("OTHER_USER_NAME");
        String otherUserAvatar = getIntent().getStringExtra("OTHER_USER_AVATAR");

        imgChatAvatar = findViewById(R.id.imgChatAvatar);
        tvChatName = findViewById(R.id.tvChatName);
        tvChatStatus = findViewById(R.id.tvChatStatus);
        rvMessages = findViewById(R.id.rvMessages);
        etInput = findViewById(R.id.etMessageInput);

        layoutTextInput = findViewById(R.id.layoutTextInput);
        layoutRecordingInput = findViewById(R.id.layoutRecordingInput);
        imgRecordingDot = findViewById(R.id.imgRecordingDot);
        tvRecordingTimer = findViewById(R.id.tvRecordingTimer);
        btnAudioRecord = findViewById(R.id.btnAudioRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);

        tvChatName.setText(otherUserName != null ? otherUserName : "Contacto");
        if (otherUserAvatar != null && !otherUserAvatar.trim().isEmpty()) {
            Glide.with(this)
                    .load(otherUserAvatar)
                    .placeholder(R.drawable.ic_chiikawa_logo)
                    .error(R.drawable.ic_chiikawa_logo)
                    .into(imgChatAvatar);
        }

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, sessionManager.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);

        // Control del teclado para mantener visible la barra y hacer scroll al escribir
        View rootLayout = findViewById(R.id.rootChatLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            if (heightDiff > 200 && !messageList.isEmpty()) {
                rvMessages.postDelayed(() -> rvMessages.smoothScrollToPosition(messageList.size() - 1), 100);
            }
        });

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/voice_temp.3gp";

        btnAudioRecord.setOnClickListener(v -> startRecordingAudio());
        btnStopRecord.setOnClickListener(v -> stopAndSendAudio());

        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());

        etInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String target = s.length() > 0 ? conversationId : null;
                SupabaseClient.updateTypingStatus(sessionManager.getToken(), sessionManager.getUserId(), target, new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                    @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
                });
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadOtherUserProfile();
        startTypingAnimation();
        loadMessages();
        startRealtimeAndPolling();
    }

    private void loadOtherUserProfile() {
        if (otherUserId == null || sessionManager.getToken() == null) return;
        SupabaseClient.getProfile(sessionManager.getToken(), otherUserId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();

                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(body);
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
                            String fullName = profile.optString("full_name", "");
                            String username = profile.optString("username", "");
                            String avatarUrl = profile.optString("avatar_url", "");

                            String nameToShow = (!fullName.trim().isEmpty()) ? fullName : username;
                            tvChatName.setText(nameToShow);

                            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                                Glide.with(ChatActivity.this)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.ic_chiikawa_logo)
                                        .error(R.drawable.ic_chiikawa_logo)
                                        .into(imgChatAvatar);
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    // ANIMACIÓN ESTILO WHATSAAP AL GRABAR AUDIO
    private void startRecordingAudio() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 101);
            return;
        }
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            // Transición de barras
            layoutTextInput.setVisibility(View.GONE);
            layoutRecordingInput.setVisibility(View.VISIBLE);

            // Iniciar animación pulsante del icono de grabación
            pulseAnimatorX = ObjectAnimator.ofFloat(imgRecordingDot, "scaleX", 1.0f, 1.4f, 1.0f);
            pulseAnimatorY = ObjectAnimator.ofFloat(imgRecordingDot, "scaleY", 1.0f, 1.4f, 1.0f);
            pulseAnimatorX.setDuration(800);
            pulseAnimatorY.setDuration(800);
            pulseAnimatorX.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnimatorY.setRepeatCount(ValueAnimator.INFINITE);
            pulseAnimatorX.start();
            pulseAnimatorY.start();

            // Iniciar cronómetro (00:00)
            recordSeconds = 0;
            tvRecordingTimer.setText("00:00");
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isRecording) {
                        recordSeconds++;
                        int mins = recordSeconds / 60;
                        int secs = recordSeconds % 60;
                        tvRecordingTimer.setText(String.format("%02d:%02d", mins, secs));
                        timerHandler.postDelayed(this, 1000);
                    }
                }
            };
            timerHandler.postDelayed(timerRunnable, 1000);

        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAndSendAudio() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }
            isRecording = false;

            // Detener animaciones y restaurar barra
            if (pulseAnimatorX != null) pulseAnimatorX.cancel();
            if (pulseAnimatorY != null) pulseAnimatorY.cancel();
            timerHandler.removeCallbacks(timerRunnable);

            layoutRecordingInput.setVisibility(View.GONE);
            layoutTextInput.setVisibility(View.VISIBLE);

            File file = new File(audioFilePath);
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();

            String filename = "audio_" + System.currentTimeMillis() + ".3gp";
            SupabaseClient.uploadFile("audios", filename, bytes, "audio/3gpp", new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    String audioUrl = SupabaseClient.getPublicUrl("audios", filename);
                    SupabaseClient.sendMessage(sessionManager.getToken(), conversationId, sessionManager.getUserId(), audioUrl, "audio", new Callback() {
                        @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                        @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                            runOnUiThread(() -> loadMessages());
                        }
                    });
                }
            });
        } catch (Exception e) {
            layoutRecordingInput.setVisibility(View.GONE);
            layoutTextInput.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Error al enviar audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTypingAnimation() {
        typingAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                if (isOtherUserTyping) {
                    typingDotCount = (typingDotCount % 3) + 1;
                    StringBuilder dots = new StringBuilder("Escribiendo");
                    for (int i = 0; i < typingDotCount; i++) dots.append(".");
                    tvChatStatus.setText(dots.toString());
                } else {
                    tvChatStatus.setText("En línea");
                }
                handler.postDelayed(this, 400);
            }
        };
        handler.post(typingAnimationRunnable);
    }

    private void loadMessages() {
        String token = sessionManager.getToken();
        if (token == null || conversationId == null) return;

        SupabaseClient.getMessages(token, conversationId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();

                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(body);
                        messageList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            messageList.add(new Message(
                                    obj.getString("id"),
                                    obj.getString("conversation_id"),
                                    obj.getString("sender_id"),
                                    obj.getString("content"),
                                    obj.optString("created_at", ""),
                                    obj.optString("status", "sent"),
                                    obj.optString("type", "text")
                            ));
                        }
                        messageAdapter.updateList(messageList);
                        if (!messageList.isEmpty()) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }

                        if (previousMessageCount > 0 && messageList.size() > previousMessageCount) {
                            Message lastMsg = messageList.get(messageList.size() - 1);
                            if (!lastMsg.getSenderId().equals(sessionManager.getUserId())) {
                                showNotification(lastMsg.getContent());
                            }
                        }
                        previousMessageCount = messageList.size();

                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty() || conversationId == null) return;
        etInput.setText("");

        SupabaseClient.sendMessage(sessionManager.getToken(), conversationId, sessionManager.getUserId(), content, "text", new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> loadMessages());
            }
        });
    }

    private void showNotification(String text) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "chat_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Mensajes", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_chiikawa_logo)
                .setContentTitle("Nuevo Mensaje Chiikawa 🌸")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void startRealtimeAndPolling() {
        webSocket = SupabaseClient.connectRealtime(new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                try {
                    JSONObject joinMsg = new JSONObject();
                    joinMsg.put("topic", "realtime:public:messages");
                    joinMsg.put("event", "phx_join");
                    joinMsg.put("payload", new JSONObject());
                    joinMsg.put("ref", "1");
                    webSocket.send(joinMsg.toString());
                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> loadMessages());
            }
        });

        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                checkOtherUserStatus();
                handler.postDelayed(this, 2500);
            }
        };
        handler.post(pollingRunnable);
    }

    private void checkOtherUserStatus() {
        if (otherUserId == null) return;
        SupabaseClient.getOtherUserStatus(sessionManager.getToken(), otherUserId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();
                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(body);
                        if (array.length() > 0) {
                            JSONObject profile = array.getJSONObject(0);
                            String typingIn = profile.optString("is_typing_in", "");
                            isOtherUserTyping = conversationId.equals(typingIn);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) webSocket.close(1000, "Destroyed");
        if (pollingRunnable != null) handler.removeCallbacks(pollingRunnable);
        if (typingAnimationRunnable != null) handler.removeCallbacks(typingAnimationRunnable);
        if (timerRunnable != null) timerHandler.removeCallbacks(timerRunnable);
    }
}