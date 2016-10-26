package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "shift_types", id = BaseColumns._ID)
public class ShiftType extends Model
{
    @Column(name = "name", index = true)
    public String name;

    @Column(name = "color")
    public int color;

    @Column(name = "weight")
    public int weight;

    @Column(name = "ser")
    public String ser;

    @Column (name = "day_start")
    public DateTime dayStart;

    @Column (name = "day_end")
    public DateTime dayEnd;

    @Column (name = "dinner_start")
    public DateTime dinnerStart;

    @Column (name = "dinner_end")
    public DateTime dinnerEnd;

    @Column (name = "day_duration")
    public Duration dayDuration;

    @Column (name = "is_fixed_price")
    public boolean isFixedPrice;

    @Column (name = "fixed_price")
    public BigDecimal fixedPrice;

    @Column (name = "additional_price")
    public BigDecimal additionalPrice;

    @Column (name = "multiplier")
    public BigDecimal multiplier;

    @Column (name = "is_count_hours")
    public boolean isCountHours;

    @Column (name = "is_average_price")
    public boolean isAveragePrice;

    @Column (name = "only_salary")
    public boolean onlySalary; // в эту смену только оклад считается, остальные надбавки не учитываются

    public ShiftType()
    {
        super();
    }

    public ShiftType(String sName)
    {
        super();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");

        name = sName;
        color = 0xffff0000;
        weight = ShiftType.allShiftTypes().size();
        dayStart =    formatter.parseDateTime("08:00:00");
        dayEnd =      formatter.parseDateTime("20:00:00");
        dinnerStart = formatter.parseDateTime("13:00:00");
        dinnerEnd =   formatter.parseDateTime("14:00:00");
        dayDuration = (new Duration(dayStart, dayEnd)).minus(new Duration(dinnerStart, dinnerEnd));

        isFixedPrice = false;
        fixedPrice = new BigDecimal(0.0);
        additionalPrice = new BigDecimal(250.0); // компенсация вахтового метода
        multiplier = new BigDecimal(1.0);
        isCountHours = true;
        isAveragePrice = false;
        onlySalary = false;
    }

    public static ArrayList<ShiftType> allShiftTypes()
    {
        List<ShiftType> typesList = new Select().from(ShiftType.class).orderBy("weight ASC").execute();
        return new ArrayList<>(typesList);
    }

    public static ShiftType getByPosition(int position)
    {
        return new Select().from(ShiftType.class).orderBy("weight ASC").limit(1).offset(position).executeSingle();
    }

    public static ShiftType getById(long _id)
    {
        return new Select().from(ShiftType.class).where("_id = ?", _id).limit(1).executeSingle();
    }

    public static ShiftType getByWeight(int weight)
    {
        return new Select().from(ShiftType.class).where("weight = ?", weight).limit(1).executeSingle();
    }

    public static void reorderBottom(int fromPosition, int ignorePosition)
    {
        int weight = ((ShiftType) new Select().from(ShiftType.class).orderBy("weight ASC").limit(1).offset(fromPosition).executeSingle()).weight;
        weight += 1;

        List<ShiftType> typesList = new Select()
                .from(ShiftType.class)
                .orderBy("weight ASC")
                .where("weight >= ?", fromPosition).and("weight <> ?", ignorePosition)
                .execute();

        ActiveAndroid.beginTransaction();
        try {
            for(ShiftType i : typesList){
                i.weight = weight;
                weight += 1;
                i.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void reorderTop(int fromPosition, int ignorePosition)
    {
        int weight = 0;
        List<ShiftType> typesList = new Select()
                .from(ShiftType.class)
                .orderBy("weight ASC")
                .where("weight <= ?", fromPosition).and("weight <> ?", ignorePosition)
                .execute();

        ActiveAndroid.beginTransaction();
        try {
            for(ShiftType i : typesList){
                i.weight = weight;
                i.save();
                weight++;
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static int reorderAfterDeletion(int position) {
        List<ShiftType> typesList = new Select()
                .from(ShiftType.class)
                .orderBy("weight ASC")
                .where("weight >= ?", position).and("weight <> ?", position)
                .execute();

        ActiveAndroid.beginTransaction();
        try {
            for(ShiftType i : typesList){
                i.weight -= 1;
                i.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
        return typesList.size();
    }

    public static boolean canDelete(int position){
        return !(Day.getByShiftType(ShiftType.getByPosition(position)).size() > 0);
    }
    public boolean canDelete(){
        return !(Day.getByShiftType(this).size() > 0);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getText() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
