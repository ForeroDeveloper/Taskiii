package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fordev.taski.adaptadores.AdaptadorListaProductos;
import com.fordev.taski.adaptadores.AdaptadorListaProductosEnInventario;
import com.fordev.taski.adaptadores.AdaptadorListaProductosInventarioRecibo;
import com.fordev.taski.adaptadores.AdaptadorListaProductosRecibo;
import com.fordev.taski.modelos.ModeloVenta;
import com.fordev.taski.modelos.ModeloVentaInventario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class CreacionDeRecibo extends AppCompatActivity {

    private static File dir;
    //Firebase Instancias
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference imagenLogo = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagen");
    //info
    private final DatabaseReference nombreNegocio = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");
    RecyclerView lista_de_productos_venta_recibo,lista_de_productos_venta_recibo_inventario;
    AdaptadorListaProductosRecibo adaptadorListaProductosRecibo;
    AdaptadorListaProductosInventarioRecibo adaptadorListaProductosInventarioRecibo;
    ImageView img_logo_negocio_recibo;
    TextView txtNombreNegocioRecibo,txtNitNegocioRecibo,txt_fecha_factura_cliente,txtNombreVendedorRecibo,txt_nombre_cliente,txt_numero_factura_cliente,txtUbicacionNegocioRecibo
            ,txtDireccionNegocioRecibo,txtTelefonoNegocio,txt_total_factura_cliente,txtProductoRecibo,txtPrecioRecibo,txtTotalRecibo;
    MaterialButton enviarRecibo;
    NestedScrollView contenidoLayout;
    RelativeLayout marcaDeAgua;
    LinearLayout venta_rapida_info;

    String key = null;
    String key2 = null;
    String urlImagen;
    long idFac = 0;
    //Variables globales de datos de la Factura
    String nombre_cliente;
    int total_factura;
    String fecha_factura;


    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creacion_de_recibo);
        lista_de_productos_venta_recibo = findViewById(R.id.lista_de_productos_venta_recibo);
        lista_de_productos_venta_recibo_inventario = findViewById(R.id.lista_de_productos_venta_recibo_inventario);
        venta_rapida_info = findViewById(R.id.venta_rapida_info);
        txtProductoRecibo = findViewById(R.id.txtProductoRecibo);
        txtPrecioRecibo = findViewById(R.id.txtPrecioRecibo);
        txtTotalRecibo = findViewById(R.id.txtTotalRecibo);
        //Logo Negocio
        img_logo_negocio_recibo = findViewById(R.id.img_logo_negocio_recibo);
        //Nombre Negocio
        txtNombreNegocioRecibo = findViewById(R.id.txtNombreNegocioRecibo);
        //Nit Negocio
        txtNitNegocioRecibo = findViewById(R.id.txtNitNegocioRecibo);
        //FEcha Factura recibo
        txt_fecha_factura_cliente = findViewById(R.id.txt_fecha_factura_cliente);
        //Nombre vendedor
        txtNombreVendedorRecibo = findViewById(R.id.txtNombreVendedorRecibo);
        //Nombre Cliente
        txt_nombre_cliente = findViewById(R.id.txt_nombre_cliente);
        //Numero de Factura
        txt_numero_factura_cliente = findViewById(R.id.txt_numero_factura_cliente);
        //Ubicacion del negocio
        txtUbicacionNegocioRecibo = findViewById(R.id.txtUbicacionNegocioRecibo);
        //Direccion
        txtDireccionNegocioRecibo = findViewById(R.id.txtDireccionNegocioRecibo);
        //Numero de negocio TEL.
        txtTelefonoNegocio = findViewById(R.id.txtTelefonoNegocio);
        //Total de la factura
        txt_total_factura_cliente = findViewById(R.id.txt_total_factura_cliente);
        //Enviar Recibo o Imprimir
        enviarRecibo = findViewById(R.id.btn_print);
        //Screen del Layout
        contenidoLayout = findViewById(R.id.layout);
        //Marca de agua visibilidad
        marcaDeAgua = findViewById(R.id.marcaDeAgua);

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");
        key2 = bundle.getString("key2");

        final DatabaseReference datosFactura = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("facturasCreadas").child(key);
        datosFactura.keepSynced(true);
        imagenLogo.keepSynced(true);

        final DatabaseReference totalFacturasCreadas = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("facturasCreadas");
        totalFacturasCreadas.keepSynced(true);

        //Traer el numero de FActuras
        totalFacturasCreadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    idFac = (snapshot.getChildrenCount());
                }
                txt_numero_factura_cliente.setText("FACTURA N°: 0" + idFac++);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Traer infomacion basica de la factura DETALLES
        datosFactura.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    fecha_factura = snapshot.child("fechaRegistro").getValue().toString();
                    txt_fecha_factura_cliente.setText("Fecha: "+ fecha_factura);
                    //Nombre Cliente
                    nombre_cliente = snapshot.child("cliente").getValue().toString();
                    if (nombre_cliente.toString().isEmpty()){
                        txt_nombre_cliente.setText("CLIENTE: " + "Sin Especificar...");
                    }else{
                        txt_nombre_cliente.setText("CLIENTE: "+ nombre_cliente);
                    }

                    total_factura = Integer.parseInt(String.valueOf(snapshot.child("abonado").getValue().toString()));
                    txt_total_factura_cliente.setText("$ " + String.valueOf(nformat.format(total_factura)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Seteo de logo desde FIREBASE USER
        imagenLogo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("imagen").exists()){
                    urlImagen = snapshot.child("imagen").getValue().toString();
                    Picasso.get().load(urlImagen).into(img_logo_negocio_recibo);
                }else {
                    img_logo_negocio_recibo.setImageDrawable(getResources().getDrawable(R.drawable.logo_taski));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Seteo de nombre negocio , nit , nom Vendedor
        nombreNegocio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Nombre negocio
                String nombre_negocio = snapshot.child("nombreNegocio").getValue().toString();
                txtNombreNegocioRecibo.setText(nombre_negocio);
                txtNombreNegocioRecibo.setVisibility(View.VISIBLE);
                //Nit
                String nit_negocio = snapshot.child("nitNegocio").getValue().toString();
                if (!nit_negocio.toString().isEmpty()){
                    txtNitNegocioRecibo.setText(nit_negocio);
                    txtNitNegocioRecibo.setVisibility(View.VISIBLE);
                }
                //Nombre Vendedor
                String nom_vendedor = snapshot.child("nombrePropietario").getValue().toString();
                txtNombreVendedorRecibo.setText("Vende: " + nom_vendedor);
                //ubicacion Negocio
                String ubicacion_negocio = snapshot.child("ubicacionNegocio").getValue().toString();
                if (!ubicacion_negocio.toString().isEmpty()){
                    txtUbicacionNegocioRecibo.setText(ubicacion_negocio);
                    txtUbicacionNegocioRecibo.setVisibility(View.VISIBLE);
                }
                //direccion Negocio
                String direccion_negocio = snapshot.child("direccionNegocio").getValue().toString();
                if (!direccion_negocio.toString().isEmpty()){
                    txtDireccionNegocioRecibo.setText(direccion_negocio);
                    txtDireccionNegocioRecibo.setVisibility(View.VISIBLE);
                }
                //telefono Negocio
                String telefono_negocio = snapshot.child("telefonoNegocio").getValue().toString();
                if (!telefono_negocio.toString().isEmpty()){
                    txtTelefonoNegocio.setText("Cel. " + telefono_negocio);
                    txtTelefonoNegocio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        enviarRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(CreacionDeRecibo.this,"Cargando...",Toast.LENGTH_LONG, true).show();
                enviarRecibo.setVisibility(View.GONE);
                getBitmapFromView(contenidoLayout);
                enviarRecibo.setVisibility(View.VISIBLE);
                marcaDeAgua.setVisibility(View.INVISIBLE);
            }
        });


        //FIREBASE
        lista_de_productos_venta_recibo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVenta> options =
                new FirebaseRecyclerOptions.Builder<ModeloVenta>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturas").child(key), ModeloVenta.class)
                        .build();
        adaptadorListaProductosRecibo=new AdaptadorListaProductosRecibo(options);
        lista_de_productos_venta_recibo.setAdapter(adaptadorListaProductosRecibo);
        adaptadorListaProductosRecibo.notifyDataSetChanged();


        //FIREBASE
        lista_de_productos_venta_recibo_inventario.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        FirebaseRecyclerOptions<ModeloVentaInventario> options2 =
                new FirebaseRecyclerOptions.Builder<ModeloVentaInventario>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("facturas").child("fechas").child("listaDeFacturasIV").child(key2), ModeloVentaInventario.class)
                        .build();
        adaptadorListaProductosInventarioRecibo=new AdaptadorListaProductosInventarioRecibo(options2);
        lista_de_productos_venta_recibo_inventario.setAdapter(adaptadorListaProductosInventarioRecibo);
        adaptadorListaProductosInventarioRecibo.notifyDataSetChanged();

        //VISIBILIDAD venta RAPIDA
            datosFactura.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("ventaRapida").exists()){
                        venta_rapida_info.setVisibility(View.VISIBLE);
                        String conceptoVenta = snapshot.child("conceptoDeVenta").getValue().toString();
                        txtProductoRecibo.setText(conceptoVenta);
                        String ventaTotal = snapshot.child("totalCalculado").getValue().toString();
                        int venta = Integer.parseInt(ventaTotal);
                        txtPrecioRecibo.setText("$"+String.valueOf(nformat.format(venta)));
                        txtTotalRecibo.setText("$"+ String.valueOf(nformat.format(venta)));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    private Bitmap getBitmapFromView(View view){
        Bitmap returnBitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        marcaDeAgua.setVisibility(View.VISIBLE);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgdrawable = view.getBackground();
        if (bgdrawable!=null){
            bgdrawable.draw(canvas);
        }else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);

        Date currentTime;
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),returnBitmap,"RECIBO_TASKI_VENTA-" + (currentTime = Calendar.getInstance().getTime()),  null);

        Uri uri = Uri.parse(bitmapPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Hola " + (nombre_cliente + "," + System.getProperty ("line.separator") + System.getProperty ("line.separator") )  + ("Aquí te dejamos una copia de tu recibo por un valor de " + "*$"+ String.valueOf(nformat.format(total_factura)) + "*"+ ". Compra que realizaste el " + fecha_factura +"." )
                        + System.getProperty("line.separator") + System.getProperty("line.separator") +  "Si tienes alguna duda respecto a tu compra no dudes en contactarnos!" + (System.getProperty("line.separator") + System.getProperty("line.separator")) + "Este mensaje fue enviado desde la aplicación Taski. ");
        startActivity(Intent.createChooser(intent, "Compartir Recibo..."));

        return  returnBitmap;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adaptadorListaProductosRecibo.stopListening();
        adaptadorListaProductosInventarioRecibo.stopListening();
    }
    @Override
    protected void onStart() {
        super.onStart();
        adaptadorListaProductosRecibo.startListening();
        adaptadorListaProductosInventarioRecibo.startListening();
        adaptadorListaProductosRecibo.notifyDataSetChanged();
        adaptadorListaProductosInventarioRecibo.notifyDataSetChanged();
    }
}