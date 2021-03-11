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
import com.fordev.taski.VentasNegocio;
import com.fordev.taski.adaptadores.AdaptadorListaFacturasEnGastos;
import com.fordev.taski.modelos.ModeloFacturaCreadaGastos;
import com.google.android.material.button.MaterialButton;
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
 * Use the {@link DiaFragmentGastos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaFragmentGastos extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //mis declaraciones
    RecyclerView listaDeFacturas;
    AdaptadorListaFacturasEnGastos adaptadorListaFacturasEnGastos;
    TextView fechaActual, totalDeuda, totalDeGastos,txtTotalFacturas;
    Calendar calendar = Calendar.getInstance();
    ImageView ic_sumar_fecha, ic_restar_fecha,ic_select_fecha_dialog;
    CardView sinContenido;
    RelativeLayout sinContenidoDos;
    private int dia,mes,ano;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    DatabaseReference databaseReference;
    CircularProgressBar progressIndicator;
    com.getbase.floatingactionbutton.FloatingActionButton faq_restar_fecha,faq_sumar_fecha;
    MaterialButton nuevaFactura;

    public DiaFragmentGastos() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiaFragmentGastos newInstance(String param1, String param2) {
        DiaFragmentGastos fragment = new DiaFragmentGastos();
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
        View view = inflater.inflate(R.layout.fragment_dia_gastos, container, false);
        //tiempo
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        //instancias
        listaDeFacturas = view.findViewById(R.id.lista_de_facturas_dia);
        fechaActual = view.findViewById(R.id.txtFechaSelect);
        totalDeuda = view.findViewById(R.id.totalDeuda);//si
        totalDeGastos = view.findViewById(R.id.totalDeGastos);
        sinContenido = view.findViewById(R.id.sinContenido);
        sinContenidoDos = view.findViewById(R.id.titulos);
        progressIndicator = view.findViewById(R.id.indicador);
        faq_restar_fecha = view.findViewById(R.id.faq_restar_fecha);
        faq_sumar_fecha = view.findViewById(R.id.faq_sumar_fecha);
        ic_sumar_fecha = view.findViewById(R.id.ic_sumar_fecha);
        ic_restar_fecha = view.findViewById(R.id.ic_restar_fehca);
        ic_select_fecha_dialog = view.findViewById(R.id.ic_seleccionar_fecha);
        txtTotalFacturas = view.findViewById(R.id.todas);
        nuevaFactura = view.findViewById(R.id.nuevaFactura);
        //seteos
        fechaActual.setText(sdf.format(calendar.getTime()));

        nuevaFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GastosNegocio.class));
            }
        });


        ic_sumar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(1);
                cargarDatosSegunFecha(calendar);
            }
        });

        ic_restar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(-1);
                cargarDatosSegunFecha(calendar);
            }
        });

        faq_sumar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(1);
                cargarDatosSegunFecha(calendar);
            }
        });

        faq_restar_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDeFecha(-1);
                cargarDatosSegunFecha(calendar);
            }
        });

        ic_select_fecha_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar Cal = Calendar.getInstance();
                dia=Cal.get(Calendar.DAY_OF_MONTH);
                mes=Cal.get(Calendar.MONTH);
                ano=Cal.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        fechaActual.setText(sdf.format(calendar.getTime()));
                        cargarDatosSegunFecha(calendar);
                    }
                },ano,mes,dia);
                datePickerDialog.show();
            }
        });



        return view;
    }

    private void selectorDeFecha(int i) {
        calendar.add(Calendar.DATE, i);
        fechaActual.setText(sdf.format(calendar.getTime()));
    }

    private void cargarDatosSegunFecha(Calendar calendar) {
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

                    if (fValue.equals(sdf.format(calendar.getTime()))) {
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

                    if (fValue.equals(sdf.format(calendar.getTime()))) {
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

                    if (fValue.equals(sdf.format(calendar.getTime()))) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            sumTotal += tValue;
                        totalDeGastos.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                        //totalDeVentas.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                    }

                }

                if (sumTotal==0){
                    progressIndicator.setProgress(100);
                    sinContenido.setVisibility(View.VISIBLE);
                    sinContenidoDos.setVisibility(View.GONE);

                }else {

                    int p =  balanceGeneral * 100 / sumTotal;
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
                                child("facturasCreadasEnGastos").orderByChild("fechaRegistro").startAt(sdf.format(calendar.getTime())).endAt(sdf.format(calendar.getTime())), ModeloFacturaCreadaGastos.class)
                        .build();
        adaptadorListaFacturasEnGastos =new AdaptadorListaFacturasEnGastos(options);
        listaDeFacturas.setAdapter(adaptadorListaFacturasEnGastos);

        adaptadorListaFacturasEnGastos.startListening();
        adaptadorListaFacturasEnGastos.notifyDataSetChanged();

        adaptadorListaFacturasEnGastos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                txtTotalFacturas.setText(String.valueOf(adaptadorListaFacturasEnGastos.getItemCount()));

            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        cargarDatosSegunFecha(calendar);
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorListaFacturasEnGastos.stopListening();
    }
}