package com.fordev.taski.adaptadores;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloFacturaCreada;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaFacturas extends FirebaseRecyclerAdapter<ModeloFacturaCreada, AdaptadorListaFacturas.myViewHolder>
{
    AdaptadorListaProductos adaptadorListaProductos;
    public AdaptadorListaFacturas(@NonNull FirebaseRecyclerOptions<ModeloFacturaCreada> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorListaFacturas.myViewHolder holder, int position, @NonNull ModeloFacturaCreada model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");

        holder.conceptoVenta.setText(model.getConceptoDeVenta());
        if (model.isEstadoDePago()){
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
            holder.iconPagado.setImageDrawable(holder.iconPagado.getContext().getResources().getDrawable(R.drawable.ic_transaccion));
        }else {
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
            holder.iconPagado.setImageDrawable(holder.iconPagado.getContext().getResources().getDrawable(R.drawable.ic_transaccion_no_pagada));
            holder.iconPunto.setColorFilter(holder.total.getContext().getResources().getColor(R.color.rojo));
        }

/*        holder.iconFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.iconPagado.getContext(),GastosNegocio.class);
                String key = model.getId();
                intent.putExtra("key", key);
                v.getContext().startActivity(intent);
            }
        });*/

    }


    @NonNull
    @Override
    public AdaptadorListaFacturas.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_facturas_clientes, parent, false);
        return new AdaptadorListaFacturas.myViewHolder(view);
    }


    static class myViewHolder extends RecyclerView.ViewHolder{
        TextView conceptoVenta, total;
        ImageView iconPagado,iconFlecha,iconPunto;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            conceptoVenta = itemView.findViewById(R.id.nombreProducto);
            total = itemView.findViewById(R.id.total);
            iconPagado = itemView.findViewById(R.id.icon_estado_de_pago);
            iconPagado = itemView.findViewById(R.id.icon_estado_de_pago);
            iconPunto = itemView.findViewById(R.id.ic_restar_fehca);


        }

    }


}
