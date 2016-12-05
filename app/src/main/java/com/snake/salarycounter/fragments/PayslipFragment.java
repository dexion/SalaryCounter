package com.snake.salarycounter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snake.salarycounter.R;
import com.snake.salarycounter.utils.Toolz;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayslipFragment extends Fragment {

    private static final String MONEY = "money_array";
    private static final String TITLE = "payslip_title";
    private static final String SHORT = "short_payslip";
    private double[] mMoney;
    private String mTitle;
    private boolean mShort;

    public static PayslipFragment newInstance(double[] money, String title, boolean isShort) {
        PayslipFragment fragment = new PayslipFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MONEY, money);
        args.putString(TITLE, title);
        args.putBoolean(SHORT, isShort);
        fragment.setArguments(args);
        return fragment;
    }

    public PayslipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMoney = getArguments().getDoubleArray(MONEY);
            mTitle = getArguments().getString(TITLE);
            mShort = getArguments().getBoolean(SHORT);
        }
    }

    @BindView(R.id.payslip_title)
    TextView title;
    @BindView(R.id.payslip_salary)
    TextView salarysum;
    @BindView(R.id.payslip_addition)
    TextView addition;
    @BindView(R.id.payslip_addition_proc)
    TextView addition_proc;
    @BindView(R.id.payslip_addition_price)
    TextView addition_price;
    @BindView(R.id.payslip_north)
    TextView north;
    @BindView(R.id.payslip_regional)
    TextView regional;
    @BindView(R.id.payslip_bonus)
    TextView bonus;
    @BindView(R.id.payslip_total_amount)
    TextView total;
    @BindView(R.id.payslip_total_tax)
    TextView tax;
    @BindView(R.id.payslip_alimony_proc)
    TextView alimony_proc;
    @BindView(R.id.payslip_residue_proc)
    TextView residue_proc;
    @BindView(R.id.payslip_cash)
    TextView cash;
    @BindView(R.id.hideable_details)
    FrameLayout hideable_details;
    @BindView(R.id.hideable_total)
    FrameLayout hideable_total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_payslip, container, false);
        ButterKnife.bind(this, rootView);

        title.setText(mTitle);
        if (mShort){
            hideable_details.setVisibility(View.GONE);
            hideable_total.setVisibility(View.GONE);
        }
        if (null != mMoney && mMoney.length > 12) {
            salarysum.setText(Toolz.money(mMoney[0]));
            addition.setText(Toolz.money(mMoney[1]));
            addition_proc.setText(Toolz.money(mMoney[2]));
            addition_price.setText(Toolz.money(mMoney[3]));
            north.setText(Toolz.money(mMoney[4]));
            regional.setText(Toolz.money(mMoney[5]));
            bonus.setText(Toolz.money(mMoney[6]));
            total.setText(Toolz.money(mMoney[9]));
            tax.setText(Toolz.money(Toolz.round(mMoney[10], 0)));
            alimony_proc.setText(Toolz.money(mMoney[11]));
            residue_proc.setText(Toolz.money(mMoney[12]));
            cash.setText(Toolz.money(mMoney[9] - Toolz.round(mMoney[10], 0)  - mMoney[11]  - mMoney[12]));
        }

        return rootView;
    }
}
