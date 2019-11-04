package com.example.andriod.ingredishare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener {

    private View mSignUp;
    private View mSignIn;
    private View mGoogleSignIn;
    private TextView mInvalidEmailView;
    private EditText mEmail;
    private EditText mPassword;

    private FirebaseAuth mFirebaseAuth;
    private LoginButton fbLoginButton;
    private CallbackManager mFacebookCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private GlobalRequestQueue mReqQueue;

    private String mFirebaseCloudMsgRegistrationToken;

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

        /* Let's try to get the Firebase Cloud Messaging registration token */
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener((Task<InstanceIdResult> task) -> {
                    if(task.isSuccessful()) {
                        mFirebaseCloudMsgRegistrationToken = task.getResult().getToken();
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

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO: handle case if they say no
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        if(currentUser != null) {
            verifyFirebaseTokenBackend(currentUser, false);
        }
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
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

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
                            Boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            verifyFirebaseTokenBackend(user, newUser);
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
                            Boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            verifyFirebaseTokenBackend(user, newUser);
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
                        Boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        verifyFirebaseTokenBackend(user, newUser);
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
                        Boolean newUser = task.getResult().getAdditionalUserInfo().isNewUser();

                        verifyFirebaseTokenBackend(user, newUser);
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

    private void verifyFirebaseTokenBackend(FirebaseUser user, boolean newUser) {

        user.getIdToken(true)
                .addOnCompleteListener((Task<GetTokenResult> task) -> {
                    if(task.isSuccessful()) {
                        /* Send token to backend for verification */

                        String idToken = task.getResult().getToken();

                        Uri uriQuery = Uri.parse(getString(R.string.server_url))
                                .buildUpon()
                                .appendPath("firebaseVerifyIdToken")
                                .appendQueryParameter("idTok", idToken)
                                .appendQueryParameter("fcmTok", mFirebaseCloudMsgRegistrationToken)
                                .build();

                        StringRequest req = new StringRequest(Request.Method.POST, uriQuery.toString(),
                                (String response) -> {
                                    Intent intent;

                                    if(newUser) {
                                        intent = new Intent(this, ProfileActivity.class);
                                    } else {
                                        intent = new Intent(this, IngredientListActivity.class);
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    startActivity(intent);
                                },

                                (VolleyError error) -> {
                                    mInvalidEmailView.setText("Login failed for some reason");
                                    mInvalidEmailView.setVisibility(View.VISIBLE);
                                    mFirebaseAuth.signOut();
                                });

                        mReqQueue.addToRequestQueue(req, "TAG");
                    } else {
                        /* failed to get token for one reason or another */
                        mInvalidEmailView.setText("Login failed for some reason");
                        mInvalidEmailView.setVisibility(View.VISIBLE);
                        mFirebaseAuth.signOut();
                    }
                });
    }
}
