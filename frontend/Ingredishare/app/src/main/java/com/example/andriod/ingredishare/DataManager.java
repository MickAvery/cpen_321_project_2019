package com.example.andriod.ingredishare;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager {
    private static DataManager dataManager;
    private final Context context;
    private GlobalRequestQueue mReqQueue;


    public DataManager(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        dataManager = new DataManager(context);
    }

    /*
    REST API function 'post' that communicates with backend
    @param request_url
    @param JSONObject postparams
     */
    public void postJSONObject(String request_url, JSONObject postparams,
                               Response.Listener<JSONObject> listener,
                               Response.ErrorListener errorListener){

        Log.e(this.getClass().toString(), "inside data manager");
        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    request_url, postparams, listener,
                    errorListener);

            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonObjReq, "post");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    REST API function 'get' that communicates with backend
    @param request_url
    @param JSONArray paramarray
    */
    public void getJSONArray(String request_url, JSONArray paramArray,
                             Response.Listener<JSONArray> listener,
                             Response.ErrorListener errorListener){

        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    request_url, paramArray, listener, errorListener);
            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonArrayRequest,"get");

        } catch(Exception e) {

        }
    }

    /*
   REST API function 'get' that communicates with backend
   @param request_url
   @param JSONObject paramarray
   */
    public void getJSONObject(String request_url, JSONObject paramArray,
                              Response.Listener<JSONObject> listener,
                              Response.ErrorListener errorListener){

        try {
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest (Request.Method.GET,
                    request_url, paramArray, listener, errorListener);
            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonArrayRequest,"get");

        } catch(Exception e) {

        }
    }
}