package com.fordev.taski.adaptadores;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.DetallesFacturaVentas;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloFacturaCreada;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdaptadorListaFacturas extends FirebaseRecyclerAdapter<ModeloFacturaCreada, AdaptadorListaFacturas.myViewHolder>
{
    AdaptadorListaProductos adaptadorListaProductos;
    public AdaptadorListaFacturas(@NonNull FirebaseRecyclerOptions<ModeloFacturaCreada> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorListaFacturas.myViewHolder holder, int position, @NonNull ModeloFacturaCreada model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(model.getFechaRegistro());
            //Poner ej: lunes en Lun.
            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            String dayOfTheWeek = sdf.format(date);
            if (dayOfTheWeek.equals("dom.")){
                holder.dia_fragmento.setCardBackgroundColor(holder.dia_corto.getContext().getResources().getColor(R.color.rojo));
            }else if (dayOfTheWeek.equals("sáb.")){
                holder.dia_fragmento.setCardBackgroundColor(holder.dia_corto.getContext().getResources().getColor(R.color.gnt_blue));
            }
            holder.dia_corto.setText(dayOfTheWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date date = dateFormat.parse(model.getFechaRegistro());
            SimpleDateFormat sdf = new SimpleDateFormat("MM.yyyy");
            String mes_year = sdf.format(date);
            holder.mes_year_fecha.setText(mes_year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));*/
        if (model.getCliente().isEmpty()){
            holder.name_cliente.setText("Cliente");
        }else{
            holder.name_cliente.setText(model.getCliente());
        }
        holder.txtMetodo.setText(model.getMetodoDePago());
        holder.conceptoVenta.setText(model.getConceptoDeVenta());
        holder.dia_fecha.setText(model.getDay());
        if (model.isEstadoDePago()){
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
            holder.iconPagado.setImageDrawable(holder.iconPagado.getContext().getResources().getDrawable(R.drawable.ic_transaccion));
        }else {
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
            holder.iconPagado.setImageDrawable(holder.iconPagado.getContext().getResources().getDrawable(R.drawable.ic_transaccion_no_pagada));
            holder.iconPunto.setColorFilter(holder.total.getContext().getResources().getColor(R.color.rojo));
            holder.iconFlecha.setColorFilter(holder.total.getContext().getResources().getColor(R.color.rojo));
        }

        holder.iconFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.iconPagado.getContext(), DetallesFacturaVentas.class);
                String key = model.getId();
                String keyIV = model.getIdInventario();
                intent.putExtra("key", key);
                intent.putExtra("keyIV", keyIV);
                v.getContext().startActivity(intent);
            }
        });

    }


    @NonNull
    @Override
    public AdaptadorListaFacturas.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_facturas_clientes, parent, false);
        return new AdaptadorListaFacturas.myViewHolder(view);
    }


    static class myViewHolder extends RecyclerView.ViewHolder{
        TextView conceptoVenta, total,dia_fecha,dia_corto,name_cliente,txtMetodo,mes_year_fecha;
        ImageView iconPagado,iconFlecha,iconPunto;
        CardView dia_fragmento;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            conceptoVenta = itemView.findViewById(R.id.nombreClientes);
            total = itemView.findViewById(R.id.total);
            dia_fecha=(TextView)itemView.findViewById(R.id.dia_fecha);
            iconPagado = itemView.findViewById(R.id.icon_estado_de_pago);
            iconFlecha = itemView.findViewById(R.id.ic_detalles_factura);
            iconPunto = itemView.findViewById(R.id.ic_restar_fehca);
            dia_corto = itemView.findViewById(R.id.dia_corto);
            txtMetodo = itemView.findViewById(R.id.txtMetodo);
            mes_year_fecha = itemView.findViewById(R.id.mes_year_fecha);
            name_cliente = itemView.findViewById(R.id.name_cliente);
            dia_fragmento = itemView.findViewById(R.id.dia_fragmento);


        }

    }


}
