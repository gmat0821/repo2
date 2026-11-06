package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.adapters.ConversationAdapter;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.models.Conversation;
import com.example.clonewhatsapp.utils.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        TextView btnProfile = findViewById(R.id.btnMainProfile);
        TextView btnLogout = findViewById(R.id.btnMainLogout);
        Button btnNewChat = findViewById(R.id.btnNewChat);
        RecyclerView rvConversations = findViewById(R.id.rvConversations);

        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(this, conversationList, item -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("CONVERSATION_ID", item.getId());
            intent.putExtra("OTHER_USER_ID", item.getOtherUserId());
            intent.putExtra("OTHER_USER_NAME", item.getOtherUserName());
            intent.putExtra("OTHER_USER_AVATAR", item.getOtherUserAvatar());
            startActivity(intent);
        });

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(adapter);

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        }
        if (btnNewChat != null) {
            btnNewChat.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));
        }
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performLogout());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        String token = sessionManager.getToken();
        String currentUserId = sessionManager.getUserId();

        if (token == null) return;

        SupabaseClient.getUserConversations(token, currentUserId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();

                runOnUiThread(() -> {
                    try {
                        if (body.startsWith("[")) {
                            JSONArray array = new JSONArray(body);
                            conversationList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String convId = obj.optString("conversation_id", "");

                                conversationList.add(new Conversation(
                                        convId,
                                        "",
                                        "Chat " + (i + 1),
                                        "",
                                        "Toca para abrir chat...",
                                        "Ahora"
                                ));
                            }
                            adapter.updateList(conversationList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void performLogout() {
        String token = sessionManager.getToken();
        if (token != null) {
            SupabaseClient.signOut(token, new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) { runOnUiThread(() -> clearAndExit()); }
                @Override public void onResponse(@NonNull Call call, @NonNull Response response) { runOnUiThread(() -> clearAndExit()); }
            });
        } else {
            clearAndExit();
        }
    }

    private void clearAndExit() {
        sessionManager.clearSession();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}