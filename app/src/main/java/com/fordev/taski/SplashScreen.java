package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 5000;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference infoBasica = databaseReference.child("users");
    //
    boolean infoIngresada = true;
    ImageView logoTaski;
    TextView poweredByLine, taskiSlogan;
    //Animation
    Animation showAnim, bottomAnim,bottomAnimDos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        logoTaski = findViewById(R.id.bg_image);
        poweredByLine = findViewById(R.id.powered_by_line);
        taskiSlogan = findViewById(R.id.taski);

        //Animation
        showAnim = AnimationUtils.loadAnimation(this, R.anim.show_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);
        bottomAnimDos = AnimationUtils.loadAnimation(this, R.anim.bottom_anim_dos);

        //Set Animations on elements
        logoTaski.setAnimation(showAnim);
        taskiSlogan.setAnimation(bottomAnim);
        poweredByLine.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                infoBasica.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("info").exists()){
                            infoIngresada = true;
                        }else {
                            infoIngresada = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && infoIngresada) {
                    // User is signed in
                    Intent i = new Intent(SplashScreen.this, UserMenuPrincipal.class);
                    startActivity(i);
                    finish();
                } else {
                    // User is signed out
                    Intent intent = new Intent(SplashScreen.this, UserDashboard.class);
                    startActivity(intent);
                    finish();
                }


            }
        },SPLASH_TIMER);
    }
}