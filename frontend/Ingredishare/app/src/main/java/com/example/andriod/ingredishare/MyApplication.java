package com.example.andriod.ingredishare;

import android.app.Application;
import android.widget.ImageView;

import com.example.andriod.ingredishare.event.EventAdapter;

public class MyApplication extends Application {
    private static MyApplication mContext;
    private static String mUserEmail;
    private static DataManager mDataManager;
    private static EventAdapter mEventAdapter;

    private static final String serverURL = "http://localhost:1337";

    private static final String profileInfoGETRequestURL = serverURL + "/getProfileInfo";
    private static final String profileInfoPOSTRequestURL = serverURL + "/updateProfileInfo";
    private static final String getAllRequestsLatLongGETRequestURL = serverURL +
            "/getAllRequestsFromLatLong";
    private static final String getCreateRequestString = serverURL + "/createRequest";

    private static ImageView mNotificationImage;
    private static Integer expirationDate = 5;

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

    public static String urlCreateRequest(){
        return getCreateRequestString;
    }

    public static ImageView getNotificationImageView() {
        return mNotificationImage;
    }

    public static void setNotificationImageView(ImageView view) {
        mNotificationImage = view;
    }

    public static void setEventAdapter(EventAdapter eventAdapter){
        mEventAdapter = eventAdapter;
    }

    public static EventAdapter getEventAdapter(){ return mEventAdapter; }

    public static Integer getExpirationDate(){ return expirationDate; }
}
