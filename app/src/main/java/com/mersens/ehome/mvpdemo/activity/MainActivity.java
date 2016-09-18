package com.mersens.ehome.mvpdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mersens.ehome.mvpdemo.R;
import com.mersens.ehome.mvpdemo.entity.UserBean;
import com.mersens.ehome.mvpdemo.presenter.LoginPresenter;
import com.mersens.ehome.mvpdemo.view.ILoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ILoginView {

    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_psd)
    EditText edtPsd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    LoginPresenter mPresenter;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new LoginPresenter(this);
    }

    @OnClick({R.id.btn_login, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                mPresenter.doLogin();
                break;
            case R.id.btn_cancel:
                mPresenter.cancel();
                break;
        }
    }

    @Override
    public void hidePro() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showPro() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public String getUserName() {
        return edtName.getText().toString();
    }

    @Override
    public String getPsd() {
        return edtPsd.getText().toString();
    }

    @Override
    public void cancel() {
        edtName.setText("");
        edtPsd.setText("");
    }

    @Override
    public void onSoccess(UserBean u) {
        tvMsg.setText(u.toString());

    }

    @Override
    public void onErroe(Exception e) {

    }
}
