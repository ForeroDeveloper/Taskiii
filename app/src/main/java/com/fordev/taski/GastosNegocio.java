package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaProductosGastos;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.fordev.taski.modelos.ModeloGastos;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import ir.esfandune.calculatorlibe.CalculatorDialog;

public class GastosNegocio extends AppCompatActivity {
    TextInputLayout edtProducto,precioUnitario,cantidadProducto,precioFinalPorUsuario;
    TextInputEditText txtprecioUnitario,txtcantidadProducto,txtprecioFinalPorUsuario;
    RecyclerView listaDeProductos;
    DatabaseReference databaseReference, databaseReferenceProv;
    FirebaseDatabase firebaseDatabase;
    AdaptadorListaProductosGastos adaptadorListaProductosGastos;
    LinearLayout cabeceraFacturas,btnAcciones,imgIlustra;
    MaterialButton btnLimpiar,btnGuardarProducto,btnProveedores;
    TextView txtCrearVenta,btnGuardarFactura;
    ImageView atras;
    private int dia,mes,ano;
    long maxid = 0;
    int precioFinal;
    int sum = 0;
    int totales;
    boolean estadoDePago = true;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    String key;
    String key2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gastos_negocio);

        btnGuardarProducto = findViewById(R.id.btnGuardar);
        edtProducto = findViewById(R.id.nombreClientes);
        txtCrearVenta = findViewById(R.id.crear_venta);
        precioUnitario = findViewById(R.id.valorTotalVenta);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);
        cantidadProducto = findViewById(R.id.cantidad);
        precioFinalPorUsuario = findViewById(R.id.precioFinal);
        listaDeProductos = findViewById(R.id.lista_de_productos_venta);
        btnProveedores = findViewById(R.id.btnInventario);
        //Edit Text Material Text
        txtprecioUnitario = findViewById(R.id.txtprecioUnitario);
        txtcantidadProducto = findViewById(R.id.txtcantidad);
        txtprecioFinalPorUsuario = findViewById(R.id.txtprecioFinal);
        btnLimpiar=(MaterialButton) findViewById(R.id.btnLimpiar);
        cabeceraFacturas =(LinearLayout) findViewById(R.id.cabecera_factura);
        imgIlustra =(LinearLayout) findViewById(R.id.crear_venta_ilustra);
        atras = findViewById(R.id.atras);

        btnLimpiar.setVisibility(View.INVISIBLE);
        edtProducto.requestFocus();
        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("gastos").child("listaDeProductosEnGastos");
        databaseReference.keepSynced(true);
        //key

        key = databaseReference.push().getKey();
        key2 = databaseReference.push().getKey();
        //Obtener Cuantos productos agrego al recycler
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    maxid=(snapshot.getChildrenCount());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtprecioUnitario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (precioUnitario.getEditText().getText().toString().equals("")){
                        precioUnitario.setHint("Precio unitario (obligatorio)");
                        txtprecioFinalPorUsuario.setText(String.valueOf(0));
                    }else if (cantidadProducto.getEditText().getText().toString().isEmpty()){
                        String precio = precioUnitario.getEditText().getText().toString();
                        precioFinal = Integer.parseInt(precio);
                        txtprecioFinalPorUsuario.setText(String.valueOf(precioFinal));
                    }else {
                        int cantidad = Integer.parseInt(cantidadProducto.getEditText().getText().toString());
                        int precioUni = Integer.parseInt(precioUnitario.getEditText().getText().toString());
                        int res = precioUni * cantidad;
                        txtprecioFinalPorUsuario.setText(String.valueOf(res));
                    }
                }catch (NumberFormatException formatException){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //ListenerPrecioProducto
        txtcantidadProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    if (cantidadProducto.getEditText().getText().toString().equals("")){
                        cantidadProducto.setHint("Cantidad (obligatorio)");
                        txtprecioFinalPorUsuario.setText(String.valueOf(precioFinal));
                    }else {
                        String cantidad = cantidadProducto.getEditText().getText().toString();
                        int cantidadFloat = Integer.parseInt(cantidad);
                        //operaciones
                        int res = cantidadFloat * precioFinal;
                        if (cantidad.isEmpty() ) {
                            cantidadProducto.setHelperTextEnabled(true);
                            cantidadProducto.setHelperText("Cantidad * Precio Unitario");
                        } else if(cantidad.equals("0")){
                            txtprecioFinalPorUsuario.setText(String.valueOf(precioFinal));
                        }else{
                            txtprecioFinalPorUsuario.setText(String.valueOf(res));
                            precioFinalPorUsuario.setHelperTextEnabled(true);
                            precioFinalPorUsuario.setHelperText("Puede Modificar el Valor");
                        }
                    }
                }catch (NumberFormatException formatException){

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //condiciones para guardar el producto a la lista
                    if (edtProducto.getEditText().getText().toString().isEmpty()){
                        edtProducto.setError("Ingrese un nombre");
                    }else if (precioUnitario.getEditText().getText().toString().isEmpty()){
                        precioUnitario.setErrorEnabled(true);
                        precioUnitario.setError("Ingrese un valor");
                        edtProducto.setErrorEnabled(false);
                    }else if (cantidadProducto.getEditText().getText().toString().isEmpty()){
                        cantidadProducto.setErrorEnabled(true);
                        cantidadProducto.setError("ingrese un valor");
                        precioUnitario.setErrorEnabled(false);
                    }else {

                        //Obtener datos del usuario en los edit Text
                        String nombre_producto = edtProducto.getEditText().getText().toString();
                        int precio_producto = Integer.parseInt(precioUnitario.getEditText().getText().toString());
                        int cantidad_producto = Integer.parseInt(cantidadProducto.getEditText().getText().toString());
                        int precio_final_producto = Integer.parseInt(precioFinalPorUsuario.getEditText().getText().toString());
                        String id = databaseReference.push().getKey();

                        //Variables iniciales para el constructor del ModeloVista REqueridos o faltantes
                        int precioTotaldeTodosLosProductos = 0;
                        int valorTotalCalculadoAutomatico = precio_producto*cantidad_producto;
                        ModeloGastos modeloVenta = new ModeloGastos();
                        modeloVenta.setId(id);
                        modeloVenta.setNombreProdcuto(nombre_producto);
                        modeloVenta.setPrecioProducto(precio_producto);
                        modeloVenta.setCantidadProducto(cantidad_producto);
                        modeloVenta.setPrecioFinalPorElUsuario(precio_final_producto);
                        modeloVenta.setValorTotalCalculadoAutomatico(valorTotalCalculadoAutomatico);
                        modeloVenta.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                        modeloVenta.setTimeStamp(getFechaMilisegundos() * -1);
                        //set precio total de todos los productos
                        if (modeloVenta.getPrecioFinalPorElUsuario()==0){
                            modeloVenta.setPrecioTotaldeTodosLosProductos(valorTotalCalculadoAutomatico);
                            sum+= modeloVenta.getPrecioTotaldeTodosLosProductos();
                            modeloVenta.setPrecioTotaldeTodosLosProductos(sum);
                            txtCrearVenta.setText(String.valueOf("$ " + nformat.format(sum)));
                        }else{
                            modeloVenta.setPrecioTotaldeTodosLosProductos(precio_final_producto);
                            sum+= modeloVenta.getPrecioTotaldeTodosLosProductos();
                            modeloVenta.setPrecioTotaldeTodosLosProductos(sum);
                            txtCrearVenta.setText(String.valueOf("$ " + nformat.format(sum)));
                        }
                        databaseReference.child(key).child(id).setValue(modeloVenta).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                edtProducto.getEditText().setText("");
                                precioUnitario.getEditText().setText("");
                                cantidadProducto.getEditText().setText("");
                                precioFinalPorUsuario.getEditText().setText("");
                            }
                        });
                        cabeceraFacturas.setVisibility(View.VISIBLE);
                        imgIlustra.setVisibility(View.GONE);
                        btnGuardarFactura.setVisibility(View.VISIBLE);

                    }
                }catch (NumberFormatException numberFormatException){
                    Toasty.error(GastosNegocio.this,"Verifica lo signos que usas en los campos!",Toast.LENGTH_LONG, true).show();
                }

            }
        });


        btnGuardarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Calendar Cal = Calendar.getInstance();
                dia=Cal.get(Calendar.DAY_OF_MONTH);
                mes=Cal.get(Calendar.MONTH);
                ano=Cal.get(Calendar.YEAR);

                DialogPlus dialogPlus = DialogPlus.newDialog(btnGuardarFactura.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_confirm_factura_gasto))
                        .setExpanded(true,1470)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();
                View view = dialogPlus.getHolderView();
                //FindViewsIdes del Dialog Plus
                AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.metodoDePago);
                RadioButton radioButton = view.findViewById(R.id.radio0);
                TextView txtTotalFactura = view.findViewById(R.id.txtTotalFactura);
                TextView txtCancelarFac = view.findViewById(R.id.txtCancel);
                TextView txtFechaSelect = view.findViewById(R.id.txtFechaSelect);
                MaterialButton btnGuardarFac = view.findViewById(R.id.btnGuardarTodo);
                TextInputEditText conceptoVenta = view.findViewById(R.id.txtConceptoVenta);
                AutoCompleteTextView clienteNombre = view.findViewById(R.id.clientes);
                AutoCompleteTextView metodoDePago = view.findViewById(R.id.metodoDePago);
                TextInputEditText notasInternas = view.findViewById(R.id.txtNotasInternas);
                TextInputLayout cliente =  view.findViewById(R.id.edtCliente);
                ImageView selecFechaFactura = view.findViewById(R.id.ic_seleccionar_fecha);
                //setFechaActual
                txtFechaSelect.setText(sdf.format(Cal.getTime()));

                databaseReferenceProv = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Proveedores").child("proveedor");

                databaseReferenceProv.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //array lista clientes
                        ArrayList proveedores = new ArrayList<String>();

                        for (DataSnapshot childSnapshot: snapshot.getChildren()){
                            HashMap map =(HashMap) childSnapshot.getValue();
                            if(map!=null) {
                                proveedores.add(map.get("nombreProveedor"));
                            }
                        }

                        ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(getApplicationContext(),
                                R.layout.lista_items_dos,
                                proveedores);
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
                txtTotalFactura.setText(String.valueOf("$ " + nformat.format(sum)));

                //CLick Listener and others
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked){
                            txtTotalFactura.setTextColor(getResources().getColor(R.color.rojo));
                            cliente.setHelperTextEnabled(true);
                            cliente.setHelperText("¡Importante!");
                            cliente.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.rojo)));
                            estadoDePago = false;
                        }else {
                            txtTotalFactura.setTextColor(getResources().getColor(R.color.verde));
                            cliente.setHelperText("Recomendado");
                            cliente.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                            estadoDePago = true;
                        }
                    }
                });

                selecFechaFactura.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(GastosNegocio.this,new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Cal.set(year,month,dayOfMonth);
                                txtFechaSelect.setText(sdf.format(Cal.getTime()));
                            }
                        },ano,mes,dia);
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
                        if (datos_concepto_venta.equals("")){
                            datos_concepto_venta = "Gasto " + (int) (Math.random() * (3000 - 1000));
                        }

                        ModeloFacturaCreada modeloFacturaCreada = new ModeloFacturaCreada();
                        modeloFacturaCreada.setId(key);
                        modeloFacturaCreada.setCliente(datos_cliente);
                        modeloFacturaCreada.setConceptoDeVenta(datos_concepto_venta);
                        modeloFacturaCreada.setNotasInternas(datos_notas_internas);
                        modeloFacturaCreada.setEstadoDePago(estadoDePago);
                        modeloFacturaCreada.setTotalCalculado(sum);
                        modeloFacturaCreada.setMetodoDePago(datos_metodo_de_pago);
                        modeloFacturaCreada.setFechaRegistro(sdf.format(Cal.getTime()));
                        modeloFacturaCreada.setTimeStamp(getFechaMilisegundos() * -1);
                        modeloFacturaCreada.setYear(String.valueOf(Cal.get(Calendar.YEAR)));
                        SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
                        modeloFacturaCreada.setMonth(String.valueOf(sdf2.format(Cal.getTime())));
                        modeloFacturaCreada.setAbonado(modeloFacturaCreada.getTotalCalculado());
                        if (!estadoDePago){
                            modeloFacturaCreada.setAbonar(modeloFacturaCreada.getTotalCalculado());
                        }else {
                            modeloFacturaCreada.setAbonar(0);
                        }

                        modeloFacturaCreada.setDay(String.valueOf(Cal.get(Calendar.DAY_OF_MONTH)));
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("facturas").child("facturasCreadasEnGastos").child(key).setValue(modeloFacturaCreada);
                        finish();

                    }
                });

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.lista_items_dos,
                        tipoDocumento);
                autoCompleteTextView.setAdapter(adapter);

                dialogPlus.show();



            }
        });


        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarDatosFactura();
            }
        });


        btnProveedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Balloon balloon = new Balloon.Builder(GastosNegocio.this)
                        .setArrowSize(10)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                        .setArrowPosition(0.5f)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setHeight(65)
                        .setTextSize(15f)
                        .setMargin(5)
                        .setCornerRadius(6f)
                        .setAlpha(0.9f)
                        .setText(" En Desarrollo... :) ")
                        .setTextColor(getResources().getColor(R.color.white))
                        .setTextIsHtml(true)
                        .setIconDrawable(getResources().getDrawable(R.drawable.logo_taski))
                        .setBackgroundColor(getResources().getColor(R.color.primario))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignBottom(btnProveedores);

            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloGastos> options =
                new FirebaseRecyclerOptions.Builder<ModeloGastos>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("gastos").child("listaDeProductosEnGastos").child(key), ModeloGastos.class)
                        .build();
        adaptadorListaProductosGastos =new AdaptadorListaProductosGastos(options);
        listaDeProductos.setAdapter(adaptadorListaProductosGastos);

    }

    private void eliminarDatosFactura() {
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("gastos").child("listaDeProductosEnGastos").child(key).removeValue();
        sum=0;
        cabeceraFacturas.setVisibility(View.GONE);
        btnLimpiar.setVisibility(View.GONE);
        imgIlustra.setVisibility(View.VISIBLE);
        btnGuardarFactura.setVisibility(View.INVISIBLE);
        txtCrearVenta.setText("$ 0");
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
        return  tiempounix;
    }

    private void hacerSumaDeItems() {
        adaptadorListaProductosGastos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (adaptadorListaProductosGastos.getItemCount()==0){
                    cabeceraFacturas.setVisibility(View.GONE);
                    btnLimpiar.setVisibility(View.GONE);
                    imgIlustra.setVisibility(View.VISIBLE);
                    btnGuardarFactura.setVisibility(View.INVISIBLE);
                }else {
                    cabeceraFacturas.setVisibility(View.VISIBLE);
                    btnLimpiar.setVisibility(View.VISIBLE);
                    imgIlustra.setVisibility(View.GONE);
                    btnGuardarFactura.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void eliminarFacturaActual() {
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("gastos").child("listaDeFacturas").child(key).removeValue();
        txtCrearVenta.setText("$ 0");
        cabeceraFacturas.setVisibility(View.INVISIBLE);
        btnLimpiar.setVisibility(View.INVISIBLE);
        imgIlustra.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        hacerSumaDeItems();
        adaptadorListaProductosGastos.startListening();
        adaptadorListaProductosGastos.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductosGastos.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        eliminarDatosFactura();
    }

    public void showCalculadora(View view) {
        new CalculatorDialog(this) {
            @Override
            public void onResult(String result) {
                Toasty.info(GastosNegocio.this,"Resultado: " + result, Toast.LENGTH_LONG, true).show();
            }
        }.showDIalog();
    }
}