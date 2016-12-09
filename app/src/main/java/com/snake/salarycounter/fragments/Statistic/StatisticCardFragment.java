package com.snake.salarycounter.fragments.Statistic;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snake.salarycounter.R;
import com.snake.salarycounter.utils.Toolz;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticCardFragment extends Fragment {

    private static final String TITLE = "title";
    private static final String CURRENT_VALUE = "current_value";
    private static final String PREVIOUS_VALUE = "previous_value";

    private String mTitle;
    private double mCurrentValue;
    private double mPreviousValue;

    public StatisticCardFragment() {
        // Required empty public constructor
    }

    public static StatisticCardFragment newInstance(String title, double currentValue, double previousValue) {
        StatisticCardFragment fragment = new StatisticCardFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putDouble(CURRENT_VALUE, currentValue);
        args.putDouble(PREVIOUS_VALUE, previousValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
            mCurrentValue = getArguments().getDouble(CURRENT_VALUE);
            mPreviousValue = getArguments().getDouble(PREVIOUS_VALUE);
        }
    }

    @BindView(R.id.statistic_card_title)
    TextView title;
    @BindView(R.id.statistic_card_value)
    TextView value;
    @BindView(R.id.statistic_card_direction_value)
    TextView directionValue;
    @BindView(R.id.statistic_card_direction)
    TextView direction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statistic_card, container, false);
        ButterKnife.bind(this, rootView);

        title.setText(mTitle);
        value.setText(Toolz.money(mCurrentValue));
        directionValue.setText(Toolz.money(mCurrentValue - mPreviousValue));
        if(mCurrentValue == mPreviousValue){
            direction.setText("{cmd-arrow-right}");
            direction.setTextColor(Color.GRAY);
            directionValue.setTextColor(Color.GRAY);
        }
        if(mCurrentValue > mPreviousValue){
            direction.setText("{cmd-arrow-top-right}");
            direction.setTextColor(Color.GREEN);
            directionValue.setTextColor(Color.GREEN);
        }
        if(mCurrentValue < mPreviousValue){
            direction.setText("{cmd-arrow-bottom-right}");
            direction.setTextColor(Color.RED);
            directionValue.setTextColor(Color.RED);
        }

        return rootView;
    }

}
