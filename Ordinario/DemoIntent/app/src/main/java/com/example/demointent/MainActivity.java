package com.example.demointent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMensaje = findViewById(R.id.btnMensaje);
        Button btnCalculo = findViewById(R.id.btnCalculo);
        Button btnPerfil = findViewById(R.id.btnPerfil);

        // Botón 1: Envía un String
        btnMensaje.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MensajeActivity.class);
            intent.putExtra("SALUDO", "¡Hola desde la Actividad Principal!");
            startActivity(intent);
        });

        // Botón 2: Envía un Integer
        btnCalculo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalculoActivity.class);
            intent.putExtra("NUMERO_BASE", 5);
            startActivity(intent);
        });

        // Botón 3: Envía un Boolean y String
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            intent.putExtra("ES_ADMIN", true);
            intent.putExtra("NOMBRE_USUARIO", "Carlos Gómez");
            startActivity(intent);
        });
    }
}