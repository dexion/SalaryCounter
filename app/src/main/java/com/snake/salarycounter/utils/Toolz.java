package com.snake.salarycounter.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Toolz {
    public static String money(Object value){
        NumberFormat numberFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault());
        return numberFormat.format(value);
    }
}
