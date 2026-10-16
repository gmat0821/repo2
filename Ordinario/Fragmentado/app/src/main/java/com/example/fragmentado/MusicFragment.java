package com.example.fragmentado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class MusicFragment extends Fragment {

    private boolean isPlaying = false;
    private boolean isBassBoosted = false;
    private int currentSongIndex = 0;

    private final String[] songs = {"Neon Horizon", "Cyber Pulse", "Midnight Drive"};
    private final String[] artists = {"Synthwave Studio", "Electro Waves", "Retrowave Band"};

    private TextView tvPlaybackStatus;
    private TextView tvTrackTitle;
    private TextView tvTrackArtist;
    private TextView tvTimeCurrent;
    private MaterialButton btnPlayPause;
    private MaterialButton btnEffect;

    public MusicFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        tvPlaybackStatus = view.findViewById(R.id.tv_playback_status);
        tvTrackTitle = view.findViewById(R.id.tv_track_title);
        tvTrackArtist = view.findViewById(R.id.tv_track_artist);
        tvTimeCurrent = view.findViewById(R.id.tv_time_current);

        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        MaterialButton btnPrev = view.findViewById(R.id.btn_prev);
        MaterialButton btnNext = view.findViewById(R.id.btn_next);
        btnEffect = view.findViewById(R.id.btn_effect);

        SeekBar seekBar = view.findViewById(R.id.seek_bar_music);

        updateSongDisplay();

        btnPlayPause.setOnClickListener(v -> togglePlayPause());

        btnNext.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex + 1) % songs.length;
            updateSongDisplay();
            Toast.makeText(getContext(), "Siguiente canción", Toast.LENGTH_SHORT).show();
        });

        btnPrev.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex - 1 + songs.length) % songs.length;
            updateSongDisplay();
            Toast.makeText(getContext(), "Canción anterior", Toast.LENGTH_SHORT).show();
        });

        btnEffect.setOnClickListener(v -> {
            isBassBoosted = !isBassBoosted;
            if (isBassBoosted) {
                btnEffect.setText(R.string.effect_on);
                Toast.makeText(getContext(), "Refuerzo de graves activado", Toast.LENGTH_SHORT).show();
            } else {
                btnEffect.setText(R.string.effect_off);
                Toast.makeText(getContext(), "Refuerzo de graves desactivado", Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seconds = (int) (progress * 2.2); // Map 0-100 to ~220 seconds
                    int mins = seconds / 60;
                    int secs = seconds % 60;
                    tvTimeCurrent.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    private void togglePlayPause() {
        isPlaying = !isPlaying;
        if (isPlaying) {
            btnPlayPause.setText(R.string.btn_pause_text);
            tvPlaybackStatus.setText(R.string.status_playing);
            Toast.makeText(getContext(), "Reproduciendo " + songs[currentSongIndex], Toast.LENGTH_SHORT).show();
        } else {
            btnPlayPause.setText(R.string.btn_play_text);
            tvPlaybackStatus.setText(R.string.status_paused);
            Toast.makeText(getContext(), "Pausado", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSongDisplay() {
        tvTrackTitle.setText(songs[currentSongIndex]);
        tvTrackArtist.setText(artists[currentSongIndex]);
    }
}
