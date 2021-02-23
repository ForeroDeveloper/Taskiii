package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.fordev.taski.modelos.ModeloVenta;
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
import java.util.Calendar;
import java.util.TimeZone;

public class VentasNegocio extends AppCompatActivity {

    TextInputLayout edtProducto,precioUnitario,cantidadProducto,precioFinalPorUsuario;
    TextInputEditText txtprecioUnitario,txtcantidadProducto,txtprecioFinalPorUsuario;
    RecyclerView listaDeProductos;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    AdaptadorListaProductos adaptadorListaProductos;
    LinearLayout cabeceraFacturas,imgIlustra;
    MaterialButton btnLimpiar,btnGuardarProducto,btnAcciones;
    TextView txtCrearVenta,btnGuardarFactura;
    private int dia,mes,ano;
    long maxid = 0;
    int precioFinal;
    int sum = 0;
    int totales;
    public String key;
    boolean estadoDePago = true;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    int activarFinalizarVenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventas_negocio);
        NumberFormat nformat = new DecimalFormat("##,###,###.##");

        btnGuardarProducto = findViewById(R.id.btnGuardar);
        edtProducto = findViewById(R.id.nombreProducto);
        txtCrearVenta = findViewById(R.id.crear_venta);
        precioUnitario = findViewById(R.id.precio);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);
        cantidadProducto = findViewById(R.id.cantidad);
        precioFinalPorUsuario = findViewById(R.id.precioFinal);
        listaDeProductos = findViewById(R.id.lista_de_productos_venta);
        //Edit Text Material Text
        txtprecioUnitario = findViewById(R.id.txtprecio);
        txtcantidadProducto = findViewById(R.id.txtcantidad);
        txtprecioFinalPorUsuario = findViewById(R.id.txtprecioFinal);
        btnLimpiar=(MaterialButton) findViewById(R.id.btnLimpiar);
        cabeceraFacturas =(LinearLayout) findViewById(R.id.cabecera_factura);
        imgIlustra =(LinearLayout) findViewById(R.id.crear_venta_ilustra);

        btnLimpiar.setVisibility(View.GONE);

        activarBotonFinalizar(0);

        Bundle bundle =getIntent().getExtras();
        String key = bundle.getString("key");



        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        String fecha = getFechaNormal(getFechaMilisegundos());
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("fechas").child("listaDeFacturas").child(key);
        //key
       // key = databaseReference.push().getKey();
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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //remover ultima factura
        //databaseReference.child("facturas").child("items").removeValue();

        btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    ModeloVenta modeloVenta = new ModeloVenta();
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

                    activarBotonFinalizar(1);

                    databaseReference.child(key).child(id).setValue(modeloVenta);
                    cabeceraFacturas.setVisibility(View.VISIBLE);
                    btnLimpiar.setVisibility(View.VISIBLE);
                    imgIlustra.setVisibility(View.GONE);
                    btnGuardarFactura.setVisibility(View.VISIBLE);


                }

            }
        });




        btnGuardarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                final Calendar Cal = Calendar.getInstance();
                dia=Cal.get(Calendar.DAY_OF_MONTH);
                mes=Cal.get(Calendar.MONTH);
                ano=Cal.get(Calendar.YEAR);

                DialogPlus dialogPlus = DialogPlus.newDialog(btnGuardarFactura.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_confirm_factura))
                        .setExpanded(true,1460)
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
                            estadoDePago = true;
                            cliente.setHelperTextEnabled(false);
                        }
                    }
                });

                selecFechaFactura.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(VentasNegocio.this,new DatePickerDialog.OnDateSetListener() {
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
                            datos_concepto_venta = "Venta " + (int) (Math.random() * (3000 - 1000));
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

                        modeloFacturaCreada.setDay(String.valueOf(Cal.get(Calendar.DAY_OF_MONTH)));
                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("facturas").child("facturasCreadas").child(String.valueOf(modeloFacturaCreada.getTimeStamp())).setValue(modeloFacturaCreada);

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
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key).removeValue();
                sum=0;
                txtCrearVenta.setText("$ 0");
                cabeceraFacturas.setVisibility(View.INVISIBLE);
                btnLimpiar.setVisibility(View.INVISIBLE);
                imgIlustra.setVisibility(View.VISIBLE);
                activarBotonFinalizar(0);
            }
        });



        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVenta> options =
                new FirebaseRecyclerOptions.Builder<ModeloVenta>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key), ModeloVenta.class)
                        .build();
        adaptadorListaProductos=new AdaptadorListaProductos(options);
        listaDeProductos.setAdapter(adaptadorListaProductos);


    }

    private void activarBotonFinalizar(int i) {
        if (activarFinalizarVenta==i){
            btnGuardarFactura.setVisibility(View.INVISIBLE);
        }else {
            btnGuardarFactura.setVisibility(View.VISIBLE);
        }
    }

    private String getFechaNormal(Long fechaMilisegundos) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechaMilisegundos);
        return fecha;
    }

    private Long getFechaMilisegundos() {
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();
        return  tiempounix;
    }

    @Override
    protected void onStart() {
        super.onStart();
        activarBotonFinalizar(0);
        adaptadorListaProductos.startListening();
        adaptadorListaProductos.notifyDataSetChanged();

    }



    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductos.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void inventarioActivity(View view) {
        Intent intent = new Intent(VentasNegocio.this, Inventario.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }
}