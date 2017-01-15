package com.example.android.mdc.services;

import com.example.android.mdc.models.LogedIn;
import com.example.android.mdc.models.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
}
