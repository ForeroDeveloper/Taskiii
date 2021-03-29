package com.fordev.taski;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.adaptadores.AdaptadorListaProductosEnInventario;
import com.fordev.taski.modelos.ModeloFacturaCreada;
import com.fordev.taski.modelos.ModeloVenta;
import com.fordev.taski.modelos.ModeloVentaInventario;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import ir.esfandune.calculatorlibe.CalculatorDialog;

public class VentasNegocio extends AppCompatActivity {

    TextInputLayout edtProducto, precioUnitario, cantidadProducto, precioFinalPorUsuario;
    TextInputEditText txtprecioUnitario, txtcantidadProducto, txtprecioFinalPorUsuario;
    RecyclerView listaDeProductos, lista_de_productos_venta_inventario;
    DatabaseReference databaseReference,databaseReferenceClientes;
    FirebaseDatabase firebaseDatabase;
    AdaptadorListaProductos adaptadorListaProductos;
    AdaptadorListaProductosEnInventario adaptadorListaProductosEnInventario;
    LinearLayout cabeceraFacturas, imgIlustra;
    MaterialButton btnLimpiar, btnGuardarProducto, btnInventario;
    TextView txtCrearVenta, btnGuardarFactura;
    ImageView atras;
    private int dia, mes, ano;
    long maxid = 0;
    double precioFinal;
    int totalDeFactura = 0;
    int totalDeFacturaInventario = 0;
    boolean estadoDePago = true;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    DecimalFormat format = new DecimalFormat("0.#");
    String key;
    String key2;
    boolean premium = true;
    int total_factura = 0;

    View view;
    DialogPlus dialogPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventas_negocio);
        NumberFormat nformat = new DecimalFormat("##,###,###.##");

        btnGuardarProducto = findViewById(R.id.btnGuardar);
        edtProducto = findViewById(R.id.nombreClientes);
        txtCrearVenta = findViewById(R.id.crear_venta);
        precioUnitario = findViewById(R.id.valorTotalVenta);
        btnGuardarFactura = findViewById(R.id.btnGuardarFactura);
        cantidadProducto = findViewById(R.id.cantidad);
        precioFinalPorUsuario = findViewById(R.id.precioFinal);
        listaDeProductos = findViewById(R.id.lista_de_productos_venta);
        lista_de_productos_venta_inventario = findViewById(R.id.lista_de_productos_venta_inventario);
        atras = findViewById(R.id.atras);
        //Edit Text Material Text
        txtprecioUnitario = findViewById(R.id.txtprecioUnitario);
        txtcantidadProducto = findViewById(R.id.txtcantidad);
        txtprecioFinalPorUsuario = findViewById(R.id.txtprecioFinal);
        btnLimpiar = (MaterialButton) findViewById(R.id.btnLimpiar);
        btnInventario = (MaterialButton) findViewById(R.id.btnInventario);
        cabeceraFacturas = (LinearLayout) findViewById(R.id.cabecera_factura);
        imgIlustra = (LinearLayout) findViewById(R.id.crear_venta_ilustra);

        btnLimpiar.setVisibility(View.GONE);
