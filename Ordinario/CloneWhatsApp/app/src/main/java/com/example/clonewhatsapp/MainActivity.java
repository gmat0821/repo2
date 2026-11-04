package com.example.clonewhatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.clonewhatsapp.api.SupabaseClient;
import com.example.clonewhatsapp.utils.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvUserEmail.setText("Sesión activa: " + sessionManager.getEmail());

        btnLogout.setOnClickListener(v -> performLogout());
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