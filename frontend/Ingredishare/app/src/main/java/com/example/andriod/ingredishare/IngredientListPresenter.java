package com.example.andriod.ingredishare;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IngredientListPresenter {


    private IngredientListView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;
    private EventAdapter eventAdapter;

    public IngredientListPresenter(DataManager dataManager, IngredientListView view,
                                   EventAdapter eventAdapter) {
        this.dataManager = dataManager;
        this.view = view;
        this.eventAdapter = eventAdapter;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /*
      Grabs events from the backend and adds them to the mEventAdapter
     */
    public void getEvents(){

        List<Double> location = view.getLocation();
        Double latitude = location.get(0);
        Double longitude = location.get(1);

        String url = MyApplication.getGetAllRequestsLatLongGETRequestURL()
                + "?lat=" + latitude.toString()
                + "&long=" + longitude.toString()
                + "&email=" + mUser.getEmail();

        JSONObject paramObj = new JSONObject();
        JSONArray paramArray = new JSONArray();

        try {

            paramObj.put(mContext.getString(R.string.lat), String.format("%f", latitude));
            paramObj.put("long", String.format("%f", longitude));
            paramObj.put(mContext.getString(R.string.email), mUser.getEmail());
            paramArray.put(paramObj);

            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray json_events_array) {
                    try {
                        Log.e(this.getClass().toString(), "inside loop");

                        for (int i = 0; i < json_events_array.length(); i++) {
                            JSONObject json_data = json_events_array.getJSONObject(i);

                            String name = json_data.getString(mContext.getString(R.string.name));
                            String description = json_data.getString(mContext.getString(R.string.description));
                            String userid = "none";
                            String type = "Post";
                            if(json_data.has(mContext.getString(R.string.userId))) {
                                userid = json_data.getString(mContext.getString(R.string.userId));
                            }
                            if(json_data.has(mContext.getString(R.string.type))){
                                type = json_data.getString(mContext.getString(R.string.type));
                            }
                            Double x = Double.parseDouble(json_data.getString("lat"));
                            Double y = Double.parseDouble(json_data.getString("long"));
                          //  Double x = 1.0;
                          //  Double y = 1.0;

                            Event event = new Event(userid, name, description, x, y, type);
                            eventAdapter.addEvent(event);

                        }

                    } catch (JSONException jsonEx) {
                        Log.e(this.getClass().toString(), jsonEx.toString());
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("POST Request Error", error.toString());
                }
            };

            MyApplication.getDataManager().getJSONArray(url, paramArray, listener, errorListener);
        } catch(Exception e) {

        }
    }
}
