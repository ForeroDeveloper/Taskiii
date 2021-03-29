package com.fordev.taski.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.fordev.taski.R;
import com.fordev.taski.UserMenuPrincipal;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PagosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public RelativeLayout gradiente;
    Animation animation;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public PagosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Balance.
     */
    // TODO: Rename and change types and number of parameters
    public static PagosFragment newInstance(String param1, String param2) {
        PagosFragment fragment = new PagosFragment();
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
        View view = inflater.inflate(R.layout.fragment_pagos, container, false);

        gradiente = view.findViewById(R.id.inicio);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.show_anim_planes);

        ImageView close = view.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserMenuPrincipal.class));
            }
        });


        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));

        ViewPager2 viewPager2 = view.findViewById(R.id.vp_2);
        viewPager2.setAdapter(new PagosFragmentAdapter(getActivity()));
        TabLayout tabLayout = view.findViewById(R.id.tab);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Básico");
                        break;
                    case 1:
                        tab.setText("Premium");
                        break;
                    case 2:
                        tab.setText("Gold");
                        break;
                }

            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();

                switch (position) {
                    case 0:
                        gradiente.setAnimation(animation);
                        gradiente.startAnimation(animation);
                        gradiente.setBackground(getResources().getDrawable(R.drawable.gradiente_basico));
                        break;
                    case 1:
                        gradiente.setAnimation(animation);
                        gradiente.startAnimation(animation);
                        gradiente.setBackground(getResources().getDrawable(R.drawable.gradiente_premium));
                        break;
                    case 2:
                        gradiente.setAnimation(animation);
                        gradiente.startAnimation(animation);
                        gradiente.setBackground(getResources().getDrawable(R.drawable.gradiente_gold));
                        break;

                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return  view;
    }


}