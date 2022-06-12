package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectofinal.Adaptadores.IncidenteAdaptador;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.Cipher;

public class ListaIncidentesAdminA extends AppCompatActivity {
    private RecyclerView recyclerView;


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
    private String llamarTitulo;
    private String llamarDescripcion;
    private String llamarFecha;
    private String llamarEstado;
    String usuarioRecibido;
    String estadoRecibido;

    CardView cardView;


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

    Button verDetalleIncidente;


    ImageView img;
    String rutaPath;

    ArrayList<ModeloIncidente> listUser;
    SearchView searchView;

    private String sKey = "kpass";
    private int T_LEN = 128;
    private Cipher encriptedCipher;
    private int KEY_SIZE = 128;

    IncidenteAdaptador mAdapter;

    Button btnPdf;
    private static final int permiso_almacenamiento =1;
    private boolean permisos = false;

    private String Dir_pdf = Environment.getExternalStorageDirectory()+"/AppPdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_incidentes_admin2);

        recyclerView = findViewById(R.id.rviewIncidentesAdminA);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btnPdf = findViewById(R.id.generarPDF);
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos();
                exportarPDF();
            }
        });

        cardView = findViewById(R.id.idCardAdmin);

        searchView = findViewById(R.id.searchBarIAdm);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        load = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();



        user = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        usuarioRecibido = getIntent().getExtras().getString("usuario");
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModeloIncidente> options = new FirebaseRecyclerOptions.Builder<ModeloIncidente>()
                .setQuery(reference.orderByChild("estado").equalTo("Activo"), ModeloIncidente.class)
                .build();

        mAdapter = new IncidenteAdaptador(options, ListaIncidentesAdminA.this);
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();

        search_view();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public void verificarPermisos(){
        //verificando si el activity tiene permiso de escritura
        int estadoPermiso = ContextCompat.checkSelfPermission(ListaIncidentesAdminA.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(estadoPermiso == PackageManager.PERMISSION_GRANTED){
            permisos_concedido();
        }else{
            ActivityCompat.requestPermissions(ListaIncidentesAdminA.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },permiso_almacenamiento);
        }
    }

    public void permisos_concedido(){
        File carpeta= new File(Dir_pdf);
        if(carpeta.exists()){

        }
        else
        {
            if(carpeta.mkdir()){
                Toast.makeText(ListaIncidentesAdminA.this, "El directorio fue creado", Toast.LENGTH_SHORT).show();
            }
        }
        permisos= true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case permiso_almacenamiento:
                if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    permisos_concedido();
                }else{
                    permisosDenegados();
                }
                break;
        }
    }
    public void permisosDenegados(){
        Toast.makeText(ListaIncidentesAdminA.this, "Ha ocurrido un error se requiere que conceda los permisos", Toast.LENGTH_SHORT).show();
    }
    public void exportarPDF(){

    }

    public void abrirPDF(File archivo, Context contexto){
        try {
            String nombreArchivo = String.valueOf(archivo);
            File Referenciaarchivo = new File(nombreArchivo);
            if (Referenciaarchivo.exists()){
                Uri url = FileProvider.getUriForFile(contexto,contexto.getApplicationContext().getPackageName()+".provider",Referenciaarchivo);
                Intent ventana = new Intent(Intent.ACTION_VIEW);
                ventana.setDataAndType(url,"application/pdf");
                ventana.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                ventana.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(ventana);
            }
        }catch (Exception error){
            Log.e("Error","Error al abrir el documento");
        }
    }

    private void search_view() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    private void textSearch(String s) {
        FirebaseRecyclerOptions<ModeloIncidente> optionsS =
                new FirebaseRecyclerOptions.Builder<ModeloIncidente>()
                        .setQuery(reference.orderByChild("nombre").startAt(s).endAt(s+"~"), ModeloIncidente.class).setLifecycleOwner(this).build();
        mAdapter = new IncidenteAdaptador(optionsS, ListaIncidentesAdminA.this);
        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
    }
}