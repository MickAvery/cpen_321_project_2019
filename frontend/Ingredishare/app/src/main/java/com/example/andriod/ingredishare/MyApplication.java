package com.example.andriod.ingredishare;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication mContext;
    private static String mUserEmail;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static MyApplication getContext() {
        return mContext;
    }

    public static String getUserEmail() {
        return mUserEmail;
    }

    public static void setUserEmail(String email) {
        mUserEmail = email;
    }
}
