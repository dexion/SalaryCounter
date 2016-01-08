package com.snake.salarycounter.fragments;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

import hirondelle.date4j.DateTime;

public class CalendarFragment extends Fragment{

    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
    private CaldroidListener listener = null;
    private boolean isCalendarEditable = true;

    public CalendarFragment() {
    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -7);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            caldroidFragment.setBackgroundResourceForDate(R.color.blue,
                    blueDate);
            caldroidFragment.setBackgroundResourceForDate(R.color.green,
                    greenDate);
            caldroidFragment.setTextColorForDate(R.color.white, blueDate);
            caldroidFragment.setTextColorForDate(R.color.white, greenDate);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        if(ShiftType.allShiftTypes().size() < 1) {
            Snackbar.make(v, R.string.must_create_one_shift_type, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_close, null).show();
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
                Toast.makeText(getActivity(), formatter.format(mDate),
                        Toast.LENGTH_SHORT).show();

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

                ShapeDrawable shapeDrawable= new ShapeDrawable();
                shapeDrawable.setShape(new RectShape());
                shapeDrawable.getPaint().setColor(d.shiftType.color);

                caldroidFragment.setBackgroundResourceForDate(shapeDrawable,
                        mDate);
                caldroidFragment.refreshView();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getActivity(), text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getActivity(),
                        "Long click " + formatter.format(date),
                        Toast.LENGTH_SHORT).show();
                caldroidFragment.clearBackgroundResourceForDate(date);
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    Toast.makeText(getActivity(),
                            "Caldroid view is created", Toast.LENGTH_SHORT)
                            .show();
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
        //t.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        t.commit();

        final TextView textView = (TextView) v.findViewById(R.id.textview);

        final Button customizeButton = (Button) v.findViewById(R.id.customize_button);

        // Customize the calendar
        customizeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (undo) {
                    customizeButton.setText("Customize");
                    textView.setText("");

                    // Reset calendar
                    caldroidFragment.clearDisableDates();
                    caldroidFragment.clearSelectedDates();
                    caldroidFragment.setMinDate(null);
                    caldroidFragment.setMaxDate(null);
                    caldroidFragment.setShowNavigationArrows(true);
                    caldroidFragment.setEnableSwipe(true);
                    caldroidFragment.refreshView();
                    undo = false;
                    return;
                }

                // Else
                undo = true;
                customizeButton.setText("Undo");
                Calendar cal = Calendar.getInstance();

                // Min date is last 7 days
                cal.add(Calendar.DATE, -7);
                Date minDate = cal.getTime();

                // Max date is next 7 days
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 14);
                Date maxDate = cal.getTime();

                // Set selected dates
                // From Date
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 2);
                Date fromDate = cal.getTime();

                // To Date
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 3);
                Date toDate = cal.getTime();

                // Set disabled dates
                ArrayList<Date> disabledDates = new ArrayList<>();
                for (int i = 5; i < 8; i++) {
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, i);
                    disabledDates.add(cal.getTime());
                }

                // Customize
                caldroidFragment.setMinDate(minDate);
                caldroidFragment.setMaxDate(maxDate);
                caldroidFragment.setDisableDates(disabledDates);
                caldroidFragment.setSelectedDates(fromDate, toDate);
                caldroidFragment.setShowNavigationArrows(false);
                caldroidFragment.setEnableSwipe(false);

                caldroidFragment.refreshView();

                // Move to date
                // cal = Calendar.getInstance();
                // cal.add(Calendar.MONTH, 12);
                // caldroidFragment.moveToDate(cal.getTime());

                String text = "Today: " + formatter.format(new Date()) + "\n";
                text += "Min Date: " + formatter.format(minDate) + "\n";
                text += "Max Date: " + formatter.format(maxDate) + "\n";
                text += "Select From Date: " + formatter.format(fromDate)
                        + "\n";
                text += "Select To Date: " + formatter.format(toDate) + "\n";
                for (Date date : disabledDates) {
                    text += "Disabled Date: " + formatter.format(date) + "\n";
                }

                textView.setText(text);
            }
        });

        Button showDialogButton = (Button) v.findViewById(R.id.show_dialog_button);

        final Bundle state = savedInstanceState;
        showDialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Setup caldroid to use as dialog
                dialogCaldroidFragment = new CaldroidFragment();
                dialogCaldroidFragment.setCaldroidListener(listener);

                // If activity is recovered from rotation
                final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
                if (state != null) {
                    dialogCaldroidFragment.restoreDialogStatesFromKey(
                            getFragmentManager(), state,
                            "DIALOG_CALDROID_SAVED_STATE", dialogTag);
                    Bundle args = dialogCaldroidFragment.getArguments();
                    if (args == null) {
                        args = new Bundle();
                        dialogCaldroidFragment.setArguments(args);
                    }
                } else {
                    // Setup arguments
                    Bundle bundle = new Bundle();
                    // Setup dialogTitle
                    dialogCaldroidFragment.setArguments(bundle);
                }

                dialogCaldroidFragment.show(getFragmentManager(),
                        dialogTag);
            }
        });

        final Button allDaysButton = (Button) v.findViewById(R.id.all_days_button);
        allDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Day> allDays = Day.allDays();
            }
        });
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

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }
}
