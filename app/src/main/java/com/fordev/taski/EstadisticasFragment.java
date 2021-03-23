package com.fordev.taski;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstadisticasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadisticasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView ventasTotales, gastosTotales, gastosMenosVentas, fechaActual, ventasTotalesPorCobrar, gastosTotalesPorPagar;
    ImageView ic_sumar_fecha, ic_restar_fecha;
    CardView escalar;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM");
    DatabaseReference databaseReference, databaseReferenceGastos;

    int ventas = 0;
    int gastos = 0;

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstadisticasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadisticasFragment newInstance(String param1, String param2) {
        EstadisticasFragment fragment = new EstadisticasFragment();
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
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        ventasTotales = view.findViewById(R.id.ventasTotales);
        ventasTotalesPorCobrar = view.findViewById(R.id.ventasTotalesPorCobrar);
        gastosTotalesPorPagar = view.findViewById(R.id.gastosTotalesPorPagar);
        gastosTotales = view.findViewById(R.id.gastosTotales);
        gastosMenosVentas = view.findViewById(R.id.gastosMenosVentas);
        fechaActual = view.findViewById(R.id.txtFechaSelect);
        ic_sumar_fecha = view.findViewById(R.id.ic_sumar_fecha);
        ic_restar_fecha = view.findViewById(R.id.ic_restar_fehca);
        escalar = view.findViewById(R.id.escalar);

/*        Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        escalar.startAnimation(connectingAnimation);*/


        //zone
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        calendar.get(Calendar.MONTH);
        fechaActual.setText(sdf.format(calendar.getTime()));

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


        return view;
    }

    private void selectorDeFecha(int i) {
        calendar.add(Calendar.MONTH, i);
        fechaActual.setText(sdf.format(calendar.getTime()));
    }

    private void cargarDatosSegunFecha(Calendar fechaInicio) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        ventasTotales.setText(String.valueOf("$ " + nformat.format(0)));
        ventasTotalesPorCobrar.setText(String.valueOf("$ " + nformat.format(0)));
        gastosTotales.setText(String.valueOf("$ " + nformat.format(0)));
        gastosTotalesPorPagar.setText(String.valueOf("$ " + nformat.format(0)));
        ventas = 0;
        gastos = 0;
        if (ventas!=0 && gastos!=0){
            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(ventas - gastos)));
        }else {
            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(0)));
        }



        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                child("facturasCreadas");

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int balanceGeneral = 0;
                int sumDeuda = 0;
                int sumTotal = 0;

                //ciclo para el total de ventas incluyendo deudas
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("month");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime()))) {
                        Object total = map.get("totalCalculado");
                        int tValue = Integer.parseInt(String.valueOf(total));
                        sumTotal += tValue;
                        ventas = sumTotal;
                        if (ventas != 0) {
                            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(ventas - gastos)));
                        }else {
                            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(0)));
                        }
                        ventasTotales.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                    }

                }


                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("month");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime()))) {
                        Object estadoDePago = map.get("estadoDePago");
                        boolean eValue = Boolean.parseBoolean(String.valueOf(estadoDePago));
                        if (!eValue) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            sumDeuda += tValue;
                            ventasTotalesPorCobrar.setText(String.valueOf("$ " + nformat.format(sumDeuda)));
                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceGastos = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").
                child("facturasCreadasEnGastos");

        databaseReferenceGastos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sumDeuda = 0;
                int sumTotal = 0;

                //ciclo para el total de ventas incluyendo deudas
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("month");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime()))) {
                        Object total = map.get("totalCalculado");
                        int tValue = Integer.parseInt(String.valueOf(total));
                        sumTotal += tValue;
                        gastos = sumTotal;
                        if (gastos != 0) {
                            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(ventas - gastos)));
                        }else {
                            gastosMenosVentas.setText(String.valueOf("$ " + nformat.format(0)));
                        }
                        gastosTotales.setText(String.valueOf("$ " + nformat.format(sumTotal)));
                    }

                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object fecha = map.get("month");
                    String fValue = String.valueOf(fecha);

                    if (fValue.equals(sdf.format(fechaInicio.getTime()))) {
                        Object estadoDePago = map.get("estadoDePago");
                        boolean eValue = Boolean.parseBoolean(String.valueOf(estadoDePago));
                        if (!eValue) {
                            Object total = map.get("totalCalculado");
                            int tValue = Integer.parseInt(String.valueOf(total));
                            sumDeuda += tValue;
                            gastosTotalesPorPagar.setText(String.valueOf("$ " + nformat.format(sumDeuda)));
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        cargarDatosSegunFecha(calendar);
    }
}