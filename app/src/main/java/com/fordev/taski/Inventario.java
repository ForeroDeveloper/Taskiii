package com.fordev.taski;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaInventario;
import com.fordev.taski.modelos.ModeloInventario;
import com.fordev.taski.modelos.ModeloVenta;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Inventario extends AppCompatActivity {
    DatabaseReference databaseReference, databaseReference2, databaseReference3,databaseReference4;
    FirebaseDatabase firebaseDatabase, firebaseDatabase2, firebaseDatabase3;
    RecyclerView listaDeProductos;
    AdaptadorListaInventario adaptadorListaInventario;
    MaterialButton faq_add, regresar;
    TextView totalProductos, txtTotalStock;
    CardView sinContenidoInventario, scannerQr;
    String idIngrsesar;
    String codigoProducto = "";
    public String keyy;
    int sum = 0;
    DecimalFormat format = new DecimalFormat("0.#");
    SearchView searchView;
    //Datos producto
    String nombreProducto = "";
    String idProducto = "";
    double cantidadProducto = 0;
    int precioProducto = 0;
    double cantidad_a_restar_stock = 0;
    int total_factura = 0;
    boolean premium = true;
    boolean gold = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);

        listaDeProductos = findViewById(R.id.lista_de_productos_inventario);
        faq_add = findViewById(R.id.faq_nuevo_pedido);
        totalProductos = findViewById(R.id.txtTotalPedidos);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        searchView = findViewById(R.id.search_view);
        sinContenidoInventario = findViewById(R.id.ilustracion);
        regresar = findViewById(R.id.regresar);
        scannerQr = findViewById(R.id.scannerQr);

        //Obtener Intent EXTRA
        Bundle bundle = getIntent().getExtras();
        keyy = bundle.getString("key");

        //tap target
        final Spannable spannable2 = new SpannableString("Con tu cámara podras escanear tus productos con QR en segundos y agregarlos a tu factura de venta");
        spannable2.setSpan(new UnderlineSpan(), spannable2.length() - "TapTargetView".length(),
                spannable2.length(), 0);

        SharedPreferences preferences = this.getSharedPreferences("TUTORIAL", Context.MODE_PRIVATE);
        boolean inventario = preferences.getBoolean("ClickScanner" , false);

        if (!inventario){
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.scannerQr), "Escanea tus Productos", spannable2)
                            .cancelable(false)
                            .drawShadow(true)
                            .titleTextDimen(R.dimen.text)
                            .tintTarget(false), new TapTargetView.Listener() {
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            SharedPreferences.Editor editor  = preferences.edit();
                            editor.putBoolean("ClickScanner", true);
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
        databaseReference4 = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference4.keepSynced(true);
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");


        databaseReference4.child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object booleanPremium = snapshot.child("premium").getValue();
                    Object booleanGold = snapshot.child("gold").getValue();
                    premium = Boolean.parseBoolean(String.valueOf(booleanPremium));
                    gold = Boolean.parseBoolean(String.valueOf(booleanGold));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double sum = 0;
                if (snapshot.exists()){
                    total_factura = (int) snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        try {
                            Map<String, Object> map = (Map<String, Object>) ds.getValue();
                            Object price = map.get("cantidadProducto");
                            double pValue = Double.parseDouble(String.valueOf(price));
                            sum += pValue;
                            txtTotalStock.setText(String.valueOf(format.format(sum)));
                            sinContenidoInventario.setVisibility(View.GONE);
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }

                    }

                }else {
                    sinContenidoInventario.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.keepSynced(true);

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (premium || gold){
                    crearProducto();
                }else {
                    if (total_factura < 10){
                        crearProducto();
                    }else {
                        serGoldDialog();
                    }
                }

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                procesobuscar(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                procesobuscar(s);
                return false;
            }
        });

        scannerQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(Inventario.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Sube el Volumen + , y activa el Flash");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
                facturaActualKey();
            }
        });


        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloInventario> options =
                new FirebaseRecyclerOptions.Builder<ModeloInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos"), ModeloInventario.class)
                        .build();


        adaptadorListaInventario = new AdaptadorListaInventario(options);
        listaDeProductos.setAdapter(adaptadorListaInventario);
        adaptadorListaInventario.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaInventario.getItemCount()));

            }
        });


    }

    private void serGoldDialog() {
        DialogPlus dialog = DialogPlus.newDialog(Inventario.this)
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
                startActivity(new Intent(Inventario.this, PlanesMenuPrincipal.class));
            }
        });

        dialog.show();
    }

    private void crearProducto() {
        String id = databaseReference.push().getKey();
        DialogPlus dialogo = DialogPlus.newDialog(faq_add.getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_agregar_inventario))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setExpanded(true, 1350)
                .setContentBackgroundResource(android.R.color.transparent)
                .create();

        View dialog = dialogo.getHolderView();

        MaterialButton btn_guardar_producto_inventario = dialog.findViewById(R.id.btn_guardar_producto_factura);
        TextInputLayout nombre_Producto = dialog.findViewById(R.id.nombreClientes);
        TextInputLayout precio_unitario = dialog.findViewById(R.id.valorTotalVenta);
        TextInputLayout cantidad_stock = dialog.findViewById(R.id.cantidad);
        TextInputLayout codigoDeBarras = dialog.findViewById(R.id.codigoDeBarras);
        TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);
        btn_guardar_producto_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nombre_Producto.getEditText().getText().toString().isEmpty()) {
                    nombre_Producto.setError("Ingrese un nombre");
                } else if (precio_unitario.getEditText().getText().toString().isEmpty()) {
                    precio_unitario.setError("Ingrese un valor");
                    nombre_Producto.setErrorEnabled(false);
                } else if (cantidad_stock.getEditText().getText().toString().isEmpty()) {
                    cantidad_stock.setError("Ingrese un valor");
                    precio_unitario.setErrorEnabled(false);
                } else {
                    String nombreProducto = nombre_Producto.getEditText().getText().toString().trim();
                    double precioUnitario = Double.parseDouble(precio_unitario.getEditText().getText().toString().trim());
                    double cantidadStock = Double.parseDouble(cantidad_stock.getEditText().getText().toString().trim());
                    String codeDeBarras = codigoDeBarras.getEditText().getText().toString().trim();
                    if (codeDeBarras.isEmpty() || codeDeBarras.equals("")) {
                        int randomQr = (int) (Math.random() * (3000 - 1000));
                        codigoProducto = String.valueOf(randomQr);
                    } else {
                        codigoProducto = codeDeBarras;
                    }

                    ModeloInventario modeloInventario = new ModeloInventario();
                    modeloInventario.setNombreProdcuto(nombreProducto);
                    modeloInventario.setPrecioProducto(precioUnitario);
                    modeloInventario.setCantidadProducto(cantidadStock);
                    modeloInventario.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                    modeloInventario.setTimeStamp(getFechaMilisegundos() * -1);
                    modeloInventario.setId(id);
                    modeloInventario.setCodigoDeBarras(codigoProducto);
                    databaseReference.child(id).setValue(modeloInventario);
                    databaseReference.keepSynced(true);
                    dialogo.dismiss();
                }
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
                ;
            }
        });

        dialogo.show();
    }

    private void facturaActualKey() {
        //Base de datos para agregar el producto a la factura actual con QR
        firebaseDatabase2 = FirebaseDatabase.getInstance();
        databaseReference2 = firebaseDatabase2.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("FacturaActualKey");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object key = snapshot.getValue().toString();
                    idIngrsesar = String.valueOf(key);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void procesobuscar(String s) {

        FirebaseRecyclerOptions<ModeloInventario> options =
                new FirebaseRecyclerOptions.Builder<ModeloInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos")
                                .orderByChild("nombreProdcuto").startAt(s).endAt(s + "\uf8ff"), ModeloInventario.class)
                        .build();

        adaptadorListaInventario = new AdaptadorListaInventario(options);
        adaptadorListaInventario.startListening();
        listaDeProductos.setAdapter(adaptadorListaInventario);
        adaptadorListaInventario.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                    totalProductos.setText(String.valueOf(adaptadorListaInventario.getItemCount()));
            }
        });

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
        adaptadorListaInventario.startListening();
        adaptadorListaInventario.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaInventario.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {

            DialogPlus dialog = DialogPlus.newDialog(Inventario.this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_agregar_a_factura))
                    .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setExpanded(true, 1000)
                    .setContentBackgroundResource(android.R.color.transparent)
                    .create();

            View views = dialog.getHolderView();

            firebaseDatabase3 = FirebaseDatabase.getInstance();
            databaseReference3 = firebaseDatabase3.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("facturas").child("fechas").child("listaDeFacturasIV").child(idIngrsesar);

            TextInputLayout cantidad_stock = views.findViewById(R.id.cantidad);
            TextInputEditText cantidad_stock_txt = views.findViewById(R.id.txtcantidad);
            MaterialButton btn_guardar_cantidad = views.findViewById(R.id.btn_guardar_producto_factura);
            TextView btn_dismiss = views.findViewById(R.id.btn_cancelar);
            TextView txtNombreProductoSeleccionado = views.findViewById(R.id.nombreClientes);
            TextView txtPrecioProductoSeleccionado = views.findViewById(R.id.numeroClientes);
            TextView txtStockProductoSeleccionado = views.findViewById(R.id.txtStock);
            CardView bajo_stock_visible = views.findViewById(R.id.bajo_stock_visible);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //Ciclo para ventas en deuda
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object codigo = map.get("codigoDeBarras");
                        String qr_code = String.valueOf(codigo);
                        if (qr_code.equals(result.getContents())) {
                            Object nomProdcuto = map.get("nombreProdcuto");
                            Object canProducto = map.get("cantidadProducto");
                            Object preProducto = map.get("precioProducto");
                            Object idProd = map.get("id");
                            nombreProducto = String.valueOf(nomProdcuto);
                            idProducto = String.valueOf(idProd);
                            cantidadProducto = Double.parseDouble(String.valueOf(canProducto));
                            precioProducto = Integer.parseInt(String.valueOf(preProducto));
                            txtNombreProductoSeleccionado.setText(nombreProducto);
                            txtPrecioProductoSeleccionado.setText("Precio item: " + "$ " + String.valueOf(format.format(precioProducto)));
                            txtStockProductoSeleccionado.setText("Stock: " + String.valueOf(format.format(cantidadProducto)));
                            //Verificar si stock es bajo;
                            if (cantidadProducto <= 5) {
                                bajo_stock_visible.setVisibility(View.VISIBLE);
                            } else {
                                bajo_stock_visible.setVisibility(View.INVISIBLE);
                            }

                            dialog.show();

                        }else {
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            cantidad_stock_txt.addTextChangedListener(new TextWatcher() {
                double resta = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (cantidad_stock.getEditText().getText().toString().isEmpty()) {
                        cantidad_stock.setHint("Ingrese un valor");
                        txtStockProductoSeleccionado.setText("Stock Disponible: " + String.valueOf(format.format(cantidadProducto)));
                    } else {
                        double cantidadStockInventario = cantidadProducto;
                        //CANTIDAD INGRESADA POR EL USUARIO
                        double canitdadIngresad = Double.parseDouble(cantidad_stock_txt.getText().toString());

                        resta = cantidadStockInventario - canitdadIngresad;
                        txtStockProductoSeleccionado.setText("Stock Disponible: " + String.valueOf(format.format(resta)));
                        cantidad_a_restar_stock = resta;
                    }

                    //cambiar stock en bajo o disabled
                    if (resta <= 5) {
                        bajo_stock_visible.setVisibility(View.VISIBLE);
                    } else {
                        bajo_stock_visible.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            btn_guardar_cantidad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!cantidad_stock.getEditText().getText().toString().isEmpty()) {

                        String put = databaseReference.push().getKey();

                        ModeloVenta modeloVenta = new ModeloVenta();
                        double cantidad = Double.parseDouble(cantidad_stock_txt.getText().toString());
                        double precio = precioProducto;
                        double total = precio * cantidad;
                        modeloVenta.setId(put);
                        modeloVenta.setPrecioProducto(precioProducto);
                        modeloVenta.setNombreProdcuto(nombreProducto);
                        modeloVenta.setCantidadProducto(cantidad);
                        modeloVenta.setPrecioFinalPorElUsuario(total);
                        modeloVenta.setValorTotalCalculadoAutomatico(total);
                        modeloVenta.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                        modeloVenta.setTimeStamp(getFechaMilisegundos() * -1);
                        modeloVenta.setPrecioTotaldeTodosLosProductos(total);
                        databaseReference3.child(put).setValue(modeloVenta).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FancyToast.makeText(Inventario.this,"Agregado a la factura correctamente!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FancyToast.makeText(Inventario.this,"Ocurrio un error al agregar, verificar tu conexion!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                            }
                        });


                        //update stock dependiento la posicion
                        Map<String, Object> map = new HashMap<>();
                        map.put("cantidadProducto", cantidad_a_restar_stock);

                        firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Inventario").child("productos").child(idProducto).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Inventario.this, "Error al actualizar inventario", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        cantidad_stock.setError("Ingrese un valor !");
                    }
                    dialog.dismiss();
                }
            });

            btn_dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


}