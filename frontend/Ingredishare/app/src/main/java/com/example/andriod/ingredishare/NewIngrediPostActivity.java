package com.example.andriod.ingredishare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class NewIngrediPostActivity extends AppCompatActivity {
    private Context mContext;
    private Button mBackbutton;
    private Button mPostButton;
    private EditText mDescription;
    private EditText mName;
    private Toolbar mToolbar;
    private GlobalRequestQueue mReqQueue;
    private String mType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredipost_layout);
        mContext = this;

        mToolbar = findViewById(R.id.toolbar);
        mBackbutton = findViewById(R.id.back_button);
        mPostButton = findViewById(R.id.postbutton);

        mBackbutton.setOnClickListener(v -> finish());
        mPostButton.setOnClickListener(v -> {
            savePost();
            testNotifications();
            finish();
        });

        mToolbar.setTitle(getIntent().getStringExtra(getString(R.string.request_or_offer)));
        mType = getIntent().getStringExtra(getString(R.string.request_or_offer));
    }

    public void savePost() {
        mDescription = findViewById(R.id.description);
        mName = findViewById(R.id.name);

        // TODO(developer): send ID Token to server and validate
        String url = getString(R.string.server_url) + getString(R.string.createRequest);

        JSONObject postparams = new JSONObject();

        try {
            postparams.put(getString(R.string.name), mName.getText());
            postparams.put(getString(R.string.description), mDescription.getText());
            postparams.put(getString(R.string.userId), MyApplication.getUserEmail());
            postparams.put(getString(R.string.type), mType);

            Double[] loc = getLocation();
            if(loc.length > 0) {
                postparams.put("lat", loc[0]);
                postparams.put("long", loc[1]);
            } else{
                postparams.put("lat", null);
                postparams.put("long", null);
            }
            Log.d("latitude", loc[0].toString());
            Log.d("longitude", loc[1].toString());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean("createRequestResponse");

                            if (success_response) {
                                Log.d("resp", "Sent to backend successfully");
                                Intent intent = new Intent(this, IngredientListActivity.class);
                                startActivity(intent);
                            } else {
                                /* TODO: new user activity. Get rid of "go to" log after done */
                                Toast.makeText(mContext, "Could not post!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, IngredientListActivity.class);
                                startActivity(intent);
                            }

                        } catch (JSONException jsonEx) {
                            Log.e(this.getClass().toString(), jsonEx.toString());
                        }

                    },

                    (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError", error)
            );

            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonObjReq, "post");

        } catch (JSONException jsonEx) {

        }

        url = getString(R.string.server_url) + "/notif_test";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                (JSONObject response) -> {

                },

                (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError", error)
        );

        mReqQueue = GlobalRequestQueue.getInstance();
        mReqQueue.addToRequestQueue(jsonObjReq, "post");
    }

    public Double[] getLocation() {
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

        return new Double[]{latitude, longitude};
    }


    public void testNotifications() {

        String url = getString(R.string.server_url) + "/notif_test";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, null,
                (JSONObject response) -> {

                },

                (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError", error)
        );

        mReqQueue = GlobalRequestQueue.getInstance();
        mReqQueue.addToRequestQueue(jsonObjReq, "post");
    }
}
