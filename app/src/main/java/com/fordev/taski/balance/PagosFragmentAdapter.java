package com.fordev.taski.balance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.fordev.taski.BasicoPlanFragment;
import com.fordev.taski.GoldPlanFragment;
import com.fordev.taski.PremiumPlanFragment;

public class PagosFragmentAdapter extends FragmentStateAdapter {
    public PagosFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new BasicoPlanFragment();
            case  1:
                return  new PremiumPlanFragment();
            default:
                return new GoldPlanFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
