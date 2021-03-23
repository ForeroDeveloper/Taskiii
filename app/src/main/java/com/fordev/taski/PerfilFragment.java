package com.fordev.taski;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

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

    RelativeLayout info_personal, info_negocio, info_clientes, info_proveedores, info_inventario, contacto_whatsapp, contacto_facebook, terminos_condiciones;
    LinearProgressIndicator progressIndicator, progressIndicatorNegocio;
    TextView txtIndicadorInfoPersonal, txtProgressIndicatorNegocio, nombre, btnPremium,premiums,usuario;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private final DatabaseReference porcentajePersonalInfo = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PorcentajeInformacion");
    private final DatabaseReference nombreProp = databaseReference.child("users")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("info");

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
        btnPremium = view.findViewById(R.id.btnPremium);
        premiums = view.findViewById(R.id.premiums);
        usuario = view.findViewById(R.id.usuario);


        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_gold))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setExpanded(true, 1600)
                        .setGravity(Gravity.BOTTOM)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View views = dialog.getHolderView();

                RelativeLayout btnAcutualizar = views.findViewById(R.id.actualizar);


                btnAcutualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), PlanesMenuPrincipal.class));
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
                    Object premium = snapshot.child("Premium").getValue();
                    boolean prem = Boolean.parseBoolean(String.valueOf(premium));

                    if (prem){
                        premiums.setText("GOLD");
                        premiums.setTextColor(getResources().getColor(R.color.gold));
                        btnPremium.setVisibility(View.GONE);
                        usuario.setVisibility(View.VISIBLE);
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