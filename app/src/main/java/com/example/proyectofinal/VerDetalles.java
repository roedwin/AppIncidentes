package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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

        if (llamarEstado.equals("Activo")){
            elEstado.setTextColor(Color.GREEN);
        }
        if (llamarEstado.equals("Resuelto")){
            elEstado.setTextColor(Color.BLUE);
        }
    }

    private void verDetallesI(String t,String d,String f,String e) {
        getImg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.getValue(String.class);
                Picasso.with(VerDetalles.this).load(link).rotate(0).into(imageView);
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

        llamarEstado = getIntent().getExtras().getString("estado");

        Button btnGuardarEstado = v.findViewById(R.id.guardarEstado);

        EditText msj = v.findViewById(R.id.enviarMensaje);

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

        String date = DateFormat.getDateInstance().format(new Date());
        Calendar calendario = Calendar.getInstance();


        int hora, minutos, segundos;
        hora = calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);

        String hour;

        if (hora <= 12){
            hour = hora + ":" + minutos + ":" + segundos + " am";
        }else{
            hour = hora + ":" + minutos + ":" + segundos + " pm";
        }

        if (llamarEstado.equals("Resuelto")){
            btnGuardarEstado.setVisibility(Button.GONE);
            btnGuardarEstado.setBackgroundColor(Color.GRAY);
        }else {
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
                        reference.child(llamarTitulo).child("mensaje").setValue(msj.getText().toString());
                        elEstado.setText(seleccionado);
                        dialogS.dismiss();
                    }
                    Intent intent = new Intent(VerDetalles.this, ListaIncidentesAdminA.class);
                    startActivity(intent);
                    finish();
                }
            });
            dialogS.show();
        }
    }
}