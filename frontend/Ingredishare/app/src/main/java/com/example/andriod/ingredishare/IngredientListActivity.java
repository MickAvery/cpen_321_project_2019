package com.example.andriod.ingredishare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IngredientListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private Button postButton;
    private Context mContext;
    private GlobalRequestQueue mReqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);
        mContext = this;
        mReqQueue = GlobalRequestQueue.getInstance();
        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);

       // Toolbar myToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        List<Event> eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList);
        recycler.setAdapter(adapter);

        getEventsFromBackend();
        ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);

        postButton = findViewById(R.id.post_ingredient_button);

        postButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, IngrediPostActivity.class);
            Toast.makeText(mContext, "Loading New Post Page", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /*
      Grabs events from the backend and adds them to the adapter
     */
    public void getEventsFromBackend(){

        String url = getString(R.string.server_url) + getString(R.string.get_all_requests_lat_long);
        JSONArray paramArray = new JSONArray();
        JSONObject getParams = new JSONObject();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO: handle case if they say no
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

      //  url = getString(R.string.server_url) + getString(R.string.getAllRequests);

        try {
            getParams.put(getString(R.string.longitude), longitude);
            getParams.put(getString(R.string.latitude), latitude);
            getParams.put(getString(R.string.email), MyApplication.getUserEmail());

            paramArray.put(getParams);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    url,
                    paramArray,
                    (JSONArray json_events_array) -> {
                        try {
                            for (int i = 0; i < json_events_array.length(); i++)
                            {
                                JSONObject json_data = json_events_array.getJSONObject(i);

                                try{
                                    String name = json_data.getString("name");
                                    String description = json_data.getString("description");
                                    String userid = json_data.getString("userId");
                                    Float x = Float.parseFloat(json_data.getString("lat"));
                                    Float y = Float.parseFloat(json_data.getString("long"));

                                    Event event = new Event(userid, name, description, x, y);
                                    adapter.addEvent(event);
                                } catch(Exception e){}
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
    }
}
