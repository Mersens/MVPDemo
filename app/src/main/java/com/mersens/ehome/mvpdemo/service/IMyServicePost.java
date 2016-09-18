package com.mersens.ehome.mvpdemo.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Mersens on 2016/9/18.
 */
public interface IMyServicePost {
    @POST("LoginAction")
    Call<ResponseBody> login(@Query("name") String name, @Query("psd") String psd);
}
