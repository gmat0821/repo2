package com.example.clonewhatsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.utils.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etAvatarUrl;
    private ImageView imgProfileAvatar;
    private SessionManager sessionManager;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        etFullName = findViewById(R.id.etFullName);
        etAvatarUrl = findViewById(R.id.etAvatarUrl);
        imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        TextView btnSelectImage = findViewById(R.id.btnSelectImage);
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfileData());

        // Selector de imágenes del teléfono
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadImageToSupabase(selectedImageUri);
                        }
                    }
                });

        btnSelectImage.setOnClickListener(v -> openGallery());
        imgProfileAvatar.setOnClickListener(v -> openGallery());

        loadProfileData();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToSupabase(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();

            String fileName = sessionManager.getUserId() + ".jpg";
            Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();

            SupabaseClient.uploadFile("avatars", fileName, bytes, "image/jpeg", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    runOnUiThread(() -> {
                        String publicUrl = SupabaseClient.getPublicUrl("avatars", fileName);
                        etAvatarUrl.setText(publicUrl);
                        Glide.with(ProfileActivity.this).load(publicUrl).into(imgProfileAvatar);
                        Toast.makeText(ProfileActivity.this, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error leyendo imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileData() {
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();
        if (token == null || userId.isEmpty()) return;

        SupabaseClient.getProfile(token, userId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws java.io.IOException {
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

                            if (!avatarUrl.isEmpty()) {
                                Glide.with(ProfileActivity.this).load(avatarUrl).placeholder(R.drawable.ic_chiikawa_logo).into(imgProfileAvatar);
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    private void saveProfileData() {
        String fullName = etFullName.getText().toString().trim();
        String avatarUrl = etAvatarUrl.getText().toString().trim();
        SupabaseClient.updateProfile(sessionManager.getToken(), sessionManager.getUserId(), fullName, avatarUrl, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {}
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Perfil guardado", Toast.LENGTH_SHORT).show());
            }
        });
    }
}