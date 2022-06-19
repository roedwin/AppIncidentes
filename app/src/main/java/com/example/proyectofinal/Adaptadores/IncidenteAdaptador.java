package com.example.proyectofinal.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.HomeEmpleado;
import com.example.proyectofinal.ListaIncidentesAdmin;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.example.proyectofinal.R;
import com.example.proyectofinal.VerDetalles;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class IncidenteAdaptador extends FirebaseRecyclerAdapter<ModeloIncidente, IncidenteAdaptador.ViewHolder> {

    Activity activity;

    private DatabaseReference reference;
    FirebaseDatabase referenceS;

    AlertDialog.Builder miDialog;
    LayoutInflater inflater;
    View v;
    AlertDialog dialog;

    private String key = "";
    private String llamarTitulo;
    private String llamarDescripcion;
    private String llamarFecha;
    private String llamarEstado;
    String usuarioRecibido;
    ListaIncidentesAdmin listaIncidentesAdmin = new ListaIncidentesAdmin();

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public IncidenteAdaptador(@NonNull FirebaseRecyclerOptions<ModeloIncidente> options,Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ModeloIncidente model) {
        holder.setNombre(model.getnombre());
        holder.setTitulo(model.getTitulo());
        holder.setEstado(model.getEstado());

        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = getRef(holder.getAdapterPosition()).getKey();
                llamarTitulo = model.getTitulo();
                llamarDescripcion = model.getDescripcion();
                llamarFecha = model.getFechaCreacion();
                llamarEstado = model.getEstado();

                Intent i = new Intent(activity, VerDetalles.class);
                i.putExtra("titulo",llamarTitulo);
                i.putExtra("descripcion",llamarDescripcion);
                i.putExtra("fecha",llamarFecha);
                i.putExtra("estado",llamarEstado);
                activity.startActivity(i);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_incidentes_admin,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton btn_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            btn_view = itemView.findViewById(R.id.verDetalleIncicente);
        }
        public void setNombre(String nombre){
            TextView textViewNombre = mView.findViewById(R.id.nombreCardIncidenteAdmin);
            textViewNombre.setText(nombre);
        }
        public void setTitulo(String titulo){
            TextView textViewTitulo = mView.findViewById(R.id.tituloCardIncidenteAdmin);
            textViewTitulo.setText(titulo);
        }
        public void setEstado(String estado){
            TextView textViewEstado = mView.findViewById(R.id.estadoIncidenteAdmin);
            textViewEstado.setText(estado);
            if (estado.equals("Activo")){
                textViewEstado.setTextColor(Color.GREEN);
            }
            if (estado.equals("Resuelto")){
                textViewEstado.setTextColor(Color.BLUE);
            }
        }
    }

}
