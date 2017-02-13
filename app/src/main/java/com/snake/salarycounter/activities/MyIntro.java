package com.snake.salarycounter.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.snake.salarycounter.R;
import com.snake.salarycounter.fragments.AppIntroFragmentExtended;

public class MyIntro extends AppIntro2 {

    // Please DO NOT override onCreate. Use init.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragmentExtended.newInstance("Считайте", "Возможность предварительного расчета полагающегося денежного вознаграждения", "gmd-account-balance-wallet", ContextCompat.getColor(getBaseContext(), R.color.md_deep_orange_800)));
        addSlide(AppIntroFragmentExtended.newInstance("Планируйте", "Укажите список рабочих периодов. Получите возможность планирования денежных потоков, расчета отпускных", "gmd-assessment", ContextCompat.getColor(getBaseContext(), R.color.md_green_800)));
        addSlide(AppIntroFragmentExtended.newInstance("Ведите учет", "Учитывайте свои доходы, прикладывайте фотографии и другие изображения", "gmd-assignment", ContextCompat.getColor(getBaseContext(), R.color.md_yellow_800)));
        addSlide(AppIntroFragmentExtended.newInstance("Мотивируйтесь", "Добавьте на рабочий стол встроенный виджет или используйте ZooperWidget", "gmd-check-circle", ContextCompat.getColor(getBaseContext(), R.color.md_blue_800)));


        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        //setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        //setVibrate(true);
        //setVibrateIntensity(30);

        setFadeAnimation();

        // Put this in init()
        askForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3); // OR

        // This will ask for the camera permission AND the contacts permission on the same slide.
        // Ensure your slide talks about both so as not to confuse the user.
        //askForPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
