package com.example.fragmentado;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnStats;
    private MaterialButton btnMusic;
    private MaterialButton btnQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnStats = findViewById(R.id.btn_stats);
        btnMusic = findViewById(R.id.btn_music);
        btnQuiz = findViewById(R.id.btn_quiz);

        // Set listeners for each button
        btnStats.setOnClickListener(v -> {
            replaceFragment(new StatsFragment());
            updateActiveButton(btnStats);
        });

        btnMusic.setOnClickListener(v -> {
            replaceFragment(new MusicFragment());
            updateActiveButton(btnMusic);
        });

        btnQuiz.setOnClickListener(v -> {
            replaceFragment(new QuizFragment());
            updateActiveButton(btnQuiz);
        });

        // Set default fragment on initial launch
        if (savedInstanceState == null) {
            replaceFragment(new StatsFragment());
            updateActiveButton(btnStats);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateActiveButton(MaterialButton activeButton) {
        btnStats.setAlpha(activeButton == btnStats ? 1.0f : 0.6f);
        btnMusic.setAlpha(activeButton == btnMusic ? 1.0f : 0.6f);
        btnQuiz.setAlpha(activeButton == btnQuiz ? 1.0f : 0.6f);

        btnStats.setStrokeWidth(activeButton == btnStats ? 4 : 0);
        btnMusic.setStrokeWidth(activeButton == btnMusic ? 4 : 0);
        btnQuiz.setStrokeWidth(activeButton == btnQuiz ? 4 : 0);
    }
}
