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
import com.snake.salarycounter.fragments.ShiftType.ListShiftTypeFragment;
import com.snake.salarycounter.fragments.ShiftType.ShiftTypeMoneyFragment;
import com.snake.salarycounter.fragments.ShiftType.ShiftTypeNameFragment;
import com.snake.salarycounter.fragments.ShiftType.ShiftTypeTimeFragment;
import com.snake.salarycounter.models.ShiftType;

public class ShowShiftTypeActivity extends AppCompatActivity{

    private int _position;
    private long _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shift_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new MaterializeBuilder()
                .withActivity(this)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        _id = getIntent().getLongExtra("shift_type_id", -1);
        if(_id == ListShiftTypeFragment.NEW_SHIFT_TYPE){
            //_position = ShiftType.allShiftTypes().size() ;
            _id = new ShiftType("").save();
        }
        /*else{
            _id = ShiftType.getByPosition(_position).getId();
        }*/

        /*ViewPagerItemAdapter adapter = new ViewPagerItemAdapter(ViewPagerItems.with(this)
                .add("Title A", R.layout.partial_show_shift_type)
                //.add("Title B", R.layout.activity_calendar)
                .add("Title C", R.layout.activity_locked_scrolling)
                .create());*/

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                //.add(R.string.title, PageFragment.class)
                .add(R.string.shift_type_main_settings, ShiftTypeNameFragment.class, new Bundler().putLong("_id", _id).get())
                .add(R.string.shift_type_time_settings, ShiftTypeTimeFragment.class, new Bundler().putLong("_id", _id).get())
                .add(R.string.shift_type_money_settings, ShiftTypeMoneyFragment.class, new Bundler().putLong("_id", _id).get())
                //.add("title", PageFragment.class)
                .create()
        );

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager(viewPager);
    }
}
