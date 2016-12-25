package com.snake.salarycounter.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialize.MaterializeBuilder;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.snake.salarycounter.R;
import com.snake.salarycounter.fragments.FinanceCondition.FinanceConditionMoneyFragment;
import com.snake.salarycounter.fragments.FinanceCondition.FinanceConditionNameFragment;
import com.snake.salarycounter.fragments.FinanceCondition.ListFinanceConditionFragment;
import com.snake.salarycounter.models.FinanceCondition;

import org.joda.time.DateTime;

public class ShowFinanceConditionActivity extends AppCompatActivity {

    private long _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tabel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new MaterializeBuilder()
                .withActivity(this)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        _id = getIntent().getLongExtra("finance_condition_id", -1);
        if (_id == ListFinanceConditionFragment.NEW_FINANCE_CONDITION) {
            if (FinanceCondition.allFinanceConditions().size() < 1) {
                _id = new FinanceCondition(DateTime.now()).save();
            } else {
                _id = FinanceCondition.getLast().copy();
            }
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.finance_condition_main_settings, FinanceConditionNameFragment.class, new Bundler().putLong("_id", _id).get())
                .add(R.string.finance_condition_money_settings, FinanceConditionMoneyFragment.class, new Bundler().putLong("_id", _id).get())
                .create()
        );

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager(viewPager);
    }
}
