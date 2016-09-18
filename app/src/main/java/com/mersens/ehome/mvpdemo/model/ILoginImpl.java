package com.mersens.ehome.mvpdemo.model;

import android.content.Context;
import android.util.Log;
import com.mersens.ehome.mvpdemo.entity.UserBean;
import com.mersens.ehome.mvpdemo.service.IMyServiceGet;
import com.mersens.ehome.mvpdemo.service.IMyServicePost;
import com.mersens.ehome.mvpdemo.utils.RetrofitManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Mersens on 2016/9/18.
 */
public class ILoginImpl implements ILogin {
    private RetrofitManager manager;
    public ILoginImpl(Context context){
        manager= RetrofitManager.getInstance(context);
    }
    @Override
    public void login(String name, String psd, final OnLoginListener listener) {
        listener.onStart();
        IMyServicePost service= manager.mRetrofit.create(IMyServicePost.class);
        Call<ResponseBody> call=service.login(name,psd);
        manager.execute(call, new RetrofitManager.RequestCallBack() {
            @Override
            public void onSueecss(String msg) {
                UserBean u;
                try {
                    JSONObject jsonObject=new JSONObject(msg);
                    u = new UserBean();
                    u.setUserName(jsonObject.getString("name"));
                    u.setPsd(jsonObject.getString("psd"));
                    u.setType(jsonObject.getString("type"));
                    listener.onSoccess(u);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String msg) {
                listener.onErroe(new Exception(msg));

            }

            @Override
            public void onStart() {


            }

            @Override
            public void onFinish() {
                listener.onFinish();

            }
        });
    }
}
