package com.example.listas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ElementoAdapter adapter;

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

        // 1. Obtener la referencia del RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // 2. Define el posicionamiento (LayoutManager)
        // Se utiliza LinearLayoutManager para una lista vertical estándar
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 3. Crear lista de datos de ejemplo
        List<Elemento> listaElementos = obtenerDatosEjemplo();

        // 4. Define y asigna el Adapter
        adapter = new ElementoAdapter(listaElementos);
        recyclerView.setAdapter(adapter);
    }

    private List<Elemento> obtenerDatosEjemplo() {
        List<Elemento> lista = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            lista.add(new Elemento(
                "Elemento " + i,
                "Esta es la descripción detallada del elemento número " + i
            ));
        }
        return lista;
    }
}