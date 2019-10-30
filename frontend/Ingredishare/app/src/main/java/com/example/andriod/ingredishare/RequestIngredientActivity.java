package com.example.andriod.ingredishare;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class RequestIngredientActivity extends AppCompatActivity implements View.OnClickListener{
    private View mPost;
    private Context mContext;
    private Button backbutton;
    private EditText description;
    private Button postButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredipost_layout);
        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Request Ingredient");
        backbutton = findViewById(R.id.back_button);
        postButton = findViewById(R.id.postbutton);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    }
}
