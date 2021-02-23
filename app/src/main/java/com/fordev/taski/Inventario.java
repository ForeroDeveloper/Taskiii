package com.fordev.taski;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.fordev.taski.modelos.ModeloInventario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Inventario extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventario);

        //Inicializar Base de Datos
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");
        //key
        String key = databaseReference.push().getKey();


    }

    public void agregarProducto(View view) {
        DialogPlus dialogPlus = DialogPlus.newDialog(getApplicationContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_agregar_inventario))
                .setExpanded(true,1460)
                .setContentBackgroundResource(android.R.color.transparent)
                .create();
        View dialog = dialogPlus.getHolderView();

        TextInputEditText nombre_Producto = dialog.findViewById(R.id.txtProducto);
        TextInputEditText precio_unitario = dialog.findViewById(R.id.txtprecio);
        TextInputEditText cantidad_stock = dialog.findViewById(R.id.txtcantidad);

        String nombreProducto = nombre_Producto.getText().toString();
        int precioUnitario = Integer.parseInt(precio_unitario.getText().toString());
        int cantidadStock = Integer.parseInt(cantidad_stock.getText().toString());

        ModeloInventario modeloInventario = new ModeloInventario();

        modeloInventario.setNombreProdcuto(nombreProducto);
        modeloInventario.setPrecioProducto(precioUnitario);



    }
    private String getFechaNormal(Long fechaMilisegundos) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechaMilisegundos);
        return fecha;
    }

    private Long getFechaMilisegundos() {
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();
        return  tiempounix;
    }
}