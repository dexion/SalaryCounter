package com.snake.salarycounter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.snake.salarycounter.R;
import com.snake.salarycounter.views.PolygonProgressView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolygonFragment extends Fragment {

    View rootView;
    @BindView(R.id.ppv) PolygonProgressView mPpv;

    @BindView(R.id.btnSwitch) Button mBtnSwitch;
    @OnClick(R.id.btnSwitch) void btnSwitchClick(View v){
        Random random = new Random();
        int mSides = random.nextInt(5)+10;
        float[] mProgressValues = new float[mSides];
        for (int i = 0; i < mSides; i++) {
            mProgressValues[i] = random.nextFloat();
            if (random.nextInt() % 3 == 0) {
                mProgressValues[i] = 1;
            }
        }
        String []mLabels = new String[mSides];
        for (int i = 0; i < mSides; i++) {
            mLabels[i] = "label" + i;
        }

        mPpv.initial(mSides,mProgressValues,mLabels);
        mPpv.animateProgress();
    }

    public PolygonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_polygon, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
