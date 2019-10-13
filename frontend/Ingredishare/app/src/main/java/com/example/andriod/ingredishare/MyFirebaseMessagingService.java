package com.example.andriod.ingredishare;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(String token) {
//        super.onNewToken(s);
        Log.d("TAG", "Refreshed token: " + token);
    }
}
