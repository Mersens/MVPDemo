package com.mersens.ehome.mvpdemo.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by Mersens on 2016/9/18.
 */
public interface IMyServiceGet {
    @GET("LoginAction")
    Call<ResponseBody> login();
}
