package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Table(name = "tabels", id = BaseColumns._ID)
public class Tabel extends Model {

    @Column(name = "start_date", index = true)
    public DateTime startDate;

    @Column(name = "hours")
    public double hours;

    public Tabel()
    {
        super();
    }

    public Tabel(DateTime date, double mHours)
    {
        super();
        startDate = date;
        hours = mHours;
    }

    public static ArrayList<Tabel> allTabel()
    {
        List<Tabel> typesList = new Select().from(Tabel.class).orderBy("start_date DESC").execute();
        return new ArrayList<>(typesList);
    }

    public static Tabel getByPosition(int position)
    {
        return new Select().from(Tabel.class).orderBy("start_date DESC").limit(1).offset(position).executeSingle();
    }

    public static Tabel getById(long _id)
    {
        return new Select().from(Tabel.class).where("_id = ?", _id).limit(1).executeSingle();
    }

    public boolean canDelete(int position)
    {
        return true;
    }

    public String getText() {
        return new SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(startDate.getMillis());
    }

    public static Tabel getByDate(DateTime date){
        return new Select().from(Tabel.class).orderBy("start_date DESC").limit(1).where("start_date <= ?", date.getMillis()).executeSingle();
    }
}
