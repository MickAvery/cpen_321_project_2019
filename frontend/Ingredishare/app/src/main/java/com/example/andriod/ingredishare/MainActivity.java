package com.example.andriod.ingredishare;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener {

    private View mSignUp;
    private View mSignIn;
    private View mGoogleSignIn;
    private TextView mInvalidEmailView;
    private EditText mEmail;
    private EditText mPassword;

    private Context mContext;
    private FirebaseAuth mFirebaseAuth;
    private FacebookSdk FacebookSdk;
    private AppEventsLogger AppEventsLogger;
    private LoginButton fbLoginButton;
    private CallbackManager mFacebookCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ImageView displayImage;
    private GlobalRequestQueue mReqQueue;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReqQueue = GlobalRequestQueue.getInstance();
        mSignUp = findViewById(R.id.sign_up_button);
        mSignIn = findViewById(R.id.log_in_button);
        mGoogleSignIn = findViewById(R.id.google_sign_in_button);
        mEmail = findViewById(R.id.email_edit_text);
        mPassword = findViewById(R.id.password_edit_text);
        mInvalidEmailView = findViewById(R.id.invalid_email);
        mContext = this;

        mSignIn.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        mGoogleSignIn.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(this);

        fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setPermissions(Arrays.asList("email", "public_profile"));

        mFacebookCallbackManager = CallbackManager.Factory.create();

        fbLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                AccessToken accessToken = loginResult.getAccessToken();
                attemptFirebaseAuthWithFacebook(accessToken);
//                useFBLoginInformation(accessToken);
            }
            @Override
            public void onCancel() {
                Log.e(this.getClass().toString(), "facebook signin cancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.e(this.getClass().toString(), "facebook signin error ", error);
            }
        });


//        mSignIn.setOnClickListener(view -> {
//            attemptLogIn(mEmail.getText().toString(), mPassword.getText().toString());
//            Log.d("resp", "Go to livefeed");
//        });
//
//
//        mSignUp.setOnClickListener(view -> {
//            attemptSignUp(mEmail.getText().toString(), mPassword.getText().toString());
//            Log.d("resp", "Go to livefeed");
//        });

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

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if(currentUser != null) {
            verifyFirebaseTokenBackend(currentUser);
        }

//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
//
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // TODO check mFirebaseAuth for active users
//
//        // TODO: handle case if they say no
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    0);
//        }
//
//        boolean isLoggedInFacebook = fbAccessToken != null && !fbAccessToken.isExpired();
//
//        if(account != null) {
//            /* check if user exists in backend */
//            String url = getString(R.string.server_url) + getString(R.string.is_existing_user_get);
//
//            JSONObject getParams = new JSONObject();
//            String email = account.getEmail();
//
//            MyApplication.setUserEmail(email);
//
//            try {
//                getParams.put("email", email);
//
//                JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, getParams,
//                        (JSONObject response) -> {
//                            try {
//                                boolean pre_existing_user = response.getBoolean("pre_existing_user");
//
//                                if(pre_existing_user) {
//                                    /* TODO: proceed to live feed. Get rid of "go to" log after done*/
//                                    Log.d("resp", "Go to livefeed");
//                                    Intent intent = new Intent(this, IngredientListActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    /* TODO: new user activity. Get rid of "go to" log after done */
//                                    Log.d("resp", "Create new user");
//                                    Intent intent = new Intent(this, ProfileActivity.class);
//                                    startActivity(intent);
//                                }
//
//                            } catch (JSONException jsonEx) {
//                                Log.e(this.getClass().toString(), jsonEx.toString());
//                            }
//                        },
//
//                        (VolleyError error) -> Log.e(this.getClass().toString(), "VolleyError",  error)
//                );
//
//                mReqQueue.addToRequestQueue(jsonObjReq, "post");
//            } catch(JSONException jsonEx) {
//
//            }
//        } else if(isLoggedInFacebook) {
//            /* TODO: we might wanna check if the user exists in the backend even after they log in via fb, but this will do for now */
//            Intent intent = new Intent(this, IngredientListActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            /* result returned from launching intent from Google Signin button */
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                attemptFirebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                /*
                 * Based on testing, the code gets here if the user decides not to continue with Google Signin
                 **/
                // TODO: error handling if necessary
            }
//            handleSignInResult(task);
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

