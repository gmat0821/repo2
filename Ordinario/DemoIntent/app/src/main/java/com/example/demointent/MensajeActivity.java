package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MensajeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        TextView tvExtraMensaje = findViewById(R.id.tvExtraMensaje);
        EditText etTextoUsuario = findViewById(R.id.etTextoUsuario);
        Button btnMayusculas = findViewById(R.id.btnMayusculas);
        Button btnMinusculas = findViewById(R.id.btnMinusculas);
        TextView tvResultadoTexto = findViewById(R.id.tvResultadoTexto);
        Button btnVolver = findViewById(R.id.btnVolverMensaje);

        // Recibir Extra
        String saludo = getIntent().getStringExtra("SALUDO");
        if (saludo != null) {
            tvExtraMensaje.setText(getString(R.string.extra_recibido, saludo));
            etTextoUsuario.setText(saludo);
        }

        // Funcionalidad específica: Transformación de texto
        btnMayusculas.setOnClickListener(v -> {
            String texto = etTextoUsuario.getText().toString();
            if (!texto.trim().isEmpty()) {
                tvResultadoTexto.setText(texto.toUpperCase());
            } else {
                Toast.makeText(this, "Por favor ingresa un texto", Toast.LENGTH_SHORT).show();
            }
        });

        btnMinusculas.setOnClickListener(v -> {
            String texto = etTextoUsuario.getText().toString();
            if (!texto.trim().isEmpty()) {
                tvResultadoTexto.setText(texto.toLowerCase());
            } else {
                Toast.makeText(this, "Por favor ingresa un texto", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de regreso
        btnVolver.setOnClickListener(v -> finish());
    }
}