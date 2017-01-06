package com.example.android.mdc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.mdc.R;

import static com.example.android.mdc.R.id.sign_up_button;

public class SignUpActivity extends AppCompatActivity {
    TextView title1, title2;
    FrameLayout rootView;
    Context ctx;
    AlertDialog.Builder builder;
    EditText email;
    EditText name;
    EditText password;
    EditText re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /*this is a test

         */
        ctx = SignUpActivity.this;
        rootView = (FrameLayout) findViewById(R.id.login_root_view_1);
        title1 = (TextView) findViewById(R.id.login_title_1);
        title2 = (TextView) findViewById(R.id.login_title_2);
        name = (EditText) findViewById(R.id.login_sign_up_1);
        email = (EditText) findViewById(R.id.login_sign_up_2);
        password = (EditText) findViewById(R.id.login_sign_up_3);
        re_password = (EditText) findViewById(R.id.login_sign_up_4);


        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        title1.setTypeface(robotoLight);
        title2.setTypeface(robotoBold);

        /*This is an Intent to go to LoginActivity*/

        android.widget.TextView signUpView = (TextView) findViewById(R.id.login_sign_up);

        signUpView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });



        /*this is for signUp button*/

        builder = new AlertDialog.Builder(this);
        Button button=(Button) findViewById(sign_up_button);
        button.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
                if(isEmpty(name.getText().toString())){
                    builder.setTitle(R.string.dialog_title_name).setMessage(R.string.dialog_text_name).setPositiveButton(R.string.dialog_ok, null).create().show();
                }else if(isEmpty(email.getText().toString())){
                    builder.setTitle(R.string.dialog_title_email).setMessage(R.string.dialog_text_email).setPositiveButton(R.string.dialog_ok, null).create().show();
                        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    builder.setTitle(R.string.dialog_title_email).setMessage(R.string.dialog_text_misspelled).setPositiveButton(R.string.dialog_ok, null).create().show();

                }else if (isEmpty(password.getText().toString())){
                    builder.setTitle(R.string.dialog_title_password_2).setMessage(R.string.dialog_text_password_2).setPositiveButton(R.string.dialog_ok, null).create().show();
                }else if (isLoongEnogh(8,password.getText().toString())){
                    builder.setTitle(R.string.dialog_title_password_1).setMessage(R.string.dialog_text_password_1).setPositiveButton(R.string.dialog_ok, null).create().show();

                } else if (password.getText().toString().equals(re_password.getText().toString())){

 /*this is an intent to go to LoginActivity, we should change this looking forward*/

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else {
                    builder.setTitle(R.string.dialog_title_re_password).setMessage(R.string.dialog_text_re_password).setPositiveButton(R.string.dialog_ok, null).create().show();
                    password.setText(null);
                    re_password.setText(null);
                }
            }


        });



    }

    public boolean isEmpty(String t){
        return t.equals("");
    }

    public boolean isLoongEnogh(int length, String txt){
        return txt.length()<length;
    }
}


