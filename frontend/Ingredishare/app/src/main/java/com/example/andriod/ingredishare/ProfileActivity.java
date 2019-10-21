package com.example.andriod.ingredishare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 2019-10-08.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mBioEditText;
    private EditText mPrefEditText;
    private View mSaveButton;
    private View mBackButton;
    private Context mContext;
    private GlobalRequestQueue mReqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mNameEditText = findViewById(R.id.name_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mPrefEditText = findViewById(R.id.pref_edit_text);
        mBackButton = findViewById(R.id.back_button);
        mSaveButton = findViewById(R.id.save_button);

        getProfileInfoFromBackend();

        mBackButton.setOnClickListener(v -> {
            Toast.makeText(this, "BACK!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
        mSaveButton.setOnClickListener(view -> {
            updateProfileInfo();
        });
    }

    public void getProfileInfoFromBackend(){
        String url = getString(R.string.server_url) + getString(R.string.get_profile_info);

        JSONObject paramObject = new JSONObject();

        JSONArray paramArray = new JSONArray();

        try {
            paramObject.put("email", MyApplication.getUserEmail());
            paramArray.put(paramObject);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    url,
                    paramArray,
                    (JSONArray response) -> {
                        try {
                            Log.e(this.getClass().toString(), "updateProfile success");
                            if(response.length() != 0) {
                                JSONObject json_data = response.getJSONObject(0);
                                mNameEditText.setText(json_data.getString(getString(R.string.full_name)));
                                mBioEditText.setText(json_data.getString(getString(R.string.bio)));
                                mPrefEditText.setText(json_data.getString(getString(R.string.food_preferences)));
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

    public void updateProfileInfo(){


        String display_name = mNameEditText.getText().toString();
        String bio = mBioEditText.getText().toString();
        String preferences = mPrefEditText.getText().toString();
        String email = MyApplication.getUserEmail();

        String url = getString(R.string.server_url) + getString(R.string.update_profile_info);

        JSONObject postparams = new JSONObject();

        try {
            postparams.put(getString(R.string.full_name), display_name);
            postparams.put(getString(R.string.bio), bio);
            postparams.put(getString(R.string.food_preferences), preferences);
            postparams.put("email", email);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean("update_success_response");

                            if (success_response) {
                                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
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
    }
}
