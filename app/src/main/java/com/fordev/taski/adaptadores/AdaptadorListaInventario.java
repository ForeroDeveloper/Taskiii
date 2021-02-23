package com.fordev.taski.adaptadores;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaInventario extends FirebaseRecyclerAdapter<ModeloInventario, AdaptadorListaInventario.myViewHolder>
{

    public AdaptadorListaInventario(@NonNull FirebaseRecyclerOptions<ModeloInventario> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloInventario model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        holder.txtProducto.setText(model.getNombreProdcuto());
        holder.txtStockCantidad.setText("Stock: " + String.valueOf(model.getCantidadProducto()));
        if (model.getCantidadProducto() <= 5){
            holder.bajoStockView.setVisibility(View.VISIBLE);
        }else {
            holder.bajoStockView.setVisibility(View.INVISIBLE);
        }

        holder.ic_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Inventario inventario = new Inventario();
                String dato = inventario.keyy;


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

        TextView txtProducto, txtStockCantidad;
        CardView bajoStockView;
        ImageView ic_agregar_producto;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto=(TextView)itemView.findViewById(R.id.nombreProducto);
            txtStockCantidad=(TextView)itemView.findViewById(R.id.txtStock);
            bajoStockView=(CardView) itemView.findViewById(R.id.bajo_stock_visible);
            ic_agregar_producto=(ImageView) itemView.findViewById(R.id.ic_agregar_producto);

        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
