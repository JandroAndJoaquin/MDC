package com.example.android.mdc.services;

/**
 * Created by Jandro on 2/25/2017.
 */

public class ApiParams {
    public String getBaseUrl() {
        return baseUrl;
    }
    String baseUrl = "http://jandrorojas.xyz/api/";

    public String getLoginUrl() {
        return loginUrl;
    }
    String loginUrl = baseUrl+"auth/login";
}
