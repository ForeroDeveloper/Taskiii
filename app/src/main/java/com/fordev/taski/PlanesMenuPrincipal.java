package com.fordev.taski;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.fordev.taski.balance.BalanceFragment;
import com.fordev.taski.balance.PagosFragment;
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

public class PlanesMenuPrincipal extends AppCompatActivity {
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
        setContentView(R.layout.planes_menu_principal);

        //setDefault Fragment in UsermenuPrincipal
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_layout, new PagosFragment()).commit();

    }

}