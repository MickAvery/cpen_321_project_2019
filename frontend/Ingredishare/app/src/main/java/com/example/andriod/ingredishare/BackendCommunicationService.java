package com.example.andriod.ingredishare;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class BackendCommunicationService {

    private GlobalRequestQueue mReqQueue;
    public Boolean success;
    public JSONArray output;

    public BackendCommunicationService() {
        success = false;
    }

    /*
    REST API function 'post' that communicates with backend
    @param request_url
    @param JSONObject postparams
    @returns boolean, true if post was successful
     */
    public boolean post(String request_url, JSONObject postparams, String responseSuccessID){
      //  AtomicReference<Boolean> success = new AtomicReference<>();
        success = false;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean(responseSuccessID);
                            if(success_response) {
                                success = true;
                                Log.e(this.getClass().toString(), "success from backend received");
                            }
                        } catch (JSONException jsonEx) {
                            Log.e(this.getClass().toString(), jsonEx.toString());
                        }

                    },

                    (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError",  error)
            );

            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonObjReq, "post");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("resp = ", success.toString());
        return success;
    }

    /*
    REST API function 'get' that communicates with backend
    @param request_url
    @param JSONArray paramarray
    @returns JSONArray output
    */
    public JSONArray get(String request_url, JSONArray paramArray){
        output = new JSONArray();

        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    request_url, paramArray,
                    (JSONArray response) -> {
                        Log.e(this.getClass().toString(), response.toString());
                        try {
                            Log.e(this.getClass().toString(), response.toString());
                            if(response.length() != 0) {
                                Log.e(this.getClass().toString(), "got stuff from backend");

                                for(int i=0; i<response.length();i++){
                                    output.put(i, response.getJSONObject(i));
                                }
                            }
                        } catch (JSONException jsonEx) {
                            Log.e(this.getClass().toString(), jsonEx.toString());
                        }

                    },

                    (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError",  error)
            );
            // Add JsonArrayRequest to the RequestQueue
            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonArrayRequest,"get");

        } catch(Exception e) {

        }

        Log.e("resp", output.toString());
        return output;
    }
}