//        edtProducto.requestFocus();



        //tap target
        final Spannable spannable2 = new SpannableString("Agrega productos a tu venta directamente desde tu inventario");
        spannable2.setSpan(new UnderlineSpan(), spannable2.length() - "TapTargetView".length(),
                spannable2.length(), 0);

        SharedPreferences preferences = this.getSharedPreferences("TUTORIAL", Context.MODE_PRIVATE);
        boolean inventario = preferences.getBoolean("ClickInventario" , false);

        if (!inventario){
              TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.btnInventario), "Selecciona desde Inventario", spannable2)
                        .cancelable(false)
                        .drawShadow(true)
                        .titleTextDimen(R.dimen.text)
                        .tintTarget(false), new TapTargetView.Listener() {
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        SharedPreferences.Editor editor  = preferences.edit();
                        editor.putBoolean("ClickInventario", true);
                        editor.apply();
                    }
                    public  void  onOuterCircleClick(TapTargetView view){
                        super.onOuterCircleClick(view);
                    }
                }

        );
        }




        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
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

        btnInventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VentasNegocio.this, Inventario.class);
                intent.putExtra("key", key);
                databaseReference.child("FacturaActualKey").setValue(key2);
                databaseReference.keepSynced(true);
                startActivity(intent);
            }
        });

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

        txtprecioUnitario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (precioUnitario.getEditText().getText().toString().equals("")) {
                        precioUnitario.setHint("Precio unitario (obligatorio)");
                        txtprecioFinalPorUsuario.setText(String.valueOf(0));
                    } else if (cantidadProducto.getEditText().getText().toString().isEmpty()) {
                        String precio = precioUnitario.getEditText().getText().toString();
                        precioFinal = Double.parseDouble(precio);
                        txtprecioFinalPorUsuario.setText(String.valueOf(precioFinal));
                    } else {
                        double cantidad = Double.parseDouble(cantidadProducto.getEditText().getText().toString());
                        double precioUni = Double.parseDouble(precioUnitario.getEditText().getText().toString());
                        double res = precioUni * cantidad;
                        txtprecioFinalPorUsuario.setText(String.valueOf(format.format(res)));
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
                    if (cantidadProducto.getEditText().getText().toString().equals("")) {
                        cantidadProducto.setHint("Cantidad (obligatorio)");
                        txtprecioFinalPorUsuario.setText(String.valueOf(precioFinal));
                    } else {
                        String cantidad = cantidadProducto.getEditText().getText().toString();
                        double cantidadFloat = Double.parseDouble(cantidad);
                        //operaciones
                        double res = cantidadFloat * precioFinal;
                        if (cantidad.isEmpty()) {
                            cantidadProducto.setHelperTextEnabled(true);
                            cantidadProducto.setHelperText("Cantidad * Precio Unitario");
                        } else if (cantidad.equals("0")) {
                            txtprecioFinalPorUsuario.setText(String.valueOf(format.format(precioFinal)));
                        } else {
                            txtprecioFinalPorUsuario.setText(String.valueOf(format.format(res)));
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
                //condiciones para guardar el producto a la lista
                try {
                    if (edtProducto.getEditText().getText().toString().isEmpty()) {
                        edtProducto.setError("Ingrese un nombre");
                    } else if (precioUnitario.getEditText().getText().toString().isEmpty()) {
                        precioUnitario.setErrorEnabled(true);
                        precioUnitario.setError("Ingrese un valor");
                        edtProducto.setErrorEnabled(false);
                    } else if (cantidadProducto.getEditText().getText().toString().isEmpty()) {
                        cantidadProducto.setErrorEnabled(true);
                        cantidadProducto.setError("ingrese un valor");
                        precioUnitario.setErrorEnabled(false);
                    } else {

                        //Obtener datos del usuario en los edit Text
                        String nombre_producto = edtProducto.getEditText().getText().toString();
                        double precio_producto = Double.parseDouble(precioUnitario.getEditText().getText().toString());
                        double cantidad_producto2 = Double.parseDouble(cantidadProducto.getEditText().getText().toString());
                        double cantidad_producto = Double.parseDouble(cantidadProducto.getEditText().getText().toString());
                        double precio_final_producto = Double.parseDouble(precioFinalPorUsuario.getEditText().getText().toString());
                        String id = databaseReference.push().getKey();

                        //Variables iniciales para el constructor del ModeloVista REqueridos o faltantes
                        int precioTotaldeTodosLosProductos = 0;
                        double valorTotalCalculadoAutomatico = precio_producto * cantidad_producto;
                        ModeloVenta modeloVenta = new ModeloVenta();
                        modeloVenta.setId(id);
                        modeloVenta.setNombreProdcuto(nombre_producto);
                        modeloVenta.setPrecioProducto(precio_producto);
                        modeloVenta.setCantidadProducto(cantidad_producto);
                        modeloVenta.setPrecioFinalPorElUsuario(precio_final_producto);
                        modeloVenta.setValorTotalCalculadoAutomatico(valorTotalCalculadoAutomatico);
                        modeloVenta.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                        modeloVenta.setTimeStamp(getFechaMilisegundos() * -1);
                        modeloVenta.setPruebaDouble(cantidad_producto2);
                        //set precio total de todos los productos
                        if (modeloVenta.getPrecioFinalPorElUsuario() == 0) {
                            modeloVenta.setPrecioTotaldeTodosLosProductos(valorTotalCalculadoAutomatico);
                        } else {
                            modeloVenta.setPrecioTotaldeTodosLosProductos(precio_final_producto);
                        }

                        databaseReference.child("facturas").child("fechas").child("listaDeFacturas").child(key).child(id).setValue(modeloVenta).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                edtProducto.getEditText().setText("");
                                precioUnitario.getEditText().setText("");
                                cantidadProducto.getEditText().setText("");
                                precioFinalPorUsuario.getEditText().setText("");
                            }
                        });
                        databaseReference.keepSynced(true);


                    }
                }catch (NumberFormatException numberFormatException){
                    FancyToast.makeText(VentasNegocio.this,"Verifica lo signos que usas en los campos!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }

            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnGuardarFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!premium) {
                    if (total_factura < 15000) {
                        crearVenta(nformat);
                    } else {
                        DialogPlus dialog = DialogPlus.newDialog(VentasNegocio.this)
                                .setContentHolder(new ViewHolder(R.layout.dialog_gold_v2))
                                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                                .setExpanded(true, 1510)
                                .setGravity(Gravity.BOTTOM)
                                .setContentBackgroundResource(android.R.color.transparent)
                                .create();

                        View views = dialog.getHolderView();

                        RelativeLayout btnAcutualizar = views.findViewById(R.id.actualizar);


                        btnAcutualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(VentasNegocio.this, PlanesMenuPrincipal.class));
                            }
                        });

                        dialog.show();
                    }

                } else {
                    crearVenta(nformat);
                }


            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarFacturaActual();
            }
        });


        //FIREBASE
        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVenta> options =
                new FirebaseRecyclerOptions.Builder<ModeloVenta>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key), ModeloVenta.class)
                        .build();
        adaptadorListaProductos = new AdaptadorListaProductos(options);
        listaDeProductos.setAdapter(adaptadorListaProductos);

        //FIREBASE
        lista_de_productos_venta_inventario.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVentaInventario> options2 =
                new FirebaseRecyclerOptions.Builder<ModeloVentaInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturasIV").child(key2), ModeloVentaInventario.class)
                        .build();
        adaptadorListaProductosEnInventario = new AdaptadorListaProductosEnInventario(options2);
        lista_de_productos_venta_inventario.setAdapter(adaptadorListaProductosEnInventario);

    }

    private void crearVenta(NumberFormat nformat) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final Calendar Cal = Calendar.getInstance();
        dia = Cal.get(Calendar.DAY_OF_MONTH);
        mes = Cal.get(Calendar.MONTH);
        ano = Cal.get(Calendar.YEAR);

        DialogPlus dialogPlus = DialogPlus.newDialog(btnGuardarFactura.getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_confirm_factura))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setExpanded(true, 1470)
                .setGravity(Gravity.BOTTOM)
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


        txtTotalFactura.setText(String.valueOf("$ " + nformat.format(totalDeFactura + totalDeFacturaInventario)));

        //CLick Listener and others
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    txtTotalFactura.setTextColor(getResources().getColor(R.color.rojo));
                    cliente.setHelperTextEnabled(true);
                    cliente.setHelperText("¡Importante!");
                    cliente.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.rojo)));
                    estadoDePago = false;
                } else {
                    txtTotalFactura.setTextColor(getResources().getColor(R.color.verde));
                    cliente.setHelperText("Recomendado");
                    cliente.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.verde)));
                    estadoDePago = true;
                    cliente.setHelperTextEnabled(true);
                }
            }
        });

        selecFechaFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VentasNegocio.this, new DatePickerDialog.OnDateSetListener() {
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
                modeloFacturaCreada.setTotalCalculado(totalDeFactura + totalDeFacturaInventario);
                modeloFacturaCreada.setMetodoDePago(datos_metodo_de_pago);
                modeloFacturaCreada.setFechaRegistro(sdf.format(Cal.getTime()));
                modeloFacturaCreada.setTimeStamp(getFechaMilisegundos() * -1);
                modeloFacturaCreada.setYear(String.valueOf(Cal.get(Calendar.YEAR)));
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
                modeloFacturaCreada.setMonth(String.valueOf(sdf2.format(Cal.getTime())));
                SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
                modeloFacturaCreada.setDay(String.valueOf(sdfDay.format(Cal.getTime())));
                modeloFacturaCreada.setAbonado(modeloFacturaCreada.getTotalCalculado());
                if (!estadoDePago) {
                    modeloFacturaCreada.setAbonar(modeloFacturaCreada.getTotalCalculado());
                } else {
                    modeloFacturaCreada.setAbonar(0);
                }

                databaseReference.child("facturas").child("facturasCreadas").child(key).setValue(modeloFacturaCreada);
                databaseReference.keepSynced(true);

                //BORRAR DATOS FACTURA ACTUAL
                borrarDatosDeFacturaActual();
                adaptadorListaProductos.notifyDataSetChanged();
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

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturasIV").child(key2).removeValue();
        txtCrearVenta.setText("$ 0");
        cabeceraFacturas.setVisibility(View.INVISIBLE);
        btnLimpiar.setVisibility(View.INVISIBLE);
        imgIlustra.setVisibility(View.VISIBLE);
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

    private void hacerSumaTotalDeProductos() {
        databaseReference.child("facturas").child("fechas").child("listaDeFacturas").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object suma = map.get("precioTotaldeTodosLosProductos");
                    int total = Integer.parseInt(String.valueOf(suma));
                    sum += total;
                    totalDeFactura = sum;
                    txtCrearVenta.setText(String.valueOf("$ " + nformat.format(totalDeFactura + totalDeFacturaInventario)));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void hacerSumaTotalDeProductosInventario() {

        FirebaseDatabase firebaseDatabase1;
        DatabaseReference databaseReference1;

        firebaseDatabase1 = FirebaseDatabase.getInstance();

        databaseReference1 = firebaseDatabase1.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("fechas").child("listaDeFacturasIV");

        databaseReference1.child(key2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object suma = map.get("precioTotaldeTodosLosProductos");
                    int total = Integer.parseInt(String.valueOf(suma));
                    sum += total;
                    totalDeFacturaInventario = sum;
                    txtCrearVenta.setText(String.valueOf("$ " + nformat.format(totalDeFactura + totalDeFacturaInventario)));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        hacerSumaTotalDeProductos();
        hacerSumaTotalDeProductosInventario();
        hacerSumaDeItems();
        hacerSumaDeItems2();
        adaptadorListaProductos.startListening();
        adaptadorListaProductosEnInventario.startListening();
        adaptadorListaProductos.notifyDataSetChanged();
    }


    private void hacerSumaDeItems() {
        adaptadorListaProductos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (adaptadorListaProductos.getItemCount() == 0) {
                    cabeceraFacturas.setVisibility(View.GONE);
                    btnLimpiar.setVisibility(View.GONE);
                    imgIlustra.setVisibility(View.VISIBLE);
                    btnGuardarFactura.setVisibility(View.INVISIBLE);
                } else {
                    cabeceraFacturas.setVisibility(View.VISIBLE);
                    btnLimpiar.setVisibility(View.VISIBLE);
                    imgIlustra.setVisibility(View.GONE);
                    btnGuardarFactura.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hacerSumaDeItems2() {
        adaptadorListaProductosEnInventario.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (adaptadorListaProductosEnInventario.getItemCount() == 0) {
                    cabeceraFacturas.setVisibility(View.GONE);
                    btnLimpiar.setVisibility(View.GONE);
                    imgIlustra.setVisibility(View.VISIBLE);
                    btnGuardarFactura.setVisibility(View.INVISIBLE);
                } else {
                    cabeceraFacturas.setVisibility(View.VISIBLE);
                    btnLimpiar.setVisibility(View.VISIBLE);btnGuardarFactura.setVisibility(View.VISIBLE);
                }
                    imgIlustra.setVisibility(View.GONE);

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductos.stopListening();
        adaptadorListaProductosEnInventario.stopListening();
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
                Toasty.info(VentasNegocio.this, "Resultado: " + result, Toast.LENGTH_LONG, true).show();
            }
        }.showDIalog();
    }

    public void irAYoutube(View view) {
    }
}