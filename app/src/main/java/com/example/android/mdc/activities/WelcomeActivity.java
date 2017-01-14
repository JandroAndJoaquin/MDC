package com.example.android.mdc.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.android.mdc.fragments.slide_1;
import com.example.android.mdc.fragments.slide_2;
import com.example.android.mdc.fragments.slide_3;
import com.github.paolorotolo.appintro.AppIntro;

public class WelcomeActivity extends AppIntro implements slide_1.OnFragmentInteractionListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //setDepthAnimation();

        addSlide(slide_1.newInstance());
        addSlide(slide_2.newInstance());
        addSlide(slide_3.newInstance());


        showSkipButton(true);
        setProgressButtonEnabled(true);

        setVibrate(true);
        setVibrateIntensity(30);
    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
