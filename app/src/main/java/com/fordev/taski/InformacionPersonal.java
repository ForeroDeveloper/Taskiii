package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InformacionPersonal extends AppCompatActivity {

    TextInputEditText txtNombrePro,txtApellidoPro,txtNumeroDocumento,txtCorreoElectronico;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    AutoCompleteTextView autoCompleteTextView;
    MaterialButton btnGuardarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_personal);
        //Intancias
        autoCompleteTextView = findViewById(R.id.autoCompleteText);
        txtNombrePro = findViewById(R.id.txtNombrePro);
        txtApellidoPro = findViewById(R.id.txtApellidoPro);
        txtNumeroDocumento = findViewById(R.id.txtNumeroDocumento);
        txtCorreoElectronico = findViewById(R.id.txtCorreoElectronico);
        btnGuardarInfo = findViewById(R.id.btnGuardar);

        //Seteos DEfaul Base de Datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("info");
        databaseReference.keepSynced(true);
        autoCompletDocumento();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object nomProObject = snapshot.child("nombrePropietario").getValue();
                String nombreProp = String.valueOf(nomProObject);
                if (!nombreProp.toString().equals("")){
                    txtNombrePro.setText(nombreProp);
                }
                //Apellido
                Object apProObject = snapshot.child("apellidoPropietario").getValue();
                String apellidoProp = String.valueOf(apProObject);
                if (!apellidoProp.toString().equals("")){
                    txtApellidoPro.setText(apellidoProp);
                }
                //Tipo Documento
                Object docProObject = snapshot.child("tipoDeDocumento").getValue();
                String docProp = String.valueOf(docProObject);
                if (!docProp.toString().equals("")){
                    autoCompleteTextView.setText(docProp);
                }
                //Numero Documento
                Object numeroDocProObject = snapshot.child("numeroDeDocumento").getValue();
                String numeroDocProp = String.valueOf(numeroDocProObject);
                if (!numeroDocProp.toString().equals("")){
                    txtNumeroDocumento.setText(numeroDocProp);
                }
                //Correo Propietario
                Object correoElecProObject = snapshot.child("correoElectronico").getValue();
                String correoElecProp = String.valueOf(correoElecProObject);
                if (!correoElecProp.toString().equals("")){
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

                String nombreUpdate = txtNombrePro.getText().toString();
                String apellidoUpdate = txtApellidoPro.getText().toString();
                String tipoDocUpdate = autoCompleteTextView.getText().toString();
                String numeroDocUpdate = txtNumeroDocumento.getText().toString();
                String correoUpdate = txtCorreoElectronico.getText().toString();
                if (nombreUpdate.isEmpty()){
                    nombreUpdate = "";
                }else if (apellidoUpdate.isEmpty()){
                    apellidoUpdate = "";
                }else if (tipoDocUpdate.isEmpty()){
                    tipoDocUpdate = "";
                }else if (numeroDocUpdate.isEmpty()){
                    numeroDocUpdate = "";
                }else if (correoUpdate.isEmpty()){
                    correoUpdate = "";
                }

                Map<String, Object> map = new HashMap<>();

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