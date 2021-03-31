package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PremiumActividad extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "PremiumActividad";
    BillingProcessor bp;
    TransactionDetails transactionDetails = null;
    CardView comprar_premium;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    TextView cambiar;
    int precioGoldTaski = 0;
    TextView precioSignoTres,precioCompleto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_actividad);
        comprar_premium = findViewById(R.id.comprar_premium);
        precioSignoTres = findViewById(R.id.precioSignoTres);
        precioCompleto = findViewById(R.id.precioCompleto);

        databaseReference.child("users").child("precioPremium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String precio = snapshot.getValue().toString();
                    int precioGold = Integer.parseInt(precio);
                    precioGoldTaski = precioGold;
                    precioSignoTres.setText(String.valueOf(precioGoldTaski));
                    precioCompleto.setText("$ " + String.valueOf(precioGoldTaski) + ".99 " + "Por Mes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cambiar = findViewById(R.id.cambiar);
        Animation connectingAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_anim);
        comprar_premium.startAnimation(connectingAnimation);

        bp = new BillingProcessor(this, getResources().getString(R.string.play_console_licence), this);
        bp.initialize();

    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized: ");
        String productID = getResources().getString(R.string.suscripcion);

        transactionDetails = bp.getSubscriptionTransactionDetails(productID);
        bp.loadOwnedPurchasesFromGoogle();

        comprar_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bp.isSubscriptionUpdateSupported()) {
                    bp.subscribe(PremiumActividad.this, productID);
                }else {
                    Log.d(TAG, "onClick: no soporta ");
                }
            }
        });

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: ");
        Map<String, Object> map = new HashMap<>();
        map.put("premium", true);
        firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info")
                .updateChildren(map);
        cambiar.setText("Premium");

        finish();
        startActivity(new Intent(getApplicationContext(), FelicidadesPremium.class));
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void atras(View view) {
        onBackPressed();
    }
}