package com.fordev.taski.balance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BalanceFragmentAdapter extends FragmentStateAdapter {
    public BalanceFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new DiaFragment();
            case 1:
                return  new SemanaFragment();
            case 2:
                return new MesFragment();
            default:
                return new AnualFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
