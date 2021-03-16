package com.fordev.taski.adaptadores;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.R;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class AdaptadorListaInventarioPerfil extends FirebaseRecyclerAdapter<ModeloInventario, AdaptadorListaInventarioPerfil.myViewHolder>
{
    double cantidad_a_restar_stock;
    boolean bandera = false;
    //Obtener la factura actual para mandarla
    FirebaseDatabase  firebaseDatabase,firebaseDatabase2;
    DatabaseReference databaseReference,databaseReference2;
    String idIngrsesar;

    public AdaptadorListaInventarioPerfil(@NonNull FirebaseRecyclerOptions<ModeloInventario> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloInventario model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        DecimalFormat format = new DecimalFormat("0.#");
        holder.txtProducto.setText(model.getNombreProdcuto());
        holder.txtPrecioItem.setText("Precio Item: " +"$ " + String.valueOf(nformat.format(model.getPrecioProducto())));
        holder.txtStockCantidad.setText("Stock Disponible: " + String.valueOf(format.format(model.getCantidadProducto())));
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


        holder.ic_editar_item_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat nformat = new DecimalFormat("##,###,###.##");
                DecimalFormat format = new DecimalFormat("0.#");
                DialogPlus dialogEdit = DialogPlus.newDialog(holder.ic_editar_item_inventario.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_editar_inventario))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1450)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();
                View views = dialogEdit.getHolderView();

                TextInputLayout cantidad_stock = views.findViewById(R.id.cantidad);
                TextInputLayout nombreAEditar = views.findViewById(R.id.nombreProductoEditar);
                TextInputLayout precioAEditar = views.findViewById(R.id.numeroCliente);
                TextInputEditText cantidad_stock_txt = views.findViewById(R.id.txtcantidad);
                MaterialButton btn_guardar_edicion = views.findViewById(R.id.btn_guardar_edicion);
                TextView btn_dismiss = views.findViewById(R.id.btn_cancelar);
                TextView txtNombreProductoSeleccionado = views.findViewById(R.id.nombreClientes);
                TextView txtPrecioProductoSeleccionado = views.findViewById(R.id.numeroClientes);
                TextView txtStockProductoSeleccionado = views.findViewById(R.id.txtStock);
                ImageView icon_de_incrementos = views.findViewById(R.id.icon_de_incrementos);

                //Seteos por DEfecto de la base de Datos
                nombreAEditar.getEditText().setText(model.getNombreProdcuto());
                precioAEditar.getEditText().setText(String.valueOf(format.format(model.getPrecioProducto())));
                if (model.getCantidadProducto()<0){
                    cantidad_stock.getEditText().setText(String.valueOf(0));
                }else {
                    cantidad_stock.getEditText().setText(String.valueOf(format.format(model.getCantidadProducto())));
                }

                icon_de_incrementos.setVisibility(View.INVISIBLE);

                txtNombreProductoSeleccionado.setText(model.getNombreProdcuto());
                txtPrecioProductoSeleccionado.setText("Precio item: " + "$ " + String.valueOf(nformat.format(model.getPrecioProducto())));
                txtStockProductoSeleccionado.setText("Stock Disponible: " + String.valueOf(format.format(model.getCantidadProducto())));

                cantidad_stock_txt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (cantidad_stock.getEditText().getText().toString().isEmpty()){
                            cantidad_stock.setHint("Ingrese un valor");
                            txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(format.format(model.getCantidadProducto())));
                            icon_de_incrementos.setVisibility(View.INVISIBLE);
                        }else{
                            double cantidadStockInventario = Double.parseDouble(String.valueOf(format.format(model.getCantidadProducto())));
                            //CANTIDAD INGRESADA POR EL USUARIO
                            double canitdadIngresad = Double.parseDouble(cantidad_stock_txt.getText().toString());

                            if (canitdadIngresad > cantidadStockInventario){
                                txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(format.format(canitdadIngresad)));
                                icon_de_incrementos.setVisibility(View.VISIBLE);
                                icon_de_incrementos.setImageDrawable(holder.ic_editar_item_inventario.getContext().getResources().getDrawable(R.drawable.ic_incremento));
                            }else {
                                txtStockProductoSeleccionado.setText("Stock Disponible: "+ String.valueOf(format.format(canitdadIngresad)));
                                icon_de_incrementos.setVisibility(View.VISIBLE);
                                icon_de_incrementos.setImageDrawable(holder.ic_editar_item_inventario.getContext().getResources().getDrawable(R.drawable.ic_decremento));
                            }

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                btn_guardar_edicion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (nombreAEditar.getEditText().getText().toString().isEmpty()) {
                            nombreAEditar.setError("Ingrese un nombre");
                        }else if (precioAEditar.getEditText().getText().toString().isEmpty()){
                            precioAEditar.setError("Ingrese un precio");
                        }else if (cantidad_stock.getEditText().getText().toString().isEmpty()){
                            cantidad_stock.setError("Ingrese una cantidad");
                        } else {
                            String nombreEditado = nombreAEditar.getEditText().getText().toString();
                            double precioEditado = Double.parseDouble(precioAEditar.getEditText().getText().toString());
                            double cantidadEditado = Double.parseDouble(cantidad_stock.getEditText().getText().toString());
                        //GUARDAR Y ACTUALIZAR DATOS
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombreProdcuto",nombreEditado);
                        map.put("precioProducto",precioEditado);
                        map.put("cantidadProducto",cantidadEditado);

                        firebaseDatabase2.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Inventario").child("productos").child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.success(holder.ic_editar_item_inventario.getContext(),"Editado Correctamente!",Toast.LENGTH_LONG, true).show();
                                        dialogEdit.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(holder.ic_editar_item_inventario.getContext(),"Hubo un error al editar!",Toast.LENGTH_LONG, true).show();
                            }
                        });

                        }
                        dialogEdit.dismiss();

                    }
                });

                btn_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogEdit.dismiss();
                    }
                });

                dialogEdit.show();
            }
        });

    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_inventario_perfil, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtProducto, txtStockCantidad,txtPrecioItem;
        CardView bajoStockView;
        ImageView ic_editar_item_inventario;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto=(TextView)itemView.findViewById(R.id.nombreClientes);
            txtStockCantidad=(TextView)itemView.findViewById(R.id.txtStock);
            bajoStockView=(CardView) itemView.findViewById(R.id.bajo_stock_visible);
            ic_editar_item_inventario=(ImageView) itemView.findViewById(R.id.ic_editar_item_inventario);
            txtPrecioItem=(TextView) itemView.findViewById(R.id.numeroClientes);

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
