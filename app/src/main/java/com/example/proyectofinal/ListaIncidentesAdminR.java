package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.proyectofinal.Adaptadores.IncidenteAdaptador;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import javax.crypto.Cipher;

public class ListaIncidentesAdminR extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_incidentes_admin_r);

        recyclerView = findViewById(R.id.rviewIncidentesAdminr);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        cardView = findViewById(R.id.idCardAdmin);

        searchView = findViewById(R.id.searchBarIAdmr);

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
                .setQuery(reference.orderByChild("estado").equalTo("Resuelto"), ModeloIncidente.class)
                .build();

        mAdapter = new IncidenteAdaptador(options, ListaIncidentesAdminR.this);
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
        mAdapter = new IncidenteAdaptador(optionsS, ListaIncidentesAdminR.this);
        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
    }
}