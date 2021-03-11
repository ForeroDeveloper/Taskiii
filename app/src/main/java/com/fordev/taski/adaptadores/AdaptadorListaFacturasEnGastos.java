package com.fordev.taski.adaptadores;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.DetallesFacturaGastos;
import com.fordev.taski.DetallesFacturaVentas;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloFacturaCreadaGastos;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaFacturasEnGastos extends FirebaseRecyclerAdapter<ModeloFacturaCreadaGastos, AdaptadorListaFacturasEnGastos.myViewHolder>
{
    AdaptadorListaProductos adaptadorListaProductos;
    public AdaptadorListaFacturasEnGastos(@NonNull FirebaseRecyclerOptions<ModeloFacturaCreadaGastos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorListaFacturasEnGastos.myViewHolder holder, int position, @NonNull ModeloFacturaCreadaGastos model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");

        holder.conceptoVenta.setText(model.getConceptoDeVenta());
        if (model.isEstadoDePago()){
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
        }else {
            holder.total.setText("$ " + String.valueOf(nformat.format(model.getTotalCalculado())));
            holder.total.setTextColor(holder.total.getContext().getResources().getColor(R.color.negro));
            holder.iconPunto.setColorFilter(holder.total.getContext().getResources().getColor(R.color.rosado));
            holder.iconFlecha.setColorFilter(holder.total.getContext().getResources().getColor(R.color.rosado));
        }

        holder.iconFlecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.iconFlecha.getContext(), DetallesFacturaGastos.class);
                String key = model.getId();
                String keyIV = model.getIdProveedores();
                intent.putExtra("key", key);
                intent.putExtra("keyIV", keyIV);
                v.getContext().startActivity(intent);
            }
        });


    }


    @NonNull
    @Override
    public AdaptadorListaFacturasEnGastos.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_facturas_gastos, parent, false);
        return new AdaptadorListaFacturasEnGastos.myViewHolder(view);
    }


    static class myViewHolder extends RecyclerView.ViewHolder{
        TextView conceptoVenta, total;
        ImageView iconPagado,iconFlecha,iconPunto;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            conceptoVenta = itemView.findViewById(R.id.nombreProducto);
            total = itemView.findViewById(R.id.total);
            iconPagado = itemView.findViewById(R.id.icon_estado_de_pago);
            iconFlecha = itemView.findViewById(R.id.ic_detalles_factura);
            iconPunto = itemView.findViewById(R.id.ic_restar_fehca);


        }

    }


}
