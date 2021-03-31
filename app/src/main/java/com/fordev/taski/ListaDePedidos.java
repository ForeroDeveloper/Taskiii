package com.fordev.taski;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaPedidos;
import com.fordev.taski.modelos.ModeloListaPedidos;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ListaDePedidos extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView listaDeProductos;
    AdaptadorListaPedidos adaptador_lista_pedidos;
    MaterialButton faq_add;
    TextView totalProductos, txtTotalStock;
    CardView sinContenidoInventario;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_de_pedidos);

        listaDeProductos = findViewById(R.id.lista_pedidos_recycler);
        faq_add = findViewById(R.id.faq_nuevo_pedido);
        totalProductos = findViewById(R.id.txtTotalPedidos);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        searchView = findViewById(R.id.search_view);
        sinContenidoInventario = findViewById(R.id.ilustracion);


        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Pedidos").child("pedido");

        databaseReference.keepSynced(true);

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogo = DialogPlus.newDialog(faq_add.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_pedido))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1480)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialog = dialogo.getHolderView();

                MaterialButton btn_guardar_pedido = dialog.findViewById(R.id.btn_guardar_pedido);
                TextInputLayout nombre_Cliente = dialog.findViewById(R.id.valorNombreCliente);
                TextInputLayout nombreProducto = dialog.findViewById(R.id.valorNombreProducto);
                TextInputLayout cantidad = dialog.findViewById(R.id.cantidad);
                TextInputLayout numero_cliente = dialog.findViewById(R.id.valorTotalVenta);
                TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

                btn_guardar_pedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (nombreProducto.getEditText().getText().toString().isEmpty()) {
                            nombreProducto.setError("Ingrese un nombre");
                        } else {
                            String nombreCliente = nombre_Cliente.getEditText().getText().toString().trim();
                            String numeroCliente = numero_cliente.getEditText().getText().toString().trim();
                            String pedido = nombreProducto.getEditText().getText().toString().trim();
                            int cantidadPedido = Integer.parseInt(cantidad.getEditText().getText().toString().trim());

                            ModeloListaPedidos modeloPedido = new ModeloListaPedidos();
                            modeloPedido.setNombreCliente(nombreCliente);
                            modeloPedido.setNumeroCliente(numeroCliente);
                            modeloPedido.setCantidadPedido(cantidadPedido);
                            modeloPedido.setNombrePedido(pedido);
                            databaseReference.child(modeloPedido.getNombrePedido()).setValue(modeloPedido);
                            databaseReference.keepSynced(true);
                            dialogo.dismiss();

                        }
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


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                procesobuscar(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                procesobuscar(s);
                return false;
            }
        });


        listaDeProductos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloListaPedidos> options =
                new FirebaseRecyclerOptions.Builder<ModeloListaPedidos>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Pedidos").child("pedido"), ModeloListaPedidos.class)
                        .build();


        adaptador_lista_pedidos = new AdaptadorListaPedidos(options);
        listaDeProductos.setAdapter(adaptador_lista_pedidos);
        adaptador_lista_pedidos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptador_lista_pedidos.getItemCount()));

            }
        });


    }

    private void procesobuscar(String s) {

        FirebaseRecyclerOptions<ModeloListaPedidos> options =
                new FirebaseRecyclerOptions.Builder<ModeloListaPedidos>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Pedidos").child("pedido")
                                .orderByChild("nombreCliente").startAt(s).endAt(s + "\uf8ff"), ModeloListaPedidos.class)
                        .build();

        adaptador_lista_pedidos = new AdaptadorListaPedidos(options);
        adaptador_lista_pedidos.startListening();
        listaDeProductos.setAdapter(adaptador_lista_pedidos);
        adaptador_lista_pedidos.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptador_lista_pedidos.getItemCount()));
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        adaptador_lista_pedidos.startListening();
        adaptador_lista_pedidos.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptador_lista_pedidos.stopListening();
    }

}