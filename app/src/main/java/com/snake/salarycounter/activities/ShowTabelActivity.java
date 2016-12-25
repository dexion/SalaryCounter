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
import com.snake.salarycounter.fragments.Tabel.ListTabelFragment;
import com.snake.salarycounter.fragments.Tabel.TabelNameFragment;
import com.snake.salarycounter.models.Tabel;

import org.joda.time.DateTime;

public class ShowTabelActivity extends AppCompatActivity {

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

        _id = getIntent().getLongExtra("tabel_id", -1);
        if (_id == ListTabelFragment.NEW_TABEL) {
            _id = new Tabel(DateTime.now(), 164).save();
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.tabel_main_settings, TabelNameFragment.class, new Bundler().putLong("_id", _id).get())
                .create()
        );

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

        viewPagerTab.setViewPager(viewPager);
    }
}
