package com.snake.salarycounter.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.snake.salarycounter.data.AbstractDataProvider;
import com.snake.salarycounter.data.ShiftTypesDataProvider;

/**
 * Created by snake on 14.10.2015.
 */
public class ShiftTypesDataProviderFragment extends Fragment {
    private AbstractDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);  // keep the mDataProvider instance
        mDataProvider = new ShiftTypesDataProvider();
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }
}
