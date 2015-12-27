package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.snake.salarycounter.data.AbstractDataProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "shift_types", id = BaseColumns._ID)
public class ShiftType extends AbstractDataProvider.Data // Data extends Model
{
    @Column(name = "name", index = true)
    public String name;

    @Column(name = "color")
    public int color;

    @Column(name = "weight")
    public int weight;

    @Column(name = "ser")
    public String ser;

    private long mId;
    private String mText;
    private int mViewType;
    private boolean mPinned;

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
        weight = allShiftTypes().size();
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

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return mViewType;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getText() {
        return name + "| id: " + getId().toString() + "| weight: " + String.valueOf(weight);
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public boolean isPinned() {
        return mPinned;
    }

    @Override
    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }
}

