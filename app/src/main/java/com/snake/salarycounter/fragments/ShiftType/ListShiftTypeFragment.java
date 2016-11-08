package com.snake.salarycounter.fragments.ShiftType;


import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.snake.salarycounter.R;
import com.snake.salarycounter.activities.MainActivity;
import com.snake.salarycounter.activities.ShowShiftTypeActivity;
import com.snake.salarycounter.items.GenericShiftTypeItem;
import com.snake.salarycounter.models.ShiftType;

import java.util.ArrayList;

public class ListShiftTypeFragment extends Fragment implements
        ItemTouchCallback,
        ItemAdapter.ItemFilterListener,
        SimpleSwipeCallback.ItemSwipeCallback,
        FastAdapter.OnClickListener<GenericShiftTypeItem>{

    public static final long NEW_SHIFT_TYPE = -10;
    protected ArrayList<ShiftType> models;
    private LoadTask mLoadTask;

    //save our FastAdapter
    private FastAdapter<GenericShiftTypeItem> fastAdapter;
    GenericItemAdapter<ShiftType, GenericShiftTypeItem> itemAdapter;

    //drag & drop
    private SimpleSwipeDragCallback touchCallback;
    private ItemTouchHelper touchHelper;

    View rootView;
    public ListShiftTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoadTask = new LoadTask();
        mLoadTask.execute();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list_recycler_view, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (null != toolbar) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_shift_type);
            ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),  ((MainActivity) getActivity()).getDrawer().getDrawerLayout(), toolbar, R.string.drawer_open, R.string.drawer_close);
            mActionBarDrawerToggle.syncState();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

            ((MainActivity) getActivity()).getDrawer().setActionBarDrawerToggle(mActionBarDrawerToggle);
        }

        //create our FastAdapter which will manage everything
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(false);

        itemAdapter = new GenericItemAdapter<>(GenericShiftTypeItem.class, ShiftType.class);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);

        rv.setAdapter(itemAdapter.wrap(fastAdapter));

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new SlideDownAlphaAnimator());

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

        Drawable leaveBehindDrawableLeft = new IconicsDrawable(getContext())
                .icon(CommunityMaterial.Icon.cmd_delete)
                .color(Color.WHITE)
                .sizeDp(24);

        //add drag&drop and swipe for item
        touchCallback = new SimpleSwipeDragCallback(
                this,
                this,
                leaveBehindDrawableLeft,
                ItemTouchHelper.LEFT,
                ContextCompat.getColor(getContext(), R.color.md_red_900)
        );
        //.withBackgroundSwipeRight(ContextCompat.getColor(this, R.color.md_blue_900))
        //.withLeaveBehindSwipeRight(leaveBehindDrawableRight);
        touchHelper = new ItemTouchHelper(touchCallback); // Create ItemTouchHelper and pass with parameter the SimpleDragCallback
        touchHelper.attachToRecyclerView(rv); // Attach ItemTouchHelper to RecyclerView

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(rv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getContext(), ShowShiftTypeActivity.class);
                intent.putExtra("shift_type_id", NEW_SHIFT_TYPE);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //load the data (only possible if we were able to get the Arguments
        if (view.getContext() != null) {
            mLoadTask = new LoadTask();
            mLoadTask.execute();
        }
    }

    @Override
    public void onDestroyView() {
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
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

        super.onCreateOptionsMenu(menu, inflater);
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

        //fastAdapter.notifyAdapterItemMoved(oldPosition, newPosition);
        itemAdapter.moveModel(oldPosition, newPosition);

        return true;
    }

    @Override
    public void itemsFiltered() {
        Toast.makeText(getActivity(), "filtered items count: " + fastAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onClick(View v, IAdapter<GenericShiftTypeItem> adapter, GenericShiftTypeItem item, int position) {
        //Toast.makeText(ListShiftTypeActivity.this, "clicked : " + item.getModel().name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setClass(getActivity(), ShowShiftTypeActivity.class);
        intent.putExtra("shift_type_id", item.getModel().getId());
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

        final View rv = rootView.findViewById(R.id.rv);

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

                            SuperToast.create(getActivity(), getString(R.string.cannot_delete), SuperToast.Duration.MEDIUM, Style.getStyle(Style.ORANGE)).show();
                            rv.removeCallbacks(this);
                        }
                    }
                    catch(SQLiteConstraintException sqlExc){
                        SuperToast.create(getActivity(), getString(R.string.error_deleting), SuperToast.Duration.MEDIUM, Style.getStyle(Style.RED)).show();
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

    public class LoadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            models = ShiftType.allShiftTypes();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(null != itemAdapter && null != fastAdapter) {
                itemAdapter.clear();
                itemAdapter.addModel(models);
                fastAdapter.notifyAdapterDataSetChanged();
            }
            super.onPostExecute(s);
        }
    }

}
