package com.example.andriod.ingredishare.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        EditText query = findViewById(R.id.search_bar);
        query.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.e(this.getClass().toString(), "query " + s);
                eventList = new ArrayList<>();
                mEventAdapter = new EventAdapter(eventList);
                recycler.setAdapter(mEventAdapter);
                presenter.getEventsFromQuery(s.toString(), mEventAdapter);
            }
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

    public void updateUI(){
        Log.e(this.getClass().toString(), "updateUI");
        mEventAdapter.notifyDataSetChanged();
    }

}
