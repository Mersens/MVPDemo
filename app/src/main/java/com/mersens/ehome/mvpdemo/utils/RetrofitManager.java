package com.mersens.ehome.mvpdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mersens on 2016/9/18.
 */
public class RetrofitManager {
    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    private Context mContext;
    public Retrofit mRetrofit;
    private static RetrofitManager manager;
    protected Map<String, String> params;
    public OkHttpClient mClient;
    /**
     * 网络请求接口，用于数据回传
     */
    public interface RequestCallBack {
        void onSueecss(String msg);
        void onError(String msg);
        void onStart();
        void onFinish();
    }

    private RetrofitManager(Context context) {
        this.mContext = context;
        File httpCacheDirectory = new File(mContext.getCacheDir(), "responses");
        int cacheSize = 32 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.addNetworkInterceptor(getNetWorkInterceptor());
        builder.addInterceptor(getInterceptor());
        builder.cache(cache);
        mClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.56.1:8080/mvpservice/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build();
    }

    //单例模式，对提供管理者实例
    public static RetrofitManager getInstance(Context context) {
        if (manager == null) {
            synchronized (RetrofitManager.class) {
                if (manager == null) {
                    manager = new RetrofitManager(context);
                }
            }
        }
        return manager;
    }

    public void execute(Call<ResponseBody> call, final RequestCallBack callBack) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                doRequest(response, callBack);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callBack.onError(t.toString());
            }
        });
    }


    public void doUpload(String url, File file, final RequestCallBack callBack) {
        if (file == null) {
            Log.e("OkHttpClientManager", "文件为空！");
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addParams(builder);
        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file);
        builder.addFormDataPart("upload", "img", fileBody);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        doRequest(request, callBack);
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    public void doRequest(final retrofit2.Response<ResponseBody> response, final RequestCallBack callBack) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                try {
                    if (response.isSuccessful()) {
                        subscriber.onNext(response.body().string());
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new IOException("Unexpected code " + response));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    callBack.onError(e.toString());
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())//设置执行耗时操作（线程池）
                .observeOn(AndroidSchedulers.mainThread())//Rxjava的返回结果运行在主线程
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onCompleted() {
                        Log.e("onCompleted","onCompleted");
                        callBack.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.toString());
                    }


                    @Override
                    public void onNext(Object o) {
                        Log.e("onNext","onNext");
                        callBack.onSueecss(o.toString());
                    }
                });
    }

    public void doRequest(final Request request, final RequestCallBack callBack) {
        callBack.onStart();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {

                try {
                    Response response = mClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        subscriber.onNext(response.body().string());
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new IOException("Unexpected code " + response));
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    callBack.onError(e.toString());
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io())//设置执行耗时操作（线程池）
                .observeOn(AndroidSchedulers.mainThread())//Rxjava的返回结果运行在主线程
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onCompleted() {
                        callBack.onFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onError(e.toString());
                    }

                    @Override
                    public void onNext(Object o) {
                        callBack.onSueecss(o.toString());
                    }
                });
    }



    public Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetWork.isNetworkConnected(mContext)) {
                    Log.e("TAG", "Interceptor没有网络连接");
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

    public Interceptor getNetWorkInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if (NetWork.isNetworkConnected(mContext)) {
                    int maxAge = 1 * 60;
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 7;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
    }
}
