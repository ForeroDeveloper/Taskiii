package com.fordev.taski;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class InformacionPersonal extends AppCompatActivity {

    TextInputEditText txtNombrePro, txtApellidoPro, txtNumeroDocumento, txtCorreoElectronico;
    DatabaseReference databaseReference, databaseReferencePorcentaje;
    FirebaseDatabase firebaseDatabase;
    AutoCompleteTextView autoCompleteTextView;
    MaterialButton btnGuardarInfo;
    String nombreUpdate, apellidoUpdate, tipoDocUpdate, numeroDocUpdate, correoUpdate;
    ImageView atras;
    int infoPersonal = 0;
    int nom = 20;
    int ape = 20;
    int tipoDoc = 20;
    int numDoc = 20;
    int correo = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_personal);
        //Intancias
        autoCompleteTextView = findViewById(R.id.autoCompleteText);
        txtNombrePro = findViewById(R.id.txtNombreNegocio);
        txtApellidoPro = findViewById(R.id.txtNitNegocio);
        txtNumeroDocumento = findViewById(R.id.txtDireccionNegocio);
        txtCorreoElectronico = findViewById(R.id.txtTelefonoNegocio);
        btnGuardarInfo = findViewById(R.id.btnGuardar);
        atras = findViewById(R.id.atras);

        //Seteos DEfaul Base de Datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("info");

        databaseReferencePorcentaje = firebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PorcentajeInformacion");
        databaseReference.keepSynced(true);
        databaseReferencePorcentaje.keepSynced(true);
        autoCompletDocumento();

        databaseReferencePorcentaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object values = snapshot.child("infoPersonal").getValue().toString();
                String values2 = String.valueOf(values);
                int values3 = Integer.parseInt(values2);
                infoPersonal = values3;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object nomProObject = snapshot.child("nombrePropietario").getValue();
                String nombreProp = String.valueOf(nomProObject);
                if (!nombreProp.toString().equals("")) {
                    txtNombrePro.setText(nombreProp);
                }
                //Apellido
                Object apProObject = snapshot.child("apellidoPropietario").getValue();
                String apellidoProp = String.valueOf(apProObject);
                if (!apellidoProp.toString().equals("")) {
                    txtApellidoPro.setText(apellidoProp);
                }
                //Tipo Documento
                Object docProObject = snapshot.child("tipoDeDocumento").getValue();
                String docProp = String.valueOf(docProObject);
                if (!docProp.toString().equals("")) {
                    autoCompleteTextView.setText(docProp);
                }
                //Numero Documento
                Object numeroDocProObject = snapshot.child("numeroDeDocumento").getValue();
                String numeroDocProp = String.valueOf(numeroDocProObject);
                if (!numeroDocProp.toString().equals("")) {
                    txtNumeroDocumento.setText(numeroDocProp);
                }
                //Correo Propietario
                Object correoElecProObject = snapshot.child("correoElectronico").getValue();
                String correoElecProp = String.valueOf(correoElecProObject);
                if (!correoElecProp.toString().equals("")) {
                    txtCorreoElectronico.setText(correoElecProp);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnGuardarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreUpdate = txtNombrePro.getText().toString().trim();
                apellidoUpdate = txtApellidoPro.getText().toString().trim();
                tipoDocUpdate = autoCompleteTextView.getText().toString().trim();
                numeroDocUpdate = txtNumeroDocumento.getText().toString().trim();
                correoUpdate = txtCorreoElectronico.getText().toString().trim();

                if (nombreUpdate.toString().isEmpty()){
                    nom = 0;
                }
                if (apellidoUpdate.toString().isEmpty()){
                    ape = 0;
                }
                if (tipoDocUpdate.toString().isEmpty()){
                    tipoDoc = 0;
                }
                if (numeroDocUpdate.toString().isEmpty()){
                    numDoc = 0;
                }
                if (correoUpdate.toString().isEmpty()){
                    correo = 0;
                }

                int sum = nom+ape+tipoDoc+numDoc+correo;

                Map<String, Object> map = new HashMap<>();
                map.put("nombrePropietario", nombreUpdate);
                map.put("apellidoPropietario", apellidoUpdate);
                map.put("tipoDeDocumento", tipoDocUpdate);
                map.put("numeroDeDocumento", numeroDocUpdate);
                map.put("correoElectronico", correoUpdate);

                Map<String, Object> mapP = new HashMap<>();
                mapP.put("infoPersonal", sum);

                databaseReferencePorcentaje.updateChildren(mapP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                databaseReference.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.custom(InformacionPersonal.this, "Guardado Correctamente!", getResources().getDrawable(R.drawable.logo_taski),
                                        getResources().getColor(R.color.white), getResources().getColor(R.color.primario), Toasty.LENGTH_SHORT, true, true).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                finish();

            }
        });


        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void autoCompletDocumento() {
        String[] tipoDocumento = new String[]{
                "CC",
                "CE",
                "NIT",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(InformacionPersonal.this,
                R.layout.lista_items,
                tipoDocumento);
        autoCompleteTextView.setAdapter(adapter);
    }
}