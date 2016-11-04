package com.snake.salarycounter.fragments.ShiftType;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimeRangePickerDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.snake.salarycounter.R;
import com.snake.salarycounter.models.ShiftType;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShiftTypeTimeFragment extends Fragment {

    private static final String ARG_PARAM1 = "_id";

    private long mId;

    private ShiftType st;
    private DateTime currentDayStart;
    private DateTime currentDayEnd;
    private DateTime currentDinnerStart;
    private DateTime currentDinnerEnd;

    DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

    @BindView(R.id.shift_type_time_day) Button day;
    @OnClick(R.id.shift_type_time_day) void setDayStartEnd() {
        TimeRangePickerDialog tpd = TimeRangePickerDialog.newInstance(
                new TimeRangePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                        currentDayStart = formatter.parseDateTime((hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay) + ":" + (minute < 10 ? "0" + minute : "" + minute));
                        currentDayEnd = formatter.parseDateTime((hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd) + ":" + (minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd));

                        if (currentDayStart.isBefore(currentDayEnd)) {
                            st.dayStart = currentDayStart;
                            st.dayEnd = currentDayEnd;
                            st.dayDuration = calcDayDuration();
                            st.save();

                            day.setText(String.format(getString(R.string.time_from_to), formatter.print(currentDayStart), formatter.print(currentDayEnd)));
                            duration.setText(String.format(getString(R.string.time_duration), new Period(calcDayDuration()).getHours(), new Period(calcDayDuration()).getMinutes()));
                        }
                        else
                        {
                            SuperToast.create(getActivity(), getString(R.string.start_cannot_after_end), SuperToast.Duration.LONG, Style.getStyle(Style.ORANGE, SuperToast.Animations.FLYIN)).show();
                            day.setText(getString(R.string.incorrect_value));
                        }
                    }
                },
                currentDayStart.getHourOfDay(),
                currentDayStart.getMinuteOfHour(),
                currentDayEnd.getHourOfDay(),
                currentDayEnd.getMinuteOfHour(),
                true
        );
        tpd.show(getActivity().getFragmentManager(), "DayStartEnd");
    }

    @BindView(R.id.shift_type_time_dinner) Button dinner;
    @OnClick(R.id.shift_type_time_dinner) void setDinnerStartEnd() {
        TimeRangePickerDialog tpd = TimeRangePickerDialog.newInstance(
                new TimeRangePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
                        currentDinnerStart = formatter.parseDateTime((hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay) + ":" + (minute < 10 ? "0" + minute : "" + minute));
                        currentDinnerEnd = formatter.parseDateTime((hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd) + ":" + (minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd));

                        if (currentDinnerStart.isBefore(currentDinnerEnd)) {
                            st.dinnerStart = currentDinnerStart;
                            st.dinnerEnd = currentDinnerEnd;
                            st.dayDuration = calcDayDuration();
                            st.save();

                            dinner.setText(String.format(getString(R.string.time_from_to), formatter.print(currentDinnerStart), formatter.print(currentDinnerEnd)));
                            duration.setText(String.format(getString(R.string.time_duration), new Period(calcDayDuration()).getHours(), new Period(calcDayDuration()).getMinutes()));
                        }
                        else
                        {
                            SuperToast.create(getActivity(), getString(R.string.start_cannot_after_end), SuperToast.Duration.LONG, Style.getStyle(Style.ORANGE, SuperToast.Animations.FLYIN)).show();
                            dinner.setText(getString(R.string.incorrect_value));
                        }
                    }
                },
                currentDinnerStart.getHourOfDay(),
                currentDinnerStart.getMinuteOfHour(),
                currentDinnerEnd.getHourOfDay(),
                currentDinnerEnd.getMinuteOfHour(),
                true
        );
        tpd.show(getActivity().getFragmentManager(), "DinnerStartEnd");
    }

    @BindView(R.id.shift_type_time_duration) TextView duration;

    public ShiftTypeTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
            st = ShiftType.getById(mId);

            currentDayStart = st.dayStart;
            currentDayEnd = st.dayEnd;
            currentDinnerStart = st.dinnerStart;
            currentDinnerEnd = st.dinnerEnd;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shift_type_time, container, false);
        ButterKnife.bind(this, v);

        day.setText(String.format(getString(R.string.time_from_to), formatter.print(currentDayStart), formatter.print(currentDayEnd)));
        dinner.setText(String.format(getString(R.string.time_from_to), formatter.print(currentDinnerStart), formatter.print(currentDinnerEnd)));
        duration.setText(String.format(getString(R.string.time_duration), new Period(calcDayDuration()).getHours(), new Period(calcDayDuration()).getMinutes()));
        return v;
    }

    @NonNull
    private Duration calcDayDuration(){
        return (new Duration(currentDayStart, currentDayEnd)).minus(new Duration(currentDinnerStart, currentDinnerEnd));
    }
}
