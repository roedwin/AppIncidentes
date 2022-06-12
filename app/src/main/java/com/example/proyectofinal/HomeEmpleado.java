package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeEmpleado extends AppCompatActivity {

    Button incidentes, reporte;
    TextView nombre;

    String nombreRecibido;
    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empleado);

        incidentes = findViewById(R.id.idVerIncidentes);
        nombre = findViewById(R.id.idUsuarioHomeI);

        incidentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeEmpleado.this, ListaIncidentes.class);
                intent.putExtra("nombre", nombreRecibido);
                intent.putExtra("u",usuario);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        nombreRecibido = getIntent().getExtras().getString("nombre");
        usuario = getIntent().getExtras().getString("u");
        nombre.setText(nombreRecibido);
    }
}