package com.example.android.mdc.services;

import com.example.android.mdc.models.JobsData;
import com.example.android.mdc.models.LogedIn;
import com.example.android.mdc.models.Person;
import com.example.android.mdc.models.SignUp;
import com.example.android.mdc.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Jandro on 1/14/2017.
 */

public interface ApiService {
    @GET("test")
    Call<List<Person>> getPersonDetails();

    @FormUrlEncoded
    @POST("api/auth/login")
    Call<LogedIn> logInUser(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/auth/signup")
    Call<SignUp> SignUpUser(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @GET("api/user")
    Call<User> getUSerDetails(@Header("Authorization") String token);

    @GET("api/jobsdata")
    Call<JobsData> getJobsData(@Header("Authorization") String token);
}
