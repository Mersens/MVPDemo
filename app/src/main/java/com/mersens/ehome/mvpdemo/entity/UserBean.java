package com.mersens.ehome.mvpdemo.entity;

/**
 * Created by Mersens on 2016/9/18.
 */
public class UserBean {
    private String userName;
    private String type;
    private String psd;
    @Override
    public String toString() {
        return "UserBean{" +
                "userName='" + userName + '\'' +
                ", type='" + type + '\'' +
                ", psd='" + psd + '\'' +
                '}';
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}
