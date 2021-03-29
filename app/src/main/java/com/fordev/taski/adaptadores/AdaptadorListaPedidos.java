package com.fordev.taski.adaptadores;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
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
import com.fordev.taski.modelos.ModeloListaPedidos;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class AdaptadorListaPedidos extends FirebaseRecyclerAdapter<ModeloListaPedidos, AdaptadorListaPedidos.myViewHolder> {

    public AdaptadorListaPedidos(@NonNull FirebaseRecyclerOptions<ModeloListaPedidos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorListaPedidos.myViewHolder holder, int position, @NonNull ModeloListaPedidos model) {
        holder.nombreClienteDePedido.setText(model.getNombreCliente());
        holder.nombreProductoPedido.setText(model.getNombrePedido());
        holder.iconLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + model.getNumeroCliente().toString()));
                v.getContext().startActivity(intent);
            }
        });
        holder.cantidadPedido.setText("Cantidad: " + String.valueOf(model.getCantidadPedido()));

        holder.iconConfirmado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DialogPlus dialog = DialogPlus.newDialog(holder.cantidadPedido.getContext())
                            .setContentHolder(new ViewHolder(R.layout.dialog_confirmar_pedido))
                            .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                            .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setGravity(Gravity.CENTER)
                            .setContentBackgroundResource(android.R.color.transparent)
                            .create();

                    View view1 = dialog.getHolderView();

                    TextView btn_dismis = view1.findViewById(R.id.btn_cancel);
                    MaterialButton btn_delete = view1.findViewById(R.id.btn_delete);

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Pedidos").child("pedido").child(getRef(position).getKey()).removeValue();
                            notifyDataSetChanged();
                            notifyItemRemoved(position);
                            dialog.dismiss();
                        }
                    });

                    btn_dismis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } catch (IndexOutOfBoundsException e) { // if fails, increment the shift and try again
                    notifyDataSetChanged();
                    notifyItemRemoved(position);
                }
            }
        });

    }


    @NonNull
    @Override
    public AdaptadorListaPedidos.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_pedidos, parent, false);
        return new AdaptadorListaPedidos.myViewHolder(view);
    }


    static class myViewHolder extends RecyclerView.ViewHolder {
        TextView nombreProductoPedido, nombreClienteDePedido, cantidadPedido;
        ImageView iconConfirmado,iconLlamar;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProductoPedido = itemView.findViewById(R.id.nombreProductoPedido);
            nombreClienteDePedido = itemView.findViewById(R.id.nombreClienteDePedido);
            cantidadPedido = itemView.findViewById(R.id.cantidadPedido);
            iconConfirmado = itemView.findViewById(R.id.ic_confirmar_llegada);
            iconLlamar = itemView.findViewById(R.id.ic_llamar);

        }

    }


}
