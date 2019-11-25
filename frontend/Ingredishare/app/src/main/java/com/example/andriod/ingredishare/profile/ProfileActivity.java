package com.example.andriod.ingredishare.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.example.andriod.ingredishare.R;
import com.example.andriod.ingredishare.main.MainActivity;
import com.example.andriod.ingredishare.search.SearchBarActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

/**
 * Created by Brandon on 2019-10-08.
 */

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    private EditText mNameEditText;
    private EditText mBioEditText;
    private EditText mPrefEditText;
    private Spinner mRadiusPref;
    private FirebaseUser mUser;
    private GlobalRequestQueue mReqQueue;
    private ProfilePresenter presenter;
    private Boolean mProfileUpdatedFlag;

    private DataManager mDataManager;
    private View mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        View mSaveButton;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        presenter = new ProfilePresenter(MyApplication.getDataManager(), this);

        mNameEditText = findViewById(R.id.name_edit_text);
        mBioEditText = findViewById(R.id.bio_edit_text);
        mPrefEditText = findViewById(R.id.pref_edit_text);
        mRadiusPref = findViewById(R.id.radius_pref);
        mBackButton = findViewById(R.id.back_button);
        mSaveButton = findViewById(R.id.save_button);
        mDataManager = MyApplication.getDataManager();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.km_radius_pref));
        mRadiusPref.setAdapter(adapter);

        Intent myIntent = getIntent();

        // Checks if user is a new user
        boolean newUser = myIntent.getBooleanExtra(getString(R.string.newUser), false);
        presenter.setNewUser(newUser);

        presenter.getProfileInfo();

        mBackButton.setOnClickListener(v -> {
            presenter.backButtonPressed();
        });
        mSaveButton.setOnClickListener(view -> {
            HashMap<String, String> profileData = new HashMap<String, String>();
            profileData.put(getString(R.string.display_name), mNameEditText.getText().toString());
            profileData.put(getString(R.string.bio), mBioEditText.getText().toString());
            profileData.put(getString(R.string.food_preferences), mPrefEditText.getText().toString());
            profileData.put(getString(R.string.radius_preference),(((Integer) (mRadiusPref.getSelectedItemPosition() + 1)).toString()));

            presenter.updateProfileInfo(profileData);
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent newIntent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        newIntent = new Intent(ProfileActivity.this,
                                IngredientListActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_profile:
                        newIntent = new Intent(ProfileActivity.this,
                                ProfileActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_new_post:
                        newIntent = new Intent(ProfileActivity.this,
                                NewIngrediPostActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.search:
                        newIntent = new Intent(ProfileActivity.this,
                                SearchBarActivity.class);
                        startActivity(newIntent);
                        break;

                }
                return true;
            }
        });
    }


    @Override
    public void updateUI(HashMap<String, String> data) {
        // If values are null just leave them empty
        if (data.containsKey(getString(R.string.display_name))) {
            mNameEditText.setText(data.get(getString(R.string.display_name)));
        }
        if (data.containsKey(getString(R.string.bio))) {
            mBioEditText.setText(data.get(getString(R.string.bio)));
        }
        if (data.containsKey(getString(R.string.bio))) {
            mPrefEditText.setText(data.get(getString(R.string.food_preferences)));
        }
        if(data.containsKey(getString(R.string.radius_preference))){
            mRadiusPref.setSelection(
                    Integer.parseInt(data.get(getString(R.string.radius_preference))) -1);
        }

    }

    public void displaySavedToast() {
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void displayInputAllFieldsToast() {
        Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
    }

    public void hideBackButton() {
        mBackButton.setVisibility(View.INVISIBLE);
    }

    public void displayBackButton() {
        mBackButton.setVisibility(View.VISIBLE);
    }

    public void displayCouldNotFindProfileInfoToast() {
        Toast.makeText(this,"Could not find profile data! Please update profile info.",
                Toast.LENGTH_SHORT).show();
    }

    public void setIngrediListActivityIntent(){
        Intent intent = new Intent(this, IngredientListActivity.class);
        startActivity(intent);
        finish();
    }

    public void toastError(){
        Toast.makeText(this,"Internal Error",
                Toast.LENGTH_SHORT).show();
    }

}
