package com.fordev.taski;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fordev.taski.balance.BalanceFragment;
import com.fordev.taski.gastos.GastosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserMenuPrincipal extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    //Firebase Instancias
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference info = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagen");
    private final DatabaseReference infoBasica = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");
    //componentes
    String urlImagen;
    ImageView logoNegocio;
    TextView nombreNegocio;
    FloatingActionButton faq;
    CardView venta_rapida,venta_multiple;
    Animation fromBottom,toBottom;
    RelativeLayout superior;
    int posicion= 0;

    private boolean clicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu_principal);
        bottomNavigationView = findViewById(R.id.navview);
        bottomNavigationView.getBackground();
        bottomNavigationView.setBackground(null);
        //faq acciones
        faq = findViewById(R.id.faq);
        venta_rapida = findViewById(R.id.venta_rapida);
        venta_multiple = findViewById(R.id.venta_multiple);

        //animaciones
        cargarAnimaciones();
        //setDefault Fragment in UsermenuPrincipal
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, new BalanceFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.balance:
                        posicion = 0;
                        fragment = new BalanceFragment();
                        faq.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primario)));
                        break;
                    case R.id.gastos:
                        fragment = new GastosFragment();
                        posicion = 1;
                        if (venta_rapida.getVisibility()==View.VISIBLE){
                            venta_rapida.startAnimation(toBottom);
                            venta_multiple.startAnimation(toBottom);
                            venta_multiple.setVisibility(View.GONE);
                            venta_rapida.setVisibility(View.GONE);
                            clicked = false;
                            animacionfaqPrincipalDos();
                        }
                        faq.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.rosado)));
                        break;

                    case R.id.profile:
                        fragment = new PerfilFragment();
                        posicion = 2;
                        if (venta_rapida.getVisibility()==View.VISIBLE){
                            venta_rapida.startAnimation(toBottom);
                            venta_multiple.startAnimation(toBottom);
                            venta_multiple.setVisibility(View.GONE);
                            venta_rapida.setVisibility(View.GONE);
                            clicked = false;
                            animacionfaqPrincipalDos();
                        }
                        break;

                    case R.id.estadisticas:
                        fragment = new EstadisticasFragment();
                        posicion = 2;
                        if (venta_rapida.getVisibility()==View.VISIBLE){
                            venta_rapida.startAnimation(toBottom);
                            venta_multiple.startAnimation(toBottom);
                            venta_multiple.setVisibility(View.GONE);
                            venta_rapida.setVisibility(View.GONE);
                            clicked = false;
                            animacionfaqPrincipalDos();
                        }
                        break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout
                    ,fragment).commit();


                return true;

        }

        });

        databaseReference.keepSynced(true);
        info.keepSynced(true);
        infoBasica.keepSynced(true);

        logoNegocio = findViewById(R.id.logoNegocio);
        nombreNegocio = findViewById(R.id.nameNegocio);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posicion==0){
                    opciones();
                }else if (posicion==1){
                    startActivity(new Intent(getApplicationContext(), GastosNegocio.class));
                }
            }
        });

        venta_rapida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VentaRapidaNegocio.class));
                ocultarOpciones();
                animacionfaqPrincipalDos();
            }
        });


        venta_multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VentasNegocio.class));
                ocultarOpciones();
                animacionfaqPrincipalDos();
            }
        });


        info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("imagen").exists()){
                    urlImagen = snapshot.child("imagen").getValue().toString();
                    Picasso.get().load(urlImagen).into(logoNegocio);
            }else {
                logoNegocio.setImageDrawable(getResources().getDrawable(R.drawable.logo_taski));
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        infoBasica.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("nombreNegocio").exists()){
                    String nombre_Negocio = snapshot.child("nombreNegocio").getValue().toString();
                    nombreNegocio.setText(nombre_Negocio);
                }else {
                    nombreNegocio.setText("Nombre Negocio");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void opciones() {
        if (clicked){
            animacionfaqPrincipal();
            venta_multiple.setAnimation(fromBottom);
            venta_rapida.setAnimation(fromBottom);
            venta_rapida.startAnimation(fromBottom);
            venta_multiple.startAnimation(fromBottom);
            venta_multiple.setVisibility(View.VISIBLE);
            venta_rapida.setVisibility(View.VISIBLE);
            clicked = false;
        }else {
            animacionfaqPrincipalDos();
            ocultarOpciones();
        }
    }

    private void ocultarOpc() {
        if (venta_multiple.getVisibility()== View.VISIBLE){
            venta_multiple.setVisibility(View.GONE);
            venta_rapida.setVisibility(View.GONE);
        }
    }

    private void ocultarOpciones() {
        venta_rapida.setAnimation(toBottom);
        venta_multiple.setAnimation(toBottom);
        venta_rapida.startAnimation(toBottom);
        venta_multiple.startAnimation(toBottom);
        venta_multiple.setVisibility(View.GONE);
        venta_rapida.setVisibility(View.GONE);
        clicked = true;
    }

    private void cargarAnimaciones() {
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.de_abajo_arriba);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.de_arriba_abajo);
    }


    private void animacionfaqPrincipalDos() {
        OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(faq).
                rotation(-90f).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();
    }

    private void animacionfaqPrincipal() {
        OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(faq).
                rotation(135f).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();
    }


    @Override
    protected void onStart() {
        super.onStart();
        cargarAnimaciones();
    }
}