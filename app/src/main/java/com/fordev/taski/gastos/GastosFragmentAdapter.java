package com.fordev.taski.gastos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fordev.taski.balance.AnualFragment;
import com.fordev.taski.balance.DiaFragment;
import com.fordev.taski.balance.MesFragment;
import com.fordev.taski.balance.SemanaFragment;

public class GastosFragmentAdapter extends FragmentStateAdapter {
    public GastosFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new DiaFragmentGastos();
            case 1:
                return  new SemanaFragmentGastos();
            case 2:
                return new MesFragmentGastos();
            default:
                return new AnualFragmentGastos();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
