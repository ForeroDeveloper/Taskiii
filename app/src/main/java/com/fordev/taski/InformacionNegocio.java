package com.fordev.taski;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class InformacionNegocio extends AppCompatActivity {

    TextInputEditText txtNombreNegocio, txtNitNegocio, txtDireccionNegocio, txtTelefonoNegocio,txtUbicacionNegocio;
    DatabaseReference databaseReference, databaseReferencePorcentaje, databaseReferenceIMG;
    FirebaseDatabase firebaseDatabase;
    MaterialButton btnGuardarInfo;
    String nombreUpdate, nitNegocioUpdate, ubicacionNegocioUpdate, direccionNegocio, telefonoNegocio;
    ImageView atras;
    CircleImageView ponerFoto, profile_image;
    public Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    String urlImagen;
    int infoNegocio = 0;
    int nom = 20;
    int ape = 20;
    int tipoDoc = 20;
    int numDoc = 20;
    int correo = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion_negocio);
        //Intancias
        txtUbicacionNegocio = findViewById(R.id.txtUbicacionNegocio);
        txtNombreNegocio = findViewById(R.id.txtNombreNegocio);
        txtNitNegocio = findViewById(R.id.txtNitNegocio);
        txtDireccionNegocio = findViewById(R.id.txtDireccionNegocio);
        txtTelefonoNegocio = findViewById(R.id.txtTelefonoNegocio);
        btnGuardarInfo = findViewById(R.id.btnGuardar);
        atras = findViewById(R.id.atras);
        ponerFoto = findViewById(R.id.camera);
        profile_image = findViewById(R.id.profile_image);

        //Store DAta Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //Seteos DEfaul Base de Datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("info");
                databaseReferenceIMG = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagen");

        databaseReferenceIMG.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("imagen").exists()){
                    urlImagen = snapshot.child("imagen").getValue().toString();
                    Picasso.get().load(urlImagen).into(profile_image);
                }else {
                    profile_image.setImageDrawable(getResources().getDrawable(R.drawable.logo_taski));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferencePorcentaje = firebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PorcentajeInformacion");
        databaseReference.keepSynced(true);
        databaseReferencePorcentaje.keepSynced(true);

        databaseReferencePorcentaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object values = snapshot.child("infoNegocio").getValue().toString();
                String values2 = String.valueOf(values);
                int values3 = Integer.parseInt(values2);
                infoNegocio = values3;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object nomProObject = snapshot.child("nombreNegocio").getValue();
                String nombreNegocio = String.valueOf(nomProObject);
                if (!nombreNegocio.toString().equals("")) {
                    txtNombreNegocio.setText(nombreNegocio);
                }
                //Apellido
                Object apProObject = snapshot.child("nitNegocio").getValue();
                String nitNegocio = String.valueOf(apProObject);
                if (!nitNegocio.toString().equals("")) {
                    txtNitNegocio.setText(nitNegocio);
                }
                //Tipo Documento
                Object docProObject = snapshot.child("ubicacionNegocio").getValue();
                String ubicacionNegocio = String.valueOf(docProObject);
                if (!ubicacionNegocio.toString().equals("")) {
                    txtUbicacionNegocio.setText(ubicacionNegocio);
                }
                //Numero Documento
                Object numeroDocProObject = snapshot.child("direccionNegocio").getValue();
                String direccionNegocio = String.valueOf(numeroDocProObject);
                if (!direccionNegocio.toString().equals("")) {
                    txtDireccionNegocio.setText(direccionNegocio);
                }
                //Correo Propietario
                Object correoElecProObject = snapshot.child("telefonoNegocio").getValue();
                String telefonoNegocio = String.valueOf(correoElecProObject);
                if (!telefonoNegocio.toString().equals("")) {
                    txtTelefonoNegocio.setText(telefonoNegocio);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnGuardarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreUpdate = txtNombreNegocio.getText().toString().trim();
                nitNegocioUpdate = txtNitNegocio.getText().toString().trim();
                ubicacionNegocioUpdate = txtUbicacionNegocio.getText().toString().trim();
                direccionNegocio = txtDireccionNegocio.getText().toString().trim();
                telefonoNegocio = txtTelefonoNegocio.getText().toString().trim();

                if (nombreUpdate.toString().isEmpty()){
                    nom = 0;
                }
                if (nitNegocioUpdate.toString().isEmpty()){
                    ape = 0;
                }
                if (ubicacionNegocioUpdate.toString().isEmpty()){
                    tipoDoc = 0;
                }
                if (direccionNegocio.toString().isEmpty()){
                    numDoc = 0;
                }
                if (telefonoNegocio.toString().isEmpty()){
                    correo = 0;
                }

                int sum = nom+ape+tipoDoc+numDoc+correo;

                Map<String, Object> map = new HashMap<>();
                map.put("nombreNegocio", nombreUpdate);
                map.put("nitNegocio", nitNegocioUpdate);
                map.put("ubicacionNegocio", ubicacionNegocioUpdate);
                map.put("direccionNegocio", direccionNegocio);
                map.put("telefonoNegocio", telefonoNegocio);

                Map<String, Object> mapP = new HashMap<>();
                mapP.put("infoNegocio", sum);

                databaseReferencePorcentaje.updateChildren(mapP).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                databaseReference.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FancyToast.makeText(InformacionNegocio.this, "Guardado Correctamente!", FancyToast.LENGTH_LONG, FancyToast.INFO, R.drawable.logo_taski, false).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InformacionNegocio.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
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

        ponerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colocarImagen();
            }
        });


    }

    private void subirFotoPerfil() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Subiendo Imagen...");
        pd.show();

        StorageReference profileFoto = storageReference.child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        profileFoto.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content), "Imagen Subida",Snackbar.LENGTH_LONG).show();
                        pd.dismiss();

                        profileFoto.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String foto = task.getResult().toString();
                                UserImagen userImagen = new UserImagen(foto);
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagen").setValue(userImagen);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toasty.custom(getApplicationContext(), "El peso no puede superar las 2MB!", getResources().getDrawable(R.drawable.logo_taski),
                                getResources().getColor(R.color.white),getResources().getColor(R.color.rosado), Toasty.LENGTH_LONG, true, true).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercent + "%");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            imageUri = data.getData();
            profile_image.setImageURI(imageUri);
            subirFotoPerfil();
        }
    }


    private void colocarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
}