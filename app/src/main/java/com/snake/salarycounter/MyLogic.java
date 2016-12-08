package com.snake.salarycounter;

import com.snake.salarycounter.models.Day;
import com.snake.salarycounter.models.FinanceCondition;
import com.snake.salarycounter.models.ShiftType;
import com.snake.salarycounter.models.Tabel;
import com.snake.salarycounter.utils.Toolz;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class MyLogic {
    private final int SIZE_OF_PAYSLIP = 15;

    private DateTime mStart;
    private DateTime mEnd;

    private ArrayList<Payslip> mPayslips = new ArrayList<>();
    private BigDecimal[][] totalPayslip = new BigDecimal[ShiftType.allShiftTypes().size()][];
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private BigDecimal totalAlimony;
    private BigDecimal totalResidue;
    private BigDecimal totalAmountOnHand;

    private class Payslip {

        ShiftType mShiftType;
        Day mDay;
        double mCountedHours = 0.0;
        double mHours = 0.0;
        boolean mEnableTax = true;

        BigDecimal mSalary = BigDecimal.valueOf(0.0);
        BigDecimal mAddition = BigDecimal.valueOf(0.0);
        BigDecimal mAdditionProc = BigDecimal.valueOf(0.0);
        BigDecimal mNorth = BigDecimal.valueOf(0.0);
        BigDecimal mDistrict = BigDecimal.valueOf(0.0);
        BigDecimal mBonus = BigDecimal.valueOf(0.0);
        BigDecimal mAdditionalPrice = BigDecimal.valueOf(0.0);
        BigDecimal mOtherBonus = BigDecimal.valueOf(0.0);
        BigDecimal mOtherBonusProc = BigDecimal.valueOf(0.0);

        ////////////////////////////
        BigDecimal mTax = BigDecimal.valueOf(0.0);
        BigDecimal mAlimony = BigDecimal.valueOf(0.0);
        BigDecimal mAlimonyProc = BigDecimal.valueOf(0.0);
        BigDecimal mResidue = BigDecimal.valueOf(0.0);
        BigDecimal mResidueProc = BigDecimal.valueOf(0.0);

        public Payslip(Day day) {
            mDay = day;
        }

        Payslip(Day day, ShiftType shiftType) {
            mDay = day;
            mShiftType = shiftType;
        }
    }

    public MyLogic() {}

    public MyLogic(DateTime start, DateTime end) {
        mStart = start;
        mEnd = end;
    }

    public void recalcAll() {

        mPayslips.clear();

        for (int i = 0; i < ShiftType.allShiftTypes().size(); i++) {
            totalPayslip[i] = null;
        }
        totalAmount = BigDecimal.valueOf(0.0);
        totalTax = BigDecimal.valueOf(0.0);
        totalAlimony = BigDecimal.valueOf(0.0);
        totalResidue = BigDecimal.valueOf(0.0);
        totalAmountOnHand = BigDecimal.valueOf(0.0);

        for (DateTime i = new DateTime(mStart); i.isBefore(mEnd.plusDays(1)); i = i.plusDays(1)) {
            recalcDay(i);
        }

        for (Payslip p : mPayslips) {
            if (totalPayslip[p.mShiftType.weight] == null) {
                totalPayslip[p.mShiftType.weight] = new BigDecimal[SIZE_OF_PAYSLIP];

                for (int i = 0; i < SIZE_OF_PAYSLIP; i++) {
                    totalPayslip[p.mShiftType.weight][i] = new BigDecimal(0.0);
                }
            }

            BigDecimal tTotalAmount = BigDecimal.valueOf(0.0); // временная переменная для суммирования. Нужна потому, что не на все применяется налог.
            tTotalAmount = tTotalAmount.add(p.mSalary);
            tTotalAmount = tTotalAmount.add(p.mAddition);
            tTotalAmount = tTotalAmount.add(p.mAdditionProc);
            tTotalAmount = tTotalAmount.add(p.mNorth);
            tTotalAmount = tTotalAmount.add(p.mDistrict);
            tTotalAmount = tTotalAmount.add(p.mBonus);
            tTotalAmount = tTotalAmount.add(p.mOtherBonusProc);

            totalPayslip[p.mShiftType.weight][0] = totalPayslip[p.mShiftType.weight][0].add(p.mSalary);
            totalPayslip[p.mShiftType.weight][1] = totalPayslip[p.mShiftType.weight][1].add(p.mAddition);
            totalPayslip[p.mShiftType.weight][2] = totalPayslip[p.mShiftType.weight][2].add(p.mAdditionProc);
            totalPayslip[p.mShiftType.weight][3] = totalPayslip[p.mShiftType.weight][3].add(p.mAdditionalPrice);
            totalPayslip[p.mShiftType.weight][4] = totalPayslip[p.mShiftType.weight][4].add(p.mNorth);
            totalPayslip[p.mShiftType.weight][5] = totalPayslip[p.mShiftType.weight][5].add(p.mDistrict);
            totalPayslip[p.mShiftType.weight][6] = totalPayslip[p.mShiftType.weight][6].add(p.mBonus);
            totalPayslip[p.mShiftType.weight][7] = totalPayslip[p.mShiftType.weight][7].add(p.mOtherBonusProc);
            totalPayslip[p.mShiftType.weight][8] = totalPayslip[p.mShiftType.weight][8].add(BigDecimal.valueOf(p.mCountedHours));
            totalPayslip[p.mShiftType.weight][9] = totalPayslip[p.mShiftType.weight][9].add(tTotalAmount).add(p.mAdditionalPrice);
            totalPayslip[p.mShiftType.weight][10] = totalPayslip[p.mShiftType.weight][10].add(p.mTax);
            totalPayslip[p.mShiftType.weight][11] = totalPayslip[p.mShiftType.weight][11].add(p.mAlimonyProc);
            totalPayslip[p.mShiftType.weight][12] = totalPayslip[p.mShiftType.weight][12].add(p.mResidueProc);
            totalPayslip[p.mShiftType.weight][13] = totalPayslip[p.mShiftType.weight][13].add(new BigDecimal(1.0));
            totalPayslip[p.mShiftType.weight][14] = totalPayslip[p.mShiftType.weight][14].add(new BigDecimal(p.mCountedHours));

            totalTax = totalTax.add(p.mTax);
            totalAlimony = totalAlimony.add(p.mAlimonyProc).add(p.mAlimony);
            totalResidue = totalResidue.add(p.mResidueProc).add(p.mResidue);
            totalAmount = totalAmount.add(tTotalAmount).add(p.mAdditionalPrice); // на это налог не начисляется

        }
        totalTax = Toolz.round(totalTax, 0);
        totalAmountOnHand = totalAmount.subtract(totalTax).subtract(totalAlimony).subtract(totalResidue);
    }

    public double[] recalDay(DateTime date) {
        double[] result = new double[SIZE_OF_PAYSLIP];
        Payslip p = recalcDay(date);
        if (null != p) {
            BigDecimal tTotalAmount = BigDecimal.valueOf(0.0); // временная переменная для суммирования. Нужна потому, что не на все применяется налог.
            tTotalAmount = tTotalAmount.add(p.mSalary)
                    .add(p.mAddition)
                    .add(p.mAdditionProc)
                    .add(p.mNorth)
                    .add(p.mDistrict)
                    .add(p.mBonus)
                    .add(p.mOtherBonusProc)
                    .add(p.mAdditionalPrice);

            result[0] = p.mSalary.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[1] = p.mAddition.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[2] = p.mAdditionProc.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[3] = p.mAdditionalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[4] = p.mNorth.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[5] = p.mDistrict.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[6] = p.mBonus.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[7] = p.mOtherBonusProc.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[8] = p.mCountedHours;
            result[9] = tTotalAmount.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[10] = p.mTax.setScale(0, RoundingMode.HALF_UP).doubleValue();
            result[11] = p.mAlimonyProc.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[12] = p.mResidueProc.setScale(2, RoundingMode.HALF_UP).doubleValue();
            result[13] = 1;
            result[14] = p.mCountedHours;
        }
        return result;
    }

    private Payslip recalcDay(DateTime date) {
        Payslip p = null;

        Day d = Day.getByDate(date.toDate());
        if (null != d) {
            ShiftType st = d.getShiftType();
            FinanceCondition fc = FinanceCondition.getByDate(date);
            Tabel t = Tabel.getByDate(date);

            if (null == st) {
                return null;
            }

            p = new Payslip(d, st);

            mPayslips.add(p);

            p.mAdditionalPrice = st.additionalPrice;

            p.mHours = (new Period(st.dayDuration).getHours()) + (new Period(st.dayDuration).getMinutes() / 60.0);

            if (st.isCountHours) {
                p.mCountedHours = p.mHours;
            }

            if (st.isFixedPrice) {
                p.mSalary = st.fixedPrice;
            } else if (st.isAveragePrice) {
                // надо как-то расчитывать средний заработок
            } else if (st.isHourlyRate) {
                p.mSalary = st.hourlyRate.multiply(new BigDecimal(p.mHours));
            }else {
                if(null == fc || null == t){
                    return null;
                }
                p.mEnableTax = fc.enable_tax;
                BigDecimal normohour = fc.salary.divide(new BigDecimal(t.hours), 15, BigDecimal.ROUND_HALF_UP);
                p.mSalary = normohour.multiply(new BigDecimal(p.mHours));

                if (!st.onlySalary) {
                    BigDecimal normohourAddition = fc.addition.divide(new BigDecimal(t.hours), 15, BigDecimal.ROUND_HALF_UP);
                    p.mAddition = normohourAddition.multiply(new BigDecimal(p.mHours));

                    BigDecimal salaryWithAdditionProc = fc.salary.multiply(BigDecimal.valueOf(fc.addition_proc / 100.0));
                    BigDecimal normohourAdditionProc = salaryWithAdditionProc.divide(new BigDecimal(t.hours), 15, BigDecimal.ROUND_HALF_UP);
                    p.mAdditionProc = normohourAdditionProc.multiply(new BigDecimal(p.mHours));

                    p.mBonus = (p.mSalary.add(p.mAddition).add(p.mAdditionProc)).multiply(new BigDecimal(fc.bonus / 100.0));

                    //p.mOtherBonus = fc.other_bonus; // надо как-то разбить премию из фонда директора за месяц на отдельную смену

                    p.mOtherBonusProc = (p.mSalary.add(p.mAddition).add(p.mAdditionProc)).multiply(new BigDecimal(fc.other_bonus_proc / 100.0));

                    p.mSalary = p.mSalary.multiply(st.multiplier); // потому что все коэффициенты применяются только на голый оклад

                    p.mNorth = (p.mSalary.add(p.mAddition).add(p.mAdditionProc).add(p.mBonus).add(p.mOtherBonus)).multiply(new BigDecimal(fc.north / 100.0));

                    p.mDistrict = (p.mSalary.add(p.mAddition).add(p.mAdditionProc).add(p.mBonus).add(p.mOtherBonus)).multiply(new BigDecimal(fc.district / 100.0));

                    if (p.mEnableTax && !p.mShiftType.onlySalary) {
                        p.mTax = (p.mSalary
                                .add(p.mAddition)
                                .add(p.mAdditionProc)
                                .add(p.mNorth)
                                .add(p.mDistrict)
                                .add(p.mBonus)
                                .add(p.mOtherBonusProc)
                        ).multiply(new BigDecimal(0.13));
                    }

                    if (!p.mShiftType.onlySalary) {
                        p.mAlimonyProc = (p.mSalary.add(p.mAddition).add(p.mAdditionProc).add(p.mNorth).add(p.mDistrict).add(p.mBonus).add(p.mOtherBonusProc).subtract(p.mTax)).multiply(new BigDecimal(fc.alimony_proc / 100.0));
                    }

                    if (!p.mShiftType.onlySalary) {
                        p.mResidueProc = (p.mSalary.add(p.mAddition).add(p.mAdditionProc).add(p.mNorth).add(p.mDistrict).add(p.mBonus).add(p.mOtherBonusProc).subtract(p.mTax)).multiply(new BigDecimal(fc.residue_proc / 100.0));
                    }

                    /*if (!p.mShiftType.onlySalary) { // Как ежедневно вычитать алименты в жесткой сумме, если они со всей платежки за месяц утекают?
                        p.mAlimony = p.mSalary.add(p.mAddition).add(p.mAdditionProc).add(p.mNorth).add(p.mDistrict).add(p.mBonus).add(p.mOtherBonusProc).subtract(p.mTax).subtract(fc.alimony.divide());
                    }*/
                }
            }
        }
        return p;
    }

    //region Getters & Setters
    public DateTime getStart() {
        return mStart;
    }

    public MyLogic setStart(DateTime mStart) {
        this.mStart = mStart;
        return this;
    }

    public DateTime getEnd() {
        return mEnd;
    }

    public MyLogic setEnd(DateTime mEnd) {
        this.mEnd = mEnd;
        return this;
    }

    public ArrayList<Payslip> getDailyPayslips() {
        return mPayslips;
    }

    public BigDecimal[][] getTotalPayslip() {
        return totalPayslip;
    }

    public double[][] getTotalPayslipDouble() {
        int SIZE = totalPayslip.length;
        double[][] tArray = new double[SIZE + 1][];
        for (int i = 0; i < SIZE; i++) {
            if (null != totalPayslip[i]) {
                tArray[i] = new double[totalPayslip[i].length];
                if(null == tArray[SIZE]){
                    tArray[SIZE] = new double[totalPayslip[i].length];
                }
                for (int ii = 0; ii < totalPayslip[i].length; ii++) {
                    tArray[i][ii] = totalPayslip[i][ii].setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
            }
        }

        for (int i = 0; i < totalPayslip.length; i++) {
            if (null != totalPayslip[i]) {
                for (int ii = 0; ii < totalPayslip[i].length; ii++) {
                    tArray[SIZE][ii] += tArray[i][ii];
                }
            }
        }

        return tArray;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public BigDecimal getTotalAmountOnHand() {
        return totalAmountOnHand;
    }

    //endregion
}
