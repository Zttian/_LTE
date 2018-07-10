package com.tky.lte.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by ttz on 2018/6/6.
 *
 */

public class TabLayoutFmPageAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] titles;
    private ArrayList<Fragment> fragments;


    public TabLayoutFmPageAdapter(FragmentManager fm, Context context, String[] titles, ArrayList<Fragment> fragments){
        super(fm);
        this.context = context;
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
