package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class VerificationOTP extends AppCompatActivity {

    PinView pinView;
    FirebaseAuth auth;
    String codeBySystem;
    TextView txtNumero;
    String phoneNumber;
    String phoneNumberSencillo;
    DatabaseReference databaseReference;
    String primerLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_o_t_p);

        pinView = findViewById(R.id.pinView);
        auth = FirebaseAuth.getInstance();
        txtNumero = findViewById(R.id.txtNumero);


        phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneNumberSencillo = getIntent().getStringExtra("phoneNumberSencillo");
        txtNumero.setText(phoneNumber);

        enviarCodigoDeVerificacion(phoneNumber);
    }

    private void enviarCodigoDeVerificacion(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code!=null){
                        pinView.setText(code);
                        verificarCodigo(code);
                    }

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerificationOTP.this, "Hubo un error al verificar, Intenta de nuevo.", Toast.LENGTH_LONG).show();

                }
            };

    private void verificarCodigo(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(VerificationOTP.this, "Verificacion exitosa", Toast.LENGTH_SHORT).show();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");

                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("primeraVez").exists()){

                                        startActivity(new Intent(getApplicationContext(),UserMenuPrincipal.class));

                                    }else {
                                        startActivity(new Intent(getApplicationContext(),DatosUsuarioDashboard.class));
                                    }

                                    finishAffinity();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerificationOTP.this, "Verificación error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void verificarScreen(View view) {
        String code = pinView.getText().toString();
        if (!code.isEmpty()){
            verificarCodigo(code);
        }else {
            Toasty.custom(this, "Ingrese un código válido", getResources().getDrawable(R.drawable.logo_taski),
                    getResources().getColor(R.color.white),getResources().getColor(R.color.primario), Toasty.LENGTH_SHORT, true, true).show();
        }
    }
}