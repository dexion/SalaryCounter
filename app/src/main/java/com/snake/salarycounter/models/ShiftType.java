package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import java.util.ArrayList;
import java.util.List;

@Table(name = "shift_types", id = BaseColumns._ID)
public class ShiftType extends Model // Data extends Model
{
    @Column(name = "name", index = true)
    public String name;

    @Column(name = "color")
    public int color;

    @Column(name = "weight")
    public int weight;

    @Column(name = "ser")
    public String ser;

    public ShiftType()
    {
        super();
    }

    public ShiftType(String Name, int Color, int Weight)
    {
        super();
        name = Name;
        color = Color;
        weight = Weight;
    }

    public ShiftType(String Name, int Color)
    {
        super();
        name = Name;
        color = Color;
        weight = ShiftType.allShiftTypes().size() == 0 ? 0 : SQLiteUtils.intQuery("SELECT MAX(weight) FROM shift_types", null) + 1;
    }

    /*public ShiftType(long id, int viewType, String text, int swipeReaction)
    {
        super();
        mId = id;
        mViewType = viewType;
    }*/

    public ShiftType(int position, ShiftType st)
    {
        super();
        name = st.name;
        color = st.color;
        weight = position;
    }

    public static ArrayList<ShiftType> allShiftTypes()
    {
        List<ShiftType> typesList = new Select().from(ShiftType.class).orderBy("weight ASC").execute();
        return new ArrayList<ShiftType>(typesList);
    }

    public static ShiftType getByPosition(int position)
    {
        return new Select().from(ShiftType.class).orderBy("weight ASC").limit(1).offset(position).executeSingle();
    }

    public static ShiftType getById(int _id)
    {
        return new Select().from(ShiftType.class).where("_id = ?", _id).limit(1).executeSingle();
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

    public boolean canDelete(int position){
        return !(Day.getByShiftType(ShiftType.getByPosition(position)).size() > 0);
    }
    @Override
    public String toString() {
        return name;
    }

    public String getText() {
        //return name + "| id: " + getId().toString() + "| weight: " + String.valueOf(weight);
        return name;
    }

    public int getColor() {
        return color;
    }
}

