package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PremiumPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PremiumPlanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RelativeLayout comprar_premium;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    int precioGoldTaski = 0;
    TextView precioSignoTres,precioCompleto;

    public PremiumPlanFragment() {
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
    public static PremiumPlanFragment newInstance(String param1, String param2) {
        PremiumPlanFragment fragment = new PremiumPlanFragment();
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
        View view = inflater.inflate(R.layout.fragment_premium_plan, container, false);

        comprar_premium = view.findViewById(R.id.comprar_premium);
        precioSignoTres = view.findViewById(R.id.precioSignoTres);
        precioCompleto = view.findViewById(R.id.precioCompleto);

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



        comprar_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PremiumActividad.class));
            }
        });


        return  view;
    }

}