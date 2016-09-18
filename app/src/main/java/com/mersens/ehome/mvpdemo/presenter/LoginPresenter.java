package com.mersens.ehome.mvpdemo.presenter;

import android.content.Context;
import com.mersens.ehome.mvpdemo.entity.UserBean;
import com.mersens.ehome.mvpdemo.model.ILogin;
import com.mersens.ehome.mvpdemo.model.ILoginImpl;
import com.mersens.ehome.mvpdemo.model.OnLoginListener;
import com.mersens.ehome.mvpdemo.view.ILoginView;

/**
 * Created by Mersens on 2016/9/18.
 */
public class LoginPresenter {
    private ILoginView mILoginView;
    private ILogin mILogin;
    private Context context;

    public LoginPresenter(ILoginView mILoginView){
        context=(Context)mILoginView;
        this.mILoginView=mILoginView;
        this.mILogin=new ILoginImpl(context);
    }
public void cancel(){
    mILoginView.cancel();
}

    public void doLogin(){
        mILogin.login(mILoginView.getUserName(),mILoginView.getPsd(), new OnLoginListener() {
            @Override
            public void onStart() {
                mILoginView.showPro();
            }

            @Override
            public void onSoccess(UserBean u) {
                mILoginView.onSoccess(u);

            }

            @Override
            public void onErroe(Exception e) {
                mILoginView.onErroe(e);

            }

            @Override
            public void onFinish() {
                mILoginView.hidePro();
            }
        });

    }

}
