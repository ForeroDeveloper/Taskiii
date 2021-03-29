package com.fordev.taski;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoldPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoldPlanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    NumberFormat nformat = new DecimalFormat("##,###,###.##");
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    RelativeLayout actualizarGold;
    int precioGoldTaski = 0;
    int precioGoldTaskiCompleto = 0;

    public GoldPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BasicoPlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoldPlanFragment newInstance(String param1, String param2) {
        GoldPlanFragment fragment = new GoldPlanFragment();
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
        View view = inflater.inflate(R.layout.fragment_gold_plan, container, false);

        actualizarGold = view.findViewById(R.id.gold);
        TextView precioSigno = view.findViewById(R.id.precioSigno);
        TextView precioCompleto = view.findViewById(R.id.precioCompleto);

        databaseReference.child("users").child("precioGoldCorto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String precio = snapshot.getValue().toString();
                    int precioGold = Integer.parseInt(precio);
                    precioGoldTaski = precioGold;
                    precioSigno.setText(String.valueOf(nformat.format(precioGoldTaski)));
                    precioCompleto.setText("$ " + String.valueOf(nformat.format(precioGoldTaski)) + ".99");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child("precioGold").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String precio = snapshot.getValue().toString();
                    int precioGold = Integer.parseInt(precio);
                    precioGoldTaskiCompleto = precioGold;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        actualizarGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_consignar))
                        .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                        .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setGravity(Gravity.CENTER)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View view1 = dialog.getHolderView();

                MaterialButton btn_whatsapp = view1.findViewById(R.id.btn_whatsapp);
                ImageView copiarCuenta = view1.findViewById(R.id.copiarCuenta);
                ImageView copiarNequi = view1.findViewById(R.id.copiarNequi);
                ImageView copiarDavi = view1.findViewById(R.id.copiarDavi);
                TextView totalPesos = view1.findViewById(R.id.totalPesos);
                totalPesos.setText("$ " + String.valueOf(nformat.format(precioGoldTaskiCompleto)));


                btn_whatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://wa.me/+573178816094/?text=Hola,%20quiero%20enviar%20un%20comprobante%20de%20pago%20y%20activar%20mi%20cuenta.";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });

                copiarCuenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClipboard(getContext(), "91208022445");
                        FancyToast.makeText(getContext(), "Copiado correctamente!", FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.logo_taski, false).show();
                    }
                });

                copiarNequi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClipboard(getContext(), "3204197482");
                        FancyToast.makeText(getContext(), "Copiado correctamente!", FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.logo_taski, false).show();
                    }
                });

                copiarDavi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClipboard(getContext(), "3204197482");
                        FancyToast.makeText(getContext(), "Copiado correctamente!", FancyToast.LENGTH_SHORT, FancyToast.INFO, R.drawable.logo_taski, false).show();
                    }
                });


                dialog.show();
            }
        });


        return view;
    }

    private void setClipboard(Context context, String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}