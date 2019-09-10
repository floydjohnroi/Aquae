package com.example.aquae;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class FilterAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"TOP PAIDS", "PRICE"};
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
                Bundle b = new Bundle();
                b.putString("isForDelivery", isForDelivery);
                Filter1Fragment fragment = new Filter1Fragment();
                fragment.setArguments(b);
                return fragment;
            case 1:
                Bundle b1 = new Bundle();
                b1.putString("isForDelivery", isForDelivery);
                Filter2Fragment fragment1 = new Filter2Fragment();
                fragment1.setArguments(b1);
                return fragment1;

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
