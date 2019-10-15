package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        mNameEditText = findViewById(R.id.name_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mPrefEditText = findViewById(R.id.pref_edit_text);
        mBackButton = findViewById(R.id.back_button);
        mSaveButton = findViewById(R.id.save_button);

        // TO DO: Grab data from the backend
        mNameEditText.setText("Brandon Holmes");
        mBioEditText.setText("UBC student");
        mPrefEditText.setText("I like baking cookies!");

        mBackButton.setOnClickListener(v -> {
            Toast.makeText(this, "BACK!", Toast.LENGTH_SHORT).show();
            finish();
        });
        mSaveButton.setOnClickListener(view -> {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            mContext = this;
            Intent intent = new Intent(mContext, IngredientListActivity.class);
            startActivity(intent);
        });
    }
}
