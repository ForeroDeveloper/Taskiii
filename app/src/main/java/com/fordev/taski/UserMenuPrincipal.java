package com.fordev.taski;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.fordev.taski.balance.BalanceFragment;
import com.fordev.taski.gastos.GastosFragment;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserMenuPrincipal extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    BottomNavigationView bottomNavigationView;
    //Firebase Instancias
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference info = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagen");
    private final DatabaseReference infoBasica = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");
    private final DatabaseReference premium = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    //componentes
    String urlImagen;
    ImageView logoNegocio,ic_pregunta;
    TextView nombreNegocio;
    FloatingActionButton faq;
    CardView venta_rapida, venta_multiple;
    Animation fromBottom, toBottom;
    RelativeLayout superior;
    int posicion = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    final Calendar Cal = Calendar.getInstance();
    private boolean clicked = true;
    boolean premium_activado = false;
    //Transacciones
    private static final String TAG = "PremiumActividad";
    BillingProcessor bp;
    TransactionDetails transactionDetails = null;
    long totalFacturasUser = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu_principal);
        bottomNavigationView = findViewById(R.id.navview);
        ic_pregunta = findViewById(R.id.ic_pregunta);
        bottomNavigationView.getBackground();
        bottomNavigationView.setBackground(null);
        //faq acciones
        faq = findViewById(R.id.faq);
        venta_rapida = findViewById(R.id.venta_rapida);
        venta_multiple = findViewById(R.id.venta_multiple);

        Cal.getTime();
        String dato = sdf.format(Cal.getTime());
        bp = new BillingProcessor(this, getResources().getString(R.string.play_console_licence), this);
        bp.initialize();
        hasSuscription();

        final DatabaseReference totalFacturasCreadas = firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("facturas").child("facturasCreadas");
        totalFacturasCreadas.keepSynced(true);

        totalFacturasCreadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    totalFacturasUser = (snapshot.getChildrenCount());
                    if (totalFacturasUser==12){
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("TUTORIAL", Context.MODE_PRIVATE);
                        boolean calificado = preferences.getBoolean("ClickCalificar", false);
                        if (!calificado) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("ClickCalificar", true);
                            editor.apply();
                            startActivity(new Intent(getApplicationContext(), CalificarAppTaski.class));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        infoBasica.child("fechaFinGold").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String fechaFin = snapshot.getValue().toString();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        Date date = dateFormat.parse(fechaFin);
                        Date date2 = dateFormat.parse(dato);
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        int dato_int = Integer.parseInt(String.valueOf(sdf.format(date)));
                        int dato_int_actual = Integer.parseInt(String.valueOf(sdf.format(date2)));
                        if (dato_int_actual == dato_int){
                            Map<String, Object> map = new HashMap<>();
                            map.put("gold", false);
                            firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info")
                                    .updateChildren(map);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("TUTORIAL", MODE_PRIVATE);

        final Spannable spannable = new SpannableString("Crea ventas Multiples y rapidas");
        spannable.setSpan(new UnderlineSpan(), spannable.length() - "TapTargetView".length(),
                spannable.length(), 0);

        boolean venta = sharedPreferences.getBoolean("CrearVenta" , false);

        if (!venta){
            TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.faq), "Crear Ventas", spannable)
                            .cancelable(false)
                            .drawShadow(true)
                            .titleTextDimen(R.dimen.text)
                            .tintTarget(false), new TapTargetView.Listener() {
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            SharedPreferences.Editor editor  = sharedPreferences.edit();
                            editor.putBoolean("CrearVenta", true);
                            editor.putBoolean("DetallesFactura", false);
                            editor.putBoolean("ClickInventario", false);
                            editor.putBoolean("ClickScanner", false);
                            editor.putBoolean("ClickPremium", false);
                            editor.putBoolean("ClickCalificar", false);
                            editor.apply();
                            opciones();
                        }
                        public  void  onOuterCircleClick(TapTargetView view){
                            super.onOuterCircleClick(view);
                        }
                    }
            );

        }


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
                        posicion = 3;
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

        ic_pregunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserMenuPrincipal.this, ComoUsarTaski.class));
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posicion==0){
                    opciones();
                }else if (posicion==1){
                    startActivity(new Intent(getApplicationContext(), GastosNegocio.class));
                }else if (posicion==2){
                    FancyToast.makeText(UserMenuPrincipal.this, "Ve a tu apartado de Balance, para agregar Ventas!", FancyToast.LENGTH_LONG, FancyToast.INFO, R.drawable.logo_taski, false).show();
                }else if (posicion==3){
                    FancyToast.makeText(UserMenuPrincipal.this, "Ve a tu apartado de Balance, para agregar Ventas!", FancyToast.LENGTH_LONG, FancyToast.INFO, R.drawable.logo_taski, false).show();
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
                if (snapshot.child("fechaFinGold").exists()){
                    //nada
                }else {
                    infoBasica.child("fechaFinGold").setValue(sdf.format(Cal.getTime()));
                }

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

    private boolean hasSuscription() {
        if (transactionDetails != null){
            return  transactionDetails.purchaseInfo != null;
        }else {
            return false;
        }

    }


    @Override
    public void onBillingInitialized() {
        String productID = getResources().getString(R.string.suscripcion);
        transactionDetails = bp.getSubscriptionTransactionDetails(productID);
        bp.loadOwnedPurchasesFromGoogle();

        if (bp.isSubscriptionUpdateSupported()) {
            Log.d(TAG, "onBillingInitialized: si soporta");
        }else {
            Log.d(TAG, "onClick: no soporta ");
        }

        if (hasSuscription()){
            //Poner Premium en True

        }else {
            //Poner Premium en False
            Map<String, Object> map = new HashMap<>();
            map.put("premium", false);
            firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info")
                    .updateChildren(map);

            Log.d(TAG, "Se puso en false la suscripcion");

        }

    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarAnimaciones();
    }


    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DialogPlus dialog = DialogPlus.newDialog(UserMenuPrincipal.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_confirm_cerrar_app))
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setGravity(Gravity.CENTER)
                .setContentBackgroundResource(android.R.color.transparent)
                .create();

        View views = dialog.getHolderView();
        MaterialButton btn_salir = views.findViewById(R.id.btn_salir);
        TextView cancel = views.findViewById(R.id.btn_cancel);

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserMenuPrincipal.super.onBackPressed();
                onBackPressed();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}