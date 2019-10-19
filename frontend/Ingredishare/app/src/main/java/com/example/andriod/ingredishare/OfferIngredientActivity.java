package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

public class OfferIngredientActivity extends AppCompatActivity implements View.OnClickListener{
    private View mPost;
    private Context mContext;
    private Button backbutton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredipost_layout);
        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Offer Ingredient");

        backbutton = findViewById(R.id.back_button);

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
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "hello!", Toast.LENGTH_SHORT).show();
    }
}
