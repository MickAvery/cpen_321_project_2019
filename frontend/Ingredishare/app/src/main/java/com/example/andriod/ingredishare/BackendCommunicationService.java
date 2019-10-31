package com.example.andriod.ingredishare;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class BackendCommunicationService {

    private GlobalRequestQueue mReqQueue;
    private Boolean success;

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
        this.success = false;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(request_url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean(responseSuccessID);
                            if (success_response) {
                                Log.e(this.getClass().toString(), "updateProfile success");
                                this.success = true;
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
        return success;
    }

    /*
    REST API function 'get' that communicates with backend
    @param request_url
    @param JSONArray paramarray
    @returns JSONArray output
    */
    public JSONArray get(String request_url, JSONArray paramArray){
        JSONArray output = new JSONArray();

        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    request_url, paramArray,
                    (JSONArray response) -> {
                        Log.e(this.getClass().toString(), response.toString());
                        try {
                            Log.e(this.getClass().toString(), response.toString());
                            if(response.length() != 0) {
                                Log.e(this.getClass().toString(), "getProfile success");

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
        return output;
    }
}
