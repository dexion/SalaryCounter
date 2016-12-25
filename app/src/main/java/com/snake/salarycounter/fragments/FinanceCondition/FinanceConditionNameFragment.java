package com.snake.salarycounter.fragments.FinanceCondition;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.snake.salarycounter.R;
import com.snake.salarycounter.models.FinanceCondition;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinanceConditionNameFragment extends Fragment {

    private static final String ARG_PARAM1 = "_id";

    private long mId;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private FinanceCondition fc;

    @BindView(R.id.finance_condition_start_date)
    EditText financeConditionStartDate;

    @OnClick(R.id.finance_condition_start_date)
    void onTabelStartDateClick() {
        DatePickerDialog tpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        currentYear = year;
                        currentMonth = monthOfYear;
                        currentDay = dayOfMonth;

                        fc.startDate = new DateTime(currentYear, currentMonth + 1, currentDay, 0, 0);
                        fc.save();
                        financeConditionStartDate.setText(fc.getText());
                    }
                },
                currentYear,
                currentMonth,
                currentDay
        );
        tpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
        tpd.show(getActivity().getFragmentManager(), "startDate");
    }

    public FinanceConditionNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
            fc = FinanceCondition.getById(mId);

            currentYear = fc.startDate.getYear();
            currentMonth = fc.startDate.getMonthOfYear() - 1; // because lib use JAN - 0 ((
            currentDay = fc.startDate.getDayOfMonth();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_finance_condition_name, container, false);

        ButterKnife.bind(this, v);

        financeConditionStartDate.setText(fc.getText());

        return v;
    }
}
