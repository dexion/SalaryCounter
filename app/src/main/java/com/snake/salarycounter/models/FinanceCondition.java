package com.snake.salarycounter.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "finances", id = BaseColumns._ID)
public class FinanceCondition extends Model{

    @Column(name = "start_date", index = true)
    public DateTime startDate;

    @Column(name = "salary")
    public BigDecimal salary; // голый оклад

    @Column(name = "addition")
    public BigDecimal addition; // ЕДВ

    @Column(name = "addition_proc")
    public double addition_proc; // доплаты в %, например, за совмещение

    @Column(name = "north")
    public double north; // северный

    @Column(name = "district")
    public double district; // районный

    @Column(name = "bonus")
    public double bonus; // премия

    @Column(name = "alimony")
    public BigDecimal alimony; // алименты в жёсткой сумме

    @Column(name = "alimony_proc")
    public double alimony_proc; // алименты в процентах от начисления

    @Column(name = "residue")
    public BigDecimal residue; // прочие вычеты

    @Column(name = "residue_proc")
    public double residue_proc; // прочие вычеты в % от начисления

    @Column(name = "other_bonus")
    public BigDecimal other_bonus; // прочие доплаты в месяц в жесткой сумме к начислению

    @Column(name = "other_bonus_proc")
    public double other_bonus_proc; // прочие доплаты в месяц в процентах к начислению

    @Column(name = "enable_tax")
    public boolean enable_tax; // подоходный налог

    public FinanceCondition()
    {
        super();
    }

    public FinanceCondition(DateTime date)
    {
        super();
        startDate = date;
    }

    public static ArrayList<FinanceCondition> allFinanceConditions()
    {
        List<FinanceCondition> typesList = new Select().from(FinanceCondition.class).orderBy("start_date ASC").execute();
        return new ArrayList<>(typesList);
    }

    public static FinanceCondition getByPosition(int position)
    {
        return new Select().from(FinanceCondition.class).orderBy("start_date ASC").limit(1).offset(position).executeSingle();
    }

    public static FinanceCondition getById(long _id)
    {
        return new Select().from(FinanceCondition.class).where("_id = ?", _id).limit(1).executeSingle();
    }

    public boolean canDelete(int position)
    {
        return true;
    }

    public String getText() {
        return startDate.toString("MMMM yyyy");
    }
}
