package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.snake.salarycounter.data.AbstractDataProvider;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Table(name = "days", id = BaseColumns._ID)
public class Day extends AbstractDataProvider.Data{

    @Column(name = "date", index = true)
    public DateTime date;

    @Column(name = "shift_type")
    public ShiftType shiftType;

    public Day(){
        super();
    }

    public Day(Date mDate){
        super();
        this.date = new DateTime(mDate);
    }

    public static ArrayList<Day> allDays()
    {
        List<Day> typesList = new Select().from(Day.class).orderBy("date ASC").execute();
        return new ArrayList<Day>(typesList);
    }

    public static Day getByPosition(int position)
    {
        return new Select().from(Day.class).orderBy("date ASC").limit(1).offset(position).executeSingle();
    }

    public static Day getByDate(Date date)
    {
        return new Select().from(Day.class).where("date = ?", new DateTime(date).getMillis()).limit(1).executeSingle();
    }

    public static Day getByDate(DateTime date)
    {
        return new Select().from(Day.class).where("date = ?", new DateTime(date).getMillis()).limit(1).executeSingle();
    }

    public static Day getById(int _id)
    {
        return new Select().from(Day.class).where("_id = ?", _id).limit(1).executeSingle();
    }

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public void setPinned(boolean pinned) {

    }

    @Override
    public boolean isPinned() {
        return false;
    }
}
