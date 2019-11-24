package com.example.andriod.ingredishare.main;

import android.content.Context;
import android.util.Log;

import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.MyApplication;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityPresenter {

    private MainView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;
    private FirebaseAuth mFirebaseAuth;

    public MainActivityPresenter(DataManager dataManager, MainView view) {
        this.dataManager = dataManager;
        this.view = view;
        mContext = MyApplication.getContext();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
    }

    public void facebookCallbackResponse(String response, LoginResult loginResult,
                                         FacebookException error){
        switch(response) {
            case "success" :
                AccessToken accessToken = loginResult.getAccessToken();
                view.attemptFirebaseAuthWithFacebook(accessToken);
            case "cancel" :
                Log.e(this.getClass().toString(), "facebook signin cancel");

            case "error" :
                Log.e(this.getClass().toString(), "facebook signin error ", error);

            default :
        }
    }

}
