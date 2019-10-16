package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class IngrediPostActivity extends AppCompatActivity implements View.OnClickListener{
    private View mPost;
    private Context mContext;
    private Button postButton;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestofferswitch_layout);

        postButton = findViewById(R.id.offer);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.ingredipost_layout);
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
