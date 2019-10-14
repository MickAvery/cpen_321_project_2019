package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private View mSignUp;
    private View mSignIn;
    private View mGoogleSignIn;
    private Context mContext;
    private FacebookSdk FacebookSdk;
    private AppEventsLogger AppEventsLogger;
    private LoginButton fbLoginButton;
    private CallbackManager fbcallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ImageView displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entering onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSignUp = findViewById(R.id.sign_up_button);
        mSignIn = findViewById(R.id.log_in_button);
        mGoogleSignIn = findViewById(R.id.google_auth);
        mContext = this;

        mGoogleSignIn.setOnClickListener(this);

        fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setPermissions(Arrays.asList("email", "public_profile"));

        fbcallbackManager = CallbackManager.Factory.create();

        fbLoginButton.registerCallback(fbcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                AccessToken accessToken = loginResult.getAccessToken();
                useFBLoginInformation(accessToken);
                Intent intent = new Intent(mContext, IngredientListActivity.class);
                Toast.makeText(mContext, "sign in", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, IngredientListActivity.class);
                Toast.makeText(mContext, "sign in", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, IngredientListActivity.class);
                Toast.makeText(mContext, "sign up", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("entering onstart");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        GoogleSignIn.
//        GoogleSignIn.silentSignIn()
//                .addOnCompleteListener(
//                        this,
//                        new OnCompleteListener<GoogleSignInAccount>() {
//                            @Override
//                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                                handleSignInResult(task);
//                            }
//                        });

        Log.println(Log.DEBUG, "tag", ",msg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // This task is always completed immediately, there is no need to attach an
        // asynchronous listener.
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        fbcallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Log.w("TAG", idToken);

            // TODO(developer): send ID Token to server and validate

//            updateUI(account);
        } catch (ApiException e) {
//            Log.w(TAG, "handleSignInResult:error", e);
//            updateUI(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_auth:
                signIn();
                break;
        }
    }


    private void useFBLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user's facebook details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        System.out.println(accessToken.getCurrentAccessToken().getPermissions());
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");

                    System.out.println(name);
                    System.out.println(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

}
