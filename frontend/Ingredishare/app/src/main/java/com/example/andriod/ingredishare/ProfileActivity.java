package com.example.andriod.ingredishare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brandon on 2019-10-08.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mBioEditText;
    private EditText mPrefEditText;
    private Spinner mRadiusPref;
    private FirebaseUser mUser;
    private GlobalRequestQueue mReqQueue;

    private Boolean mProfileUpdatedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        View mSaveButton;
        View mBackButton;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mNameEditText = findViewById(R.id.name_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mPrefEditText = findViewById(R.id.pref_edit_text);
        mRadiusPref = findViewById(R.id.radius_pref);
        mBackButton = findViewById(R.id.back_button);
        mSaveButton = findViewById(R.id.save_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.km_radius_pref));
        mRadiusPref.setAdapter(adapter);

        Intent myIntent = getIntent();

        // Checks if user is a new user
        boolean newUser =  myIntent.getBooleanExtra(getString(R.string.newUser), false);

        // True if profile has been updated at least once in this intent
        mProfileUpdatedFlag = false;


        getProfileInfoFromBackend();

        mBackButton.setOnClickListener(v -> {
            if(newUser && !mProfileUpdatedFlag){
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, IngredientListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mSaveButton.setOnClickListener(view -> {
            updateProfileInfo();
        });
    }

    public void getProfileInfoFromBackend(){
        String url = getString(R.string.server_url) + getString(R.string.get_profile_info)
                + "?email=" + mUser.getEmail();

        try {


            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest (Request.Method.GET,
                    url, null,
                    (JSONObject response) -> {
                        try {
                            Log.e(this.getClass().toString(), response.toString());

                            String name = response.getString(getString(R.string.display_name));
                            String bio = response.getString(getString(R.string.bio));
                            String preference = response.getString(getString(R.string.food_preferences));

                            // If values are null just leave them empty
                            if (name != null) {
                                mNameEditText.setText(name);
                            }
                            if (bio != null) {
                                mBioEditText.setText(bio);
                            }
                            if (preference != null) {
                                mPrefEditText.setText(preference);
                            }


                        } catch (JSONException jsonEx) {
                            Log.e(this.getClass().toString(), jsonEx.toString());
                        }

                    },

                    (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError",  error)
            );
            // Add JsonArrayRequest to the RequestQueue
            mReqQueue = GlobalRequestQueue.getInstance();
            mReqQueue.addToRequestQueue(jsonObjectRequest,"get");

        } catch(Exception e) {

        }
    }

    public void updateProfileInfo(){


        String display_name = mNameEditText.getText().toString();
        String bio = mBioEditText.getText().toString();
        String preferences = mPrefEditText.getText().toString();
        String email = mUser.getEmail();

        int rad = mRadiusPref.getSelectedItemPosition() + 1;

        Log.e(this.getClass().toString(), display_name);
        Log.e(this.getClass().toString(), bio);


        try {
            if("".equals(display_name) ||
                    "".equals(bio) ||
                    "".equals(preferences)){
                throw new StringIndexOutOfBoundsException();
            }

            String url = getString(R.string.server_url) + getString(R.string.update_profile_info) ;

            JSONObject postparams = new JSONObject();
            postparams.put("displayName", display_name);
            postparams.put(getString(R.string.bio), bio);
            postparams.put(getString(R.string.food_preferences), preferences);
            postparams.put("email", email);
            postparams.put(getString(R.string.radius_preference), rad);

            Log.d(this.getClass().toString(), display_name);
            Log.d(this.getClass().toString(), bio);
            Log.d(this.getClass().toString(), preferences);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        try {
                            Boolean success_response = response.getBoolean("updateProfileInfo");
                            if (success_response) {
                                Log.e(this.getClass().toString(), "updateProfile success");
                                mProfileUpdatedFlag = true;


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

        }  catch(StringIndexOutOfBoundsException e){
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
        }
    }
}
