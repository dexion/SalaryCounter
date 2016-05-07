package com.snake.salarycounter;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.crashlytics.android.Crashlytics;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.mikepenz.iconics.Iconics;
import com.parse.Parse;
import com.snake.salarycounter.activities.CustomPinActivity;
import com.snake.salarycounter.models.Day;
import com.snake.salarycounter.models.ShiftType;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        JodaTimeAndroid.init(this);
        ActiveAndroid.initialize(this);
        //LeakCanary.install(this);


        //only required if you add a custom or generic font on your own
        /*Iconics.init(getApplicationContext());

        //register custom fonts like this (or also provide a font definition file)
        Iconics.registerFont(new CustomFont());*/

        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.security_lock);
        //lockManager.getAppLock().setTimeout(20 * 1000);

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("UiMwAE3tZuBd68FGbNGVX3FrJonMR8l1RB10pP1G")
                .server("http://salarycounter.herokuapp.com/parse/")
                .build()
        );
    }
}
