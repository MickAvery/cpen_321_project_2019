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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
    private RequestQueue requestQueue;

    /*
    Create a getRequestQueue() method to return the instance of
    RequestQueue.This kind of implementation ensures that
    the variable is instatiated only once and the same
    instance is used throughout the application
    */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }
    /*
    public method to add the Request to the the single
    instance of RequestQueue created above.Setting a tag to every
    request helps in grouping them. Tags act as identifier
    for requests and can be used while cancelling them
    */
    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
    /*
    Cancel all the requests matching with the given tag
    */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

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

        if(account == null) { /* TODO set back to != after debugging */
            Intent intent = new Intent(mContext, IngredientListActivity.class);
            startActivity(intent);
        }
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
            String email = account.getEmail();
            Log.w("TAG", idToken);

            // TODO(developer): send ID Token to server and validate
            String url = "http://10.0.2.2:1337/tokensignin/";

            JSONObject postparams = new JSONObject();

            try {
                postparams.put("idToken", idToken);

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                        (JSONObject response) -> {
                            try {
                                boolean pre_existing_user = response.getBoolean("pre_existing_user");

                                if(pre_existing_user) {
                                    /* TODO: proceed to live feed */
                                    Log.println(Log.DEBUG, "resp", "Go to livefeed");
                                    mContext = this;
                                    Intent intent = new Intent(mContext, IngredientListActivity.class);
                                    startActivity(intent);
                                } else {
                                    /* TODO: new user activity */
                                    Log.println(Log.DEBUG, "resp", "Create new user");
                                    mContext = this;
                                    Intent intent = new Intent(mContext, ProfileActivity.class);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                }

                            } catch (JSONException jsonEx) {

                            }
                        },
                        (VolleyError error) -> {
                            Log.println(Log.DEBUG, "resp", "error");
                        }
                );

                addToRequestQueue(jsonObjReq, "post");
            } catch(JSONException jsonEx) {

            }

        } catch (ApiException e) {
            Log.e("Auth", "handleSignInResult:error", e);
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

                    // TO DO : Send name, photo, and email to backend for storage
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
