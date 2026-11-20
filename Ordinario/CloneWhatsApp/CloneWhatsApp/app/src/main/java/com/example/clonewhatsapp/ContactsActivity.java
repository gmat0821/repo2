package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clonewhatsapp.adapters.UserAdapter;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.models.User;
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

public class ContactsActivity extends AppCompatActivity {

    private UserAdapter userAdapter;
    private List<User> userList;
    private SessionManager sessionManager;
    private TextView tvContactCount;
    private boolean isOpeningChat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        sessionManager = new SessionManager(this);
        RecyclerView rvContacts = findViewById(R.id.rvContacts);
        EditText etSearchUser = findViewById(R.id.etSearchUser);
        TextView btnBack = findViewById(R.id.btnBackContacts);
        tvContactCount = findViewById(R.id.tvContactCount);

        btnBack.setOnClickListener(v -> finish());

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, this::handleContactSelected);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(userAdapter);

        loadUsers();

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) loadUsers();
                else searchUsers(query);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpeningChat = false;
    }

    private void handleContactSelected(User targetUser) {
        if (isOpeningChat) return;

        String token = sessionManager.getToken();
        String currentUserId = sessionManager.getUserId();
        if (token == null || currentUserId.isEmpty()) return;

        isOpeningChat = true;

        // 1. Consultar conversaciones actuales del usuario para verificar si ya existe una con targetUser
        SupabaseClient.getUserConversationsMembers(token, currentUserId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    isOpeningChat = false;
                    Toast.makeText(ContactsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String body = responseBody != null ? responseBody.string() : "";

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        isOpeningChat = false;
                        Toast.makeText(ContactsActivity.this, "Error verificando chats", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                try {
                    JSONArray array = new JSONArray(body);
                    if (array.length() == 0) {
                        // No tiene ningún chat todavía -> crear chat nuevo
                        createNewConversation(token, currentUserId, targetUser);
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.length(); i++) {
                        String cId = array.getJSONObject(i).getString("conversation_id");
                        sb.append("\"").append(cId).append("\"");
                        if (i < array.length() - 1) sb.append(",");
                    }

                    // 2. Revisar miembros de esos chats para ver si el contacto ya está en alguno
                    SupabaseClient.getMembersOfConversations(token, sb.toString(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(() -> {
                                isOpeningChat = false;
                                Toast.makeText(ContactsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            ResponseBody mBodyObj = response.body();
                            String mBody = mBodyObj != null ? mBodyObj.string() : "";

                            try {
                                JSONArray mArray = new JSONArray(mBody);
                                String existingConversationId = null;

                                for (int i = 0; i < mArray.length(); i++) {
                                    JSONObject obj = mArray.getJSONObject(i);
                                    if (targetUser.getId().equals(obj.getString("user_id"))) {
                                        existingConversationId = obj.getString("conversation_id");
                                        break;
                                    }
                                }

                                if (existingConversationId != null) {
                                    // Ya existe un chat con este contacto -> abrir el existente
                                    final String finalConvId = existingConversationId;
                                    runOnUiThread(() -> openChatActivity(finalConvId, targetUser));
                                } else {
                                    // Contacto nuevo -> crear conversación nueva
                                    createNewConversation(token, currentUserId, targetUser);
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    isOpeningChat = false;
                                    Toast.makeText(ContactsActivity.this, "Error procesando contactos", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        isOpeningChat = false;
                        Toast.makeText(ContactsActivity.this, "Error procesando conversaciones", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void createNewConversation(String token, String currentUserId, User targetUser) {
        SupabaseClient.createConversation(token, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    isOpeningChat = false;
                    Toast.makeText(ContactsActivity.this, "Error creando conversación", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                String body = responseBody != null ? responseBody.string() : "";

                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        isOpeningChat = false;
                        Toast.makeText(ContactsActivity.this, "No se pudo crear el chat (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                try {
                    JSONArray array = new JSONArray(body);
                    if (array.length() > 0) {
                        String newConversationId = array.getJSONObject(0).getString("id");

                        // Registrar primero al usuario actual y luego al contacto antes de abrir el chat
                        SupabaseClient.addConversationMember(token, newConversationId, currentUserId, new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                runOnUiThread(() -> isOpeningChat = false);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) {
                                SupabaseClient.addConversationMember(token, newConversationId, targetUser.getId(), new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        runOnUiThread(() -> isOpeningChat = false);
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                                        runOnUiThread(() -> openChatActivity(newConversationId, targetUser));
                                    }
                                });
                            }
                        });
                    } else {
                        runOnUiThread(() -> isOpeningChat = false);
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> isOpeningChat = false);
                }
            }
        });
    }

    private void openChatActivity(String conversationId, User targetUser) {
        Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
        intent.putExtra("CONVERSATION_ID", conversationId);
        intent.putExtra("OTHER_USER_ID", targetUser.getId());
        String displayName = (targetUser.getFullName() != null && !targetUser.getFullName().trim().isEmpty())
                ? targetUser.getFullName()
                : targetUser.getUsername();
        intent.putExtra("OTHER_USER_NAME", displayName);
        intent.putExtra("OTHER_USER_AVATAR", targetUser.getAvatarUrl());
        startActivity(intent);
        finish();
    }

    private void loadUsers() {
        String token = sessionManager.getToken();
        String currentUserId = sessionManager.getUserId();
        if (token == null) return;

        SupabaseClient.getUsers(token, currentUserId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();
                runOnUiThread(() -> parseAndDisplayUsers(body));
            }
        });
    }

    private void searchUsers(String query) {
        String token = sessionManager.getToken();
        String currentUserId = sessionManager.getUserId();
        if (token == null) return;

        SupabaseClient.searchUsers(token, currentUserId, query, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();
                runOnUiThread(() -> parseAndDisplayUsers(body));
            }
        });
    }

    private void parseAndDisplayUsers(String jsonArrayString) {
        try {
            JSONArray array = new JSONArray(jsonArrayString);
            userList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                userList.add(new User(
                        obj.getString("id"),
                        obj.optString("username", ""),
                        obj.optString("full_name", ""),
                        obj.optString("avatar_url", "")
                ));
            }
            userAdapter.updateList(userList);
            tvContactCount.setText(userList.size() + " contactos");
        } catch (Exception e) { e.printStackTrace(); }
    }
}