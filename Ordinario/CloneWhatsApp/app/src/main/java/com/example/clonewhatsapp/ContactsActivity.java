package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        sessionManager = new SessionManager(this);
        RecyclerView rvContacts = findViewById(R.id.rvContacts);
        EditText etSearchUser = findViewById(R.id.etSearchUser);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, user -> openOrCreateChat(user));

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

    private void openOrCreateChat(User targetUser) {
        String token = sessionManager.getToken();
        String currentUserId = sessionManager.getUserId();

        SupabaseClient.createConversation(token, currentUserId, targetUser.getId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ContactsActivity.this, "Error al crear chat", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody == null) return;
                String body = responseBody.string();

                runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(body);
                        if (array.length() > 0) {
                            String conversationId = array.getJSONObject(0).getString("id");

                            // Agregar miembros
                            SupabaseClient.addConversationMember(token, conversationId, currentUserId, new Callback() {
                                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
                            });
                            SupabaseClient.addConversationMember(token, conversationId, targetUser.getId(), new Callback() {
                                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {}
                                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {}
                            });

                            Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
                            intent.putExtra("CONVERSATION_ID", conversationId);
                            intent.putExtra("OTHER_USER_ID", targetUser.getId());
                            intent.putExtra("OTHER_USER_NAME", targetUser.getFullName() != null && !targetUser.getFullName().isEmpty() ? targetUser.getFullName() : targetUser.getUsername());
                            intent.putExtra("OTHER_USER_AVATAR", targetUser.getAvatarUrl());
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}