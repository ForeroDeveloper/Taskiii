package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fordev.taski.modelos.ModeloComprasNegocio;
import com.fordev.taski.modelos.ModeloInventario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RegistrarCompra extends AppCompatActivity {

    DatabaseReference databaseReference, databaseReference1;
    FirebaseDatabase firebaseDatabase;
    MaterialButton btn_guardar_compra, scan;
    TextInputLayout edtNombreProveedor, edtNombreProducto, edtCodigoDeBarras, edtCantidad, edtValorCompra, edtUtilidad, edtValorVenta, edtCostoTotal;
    TextInputEditText txtUtilidad, txt_valor_compra, txt_cantidad;
    String key;
    RadioButton radioButton;
    Boolean estadoDePago = true;
    DecimalFormat format = new DecimalFormat("0.#");
    String codigoBarras;
    boolean entrar = false;
    String cantidad2;
    String precio;
    String ids;
    double totales;
    int sum = 0;
    SimpleDateFormat sdfMes = new SimpleDateFormat("MM");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agregar_compra);
        btn_guardar_compra = findViewById(R.id.btn_guardar_compra);
        edtNombreProveedor = findViewById(R.id.edtNombreProveedor);
        edtNombreProducto = findViewById(R.id.edtNombreProducto);
        edtCodigoDeBarras = findViewById(R.id.edtCodigoDeBarras);
        edtCantidad = findViewById(R.id.edtCantidad);
        edtValorCompra = findViewById(R.id.edtValorCompra);
        edtUtilidad = findViewById(R.id.edtUtilidad);
        edtValorVenta = findViewById(R.id.edtValorVenta);
        edtCostoTotal = findViewById(R.id.edtCostoTotal);
        txtUtilidad = findViewById(R.id.txtUtilidad);
        txt_valor_compra = findViewById(R.id.txt_valor_compra);
        txt_cantidad = findViewById(R.id.txt_cantidad);
        radioButton = findViewById(R.id.radio0);
        scan = findViewById(R.id.scan);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Compras").child("lista");
        databaseReference1 = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Inventario").child("productos");


        databaseReference.keepSynced(true);

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    estadoDePago = false;
                } else {
                    estadoDePago = true;
                }
            }
        });

        txt_cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtValorCompra.getEditText().getText().toString().isEmpty()) {
                    if (!edtCantidad.getEditText().getText().toString().isEmpty()) {
                        edtValorCompra.setErrorEnabled(false);
                        double valor = Double.parseDouble(edtValorCompra.getEditText().getText().toString());
                        double cantidad = Double.parseDouble(edtCantidad.getEditText().getText().toString());
                        double total = cantidad * valor;
                        edtCostoTotal.getEditText().setText(String.valueOf(format.format(total)));

                    } else {
                        edtValorCompra.setError("Ingrese un valor para calcular el costo total");
                    }

                } else {
                    edtValorCompra.setError("Ingrese un valor de compra");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        txt_valor_compra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtUtilidad.getEditText().setText("");
                edtValorVenta.getEditText().setText("");
                if (!edtCantidad.getEditText().getText().toString().isEmpty()) {
                    if (!edtValorCompra.getEditText().getText().toString().isEmpty()) {
                        edtCantidad.setErrorEnabled(false);
                        edtValorCompra.setErrorEnabled(false);
                        double valor = Double.parseDouble(edtValorCompra.getEditText().getText().toString());
                        double cantidad = Double.parseDouble(edtCantidad.getEditText().getText().toString());
                        double total = cantidad * valor;
                        edtCostoTotal.getEditText().setText(String.valueOf(format.format(total)));

                    } else {
                        edtCostoTotal.getEditText().setText("");
                        edtValorCompra.setError("Ingrese un valor para calcular el costo total");
                    }

                } else {
                    edtCantidad.setError("Ingrese la cantidad");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtUtilidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtUtilidad.getEditText().getText().toString().isEmpty()) {
                    if (!edtValorCompra.getEditText().getText().toString().isEmpty()) {
                        edtValorCompra.setErrorEnabled(false);
                        double valorCompra = Double.parseDouble(edtValorCompra.getEditText().getText().toString());
                        double utilidadIngresada = Double.parseDouble(edtUtilidad.getEditText().getText().toString());
                        double total = (utilidadIngresada * valorCompra) / 100;
                        edtValorVenta.getEditText().setText(String.valueOf(format.format(total + valorCompra)));

                    } else {
                        edtValorCompra.setError("Ingrese un valor para calcular el precio de venta");
                    }

                } else {
                    edtUtilidad.getEditText().setHint("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(RegistrarCompra.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Sube el Volumen + , y activa el Flash");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });

        btn_guardar_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = databaseReference.push().getKey();

                if (edtCodigoDeBarras.getEditText().getText().toString().isEmpty()) {
                    int random = (int) (Math.random() * (3000 - 1000));
                    codigoBarras = String.valueOf(random);
                } else {
                    codigoBarras = edtCodigoDeBarras.getEditText().getText().toString();
                }

                if (edtNombreProducto.getEditText().getText().toString().isEmpty() || edtValorCompra.getEditText().getText().toString().isEmpty() ||
                        edtCantidad.getEditText().getText().toString().isEmpty() || edtUtilidad.getEditText().getText().toString().isEmpty()) {
                    FancyToast.makeText(RegistrarCompra.this, "Ingresa los datos requeridos!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

                } else {

                    String proveedor = edtNombreProveedor.getEditText().getText().toString().trim();
                    String producto = edtNombreProducto.getEditText().getText().toString().trim();
                    double cantidad = Double.parseDouble(edtCantidad.getEditText().getText().toString().trim());
                    double vCompra = Double.parseDouble(edtValorCompra.getEditText().getText().toString().trim());
                    double utilidad = Double.parseDouble(edtUtilidad.getEditText().getText().toString().trim());
                    double vVenta = Double.parseDouble(edtValorVenta.getEditText().getText().toString().trim());
                    double costoTotal = Double.parseDouble(edtCostoTotal.getEditText().getText().toString().trim());

                    ModeloComprasNegocio modeloComprasNegocio = new ModeloComprasNegocio();
                    modeloComprasNegocio.setNombreProveedor(proveedor);
                    modeloComprasNegocio.setNombreProducto(producto);
                    modeloComprasNegocio.setCodigoDeBarras(codigoBarras);
                    modeloComprasNegocio.setCantidadProducto(cantidad);
                    modeloComprasNegocio.setPrecioCosto(vCompra);
                    modeloComprasNegocio.setUtilidad(utilidad);
                    modeloComprasNegocio.setPrecioVenta(vVenta);
                    modeloComprasNegocio.setCostoTotal(costoTotal);
                    modeloComprasNegocio.setPagado(estadoDePago);
                    modeloComprasNegocio.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                    modeloComprasNegocio.setId(key);
                    modeloComprasNegocio.setMonth(String.valueOf(sdfMes.format(Calendar.getInstance().getTime())));
                    databaseReference.child(key).setValue(modeloComprasNegocio);
                    Toast.makeText(RegistrarCompra.this, "Registrada Correctamente", Toast.LENGTH_SHORT).show();


                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                int i = (int) snapshot.getChildrenCount();
                                String code = ds.child("codigoDeBarras").getValue().toString();
                                cantidad2 = ds.child("cantidadProducto").getValue().toString();
                                ids = ds.child("id").getValue().toString();

                                if (sum <= i){
                                    sum++;
                                    if (code.equals(codigoBarras)){
                                        Toast.makeText(RegistrarCompra.this, "enocntrado " + code, Toast.LENGTH_SHORT).show();
                                        cantidad2 = ds.child("cantidadProducto").getValue().toString();
                                        ids = ds.child("id").getValue().toString();

                                        double cantidadObtenida = Double.parseDouble(String.valueOf(cantidad2));
                                        double precioNuevo = modeloComprasNegocio.getPrecioVenta();
                                        totales = cantidadObtenida + modeloComprasNegocio.getCantidadProducto();
                                        //MAP
                                        Map<String, Object> map1 = new HashMap<>();
                                        map1.put("cantidadProducto", totales);
                                        map1.put("precioProducto", precioNuevo);
                                        firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos")
                                                .child(ids).updateChildren(map1);
                                        break;
                                    }else {
                                        if (sum==i){
                                            String key2 = databaseReference.push().getKey();
                                            ModeloInventario modeloInventario = new ModeloInventario();
                                            modeloInventario.setNombreProdcuto(modeloComprasNegocio.getNombreProducto());
                                            modeloInventario.setPrecioProducto(modeloComprasNegocio.getPrecioVenta());
                                            modeloInventario.setCantidadProducto(modeloComprasNegocio.getCantidadProducto());
                                            modeloInventario.setFechaRegistro(getFechaNormal(getFechaMilisegundos()));
                                            modeloInventario.setTimeStamp(getFechaMilisegundos() * -1);
                                            modeloInventario.setId(key2);
                                            modeloInventario.setCodigoDeBarras(modeloComprasNegocio.getCodigoDeBarras());
                                            databaseReference1.child(key2).setValue(modeloInventario);
                                        }else {

                                        Toast.makeText(RegistrarCompra.this, "no hay reustlado", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                            }

                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    

                    /*databaseReference1.orderByChild("codigoDeBarras").equalTo(modeloComprasNegocio.getCodigoDeBarras());

                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getChildren();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                cantidad2 = ds.child("cantidadProducto").getValue().toString();
                                ids = ds.child("id").getValue().toString();
                                Toast.makeText(RegistrarCompra.this, cantidad2, Toast.LENGTH_SHORT).show();
                            }

                            double cantidadObtenida = Double.parseDouble(String.valueOf(cantidad2));
                            double precioNuevo = modeloComprasNegocio.getPrecioVenta();
                            totales = cantidadObtenida + modeloComprasNegocio.getCantidadProducto();
                            //MAP
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("cantidadProducto", totales);
                            map1.put("precioProducto", precioNuevo);
                            firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Inventario").child("productos")
                                    .child(ids).updateChildren(map1);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(RegistrarCompra.this, "error", Toast.LENGTH_SHORT).show();

                        }
                    });
*/
                }

            }
        });

    }

    private String getFechaNormal(Long fechaMilisegundos) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechaMilisegundos);
        return fecha;
    }

    private Long getFechaMilisegundos() {
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();
        return tiempounix;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            edtCodigoDeBarras.getEditText().setText(result.getContents());
            FancyToast.makeText(RegistrarCompra.this, "Escaneado Correctamente!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, R.drawable.logo_taski, false).show();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}