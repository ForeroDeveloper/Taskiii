package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpScreen extends AppCompatActivity {
    RelativeLayout btnSigIn;
    GoogleSignInClient signInClient;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);
        btnSigIn = findViewById(R.id.google);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("389781457112-feeu7h4cmbj8n8vfrqgc67l8o2r826hl.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //iniiciar cliente sign in
        signInClient = GoogleSignIn.getClient(SignUpScreen.this, googleSignInOptions);

        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = signInClient.getSignInIntent();
                //start acitivty
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check condition
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            //check condition
            if (signInAccountTask.isSuccessful()) {
                //cuando inicie en google
                String s = "Ingreso Satisfactorio!";
                //Display toas
                displayToast(s);
                //Initialize sign in account
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);

                    if (googleSignInAccount != null) {
                        //Cuando la cuenta es diferente de nulo

                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(), null);

                        //Checkear las credenciales
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task2) {

                                        if (task2.isSuccessful()) {
                                            //redirigir a user menu principal
                                            Toast.makeText(SignUpScreen.this, "Verificacion exitosa", Toast.LENGTH_SHORT).show();
                                            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");

                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.child("primeraVez").exists()) {
                                                        startActivity(new Intent(getApplicationContext(), UserMenuPrincipal.class));
                                                    } else {
                                                        startActivity(new Intent(getApplicationContext(), DatosUsuarioDashboard.class));
                                                    }
                                                    finishAffinity();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                });
                    }

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}