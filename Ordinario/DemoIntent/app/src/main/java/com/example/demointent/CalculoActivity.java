package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalculoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculo);

        TextView tvExtraNumero = findViewById(R.id.tvExtraNumero);
        EditText etNumeroIngresado = findViewById(R.id.etNumeroIngresado);
        Button btnCuadrado = findViewById(R.id.btnCuadrado);
        Button btnDoble = findViewById(R.id.btnDoble);
        Button btnFactorial = findViewById(R.id.btnFactorial);
        TextView tvResultadoCalculo = findViewById(R.id.tvResultadoCalculo);
        Button btnVolver = findViewById(R.id.btnVolverCalculo);

        // Recibir Extra
        int numeroBase = getIntent().getIntExtra("NUMERO_BASE", 0);
        tvExtraNumero.setText(getString(R.string.numero_recibido, numeroBase));
        etNumeroIngresado.setText(String.valueOf(numeroBase));

        // Funcionalidades específicas
        btnCuadrado.setOnClickListener(v -> {
            Double num = obtenerNumero(etNumeroIngresado);
            if (num != null) {
                double res = num * num;
                tvResultadoCalculo.setText(String.format("Resultado: %.2f² = %.2f", num, res));
            }
        });

        btnDoble.setOnClickListener(v -> {
            Double num = obtenerNumero(etNumeroIngresado);
            if (num != null) {
                double res = num * 2;
                tvResultadoCalculo.setText(String.format("Resultado: %.2f × 2 = %.2f", num, res));
            }
        });

        btnFactorial.setOnClickListener(v -> {
            Double num = obtenerNumero(etNumeroIngresado);
            if (num != null) {
                int val = num.intValue();
                if (val < 0 || val > 20) {
                    Toast.makeText(this, "Ingresa un entero entre 0 y 20 para el factorial", Toast.LENGTH_SHORT).show();
                } else {
                    long fact = calcularFactorial(val);
                    tvResultadoCalculo.setText(String.format("Resultado: %d! = %d", val, fact));
                }
            }
        });

        // Botón de regreso
        btnVolver.setOnClickListener(v -> finish());
    }

    private Double obtenerNumero(EditText editText) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un número", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número no válido", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private long calcularFactorial(int n) {
        if (n <= 1) return 1;
        long factorial = 1;
        for (int i = 2; i <= n; i++) {
            factorial *= i;
        }
        return factorial;
    }
}