package com.snake.salarycounter.activities;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.util.ReflectionUtils;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.snake.salarycounter.R;

public class CustomPinActivity extends AppLockActivity {

    private static final int MAX_ATTEMPTS = 3;

    @Override
    public void showForgotDialog() {
        final Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        new MaterialDialog.Builder(this)
                .title(R.string.activity_dialog_title)
                .content(R.string.activity_dialog_content)
                .positiveText(R.string.activity_dialog_accept)
                .negativeText(R.string.activity_dialog_decline)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                })
                .show();
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

    @Override
    public void onStop() {
        Log.d(TAG, "onStop " + "CustomPinActivity");

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong("LAST_ACTIVE_MILLIS", 0);
        editor.apply();

        super.onStop();
    }
}
