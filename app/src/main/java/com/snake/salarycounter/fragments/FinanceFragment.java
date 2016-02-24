package com.snake.salarycounter.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.snake.salarycounter.R;
import com.snake.salarycounter.adapters.InfinitePagerAdapter;
import com.snake.salarycounter.adapters.MonthlyFinancePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinanceFragment extends Fragment {

    public static int NUMBER_OF_PAGES = 4;

    public FinanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_finance, container, false);

        /*FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add("R.string.titleA", FinanceFormFragment.class)
                .add("R.string.titleB", FinanceFormFragment.class)
                .add("R.string.titleC", FinanceFormFragment.class)
                .add("R.string.titleD", FinanceFormFragment.class)
                .add("R.string.titleE", FinanceFormFragment.class)
                .create());*/

        final MonthlyFinancePagerAdapter adapter = new MonthlyFinancePagerAdapter(
                getChildFragmentManager());

        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(adapter);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(wrappedAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int virtual_position = position % adapter.getCount();
                Log.w("ViewPager", "Page selected: " + String.valueOf(virtual_position));

                FinanceFormFragment c_fff = (FinanceFormFragment) adapter.getItem(virtual_position);
                FinanceFormFragment pr_fff = (FinanceFormFragment) adapter.getItem(virtual_position - 1);
                FinanceFormFragment nx_fff = (FinanceFormFragment) adapter.getItem(virtual_position + 1);
                FinanceFormFragment nx2_fff = (FinanceFormFragment) adapter.getItem(virtual_position + 2);
                pr_fff.setMonth(c_fff.getMonth() - 1);
                nx_fff.setMonth(c_fff.getMonth() + 1);
                nx2_fff.setMonth(c_fff.getMonth() + 2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //SmartTabLayout viewPagerTab = (SmartTabLayout) v.findViewById(R.id.viewpagertab);
        //viewPagerTab.setViewPager(viewPager);

        //viewPagerTab.setOnPageChangeListener();

        viewPager.setCurrentItem(2);

        int position = FragmentPagerItem.getPosition(getArguments());

        return v;
    }
}
