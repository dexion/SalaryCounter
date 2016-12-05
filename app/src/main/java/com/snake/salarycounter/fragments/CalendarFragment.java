package com.snake.salarycounter.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.snake.salarycounter.MyLogic;
import com.snake.salarycounter.R;
import com.snake.salarycounter.activities.MainActivity;
import com.snake.salarycounter.events.SwitchEvent;
import com.snake.salarycounter.models.Day;
import com.snake.salarycounter.models.ShiftType;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class CalendarFragment extends Fragment {

    private CaldroidCustomFragment caldroidFragment;
    private CaldroidListener caldroidEditListener = null;
    private CaldroidListener caldroidShowListener = null;
    private boolean isCalendarEditable = false;

    @BindView(R.id.daily_payslip)
    LinearLayout llDailyPayslip;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public CalendarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, rootView);

        if (null != toolbar) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_calendar);
            ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).getDrawer().getDrawerLayout(), toolbar, R.string.drawer_open, R.string.drawer_close);
            mActionBarDrawerToggle.syncState();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

            ((MainActivity) getActivity()).getDrawer().setActionBarDrawerToggle(mActionBarDrawerToggle);
        }

        EventBus.getDefault().register(this);

        if (ShiftType.allShiftTypes().size() < 1) {
            Snackbar.make(rootView, R.string.must_create_one_shift_type, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_close, null)
                    .show();
            isCalendarEditable = false;
        }
        else {
            isCalendarEditable = true;
        }

        caldroidFragment = new CaldroidCustomFragment();

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);

            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        // Setup caldroidEditListener
        caldroidEditListener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date mDate, View view) {
                //Toast.makeText(getActivity(), formatter.format(mDate),
                //        Toast.LENGTH_SHORT).show();

                Day d = Day.getByDate(mDate);

                if (d != null) {
                    int tPostition;
                    if (d.shiftType.weight + 1 == ShiftType.allShiftTypes().size()) {
                        tPostition = 0;
                    } else {
                        tPostition = d.shiftType.weight + 1;
                    }
                    d.shiftType = ShiftType.getByPosition(tPostition);
                    d.save();
                } else {
                    d = new Day(mDate);
                    d.shiftType = ShiftType.getByPosition(0);
                    d.save();
                }

                caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(d.shiftType.color), mDate);
                caldroidFragment.refreshView();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                //String text = "month: " + month + " year: " + year;
                //Toast.makeText(getActivity(), text,
                //        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(final Date mDate, View view) {
                //Toast.makeText(getActivity(),
                //        "Long click " + formatter.format(mDate),
                //        Toast.LENGTH_SHORT).show();
                final Resources res = getResources();

                new MaterialDialog.Builder(getContext())
                        .title(R.string.dialog_delete)
                        .content(R.string.realy_clear)
                        .positiveText(R.string.activity_dialog_accept)
                        .negativeText(R.string.activity_dialog_decline)
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Day d = Day.getByDate(mDate);
                                if (null != d) {
                                    d.delete();
                                    caldroidFragment.clearBackgroundDrawableForDate(mDate);
                                    caldroidFragment.refreshView();
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    //Toast.makeText(getActivity(),
                    //        "Caldroid view is created", Toast.LENGTH_SHORT)
                    //        .show();
                }
            }

        };

        caldroidShowListener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date mDate, View view) {
                MyLogic lgc = new MyLogic();
                Day d = Day.getByDate(mDate);
                if (d != null) {
                    getFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(llDailyPayslip.getId(),
                                PayslipFragment.newInstance(lgc.recalDay(new DateTime(mDate)), d.shiftType.getText(), true),
                                d.shiftType.getText())
                            .commitAllowingStateLoss();
                }
                else{
                    llDailyPayslip.removeAllViews();
                }
            }
        };

        caldroidFragment.setCaldroidListener(caldroidShowListener);

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.setCustomAnimations(R.anim.slide_down, R.anim.slide_down);
        t.commit();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate your Menu
        inflater.inflate(R.menu.menu_calendar, menu);

        MenuItem switchEdit = menu.findItem(R.id.switch_edit);
        switchEdit.setActionView(R.layout.menu_switch_layout);

        final SwitchCompat actionView = (SwitchCompat) switchEdit.getActionView().findViewById(R.id.switchForActionBar);
        actionView.setShowText(true);
        actionView.setThumbDrawable(
                new IconicsDrawable(getContext(), "gmd_remove_red_eye")
                        .color(Color.WHITE)
                        .sizeDp(32)
        );
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    actionView.setThumbDrawable(
                            new IconicsDrawable(getContext(), "gmd_edit")
                                    .color(Color.WHITE)
                                    .sizeDp(32)
                    );
                }
                else{
                    actionView.setThumbDrawable(
                            new IconicsDrawable(getContext(), "gmd_remove_red_eye")
                                    .color(Color.WHITE)
                                    .sizeDp(32)
                    );
                }
                EventBus.getDefault().post(new SwitchEvent(isChecked));
            }

        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    private void setCustomResourceForDates() {
        for(Day d : Day.allDays()){
            caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(d.shiftType.color), d.date.toDate());
        }
    }

    public void onEvent(SwitchEvent event){
        if(event.mChecked && isCalendarEditable){
            // можно редактировать
            caldroidFragment.setCaldroidListener(caldroidEditListener);
        }
        else{
            // нельзя редактировать календарь
            caldroidFragment.setCaldroidListener(caldroidShowListener);
        }
    }
}
