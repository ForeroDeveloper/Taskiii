package com.fordev.taski;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.adaptadores.AdaptadorListaProductosEnInventario;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import ir.esfandune.calculatorlibe.CalculatorDialog;

public class VentaRapidaNegocio extends AppCompatActivity {

    TextInputLayout edtVentaTotal, valorTotalVenta, cantidadProducto, precioFinalPorUsuario;
    TextInputEditText txtValorTotalVenta, txtcantidadProducto, txtprecioFinalPorUsuario;
    RecyclerView listaDeProductos, lista_de_productos_venta_inventario;
    DatabaseReference databaseReference, databaseReferenceClientes;
    FirebaseDatabase firebaseDatabase;
    AdaptadorListaProductos adaptadorListaProductos;
    AdaptadorListaProductosEnInventario adaptadorListaProductosEnInventario;
    LinearLayout cabeceraFacturas, imgIlustra;
    MaterialButton btnLimpiar, btnGuardarProducto, btnInventario;
    TextView txtCrearVenta, btnGuardarFactura, valorTotalTxt;
    ImageView atras;
    private int dia, mes, ano;
    long maxid = 0;
    int precioFinal;
    boolean premium = true;
    int total_factura = 0;
    int totalDeFactura = 0;
    int totalDeFacturaInventario = 0;
    boolean estadoDePago = true;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    DecimalFormat format = new DecimalFormat("0.#");
    String key;
    String key2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venta_rapida_negocio);
        NumberFormat nformat = new DecimalFormat("##,###,###.##");

        btnGuardarProducto = findViewById(R.id.btnGuardar);
        edtVentaTotal = findViewById(R.id.valorTotalVenta);
        txtCrearVenta = findViewById(R.id.crear_venta);
        valorTotalVenta = findViewById(R.id.valorTotalVenta);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);
        cantidadProducto = findViewById(R.id.cantidad);
        precioFinalPorUsuario = findViewById(R.id.precioFinal);
        listaDeProductos = findViewById(R.id.lista_de_productos_venta);
        lista_de_productos_venta_inventario = findViewById(R.id.lista_de_productos_venta_inventario);
        atras = findViewById(R.id.atras);
        valorTotalTxt = findViewById(R.id.valorTotalTxt);
        RadioButton radioButton = findViewById(R.id.radio0);

        //Edit Text Material Text
        txtValorTotalVenta = findViewById(R.id.txtprecioUnitario);
        txtcantidadProducto = findViewById(R.id.txtcantidad);
        txtprecioFinalPorUsuario = findViewById(R.id.txtprecioFinal);
        btnLimpiar = (MaterialButton) findViewById(R.id.btnLimpiar);
        btnInventario = (MaterialButton) findViewById(R.id.btnInventario);
        cabeceraFacturas = (LinearLayout) findViewById(R.id.cabecera_factura);
        imgIlustra = (LinearLayout) findViewById(R.id.crear_venta_ilustra);

        edtVentaTotal.requestFocus();

        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        String fecha = getFechaNormal(getFechaMilisegundos());
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.keepSynced(true);

        databaseReference.child("info").child("Premium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object booleanPremium = snapshot.getValue();
                    premium = Boolean.parseBoolean(String.valueOf(booleanPremium));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("facturas").child("facturasCreadas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    total_factura = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //keys
        key = databaseReference.push().getKey();
        key2 = databaseReference.push().getKey();

        //Obtener Cuantos productos agrego al recycler
        databaseReference.child("facturas").child("fechas").child("listaDeFacturas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.keepSynced(true);
                if (snapshot.exists()) {
                    maxid = (snapshot.getChildrenCount());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtValorTotalVenta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (valorTotalVenta.getEditText().getText().toString().equals("")) {
                        valorTotalVenta.setHint("Valor Total (obligatorio)");
                        valorTotalTxt.setText("$ " + String.valueOf(0));
                    } else {
                        int ventaTotal = Integer.parseInt(valorTotalVenta.getEditText().getText().toString());
                        precioFinal = ventaTotal;
                        valorTotalTxt.setText("$ " + String.valueOf(nformat.format(ventaTotal)));
                    }
                } catch (NumberFormatException formatException) {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //CLick Listener and others
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    estadoDePago = false;

                } else {
                    estadoDePago = true;
                }
            }
        });

        btnGuardarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!premium) {
                    if (total_factura < 20) {
                        ventaRapida();
                    } else {
                        DialogPlus dialog = DialogPlus.newDialog(VentaRapidaNegocio.this)
                                .setContentHolder(new ViewHolder(R.layout.dialog_gold))
                                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                .setExpanded(true, 1600)
                                .setGravity(Gravity.BOTTOM)
                                .setContentBackgroundResource(android.R.color.transparent)
                                .create();

                        View views = dialog.getHolderView();

                        RelativeLayout btnAcutualizar = views.findViewById(R.id.actualizar);


                        btnAcutualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(VentaRapidaNegocio.this, PlanesMenuPrincipal.class));
                            }
                        });

                        dialog.show();
                    }

                } else {
                    ventaRapida();
                }


            }
        });


        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void ventaRapida() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final Calendar Cal = Calendar.getInstance();
        dia = Cal.get(Calendar.DAY_OF_MONTH);
        mes = Cal.get(Calendar.MONTH);
        ano = Cal.get(Calendar.YEAR);

        DialogPlus dialogPlus = DialogPlus.newDialog(btnGuardarFactura.getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_confirm_factura_rapida))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setExpanded(true, 1300)
                .setGravity(Gravity.BOTTOM)
                .setContentBackgroundResource(android.R.color.transparent)
                .create();
        View view = dialogPlus.getHolderView();
        //FindViewsIdes del Dialog Plus
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.metodoDePago);
        TextView txtTotalFactura = view.findViewById(R.id.txtTotalFactura);
        TextView txtCancelarFac = view.findViewById(R.id.txtCancel);
        TextView txtFechaSelect = view.findViewById(R.id.txtFechaSelect);
        MaterialButton btnGuardarFac = view.findViewById(R.id.btnGuardarTodo);
        TextInputEditText conceptoVenta = view.findViewById(R.id.txtConceptoVenta);
        AutoCompleteTextView clienteNombre = view.findViewById(R.id.clientes);
        AutoCompleteTextView metodoDePago = view.findViewById(R.id.metodoDePago);
        TextInputEditText notasInternas = view.findViewById(R.id.txtNotasInternas);
        TextInputLayout cliente = view.findViewById(R.id.edtCliente);
        ImageView selecFechaFactura = view.findViewById(R.id.ic_seleccionar_fecha);
        //setFechaActual
        txtFechaSelect.setText(sdf.format(Cal.getTime()));

        databaseReference.child("Clientes").child("cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //array lista clientes
                ArrayList clientes = new ArrayList<String>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    HashMap map = (HashMap) childSnapshot.getValue();
                    if (map != null) {
                        clientes.add(map.get("nombreCliente"));
                    }
                }

                ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.lista_items_dos,
                        clientes);
                clienteNombre.setAdapter(adapterClientes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Variables and others
        String[] tipoDocumento = new String[]{
                "Efectivo",
                "Tarjeta",
                "Tranferencia bancaria",
                "Otro"
        };

        txtTotalFactura.setText(String.valueOf("$ " + nformat.format(precioFinal)));

        selecFechaFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VentaRapidaNegocio.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Cal.set(year, month, dayOfMonth);
                        txtFechaSelect.setText(sdf.format(Cal.getTime()));
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });
        txtCancelarFac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlus.dismiss();
            }
        });
        btnGuardarFac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtener Datos
                String datos_cliente = clienteNombre.getText().toString();
                String datos_concepto_venta = conceptoVenta.getText().toString();
                String datos_notas_internas = notasInternas.getText().toString();
                String datos_metodo_de_pago = metodoDePago.getText().toString();
                if (datos_concepto_venta.equals("")) {
                    datos_concepto_venta = "Venta " + (int) (Math.random() * (3000 - 1000));
                }

                ModeloFacturaCreada modeloFacturaCreada = new ModeloFacturaCreada();
                modeloFacturaCreada.setId(key);
                modeloFacturaCreada.setIdInventario(key2);
                modeloFacturaCreada.setCliente(datos_cliente);
                modeloFacturaCreada.setConceptoDeVenta(datos_concepto_venta);
                modeloFacturaCreada.setNotasInternas(datos_notas_internas);
                modeloFacturaCreada.setEstadoDePago(estadoDePago);
                modeloFacturaCreada.setTotalCalculado(precioFinal);
                modeloFacturaCreada.setMetodoDePago(datos_metodo_de_pago);
                modeloFacturaCreada.setFechaRegistro(sdf.format(Cal.getTime()));
                modeloFacturaCreada.setTimeStamp(getFechaMilisegundos() * -1);
                modeloFacturaCreada.setYear(String.valueOf(Cal.get(Calendar.YEAR)));
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
                modeloFacturaCreada.setMonth(String.valueOf(sdf2.format(Cal.getTime())));
                modeloFacturaCreada.setDay(String.valueOf(Cal.get(Calendar.DAY_OF_MONTH)));
                modeloFacturaCreada.setAbonado(modeloFacturaCreada.getTotalCalculado());
                if (!estadoDePago) {
                    modeloFacturaCreada.setAbonar(modeloFacturaCreada.getTotalCalculado());
                } else {
                    modeloFacturaCreada.setAbonar(0);
                }

                databaseReference.child("facturas").child("facturasCreadas").child(key).setValue(modeloFacturaCreada);
                databaseReference.child("facturas").child("facturasCreadas").child(key).child("ventaRapida").setValue("si");
                databaseReference.keepSynced(true);

                //BORRAR DATOS FACTURA ACTUAL
                borrarDatosDeFacturaActual();
                finish();

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.lista_items_dos,
                tipoDocumento);
        autoCompleteTextView.setAdapter(adapter);


        dialogPlus.show();
    }

    private void eliminarFacturaActual() {
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key).removeValue();
    }


    private String getFechaNormal(Long fechaMilisegundos) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechaMilisegundos);
        return fecha;
    }

    private Long getFechaMilisegundos() {
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();
        return tiempounix;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eliminarFacturaActual();
    }

    private void borrarDatosDeFacturaActual() {
        databaseReference.child("FacturaActualKey").removeValue();
        databaseReference.keepSynced(true);
    }




    public void showCalculadora(View view) {
        new CalculatorDialog(this) {
            @Override
            public void onResult(String result) {
                Toasty.info(VentaRapidaNegocio.this, "Resultado: " + result, Toast.LENGTH_LONG, true).show();
            }
        }.showDIalog();
    }
}