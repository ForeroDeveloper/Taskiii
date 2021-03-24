package com.fordev.taski;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.adaptadores.AdaptadorListaProductosEnInventario;
import com.fordev.taski.adaptadores.AdaptadorListaProductosGastos;
import com.fordev.taski.modelos.ModeloGastos;
import com.fordev.taski.modelos.ModeloVenta;
import com.fordev.taski.modelos.ModeloVentaInventario;
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
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DetallesFacturaGastos extends AppCompatActivity {

    TextView nombreCliente, totalVenta, totalPorCobrar,
            fecha, txtMetodoDePago, txtEstadoDePago, notaIngresada, totalPorCobrarPagado, totalDeVentas;
    RecyclerView listaDeProductos, lista_de_productos_venta_inventario;
    DatabaseReference databaseReference, databaseReference1;
    FirebaseDatabase firebaseDatabase, firebaseDatabase1;
    AdaptadorListaProductosGastos adaptadorListaProductosGastos;
    AdaptadorListaProductosEnInventario adaptadorListaProductosEnInventario;
    RelativeLayout fondoPrincipal,eliminar_factura;
    MaterialButton faq_abonar, faq_enviar_factura;
    CardView metodo_de_pago;
    LinearProgressIndicator progressIndicator2;
    ImageView icon_detalles_notas;
    LinearLayout visibilidad_nota_interna;
    int totalVentaGlobal = 0;
    int totalAbonadoGlobal = 0;
    int cantidaActualizada = 0;
    int saberSiEsCero = 0;
    int totalPagado = 0;
    int abonar = 0;
    int abonado = 0;
    String key = null;
    String key2 = null;
    boolean click = false;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles_factura_gastos);

        nombreCliente = findViewById(R.id.nombreCliente);
        totalVenta = findViewById(R.id.txtTotalVenta);
        totalPorCobrar = findViewById(R.id.txtTotalPorCobrar);
        fecha = findViewById(R.id.fecha);
        listaDeProductos = findViewById(R.id.lista_de_productos_venta);
        lista_de_productos_venta_inventario = findViewById(R.id.lista_de_productos_venta_inventario);
        txtMetodoDePago = findViewById(R.id.txtMetodo);
        txtEstadoDePago = findViewById(R.id.txtEstadoDePago);
        fondoPrincipal = findViewById(R.id.colorPrincipal);
        faq_abonar = findViewById(R.id.faq_abonar);
        metodo_de_pago = findViewById(R.id.metodo_de_pago);
        totalPorCobrarPagado = findViewById(R.id.totalPorCobrarPagado);
        totalDeVentas = findViewById(R.id.totalDeVentas);
        progressIndicator2 = findViewById(R.id.indicador2);
        icon_detalles_notas = findViewById(R.id.icon_detalles_notas);
        visibilidad_nota_interna = findViewById(R.id.visibilidad_nota_interna);
        notaIngresada = findViewById(R.id.notaIngresada);
        eliminar_factura = findViewById(R.id.eliminar_factura);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");
        key2 = bundle.getString("keyIV");

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase1 = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("facturasCreadasEnGastos").child(key);
        databaseReference.keepSynced(true);

        eliminar_factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialog = DialogPlus.newDialog(DetallesFacturaGastos.this)
                        .setContentHolder(new ViewHolder(R.layout.dialog_confirm_delete))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setGravity(Gravity.CENTER)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();
                MaterialButton btn_delete = views.findViewById(R.id.btn_delete);
                TextView cancel = views.findViewById(R.id.btn_cancel);


                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("gastos").child("listaDeProductosEnGastos").child(key).removeValue();
                        finish();
                        dialog.dismiss();
                        FancyToast.makeText(DetallesFacturaGastos.this,"Eliminada Correctamente!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Object clienteObject = snapshot.child("cliente").getValue();
                String clienteValor = String.valueOf(clienteObject);

                if (clienteValor.isEmpty()) {
                    nombreCliente.setText("Sin Especificar");
                } else {
                    nombreCliente.setText(clienteValor);
                }

                //abono
                try {
                    Object abonarObject = snapshot.child("abonado").getValue();
                    abonar = Integer.parseInt(String.valueOf(abonarObject));
                }catch (NumberFormatException ndf){
                    System.out.println("Error NumberFormatException value: " + abonar);
                }
                totalVenta.setText(String.valueOf(nformat.format(abonar)));
                totalDeVentas.setText(String.valueOf(nformat.format(abonar)));
                totalVentaGlobal = abonar;


                //cilco abonar
                try {
                    Object ePAgo = snapshot.child("abonar").getValue();
                    abonado = Integer.parseInt(String.valueOf(ePAgo));
                }catch (NumberFormatException ndf){
                    System.out.println("Error NumberFormatException value: " + abonar);
                }
                totalPorCobrar.setText("$ " + nformat.format(abonado));
                totalAbonadoGlobal = abonado;
                totalPorCobrarPagado.setText(String.valueOf(nformat.format(abonar - abonado)));

                totalAbonadoGlobal = abonado;
                totalPagado = abonar - abonado;

                if (abonar != 0) {
                    int p = totalPagado * 100 / abonar;
                    progressIndicator2.setProgress(p);
                } else {
                    progressIndicator2.setProgress(100);
                }

                //metodo de Pago
                Object metPagoObject = snapshot.child("metodoDePago").getValue();
                String metodoDePago = String.valueOf(metPagoObject);

                if (metodoDePago.toString().equals("")) {
                    txtMetodoDePago.setText("Sin elegir");
                } else {
                    txtMetodoDePago.setText(metodoDePago);
                }
                //Estado de Pago
                Object estadoDePagoObject = snapshot.child("estadoDePago").getValue();
                boolean estadoDePago = Boolean.parseBoolean(String.valueOf(estadoDePagoObject));
                if (estadoDePago) {
                    txtEstadoDePago.setText("Pagado");
                    faq_abonar.setVisibility(View.INVISIBLE);
                } else {
                    txtEstadoDePago.setText("Por Pagar");
                    fondoPrincipal.setBackgroundColor(getResources().getColor(R.color.rojo_fondos));
                    nombreCliente.setTextColor(getResources().getColor(R.color.rojo_naranja));
                    metodo_de_pago.setCardBackgroundColor(getResources().getColor(R.color.rojo_fondos));
                    txtEstadoDePago.setTextColor(getResources().getColor(R.color.rojo_naranja));
                    faq_abonar.setVisibility(View.VISIBLE);
                }

                //Notas Internas
                Object notInternasObject = snapshot.child("notasInternas").getValue();
                String notInternas = String.valueOf(notInternasObject);

                if (notInternas.toString().equals("")) {
                    notaIngresada.setText("No hay notas en esta venta :(");
                } else {
                    notaIngresada.setText(notInternas);
                }

                //Fecha factura
                Object fechaFacturaObject = snapshot.child("fechaRegistro").getValue();
                String fechaRegistro = String.valueOf(fechaFacturaObject);

                if (fechaRegistro.toString().equals("")) {
                    fecha.setText("No hay fecha.");
                } else {
                    fecha.setText(fechaRegistro);
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
                DialogPlus dialog = DialogPlus.newDialog(DetallesFacturaGastos.this)
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
                        if (cantidad_abonar.getEditText().getText().toString().isEmpty()) {
                            cantidadIngresadaTotal = 0;
                        } else {
                            cantidadIngresadaTotal = Integer.parseInt(cantidad_abonar.getEditText().getText().toString());
                        }

                        if (cantidad_abonar.getEditText().getText().toString().isEmpty()) {
                            cantidad_abonar.setHint("Ingrese un valor");
                            txt_total_faltante.setText("$ " + nformat.format(totalAbonadoGlobal));
                        } else if (cantidadIngresadaTotal > totalAbonadoGlobal) {
                            if (cantidad_abonar.getEditText().getText().toString().isEmpty()) {
                                cantidad_abonar.setHint("Ingrese un valor");
                                txt_total_faltante.setText("$ " + nformat.format(totalAbonadoGlobal));
                            } else {
                                cantidad_abonar_txt_listener.setText(String.valueOf(totalAbonadoGlobal));
                            }

                        } else {
                            int cantidaIngresada = Integer.parseInt(cantidad_abonar.getEditText().getText().toString().trim());
                            cantidaActualizada = totalAbonadoGlobal - cantidaIngresada;
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
                        if (!cantidad_abonar.getEditText().getText().toString().isEmpty()) {
                            int cantidadIngresaGuardar = Integer.parseInt(cantidad_abonar.getEditText().getText().toString());
                            saberSiEsCero = totalAbonadoGlobal - cantidadIngresaGuardar;

                            Map<String, Object> map = new HashMap<>();
                            map.put("abonar", totalAbonadoGlobal - cantidadIngresaGuardar);
                            map.put("totalCalculado", totalAbonadoGlobal - cantidadIngresaGuardar);
                            if (saberSiEsCero == 0) {
                                map.put("estadoDePago", true);
                                map.put("totalCalculado", totalVentaGlobal);
                                fondoPrincipal.setBackgroundColor(getResources().getColor(R.color.azul_fondos));
                                nombreCliente.setTextColor(getResources().getColor(R.color.azul));
                                metodo_de_pago.setCardBackgroundColor(getResources().getColor(R.color.verde_fondos));
                                txtEstadoDePago.setTextColor(getResources().getColor(R.color.verde_complemento));
                            }

                            firebaseDatabase1.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("facturas").child("facturasCreadasEnGastos").child(key).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetallesFacturaGastos.this, "Error al abonar", Toast.LENGTH_SHORT).show();
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

        icon_detalles_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visibilidad_nota_interna.getVisibility() == View.VISIBLE) {
                    visibilidad_nota_interna.setVisibility(View.GONE);
                } else {
                    visibilidad_nota_interna.setVisibility(View.VISIBLE);
                }
            }
        });

