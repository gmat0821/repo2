package com.example.fragmentado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class StatsFragment extends Fragment {

    private int score = 10;
    private TextView tvScoreValue;
    private TextView tvBadge;
    private ProgressBar progressLevel;

    public StatsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        tvScoreValue = view.findViewById(R.id.tv_score_value);
        tvBadge = view.findViewById(R.id.tv_badge);
        progressLevel = view.findViewById(R.id.progress_level);

        MaterialButton btnPlus = view.findViewById(R.id.btn_plus);
        MaterialButton btnMinus = view.findViewById(R.id.btn_minus);
        MaterialButton btnReset = view.findViewById(R.id.btn_reset);

        updateUI();

        btnPlus.setOnClickListener(v -> {
            if (score < 100) {
                score += 10;
                updateUI();
            } else {
                Toast.makeText(getContext(), "¡Puntaje máximo alcanzado!", Toast.LENGTH_SHORT).show();
            }
        });

        btnMinus.setOnClickListener(v -> {
            if (score > 0) {
                score -= 10;
                updateUI();
            }
        });

        btnReset.setOnClickListener(v -> {
            score = 0;
            updateUI();
            Toast.makeText(getContext(), "Contador reiniciado", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void updateUI() {
        tvScoreValue.setText(String.valueOf(score));
        progressLevel.setProgress(score);

        if (score >= 70) {
            tvBadge.setText(R.string.stats_rank_expert);
        } else if (score >= 30) {
            tvBadge.setText(R.string.stats_rank_advanced);
        } else {
            tvBadge.setText(R.string.stats_rank_novice);
        }
    }
}
