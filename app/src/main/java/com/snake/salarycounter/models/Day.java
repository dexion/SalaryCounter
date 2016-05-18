package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Table(name = "days", id = BaseColumns._ID)
public class Day extends Model {

    @Column(name = "date", index = true)
    public DateTime date;

    @Column(name = "shift_type")
    public ShiftType shiftType;

    public Day() {
        super();
    }

    public Day(Date mDate) {
        super();
        this.date = new DateTime(mDate);
    }

    public static ArrayList<Day> allDays() {
        List<Day> typesList = new Select().from(Day.class).orderBy("date ASC").execute();
        return new ArrayList<>(typesList);
    }

    public static Day getByPosition(int position) {
        return new Select().from(Day.class).orderBy("date ASC").limit(1).offset(position).executeSingle();
    }

    public static Day getByDate(Date date) {
        return new Select().from(Day.class).where("date = ?", new DateTime(date).getMillis()).limit(1).executeSingle();
    }

    public static Day getByDate(DateTime date) {
        return new Select().from(Day.class).where("date = ?", new DateTime(date).getMillis()).limit(1).executeSingle();
    }

    public static Day getById(int _id) {
        return new Select().from(Day.class).where("_id = ?", _id).limit(1).executeSingle();
    }

    public static ArrayList<Day> getByShiftType(ShiftType shiftType) {
        List<Day> days = new Select().from(Day.class).where("shift_type = ?", shiftType.getId()).execute();
        return new ArrayList<>(days);
    }

    public String getText() {
        return null;
    }

    public ShiftType getShiftType(){
        return shiftType;
    }
}
