package com.example.android.mdc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.helpers.SoftKeyboard;

public class LoginActivity extends AppCompatActivity {

    TextView title1, title2;
    FrameLayout rootV;
    Context ctx;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = LoginActivity.this;
        rootV = (FrameLayout) findViewById(R.id.login_root_view);
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        logo = (ImageView) findViewById(R.id.login_logo);
        title1 = (TextView) findViewById(R.id.login_title_1);
        title2 = (TextView) findViewById(R.id.login_title_2);

        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        title1.setTypeface(robotoLight);
        title2.setTypeface(robotoBold);

        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard(rootV, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                Log.v("JANDRO", "Hidden");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                Log.v("JANDRO", "Shown");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.setVisibility(View.INVISIBLE);
                        logo.setVisibility(View.GONE);
                    }
                });
            }
        });


        /*This is an Intent to go to SingUpActivity*/

        android.widget.TextView signUpView = (TextView) findViewById(R.id.login_sign_up);

        signUpView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }

        });


    }
}
