package com.example.andriod.ingredishare.Email;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostPresenter;
import com.example.andriod.ingredishare.Profile.ProfileActivity;
import com.example.andriod.ingredishare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailActivity extends AppCompatActivity implements EmailView {

    private Context mContext;
    private GlobalRequestQueue mReqQueue;
    private String mType;
    private FirebaseUser mUser;
    private EmailPresenter presenter;
    private String emailToSendTo;
    private String subjectLine;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_layout);
        mContext = this;

        emailToSendTo = getIntent().getStringExtra(getString(R.string.email));
        TextView mEmailTextView = findViewById(R.id.send_email_to);
        mEmailTextView.setText(emailToSendTo);

        subjectLine = getIntent().getStringExtra(getString(R.string.email_subject));
        EditText subject = findViewById(R.id.email_subject);
        subject.setText(subjectLine);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        Button mBackbutton;
        Button mSendButton;
        Toolbar mToolbar;

        mToolbar = findViewById(R.id.toolbar);
        mBackbutton = findViewById(R.id.back_button);
        mSendButton = findViewById(R.id.postbutton);

        presenter = new EmailPresenter(this);

        mToolbar.setTitle(getIntent().getStringExtra(getString(R.string.request_or_offer)));
        mType = getIntent().getStringExtra(getString(R.string.request_or_offer));

        mBackbutton.setOnClickListener(v -> finish());
        mSendButton.setOnClickListener(v -> {
            EditText mSubject;
            EditText mMessage;

            mSubject = findViewById(R.id.email_subject);
            mMessage = findViewById(R.id.email_message);

            presenter.sendEmail(emailToSendTo, mSubject.getText().toString(),
                    mMessage.getText().toString());
            finish();
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent newIntent;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        newIntent = new Intent(EmailActivity.this,
                                IngredientListActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_profile:
                        newIntent = new Intent(EmailActivity.this,
                                ProfileActivity.class);
                        startActivity(newIntent);
                        break;
                    case R.id.action_new_post:
                        newIntent = new Intent(EmailActivity.this,
                                NewIngrediPostActivity.class);
                        startActivity(newIntent);
                        break;

                }
                return true;
            }
        });
    }

    public void toastCouldNotSend(){
            Toast.makeText(this, "Could not send email!", Toast.LENGTH_SHORT).show();
    }

    public void toastSuccess(){
        finish();
    }
}
