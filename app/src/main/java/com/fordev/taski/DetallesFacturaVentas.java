package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.adaptadores.AdaptadorListaProductosEnInventario;
import com.fordev.taski.modelos.ModeloVenta;
import com.fordev.taski.modelos.ModeloVentaInventario;
import com.fordev.taski.otros.ProgressAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.HashMap;
import java.util.Map;

public class DetallesFacturaVentas extends AppCompatActivity {

    TextView nombreCliente, totalVenta, totalPorCobrar,
            fecha,txtMetodoDePago,txtEstadoDePago,txt_metodo_de_pago, totalPorCobrarPagado,totalDeVentas;
    RecyclerView lista_de_productos_venta,lista_de_productos_venta_inventario;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase, firebaseDatabase1;
    AdaptadorListaProductos adaptadorListaProductos;
    AdaptadorListaProductosEnInventario adaptadorListaProductosEnInventario;
    RelativeLayout fondoPrincipal;
    MaterialButton faq_abonar;
    CardView metodo_de_pago;
    LinearProgressIndicator progressIndicator;
    int sumaTotalItems = 0;
    int sumaTotalItems2 = 0;
    int totalVentaGlobal = 0;
    int totalAbonadoGlobal = 0;
    int cantidaActualizada = 0;
    int saberSiEsCero = 0;
    int totalPagado = 0;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_factura_ventas);

        nombreCliente = findViewById(R.id.nombreCliente);
        totalVenta = findViewById(R.id.txtTotalVenta);
        totalPorCobrar = findViewById(R.id.txtTotalPorCobrar);
        fecha = findViewById(R.id.fecha);
        lista_de_productos_venta = findViewById(R.id.lista_de_productos_venta);
        lista_de_productos_venta_inventario = findViewById(R.id.lista_de_productos_venta_inventario);
        txtMetodoDePago = findViewById(R.id.txtMetodo);
        txtEstadoDePago = findViewById(R.id.txtEstadoDePago);
        fondoPrincipal = findViewById(R.id.colorPrincipal);
        faq_abonar = findViewById(R.id.faq_abonar);
        metodo_de_pago = findViewById(R.id.metodo_de_pago);
        totalPorCobrarPagado = findViewById(R.id.totalPorCobrarPagado);
        totalDeVentas = findViewById(R.id.totalDeVentas);
        progressIndicator = findViewById(R.id.indicador);


        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");
        String key2 = bundle.getString("keyIV");

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase1 = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("facturasCreadas").child(key);

        databaseReference.child("cliente").addValueEventListener(new ValueEventListener() {
            String clienteValor = null;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object cNombre = snapshot.getValue();
                    clienteValor = String.valueOf(cNombre);
                    if (clienteValor.isEmpty()){
                        nombreCliente.setText("Sin Especificar");
                    }else {
                        nombreCliente.setText(clienteValor);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("abonado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                Object total = snapshot.getValue();
                String totalVentaValor = String.valueOf(total);
                int totales = Integer.parseInt(totalVentaValor);
                totalVenta.setText("$ " + nformat.format(totales));
                totalDeVentas.setText("$ " + nformat.format(totales));
                totalVentaGlobal = totales;
                }else {
                    Toast.makeText(DetallesFacturaVentas.this, "No hay datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("metodoDePago").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object mValor = snapshot.getValue();
                if (mValor.toString().equals("")){
                    txtMetodoDePago.setText("Sin elegir");
                }else {
                String metodoDePagoValor = String.valueOf(mValor);
                txtMetodoDePago.setText(metodoDePagoValor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("estadoDePago").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object ePAgo = snapshot.getValue();
                boolean bandera = Boolean.parseBoolean(String.valueOf(ePAgo));
                if (bandera){
                    txtEstadoDePago.setText("Pagado");
                }else {
                    txtEstadoDePago.setText("Por Cobrar");
                    fondoPrincipal.setBackgroundColor(getResources().getColor(R.color.rojo_fondos));
                    nombreCliente.setTextColor(getResources().getColor(R.color.rojo_naranja));
                    metodo_de_pago.setCardBackgroundColor(getResources().getColor(R.color.rojo_fondos));
                    txtEstadoDePago.setTextColor(getResources().getColor(R.color.rojo_naranja));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("abonar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                Object ePAgo = snapshot.getValue();
                int abonado = Integer.parseInt(String.valueOf(ePAgo));
                totalPorCobrar.setText("$ " + nformat.format(abonado));
                    totalPorCobrarPagado.setText("$ " + nformat.format(totalVentaGlobal-abonado));
                    totalAbonadoGlobal = abonado;
                    totalPagado = totalVentaGlobal-abonado;
                    //totalVentaGlobal = abonado;


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        faq_abonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat nformat = new DecimalFormat("##,###,###.##");
                DialogPlus dialog = DialogPlus.newDialog(DetallesFacturaVentas.this)
                        .setContentHolder(new ViewHolder(R.layout.dialog_abonar_cantidad))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1000)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();

                TextView txt_total_venta = views.findViewById(R.id.txtTotalVenta);
                TextView txt_total_faltante = views.findViewById(R.id.txtFaltante);
                TextInputLayout cantidad_abonar = views.findViewById(R.id.cantidad);
                TextInputEditText cantidad_abonar_txt_listener = views.findViewById(R.id.txtcantidad);
                MaterialButton guardar_abono = views.findViewById(R.id.btn_guardar_abono);
                TextView btn_cancelar = views.findViewById(R.id.btn_cancelar);


                txt_total_venta.setText("$ " + nformat.format(totalVentaGlobal));
                txt_total_faltante.setText("$ " + nformat.format(totalAbonadoGlobal));

                cantidad_abonar_txt_listener.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        int cantidadIngresadaTotal = 0;
                        if (cantidad_abonar.getEditText().getText().toString().isEmpty()){
                            cantidadIngresadaTotal = 0;
                        }else {
                            cantidadIngresadaTotal = Integer.parseInt(cantidad_abonar.getEditText().getText().toString());
                        }

                        if (cantidad_abonar.getEditText().getText().toString().isEmpty()){
                            cantidad_abonar.setHint("Ingrese un valor");
                            txt_total_faltante.setText("$ " + nformat.format(totalAbonadoGlobal));
                        }else if (cantidadIngresadaTotal>totalAbonadoGlobal){
                            if (cantidad_abonar.getEditText().getText().toString().isEmpty()){
                                cantidad_abonar.setHint("Ingrese un valor");
                                txt_total_faltante.setText("$ " + nformat.format(totalAbonadoGlobal));
                            }else{
                            cantidad_abonar.setHelperText("No puedes abonar más de la venta total.");
                            cantidad_abonar.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.rojo)));
                            cantidad_abonar_txt_listener.setHint("");
                            guardar_abono.setClickable(false);
                            }

                        }else {
                            int cantidaIngresada = Integer.parseInt(cantidad_abonar.getEditText().getText().toString().trim());
                            cantidaActualizada = totalAbonadoGlobal - cantidaIngresada;
                            guardar_abono.setClickable(true);
                            txt_total_faltante.setText("$ " + nformat.format(cantidaActualizada));
                            cantidad_abonar.setHelperText("Excelente!");
                            cantidad_abonar.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                guardar_abono.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!cantidad_abonar.getEditText().getText().toString().isEmpty()){
                            int cantidadIngresaGuardar = Integer.parseInt(cantidad_abonar.getEditText().getText().toString());
                            saberSiEsCero = totalAbonadoGlobal - cantidadIngresaGuardar;

                            Map<String, Object> map = new HashMap<>();
                            map.put("abonar",totalAbonadoGlobal - cantidadIngresaGuardar);
                            map.put("totalCalculado",totalAbonadoGlobal - cantidadIngresaGuardar);
                            if (saberSiEsCero==0){
                                map.put("estadoDePago",true);
                                map.put("totalCalculado",totalVentaGlobal);
                                fondoPrincipal.setBackgroundColor(getResources().getColor(R.color.verde_fondos));
                                nombreCliente.setTextColor(getResources().getColor(R.color.verde_complemento));
                                metodo_de_pago.setCardBackgroundColor(getResources().getColor(R.color.verde_fondos));
                                txtEstadoDePago.setTextColor(getResources().getColor(R.color.verde_complemento));
                            }

                            firebaseDatabase1.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("facturas").child("facturasCreadas").child(key).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetallesFacturaVentas.this, "Error al abonar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        dialog.dismiss();
                    }
                });

                btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



                dialog.show();
            }
        });




        //FIREBASE
        lista_de_productos_venta.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVenta> options =
                new FirebaseRecyclerOptions.Builder<ModeloVenta>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key), ModeloVenta.class)
                        .build();
        adaptadorListaProductos=new AdaptadorListaProductos(options);
        lista_de_productos_venta.setAdapter(adaptadorListaProductos);

        //FIREBASE
        lista_de_productos_venta_inventario.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVentaInventario> options2 =
                new FirebaseRecyclerOptions.Builder<ModeloVentaInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturasIV").child(key2), ModeloVentaInventario.class)
                        .build();
        adaptadorListaProductosEnInventario=new AdaptadorListaProductosEnInventario(options2);
        lista_de_productos_venta_inventario.setAdapter(adaptadorListaProductosEnInventario);

    }


    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductos.stopListening();
        adaptadorListaProductosEnInventario.stopListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        adaptadorListaProductos.startListening();
        adaptadorListaProductosEnInventario.startListening();
        adaptadorListaProductos.notifyDataSetChanged();
    }

}