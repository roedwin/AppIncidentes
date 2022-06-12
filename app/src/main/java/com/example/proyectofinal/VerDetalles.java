package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VerDetalles extends AppCompatActivity {

    private DatabaseReference reference;
    FirebaseDatabase referenceS;

    private String llamarTitulo = "";
    private String llamarDescripcion;
    private String llamarFecha;
    private String llamarEstado;

    String seleccionado = "";

    AlertDialog.Builder miDialog;
    LayoutInflater inflater;
    View v;
    AlertDialog dialog;
    TextView elTitulo;
    TextView laDescripcion;
    TextView laFecha;
    TextView elEstado;
    ImageView imageView;
    DatabaseReference getImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalles);

        miDialog = new AlertDialog.Builder(this);
        inflater = LayoutInflater.from(this);
        v = inflater.inflate(R.layout.cambiar_status,null);
        miDialog.setView(v);
        dialog = miDialog.create();


        elTitulo = findViewById(R.id.tituloDetalle);
        laDescripcion = findViewById(R.id.descripcionDetalle);
        laFecha = findViewById(R.id.detalleFecha);
        elEstado = findViewById(R.id.estadoDetalle);
        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");

        imageView = findViewById(R.id.imagenDeFirebase);


    }

    @Override
    protected void onStart() {
        super.onStart();
        llamarTitulo = getIntent().getExtras().getString("titulo");
        llamarDescripcion = getIntent().getExtras().getString("descripcion");
        llamarFecha = getIntent().getExtras().getString("fecha");
        llamarEstado = getIntent().getExtras().getString("estado");
        getImg = reference.child(llamarTitulo).child("path");
        verDetallesI(llamarTitulo,llamarDescripcion,llamarFecha,llamarEstado);
    }

    private void verDetallesI(String t,String d,String f,String e) {
        getImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.getValue(String.class);
                Picasso.with(VerDetalles.this).load(link).rotate(270).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        elTitulo.setText(t);
        laDescripcion.setText(d);
        laFecha.setText(f);
        elEstado.setText(e);


        Button updateEstado = findViewById(R.id.editarEstado);
        updateEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarEstado();
            }
        });
    }
    private void cambiarEstado() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(VerDetalles.this);
        LayoutInflater inflater = LayoutInflater.from(VerDetalles.this);
        View v = inflater.inflate(R.layout.cambiar_status,null);
        miDialog.setView(v);

        AlertDialog dialogS = miDialog.create();

        CheckBox adE = v.findViewById(R.id.checkActivo);
        CheckBox emE = v.findViewById(R.id.checkResuelto);

        Button btnGuardarEstado = v.findViewById(R.id.guardarEstado);

        if (llamarEstado.equals("Activo")){
            adE.setChecked(true);
            seleccionado = "Activo";
        }
        if (llamarEstado.equals("Resuelto")){
            emE.setChecked(true);
            seleccionado = "Resuelto";
        }
        adE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    emE.setChecked(false);
                }
            }
        });
        emE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    adE.setChecked(false);
                }
            }
        });

        btnGuardarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adE.isChecked()==true){
                    seleccionado = "Activo";

                }
                if (emE.isChecked()==true){
                    seleccionado = "Resuelto";

                }

                if (seleccionado.equals("Activo")){
                    dialogS.dismiss();
                }
                if (seleccionado.equals("Resuelto")){
                    reference.child(llamarTitulo).child("estado").setValue(seleccionado);
                    elEstado.setText(seleccionado);
                    dialogS.dismiss();
                }
            }
        });
        dialogS.show();
    }
}