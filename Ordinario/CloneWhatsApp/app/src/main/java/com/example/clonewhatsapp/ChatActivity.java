package com.example.clonewhatsapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String conversationId;
    private String otherUserId;
    private SessionManager sessionManager;

    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText etInput;
    private TextView tvChatStatus;

    private WebSocket webSocket;
    private final Handler handler = new Handler();
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);

        conversationId = getIntent().getStringExtra("CONVERSATION_ID");
        otherUserId = getIntent().getStringExtra("OTHER_USER_ID");
        String otherUserName = getIntent().getStringExtra("OTHER_USER_NAME");
        String otherUserAvatar = getIntent().getStringExtra("OTHER_USER_AVATAR");

        ImageView imgAvatar = findViewById(R.id.imgChatAvatar);
        TextView tvChatName = findViewById(R.id.tvChatName);
        tvChatStatus = findViewById(R.id.tvChatStatus);
        rvMessages = findViewById(R.id.rvMessages);
        etInput = findViewById(R.id.etMessageInput);
        Button btnSend = findViewById(R.id.btnSend);

        tvChatName.setText(otherUserName != null ? otherUserName : "Chat");
        if (otherUserAvatar != null && !otherUserAvatar.isEmpty()) {
            Glide.with(this).load(otherUserAvatar).placeholder(R.mipmap.ic_launcher_round).into(imgAvatar);
        }

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, sessionManager.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(messageAdapter);

        loadMessages();
        startRealtimeAndPolling();

        btnSend.setOnClickListener(v -> sendMessage());

        // Indicador de escritura (Fase 8)
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String target = s.length() > 0 ? conversationId : null;
                SupabaseClient.updateTypingStatus(sessionManager.getToken(), sessionManager.getUserId(), target, new Callback() {
                    @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                    @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
                });
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
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
                                    obj.optString("status", "sent")
                            ));
                        }
                        messageAdapter.updateList(messageList);
                        if (!messageList.isEmpty()) {
                            rvMessages.scrollToPosition(messageList.size() - 1);
                        }
                        markAsRead();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty() || conversationId == null) return;

        etInput.setText("");
        SupabaseClient.sendMessage(sessionManager.getToken(), conversationId, sessionManager.getUserId(), content, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Error enviando mensaje", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> loadMessages());
            }
        });
    }

    private void markAsRead() {
        if (conversationId == null) return;
        SupabaseClient.markMessagesAsRead(sessionManager.getToken(), conversationId, sessionManager.getUserId(), new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
        });
    }

    private void startRealtimeAndPolling() {
        // Suscripción Realtime por WebSockets (Fase 7)
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                runOnUiThread(() -> loadMessages());
            }
        });

        // Polling de respaldo cada 3s para sincronización instantánea y estados
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                checkOtherUserStatus();
                handler.postDelayed(this, 3000);
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

                            if (conversationId.equals(typingIn)) {
                                tvChatStatus.setText("Escribiendo...");
                            } else {
                                tvChatStatus.setText("En línea");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) webSocket.close(1000, "Activity destroyed");
        if (pollingRunnable != null) handler.removeCallbacks(pollingRunnable);
        SupabaseClient.updateTypingStatus(sessionManager.getToken(), sessionManager.getUserId(), null, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
        });
    }
}