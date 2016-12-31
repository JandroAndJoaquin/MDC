package com.example.android.mdc.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.mdc.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("Roboto-Thin.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
