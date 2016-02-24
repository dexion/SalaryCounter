package com.snake.salarycounter.adapters;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snake.salarycounter.fragments.FinanceFormFragment;
import com.snake.salarycounter.fragments.FinanceFragment;

public class MonthlyFinancePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<FinanceFormFragment> fragments;

    // Lazily create the fragments
    public ArrayList<FinanceFormFragment> getFragments() {
        if (fragments == null) {
            fragments = new ArrayList<>();
            for (int i = 0; i < getCount(); i++) {
                fragments.add(new FinanceFormFragment());
            }
        }
        return fragments;
    }

    public void setFragments(ArrayList<FinanceFormFragment> fragments) {
        this.fragments = fragments;
    }

    public MonthlyFinancePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        FinanceFormFragment fragment = getFragments().get(position);
        return fragment;
    }

    @Override
    public int getCount() {
        // We need 4 gridviews for previous month, current month and next month,
        // and 1 extra fragment for fragment recycle
        return FinanceFragment.NUMBER_OF_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(
                ((FinanceFormFragment) getItem(position)).getMonth()
        );
    }
}
