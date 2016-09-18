package com.mersens.ehome.mvpdemo.model;

import com.mersens.ehome.mvpdemo.entity.UserBean;


/**
 * Created by Mersens on 2016/9/14.
 */
public interface OnLoginListener {
    void onStart();
    void onSoccess(UserBean u);
    void onErroe(Exception e);
    void onFinish();
}
