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

    private Context mContext;
    private GlobalRequestQueue mReqQueue;
    private Boolean success;

    public BackendCommunicationService() {
        mContext = MyApplication.getContext();
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

    /*
    Returns a HashMap with keys associated with the parameter id's and values associated with values
    received from the backend. Returns an empty HashMap if no values were received.
     */
    public HashMap<String,String> getProfileInfoFromBackend(){

        HashMap<String,String> output = new HashMap<String,String>();

        String url = MyApplication.getContext().getString(R.string.server_url) +
            MyApplication.getContext().getString(R.string.get_profile_info);
             //   + "?email=" + MyApplication.getUserEmail();

        JSONObject paramObject = new JSONObject();

        JSONArray paramArray = new JSONArray();

        try {
            paramObject.put(MyApplication.getContext().getString(R.string.email), MyApplication.getUserEmail());
            paramArray.put(paramObject);
            Log.e(this.getClass().toString(), "Parameter array : " + paramArray.toString());
            Log.e(this.getClass().toString(), "Email : " + MyApplication.getUserEmail());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    url, paramArray,
                    (JSONArray response) -> {
                        Log.e(this.getClass().toString(), response.toString());
                        try {
                            Log.e(this.getClass().toString(), response.toString());
                            if(response.length() != 0) {
                                Log.e(this.getClass().toString(), "getProfile success");
                                JSONObject json_data = response.getJSONObject(0);

                                output.put(MyApplication.getContext().getString(R.string.displayName),
                                        json_data.getString(mContext.getString(R.string.displayName)));
                                output.put(MyApplication.getContext().getString(R.string.bio),
                                        json_data.getString(mContext.getString(R.string.bio)));
                                output.put(MyApplication.getContext().getString(R.string.food_preferences),
                                        json_data.getString(mContext.getString(R.string.food_preferences)));
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

    /*
    Returns true if profile info successfully updated & posted to the backend, false otherwise
    @param String displayName
    @param String bio
    @param String preferences
    @param String email
    @returns boolean
     */
    public Boolean updateProfileInfo(String displayName, String bio, String preferences, String email){
        Boolean success = false;

        String url = mContext.getString(R.string.server_url) + mContext.getString(R.string.update_profile_info) ;

        JSONObject postparams = new JSONObject();

        try {
            postparams.put(mContext.getString(R.string.displayName), displayName);
            postparams.put(mContext.getString(R.string.bio), bio);
            postparams.put(mContext.getString(R.string.food_preferences), preferences);
            postparams.put(mContext.getString(R.string.email), email);

            Log.e(this.getClass().toString(), displayName);
            Log.e(this.getClass().toString(), bio);
            Log.e(this.getClass().toString(), preferences);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean("updateProfileInfo");
                            if (success_response) {
                                Log.e(this.getClass().toString(), "updateProfile success");
                                success = true;

                              //  Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException jsonEx) {
                            Log.e(this.getClass().toString(), jsonEx.toString());
                        }

                    },

                    (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError",  error)
            );

            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonObjReq, "post");
        } catch(JSONException jsonEx) {

        }

        return success;
    }


}
