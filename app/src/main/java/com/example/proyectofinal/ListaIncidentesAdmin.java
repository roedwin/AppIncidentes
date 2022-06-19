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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;


import com.example.proyectofinal.Adaptadores.IncidenteAdaptador;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;




import javax.crypto.Cipher;

public class ListaIncidentesAdmin extends AppCompatActivity {

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
    private static final int permiso_almacenamiento = 1;
    private boolean permisos;

    private String Dir_pdf = Environment.getExternalStorageDirectory()+"/AppPdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_incidentes_admin);

        recyclerView = findViewById(R.id.rviewIncidentesAdmin);
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
                .setQuery(reference, ModeloIncidente.class)
                .build();

        mAdapter = new IncidenteAdaptador(options, ListaIncidentesAdmin.this);
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
        int estadoPermiso = ContextCompat.checkSelfPermission(ListaIncidentesAdmin.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(estadoPermiso == PackageManager.PERMISSION_GRANTED){
            permisos_concedido();
        }else{
            ActivityCompat.requestPermissions(ListaIncidentesAdmin.this,new String[]{
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
                Toast.makeText(ListaIncidentesAdmin.this, "El directorio fue creado", Toast.LENGTH_SHORT).show();
            }
        }
        permisos = true;
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
        //Toast.makeText(ListaIncidentesAdmin.this, "Ha ocurrido un error se requiere que conceda los permisos", Toast.LENGTH_SHORT).show();
    }
    public void exportarPDF(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String nombreArchivo = "reporte_"+dtf.format(LocalDateTime.now())+".pdf";
        File file;
        try {
            file = new File(getExternalFilesDir(null), nombreArchivo);
            if (!file.exists()) {
                file.createNewFile();
            }
            PdfWriter pdfEscrito = new PdfWriter(file);
            PdfDocument pdfDocumento = new PdfDocument(pdfEscrito);
            Document documento = new Document(pdfDocumento);
            pdfDocumento.setDefaultPageSize(PageSize.A4);

            //17 pintando la imagen en el documento pdf
            Drawable logo = getDrawable(R.drawable.ir);
            Bitmap lienzo =((BitmapDrawable)logo).getBitmap();
            ByteArrayOutputStream streamArchivo = new ByteArrayOutputStream();
            lienzo.compress(Bitmap.CompressFormat.PNG,100,streamArchivo);
            byte[] datosLienzo = streamArchivo.toByteArray();

            //pintar el encabezado de la tabla

            float columnWidth[] = {140,140,140,140};

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
                switch (hora){
                    case 13:
                        hora = 1;
                        break;
                    case 14:
                        hora = 2;
                        break;
                    case 15:
                        hora = 3;
                        break;
                    case 16:
                        hora = 4;
                        break;
                    case 17:
                        hora = 5;
                        break;
                    case 18:
                        hora = 6;
                        break;
                    case 19:
                        hora = 7;
                        break;
                    case 20:
                        hora = 8;
                        break;
                    case 21:
                        hora = 9;
                        break;
                    case 22:
                        hora = 10;
                        break;
                    case 23:
                        hora = 11;
                        break;
                    case 2:
                        hora = 12;
                        break;
                }
                hour = hora + ":" + minutos + ":" + segundos + " pm";
            }

            //pintar el logo dentro del pdf
            ImageData imgDatos= ImageDataFactory.create(datosLienzo);
            com.itextpdf.layout.element.Image elLogo = new Image(imgDatos);
            elLogo.setHeight(60);
            elLogo.setHorizontalAlignment(HorizontalAlignment.LEFT);

            Table table1 = new Table(columnWidth);

            //Encabezado
            //fila 1
            table1.addCell(new Cell(4,1).add(elLogo).setBorder(Border.NO_BORDER));
            table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

            //fila 2
            //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table1.addCell(new Cell(1,3).add(new Paragraph("")).setBorder(Border.NO_BORDER));
            //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

            //fila 3
            //table1.addCell(new Cell().add(new Paragraph("")));
            table1.addCell(new Cell(1,3).add(new Paragraph("Reporte General de Incidentes")
                            .setFontSize(20f))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold().setBorder(Border.NO_BORDER));
            //table1.addCell(new Cell().add(new Paragraph("")));

            //fila 4
            //table1.addCell(new Cell().add(new Paragraph("")));
            table1.addCell(new Cell(1,3).add(new Paragraph("")).setBorder(Border.NO_BORDER));
            //table1.addCell(new Cell().add(new Paragraph("")));

            //fila 5
            //table1.addCell(new Cell().add(new Paragraph("")));
            table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
            table1.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER));
            table1.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));

            documento.add(table1);
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("Fecha: "+date));
            documento.add(new Paragraph("Hora: "+hour));

            //documento.add(new Paragraph("\n"));

            //pintar las columnas de la tabla
            float anchoColumna[]= {140f,140f,80f,140f};
            Table tabla = new Table(anchoColumna);


            Cell celdaID = new Cell();
            celdaID.setBackgroundColor(ColorConstants.BLACK);
            celdaID.setFontColor(ColorConstants.WHITE);
            celdaID.add(new Paragraph("Titulo"));
            tabla.addCell(celdaID);

            Cell celdaPlaca = new Cell();
            celdaPlaca.setBackgroundColor(ColorConstants.BLACK);
            celdaPlaca.setFontColor(ColorConstants.WHITE);
            celdaPlaca.add(new Paragraph("Usuario"));
            tabla.addCell(celdaPlaca);

            Cell celdaMarca = new Cell();
            celdaMarca.setBackgroundColor(ColorConstants.BLACK);
            celdaMarca.setFontColor(ColorConstants.WHITE);
            celdaMarca.add(new Paragraph("Estado"));
            tabla.addCell(celdaMarca);

            Cell celdaDesc = new Cell();
            celdaDesc.setBackgroundColor(ColorConstants.BLACK);
            celdaDesc.setFontColor(ColorConstants.WHITE);
            celdaDesc.add(new Paragraph("Descripcion"));
            tabla.addCell(celdaDesc);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot myData : snapshot.getChildren()){
                        Cell ID = new Cell();
                        ID.setBackgroundColor(ColorConstants.WHITE);
                        ID.setFontColor(ColorConstants.BLACK);
                        ID.add(new Paragraph(myData.child("titulo").getValue().toString()));
                        tabla.addCell(ID);

                        Cell Placa = new Cell();
                        Placa.setBackgroundColor(ColorConstants.WHITE);
                        Placa.setFontColor(ColorConstants.BLACK);
                        Placa.add(new Paragraph(myData.child("nombre").getValue().toString()));
                        tabla.addCell(Placa);

                        Cell Marca = new Cell();
                        Marca.setBackgroundColor(ColorConstants.WHITE);
                        Marca.setFontColor(ColorConstants.BLACK);
                        Marca.add(new Paragraph(myData.child("estado").getValue().toString()));
                        tabla.addCell(Marca);

                        Cell Descripcion = new Cell();
                        Descripcion.setBackgroundColor(ColorConstants.WHITE);
                        Descripcion.setFontColor(ColorConstants.BLACK);
                        Descripcion.add(new Paragraph(myData.child("descripcion").getValue().toString()));
                        tabla.addCell(Descripcion);
                    }

                    //miBD.close();
                    documento.add(tabla);
                    documento.close();
                    abrirPDF(file,ListaIncidentesAdmin.this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abrirPDF(File archivo, Context contexto){
        try {
            String nombreArchivo = String.valueOf(archivo);
            File Referenciaarchivo = new File(nombreArchivo);
            if (Referenciaarchivo.exists()){
                Uri url = FileProvider.getUriForFile(contexto,"com.example.proyectofinal",Referenciaarchivo);
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
        mAdapter = new IncidenteAdaptador(optionsS, ListaIncidentesAdmin.this);
        mAdapter.startListening();
        recyclerView.setAdapter(mAdapter);
    }


}