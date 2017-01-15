package com.example.android.mdc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.utils.SharedPreference;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TextView id = (TextView) findViewById(R.id.testId);
        TextView name = (TextView) findViewById(R.id.testName);
        TextView email = (TextView) findViewById(R.id.testEmail);
        TextView token = (TextView) findViewById(R.id.testToken);

        startActivity(new Intent(this, WelcomeActivity.class));

        SharedPreference prefs = new SharedPreference(this);

        id.setText(prefs.getValue("userId"));
        name.setText(prefs.getValue("userName"));
        email.setText(prefs.getValue("userEmail"));
        token.setText(prefs.getValue("userToken"));

//        if(!id.equals(2)){
//            startActivity(new Intent(this, LoginActivity.class));
//        }

    }
}
