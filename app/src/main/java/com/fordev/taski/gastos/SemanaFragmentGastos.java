package com.fordev.taski.gastos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.GastosNegocio;
import com.fordev.taski.R;
import com.fordev.taski.adaptadores.AdaptadorListaFacturas;
import com.fordev.taski.adaptadores.AdaptadorListaFacturasEnGastos;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.fordev.taski.modelos.ModeloFacturaCreadaGastos;
import com.fordev.taski.otros.ProgressAnimation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shunan.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SemanaFragmentGastos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SemanaFragmentGastos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //mis declaraciones
    RecyclerView listaDeFacturas;
    AdaptadorListaFacturasEnGastos adaptadorListaFacturasEnGastos;
    TextView fechaActual, totalDeuda, ventasEnDeuda, totalDeGastos, facturasTotales, txtFechaSelectFin;
    Calendar fechaInicio = Calendar.getInstance();
    Calendar fechaFin = Calendar.getInstance();
    Calendar fechauno = Calendar.getInstance();
    Calendar fechados = Calendar.getInstance();
    Calendar fechatres = Calendar.getInstance();
    Calendar fechacuatro = Calendar.getInstance();
    Calendar fechacinco = Calendar.getInstance();
    ImageView ic_sumar_fecha, ic_restar_fecha;
    CardView sinContenido;
    RelativeLayout sinContenidoDos;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    DatabaseReference databaseReference;
    CircularProgressBar progressIndicator;
    com.getbase.floatingactionbutton.FloatingActionButton faq_restar_fecha, faq_sumar_fecha;
    MaterialButton nuevaFactura;

    public SemanaFragmentGastos() {
        // Required empty public constructor
    }

    public static SemanaFragmentGastos newInstance(String param1, String param2) {
        SemanaFragmentGastos fragment = new SemanaFragmentGastos();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_semana_gastos, container, false);

        //tiempo
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        //instancias
        listaDeFacturas = view.findViewById(R.id.lista_de_facturas_dia);
        fechaActual = view.findViewById(R.id.txtFechaSelect);
        totalDeuda = view.findViewById(R.id.totalDeuda);//si
        totalDeGastos = view.findViewById(R.id.totalDeGastos);
        sinContenido = view.findViewById(R.id.sinContenido);
        sinContenidoDos = view.findViewById(R.id.titulos);
        txtFechaSelectFin = view.findViewById(R.id.txtFechaSelectFin);
        progressIndicator = view.findViewById(R.id.indicador);
        faq_restar_fecha = view.findViewById(R.id.faq_restar_fecha);
        faq_sumar_fecha = view.findViewById(R.id.faq_sumar_fecha);
        ic_sumar_fecha = view.findViewById(R.id.ic_sumar_fecha);
        ic_restar_fecha = view.findViewById(R.id.ic_restar_fehca);
        nuevaFactura = view.findViewById(R.id.nuevaFactura);
        facturasTotales = view.findViewById(R.id.todas);

        //seteos
        fechaInicio.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fechados.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fechados.add(Calendar.DATE, 1);
        fechatres.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fechatres.add(Calendar.DATE, 2);
        fechacuatro.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fechacuatro.add(Calendar.DATE, 3);
        fechacinco.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        fechacinco.add(Calendar.DATE, 4);
        fechaActual.setText(sdf.format(fechaInicio.getTime()));
        fechaFin.add(Calendar.DATE, 7);
        txtFechaSelectFin.setText(sdf.format(fechaFin.getTime()));

        cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);

        nuevaFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GastosNegocio.class));
            }
        });


        ic_sumar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(7);
                cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);
            }
        });

        ic_restar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(-7);
                cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);
            }
        });

        faq_sumar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(7);
                cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);
            }
        });

        faq_restar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(-7);
                cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);
            }
        });

        return view;
    }

    private void selectorDeFecha(int i) {

        fechaInicio.add(Calendar.DATE, i);
        fechados.add(Calendar.DATE, i);
        fechatres.add(Calendar.DATE, i);
        fechacuatro.add(Calendar.DATE, i);
        fechacinco.add(Calendar.DATE, i);
        fechaFin.add(Calendar.DATE, i);
        fechaActual.setText(sdf.format(fechaInicio.getTime()));
        txtFechaSelectFin.setText(sdf.format(fechaFin.getTime()));
            

    }

    private void cargarDatosSegunFecha(Calendar fechaInicio, Calendar fechaFin, Calendar fechados, Calendar fechatres, Calendar fechacuatro, Calendar fechacinco) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        totalDeuda.setText(String.valueOf("$ " + nformat.format(0)));
        //ventasEnDeuda.setText(String.valueOf("$ " + nformat.format(0)));
        totalDeGastos.setText(String.valueOf("$ " + nformat.format(0)));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                child("facturasCreadasEnGastos");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int balanceGeneral = 0;
                int sumDeuda = 0;
                int sumTotal = 0;

                //Ciclo para ventas en deuda
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("fechaRegistro");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime())) || fValue.equals(sdf.format(fechaFin.getTime())) || fValue.equals(sdf.format(fechados.getTime())) ||
                            fValue.equals(sdf.format(fechatres.getTime())) || fValue.equals(sdf.format(fechacuatro.getTime())) || fValue.equals(sdf.format(fechacinco.getTime()))) {
                        Object estadoDePago = map.get("estadoDePago");
                        boolean eValue = Boolean.parseBoolean(String.valueOf(estadoDePago));
                        if (!eValue) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            sumDeuda += tValue;
                            totalDeuda.setText(String.valueOf("$ " + nformat.format(sumDeuda)));
                            // ventasEnDeuda.setText(String.valueOf("$ " + nformat.format(balanceGeneral)));
                        }
                    }

                }
                //Ciclo para el balance General gastos
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("fechaRegistro");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime())) || fValue.equals(sdf.format(fechaFin.getTime())) || fValue.equals(sdf.format(fechados.getTime())) ||
                            fValue.equals(sdf.format(fechatres.getTime())) || fValue.equals(sdf.format(fechacuatro.getTime())) || fValue.equals(sdf.format(fechacinco.getTime()))) {
                        Object estadoDePago = map.get("estadoDePago");
                        boolean eValue = Boolean.parseBoolean(String.valueOf(estadoDePago));
                        if (eValue) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            balanceGeneral += tValue;
                            //totalBalance.setText(String.valueOf("$ " + nformat.format(sumDeuda)));

                        }
                    }

                }
                //ciclo para el total de gastos incluyendo deudas de gastos
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("fechaRegistro");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime())) || fValue.equals(sdf.format(fechaFin.getTime())) || fValue.equals(sdf.format(fechados.getTime())) ||
                            fValue.equals(sdf.format(fechatres.getTime())) || fValue.equals(sdf.format(fechacuatro.getTime())) || fValue.equals(sdf.format(fechacinco.getTime()))) {
                        Object total = map.get("totalCalculado");
                        int tValue = Integer.parseInt(String.valueOf(total));
                        sumTotal += tValue;
                        totalDeGastos.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                        //totalDeVentas.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                    }

                }

                if (sumTotal == 0) {
                    progressIndicator.setProgress(100);
                    sinContenido.setVisibility(View.VISIBLE);
                    sinContenidoDos.setVisibility(View.GONE);

                } else {

                    int p = balanceGeneral * 100 / sumTotal;
                    progressIndicator.setProgress(p);
                    sinContenido.setVisibility(View.GONE);
                    sinContenidoDos.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager mLayaoutManager = new LinearLayoutManager(getContext());
        mLayaoutManager.setReverseLayout(true);
        mLayaoutManager.setStackFromEnd(true);

        listaDeFacturas.setLayoutManager(mLayaoutManager);
        FirebaseRecyclerOptions<ModeloFacturaCreadaGastos> options =
                new FirebaseRecyclerOptions.Builder<ModeloFacturaCreadaGastos>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                                child("facturasCreadasEnGastos").orderByChild("fechaRegistro").startAt(sdf.format(fechaInicio.getTime())).endAt(sdf.format(fechaFin.getTime())), ModeloFacturaCreadaGastos.class)
                        .build();
        adaptadorListaFacturasEnGastos = new AdaptadorListaFacturasEnGastos(options);
        listaDeFacturas.setAdapter(adaptadorListaFacturasEnGastos);
        adaptadorListaFacturasEnGastos.startListening();
        adaptadorListaFacturasEnGastos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                facturasTotales.setText(String.valueOf(adaptadorListaFacturasEnGastos.getItemCount()));
            }
        });
        adaptadorListaFacturasEnGastos.notifyDataSetChanged();

    }


    @Override
    public void onStart() {
        super.onStart();
        cargarDatosSegunFecha(fechaInicio, fechaFin, fechados, fechatres, fechacuatro, fechacinco);
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorListaFacturasEnGastos.stopListening();
    }
}