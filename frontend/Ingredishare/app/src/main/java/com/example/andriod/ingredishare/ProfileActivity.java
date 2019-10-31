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
    private GlobalRequestQueue mReqQueue;
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

        // Get profile info from the backend
        MyApplication.setUserEmail("taravirginillo@gmail.com");
        HashMap<String,String> profileInfo = mBackendCommunicationService.getProfileInfoFromBackend();

        // Display profile info received from backend
        if(!profileInfo.isEmpty()){
            mNameEditText.setText(profileInfo.get(getString(R.string.displayName)));
            mBioEditText.setText(profileInfo.get(getString(R.string.bio)));
            mPrefEditText.setText(profileInfo.get(getString(R.string.food_preferences)));
        }

        mBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
        mSaveButton.setOnClickListener(view -> {

            // Post new profile info to the backend
            Boolean response = mBackendCommunicationService.updateProfileInfo(mNameEditText.getText().toString(),
                    mBioEditText.getText().toString(), mPrefEditText.getText().toString(),
                    MyApplication.getUserEmail());

            if(response) { Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show(); }
            Intent intent = new Intent(this, IngredientListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
