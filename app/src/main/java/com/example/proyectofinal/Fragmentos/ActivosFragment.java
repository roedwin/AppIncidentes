package com.example.proyectofinal.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectofinal.Adaptadores.IncidenteAdaptador;
import com.example.proyectofinal.ListaIncidentesAdmin;
import com.example.proyectofinal.Modelos.ModeloIncidente;
import com.example.proyectofinal.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    IncidenteAdaptador mAdapter;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ArrayList<ModeloIncidente> arrayList;

    public ActivosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivosFragment newInstance(String param1, String param2) {
        ActivosFragment fragment = new ActivosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_activos, container, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.rviewIncidentesAdminTabActivos);
        reference = FirebaseDatabase.getInstance().getReference().child("TblIncidentes");
        /*arrayList = new ArrayList<>();
        recyclerView = vista.findViewById(R.id.rviewIncidentesAdminTabActivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        llenarLista();*/
        FirebaseRecyclerOptions<ModeloIncidente> options = new FirebaseRecyclerOptions.Builder<ModeloIncidente>()
                .setQuery(reference, ModeloIncidente.class)
                .build();

        mAdapter = new IncidenteAdaptador(options, getActivity());
        mAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        return vista;
    }

    private void llenarLista() {

    }
}