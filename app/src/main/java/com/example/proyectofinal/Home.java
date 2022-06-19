package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Modelos.ModeloUsuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Home extends AppCompatActivity {

    private DatabaseReference reference;

    Button goToUsers, goToIncidentes;

    TextView usuario, rol, count;
    String nombreRecibido;
    String usuarioRecibido;
    String estadoRecibido;

    NotificationBadge notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usuario = findViewById(R.id.idUsuarioHome);
        rol = findViewById(R.id.idRolHome);
        //count = findViewById(R.id.activosCount);

        notificationBadge = findViewById(R.id.badgeN);


        goToUsers = findViewById(R.id.btnGoToUsers);
        goToIncidentes = findViewById(R.id.goIncidentes);

        goToUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,ListaUsuarios.class);
                intent.putExtra("usuario",usuarioRecibido);
                startActivity(intent);
            }
        });

        goToIncidentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,IntermediaroIncidentes.class);
                intent.putExtra("usuario",usuarioRecibido);
                intent.putExtra("estado",estadoRecibido);
                startActivity(intent);
            }
        });
        //reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes").child("estado");

    }

    @Override
    protected void onStart() {
        super.onStart();
        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");

        reference.orderByChild("estado").equalTo("Activo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = snapshot.getChildrenCount();
                //count.setText(String.valueOf(total));
                String num = String.valueOf(total);

                notificationBadge.setNumber(Integer.valueOf(num));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        estadoRecibido = getIntent().getExtras().getString("estado");

        nombreRecibido = getIntent().getExtras().getString("usuario");
        usuarioRecibido = getIntent().getExtras().getString("u");
        String rolRecibido = getIntent().getExtras().getString("role");

        usuario.setText(nombreRecibido);
        rol.setText(rolRecibido);
    }
}