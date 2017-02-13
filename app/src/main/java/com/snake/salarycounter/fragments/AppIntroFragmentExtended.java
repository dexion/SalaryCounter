package com.snake.salarycounter.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntroBaseFragment;
import com.github.paolorotolo.appintro.CustomFontCache;
import com.github.paolorotolo.appintro.util.LogHelper;
import com.mikepenz.iconics.IconicsDrawable;

public class AppIntroFragmentExtended extends AppIntroBaseFragment {

    protected static final String ARG_TITLE = "title";
    protected static final String ARG_TITLE_TYPEFACE = "title_typeface";
    protected static final String ARG_DESC = "desc";
    protected static final String ARG_DESC_TYPEFACE = "desc_typeface";
    protected static final String ARG_DRAWABLE = "drawable";
    protected static final String ARG_BG_COLOR = "bg_color";
    protected static final String ARG_TITLE_COLOR = "title_color";
    protected static final String ARG_DESC_COLOR = "desc_color";

    private static final String TAG = LogHelper.makeLogTag(AppIntroFragmentExtended.class);

    private int bgColor, titleColor, descColor, layoutId;
    private String drawable, title, titleTypeface, description, descTypeface;

    private LinearLayout mainLayout;

    public AppIntroFragmentExtended() {
    }


    public static AppIntroFragmentExtended newInstance(CharSequence title, CharSequence description, String imageDrawable, int bgColor) {
        return newInstance(title, description, imageDrawable, bgColor, 0, 0);
    }


    public static AppIntroFragmentExtended newInstance(CharSequence title, CharSequence description, String imageDrawable, int bgColor, int titleColor, int descColor) {
        AppIntroFragmentExtended sampleSlide = new AppIntroFragmentExtended();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_TITLE, title);
        args.putCharSequence(ARG_DESC, description);
        args.putString(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_TITLE_COLOR, titleColor);
        args.putInt(ARG_DESC_COLOR, descColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (getArguments() != null && getArguments().size() != 0) {
            drawable = getArguments().getString(ARG_DRAWABLE);
            title = getArguments().getString(ARG_TITLE);
            titleTypeface = getArguments().containsKey(ARG_TITLE_TYPEFACE) ?
                    getArguments().getString(ARG_TITLE_TYPEFACE) : "";
            description = getArguments().getString(ARG_DESC);
            descTypeface = getArguments().containsKey(ARG_DESC_TYPEFACE) ?
                    getArguments().getString(ARG_DESC_TYPEFACE) : "";
            bgColor = getArguments().getInt(ARG_BG_COLOR);
            titleColor = getArguments().containsKey(ARG_TITLE_COLOR) ?
                    getArguments().getInt(ARG_TITLE_COLOR) : 0;
            descColor = getArguments().containsKey(ARG_DESC_COLOR) ?
                    getArguments().getInt(ARG_DESC_COLOR) : 0;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            drawable = savedInstanceState.getString(ARG_DRAWABLE);
            title = savedInstanceState.getString(ARG_TITLE);
            titleTypeface = savedInstanceState.getString(ARG_TITLE_TYPEFACE);
            description = savedInstanceState.getString(ARG_DESC);
            descTypeface = savedInstanceState.getString(ARG_DESC_TYPEFACE);
            bgColor = savedInstanceState.getInt(ARG_BG_COLOR);
            titleColor = savedInstanceState.getInt(ARG_TITLE_COLOR);
            descColor = savedInstanceState.getInt(ARG_DESC_COLOR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_DRAWABLE, drawable);
        outState.putString(ARG_TITLE, title);
        outState.putString(ARG_DESC, description);
        outState.putInt(ARG_BG_COLOR, bgColor);
        outState.putInt(ARG_TITLE_COLOR, titleColor);
        outState.putInt(ARG_DESC_COLOR, descColor);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), container, false);
        TextView t = (TextView) v.findViewById(com.github.paolorotolo.appintro.R.id.title);
        TextView d = (TextView) v.findViewById(com.github.paolorotolo.appintro.R.id.description);
        ImageView i = (ImageView) v.findViewById(com.github.paolorotolo.appintro.R.id.image);
        mainLayout = (LinearLayout) v.findViewById(com.github.paolorotolo.appintro.R.id.main);


        t.setText(title);
        if (titleColor != 0) {
            t.setTextColor(titleColor);
        }
        if (titleTypeface != null && titleTypeface.equals("")) {
            if (CustomFontCache.get(titleTypeface, getContext()) != null) {
                t.setTypeface(CustomFontCache.get(titleTypeface, getContext()));
            }
        }
        d.setText(description);
        if (descColor != 0) {
            d.setTextColor(descColor);
        }
        if (descTypeface != null && descTypeface.equals("")) {
            if (CustomFontCache.get(descTypeface, getContext()) != null) {
                d.setTypeface(CustomFontCache.get(descTypeface, getContext()));
            }
        }
        
        i.setImageDrawable(new IconicsDrawable(getActivity()).icon(drawable).color(Color.WHITE).sizeDp(64));
        mainLayout.setBackgroundColor(bgColor);

        return v;
    }

    @Override
    public void onSlideDeselected() {
        LogHelper.d(TAG, String.format("Slide %s has been deselected.", title));
    }

    @Override
    public void onSlideSelected() {
        LogHelper.d(TAG, String.format("Slide %s has been selected.", title));
    }

    @Override
    public int getDefaultBackgroundColor() {
        return bgColor;
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        mainLayout.setBackgroundColor(backgroundColor);
    }

    @Override
    protected int getLayoutId() {
        return com.github.paolorotolo.appintro.R.layout.fragment_intro;
    }
}
