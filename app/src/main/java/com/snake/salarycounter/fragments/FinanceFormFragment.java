package com.snake.salarycounter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snake.salarycounter.R;

public class FinanceFormFragment extends Fragment {
    private int month = 0;

    public FinanceFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finance_form, container, false);
    }

    public void setMonth(int month){
        this.month = month;
        return;
    }

    public int getMonth() {
        return month;
    }
}
