package com.snake.salarycounter.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Toolz {
    public static String money(Object value){
        NumberFormat numberFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault());
        return numberFormat.format(value);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
            return bd.doubleValue();
        } catch (java.lang.NumberFormatException e) {
            //Crashlytics.logException(e);
            return 0;
        }
    }

    public static BigDecimal round(BigDecimal value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        try {
            value.setScale(places, BigDecimal.ROUND_HALF_UP);
            return value;
        } catch (java.lang.NumberFormatException e) {
            return new BigDecimal(0.0);
        }
    }

}
