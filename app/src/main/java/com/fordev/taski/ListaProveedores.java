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
import com.fordev.taski.adaptadores.AdaptadorListaProveedores;
import com.fordev.taski.modelos.ModeloProveedores;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ListaProveedores extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView listaDeProductos;
    AdaptadorListaProveedores adaptadorListaProveedores;
    MaterialButton faq_add;
    TextView totalProductos, txtTotalStock;
    CardView sinContenidoInventario;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_proveedores);

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
                .child("Proveedores").child("proveedor");

        databaseReference.keepSynced(true);

        faq_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogo = DialogPlus.newDialog(faq_add.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_agregar_proveedor))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1450)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialog = dialogo.getHolderView();

                MaterialButton btn_guardar_cliente = dialog.findViewById(R.id.btn_guardar_pedido);
                TextInputLayout nombre_proveedor = dialog.findViewById(R.id.nombreProveedor);
                TextInputLayout numero_proveedor = dialog.findViewById(R.id.numeroProveedor);
                TextInputLayout desc_proveedor = dialog.findViewById(R.id.descProveedor);
                TextView btn_cancelar = dialog.findViewById(R.id.btn_cancelar);

                btn_guardar_cliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (nombre_proveedor.getEditText().getText().toString().isEmpty()) {
                            nombre_proveedor.setError("Ingrese un nombre");
                        } else {
                            String nombreCliente = nombre_proveedor.getEditText().getText().toString().trim();
                            String numeroCliente = numero_proveedor.getEditText().getText().toString().trim();
                            String descProveedor = desc_proveedor.getEditText().getText().toString().trim();

                            ModeloProveedores modeloProveedor = new ModeloProveedores();
                            modeloProveedor.setNombreProveedor(nombreCliente);
                            modeloProveedor.setNumeroTelefono(numeroCliente);
                            modeloProveedor.setDescProveedor(descProveedor);

                            databaseReference.child(modeloProveedor.getNombreProveedor()).setValue(modeloProveedor);
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
        FirebaseRecyclerOptions<ModeloProveedores> options =
                new FirebaseRecyclerOptions.Builder<ModeloProveedores>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Proveedores").child("proveedor"), ModeloProveedores.class)
                        .build();


        adaptadorListaProveedores = new AdaptadorListaProveedores(options);
        listaDeProductos.setAdapter(adaptadorListaProveedores);
        adaptadorListaProveedores.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaProveedores.getItemCount()));

            }
        });


    }

    private void procesobuscar(String s) {

        FirebaseRecyclerOptions<ModeloProveedores> options =
                new FirebaseRecyclerOptions.Builder<ModeloProveedores>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Proveedores").child("proveedor")
                                .orderByChild("nombreProveedor").startAt(s).endAt(s + "\uf8ff"), ModeloProveedores.class)
                        .build();

        adaptadorListaProveedores = new AdaptadorListaProveedores(options);
        adaptadorListaProveedores.startListening();
        listaDeProductos.setAdapter(adaptadorListaProveedores);
        adaptadorListaProveedores.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                totalProductos.setText(String.valueOf(adaptadorListaProveedores.getItemCount()));
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        adaptadorListaProveedores.startListening();
        adaptadorListaProveedores.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProveedores.stopListening();
    }

}