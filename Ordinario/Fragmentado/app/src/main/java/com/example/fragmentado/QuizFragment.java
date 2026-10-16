package com.example.fragmentado;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class QuizFragment extends Fragment {

    public QuizFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        RadioGroup rgOptions = view.findViewById(R.id.rg_options);
        TextInputEditText etComments = view.findViewById(R.id.et_comments);
        View btnSubmit = view.findViewById(R.id.btn_submit_quiz);
        MaterialCardView cardResult = view.findViewById(R.id.card_result);
        TextView tvResultDetails = view.findViewById(R.id.tv_result_details);

        btnSubmit.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(getContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = view.findViewById(selectedId);
            String optionText = selectedRadioButton.getText().toString();

            String commentText = etComments.getText() != null ? etComments.getText().toString().trim() : "";

            StringBuilder summary = new StringBuilder();
            summary.append("Opción seleccionada:\n").append(optionText);

            if (!TextUtils.isEmpty(commentText)) {
                summary.append("\n\nComentario:\n\"").append(commentText).append("\"");
            } else {
                summary.append("\n\n(Sin comentario adicional)");
            }

            tvResultDetails.setText(summary.toString());
            cardResult.setVisibility(View.VISIBLE);

            Toast.makeText(getContext(), "¡Gracias por tus comentarios!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
