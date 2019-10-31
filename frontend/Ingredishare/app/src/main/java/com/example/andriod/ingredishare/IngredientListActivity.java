package com.example.andriod.ingredishare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            showClickableToast();
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

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO: handle case if they say no
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude;
        double latitude;
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } else {
            longitude = 1;
            latitude = 1;
        }
        String url = getString(R.string.server_url)
                + getString(R.string.get_all_requests_lat_long)
                + "?lat=1"
                + "&long=1";

        try {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    url,
                    null,
                    (JSONArray json_events_array) -> {
                        try {
                            Log.e(this.getClass().toString(), "inside loop");

                            for (int i = 0; i < json_events_array.length(); i++) {
                                JSONObject json_data = json_events_array.getJSONObject(i);

                                String name = json_data.getString(getString(R.string.name));
                                String description = json_data.getString(getString(R.string.description));
                                String userid = "none";
                                String type = "Offer";
                                if(json_data.has(getString(R.string.userId))) {
                                    userid = json_data.getString(getString(R.string.userId));
                                }
                                if(json_data.has(getString(R.string.type))){
                                    type = json_data.getString(getString(R.string.userId));
                                }
                               // Float x = Float.parseFloat(json_data.getString("lat"));
                                //Float y = Float.parseFloat(json_data.getString("long"));
                                Double x = 1.0;
                                Double y = 1.0;

                                Event event = new Event(userid, name, description, x, y, type);
                                adapter.addEvent(event);

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

    private void showClickableToast() {

        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.offer_request_quick, this.findViewById(R.id.layout_root));
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(layout);
        AlertDialog alertDialog = dialogBuilder.create();
        WindowManager.LayoutParams wmlp = Objects.requireNonNull(alertDialog.getWindow()).getAttributes();
        wmlp.gravity = Gravity.BOTTOM;

        layout.findViewById(R.id.offer).setOnClickListener( view -> {
            Intent intent = new Intent(mContext, NewIngrediPostActivity.class);
            intent.putExtra(getString(R.string.request_or_offer), getString(R.string.offer_ingredient));
            startActivity(intent);
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });
        layout.findViewById(R.id.request).setOnClickListener( view -> {
            Intent intent = new Intent(mContext, NewIngrediPostActivity.class);
            intent.putExtra(getString(R.string.request_or_offer), getString(R.string.request_ingredient));
            startActivity(intent);
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
