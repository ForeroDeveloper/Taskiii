package com.fordev.taski;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RelativeLayout info_personal, info_negocio, info_clientes, info_proveedores, info_inventario, contacto_whatsapp, contacto_facebook, terminos_condiciones,cerrar_sesion,
    info_pedidos;
    LinearProgressIndicator progressIndicator, progressIndicatorNegocio;
    TextView txtIndicadorInfoPersonal, txtProgressIndicatorNegocio, nombre, btnPremium,premiums,usuario,usuarioPremium;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference porcentajePersonalInfo = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PorcentajeInformacion");
    private final DatabaseReference nombreProp = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");

    ImageView info_basica, activo, info_contactos, info_inventario_icon;
    String codigo = null;
    int precioGoldTaski = 0;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat nformat = new DecimalFormat("##,###,###.##");


    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilGragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_gragment, container, false);

        info_personal = view.findViewById(R.id.info_personal);
        info_negocio = view.findViewById(R.id.info_negocio);
        info_clientes = view.findViewById(R.id.info_clientes);
        info_proveedores = view.findViewById(R.id.info_proveedores);
        progressIndicator = view.findViewById(R.id.indicadorPersonal);
        info_inventario = view.findViewById(R.id.info_inventario);
        contacto_whatsapp = view.findViewById(R.id.contacto_whatsapp);
        contacto_facebook = view.findViewById(R.id.contacto_facebook);
        terminos_condiciones = view.findViewById(R.id.terminos_condiciones);
        progressIndicatorNegocio = view.findViewById(R.id.progressIndicatorNegocio);
        txtIndicadorInfoPersonal = view.findViewById(R.id.txtIndicadorInfoPersonal);
        txtProgressIndicatorNegocio = view.findViewById(R.id.txtprogressIndicatorNegocio);
        nombre = view.findViewById(R.id.nombre);
        info_contactos = view.findViewById(R.id.info_contactos);
        btnPremium = view.findViewById(R.id.btnPremium);
        premiums = view.findViewById(R.id.premiums);
        usuario = view.findViewById(R.id.usuario);
        usuarioPremium = view.findViewById(R.id.usuarioPremium);
        cerrar_sesion = view.findViewById(R.id.cerrar_sesion);
        //infos
        info_basica = view.findViewById(R.id.info_basica);
        info_inventario_icon = view.findViewById(R.id.info_inventario_icon);
        activo = view.findViewById(R.id.activo);
        info_pedidos = view.findViewById(R.id.info_pedidos);

        calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        String dato = sdf.format(calendar.getTime());

        //tap target
        final Spannable spannable2 = new SpannableString("Aquí puedes ingresar el codigo de activación el cual se entregará cuando haces el respectivo pago de la App.");
        spannable2.setSpan(new UnderlineSpan(), spannable2.length() - "TapTargetView".length(),
                spannable2.length(), 0);

        SharedPreferences preferences = getActivity().getSharedPreferences("TUTORIAL", Context.MODE_PRIVATE);
        boolean premiuimAviso = preferences.getBoolean("ClickPremium" , false);

        if (!premiuimAviso){
            TapTargetView.showFor(getActivity(),
                    TapTarget.forView(activo, "Activa tu cuenta a GOLD", spannable2)
                            .cancelable(false)
                            .drawShadow(true)
                            .titleTextDimen(R.dimen.text)
                            .tintTarget(false), new TapTargetView.Listener() {
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            SharedPreferences.Editor editor  = preferences.edit();
                            editor.putBoolean("ClickPremium", true);
                            editor.apply();
                        }

                        public void onOuterCircleClick(TapTargetView view) {
                            super.onOuterCircleClick(view);
                        }
                    }
            );
        }


        Animation connectingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        activo.startAnimation(connectingAnimation);

        databaseReference.child("users").child("precioPremium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String precio = snapshot.getValue().toString();
                    int precioGold = Integer.parseInt(precio);
                    precioGoldTaski = precioGold;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        activo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("users").child("activacion").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String code = snapshot.getValue().toString();
                            if (code!=null && !code.equals("")){
                                codigo = code;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_activar_cuenta))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setGravity(Gravity.CENTER)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View view1 = dialog.getHolderView();

                MaterialButton btn_confirmar = view1.findViewById(R.id.btn_confirmar);
                TextInputLayout code_introducido = view1.findViewById(R.id.codigo);
                TextView btn_cancel = view1.findViewById(R.id.btn_cancel);

                btn_confirmar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String codigo_introducido = code_introducido.getEditText().getText().toString();

                        if (codigo_introducido.equals(codigo)){
                            Map<String, Object> map = new HashMap<>();
                            map.put("gold", true);
                            map.put("fechaFinGold", dato);
                            firebaseDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info")
                                    .updateChildren(map);
                            //actualizar nuevo codigo
                            int randomCode = (int) (Math.random() * (30000 - 100));
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("activacion", String.valueOf(randomCode));
                            firebaseDatabase.getReference().child("users").updateChildren(map2);
                            dialog.dismiss();
                            FancyToast.makeText(getContext(), "Codigo Correcto!", FancyToast.LENGTH_LONG, FancyToast.INFO, R.drawable.logo_taski, false).show();
                            v.getContext().startActivity(new Intent(getContext(),FelicidadesGold.class));
                        }else {
                            FancyToast.makeText(getContext(),"Código Incorrecto",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        info_inventario_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balloon balloon = new Balloon.Builder(getContext())
                        .setArrowSize(10)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                        .setArrowPosition(0.5f)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setHeight(110)
                        .setTextSize(15f)
                        .setMargin(5)
                        .setCornerRadius(6f)
                        .setAlpha(0.9f)
                        .setText("Agrega y administra tu inventario total, incluyendo el valor que tiene en su totalidad, podrás generar los QR a cada producto que registres.")
                        .setTextColor(getResources().getColor(R.color.white))
                        .setTextIsHtml(true)
                        .setBackgroundColor(getResources().getColor(R.color.primario))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignBottom(info_inventario_icon);
            }
        });

        info_basica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balloon balloon = new Balloon.Builder(getContext())
                        .setArrowSize(10)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                        .setArrowPosition(0.5f)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setHeight(70)
                        .setTextSize(15f)
                        .setMargin(5)
                        .setCornerRadius(6f)
                        .setAlpha(0.9f)
                        .setText("Información que se mostrará en tus facturas de venta y cobros")
                        .setTextColor(getResources().getColor(R.color.white))
                        .setTextIsHtml(true)
                        .setBackgroundColor(getResources().getColor(R.color.primario))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignBottom(info_basica);

            }
        });

        info_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balloon balloon = new Balloon.Builder(getContext())
                        .setArrowSize(10)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                        .setArrowPosition(0.5f)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setHeight(100)
                        .setTextSize(15f)
                        .setMargin(5)
                        .setCornerRadius(6f)
                        .setAlpha(0.9f)
                        .setText("Podrás ingresar la lista de contacto de tus clientes y proveedores, hacerles llamadas , y agregarlos a las facturas sin necesidad de reescribir el nombre.")
                        .setTextColor(getResources().getColor(R.color.white))
                        .setTextIsHtml(true)
                        .setBackgroundColor(getResources().getColor(R.color.primario))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignBottom(info_contactos);
            }
        });

        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), UserDashboard.class));
                getActivity().finish();
            }
        });


        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_gold))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1510)
                        .setGravity(Gravity.BOTTOM)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();

                RelativeLayout btnAcutualizar = views.findViewById(R.id.actualizar);
                TextView precio = views.findViewById(R.id.precio);
                precio.setText("$ "+String.valueOf(nformat.format(precioGoldTaski))+".99");


                btnAcutualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PlanesMenuPrincipal.class));
                        dialog.dismiss();
                        //comen
                    }
                });

                dialog.show();
            }
        });


        info_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InformacionPersonal.class));
            }
        });

        info_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ListaDePedidos.class));
            }
        });

        info_negocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InformacionNegocio.class));
            }
        });

        info_proveedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ListaProveedores.class));
            }
        });


        porcentajePersonalInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object valueObject = snapshot.child("infoPersonal").getValue();
                    int porcentajeFinal = Integer.parseInt(String.valueOf(valueObject));
                    int p = porcentajeFinal * 100 / 100;
                    progressIndicator.setProgress(p);
                    txtIndicadorInfoPersonal.setText(String.valueOf(porcentajeFinal) + "%");

                } else {
                    progressIndicator.setProgress(0);
                }

                if (snapshot.exists()) {
                    Object valueObject = snapshot.child("infoNegocio").getValue();
                    int porcentajeFinal = Integer.parseInt(String.valueOf(valueObject));
                    int p = porcentajeFinal * 100 / 100;
                    progressIndicatorNegocio.setProgress(p);
                    txtProgressIndicatorNegocio.setText(String.valueOf(porcentajeFinal) + "%");

                } else {
                    progressIndicatorNegocio.setProgress(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nombreProp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("nombrePropietario").exists()){
                    String nomPropietario = snapshot.child("nombrePropietario").getValue().toString();
                    Object premium = snapshot.child("premium").getValue();
                    Object gold = snapshot.child("gold").getValue();
                    boolean prem = Boolean.parseBoolean(String.valueOf(premium));
                    boolean golAc = Boolean.parseBoolean(String.valueOf(gold));

                    try {
                        if (prem){
                            premiums.setText("PREMIUM");
                            premiums.setTextColor(getResources().getColor(R.color.rojo));
                            btnPremium.setVisibility(View.GONE);
                            usuarioPremium.setVisibility(View.VISIBLE);
                        }
                        if (golAc){
                            premiums.setText("GOLD");
                            premiums.setTextColor(getResources().getColor(R.color.gold));
                            btnPremium.setVisibility(View.GONE);
                            usuario.setVisibility(View.VISIBLE);
                        }
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                    if (nomPropietario.equals("")) {
                        nombre.setText("Sin Especificar");
                    } else {
                        nombre.setText(nomPropietario + "!");
                    }
                }else {
                    nombre.setText("Sin Especificar");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        info_clientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ListaClientes.class));
            }
        });

        info_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), InventarioPerfil.class));
            }
        });

        contacto_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://wa.me/+573178816094/?text=Hola,%20quiero%20chatear%20con%20alguien";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        contacto_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://fb.me/taski.app.co";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        terminos_condiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://docs.google.com/document/d/1UCea1PneoyGPPcqVAf5hkH_bVAecA_E6MXMauFkQnKo/edit?usp=sharing";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return view;
    }

}