package com.snake.salarycounter.activities.ShiftType;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeDragCallback;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;
import com.snake.salarycounter.R;
import com.snake.salarycounter.items.GenericShiftTypeItem;
import com.snake.salarycounter.models.ShiftType;

public class ListShiftTypeActivity extends AppCompatActivity
        implements
            ItemTouchCallback,
            ItemAdapter.ItemFilterListener,
            SimpleSwipeCallback.ItemSwipeCallback,
            FastAdapter.OnClickListener<GenericShiftTypeItem> {

    public static final int NEW_SHIFT_TYPE = -10;

    //save our FastAdapter
    private FastAdapter<GenericShiftTypeItem> fastAdapter;
    GenericItemAdapter<ShiftType, GenericShiftTypeItem> itemAdapter;

    //drag & drop
    private SimpleSwipeDragCallback touchCallback;
    private ItemTouchHelper touchHelper;

    @Override
    protected void onResume() {
        super.onResume();
        itemAdapter.clear();
        itemAdapter.addModel(ShiftType.allShiftTypes());
        fastAdapter.notifyAdapterDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_shift_type);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////////////////////////////////////////////////////////////////////////////////////////

        new MaterializeBuilder()
                .withActivity(this)
                .withTranslucentStatusBarProgrammatically(true)
                .build();

        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(false);

        itemAdapter = new GenericItemAdapter<>(GenericShiftTypeItem.class, ShiftType.class);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());

        itemAdapter.addModel(ShiftType.allShiftTypes());

        //configure the itemAdapter
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<GenericShiftTypeItem>() {
            @Override
            public boolean filter(GenericShiftTypeItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.getModel().name.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

        fastAdapter.withOnClickListener(this);

        Drawable leaveBehindDrawableLeft = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_delete)
                .color(Color.WHITE)
                .sizeDp(24);

        //add drag&drop and swipe for item
        touchCallback = new SimpleSwipeDragCallback(
                this,
                this,
                leaveBehindDrawableLeft,
                ItemTouchHelper.LEFT,
                ContextCompat.getColor(this, R.color.md_red_900)
        );
                //.withBackgroundSwipeRight(ContextCompat.getColor(this, R.color.md_blue_900))
                //.withLeaveBehindSwipeRight(leaveBehindDrawableRight);
        touchHelper = new ItemTouchHelper(touchCallback); // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv); // Attach ItemTouchHelper to RecyclerView

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ListShiftTypeActivity.this, ShowShiftTypeActivity.class);
                intent.putExtra("shift_type_position", NEW_SHIFT_TYPE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    touchCallback.setIsDragEnabled(false);
                    itemAdapter.filter(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    itemAdapter.filter(s);
                    touchCallback.setIsDragEnabled(TextUtils.isEmpty(s));
                    return true;
                }
            });
        } else {
            menu.findItem(R.id.search).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean itemTouchOnMove(int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return false;
        }

        ShiftType st1 = ShiftType.getByPosition(oldPosition);

        if(oldPosition < newPosition)
        {
            ShiftType.reorderTop(newPosition, st1.weight);
        }
        else
        {
            ShiftType.reorderBottom(newPosition, st1.weight);
        }


        st1.weight = newPosition;
        st1.save();

        fastAdapter.notifyAdapterItemMoved(oldPosition, newPosition);
        return true;
    }

    @Override
    public void itemsFiltered() {
        Toast.makeText(ListShiftTypeActivity.this, "filtered items count: " + fastAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onClick(View v, IAdapter<GenericShiftTypeItem> adapter, GenericShiftTypeItem item, int position) {
        //Toast.makeText(ListShiftTypeActivity.this, "clicked : " + item.getModel().name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(this, ShowShiftTypeActivity.class);
        intent.putExtra("shift_type_position", position);
        startActivity(intent);
        return false;
    }

    @Override
    public void itemSwiped(int position, int direction) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        final GenericShiftTypeItem item = fastAdapter.getItem(position);
        item.setSwipedDirection(direction);

        final View rv = findViewById(R.id.rv);

        // This can vary depending on direction but remove & archive simulated here both results in
        // removal from list
        final Runnable removeRunnable = new Runnable() {
            @Override
            public void run() {
                item.setSwipedAction(null);
                int position = itemAdapter.getAdapterPosition(item);
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        if (ShiftType.getByPosition(position).canDelete(position)) {
                            ShiftType.getByPosition(position).delete();
                            ShiftType.reorderAfterDeletion(position);
                            itemAdapter.remove(position);
                        }
                        else
                        {
                            item.setSwipedDirection(0);

                            SuperToast.create(ListShiftTypeActivity.this, getString(R.string.cannot_delete), SuperToast.Duration.MEDIUM, Style.getStyle(Style.ORANGE)).show();
                            rv.removeCallbacks(this);
                        }
                    }
                    catch(SQLiteConstraintException sqlExc){
                        SuperToast.create(ListShiftTypeActivity.this, getString(R.string.error_deleting), SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED)).show();
                    }
                }
            }
        };

        rv.postDelayed(removeRunnable, 1500);

        item.setSwipedAction(new Runnable() {
            @Override
            public void run() {
                rv.removeCallbacks(removeRunnable);
                item.setSwipedDirection(0);
                int position = itemAdapter.getAdapterPosition(item);
                if (position != RecyclerView.NO_POSITION) {
                    itemAdapter.notifyItemChanged(position);
                }
            }
        });

        itemAdapter.notifyItemChanged(position);

        //TODO can this above be made more generic, along with the support in the item?
    }
}
