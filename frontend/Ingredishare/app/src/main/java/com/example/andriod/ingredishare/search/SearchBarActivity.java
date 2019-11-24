package com.example.andriod.ingredishare.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.IngredientList.IngredientListPresenter;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.example.andriod.ingredishare.R;
import com.example.andriod.ingredishare.event.Event;
import com.example.andriod.ingredishare.event.EventAdapter;
import com.example.andriod.ingredishare.main.MainActivity;
import com.example.andriod.ingredishare.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchBarActivity extends AppCompatActivity implements SearchBarView  {

    private FirebaseUser mUser;
    EventAdapter mEventAdapter;
    SearchBarPresenter presenter;
    private List<Event> eventList;
    RecyclerView recycler;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(this.getClass().toString(), "onCreate inside");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bar_layout);

        Log.e(this.getClass().toString(), "content view set");

        recycler = findViewById(R.id.recycler_view2);
        mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);


        eventList = new ArrayList<>();
        mEventAdapter = new EventAdapter(eventList);
        recycler.setAdapter(mEventAdapter);

        presenter = new SearchBarPresenter(this,
                mEventAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.e(this.getClass().toString(), "midway");

        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(0, 0);

        Button searchButton = findViewById(R.id.search_bar_button);
        searchButton.setOnClickListener(v -> {
            EditText text = findViewById(R.id.search_bar);
            Log.e(this.getClass().toString(), "query " + text.getText().toString());
            presenter.getEventsFromQuery(text.getText().toString());
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent newIntent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        newIntent = new Intent(SearchBarActivity.this,
                                IngredientListActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_profile:
                        newIntent = new Intent(SearchBarActivity.this,
                                ProfileActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_new_post:
                        newIntent = new Intent(SearchBarActivity.this,
                                NewIngrediPostActivity.class);
                        startActivity(newIntent);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });

        Log.e(this.getClass().toString(), "onCreate out");
    }

    @Override
    protected void onStart() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.search);
        super.onStart();
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
            case R.id.action_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();

                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                break;

            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void updateUI(){
        Log.e(this.getClass().toString(), "updateUI");
        mEventAdapter.notifyDataSetChanged();
    }

}
