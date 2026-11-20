package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        TextView btnMenuMore = findViewById(R.id.btnMenuMore);
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

        btnMenuMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenu().add("Ver Perfil");
            popup.getMenu().add("Cerrar Sesión");
            popup.setOnMenuItemClickListener(item -> {
                if ("Ver Perfil".equals(item.getTitle())) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else if ("Cerrar Sesión".equals(item.getTitle())) {
                    performLogout();
                }
                return true;
            });
            popup.show();
        });

        if (btnNewChat != null) {
            btnNewChat.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContactsActivity.class)));
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
        if (token == null || currentUserId.isEmpty()) return;

        SupabaseClient.getUserConversationsMembers(token, currentUserId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();

                try {
                    JSONArray array = new JSONArray(body);
                    if (array.length() == 0) {
                        runOnUiThread(() -> {
                            conversationList.clear();
                            adapter.updateList(conversationList);
                        });
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        String cId = item.getString("conversation_id");
                        sb.append("\"").append(cId).append("\"");
                        if (i < array.length() - 1) sb.append(",");
                    }

                    SupabaseClient.getMembersOfConversations(token, sb.toString(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {}

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            ResponseBody rBody = response.body();
                            if (rBody == null) return;
                            String mBody = rBody.string();

                            runOnUiThread(() -> {
                                try {
                                    JSONArray mArray = new JSONArray(mBody);
                                    List<Conversation> finalConversations = new ArrayList<>();
                                    Set<String> seenUserIds = new HashSet<>();

                                    for (int i = 0; i < mArray.length(); i++) {
                                        JSONObject obj = mArray.getJSONObject(i);
                                        String convId = obj.getString("conversation_id");
                                        String userId = obj.getString("user_id");

                                        // Evita mostrarte a ti mismo y bloquea contactos duplicados
                                        if (!userId.equals(currentUserId) && !seenUserIds.contains(userId)) {
                                            seenUserIds.add(userId);

                                            JSONObject profile = obj.optJSONObject("profiles");
                                            String name = "Contacto";
                                            String avatar = "";

                                            if (profile != null) {
                                                String fullName = profile.optString("full_name", "");
                                                String username = profile.optString("username", "");
                                                name = !fullName.trim().isEmpty() ? fullName : username;
                                                avatar = profile.optString("avatar_url", "");
                                            }

                                            finalConversations.add(new Conversation(
                                                    convId,
                                                    userId,
                                                    name,
                                                    avatar,
                                                    "Toca para ver mensajes...",
                                                    "Ahora"
                                            ));
                                        }
                                    }

                                    conversationList.clear();
                                    conversationList.addAll(finalConversations);
                                    adapter.updateList(conversationList);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void performLogout() {
        String token = sessionManager.getToken();
        if (token != null) {
            SupabaseClient.signOut(token, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> clearAndExit());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    runOnUiThread(() -> clearAndExit());
                }
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