package com.snake.salarycounter.fragments.Statistic;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.snake.salarycounter.MyLogic;
import com.snake.salarycounter.R;
import com.snake.salarycounter.activities.MainActivity;
import com.snake.salarycounter.utils.Toolz;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatisticFragment extends Fragment {

    public StatisticFragment() {
        // Required empty public constructor
    }

    private static final String START_DATE_STATISTIC_PREFERENCE_KEY = "START_DATE_STATISTIC";
    private static final String END_DATE_STATISTIC_PREFERENCE_KEY = "END_DATE_STATISTIC";
    private SharedPreferences mSharedPreferences;

    View rootView;

    private StatisticTask mStatTask;
    private ProgressDialog mProgressDialog;

    private DateTime startDate = DateTime.now();
    private int currentStartYear;
    private int currentStartMonth;
    private int currentStartDay;

    private DateTime endDate = startDate.plusMonths(1);
    private int currentEndYear;
    private int currentEndMonth;
    private int currentEndDay;

    MyLogic lgc = new MyLogic(startDate, endDate);
    BigDecimal totalAmountOnHand;
    ArrayList<Double> mValues = new ArrayList<>();
    ArrayList<String> mTitles = new ArrayList<>();
    List<Entry> mEntries = new ArrayList<>();

    private final static SimpleDateFormat sdfDay = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final static SimpleDateFormat sdfMonth = new SimpleDateFormat("LLLL yyyy", Locale.getDefault());
    private final static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());

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
                        calcStartDate.setText(sdfDay.format(startDate.getMillis()));

                        showSpinner();
                        mStatTask = new StatisticTask();
                        mStatTask.execute();

                        SharedPreferences.Editor ed = mSharedPreferences.edit();
                        ed.putLong(START_DATE_STATISTIC_PREFERENCE_KEY, startDate.getMillis()).apply();
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
                        calcEndDate.setText(sdfDay.format(endDate.getMillis()));

                        showSpinner();
                        mStatTask = new StatisticTask();
                        mStatTask.execute();

                        SharedPreferences.Editor ed = mSharedPreferences.edit();
                        ed.putLong(END_DATE_STATISTIC_PREFERENCE_KEY, endDate.getMillis()).apply();
                    }
                },
                currentEndYear,
                currentEndMonth,
                currentEndDay
        );
        tpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        tpd.show(getActivity().getFragmentManager(), "endDate");
    }

    @BindView(R.id.grid_payslip)
    GridLayout glPayslip;

    @BindView(R.id.statistic_chart)
    LineChart mLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

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

        mLineChart.setNoDataText(getString(R.string.no_data));
        initDates();
        resetDates();
        setTitle(0.0);
        showSpinner();

        calcStartDate.setText(sdfDay.format(startDate.getMillis()));
        calcEndDate.setText(sdfDay.format(endDate.getMillis()));

        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //load the data (only possible if we were able to get the Arguments
        if (view.getContext() != null) {
            mStatTask = new StatisticTask();
            mStatTask.execute();
        }
    }

    @Override
    public void onDestroyView() {
        if (mStatTask != null) {
            mStatTask.cancel(true);
            mStatTask = null;
        }
        super.onDestroyView();
    }

    private void showSpinner(){
        if(glPayslip.getChildCount() > 0)
            glPayslip.removeAllViews();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getString(R.string.loading_message));
        mProgressDialog.show();
    }

    private void prepareData(){
        int duration = new Duration(startDate, endDate).toStandardDays().getDays();
        lgc.setStart(startDate).setEnd(endDate).recalcAll();
        totalAmountOnHand = lgc.getTotalAmountOnHand();
        mValues.clear();
        mTitles.clear();
        mEntries.clear();

        if(duration < 50){ // Считаем по дням
            for (DateTime i = new DateTime(startDate); i.isBefore(endDate.plusDays(1)); i = i.plusDays(1)) {
                mTitles.add(sdfDay.format(i.getMillis()));
                double dailyPayslip[] = lgc.recalDay(i);
                mValues.add(dailyPayslip[9] - Toolz.round(dailyPayslip[10], 0)  - dailyPayslip[11]  - dailyPayslip[12]);
            }
        }else if(duration < 500){ // Считаем по месяцам
            for (DateTime i = new DateTime(startDate); i.isBefore(endDate.plusDays(1));) {
                mTitles.add(sdfMonth.format(i.getMillis()));

                if(MyLogic.getLastDay(i).isBefore(endDate.plusDays(1))) {
                    lgc.setStart(i).setEnd(MyLogic.getLastDay(i));
                }
                else{
                    lgc.setStart(i).setEnd(endDate);
                }
                lgc.recalcAll();
                int index = lgc.getTotalPayslipDouble().length - 1;
                if(index > 0) {
                    double payslip[] = lgc.getTotalPayslipDouble()[index];
                    if(null == payslip) {
                        mValues.add(0.0);
                    }
                    else {
                        mValues.add(payslip[9] - Toolz.round(payslip[10], 0) - payslip[11] - payslip[12]);
                    }
                }
                i = lgc.getEnd().plusDays(1);
            }
        }
        else{ // Считаем по годам

        }

        for(int i = 0; i < mValues.size(); i++){
            mEntries.add(new Entry(i, mValues.get(i).floatValue()));
        }
    }

    private void fillChart(){
        LineDataSet dataSet = new LineDataSet(mEntries, "Label");
        dataSet.setColor(Color.WHITE);
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(1.8f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setFillColor(Color.WHITE);
        dataSet.setFillAlpha(100);

        LineData lineData = new LineData(dataSet);
        //lineData.setValueTypeface(mTfLight);
        lineData.setValueTextSize(9f);
        lineData.setDrawValues(false);
        lineData.setHighlightEnabled(false);

        mLineChart.setViewPortOffsets(0, 0, 0, 0);
        mLineChart.setBackgroundColor(getResources().getColor(R.color.primary));
        mLineChart.setDrawGridBackground(false);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setData(lineData);

        /*IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(mTitles.size() > 0)
                    return mTitles.get((int) value);
                else
                    return "";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        };*/

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setGranularity(1f);
        //xAxis.setValueFormatter(formatter);
        xAxis.setEnabled(false);

        YAxis y = mLineChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(4, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        mLineChart.getAxisRight().setEnabled(false);

        mLineChart.animateXY(2000, 2000);

        mLineChart.invalidate();
    }

    private void fillStatisticCardViews(){
        if(glPayslip.getChildCount() > 0)
            glPayslip.removeAllViews();

        /*getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(
                    glPayslip.getId(),
                    PayslipFragment.newInstance(lgc.getTotalPayslipDouble()[lgc.getTotalPayslipDouble().length - 1], getString(R.string.payslip_total_amount), false),
                    getString(R.string.payslip_total))
                .commitAllowingStateLoss();*/

        for (int i = 0; i < mTitles.size(); i++) {
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .add(
                        glPayslip.getId(),
                        StatisticCardFragment.newInstance(mTitles.get(i), mValues.get(i), i == 0 ? mValues.get(i) : mValues.get(i-1)),
                        mTitles.get(i))
                    .commitAllowingStateLoss();
        }

        glPayslip.invalidate();
    }

    private void initDates() {
        startDate = new DateTime(mSharedPreferences.getLong(START_DATE_STATISTIC_PREFERENCE_KEY, DateTime.now().getMillis()));
        endDate = new DateTime(mSharedPreferences.getLong(END_DATE_STATISTIC_PREFERENCE_KEY, startDate.plusMonths(1).getMillis()));
        currentStartYear = startDate.getYear();
        currentStartMonth = startDate.getMonthOfYear() - 1;
        currentStartDay = startDate.getDayOfMonth();

        currentEndYear = endDate.getYear();
        currentEndMonth = endDate.getMonthOfYear() - 1;
        currentEndDay = endDate.getDayOfMonth();
    }

    private void resetDates(){
        startDate = new DateTime(currentStartYear, currentStartMonth + 1, currentStartDay, 0, 0);
        endDate = new DateTime(currentEndYear, currentEndMonth + 1, currentEndDay, 0, 0);
        lgc.setStart(startDate).setEnd(endDate);
    }

    private void setTitle(Object value){
        ctl.setTitle(Toolz.money(value));
    }

    public class StatisticTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            prepareData();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            setTitle(totalAmountOnHand);
            fillChart();
            fillStatisticCardViews();
            if(null!= mProgressDialog){
                mProgressDialog.dismiss();
            }
            super.onPostExecute(s);
        }
    }
}
