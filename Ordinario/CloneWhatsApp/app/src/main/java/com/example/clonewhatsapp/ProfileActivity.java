package com.example.clonewhatsapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.utils.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etAvatarUrl;
    private ImageView imgProfileAvatar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        etFullName = findViewById(R.id.etFullName);
        etAvatarUrl = findViewById(R.id.etAvatarUrl);
        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);

        loadProfileData();

        btnSaveProfile.setOnClickListener(v -> saveProfileData());
    }

    private void renderImage(String url) {
        if (url == null || url.trim().isEmpty()) return;

        Glide.with(ProfileActivity.this)
                .load(url.trim())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error: URL de imagen no válida o inaccesible", Toast.LENGTH_SHORT).show());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imgProfileAvatar);
    }

    private void loadProfileData() {
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();

        if (token == null || userId.isEmpty()) return;

        SupabaseClient.getProfile(token, userId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error al cargar datos del perfil", Toast.LENGTH_SHORT).show());
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
                            JSONObject profile = array.getJSONObject(0);
                            String fullName = profile.optString("full_name", "");
                            String avatarUrl = profile.optString("avatar_url", "");

                            etFullName.setText(fullName);
                            etAvatarUrl.setText(avatarUrl);

                            renderImage(avatarUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void saveProfileData() {
        String fullName = etFullName.getText().toString().trim();
        String avatarUrl = etAvatarUrl.getText().toString().trim();
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();

        if (token == null || userId.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseClient.updateProfile(token, userId, fullName, avatarUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error de red al guardar", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                        renderImage(avatarUrl);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al actualizar perfil en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}