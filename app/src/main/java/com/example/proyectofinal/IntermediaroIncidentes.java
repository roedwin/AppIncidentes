package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntermediaroIncidentes extends AppCompatActivity {

    Button goToActivos, goToResueltos, goToTodos;
    String estadoRecibido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediaro_incidentes);

        goToActivos = findViewById(R.id.btnGoToActivos);
        goToResueltos = findViewById(R.id.btnGoToResueltos);
        goToTodos = findViewById(R.id.incidentesTodos);

        goToTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntermediaroIncidentes.this, ListaIncidentesAdmin.class);
                i.putExtra("estado",estadoRecibido);
                startActivity(i);
            }
        });

        goToActivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntermediaroIncidentes.this, ListaIncidentesAdminA.class);
                i.putExtra("estado",estadoRecibido);
                startActivity(i);
            }
        });

        goToResueltos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IntermediaroIncidentes.this, ListaIncidentesAdminR.class);
                i.putExtra("estado",estadoRecibido);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        estadoRecibido = getIntent().getExtras().getString("estado");
    }
}