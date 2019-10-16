package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

public class IngrediPostActivity extends AppCompatActivity implements View.OnClickListener{
    private View mPost;
    private Context mContext;
    private Button offerButton;
    private Button requestButton;
    private Button backbutton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestofferswitch_layout);
        mContext = this;

        offerButton = findViewById(R.id.offer);
        requestButton = findViewById(R.id.request);
        backbutton = findViewById(R.id.back_button);

        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OfferIngredientActivity.class);
                startActivity(intent);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RequestIngredientActivity.class);
                startActivity(intent);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IngredientListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "hello!", Toast.LENGTH_SHORT).show();
    }
}
