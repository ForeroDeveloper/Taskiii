package com.fordev.taski.adaptadores;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.Inventario;
import com.fordev.taski.R;
import com.fordev.taski.VentasNegocio;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AdaptadorListaInventario extends FirebaseRecyclerAdapter<ModeloInventario, AdaptadorListaInventario.myViewHolder>
{
    int cantidad_a_restar_stock;
    boolean bandera = false;
    //Obtener la factura actual para mandarla
    FirebaseDatabase  firebaseDatabase,firebaseDatabase2;
    DatabaseReference databaseReference,databaseReference2;
    String idIngrsesar;


    public AdaptadorListaInventario(@NonNull FirebaseRecyclerOptions<ModeloInventario> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloInventario model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        holder.txtProducto.setText(model.getNombreProdcuto());
        holder.txtPrecioItem.setText("Precio Item: " +"$ " + String.valueOf(model.getPrecioProducto()));
        holder.txtStockCantidad.setText("Stock: " + String.valueOf(model.getCantidadProducto()));
        if (model.getCantidadProducto() <= 5){
            holder.bajoStockView.setVisibility(View.VISIBLE);
        }else {
            holder.bajoStockView.setVisibility(View.INVISIBLE);
        }

        firebaseDatabase2 = FirebaseDatabase.getInstance();
        databaseReference2 = firebaseDatabase2.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("FacturaActualKey");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object key = snapshot.getValue();
                idIngrsesar = String.valueOf(key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.ic_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat nformat = new DecimalFormat("##,###,###.##");
                DialogPlus dialog = DialogPlus.newDialog(holder.ic_agregar_producto.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_a_factura))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1000)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("facturas").child("fechas").child("listaDeFacturasIV").child(idIngrsesar);

                TextInputLayout cantidad_stock = views.findViewById(R.id.cantidad);
                TextInputEditText cantidad_stock_txt = views.findViewById(R.id.txtcantidad);
                MaterialButton btn_guardar_cantidad = views.findViewById(R.id.btn_guardar_producto_inventario);
                TextView txtNombreProductoSeleccionado = views.findViewById(R.id.nombreProducto);
                TextView txtPrecioProductoSeleccionado = views.findViewById(R.id.precio_item);
                TextView txtStockProductoSeleccionado = views.findViewById(R.id.txtStock);
                CardView bajo_stock_visible = views.findViewById(R.id.bajo_stock_visible);

                //Verificar si stock es bajo;
                if (model.getCantidadProducto() <= 5){
                    bajo_stock_visible.setVisibility(View.VISIBLE);
                }else{
                    bajo_stock_visible.setVisibility(View.INVISIBLE);
                }

                txtNombreProductoSeleccionado.setText(model.getNombreProdcuto());
                txtPrecioProductoSeleccionado.setText("Precio item: " + "$ " + String.valueOf(model.getPrecioProducto()));
                txtStockProductoSeleccionado.setText("Stock: " + String.valueOf(model.getCantidadProducto()));

                cantidad_stock_txt.addTextChangedListener(new TextWatcher() {
                    int resta = 0;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (cantidad_stock.getEditText().getText().toString().isEmpty()){
                            cantidad_stock.setHint("Ingrese un valor");
                            txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(model.getCantidadProducto()));
                        }else{
                            int cantidadStockInventario = model.getCantidadProducto();
                            //CANTIDAD INGRESADA POR EL USUARIO
                            int canitdadIngresad = Integer.parseInt(cantidad_stock_txt.getText().toString());
                            resta = cantidadStockInventario - canitdadIngresad;
                            txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(resta));
                            cantidad_a_restar_stock = resta;
                        }

                        //cambiar stock en bajo o disabled
                        if (resta <= 5){
                            bajo_stock_visible.setVisibility(View.VISIBLE);
                        }else {
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

                            if (!cantidad_stock.getEditText().getText().toString().isEmpty()){

                                        String put = databaseReference.push().getKey();

                                        ModeloVenta modeloVenta = new ModeloVenta();
                                        int cantidad = Integer.parseInt(cantidad_stock_txt.getText().toString());
                                        int precio = model.getPrecioProducto();
                                        int total = precio * cantidad;
                                        modeloVenta.setId(put);
                                        modeloVenta.setNombreProdcuto(model.getNombreProdcuto());
                                        modeloVenta.setCantidadProducto(cantidad);
                                        modeloVenta.setPrecioFinalPorElUsuario(total);
                                        modeloVenta.setValorTotalCalculadoAutomatico(total);
                                        modeloVenta.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                                        modeloVenta.setTimeStamp(getFechaMilisegundos() * -1);
                                        modeloVenta.setPrecioTotaldeTodosLosProductos(total);
                                        databaseReference.child(put).setValue(modeloVenta);


                                //update stock dependiento la posicion
                                Map<String, Object> map = new HashMap<>();
                                map.put("cantidadProducto",cantidad_a_restar_stock);

                                firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("Inventario").child("productos").child(getRef(position).getKey()).updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }else {
                                cantidad_stock.setError("Ingrese un valor !");
                            }

                    }
                });

                dialog.show();
            }
        });

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_inventario, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtProducto, txtStockCantidad,txtPrecioItem;
        CardView bajoStockView;
        ImageView ic_agregar_producto;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto=(TextView)itemView.findViewById(R.id.nombreProducto);
            txtStockCantidad=(TextView)itemView.findViewById(R.id.txtStock);
            bajoStockView=(CardView) itemView.findViewById(R.id.bajo_stock_visible);
            ic_agregar_producto=(ImageView) itemView.findViewById(R.id.ic_agregar_producto);
            txtPrecioItem=(TextView) itemView.findViewById(R.id.precio_item);

        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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

}
