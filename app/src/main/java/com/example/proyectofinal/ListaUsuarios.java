package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Modelos.ModeloUsuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ListaUsuarios extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton btnFloat;

    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String onlineUserID;

    private String etUser;

    private ProgressDialog load;

    String seleccionado = "";
    private String usuarioRecibido;

    private String key = "";
    private String llamarNombre;
    private String llamarUsuario;
    private String llamarPass;
    private String llamarRol;

    ArrayList<ModeloUsuario> listUser;
    SearchView searchView;

    private String sKey = "kpass";
    private int T_LEN = 128;
    private Cipher encriptedCipher;
    private int KEY_SIZE = 128;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        recyclerView = findViewById(R.id.rview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        load = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("TblUser");

        btnFloat = findViewById(R.id.btnAddUser);
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarUsuario();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        usuarioRecibido = getIntent().getExtras().getString("usuario");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModeloUsuario> options = new FirebaseRecyclerOptions.Builder<ModeloUsuario>()
                .setQuery(reference, ModeloUsuario.class)
                .build();
        FirebaseRecyclerAdapter<ModeloUsuario, miViewHolder> adapter = new FirebaseRecyclerAdapter<ModeloUsuario, miViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull miViewHolder holder, int position, @NonNull ModeloUsuario modeloUsuario) {
                holder.setNombre(modeloUsuario.getNombre());
                holder.setUsuario(modeloUsuario.getUsuario());
                holder.setRol(modeloUsuario.getRol());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(holder.getAdapterPosition()).getKey();
                        llamarNombre = modeloUsuario.getNombre();
                        llamarUsuario = modeloUsuario.getUsuario();
                        llamarPass = modeloUsuario.getPass();
                        llamarRol = modeloUsuario.getRol();

                        updateUsuario();
                    }
                });
            }

            @NonNull
            @Override
            public miViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
                return new miViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    private void agregarUsuario() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.input_file, null);
        miDialog.setView(view);

        AlertDialog dialog = miDialog.create();
        dialog.setCancelable(false);

        final EditText nombreC = view.findViewById(R.id.etAddNombre);
        final EditText usuario = view.findViewById(R.id.etAddUser);
        final EditText contra = view.findViewById(R.id.etAddpass);
        Button saveBtn =  view.findViewById(R.id.btnGuardar);
        Button cancelBtn = view.findViewById(R.id.btnCancel);
        CheckBox ad = view.findViewById(R.id.idAdmin);
        CheckBox em = view.findViewById(R.id.idEmpleado);

        ad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    em.setChecked(false);
                }
            }
        });
        em.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    ad.setChecked(false);
                }
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("TblUser").child(usuario.getText().toString());


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etNombre = nombreC.getText().toString().trim();
                etUser = usuario.getText().toString().trim();
                String etContra = contra.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Calendar calendario = Calendar.getInstance();

                String passEncriptada = "";
                try {
                    passEncriptada = AESCrypt.encrypt(sKey,etContra);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                String seleccion = "";
                if (ad.isChecked()==true){
                    seleccion = "administrador";
                }
                if (em.isChecked()==true){
                    seleccion = "empleado";
                }

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

                if (TextUtils.isEmpty(etNombre)){
                    nombreC.setError("El nombre es requerido");
                    return;
                }
                if (TextUtils.isEmpty(etUser)){
                    usuario.setError("El usuario es requerida");
                    return;
                }
                if (TextUtils.isEmpty(etContra)){
                    contra.setError("La contraseña es requerida");
                    return;
                }else{
                    load.setMessage("Agregando Usuario");
                    load.setCanceledOnTouchOutside(false);
                    load.show();

                    ModeloUsuario model = new ModeloUsuario(etUser,etNombre,seleccion, passEncriptada,date+"-"+hour);

                    reference.child(etUser).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                load.dismiss();
                                Toast.makeText(ListaUsuarios.this, "Usuario agregado correctamente", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else {
                                load.dismiss();
                                String error = task.getException().toString();
                                Toast.makeText(ListaUsuarios.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void updateUsuario() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.update_date,null);
        miDialog.setView(v);

        AlertDialog dialog = miDialog.create();

        EditText validacion = v.findViewById(R.id.etAddUserEdit);

        EditText miNombre = v.findViewById(R.id.etAddNombreEdit);
        EditText miUsuario = v.findViewById(R.id.etAddUserEdit);
        EditText miPass = v.findViewById(R.id.etAddpassEdit);

        String passEncriptada = "";
        try {
            passEncriptada = AESCrypt.decrypt(sKey,llamarPass);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        CheckBox adE = v.findViewById(R.id.idAdminEdit);
        CheckBox emE = v.findViewById(R.id.idEmpleadoEdit);

        miNombre.setText(llamarNombre);
        miNombre.setSelection(llamarNombre.length());

        miUsuario.setText(llamarUsuario);
        miUsuario.setSelection(llamarUsuario.length());
        miUsuario.setFocusable(false);

        miPass.setText(passEncriptada);
        miPass.setSelection(passEncriptada.length());


        if (llamarRol.equals("administrador")){
            adE.setChecked(true);
            seleccionado = "administrador";
        }
        if (llamarRol.equals("empleado")){
            emE.setChecked(true);
            seleccionado = "empleado";
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


        Button deleteTaskBtn = v.findViewById(R.id.btnEliminarEdit);
        Button updateTaskBtn = v.findViewById(R.id.btnGuardarEdit);
        Button salirEditBtn = v.findViewById(R.id.btnSalirEdit);

        updateTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llamarNombre = miNombre.getText().toString().trim();
                llamarUsuario = miUsuario.getText().toString().trim();
                llamarPass = miPass.getText().toString().trim();

                String passEncriptada = "";
                try {
                    passEncriptada = AESCrypt.encrypt(sKey,llamarPass);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }

                String date = DateFormat.getDateInstance().format(new Date());
                Calendar calendario = Calendar.getInstance();

                if (adE.isChecked()==true){
                    seleccionado = "administrador";
                }
                if (emE.isChecked()==true){
                    seleccionado = "empleado";
                }

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

                ModeloUsuario model = new ModeloUsuario(llamarUsuario, llamarNombre, seleccionado,passEncriptada,date+"-"+hour);

                reference.child(llamarUsuario).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ListaUsuarios.this, "Usuario actualizado con exito", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(ListaUsuarios.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (llamarUsuario.equals(usuarioRecibido)){
                    Toast.makeText(ListaUsuarios.this, "Error, no puede eliminar este usuario", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(ListaUsuarios.this);
                    alerta.setMessage("Realmente desea borrar este usuario")
                            .setCancelable(false)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    reference.child(llamarUsuario).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ListaUsuarios.this, "Usuario Eliminado", Toast.LENGTH_SHORT).show();
                                            }else {
                                                String error = task.getException().toString();
                                                Toast.makeText(ListaUsuarios.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("Borrar");
                    titulo.show();
                }
                dialog.dismiss();
            }
        });

        salirEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public static class miViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public miViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setNombre(String nombre){
            TextView textViewNombre = mView.findViewById(R.id.nombreCard);
            textViewNombre.setText(nombre);
        }
        public void setUsuario(String usuario){
            TextView textViewsuario = mView.findViewById(R.id.userCard);
            textViewsuario.setText(usuario);
        }
        public void setRol(String rol){
            TextView textViewRol = mView.findViewById(R.id.rolCard);
            textViewRol.setText(rol);
        }
    }
}