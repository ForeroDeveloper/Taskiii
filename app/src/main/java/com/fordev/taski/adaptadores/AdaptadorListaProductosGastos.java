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
import com.fordev.taski.modelos.ModeloGastos;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaProductosGastos extends FirebaseRecyclerAdapter<ModeloGastos, AdaptadorListaProductosGastos.myViewHolder>
{

    public AdaptadorListaProductosGastos(@NonNull FirebaseRecyclerOptions<ModeloGastos> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloGastos model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        holder.txtProducto.setText(model.getNombreProdcuto());
        holder.txtCantidad.setText(String.valueOf(model.getCantidadProducto()));
        if (model.getPrecioFinalPorElUsuario()!=0){
            holder.txtTotal.setText("$ " + String.valueOf(nformat.format(model.getPrecioFinalPorElUsuario())));
        }else{
            holder.txtTotal.setText(String.valueOf(model.getValorTotalCalculadoAutomatico()));
        }


    }




    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_productos_gastos, parent, false);
        return new myViewHolder(view);

    }


    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtProducto, txtCantidad, txtTotal,txtTotalProdcutos;
        ImageView imgDelete;
        RecyclerView recyclerView;
        MaterialButton btnLimpiar, btnGuardarFactura,btnGuardar;
        LinearLayout cabeceraFacturas;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto=(TextView)itemView.findViewById(R.id.txtNombreCliente);
            txtCantidad=(TextView)itemView.findViewById(R.id.txtCantidad);
            txtTotal=(TextView)itemView.findViewById(R.id.txtTotal);
            txtTotalProdcutos=(TextView)itemView.findViewById(R.id.crear_venta);
            recyclerView=(RecyclerView)itemView.findViewById(R.id.lista_de_productos_venta);
            btnLimpiar=(MaterialButton) itemView.findViewById(R.id.btnLimpiar);
            btnGuardar=(MaterialButton)itemView.findViewById(R.id.btnGuardar);
            cabeceraFacturas=(LinearLayout) itemView.findViewById(R.id.cabecera_factura);

        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
