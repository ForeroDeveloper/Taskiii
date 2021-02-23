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

public class AdaptadorListaInventario extends FirebaseRecyclerAdapter<ModeloInventario, AdaptadorListaInventario.myViewHolder>
{
    int cantidad_a_restar_stock;

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

        holder.ic_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(holder.ic_agregar_producto.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_a_factura))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1000)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();

                TextInputLayout cantidad_stock = views.findViewById(R.id.cantidad);
                TextInputEditText cantidad_stock_txt = views.findViewById(R.id.txtcantidad);
                MaterialButton btn_guardar_cantidad = views.findViewById(R.id.btn_guardar_producto_inventario);
                TextView txtNombreProductoSeleccionado = views.findViewById(R.id.nombreProducto);
                TextView txtPrecioProductoSeleccionado = views.findViewById(R.id.precio_item);
                TextView txtStockProductoSeleccionado = views.findViewById(R.id.txtStock);

                txtNombreProductoSeleccionado.setText(model.getNombreProdcuto());
                txtPrecioProductoSeleccionado.setText("Precio item: " + "$ " + String.valueOf(model.getPrecioProducto()));
                txtStockProductoSeleccionado.setText("Stock: " + String.valueOf(model.getCantidadProducto()));

                cantidad_stock_txt.addTextChangedListener(new TextWatcher() {
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
                            int resta = cantidadStockInventario - canitdadIngresad;
                            txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(resta));
                            cantidad_a_restar_stock = resta;
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
                            int stock = Integer.parseInt(cantidad_stock.getEditText().getText().toString());
                            //Obtener la factura actual para mandarla
                            FirebaseDatabase  fire = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference, databaseReference2;
                            databaseReference = fire.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("facturas").child("fechas").child("listaDeFacturas").child("FacturaActual");

                            databaseReference2 = fire.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("facturas").child("fechas").child("listaDeFacturas");
                            //id
                            String id = databaseReference.push().getKey();


                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String keyFacAct;
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                        Object keyDb = map.get("idFacturaActual");
                                        String key = String.valueOf(keyDb);
                                        ModeloVenta modeloVenta = new ModeloVenta();
                                        int cantidad = Integer.parseInt(cantidad_stock_txt.getText().toString());
                                        int precio = model.getPrecioProducto();
                                        int total = precio * cantidad;
                                        modeloVenta.setId(id);
                                        modeloVenta.setNombreProdcuto(model.getNombreProdcuto());
                                        modeloVenta.setCantidadProducto(cantidad);
                                        modeloVenta.setValorTotalCalculadoAutomatico(total);
                                        databaseReference2.child(key).child(id).setValue(modeloVenta);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //update stock dependiento la posicion
                            Map<String, Object> map = new HashMap<>();
                            map.put("cantidadProducto",cantidad_a_restar_stock);

                            fire.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

}
