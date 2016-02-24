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
import com.snake.salarycounter.fragments.ShowShiftType.ShiftTypeNameFragment;
import com.snake.salarycounter.models.ShiftType;

public class ShowShiftTypeActivity extends AppCompatActivity{

    private int _position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shift_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new MaterializeBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        _position = getIntent().getIntExtra("shift_type_position", -1);
        if(_position == ShiftTypeActivity.NEW_SHIFT_TYPE){
            _position = ShiftType.allShiftTypes().size() ;
            new ShiftType("", 0xffff0000, _position).save();
        }

        /*ViewPagerItemAdapter adapter = new ViewPagerItemAdapter(ViewPagerItems.with(this)
                .add("Title A", R.layout.partial_show_shift_type)
                //.add("Title B", R.layout.activity_calendar)
                .add("Title C", R.layout.activity_locked_scrolling)
                .create());*/

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                //.add(R.string.title, PageFragment.class)
                .add(R.string.shift_type_main_settings, ShiftTypeNameFragment.class, new Bundler().putInt("_position", _position).get())
                //.add("title", PageFragment.class)
                .create()
        );

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager(viewPager);
    }
}
