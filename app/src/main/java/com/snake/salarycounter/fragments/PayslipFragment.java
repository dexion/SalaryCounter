package com.snake.salarycounter.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snake.salarycounter.R;
import com.snake.salarycounter.utils.Toolz;

import java.util.Formatter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayslipFragment extends Fragment {

    private static final String MONEY = "money_array";
    private static final String TITLE = "payslip_title";
    private double[] mMoney;
    private String mTitle;

    public static PayslipFragment newInstance(double[] money, String title) {
        PayslipFragment fragment = new PayslipFragment();
        Bundle args = new Bundle();
        args.putDoubleArray(MONEY, money);
        args.putString(TITLE, title);
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
    @BindView(R.id.payslip_cash)
    TextView cash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_payslip, container, false);
        ButterKnife.bind(this, rootView);

        title.setText(mTitle);
        if (null != mMoney) {
            salarysum.setText(Toolz.money(mMoney[0]));
            addition.setText(Toolz.money(mMoney[1]));
            addition_proc.setText(Toolz.money(mMoney[2]));
            addition_price.setText(Toolz.money(mMoney[3]));
            north.setText(Toolz.money(mMoney[4]));
            regional.setText(Toolz.money(mMoney[5]));
            bonus.setText(Toolz.money(mMoney[6]));
            total.setText(Toolz.money(mMoney[9]));
            tax.setText(Toolz.money(mMoney[10]));
            cash.setText(Toolz.money(mMoney[9] - mMoney[10]));
        }

        return rootView;
    }
}
