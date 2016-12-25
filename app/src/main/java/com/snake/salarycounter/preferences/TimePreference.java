package com.snake.salarycounter.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Locale;

public class TimePreference extends DialogPreference {

    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker timePicker = null;

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        if (pieces.length > 1) {
            return (Integer.parseInt(pieces[1]));
        } else {
            return (0);
        }
    }

    public static int getHourFor(SharedPreferences preferences, String field) {
        return getHour(preferences.getString(field, "08:00"));
    }

    public static int getMinuteFor(SharedPreferences preferences, String field) {
        return getMinute(preferences.getString(field, "08:00"));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        //setPositiveButtonText("Set");
        //setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(true);

        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = timePicker.getCurrentHour();
            lastMinute = timePicker.getCurrentMinute();

            String time = String.format(Locale.getDefault(), "%02d:%02d", lastHour, lastMinute); // String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);

            if (callChangeListener(time)) {
                persistString(time);
                setSummary(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time = null;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);

        persistString(time);
        setSummary(time);
    }
}