//    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String idToken = account.getIdToken();
//            String email = account.getEmail();
//            Log.w("TAG", idToken);
//
//            // Save user email for access across app
//            MyApplication.setUserEmail(email);
//
//            // Send ID Token to server and validate
//            String url = getString(R.string.server_url) + getString(R.string.tok_signin_put);
//
//            JSONObject postparams = new JSONObject();
//
//            try {
//                postparams.put(getString(R.string.id_token), idToken);
//
//                JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
//                        (JSONObject response) -> {
//                            try {
//                                boolean pre_existing_user = response.getBoolean("pre_existing_user");
//
//                                if(pre_existing_user) {
//                                    /* TODO: proceed to live feed */
//                                    Log.println(Log.DEBUG, "resp", "Go to livefeed");
//                                    Intent intent = new Intent(this, IngredientListActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    /* TODO: new user activity */
//                                    Log.println(Log.DEBUG, "resp", "Create new user");
//                                    Intent intent = new Intent(this, ProfileActivity.class);
//                                    startActivity(intent);
//                                }
//
//                            } catch (JSONException jsonEx) {
//
//                            }
//                        },
//
//                        (VolleyError error) -> {
//                            Log.println(Log.DEBUG, "resp", "error");
//                        }
//                );
//
//                mReqQueue.addToRequestQueue(jsonObjReq, "post");
//            } catch(JSONException jsonEx) {
//                Log.e(this.getClass().toString(), "handleSignInResult:error", jsonEx);
//            }
//
//        } catch (ApiException e) {
//            Log.e(this.getClass().toString(), "handleSignInResult:error", e);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                /* start new intent for Google Signin */

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                /* at this point, the result from the intent is handled in onActivityResult() */
                break;

            case R.id.log_in_button: {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                attemptEmailSignIn(email, password);
                break;
            }

            case R.id.sign_up_button: {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                attemptEmailSignUp(email, password);
                break;
            }
        }
    }

    /*
     *
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        LoginManager.getInstance().logOut(); /* log out of facebook */
        mGoogleSignInClient.signOut();
    }

    private void attemptEmailSignIn(String email, String password) {
        boolean emailIsValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if(emailIsValid) {
            /* attempt sign in using Firebase Auth API */
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                        if(task.isSuccessful()) {
                            /* Firebase Signin successful, send ID token to backend for verification */
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            verifyFirebaseTokenBackend(user);
                        } else {
                            /* Firebase signin failed */
                            mInvalidEmailView.setText(R.string.fail_log_in);
                            mInvalidEmailView.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            mInvalidEmailView.setText(R.string.invalid_email);
            mInvalidEmailView.setVisibility(View.VISIBLE);
        }
    }

    private void attemptEmailSignUp(String email, String password) {
        boolean emailIsValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if(emailIsValid) {
            /* attempt to register using Firebase Auth API */
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                        if(task.isSuccessful()) {
                            /* new user created, send ID token to backend for verification */
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            verifyFirebaseTokenBackend(user);
                        } else {
                            /* failed to create user, reasons below */

                            if(task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                /* invalid password */
                                mInvalidEmailView.setText(R.string.fail_sign_up_invalid_password);

                            } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                /* invalid email */
                                mInvalidEmailView.setText(R.string.invalid_email);

                            } else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                /* email already in use */
                                mInvalidEmailView.setText(R.string.fail_sign_up_user_exists);

                            }

                            mInvalidEmailView.setVisibility(View.VISIBLE);
                        }
            });
        } else {
            mInvalidEmailView.setText(R.string.invalid_email);
            mInvalidEmailView.setVisibility(View.VISIBLE);
        }
    }

    private void attemptFirebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                    if(task.isSuccessful()) {
                        /* send ID token to backend for verification */
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        verifyFirebaseTokenBackend(user);
                    } else {
                        /* Google Signin failed for one reason or another */

                        if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                            /* user already exists through another signin method */
                            mInvalidEmailView.setText("User already exists through another signin method");
                        } else {
                            /*
                             * At this point, exception can either be
                             *  - FirebaseAuthInvalidUserException (user account you are trying to sign in to has been disabled)
                             *  - FirebaseAuthInvalidCredentialsException (thrown if the credential is malformed or has expired)
                             * (https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.html#signInWithCredential(com.google.firebase.auth.AuthCredential))
                             * Just throw generic error
                             */
                            mInvalidEmailView.setText("Failed to log in with Google email");
                        }

                        mFirebaseAuth.signOut(); /* sign out of Firebase auth */
                        mInvalidEmailView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void attemptFirebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                    if(task.isSuccessful()) {
                        /* send ID token to backend for verification */
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        verifyFirebaseTokenBackend(user);
                    } else {
                        /* Facebook signin failed for one reason or another */

                        if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                            /* user already exists through another signin method */
                            mInvalidEmailView.setText("User already exists through another signin method");
                        } else {
                            /*
                             * At this point, exception can either be
                             *  - FirebaseAuthInvalidUserException (user account you are trying to sign in to has been disabled)
                             *  - FirebaseAuthInvalidCredentialsException (thrown if the credential is malformed or has expired)
                             * (https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.html#signInWithCredential(com.google.firebase.auth.AuthCredential))
                             * Just throw generic error
                             */
                            mInvalidEmailView.setText("Failed to log in with Facebook");
                        }

                        mFirebaseAuth.signOut(); /* sign out of Firebase auth */
                        mInvalidEmailView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void verifyFirebaseTokenBackend(FirebaseUser user) {

        user.getIdToken(true)
                .addOnCompleteListener((Task<GetTokenResult> task) -> {
                    if(task.isSuccessful()) {
                        /* Send token to backend for verification */
                        // TODO: implement this
                        String idToken = task.getResult().getToken();

                        Intent intent = new Intent(this, IngredientListActivity.class);
                        startActivity(intent);
                    } else {
                        /* failed to get token for one reason or another */
                    }
                });

    }

