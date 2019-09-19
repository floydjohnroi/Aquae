package com.example.aquae;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FilterAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"NEARBY", "POPULAR", "ALL"};
    private Context context;
    String isForDelivery;

    public FilterAdapter(FragmentManager fm, Context ctx, String isForDelivery) {
        super(fm);
        context = ctx;
        this.isForDelivery = isForDelivery;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Bundle b1 = new Bundle();
                b1.putString("isForDelivery", isForDelivery);
                Filter1Fragment fragment1 = new Filter1Fragment();
                fragment1.setArguments(b1);
                return fragment1;
            case 1:
                Bundle b2 = new Bundle();
                b2.putString("isForDelivery", isForDelivery);
                Filter2Fragment fragment2 = new Filter2Fragment();
                fragment2.setArguments(b2);
                return fragment2;
            case 2:
                Bundle b3 = new Bundle();
                b3.putString("isForDelivery", isForDelivery);
                Filter3Fragment fragment3 = new Filter3Fragment();
                fragment3.setArguments(b3);
                return fragment3;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
