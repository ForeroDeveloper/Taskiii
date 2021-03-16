package com.fordev.taski.adaptadores;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloVentaInventario;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdaptadorListaProductosEnInventario extends FirebaseRecyclerAdapter<ModeloVentaInventario, AdaptadorListaProductosEnInventario.myViewHolder>
{

    public AdaptadorListaProductosEnInventario(@NonNull FirebaseRecyclerOptions<ModeloVentaInventario> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloVentaInventario model) {
        NumberFormat nformat = new DecimalFormat("##,###,###.##");
        DecimalFormat format = new DecimalFormat("0.#");
        holder.txtProducto.setText(model.getNombreProdcuto());
        holder.txtCantidad.setText(String.valueOf(format.format(model.getCantidadProducto())));
        if (model.getPrecioFinalPorElUsuario()!=0){
            holder.txtTotal.setText("$ " + String.valueOf(nformat.format(model.getPrecioFinalPorElUsuario())));
        }else{
            holder.txtTotal.setText("$ " + String.valueOf(nformat.format(model.getValorTotalCalculadoAutomatico())));
        }


    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_productos_factura_inventario, parent, false);
        return new myViewHolder(view);

    }

    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtProducto, txtCantidad, txtTotal;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto=(TextView)itemView.findViewById(R.id.txtNombreCliente);
            txtCantidad=(TextView)itemView.findViewById(R.id.txtCantidad);
            txtTotal=(TextView)itemView.findViewById(R.id.txtTotal);
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
