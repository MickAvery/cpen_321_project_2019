package com.example.andriod.ingredishare.IngredientList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.event.Event;
import com.example.andriod.ingredishare.event.EventAdapter;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IngredientListPresenter {


    private IngredientListView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;
    private EventAdapter eventAdapter;
    private RecyclerView mRecycler;
    private ImageView mNotificationImage;
    private static final Integer dateMultiplier = 24 * 60 * 60 * 1000;

    public IngredientListPresenter(DataManager dataManager, IngredientListView view,
                                   EventAdapter eventAdapter, RecyclerView recycler, ImageView notificationImage) {
        this.dataManager = dataManager;
        this.view = view;
        this.eventAdapter = eventAdapter;
        this.mContext = MyApplication.getContext();
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mRecycler = recycler;
        this.mNotificationImage = notificationImage;

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        getEvents();
                    }
                }
            }
        });
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
                        Log.e("OUT", json_events_array.toString());
                        for (int i = 0; i < json_events_array.length(); i++) {
                            JSONObject json_data = json_events_array.getJSONObject(i);

                            String name = json_data.getString(mContext.getString(R.string.name));
                            String description = json_data.getString(mContext.getString(R.string.description));
                            String userid = "none";
                            String type = "Post";

                            // Hacky ~ any post that doesn't have a date will be given today - 4
                            Long date = System.currentTimeMillis() - 4*dateMultiplier;
                            if(json_data.has(mContext.getString(R.string.userId))) {
                                userid = json_data.getString(mContext.getString(R.string.userId));
                            }
                            if(json_data.has(mContext.getString(R.string.type))){
                                type = json_data.getString(mContext.getString(R.string.type));
                            }

                            if(json_data.has(mContext.getString(R.string.date))){
                                date = Long.parseLong(
                                        json_data.getString(mContext.getString(R.string.date)));
                            }
                            Double x = Double.parseDouble(json_data.getString("lat"));
                            Double y = Double.parseDouble(json_data.getString("long"));

                            Date today = new Date(System.currentTimeMillis());
                            Date postDate = new Date(date);

                         /*   int diffInDays = (int)( (today.getTime() - postDate.getTime())
                                    / dateMultiplier );
                            if(diffInDays < MyApplication.getExpirationDate()) {

                            }*/
                            Event event = new Event(userid, name, description, x, y, type, date);
                            eventAdapter.addEvent(event);
                            /* scroll to top */
                            mRecycler.scrollToPosition(0);

                            /* set notification dot invisible */
                            mNotificationImage.setVisibility(View.INVISIBLE);
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
