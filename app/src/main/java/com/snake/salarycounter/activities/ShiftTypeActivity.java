package com.snake.salarycounter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialize.MaterializeBuilder;
import com.snake.salarycounter.R;
import com.snake.salarycounter.data.AbstractDataProvider;
import com.snake.salarycounter.fragments.RecyclerListViewFragment;
import com.snake.salarycounter.fragments.ShiftTypesDataProviderFragment;
import com.snake.salarycounter.models.ShiftType;

public class ShiftTypeActivity extends AppCompatActivity {

    public static final int NEW_SHIFT_TYPE = -10;
    private int currentBackgroundColor = 0xffffffff;

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ShiftTypesDataProviderFragment(), FRAGMENT_TAG_DATA_PROVIDER)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecyclerListViewFragment(), FRAGMENT_LIST_VIEW)
                    .commit();
        }

        ////////////////////////////////////////////////////////////////////////////////////////

        new MaterializeBuilder()
                .withActivity(this)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new ShiftType("Test", 0xffff0000).save();
                getDataAdapter().notifyDataSetChanged();*/

                Intent intent = new Intent();
                intent.setClass(ShiftTypeActivity.this, ShowShiftTypeActivity.class);
                intent.putExtra("shift_type_position", NEW_SHIFT_TYPE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataAdapter().notifyDataSetChanged();
    }

    /**
     * This method will be called when a list item is removed
     *
     * @param position The position of the item within data set
     */
    public void onItemRemoved(int position) {
        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                R.string.snack_bar_text_item_removed,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.action_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onItemUndoActionClicked();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snackbar_action_color_done));
        snackbar.show();
    }

    /**
     * This method will be called when a list item is pinned
     *
     * @param position The position of the item within data set
     */
    public void onItemPinned(int position) {

    }

    /**
     * This method will be called when a list item is clicked
     *
     * @param position The position of the item within data set
     */
    public void onItemClicked(int position) {
        /*final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        AbstractDataProvider.Data data = getDataProvider().getItem(position);

        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            ((RecyclerListViewFragment) fragment).notifyItemChanged(position);
        }*/

        //Toast.makeText(this, "Clicked: " + String.valueOf(position), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(this, ShowShiftTypeActivity.class);
        intent.putExtra("shift_type_position", position);
        startActivity(intent);
    }

    private void onItemUndoActionClicked() {
        int position = getDataProvider().undoLastRemoval();
        if (position >= 0) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
            ((RecyclerListViewFragment) fragment).notifyItemInserted(position);
        }
    }

    public AbstractDataProvider getDataProvider() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ShiftTypesDataProviderFragment) fragment).getDataProvider();
    }

    public RecyclerView.Adapter getDataAdapter() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_LIST_VIEW);
        return ((RecyclerListViewFragment) fragment).getDataAdapter();
    }
}
