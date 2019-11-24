package com.example.andriod.ingredishare.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

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
import com.example.andriod.ingredishare.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchBarActivity extends AppCompatActivity implements SearchBarView  {

    EditText searchQuery;
    private FirebaseUser mUser;
    EventAdapter mEventAdapter;
    SearchBarPresenter presenter;
    private List<Event> eventList;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_bar_layout);

        RecyclerView.LayoutManager mLayoutManager;
        recycler = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);

        searchQuery = findViewById(R.id.search_bar);

        mEventAdapter = MyApplication.getEventAdapter();

        presenter = new SearchBarPresenter(this,
                mEventAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(0, 0);

        eventList = new ArrayList<>();

        Button searchButton = findViewById(R.id.search_bar_button);
        searchButton.setOnClickListener(v -> {
            EditText text = findViewById(R.id.search_bar);
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
                    case R.id.search:
                        newIntent = new Intent(SearchBarActivity.class,
                                SearchBarActivity.class);
                        startActivity(newIntent);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
    }

    public void setActivityEventAdapter(EventAdapter e){
        recycler.setAdapter(e);
    }

}
