package com.example.andriod.ingredishare;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class GlobalRequestQueue {
    private static final GlobalRequestQueue ourInstance = new GlobalRequestQueue();
    private RequestQueue reqQueue;

    public static GlobalRequestQueue getInstance() {
        return ourInstance;
    }

    private GlobalRequestQueue() {
        reqQueue = Volley.newRequestQueue(MyApplication.getContext());
    }

    public RequestQueue getRequestQueue() {
        return reqQueue;
    }

    public void addToRequestQueue(Request req, String tag) {
        req.setTag(tag);
        reqQueue.add(req);
    }

    public void cancelAllRequests(String tag) {
        reqQueue.cancelAll(tag);
    }
}
