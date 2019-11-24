package com.example.andriod.ingredishare.NewIngrediPost;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.main.MainActivity;
import com.example.andriod.ingredishare.profile.ProfileActivity;
import com.example.andriod.ingredishare.R;
import com.example.andriod.ingredishare.search.SearchBarActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class NewIngrediPostActivity extends AppCompatActivity implements NewIngrediPostView{
    private Context mContext;
    private GlobalRequestQueue mReqQueue;
    private String mType;
    private FirebaseUser mUser;
    private NewIngrediPostPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredipost_layout);
        mContext = this;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        View mBackbutton;
        Button mPostButton;

        mBackbutton = findViewById(R.id.back_button);
        mPostButton = findViewById(R.id.postbutton);

        presenter = new NewIngrediPostPresenter(MyApplication.getDataManager(), this);

        Switch mySwitch = (Switch) findViewById(R.id.simpleSwitch);
        mySwitch.setText(getString(R.string.request_ingredient));
        mType = getString(R.string.request_ingredient);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    mySwitch.setText(getString(R.string.request_ingredient));
                    mType = getString(R.string.request_ingredient);
                } else{
                    mySwitch.setText(getString(R.string.offer_ingredient));
                    mType = getString(R.string.offer_ingredient);
                }
            }
        });

        mBackbutton.setOnClickListener(v -> finish());
        mPostButton.setOnClickListener(v -> {
            EditText mDescription;
            EditText mName;

            mDescription = findViewById(R.id.description);
            mName = findViewById(R.id.name);
            if (presenter != null && MyApplication.getDataManager() != null) {
                presenter.savePost(mDescription.getText().toString(), mName.getText().toString(), mType);
            } else {
                finish();
            }
            testNotifications();
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_new_post);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent newIntent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        newIntent = new Intent(NewIngrediPostActivity.this,
                                IngredientListActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_profile:
                        newIntent = new Intent(NewIngrediPostActivity.this,
                                ProfileActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_new_post:
                        newIntent = new Intent(NewIngrediPostActivity.this,
                                NewIngrediPostActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.search:
                        newIntent = new Intent(NewIngrediPostActivity.this,
                                SearchBarActivity.class);
                        startActivity(newIntent);
                        break;

                }
                return true;
            }
        });
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

    public void startIngredientListActivity(){
        Intent intent = new Intent(this, IngredientListActivity.class);
        startActivity(intent);
    }

    public void toastCouldNotPost(){
        Toast.makeText(mContext, "Could not post!", Toast.LENGTH_SHORT).show();
    }

    public void displayInputAllFieldsToast() {
        Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
    }
}
