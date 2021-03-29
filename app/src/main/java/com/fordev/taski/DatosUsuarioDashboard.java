package com.fordev.taski;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fordev.taski.otros.PorcentajesInformacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class DatosUsuarioDashboard extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView txtUri;
    TextInputLayout lista_doc,edt_NombreNegocio;
    TextInputEditText nombre_Negocio,nit_Negocio,nombre_Propietario,numero_documento,correo,txtApellidoPropietario;
    AutoCompleteTextView autoCompleteTextView;
    CircleImageView ponerFoto, profile_image;
    public Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    UploadTask uploadTask;
    Dialog myDiagol;
    String primeraVez2;
    RelativeLayout cargandoInfo,registrarInfo;
    int infoPersonal = 0;
    int infoNegocio = 0;
    String email;
    String phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_usuario_dashboard);
        //txtPhone = findViewById(R.id.txtNumeroUsuario);

        autoCompleteTextView = findViewById(R.id.autoCompleteText);
        lista_doc = findViewById(R.id.edtTipoDoc);
        edt_NombreNegocio = findViewById(R.id.edtNombreNegocio);
        nombre_Negocio = findViewById(R.id.txtNombreNegocio);
        nit_Negocio = findViewById(R.id.txtNumeroNit);
        nombre_Propietario = findViewById(R.id.txtNombrePropietario);
        numero_documento = findViewById(R.id.txtNumeroDoc);
        correo = findViewById(R.id.txtCorreo);
        ponerFoto = findViewById(R.id.camera);
        profile_image = findViewById(R.id.profile_image);
        cargandoInfo = findViewById(R.id.cargandoDatos);
        registrarInfo = findViewById(R.id.registrarInformacion);
        txtApellidoPropietario = findViewById(R.id.txtApellidoPropietario);

        //Store DAta Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profile_image.setImageDrawable(getResources().getDrawable(R.drawable.logo_taski));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email = user.getEmail();
        phone = user.getPhoneNumber();

        if (email==null){
            email = "";
        }
        if (phone == null) {
            phone = "";
        }


        ponerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colocarImagen();
            }
        });

        autoCompletDocumento();

    }

    private void colocarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void autoCompletDocumento() {
        String[] tipoDocumento = new String[]{
                "CC",
                "CE",
                "NIT",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DatosUsuarioDashboard.this,
                R.layout.lista_items,
                tipoDocumento);
        autoCompleteTextView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void GuardarDatos(View view) {
        String nombreNegocio = nombre_Negocio.getText().toString().trim();
        String nitNegocio = nit_Negocio.getText().toString().trim();
        String nombrePropietario = nombre_Propietario.getText().toString().trim();
        String numeroDocumento = numero_documento.getText().toString().trim();
        String tipoDocumento = autoCompleteTextView.getText().toString().trim();
        String correoElectronico = correo.getText().toString().trim();
        String txtApellidoProp = txtApellidoPropietario.getText().toString().trim();

        if (!nombrePropietario.isEmpty()){
            infoPersonal += 20;
        }

        if (!numeroDocumento.isEmpty()){
            infoPersonal += 20;
        }
        if (!tipoDocumento.isEmpty()){
            infoPersonal += 20;
        }
        if (!correoElectronico.isEmpty()){
            infoPersonal += 20;
        }
        if (!txtApellidoProp.isEmpty()){
            infoPersonal +=20;
        }
        //negocio
        if (!nombreNegocio.isEmpty()){
            infoNegocio += 20;
        }

        porcentajesMetodo();

        if(nombreNegocio.isEmpty()){
            edt_NombreNegocio.setError("Ingrese por favor un nombre");
            Toasty.custom(this, "Error al guardar!", getResources().getDrawable(R.drawable.logo_taski),
                    getResources().getColor(R.color.white),getResources().getColor(R.color.rosado), Toasty.LENGTH_SHORT, true, true).show();
        }else {
            UserInfoBasica userInfoBasica = new UserInfoBasica(nombreNegocio,nitNegocio,nombrePropietario,tipoDocumento,numeroDocumento,correoElectronico,"si","","","",txtApellidoProp,email,"",phone,false,false);
            FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info").setValue(userInfoBasica);
            Toasty.custom(this, "Guardado Correctamente!", getResources().getDrawable(R.drawable.logo_taski),
                    getResources().getColor(R.color.white),getResources().getColor(R.color.primario), Toasty.LENGTH_SHORT, true, true).show();
            startActivity(new Intent(getApplicationContext(),UserMenuPrincipal.class));
        }

    }

    private void porcentajesMetodo() {
        PorcentajesInformacion porcentajesInformacion = new PorcentajesInformacion();
        porcentajesInformacion.setInfoPersonal(infoPersonal);
        porcentajesInformacion.setInfoNegocio(infoNegocio);
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PorcentajeInformacion").setValue(porcentajesInformacion);
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
                        Toasty.custom(getApplicationContext(), "Error al subir la imagen!", getResources().getDrawable(R.drawable.logo_taski),
                                getResources().getColor(R.color.white),getResources().getColor(R.color.rosado), Toasty.LENGTH_SHORT, true, true).show();
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
            profile_image.setImageURI(imageUri);
            subirFotoPerfil();
        }
    }

    public void loHareLuego(View view) {
        UserInfoBasica userInfoBasica = new UserInfoBasica("","","","","","","si","","","","",email,"",phone,false,false);
        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info").setValue(userInfoBasica);
        porcentajesMetodo();
        Toasty.custom(this, "Recuerda Agregalos Luego!", getResources().getDrawable(R.drawable.logo_taski),
                getResources().getColor(R.color.white),getResources().getColor(R.color.primario), Toasty.LENGTH_LONG, true, true).show();
        startActivity(new Intent(getApplicationContext(),UserMenuPrincipal.class));

    }
}