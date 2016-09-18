package com.mersens.ehome.mvpdemo.model;

/**
 * Created by Mersens on 2016/9/18.
 */
public interface ILogin {
    void login(String name,String psd,OnLoginListener listener);
}
