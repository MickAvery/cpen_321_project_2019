package com.example.andriod.ingredishare;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication mContext;
    private static String mUserEmail;
    private static DataManager mDataManager;

    private static final String serverURL = "http://10.0.2.2:1337";

    private static final String profileInfoGETRequestURL = serverURL + "/getProfileInfo";
    private static final String profileInfoPOSTRequestURL = serverURL + "/updateProfileInfo";
    private static final String getAllRequestsLatLongGETRequestURL = serverURL +
            "/getAllRequestsFromLatLong";

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

    public static void setDataManager(DataManager dataManager){
        mDataManager = dataManager;
    }

    public static DataManager getDataManager(){
        return mDataManager;
    }

    public static String getServerURL(){
        return serverURL;
    }

    public static String getURL_profileInfoGET(){
        return profileInfoGETRequestURL;
    }

    public static String getProfileInfoPOSTRequestURL(){
        return profileInfoPOSTRequestURL;
    }

    public static String getGetAllRequestsLatLongGETRequestURL(){
        return getAllRequestsLatLongGETRequestURL;
    }
}
