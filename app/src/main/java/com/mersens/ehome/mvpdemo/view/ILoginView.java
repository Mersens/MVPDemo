package com.mersens.ehome.mvpdemo.view;

import com.mersens.ehome.mvpdemo.entity.UserBean;

/**
 * Created by Mersens on 2016/9/18.
 */
public interface ILoginView {
    void hidePro();
    void showPro();
    String getUserName();
    String getPsd();
    void cancel();
    void onSoccess(UserBean u);
    void onErroe(Exception e);
}
