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
import com.example.android.mdc.services.ApiParams;
import com.example.android.mdc.utils.SharedPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

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
    SoftKeyboard softKeyboard;
    ApiParams api = new ApiParams();

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

        if(prefs.getValue("userId")!=null && !prefs.getValue("userId").equals("")){
            email.setText(prefs.getValue("userEmail"));
            setAvatar(prefs.getValue("userId"));
        }

        title1.setTypeface(robotoLight);
        title2.setTypeface(robotoBold);


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
                    tryLoginUser(emailText, passText);
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
    //this method will check if email text is valid
    public static boolean isEmailValid(String email){
        return !(email==null) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //this method willl check if the password has at least the minimum length required
    public static boolean isTextLongEnough(int amount, String text){
        return text!=null && text.length()>=amount;
    }

    //this method will toggle the visibility of the loader
    public void toggleLoader(boolean visible){
        if(visible){
            overlay.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
        }else{
            loader.setVisibility(View.INVISIBLE);
            overlay.setVisibility(View.INVISIBLE);
        }
    }

    //this method will try to login the user
    public void tryLoginUser(final String email, String password){
        RequestParams data = new RequestParams();
        data.put("email", email);
        data.put("password", password);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(api.getLoginUrl(), data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v("Jandro", "It worked. Status"+statusCode);
                Log.v("Jandro", "Body: "+new String(responseBody));
                JSONObject body;
                try {
                    body = new JSONObject(new String(responseBody));
                    SharedPreference prefs = new SharedPreference(ctx);
                    prefs.setValue("userEmail", email);
                    prefs.setValue("userToken", body.getString("token"));
                    getUserData(body.getString("token"));
                }catch(JSONException e){
                    toggleLoader(false);
                    alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+". Error description: "+e.getMessage()).setPositiveButton("OK", null).create().show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("Jandro", "There was an error. Status"+statusCode);
                Log.v("Jandro", "Body: "+new String(responseBody));
                toggleLoader(false);
                if(statusCode==403){
                    alertBuilder.setTitle(R.string.login_invalid_user_title).setMessage(R.string.login_invalid_user_message).setPositiveButton("OK", null).create().show();
                }else{
                    alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+". Error Code: "+statusCode).setPositiveButton("OK", null).create().show();
                }

            }
        });
    }

    //this method will fetch the user data in the server once logedIn
    public void getUserData(String token){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(api.getBaseUrl()+"user?token="+token, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                JSONObject response;
                try{
                    response = new JSONObject(new String(responseBody));
                    prefs.setValue("userId", response.getString("id"));
                    prefs.setValue("userName", response.getString("name"));
                    startActivity(new Intent(ctx, WelcomeBack.class));
                }catch(JSONException e){
                    alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+". Error description: "+e.getMessage()).setPositiveButton("OK", null).create().show();
                }
                toggleLoader(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                toggleLoader(false);
                alertBuilder.setTitle(R.string.error_getting_user_data).setMessage(R.string.error_getting_user_description+" Error code: "+statusCode).setPositiveButton("OK", null).create().show();
            }
        });
    }

    //this method will set the user's avatar (if has one)
    public void setAvatar(final String userId){
        new Thread() {
            public void run() {
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con =  (HttpURLConnection) new URL(api.getBaseUrl()+"avatar/"+userId).openConnection();
                    con.setRequestMethod("HEAD");
                    if( (con.getResponseCode() == HttpURLConnection.HTTP_OK) ){
                        Log.v("Jandro", "Has Avatar");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(ctx).load(api.getBaseUrl()+"avatar/"+userId).transform(new CircleTransform()).into(logo);
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
