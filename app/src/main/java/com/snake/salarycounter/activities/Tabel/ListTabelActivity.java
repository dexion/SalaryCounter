package com.snake.salarycounter.activities.Tabel;

import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.GenericItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;
import com.snake.salarycounter.R;
import com.snake.salarycounter.generic.GenericTabelItem;
import com.snake.salarycounter.models.Tabel;

import org.joda.time.DateTime;


public class ListTabelActivity extends AppCompatActivity
        implements
        ItemAdapter.ItemFilterListener,
        SimpleSwipeCallback.ItemSwipeCallback,
        FastAdapter.OnClickListener<GenericTabelItem>  {

    //save our FastAdapter
    private FastAdapter<GenericTabelItem> fastAdapter;
    GenericItemAdapter<Tabel, GenericTabelItem> itemAdapter;

    //drag & drop
    private SimpleSwipeCallback touchCallback;
    private ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tabel);

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

        itemAdapter = new GenericItemAdapter<>(GenericTabelItem.class, Tabel.class);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new SlideDownAlphaAnimator());

        itemAdapter.addModel(Tabel.allTabel());

        //configure the itemAdapter
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<GenericTabelItem>() {
            @Override
            public boolean filter(GenericTabelItem item, CharSequence constraint) {
                //return true if we should filter it out
                //return false to keep it
                return !item.getModel().getText().toLowerCase().contains(constraint.toString().toLowerCase());
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
        touchCallback = new SimpleSwipeCallback(
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
                /*Intent intent = new Intent();
                intent.setClass(ListTabelActivity.this, ShowTabelActivity.class);
                intent.putExtra("shift_type_position", NEW_TABEL);
                startActivity(intent);*/
                for(int i = 2012; i < 2016; i++)
                    for(int ii = 1; ii < 13; ii++)
                        new Tabel(DateTime.now().withYear(i).withMonthOfYear(ii), 164.25).save();
                SuperToast.create(ListTabelActivity.this, "Ok", SuperToast.Duration.MEDIUM, Style.getStyle(Style.BLUE)).show();
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
    public void itemsFiltered() {

    }

    @Override
    public void itemSwiped(int position, int direction) {
        // -- Option 1: Direct action --
        //do something when swiped such as: select, remove, update, ...:
        //A) fastItemAdapter.select(position);
        //B) fastItemAdapter.remove(position);
        //C) update item, set "read" if an email etc

        // -- Option 2: Delayed action --
        final GenericTabelItem item = fastAdapter.getItem(position);
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
                        if (Tabel.getByPosition(position).canDelete(position)) {
                            Tabel.getByPosition(position).delete();
                            itemAdapter.remove(position);
                        }
                        else
                        {
                            item.setSwipedDirection(0);

                            SuperToast.create(ListTabelActivity.this, getString(R.string.cannot_delete), SuperToast.Duration.MEDIUM, Style.getStyle(Style.ORANGE)).show();
                            rv.removeCallbacks(this);
                        }
                    }
                    catch(SQLiteConstraintException sqlExc){
                        SuperToast.create(ListTabelActivity.this, getString(R.string.error_deleting), SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED)).show();
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


    @Override
    public boolean onClick(View v, IAdapter<GenericTabelItem> adapter, GenericTabelItem item, int position) {
        return false;
    }
}
