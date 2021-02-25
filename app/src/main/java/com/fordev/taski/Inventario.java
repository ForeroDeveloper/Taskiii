package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaInventario;
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.modelos.ModeloInventario;
import com.fordev.taski.modelos.ModeloVenta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Map;
import java.util.TimeZone;

public class Inventario extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView listaDeProductos;
    AdaptadorListaInventario adaptadorListaInventario;
    MaterialButton faq_add,regresar;
    TextView totalProductos, txtTotalStock;
    ImageView prueba;
    public String keyy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);

        listaDeProductos = findViewById(R.id.lista_de_productos_inventario);
        faq_add = findViewById(R.id.faq_inventario);
        totalProductos = findViewById(R.id.txtTotalProductos);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        regresar = findViewById(R.id.regresar);
        prueba = findViewById(R.id.search);

        //Obtener Intent EXTRA
        Bundle bundle = getIntent().getExtras();
        keyy = bundle.getString("key");


        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum = 0;

                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object price = map.get("cantidadProducto");
                        int pValue = Integer.parseInt(String.valueOf(price));
                            sum += pValue;
                            txtTotalStock.setText(String.valueOf(sum));
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //key
                String id = databaseReference.push().getKey();
                DialogPlus dialogo = DialogPlus.newDialog(faq_add.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_inventario))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1100)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialog = dialogo.getHolderView();

                MaterialButton btn_guardar_producto_inventario = dialog.findViewById(R.id.btn_guardar_producto_inventario);
                TextInputLayout nombre_Producto = dialog.findViewById(R.id.nombreProducto);
                TextInputLayout precio_unitario = dialog.findViewById(R.id.precio);
                TextInputLayout cantidad_stock = dialog.findViewById(R.id.cantidad);

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

                            String nombreProducto = nombre_Producto.getEditText().getText().toString();
                            int precioUnitario = Integer.parseInt(precio_unitario.getEditText().getText().toString());
                            int cantidadStock = Integer.parseInt(cantidad_stock.getEditText().getText().toString());

                            ModeloInventario modeloInventario = new ModeloInventario();

                            modeloInventario.setNombreProdcuto(nombreProducto);
                            modeloInventario.setPrecioProducto(precioUnitario);
                            modeloInventario.setCantidadProducto(cantidadStock);
                            modeloInventario.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                            modeloInventario.setTimeStamp(getFechaMilisegundos() * -1);
                            modeloInventario.setId(id);
                            databaseReference.child(id).setValue(modeloInventario);
                            dialogo.dismiss();
                        }

                    }
                });

                dialogo.show();
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloInventario> options =
                new FirebaseRecyclerOptions.Builder<ModeloInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos"), ModeloInventario.class)
                        .build();



        adaptadorListaInventario=new AdaptadorListaInventario(options);
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

}