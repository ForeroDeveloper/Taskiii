package com.fordev.taski.balance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.CalificarAppTaski;
import com.fordev.taski.R;
import com.fordev.taski.VentasNegocio;
import com.fordev.taski.adaptadores.AdaptadorListaFacturas;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.fordev.taski.otros.ProgressAnimation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //mis declaraciones
    RecyclerView listaDeFacturas;
    AdaptadorListaFacturas adaptadorListaFacturas;
    TextView fechaActual,totalBalance,ventasEnDeuda,totalDeVentas,txtTotalFacturas;
    Calendar calendar = Calendar.getInstance();
    ImageView ic_sumar_fecha, ic_restar_fecha,ic_select_fecha_dialog;
    CardView sinContenido;
    RelativeLayout sinContenidoDos;
    MaterialButton nuevaFactura;
    private int dia,mes,ano;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    DatabaseReference databaseReference;
    LinearProgressIndicator progressIndicator;
    com.getbase.floatingactionbutton.FloatingActionButton faq_restar_fecha,faq_sumar_fecha;
    ShimmerFrameLayout shimmerFrameLayout;
    public DiaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiaFragment newInstance(String param1, String param2) {
        DiaFragment fragment = new DiaFragment();
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
        View view = inflater.inflate(R.layout.fragment_dia, container, false);
        //tiempo
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        //instancias
        listaDeFacturas = view.findViewById(R.id.lista_de_facturas_dia);
        fechaActual = view.findViewById(R.id.txtFechaSelect);
        totalBalance = view.findViewById(R.id.totalDeuda);
        ventasEnDeuda = view.findViewById(R.id.totalPorCobrarPagado);
        totalDeVentas = view.findViewById(R.id.totalDeVentas);
        sinContenido = view.findViewById(R.id.sinContenido);
        sinContenidoDos = view.findViewById(R.id.titulos);
        progressIndicator = view.findViewById(R.id.indicador);
        faq_restar_fecha = view.findViewById(R.id.faq_restar_fecha);
        faq_sumar_fecha = view.findViewById(R.id.faq_sumar_fecha);
        ic_sumar_fecha = view.findViewById(R.id.ic_sumar_fecha);
        ic_restar_fecha = view.findViewById(R.id.ic_restar_fehca);
        ic_select_fecha_dialog = view.findViewById(R.id.ic_seleccionar_fecha);
        nuevaFactura = view.findViewById(R.id.nuevaFactura);
        txtTotalFacturas = view.findViewById(R.id.todas);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmerAnimation();
        //seteos
        fechaActual.setText(sdf.format(calendar.getTime()));
        sinContenido.setVisibility(View.GONE);


        nuevaFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VentasNegocio.class));
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
        totalBalance.setText(String.valueOf("$ " + nformat.format(0)));
        ventasEnDeuda.setText(String.valueOf("$ " + nformat.format(0)));
        totalDeVentas.setText(String.valueOf("$ " + nformat.format(0)));
        progressIndicator.setProgress(100);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                child("facturasCreadas");
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
                            totalBalance.setText(String.valueOf("$ " + nformat.format(sumDeuda)));

                        }
                    }

                }
                //Ciclo para el balance General
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
                            ventasEnDeuda.setText(String.valueOf("$ " + nformat.format(balanceGeneral)));                        }
                    }

                }
                //ciclo para el total de ventas incluyendo deudas
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("fechaRegistro");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(calendar.getTime()))) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            sumTotal += tValue;
                        totalDeVentas.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                    }

                }

                if (sumTotal==0){
                    progressIndicator.setProgress(100);
                    sinContenido.setVisibility(View.VISIBLE);
                    sinContenidoDos.setVisibility(View.GONE);
                }else if(balanceGeneral==0){
                    totalBalance.setText(String.valueOf("$ " + nformat.format(sumDeuda)));
                    progressIndicator.setProgress(0);
                }else {

                    int p =  balanceGeneral * 100 / sumTotal;
//                    progressIndicator.setProgress(p);
                    ProgressAnimation progressAnimation = new ProgressAnimation(progressIndicator, 0, p);
                    progressAnimation.setDuration(1000);
                    progressIndicator.setAnimation(progressAnimation);
                    sinContenido.setVisibility(View.GONE);
                    sinContenidoDos.setVisibility(View.VISIBLE);
                }

                if (sumDeuda!=0){
                    sinContenido.setVisibility(View.GONE);
                    sinContenidoDos.setVisibility(View.VISIBLE);
                }

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                listaDeFacturas.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LinearLayoutManager mLayaoutManager = new LinearLayoutManager(getContext());
        mLayaoutManager.setReverseLayout(true);
        mLayaoutManager.setStackFromEnd(true);

        listaDeFacturas.setLayoutManager(mLayaoutManager);
        FirebaseRecyclerOptions<ModeloFacturaCreada> options =
                new FirebaseRecyclerOptions.Builder<ModeloFacturaCreada>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                                child("facturasCreadas").orderByChild("fechaRegistro").startAt(sdf.format(calendar.getTime())).endAt(sdf.format(calendar.getTime())), ModeloFacturaCreada.class)
                        .build();
        adaptadorListaFacturas=new AdaptadorListaFacturas(options);
        listaDeFacturas.setAdapter(adaptadorListaFacturas);

        adaptadorListaFacturas.startListening();

        adaptadorListaFacturas.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                txtTotalFacturas.setText(String.valueOf(adaptadorListaFacturas.getItemCount()));
                if (adaptadorListaFacturas.getItemCount() == 1){
                    SharedPreferences preferences = getContext().getSharedPreferences("TUTORIAL", Context.MODE_PRIVATE);
                    boolean unaFactura = preferences.getBoolean("DetallesFactura" , false);

                    if (!unaFactura){
                        SharedPreferences.Editor editor  = preferences.edit();
                        editor.putBoolean("DetallesFactura", true);
                        editor.apply();

                        DialogPlus dialog = DialogPlus.newDialog(getContext())
                                .setContentHolder(new ViewHolder(R.layout.dialog_ver_detalles_factura))
                                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                .setGravity(Gravity.CENTER)
                                .setContentBackgroundResource(android.R.color.transparent)
                                .create();

                        View view1 = dialog.getHolderView();

                        MaterialButton btn_dismis = view1.findViewById(R.id.btn_dismiss);

                        btn_dismis.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }

            }
        });
        adaptadorListaFacturas.notifyDataSetChanged();

    }


    @Override
    public void onStart() {
        super.onStart();
        cargarDatosSegunFecha(calendar);
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorListaFacturas.stopListening();
    }
}