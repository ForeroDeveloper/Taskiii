package com.fordev.taski.adaptadores;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloVenta;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaProductosRecibo extends FirebaseRecyclerAdapter<ModeloVenta, AdaptadorListaProductosRecibo.myViewHolder>
{

    public AdaptadorListaProductosRecibo(@NonNull FirebaseRecyclerOptions<ModeloVenta> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloVenta model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        DecimalFormat format = new DecimalFormat("0.#");
        holder.txtProductoRecibo.setText(model.getNombreProdcuto());
        holder.txtCantidadRecibo.setText(String.valueOf(format.format(model.getCantidadProducto())));
        holder.txtPrecioRecibo.setText("$" + String.valueOf(nformat.format(model.getPrecioProducto())));
        if (model.getPrecioFinalPorElUsuario()!=0){
            holder.txtTotalRecibo.setText("$" + String.valueOf(nformat.format(model.getPrecioFinalPorElUsuario())));
        }else{
            holder.txtTotalRecibo.setText("$" + String.valueOf(nformat.format(model.getValorTotalCalculadoAutomatico())));
        }


    }




    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_productos_recibo, parent, false);
        return new myViewHolder(view);

    }


    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtProductoRecibo, txtCantidadRecibo, txtTotalRecibo,txtPrecioRecibo;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductoRecibo=(TextView)itemView.findViewById(R.id.txtProductoRecibo);
            txtCantidadRecibo=(TextView)itemView.findViewById(R.id.txtCantidadRecibo);
            txtTotalRecibo=(TextView)itemView.findViewById(R.id.txtTotalRecibo);
            txtPrecioRecibo=(TextView)itemView.findViewById(R.id.txtPrecioRecibo);

        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
