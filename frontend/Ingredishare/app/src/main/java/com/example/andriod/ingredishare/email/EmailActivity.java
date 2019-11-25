package com.example.andriod.ingredishare.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
import com.example.andriod.ingredishare.NewIngrediPost.NewIngrediPostActivity;
import com.example.andriod.ingredishare.main.MainActivity;
import com.example.andriod.ingredishare.profile.ProfileActivity;
import com.example.andriod.ingredishare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailActivity extends AppCompatActivity implements EmailView {

    private EmailPresenter presenter;
    private String emailToSendTo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_layout);

        emailToSendTo = getIntent().getStringExtra(getString(R.string.email));
        TextView mEmailTextView = findViewById(R.id.send_email_to);
        mEmailTextView.setText(emailToSendTo);

        String subjectLine = getIntent().getStringExtra(getString(R.string.email_subject));
        EditText subject = findViewById(R.id.email_subject);
        subject.setText(subjectLine);

       // FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        Button mBackbutton;
        Button mSendButton;
        Toolbar mToolbar;

        mToolbar = findViewById(R.id.toolbar);
        mBackbutton = findViewById(R.id.back_button);
        mSendButton = findViewById(R.id.postbutton);

        presenter = new EmailPresenter(this);

        mToolbar.setTitle(getIntent().getStringExtra(getString(R.string.request_or_offer)));
      //  String mType = getIntent().getStringExtra(getString(R.string.request_or_offer));

        mBackbutton.setOnClickListener(v -> finish());
        mSendButton.setOnClickListener(v -> {
            EditText mSubject;
            EditText mMessage;

            mSubject = findViewById(R.id.email_subject);
            mMessage = findViewById(R.id.email_message);

            ShareCompat.IntentBuilder.from(this)
                    .setType("message/rfc822")
                    .addEmailTo(emailToSendTo)
                    .setSubject(mSubject.getText().toString())
                    .setText(mMessage.getText().toString())
                    //.setHtmlText(body) //If you are using HTML in your body text
                    .setChooserTitle("Emailing")
                    .startChooser();

//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//
//            emailIntent.setType("*/*");
//            emailIntent.setData(Uri.parse(".mailto:"));
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, emailToSendTo);
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, mSubject.getText().toString());
//            emailIntent.putExtra(Intent.EXTRA_TEXT, mMessage.getText().toString());
//
//            try {
//                startNewActivity(Intent.createChooser(emailIntent, "Chooser Title"));
//                //toastSuccess();
//            } catch (android.content.ActivityNotFoundException ex) {
//                toastCouldNotSend();
//            }
//            //finish();
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
                    default:
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

    @Override
    public void startNewActivity(Intent intent) {
        startActivity(Intent.createChooser(intent, "Send mail..."));
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
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();

                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
