package com.example.demointent;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView tvUsuario = findViewById(R.id.tvUsuario);
        TextView tvExtraAdmin = findViewById(R.id.tvExtraAdmin);
        EditText etNombreUsuario = findViewById(R.id.etNombreUsuario);
        SwitchMaterial switchAdmin = findViewById(R.id.switchAdmin);
        Button btnActualizarPerfil = findViewById(R.id.btnActualizarPerfil);
        TextView tvAccionesPermitidas = findViewById(R.id.tvAccionesPermitidas);
        Button btnVolver = findViewById(R.id.btnVolverPerfil);

        // Recibir Extras
        String nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        boolean esAdmin = getIntent().getBooleanExtra("ES_ADMIN", false);

        if (nombreUsuario != null) {
            tvUsuario.setText(getString(R.string.usuario_recibido, nombreUsuario));
            etNombreUsuario.setText(nombreUsuario);
        }
        tvExtraAdmin.setText(getString(R.string.admin_recibido, String.valueOf(esAdmin)));

        switchAdmin.setChecked(esAdmin);
        actualizarPermisos(etNombreUsuario.getText().toString(), esAdmin, tvAccionesPermitidas);

        // Funcionalidad específica: Cambiar permisos dinámicamente y actualizar perfil
        switchAdmin.setOnCheckedChangeListener((buttonView, isChecked) ->
                actualizarPermisos(etNombreUsuario.getText().toString(), isChecked, tvAccionesPermitidas)
        );

        btnActualizarPerfil.setOnClickListener(v -> {
            String nuevoNombre = etNombreUsuario.getText().toString().trim();
            boolean modoAdmin = switchAdmin.isChecked();
            actualizarPermisos(nuevoNombre, modoAdmin, tvAccionesPermitidas);
            Toast.makeText(this, "Perfil actualizado para: " + nuevoNombre, Toast.LENGTH_SHORT).show();
        });

        // Botón de regreso
        btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizarPermisos(String usuario, boolean esAdmin, TextView tvAcciones) {
        String rol = esAdmin ? "ADMINISTRADOR" : "USUARIO ESTÁNDAR";
        String detalle = esAdmin
                ? "- Crear y eliminar usuarios\n- Modificar configuración global\n- Acceso total a reportes"
                : "- Ver perfil personal\n- Modificar datos propios\n- Acceso limitado";

        tvAcciones.setText("Usuario: " + usuario + "\nRol: " + rol + "\n\nPermisos:\n" + detalle);
    }
}