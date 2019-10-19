package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class IngredientListActivity  extends AppCompatActivity {

    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private Button postButton;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);
        mContext = this;
        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);

       // Toolbar myToolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(myToolbar);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        List<Event> eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList);
        recycler.setAdapter(adapter);

        for(int i = 0; i < 10; i++){
            Event event = new Event("Sarah", ("egg" + i), "info", "photo");
            adapter.addEvent(event);
        }
        ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);

        postButton = findViewById(R.id.post_ingredient_button);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IngrediPostActivity.class);
                Toast.makeText(mContext, "Loading New Post Page", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
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
                return true;

            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
