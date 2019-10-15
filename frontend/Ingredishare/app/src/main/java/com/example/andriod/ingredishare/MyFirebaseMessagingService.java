package com.example.andriod.ingredishare;

import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(String token) {
//        super.onNewToken(s);
        /* TODO: send to backend for saving, to target specific device for push notifs */
        String url = getString(R.string.server_url) + getString(R.string.save_fcm_tok_put);

        JSONObject putparams = new JSONObject();
        String email = MyApplication.getUserEmail();

        MyApplication.setUserEmail(email);

        try {
            putparams.put("email", email);
            putparams.put("token", token);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, putparams,
                    (JSONObject response) -> {
                        Log.println(Log.DEBUG, "resp", "success");
                    },

                    (VolleyError error) -> {
                        Log.println(Log.DEBUG, "resp", "error");
                    }
            );

            GlobalRequestQueue reqQueue = GlobalRequestQueue.getInstance();
            reqQueue.addToRequestQueue(jsonObjReq, "put");
        } catch(JSONException jsonEx) {

        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAG", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


    }

    @Override
    public void onDeletedMessages() {
//        super.onDeletedMessages();

    }
}
