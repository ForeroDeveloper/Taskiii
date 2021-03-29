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
import com.fordev.taski.adaptadores.AdaptadorListaClientes;
import com.fordev.taski.modelos.ModeloClientes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ListaClientes extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView listaDeProductos;
    AdaptadorListaClientes adaptadorListaClientes;
    MaterialButton faq_add;
    TextView totalProductos, txtTotalStock;
    CardView sinContenidoInventario;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_clientes);

        listaDeProductos = findViewById(R.id.lista_clientes);
        faq_add = findViewById(R.id.faq_nuevo_pedido);
        totalProductos = findViewById(R.id.txtTotalPedidos);
        txtTotalStock = findViewById(R.id.txtTotalStock);
        searchView = findViewById(R.id.search_view);
        sinContenidoInventario = findViewById(R.id.ilustracion);


        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Clientes").child("cliente");

        databaseReference.keepSynced(true);

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogo = DialogPlus.newDialog(faq_add.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_cliente))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1100)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialog = dialogo.getHolderView();

                MaterialButton btn_guardar_cliente = dialog.findViewById(R.id.btn_guardar_pedido);
                TextInputLayout nombre_Cliente = dialog.findViewById(R.id.nombreClientes);
                TextInputLayout numero_cliente = dialog.findViewById(R.id.valorTotalVenta);
                TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

                btn_guardar_cliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (nombre_Cliente.getEditText().getText().toString().isEmpty()) {
                            nombre_Cliente.setError("Ingrese un nombre");
                        } else {
                            String nombreCliente = nombre_Cliente.getEditText().getText().toString().trim();
                            String numeroCliente = numero_cliente.getEditText().getText().toString().trim();

                            ModeloClientes modeloCliente = new ModeloClientes();
                            modeloCliente.setNombreCliente(nombreCliente);
                            modeloCliente.setNumeroTelefono(numeroCliente);

                            databaseReference.child(modeloCliente.getNombreCliente()).setValue(modeloCliente);
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
        FirebaseRecyclerOptions<ModeloClientes> options =
                new FirebaseRecyclerOptions.Builder<ModeloClientes>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Clientes").child("cliente"), ModeloClientes.class)
                        .build();


        adaptadorListaClientes = new AdaptadorListaClientes(options);
        listaDeProductos.setAdapter(adaptadorListaClientes);
        adaptadorListaClientes.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaClientes.getItemCount()));

            }
        });


    }

    private void procesobuscar(String s) {

        FirebaseRecyclerOptions<ModeloClientes> options =
                new FirebaseRecyclerOptions.Builder<ModeloClientes>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Clientes").child("cliente")
                                .orderByChild("nombreCliente").startAt(s).endAt(s + "\uf8ff"), ModeloClientes.class)
                        .build();

        adaptadorListaClientes = new AdaptadorListaClientes(options);
        adaptadorListaClientes.startListening();
        listaDeProductos.setAdapter(adaptadorListaClientes);
        adaptadorListaClientes.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaClientes.getItemCount()));
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        adaptadorListaClientes.startListening();
        adaptadorListaClientes.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaClientes.stopListening();
    }

}