package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText loginEmail, loginPass;
    private Button loginButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private ProgressDialog load;

    private String sKey = "kpass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        load = new ProgressDialog(this);

        /*toolbar = findViewById(R.id.loginBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Iniciar Sesion");*/

        /*if (firebaseAuth!=null){
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }else {

        }*/


        loginEmail = findViewById(R.id.etUser);
        loginPass = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = loginEmail.getText().toString();
                String password = loginPass.getText().toString();



                if (TextUtils.isEmpty(usuario)){
                    loginEmail.setError("El usuario es requerido");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPass.setError("La contraseña es requerida");
                    return;
                }else {
                    load.setMessage("Inicio de sesion en progreso");
                    load.setCanceledOnTouchOutside(false);
                    load.show();
                    reference.child("TblUser").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String passEncriptada = "";
                            try {
                                passEncriptada = AESCrypt.encrypt(sKey,password);
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            if (snapshot.hasChild(usuario)){
                                final String getPass = snapshot.child(usuario).child("pass").getValue(String.class);
                                if (getPass.equals(passEncriptada)){

                                    final String getRole = snapshot.child(usuario).child("rol").getValue(String.class);
                                    final String getNombre = snapshot.child(usuario).child("nombre").getValue(String.class);
                                    final String getEstado = snapshot.child(usuario).child("estado").getValue(String.class);

                                    if (getRole.equals("administrador")){
                                        load.dismiss();
                                        Intent intent = new Intent(MainActivity.this, Home.class);
                                        intent.putExtra("usuario",getNombre);
                                        intent.putExtra("u",usuario);
                                        intent.putExtra("role",getRole);
                                        intent.putExtra("estado",getEstado);
                                        startActivity(intent);
                                    }
                                    if (getRole.equals("empleado")){
                                        load.dismiss();
                                        Intent intent = new Intent(MainActivity.this, HomeEmpleado.class);
                                        intent.putExtra("nombre",getNombre);
                                        intent.putExtra("u",usuario);
                                        intent.putExtra("role",getRole);
                                        startActivity(intent);
                                    }
                                }
                                else {
                                    load.dismiss();
                                    Toast.makeText(MainActivity.this, "Contraseña equivocada", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(MainActivity.this, "Contraseña equivocada", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    load.dismiss();
                }
            }
        });
    }
}