package com.example.android.mdc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.mdc.R;
import com.example.android.mdc.models.SignUp;
import com.example.android.mdc.services.ApiService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.mdc.R.id.login_sign_up_1;
import static com.example.android.mdc.R.id.sign_up_button;


public class SignUpActivity extends AppCompatActivity {
    TextView title1, title2;
    EditText title3, email, name, password, re_password;
    FrameLayout rootView;
    Context ctx;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ctx = SignUpActivity.this;
        title1 = (TextView) findViewById(R.id.login_title_1);
        title2 = (TextView) findViewById(R.id.login_title_2);
        name = (EditText) findViewById(login_sign_up_1);
        email = (EditText) findViewById(R.id.login_sign_up_2);
        password = (EditText) findViewById(R.id.login_sign_up_3);
        re_password = (EditText) findViewById(R.id.login_sign_up_4);
        rootView = (FrameLayout) findViewById(R.id.login_root_view_1);
        title3 = (EditText) findViewById(R.id.login_sign_up_1);



        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        title1.setTypeface(robotoLight);
        title2.setTypeface(robotoBold);

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

//                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                    startActivity(intent);
                    trySignUp(name.getText().toString(), email.getText().toString(), password.getText().toString());
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

    public void signUpUser(String name, String email, String password){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://jandrorojas.xyz/public/").addConverterFactory(GsonConverterFactory.create()).build();
        final ApiService service = retrofit.create(ApiService.class);
        Call<SignUp> call = service.SignUpUser(name, email, password);

        call.enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(Call<SignUp> call, Response<SignUp> response) {
                Log.v("Jandro", "SignUp Response Code: "+response.raw().code());
                Response<SignUp> rp = response;
                Log.v("Jandro", "Content: "+response.errorBody());
                if(response.raw().code()==201){
                    builder.setTitle(R.string.signup_ok_dialog_title).setMessage(R.string.signup_ok_dialog).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ctx, LoginActivity.class));
                        }
                    }).create().show();
                }else{
                    builder.setTitle(R.string.sign_up_wrong_dialog_title).setMessage(R.string.sign_up_wrong_dialog).setPositiveButton("OK", null).create().show();
                }
            }

            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {

            }
        });

    }

    public void trySignUp(String name, String email, String password){
        RequestParams data = new RequestParams();
        data.put("name", name);
        data.put("email", email);
        data.put("password", password);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://jandrorojas.xyz/api/auth/signup", data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==201){
                    builder.setTitle(R.string.signup_ok_dialog_title).setMessage(R.string.signup_ok_dialog).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ctx, LoginActivity.class));
                        }
                    }).create().show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v("Jandro", "Response: "+new String(responseBody)+". "+error.getMessage());
                builder.setTitle(R.string.sign_up_wrong_dialog_title).setMessage(getResources().getString(R.string.sign_up_wrong_dialog)+". Error code: "+statusCode+". Error Message: "+error.getMessage()).setPositiveButton("OK", null).create().show();
            }
        });
    }
}


