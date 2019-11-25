package com.example.andriod.ingredishare.IngredientList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.andriod.ingredishare.event.Event;
import com.example.andriod.ingredishare.event.EventAdapter;
import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.main.MainActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.example.andriod.ingredishare.search.SearchBarActivity;
import com.example.andriod.ingredishare.profile.ProfileActivity;
import com.example.andriod.ingredishare.R;
import com.example.andriod.ingredishare.search.SearchBarActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IngredientListActivity extends AppCompatActivity implements IngredientListView  {

    private EventAdapter mEventAdapter;
    private Context mContext;
    private GlobalRequestQueue mReqQueue;
    private FirebaseUser mUser;
    private IngredientListPresenter mPresenter;
    private RecyclerView mRecycler;
    private ImageView mNotifImage;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView.LayoutManager mLayoutManager;
        Button mPostButton;

//        RecyclerView.LayoutManager lManager;
//        Button postButton;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.newsfeed);
        mContext = this;
        mReqQueue = GlobalRequestQueue.getInstance();
        // Get the RecyclerView
        mRecycler = findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        // Set the custom mEventAdapter
        List<Event> eventList = new ArrayList<>();
        mEventAdapter = new EventAdapter(eventList);
        mRecycler.setAdapter(mEventAdapter);

        mNotifImage = findViewById(R.id.notif_dot);
        MyApplication.setNotificationImageView(mNotifImage);

        mPresenter = new IngredientListPresenter(MyApplication.getDataManager(), this,
                mEventAdapter, mRecycler, mNotifImage);
        mPresenter.getEvents();
        MyApplication.setEventAdapter(mEventAdapter);


        //getEventsFromBackend();
        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(0, 0);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("  ");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent newIntent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        newIntent = new Intent(IngredientListActivity.this,
                                IngredientListActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_profile:
                        newIntent = new Intent(IngredientListActivity.this,
                                ProfileActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_new_post:
                        newIntent = new Intent(IngredientListActivity.this,
                                NewIngrediPostActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.search:
                        newIntent = new Intent(IngredientListActivity.this,
                                SearchBarActivity.class);
                        startActivity(newIntent);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });

        mNotifImage = findViewById(R.id.notif_dot);
        MyApplication.setNotificationImageView(mNotifImage);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();

                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onStart() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        super.onStart();
    }

    public List<Double> getLocation(){

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

        List<Double> loc = new ArrayList<Double>();
        loc.add(latitude);
        loc.add(longitude);

        return loc;
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

    public void updateUI(){
        Log.e(this.getClass().toString(), "updateUI");
        mEventAdapter.clearAllRequests();
        mPresenter.getEvents();
    }
}
