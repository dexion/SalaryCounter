package com.snake.salarycounter.fragments.Calendar;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.snake.salarycounter.R;

import java.util.Map;

import hirondelle.date4j.DateTime;

public class CaldroidCustomAdapter extends CaldroidGridAdapter {

    public CaldroidCustomAdapter(Context context, int month, int year,
                                       Map<String, Object> caldroidData,
                                       Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setCustomResources(DateTime dateTime, View backgroundView,
                                      TextView textView) {
        super.setCustomResources( dateTime, backgroundView, textView);

        // Set custom background resource
        Map<DateTime, Drawable> backgroundForDateTimeMap = (Map<DateTime, Drawable>) caldroidData
                .get(CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP);
        if (backgroundForDateTimeMap != null) {
            // Get background resource for the dateTime
            ColorDrawable drawable = (ColorDrawable) backgroundForDateTimeMap.get(dateTime);

            // Set it
            if (drawable != null) {
                backgroundView.setBackgroundResource(R.drawable.bg_circle);
                GradientDrawable gd = (GradientDrawable) backgroundView.getBackground();
                //gd.setColor(drawable.getColor());
                gd.setStroke(10, drawable.getColor());
            }
        }
    }
}
