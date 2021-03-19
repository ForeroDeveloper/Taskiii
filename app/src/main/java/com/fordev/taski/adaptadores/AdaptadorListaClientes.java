package com.fordev.taski.adaptadores;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.R;
import com.fordev.taski.modelos.ModeloClientes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AdaptadorListaClientes extends FirebaseRecyclerAdapter<ModeloClientes, AdaptadorListaClientes.myViewHolder>
{


    public AdaptadorListaClientes(@NonNull FirebaseRecyclerOptions<ModeloClientes> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ModeloClientes model) {
        holder.txtNombreCliente.setText(model.getNombreCliente());
        holder.txtNumeroCliente.setText(model.getNumeroTelefono());


        holder.ic_editar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogo = DialogPlus.newDialog(holder.ic_editar_cliente.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_cliente))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1100)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialog = dialogo.getHolderView();

                MaterialButton btn_guardar_cliente = dialog.findViewById(R.id.btn_guardar_proveedor);
                TextInputLayout nombre_Cliente = dialog.findViewById(R.id.nombreClientes);
                TextInputLayout numero_cliente = dialog.findViewById(R.id.valorTotalVenta);
                TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);


                //seteo por default
                nombre_Cliente.getEditText().setText(model.getNombreCliente());
                numero_cliente.getEditText().setText(model.getNumeroTelefono());
                btn_guardar_cliente.setText("Editar");


                btn_guardar_cliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nombreUpdate = nombre_Cliente.getEditText().getText().toString().trim();
                        String numeroUpdate = numero_cliente.getEditText().getText().toString().trim();
                        //GUARDAR Y ACTUALIZAR DATOS
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombreCliente",nombreUpdate);
                        map.put("numeroTelefono",numeroUpdate);

                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Clientes").child("cliente").child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.success(holder.ic_editar_cliente.getContext(), "Editado Correctamente!", Toast.LENGTH_SHORT, true).show();
                                    }
                                });

                        dialogo.dismiss();
                    }
                });

                btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogo.dismiss();;
                    }
                });

                dialogo.show();

            }
        });

        holder.ic_llamar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + model.getNumeroTelefono().toString()));
                v.getContext().startActivity(intent);
            }
        });
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_card_lista_clientes, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder{

        TextView txtNombreCliente, txtNumeroCliente;
        ImageView ic_editar_cliente, ic_llamar_cliente;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreCliente=(TextView)itemView.findViewById(R.id.nombreClientes);
            txtNumeroCliente=(TextView)itemView.findViewById(R.id.numeroClientes);
            ic_editar_cliente=(ImageView) itemView.findViewById(R.id.ic_editar_cliente);
            ic_llamar_cliente=(ImageView) itemView.findViewById(R.id.ic_llamar);

        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


}
