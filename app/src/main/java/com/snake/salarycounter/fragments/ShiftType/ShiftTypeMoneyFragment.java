package com.snake.salarycounter.fragments.ShiftType;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.snake.salarycounter.R;
import com.snake.salarycounter.events.TextEvent;
import com.snake.salarycounter.models.ShiftType;
import com.snake.salarycounter.watchers.TextValidator;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import de.greenrobot.event.EventBus;

public class ShiftTypeMoneyFragment extends Fragment {

    private static final String ARG_PARAM1 = "_id";

    private long mId;

    private ShiftType st;

    @BindView(R.id.shift_type_money_is_count_hours)
    CheckBox isCountHours;

    @OnCheckedChanged(R.id.shift_type_money_is_count_hours)
    void onIsCountHoursChecked(boolean checked) {
        st.isCountHours = checked;
        st.save();
    }

    @BindView(R.id.shift_type_money_is_average_price)
    CheckBox isAveragePrice;

    @OnCheckedChanged(R.id.shift_type_money_is_average_price)
    void onIsAverageChecked(boolean checked) {
        st.isAveragePrice = checked;
        if (checked) {
            isFixedPrice.setChecked(false);
            st.isFixedPrice = false;
            isHourlyRate.setChecked(false);
            st.isHourlyRate = false;
        }
        st.save();
    }

    @BindView(R.id.shift_type_money_is_hourly_rate)
    CheckBox isHourlyRate;

    @OnCheckedChanged(R.id.shift_type_money_is_hourly_rate)
    void onIsHourlyRateChecked(boolean checked) {
        hourlyRate.setEnabled(checked);
        st.isHourlyRate = checked;
        if (checked) {
            isFixedPrice.setChecked(false);
            st.isFixedPrice = false;
            isAveragePrice.setChecked(false);
            st.isAveragePrice = false;
        }
        st.save();
    }

    @BindView(R.id.shift_type_money_is_fixed_price)
    CheckBox isFixedPrice;

    @OnCheckedChanged(R.id.shift_type_money_is_fixed_price)
    void onIsFixedPriceChecked(boolean checked) {
        fixedPrice.setEnabled(checked);
        st.isFixedPrice = checked;
        if (checked) {
            isAveragePrice.setChecked(false);
            st.isAveragePrice = false;
            isHourlyRate.setChecked(false);
            st.isHourlyRate = false;
        }
        st.save();
    }

    @BindView(R.id.shift_type_money_only_salary)
    CheckBox onlySalary;

    @OnCheckedChanged(R.id.shift_type_money_only_salary)
    void onOnlySalaryChecked(boolean checked) {
        st.onlySalary = checked;
        st.save();
    }

    @BindView(R.id.shift_type_money_fixed_price)
    EditText fixedPrice;
    @BindView(R.id.shift_type_money_hourly_rate)
    EditText hourlyRate;
    @BindView(R.id.shift_type_money_additional_price)
    EditText additionalPrice;
    @BindView(R.id.shift_type_money_multiplier)
    EditText multiplier;

    public ShiftTypeMoneyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getLong(ARG_PARAM1);
            st = ShiftType.getById(mId);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shift_type_money, container, false);
        ButterKnife.bind(this, v);

        isCountHours.setChecked(st.isCountHours);
        isAveragePrice.setChecked(st.isAveragePrice);
        isFixedPrice.setChecked(st.isFixedPrice);
        isHourlyRate.setChecked(st.isHourlyRate);
        onlySalary.setChecked(st.onlySalary);
        fixedPrice.setText(st.fixedPrice.toString());
        fixedPrice.setEnabled(st.isFixedPrice);
        fixedPrice.addTextChangedListener(new TextValidator(getActivity(), fixedPrice, st.getId()));
        hourlyRate.setText(st.hourlyRate.toString());
        hourlyRate.setEnabled(st.isHourlyRate);
        hourlyRate.addTextChangedListener(new TextValidator(getActivity(), hourlyRate, st.getId()));
        additionalPrice.setText(st.additionalPrice.toString());
        additionalPrice.addTextChangedListener(new TextValidator(getActivity(), additionalPrice, st.getId()));
        multiplier.setText(st.multiplier.toString());
        multiplier.addTextChangedListener(new TextValidator(getActivity(), multiplier, st.getId()));
        return v;
    }

    public void onEvent(TextEvent event) {
        if (st.getId().equals(event.mId)) {
            switch (event.mTextEditId) {
                case R.id.shift_type_money_hourly_rate:
                    st.hourlyRate = new BigDecimal(event.mValue);
                    st.save();
                    break;
                case R.id.shift_type_money_fixed_price:
                    st.fixedPrice = new BigDecimal(event.mValue);
                    st.save();
                    break;
                case R.id.shift_type_money_additional_price:
                    st.additionalPrice = new BigDecimal(event.mValue);
                    st.save();
                    break;
                case R.id.shift_type_money_multiplier:
                    st.multiplier = new BigDecimal(event.mValue);
                    st.save();
                    break;
                default:
                    break;
            }
        }
    }
}
