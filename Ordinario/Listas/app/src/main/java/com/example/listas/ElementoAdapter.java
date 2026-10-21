package com.example.listas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ElementoAdapter extends RecyclerView.Adapter<ElementoAdapter.ElementoViewHolder> {

    private List<Elemento> listaElementos;

    // Constructor del Adaptador
    public ElementoAdapter(List<Elemento> listaElementos) {
        this.listaElementos = listaElementos;
    }

    // 1. Define el Holder (ViewHolder)
    public static class ElementoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitulo;
        public TextView tvDescripcion;

        public ElementoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }

    // 2. Infla el diseño de cada ítem y crea el ViewHolder
    @NonNull
    @Override
    public ElementoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_elemento, parent, false);
        return new ElementoViewHolder(view);
    }

    // 3. Enlaza los datos del modelo con las vistas del ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ElementoViewHolder holder, int position) {
        Elemento elemento = listaElementos.get(position);
        holder.tvTitulo.setText(elemento.getTitulo());
        holder.tvDescripcion.setText(elemento.getDescripcion());
    }

    // 4. Retorna la cantidad de elementos en la lista
    @Override
    public int getItemCount() {
        return listaElementos != null ? listaElementos.size() : 0;
    }
}