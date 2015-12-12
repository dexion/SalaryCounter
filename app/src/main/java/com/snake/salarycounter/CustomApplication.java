package com.snake.salarycounter;

import android.app.Application;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //only required if you add a custom or generic font on your own
        /*Iconics.init(getApplicationContext());

        //register custom fonts like this (or also provide a font definition file)
        Iconics.registerFont(new CustomFont());*/
    }
}