/*        faq_enviar_factura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallesFacturaGastos.this, CreacionDeRecibo.class);
                intent.putExtra("key", key);
                intent.putExtra("key2", key2);
                startActivity(intent);
            }
        });*/


        //FIREBASE

        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloGastos> options =
                new FirebaseRecyclerOptions.Builder<ModeloGastos>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("gastos").child("listaDeProductosEnGastos").child(key), ModeloGastos.class)
                        .build();
        adaptadorListaProductosGastos = new AdaptadorListaProductosGastos(options);
        listaDeProductos.setAdapter(adaptadorListaProductosGastos);


/*        //FIREBASE
        lista_de_productos_venta_inventario.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVentaInventario> options2 =
                new FirebaseRecyclerOptions.Builder<ModeloVentaInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturasIV").child(key2), ModeloVentaInventario.class)
                        .build();
        adaptadorListaProductosEnInventario=new AdaptadorListaProductosEnInventario(options2);
        lista_de_productos_venta_inventario.setAdapter(adaptadorListaProductosEnInventario);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductosGastos.stopListening();
//        /*adaptadorListaProductosEnInventario.stopListening();*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        adaptadorListaProductosGastos.startListening();
/*
        adaptadorListaProductosEnInventario.startListening();
*/
        adaptadorListaProductosGastos.notifyDataSetChanged();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}