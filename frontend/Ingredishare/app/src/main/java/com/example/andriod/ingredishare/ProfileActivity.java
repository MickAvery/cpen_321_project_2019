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

import java.util.HashMap;

/**
 * Created by Brandon on 2019-10-08.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mBioEditText;
    private EditText mPrefEditText;
    private View mSaveButton;
    private View mBackButton;
    private BackendCommunicationService mBackendCommunicationService;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mNameEditText = findViewById(R.id.name_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mPrefEditText = findViewById(R.id.pref_edit_text);
        mBackButton = findViewById(R.id.back_button);
        mSaveButton = findViewById(R.id.save_button);
        mBackendCommunicationService = new BackendCommunicationService();

        // Brute force fix to bug
        if(MyApplication.getUserEmail() == null) {
            MyApplication.setUserEmail("taravirginillo@gmail.com");
        }

        // Display profile info received from backend
        getProfileInfo();

        mBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
        mSaveButton.setOnClickListener(view -> {


            if(updateProfileInfo()) { Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show(); }
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /*
    Posts profile info to the backend
    @returns true if post is successful
     */
    public boolean updateProfileInfo(){

        Boolean response = false;

        String url = MyApplication.getContext().getString(R.string.server_url) +
                MyApplication.getContext().getString(R.string.get_profile_info);

        JSONObject paramObject = new JSONObject();

        JSONArray paramArray = new JSONArray();

        try {
            paramObject.put(MyApplication.getContext().getString(R.string.email), MyApplication.getUserEmail());
            paramArray.put(paramObject);
            Log.e(this.getClass().toString(), "Parameter array : " + paramArray.toString());
            Log.e(this.getClass().toString(), "Email : " + MyApplication.getUserEmail());

            response = mBackendCommunicationService.post(url, paramObject,
                    getString(R.string.success_id_updateProfile));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*
   Grabs data from backend and displays on view
    */
    public void getProfileInfo(){

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

            JSONArray response = mBackendCommunicationService.get(url, paramArray);

            // Display profile info received from backend
            if(response.length() != 0) {
                JSONObject json_data = response.getJSONObject(0);

                mNameEditText.setText(json_data.getString(getString(R.string.displayName)));
                mBioEditText.setText(json_data.getString(getString(R.string.bio)));
                mPrefEditText.setText(json_data.getString(getString(R.string.food_preferences)));
            }

        } catch(Exception e) {

        }
    }


}