//    private void attemptSignUp(String email, String password) {
//        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//
//            mInvalidEmailView.setVisibility(View.INVISIBLE);
//            String url = getString(R.string.server_url) + getString(R.string.user_pass_sign_up);
//            JSONObject postparams = new JSONObject();
//
//            try {
//                postparams.put(getString(R.string.email), email);
//                postparams.put(getString(R.string.password), password);
//
//                JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
//                        (JSONObject response) -> {
//                            try {
//                                boolean success = response.getBoolean(getString(R.string.success));
//
//                                if (success) {
//                                    MyApplication.setUserEmail(email);
//                                    Log.println(Log.DEBUG, "resp", "Go to new profile");
//                                    Intent intent = new Intent(this, ProfileActivity.class);
//                                    startActivity(intent);
//                                } else {
//                                    mInvalidEmailView.setText(getString(R.string.fail_sign_up));
//                                    mInvalidEmailView.setVisibility(View.VISIBLE);
//                                }
//
//                            } catch (JSONException jsonEx) {
//
//                            }
//                        },
//
//                        (VolleyError error) -> {
//                            Log.println(Log.DEBUG, "resp", "error");
//                        }
//                );
//
//                mReqQueue.addToRequestQueue(jsonObjReq, "post");
//            } catch (JSONException jsonEx) {
//                Log.e(this.getClass().toString(), "attemptLogIn:error", jsonEx);
//            }
//        } else {
//            mInvalidEmailView.setText(R.string.invalid_email);
//            mInvalidEmailView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void attemptLogIn(String email, String password) {
//        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//
//            mInvalidEmailView.setVisibility(View.INVISIBLE);
//            String url =
//                    getString(R.string.server_url) + getString(R.string.user_pass_log_in)
//                    + "?"
//                    + "email=" + email
//                    + "&"
//                    + "password=" + password;
//
//            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
//                    (JSONObject response) -> {
//                        try {
//                            boolean success = response.getBoolean(getString(R.string.success));
//
//                            if (success) {
//                                MyApplication.setUserEmail(email);
//                                Log.println(Log.DEBUG, "resp", "Go to livefeed log in");
//                                Intent intent = new Intent(this, IngredientListActivity.class);
//                                startActivity(intent);
//                            } else {
//                                mInvalidEmailView.setText(getString(R.string.fail_log_in));
//                                mInvalidEmailView.setVisibility(View.VISIBLE);
//                            }
//
//                        } catch (JSONException jsonEx) {
//
//                        }
//                    },
//
//                    (VolleyError error) -> {
//                        Log.println(Log.DEBUG, "resp", "error");
//                    }
//            );
//
//            mReqQueue.addToRequestQueue(jsonObjReq, "post");
//        } else {
//            mInvalidEmailView.setText(R.string.invalid_email);
//            mInvalidEmailView.setVisibility(View.VISIBLE);
//        }
//    }


    private void useFBLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user's facebook details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
      //  Log.println(Log.DEBUG, "resp", accessToken.getCurrentAccessToken().getPermissions().toString());
       // System.out.println(accessToken.getCurrentAccessToken().getPermissions());
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    String url = getString(R.string.server_url) + getString(R.string.fb_user_info_put);

                    MyApplication.setUserEmail(email);
                    JSONObject postparams = new JSONObject();

                    try {
                        postparams.put(getString(R.string.email), email);
                        postparams.put(getString(R.string.full_name), name);
                        postparams.put(getString(R.string.fb_profile_photo), image);

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(url, postparams,
                                (JSONObject jsonResponse) -> {
                                    try {
                                        boolean pre_existing_user = jsonResponse.getBoolean("pre_existing_user");

                                        if (pre_existing_user) {
                                            /* TODO: proceed to live feed */
                                            Log.println(Log.DEBUG, "resp", "Go to livefeed");
                                            Intent intent = new Intent(mContext, IngredientListActivity.class);
                                            startActivity(intent);
                                        } else {
                                            /* TODO: new user activity */
                                            Log.println(Log.DEBUG, "resp", "Create new user");
                                            Intent intent = new Intent(mContext, ProfileActivity.class);
                                            startActivity(intent);
                                        }

                                    } catch (JSONException jsonEx) {

                                    }
                                },

                                (VolleyError error) -> {
                                    Log.println(Log.DEBUG, "resp", "error");
                                }
                        );

                        mReqQueue.addToRequestQueue(jsonObjReq, "post");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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