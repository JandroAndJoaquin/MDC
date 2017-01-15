package com.example.android.mdc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.helpers.CircleTransform;
import com.example.android.mdc.helpers.SoftKeyboard;
import com.example.android.mdc.models.LogedIn;
import com.example.android.mdc.models.User;
import com.example.android.mdc.services.ApiService;
import com.example.android.mdc.utils.SharedPreference;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    TextView title1, title2, signUpLink;
    FrameLayout rootV;
    Context ctx;
    ImageView logo;
    Button submitBtn;
    AlertDialog.Builder alertBuilder;
    EditText email, password;
    SharedPreference prefs;
    View overlay;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = LoginActivity.this;
        prefs = new SharedPreference(ctx);
        rootV = (FrameLayout) findViewById(R.id.login_root_view);
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        logo = (ImageView) findViewById(R.id.login_logo);
        title1 = (TextView) findViewById(R.id.login_title_1);
        title2 = (TextView) findViewById(R.id.login_title_2);
        submitBtn = (Button) findViewById(R.id.login_login_button);
        signUpLink= (TextView) findViewById(R.id.login_sign_up);
        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        email = (EditText) findViewById(R.id.login_email_input);
        password = (EditText) findViewById(R.id.login_pass_input);
        alertBuilder = new AlertDialog.Builder(ctx);
        overlay = findViewById(R.id.login_loader_overlay);
        loader = (ProgressBar) findViewById(R.id.login_loader);

        if(!prefs.getValue("userId").equals("")){
            email.setText(prefs.getValue("userEmail"));
            setAvatar(prefs.getValue("userId"));
        }

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
                    toggleLoader(true);
                    tryloginUser(emailText, passText);
                }
            }
        });
        signUpLink.setOnClickListener(new View.OnClickListener() {

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
        return text!=null && text.length()>=amount;
    }

    public void toggleLoader(boolean visible){
        if(visible){
            overlay.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
        }else{
            loader.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.INVISIBLE);
        }
    }


    private void tryloginUser(final String email, String password){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://jandrorojas.xyz/public/").addConverterFactory(GsonConverterFactory.create()).build();
        final ApiService service = retrofit.create(ApiService.class);
        Call<LogedIn> call = service.logInUser(email, password);

        call.enqueue(new Callback<LogedIn>() {

            @Override
            public void onResponse(Call<LogedIn> call, Response<LogedIn> response) {
                Log.v("MyToken", "Code: "+response.raw().code()+" | Message: "+response.raw().message());
                if(response.raw().code()==403){
                    toggleLoader(false);
                    alertBuilder.setTitle(R.string.login_invalid_user_title).setMessage(R.string.login_invalid_user_message).setPositiveButton("OK", null).create().show();
                }else{
                    LogedIn logedIndata = response.body();
                    SharedPreference prefs = new SharedPreference(ctx);
                    prefs.setValue("userEmail", email);
                    prefs.setValue("userToken", logedIndata.getToken());
                    getUserData(logedIndata.getToken());
                }
            }

            @Override
            public void onFailure(Call<LogedIn> call, Throwable t) {
                Log.e("Jandro", t.getMessage());
                toggleLoader(false);
                alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description).setPositiveButton("OK", null).create().show();
            }

        });
    }

    public void getUserData(String token){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://jandrorojas.xyz/public/").addConverterFactory(GsonConverterFactory.create()).build();
        final ApiService service = retrofit.create(ApiService.class);
        Call<User> userCall = service.getUSerDetails("Bearer{"+token+"}");
        userCall.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userInfo = response.body();
                Log.v("Jandro", "UserId: "+userInfo.getId());
                if(response.raw().code()==403){
                    toggleLoader(false);
                    alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description).setPositiveButton("OK", null).create().show();
                }else{
                    prefs.setValue("userId", userInfo.getId());
                    prefs.setValue("userName", userInfo.getName());
                    startActivity(new Intent(ctx, HomeActivity.class));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.v("Jandro", t.getMessage());
                toggleLoader(false);
                alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description).setPositiveButton("OK", null).create().show();
            }
        });
    }

    public void setAvatar(final String userId){
        new Thread() {
            public void run() {
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con =  (HttpURLConnection) new URL("http://jandrorojas.xyz/app/Assets/Images/profile_images/"+userId+".jpg").openConnection();
                    con.setRequestMethod("HEAD");
                    if( (con.getResponseCode() == HttpURLConnection.HTTP_OK) ){
                        Log.v("Jandro", "Has Avatar");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(ctx).load("http://jandrorojas.xyz/app/Assets/Images/profile_images/"+userId+".jpg").transform(new CircleTransform()).into(logo);
                            }
                        });
                    }
                    else
                        Log.v("Jandro", "Doesn\'t Has Avatar");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
