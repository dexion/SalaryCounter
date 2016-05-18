package com.snake.salarycounter;

import com.snake.salarycounter.models.Day;
import com.snake.salarycounter.models.FinanceCondition;
import com.snake.salarycounter.models.ShiftType;
import com.snake.salarycounter.models.Tabel;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MyLogic {

    public DateTime mStart;
    public DateTime mEnd;

    ArrayList<Payslip> mPayslips = new ArrayList<>();

    public class Payslip {
        Day mDay;
        double mHours = 0;
        BigDecimal mSalary = BigDecimal.valueOf(0.0);
        BigDecimal mAddition = BigDecimal.valueOf(0.0);
        BigDecimal mAdditionProc = BigDecimal.valueOf(0.0);
        BigDecimal mNorth = BigDecimal.valueOf(0.0);
        BigDecimal mDistrict = BigDecimal.valueOf(0.0);
        BigDecimal mBonus = BigDecimal.valueOf(0.0);

        ////////////////////////////
        BigDecimal mAlimony = BigDecimal.valueOf(0.0);
        BigDecimal mAlimonyProc = BigDecimal.valueOf(0.0);
        BigDecimal mResidue = BigDecimal.valueOf(0.0);
        BigDecimal mRecidueProc = BigDecimal.valueOf(0.0);

        public Payslip(Day day){
            mDay = day;
        }
    }

    public MyLogic(DateTime start, DateTime end){
        mStart = start;
        mEnd = end;

    }

    public void Recalc(){
        for(DateTime i = mStart; i.isBefore(mEnd); i.plusDays(1)){
            RecalcDay(i);
        }
    }

    public void RecalcDay(DateTime date){
        Day d = Day.getByDate(date);
        if(null != d) {
            ShiftType st = d.getShiftType();
            FinanceCondition fc = FinanceCondition.getByDate(date);
            Tabel t = Tabel.getByDate(date);
            Payslip p = new Payslip(d);

            mPayslips.add(p);
            if (st.isCountHours) {
                p.mHours = (new Period(st.dayDuration).getHours()) + (new Period(st.dayDuration).getMinutes() / 60.0);
            }

            if (st.isFixedPrice) {
                p.mSalary = st.fixedPrice;
            } else {
                BigDecimal normohour = fc.salary.divide(new BigDecimal(t.hours), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
    }
}
