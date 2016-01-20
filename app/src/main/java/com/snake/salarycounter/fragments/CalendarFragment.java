package com.snake.salarycounter.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.snake.salarycounter.R;
import com.snake.salarycounter.models.Day;
import com.snake.salarycounter.models.ShiftType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class CalendarFragment extends Fragment {

    private CaldroidFragment caldroidFragment;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
    private CaldroidListener listener = null;
    private boolean isCalendarEditable = true;

    public CalendarFragment() {
    }

    private void setCustomResourceForDates() {
        for(Day d : Day.allDays()){
            caldroidFragment.setBackgroundResourceForDate(d.shiftType.color, d.date.toDate());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        if(ShiftType.allShiftTypes().size() < 1) {
            Snackbar.make(v, R.string.must_create_one_shift_type, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_close, null)
                    .show();
            isCalendarEditable = false;
        }

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
        // caldroidFragment = new CaldroidSampleCustomFragment();

        // Setup arguments

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

            // Uncomment this to customize startDayOfWeek
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            //args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }

        setCustomResourceForDates();

        // Setup listener
        listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date mDate, View view) {
                //Toast.makeText(getActivity(), formatter.format(mDate),
                //        Toast.LENGTH_SHORT).show();

                Day d = Day.getByDate(mDate);

                if(d != null)
                {
                    int tPostition;
                    if(d.shiftType.weight + 1 == ShiftType.allShiftTypes().size())
                    {
                        tPostition = 0;
                    }
                    else
                    {
                        tPostition = d.shiftType.weight + 1;
                    }
                    d.shiftType = ShiftType.getByPosition(tPostition);
                    d.save();
                }
                else
                {
                    d = new Day(mDate);
                    d.shiftType = ShiftType.getByPosition(0);
                    d.save();
                }

                caldroidFragment.setBackgroundResourceForDate(d.shiftType.color, mDate);
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

                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(),
                        res.getString(R.string.dialog_delete),
                        res.getString(R.string.activity_dialog_accept));
                builder.content(res.getString(R.string.realy_clear));
                builder.negativeText(res.getString(R.string.activity_dialog_decline));

                //Set theme
                builder.darkTheme(false);
                builder.typeface(Typeface.SANS_SERIF);
                builder.positiveColor(res.getColor(R.color.light_blue_500)); // int res, or int colorRes parameter versions available as well.
                builder.negativeColor(res.getColor(R.color.light_blue_500));
                builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
                builder.titleAlignment(BaseDialog.Alignment.CENTER);
                builder.buttonAlignment(BaseDialog.Alignment.CENTER);
                builder.setButtonStacking(false);

                //Set text sizes
                builder.titleTextSize((int) res.getDimension(R.dimen.activity_dialog_title_size));
                builder.contentTextSize((int) res.getDimension(R.dimen.activity_dialog_content_size));
                builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
                builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

                //Build the dialog.
                CustomDialog customDialog = builder.build();
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.restart_needed), Toast.LENGTH_LONG).show();
                        Day d = Day.getByDate(mDate);
                        d.delete();

                        caldroidFragment.setBackgroundResourceForDate(0, mDate);
                        caldroidFragment.refreshView();
                        caldroidFragment.clearBackgroundResourceForDate(mDate);
                        caldroidFragment.refreshView();
                    }

                    @Override
                    public void onCancelClick() {
                        //Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });

                // Show the dialog.
                customDialog.show();
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

        // Setup Caldroid
        if(isCalendarEditable)
        {
            caldroidFragment.setCaldroidListener(listener);
        }

        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.setCustomAnimations(R.anim.slide_down, R.anim.slide_down);
        t.commit();

        return v;
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }
}
