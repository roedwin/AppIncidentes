package com.example.proyectofinal.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.R;
import com.example.proyectofinal.Modelos.ModeloUsuario;

import java.util.ArrayList;

public class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.myViewHolder> {

    ArrayList<ModeloUsuario> list;

    public AdaptadorUsuario(ArrayList<ModeloUsuario> list){
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.nombre.setText(list.get(position).getNombre());
        holder.usuario.setText(list.get(position).getUsuario());
        holder.rol.setText(list.get(position).getRol());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, usuario, rol;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreCard);
            usuario = itemView.findViewById(R.id.userCard);
            rol = itemView.findViewById(R.id.rolCard);
        }
    }
}

