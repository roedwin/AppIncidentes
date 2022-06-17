package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Adaptadores.IncidenteAdaptador;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.example.proyectofinal.Modelos.ModeloUsuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Cipher;

public class ListaIncidentes extends AppCompatActivity {

    FirebaseRecyclerAdapter<ModeloIncidente, ListaIncidentes.miViewHolder> adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton btnFloat;

    private static final int file = 1;
    private DatabaseReference reference;
    FirebaseDatabase referenceS;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String onlineUserID;


    private ProgressDialog load;

    String seleccionado = "";

    private String key = "";
    private String llamarNombre;
    private String llamarUsuario;
    private String llamarPass;
    private String llamarRol;
    ImageView img;
    String rutaPath;
    private String nombreRecibido;

    ArrayList<ModeloIncidente> listUser;
    SearchView searchView;

    private String sKey = "kpass";
    private int T_LEN = 128;
    private Cipher encriptedCipher;
    private int KEY_SIZE = 128;

    IncidenteAdaptador mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_incidentes);

        recyclerView = findViewById(R.id.rviewIncidentes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        searchView = findViewById(R.id.searchBar);

        load = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");
        referenceS = FirebaseDatabase.getInstance();

        btnFloat = findViewById(R.id.btnAddIncidente);
        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarIncidente();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        nombreRecibido = getIntent().getExtras().getString("nombre");

        Query query = referenceS.getReference("TblIncidentes");

        FirebaseRecyclerOptions<ModeloIncidente> options = new FirebaseRecyclerOptions.Builder<ModeloIncidente>()
                .setQuery(query.orderByChild("nombre").equalTo(nombreRecibido), ModeloIncidente.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ModeloIncidente, ListaIncidentes.miViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListaIncidentes.miViewHolder holder, int position, @NonNull ModeloIncidente modeloIncidente) {
                holder.setNombre(modeloIncidente.getnombre());
                holder.setTitulo(modeloIncidente.getTitulo());
                holder.setMensaje(modeloIncidente.getMensaje());

                /*holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        key = getRef(holder.getAdapterPosition()).getKey();
                        llamarNombre = modeloUsuario.getNombre();
                        llamarUsuario = modeloUsuario.getUsuario();
                        llamarPass = modeloUsuario.getPass();
                        llamarRol = modeloUsuario.getRol();

                        updateUsuario();
                    }
                });*/
            }

            @NonNull
            @Override
            public ListaIncidentes.miViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_incidentes,parent,false);
                return new ListaIncidentes.miViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    private void agregarIncidente() {
        AlertDialog.Builder miDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.input_file_incidentes, null);
        miDialog.setView(view);

        AlertDialog dialog = miDialog.create();
        dialog.setCancelable(false);

        String nombreRecibido = getIntent().getExtras().getString("nombre");
        String usuarioRecibido = getIntent().getExtras().getString("u");



        final EditText titulo = view.findViewById(R.id.idTituloIncidenteTxt);
        final EditText descripcion = view.findViewById(R.id.idDescripcionIncidenteTxt);

        TextView texto = view.findViewById(R.id.txtSubirImg);

        Button saveBtn =  view.findViewById(R.id.btnGuardarIncidente);
        Button cancelBtn = view.findViewById(R.id.btnCancelIncidente);

        img = view.findViewById(R.id.fotoSubida);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder miDialog = new AlertDialog.Builder(ListaIncidentes.this);
                LayoutInflater inflater = LayoutInflater.from(ListaIncidentes.this);

                View v = inflater.inflate(R.layout.buttons, null);
                miDialog.setView(v);

                AlertDialog dialog = miDialog.create();
                dialog.setCancelable(false);

                ImageButton btnTomarFoto = v.findViewById(R.id.takePhoto);
                ImageButton btnBuscarFoto = v.findViewById(R.id.searchPhoto);

                btnTomarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tomarFoto();
                        dialog.dismiss();
                        texto.setText("");
                    }
                });

                dialog.show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etTitulo = titulo.getText().toString().trim();
                //String etPath = usuario.getText().toString().trim();
                String etDescripcion = descripcion.getText().toString().trim();
                String id = reference.push().getKey();
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

                if (TextUtils.isEmpty(etTitulo)){
                    titulo.setError("El nombre es requerido");
                    return;
                }
                /*if (TextUtils.isEmpty(etPath)){
                    usuario.setError("El usuario es requerida");
                    return;
                }*/
                if (TextUtils.isEmpty(etDescripcion)){
                    descripcion.setError("La descripcion es requerida");
                    return;
                }else{
                    load.setMessage("Agregando Incidente");
                    load.setCanceledOnTouchOutside(false);
                    load.show();

                    final String randomKey = UUID.randomUUID().toString();
                    Uri stringUri = Uri.fromFile(new File(rutaPath));
                    StorageReference sReference = storageReference.child("imagenes/"+randomKey);
                    UploadTask uploadTask = sReference.putFile(stringUri);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return sReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();

                                String urlRecibido = uri.toString();
                                ModeloIncidente model = new ModeloIncidente(etTitulo,etDescripcion,urlRecibido,"Activo",nombreRecibido,date+"-"+hour,"-","");

                                reference.child(etTitulo).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            load.dismiss();
                                            Toast.makeText(ListaIncidentes.this, "Incidente agregado correctamente", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }else {
                                            load.dismiss();
                                            String error = task.getException().toString();
                                            Toast.makeText(ListaIncidentes.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            //Bundle extras = data.getExtras();
            Bitmap imgBit = BitmapFactory.decodeFile(rutaPath);
            img.setImageBitmap(imgBit);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void tomarFoto() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager())!=null){
            File imagenFile = null;
            try {
                imagenFile = crearImagen();
            }catch (IOException e){
                e.printStackTrace();
            }
            if (imagenFile!=null){
                Uri fotoUri = FileProvider.getUriForFile(this,"com.example.proyectofinal",imagenFile);
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT,fotoUri);
                startActivityForResult(intentCamara,1);
            }
        }
    }

    private File crearImagen() throws IOException {
        String nombreFoto = "FOTO_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagenF = File.createTempFile(nombreFoto,"jpg",directorio);

        rutaPath = imagenF.getAbsolutePath();
        return imagenF;
    }


    public static class miViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public miViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setNombre(String nombre){
            TextView textViewNombre = mView.findViewById(R.id.nombreCardIncidente);
            textViewNombre.setText(nombre);
        }
        public void setTitulo(String titulo){
            TextView textViewTitulo = mView.findViewById(R.id.tituloCardIncidente);
            textViewTitulo.setText(titulo);
        }
        public void setMensaje(String mensaje){
            TextView textViewMensaje = mView.findViewById(R.id.mensajeRecibido);
            textViewMensaje.setText(mensaje);
        }
    }
}