package com.snake.salarycounter.activities;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import android.support.design.widget.Snackbar;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.util.ReflectionUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.snake.salarycounter.CustomApplication;
import com.snake.salarycounter.R;
import com.snake.salarycounter.models.Category;
import com.snake.salarycounter.models.Item;
import com.snake.salarycounter.models.ShiftType;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class CustomPinActivity extends AppLockActivity {

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public void showForgotDialog() {
        final Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(this,
                res.getString(R.string.activity_dialog_title),
                res.getString(R.string.activity_dialog_accept));
        builder.content(res.getString(R.string.activity_dialog_content));
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.restart_needed), Toast.LENGTH_LONG).show();
                //SharedPreferences.edit().clear().commit();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CustomPinActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                //ActiveAndroid.execSQL("delete from "+ Cache.getTableInfo(ShiftType.class).getTableName()+";");
                //ActiveAndroid.execSQL("delete from sqlite_sequence where name='"+Cache.getTableInfo(ShiftType.class).getTableName()+"';");
                deleteDb();
                CustomPinActivity.this.finishAffinity();
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
    public void onPinFailure(int attempts) {
        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.attempts_remaining, MAX_ATTEMPTS - attempts), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.action_close), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
        if (attempts > MAX_ATTEMPTS - 1) {
            this.finishAffinity();
            return;
        }
    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    @Override
    public int getPinLength() {
        return super.getPinLength();//you can override this method to change the pin length from the default 4
    }

    public void deleteDb() {
        ActiveAndroid.dispose();

        String aaName = ReflectionUtils.getMetaData(getApplicationContext(), "AA_DB_NAME");

        if (aaName == null) {
            aaName = "Application.db";
        }

        deleteDatabase(aaName);
        //ActiveAndroid.initialize(this);
    }
}
