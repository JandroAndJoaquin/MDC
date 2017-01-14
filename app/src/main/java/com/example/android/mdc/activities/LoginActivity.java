package com.example.android.mdc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.helpers.SoftKeyboard;

public class LoginActivity extends AppCompatActivity {
    //declare local variables
    TextView title1, title2;
    FrameLayout rootV;
    Context ctx;
    ImageView logo;
    Button submitBtn;
    AlertDialog.Builder alertBuilder;
    EditText email, password;

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
        submitBtn = (Button) findViewById(R.id.login_login_button);
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        email = (EditText) findViewById(R.id.login_email_input);
        password = (EditText) findViewById(R.id.login_pass_input);
        alertBuilder = new AlertDialog.Builder(ctx);

        title1.setTypeface(robotoLight);
        title2.setTypeface(robotoBold);

        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard(rootV, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override

            public void onSoftKeyboardShow(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logo.setVisibility(View.INVISIBLE);
                        logo.setVisibility(View.GONE);
                    }
                });
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();
                String passText = password.getText().toString();
                if(!isEmailValid(emailText)){
                    alertBuilder.setTitle(R.string.login_check_email).setMessage(emailText.equals("")?R.string.login_forgot_email:R.string.login_invalid_email).setPositiveButton("OK", null).create().show();
                }else if(passText.equals("")){
                    alertBuilder.setTitle(R.string.login_check_pass).setMessage(R.string.login_forgot_pass).setPositiveButton("OK", null).create().show();
                }else if(!isTextLongEnough(8, passText)){
                    alertBuilder.setTitle(R.string.login_check_pass).setMessage(R.string.login_too_short_pass).setPositiveButton("OK", null).create().show();
                }else{
                    startActivity(new Intent(ctx, HomeActivity.class));
                }
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
    public static boolean isEmailValid(String email){
        return !(email==null) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isTextLongEnough(int amount, String text){
        return text!=null && text.length()>amount;
    }
}
