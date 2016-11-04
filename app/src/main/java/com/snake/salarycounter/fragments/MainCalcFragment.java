package com.snake.salarycounter.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snake.salarycounter.MyLogic;
import com.snake.salarycounter.R;
import com.snake.salarycounter.activities.MainActivity;
import com.snake.salarycounter.events.ViewCreated;
import com.snake.salarycounter.models.ShiftType;
import com.snake.salarycounter.utils.Toolz;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainCalcFragment extends Fragment {

    public MainCalcFragment() {
        // Required empty public constructor
    }

    private static final String START_DATE_PREFERENCE_KEY = "START_DATE";
    private static final String END_DATE_PREFERENCE_KEY = "END_DATE";
    private SharedPreferences mSharedPreferences;

    View rootView;

    private DateTime startDate = DateTime.now();
    private int currentStartYear;
    private int currentStartMonth;
    private int currentStartDay;

    private DateTime endDate = startDate.plusMonths(1);
    private int currentEndYear;
    private int currentEndMonth;
    private int currentEndDay;

    MyLogic lgc = new MyLogic(startDate, endDate);

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout ctl;

    @BindView(R.id.calculator_start_date)
    TextView calcStartDate;
    @OnClick(R.id.calculator_start_date) void onStartDateClicked(){
        DatePickerDialog tpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        currentStartYear = year;
                        currentStartMonth = monthOfYear;
                        currentStartDay = dayOfMonth;

                        resetDates();
                        calcStartDate.setText(sdf.format(startDate.getMillis()));
                        lgc.recalcAll();
                        setTitleAmount();
                        setPayslipList();

                        SharedPreferences.Editor ed = mSharedPreferences.edit();
                        ed.putLong(START_DATE_PREFERENCE_KEY, startDate.getMillis()).apply();
                    }
                },
                currentStartYear,
                currentStartMonth,
                currentStartDay
        );
        tpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        tpd.show(getActivity().getFragmentManager(), "startDate");
    }

    @BindView(R.id.calculator_end_date)
    TextView calcEndDate;
    @OnClick(R.id.calculator_end_date) void onEndDateClicked(){
        DatePickerDialog tpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        currentEndYear = year;
                        currentEndMonth = monthOfYear;
                        currentEndDay = dayOfMonth;

                        resetDates();
                        calcEndDate.setText(sdf.format(endDate.getMillis()));
                        lgc.recalcAll();
                        setTitleAmount();
                        setPayslipList();

                        SharedPreferences.Editor ed = mSharedPreferences.edit();
                        ed.putLong(END_DATE_PREFERENCE_KEY, endDate.getMillis()).apply();
                    }
                },
                currentEndYear,
                currentEndMonth,
                currentEndDay
        );
        tpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        tpd.show(getActivity().getFragmentManager(), "endDate");
    }

    @BindView(R.id.list_payslip)
    LinearLayout llPayslip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main_calc, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (null != toolbar && null != ((MainActivity) getActivity()).getDrawer()) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),  ((MainActivity) getActivity()).getDrawer().getDrawerLayout(), toolbar, R.string.drawer_open, R.string.drawer_close);
            mActionBarDrawerToggle.syncState();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

            ((MainActivity) getActivity()).getDrawer().setActionBarDrawerToggle(mActionBarDrawerToggle);
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ButterKnife.bind(this, rootView);

        initDates();
        resetDates();

        lgc.recalcAll();

        calcStartDate.setText(sdf.format(startDate.getMillis()));
        calcEndDate.setText(sdf.format(endDate.getMillis()));
        setTitleAmount();
        setPayslipList();

        EventBus.getDefault().post(new ViewCreated(getActivity().getClass().toString()));

        return rootView;
    }

    private void setPayslipList(){
        if(llPayslip.getChildCount() > 0)
            llPayslip.removeAllViews();

        getFragmentManager().beginTransaction().add(
                llPayslip.getId(),
                PayslipFragment.newInstance(lgc.getTotalPayslipDouble()[lgc.getTotalPayslipDouble().length - 1], getString(R.string.payslip_total_amount)),
                getString(R.string.payslip_total))
                .commit();

        for (int i = 0; i < lgc.getTotalPayslipDouble().length - 1; i++) {
            if(null != lgc.getTotalPayslip()[i] && 0 != lgc.getTotalPayslip()[i][9].compareTo( BigDecimal.ZERO)){
                getFragmentManager().beginTransaction().add(
                        llPayslip.getId(),
                        PayslipFragment.newInstance(lgc.getTotalPayslipDouble()[i], ShiftType.getByWeight(i).getText()),
                        ShiftType.getByWeight(i).getText())
                        .commit();
            }
        }

        llPayslip.invalidate();
    }

    private void initDates() {
        startDate = new DateTime(mSharedPreferences.getLong(START_DATE_PREFERENCE_KEY, DateTime.now().getMillis()));
        endDate = new DateTime(mSharedPreferences.getLong(END_DATE_PREFERENCE_KEY, startDate.plusMonths(1).getMillis()));
        currentStartYear = startDate.getYear();
        currentStartMonth = startDate.getMonthOfYear() - 1;
        currentStartDay = startDate.getDayOfMonth();

        currentEndYear = endDate.getYear();
        currentEndMonth = endDate.getMonthOfYear() - 1;
        currentEndDay = endDate.getDayOfMonth();
    }

    private void setTitleAmount(){
        ctl.setTitle(Toolz.money(lgc.getTotalAmount()));
    }

    private void resetDates(){
        startDate = new DateTime(currentStartYear, currentStartMonth + 1, currentStartDay, 0, 0);
        endDate = new DateTime(currentEndYear, currentEndMonth + 1, currentEndDay, 0, 0);
        lgc.setStart(startDate);
        lgc.setEnd(endDate);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
