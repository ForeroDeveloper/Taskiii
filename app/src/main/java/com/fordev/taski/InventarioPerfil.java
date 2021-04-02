package com.fordev.taski;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.DialogCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaInventarioPerfil;
import com.fordev.taski.modelos.ModeloInventario;
import com.fordev.taski.modelos.ModeloVenta;
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

public class InventarioPerfil extends AppCompatActivity {
    DatabaseReference databaseReference,databaseReference2;
    FirebaseDatabase firebaseDatabase;
    RecyclerView listaDeProductos;
    AdaptadorListaInventarioPerfil adaptadorListaInventarioPerfil;
    MaterialButton faq_add,regresar;
    TextView totalProductos, txtTotalStock,txtTotalInventario;
    CardView sinContenidoInventario,scannerQr;
    boolean premium = true;
    boolean gold = true;
    String codigoProducto = "";
    DecimalFormat format = new DecimalFormat("0.#");
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    SearchView searchView;
    int total_factura = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario_perfil);

        listaDeProductos = findViewById(R.id.lista_de_productos_inventario);
        faq_add = findViewById(R.id.faq_nuevo_pedido);
        totalProductos = findViewById(R.id.txtTotalPedidos);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        searchView = findViewById(R.id.search_view);
        sinContenidoInventario = findViewById(R.id.ilustracion);
        txtTotalInventario = findViewById(R.id.txtTotalInventario);
        scannerQr = findViewById(R.id.scannerQr);
        regresar = findViewById(R.id.regresar);



        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");

        databaseReference2 = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference2.child("info").addValueEventListener(new ValueEventListener() {
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
                total_factura = (int) snapshot.getChildrenCount();
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object price = map.get("cantidadProducto");
                        double pValue = Double.parseDouble(String.valueOf(price));
                            sum += pValue;
                            txtTotalStock.setText(String.valueOf(format.format(sum)));
                                sinContenidoInventario.setVisibility(View.GONE);
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
                IntentIntegrator intentIntegrator = new IntentIntegrator(InventarioPerfil.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Sube el Volumen + , y activa el Flash");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.initiateScan();
            }
        });


        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloInventario> options =
                new FirebaseRecyclerOptions.Builder<ModeloInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos"), ModeloInventario.class)
                        .build();



        adaptadorListaInventarioPerfil=new AdaptadorListaInventarioPerfil(options);
        listaDeProductos.setAdapter(adaptadorListaInventarioPerfil);
        adaptadorListaInventarioPerfil.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaInventarioPerfil.getItemCount()));

            }
        });


    }

    private void serGoldDialog() {
        DialogPlus dialog = DialogPlus.newDialog(InventarioPerfil.this)
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
                startActivity(new Intent(InventarioPerfil.this, PlanesMenuPrincipal.class));
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
        TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);
        TextInputLayout codigoDeBarras = dialog.findViewById(R.id.codigoDeBarras);

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
                    if (codeDeBarras.isEmpty() || codeDeBarras.equals("")){
                        int randomQr = (int) (Math.random() * (3000 - 1000));
                        codigoProducto = String.valueOf(randomQr);
                    }else {
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

    private void procesobuscar(String s) {

        FirebaseRecyclerOptions<ModeloInventario> options =
                new FirebaseRecyclerOptions.Builder<ModeloInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos")
                                .orderByChild("nombreProdcuto").startAt(s).endAt(s + "\uf8ff"), ModeloInventario.class)
                        .build();

        adaptadorListaInventarioPerfil=new AdaptadorListaInventarioPerfil(options);
        adaptadorListaInventarioPerfil.startListening();
        listaDeProductos.setAdapter(adaptadorListaInventarioPerfil);
        adaptadorListaInventarioPerfil.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                    totalProductos.setText(String.valueOf(adaptadorListaInventarioPerfil.getItemCount()));
            }
        });

    }

    private void totalDelInventario() {

        FirebaseDatabase firebaseDatabase1;
        DatabaseReference databaseReference1;

        firebaseDatabase1 = FirebaseDatabase.getInstance();

        databaseReference1 = firebaseDatabase1.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalInventario = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object precioProductoObject = map.get("precioProducto");
                    Object cantidadProductoObject = map.get("cantidadProducto");
                    int totalPrecio = Integer.parseInt(String.valueOf(precioProductoObject));
                    double totalCantidad = Double.parseDouble(String.valueOf(cantidadProductoObject));
                    totalInventario += totalPrecio*totalCantidad;
                }
                txtTotalInventario.setText(String.valueOf("$ " + nformat.format(totalInventario)));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference1.keepSynced(true);
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
        totalDelInventario();
        adaptadorListaInventarioPerfil.startListening();
        adaptadorListaInventarioPerfil.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaInventarioPerfil.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {

            DialogPlus dialog = DialogPlus.newDialog(InventarioPerfil.this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_copiar_qr))
                    .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                    .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setGravity(Gravity.CENTER)
                    .setContentBackgroundResource(android.R.color.transparent)
                    .create();

            View view = dialog.getHolderView();

            TextView codigoQR = view.findViewById(R.id.codigoQR);
            MaterialButton btn_copiar = view.findViewById(R.id.btn_copiar);
            TextView btn_cancel = view.findViewById(R.id.btn_cancel);

            codigoQR.setText(result.getContents());

            btn_copiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClipboard(InventarioPerfil.this, codigoQR.getText().toString());
                    FancyToast.makeText(InventarioPerfil.this, "Copiado correctamente!", FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.logo_taski, false).show();
                    dialog.dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


}