package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class OfferIngredientActivity extends AppCompatActivity implements View.OnClickListener{
    private View mPost;
    private Context mContext;
    private Button backbutton;
    private Button postButton;
    private EditText description;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredipost_layout);
        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Offer Ingredient");

        backbutton = findViewById(R.id.back_button);
        postButton = findViewById(R.id.postbutton);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IngrediPostActivity.class);
                startActivity(intent);
                // Intent intent = new Intent(mContext, IngredientListActivity.class);
                //    Toast.makeText(mContext, "posted!", Toast.LENGTH_SHORT).show();
                //  startActivity(intent);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
                // Intent intent = new Intent(mContext, IngredientListActivity.class);
                //    Toast.makeText(mContext, "posted!", Toast.LENGTH_SHORT).show();
                //  startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "hello!", Toast.LENGTH_SHORT).show();
    }

    public void savePost(){
        description = findViewById(R.id.description);
        Event newPostEvent = new Event("Sarah", "123", description.toString(), "123");

        // TODO(developer): send ID Token to server and validate
        //     String url = "http://10.0.2.2:1337/tokensignin/";
        String url = getString(R.string.server_url) + getString(R.string.tok_signin_put);

        JSONObject postparams = new JSONObject();

        try {
            postparams.put(getString(R.string.full_name), newPostEvent.getName());

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                    (JSONObject response) -> {
                        mContext = this;
                        Intent intent = new Intent(mContext, IngredientListActivity.class);
                        startActivity(intent);
                    },
                    (VolleyError error) -> {
                        Log.println(Log.DEBUG, "resp", "error");
                    }
            );

        //    addToRequestQueue(jsonObjReq, "post");
        } catch(JSONException jsonEx) {

        }
    }
}
