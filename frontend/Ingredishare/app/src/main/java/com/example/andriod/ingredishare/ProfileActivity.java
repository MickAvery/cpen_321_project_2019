package com.example.andriod.ingredishare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
        mSaveButton.setOnClickListener(view -> {
            updateProfileInfo();
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void getProfileInfoFromBackend(){
        String url = getString(R.string.server_url) + getString(R.string.get_profile_info)
                + "?email=" + MyApplication.getUserEmail();

        JSONObject paramObject = new JSONObject();

        JSONArray paramArray = new JSONArray();

        try {
            paramObject.put("email", MyApplication.getUserEmail());
            paramArray.put(paramObject);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest (Request.Method.GET,
                    url, null,
                    (JSONArray response) -> {
                        Log.e(this.getClass().toString(), response.toString());
                        try {
                            Log.e(this.getClass().toString(), response.toString());
                            if(response.length() != 0) {
                                Log.e(this.getClass().toString(), "getProfile success");
                                JSONObject json_data = response.getJSONObject(0);
                                mNameEditText.setText(json_data.getString("displayName"));
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

        String url = getString(R.string.server_url) + getString(R.string.update_profile_info) ;

        JSONObject postparams = new JSONObject();

        try {
            postparams.put("displayName", display_name);
            postparams.put(getString(R.string.bio), bio);
            postparams.put(getString(R.string.food_preferences), preferences);
            postparams.put("email", email);

            Log.e(this.getClass().toString(), display_name);
            Log.e(this.getClass().toString(), bio);
            Log.e(this.getClass().toString(), preferences);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean("updateProfileInfo");
                            if (success_response) {
                                Log.e(this.getClass().toString(), "updateProfile success");


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
