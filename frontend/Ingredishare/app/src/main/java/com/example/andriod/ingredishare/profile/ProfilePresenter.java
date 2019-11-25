package com.example.andriod.ingredishare.profile;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePresenter  {

    private ProfileView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;

    public ProfilePresenter(DataManager dataManager, ProfileView view) {
        this.dataManager = dataManager;
        this.view = view;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getProfileInfo(){

        HashMap<String, String> profileData = new HashMap<String,String>();
        String url = MyApplication.getURL_profileInfoGET()
                + "?email=" + mUser.getEmail();

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    Log.e(this.getClass().toString(), response.toString());

                    String name = response.getString(mContext.getString(R.string.display_name));
                    String bio = response.getString(mContext.getString(R.string.bio));
                    String preference = response.getString(mContext.getString(R.string.food_preferences));
                    String radius = response.getString(mContext.getString(R.string.radius_preference));
                    // If values are null just leave them empty
                    if (name != null && bio != null && preference != null && radius != null) {
                        profileData.put(mContext.getString(R.string.display_name), name);
                        profileData.put(mContext.getString(R.string.bio), bio);
                        profileData.put(mContext.getString(R.string.food_preferences), preference);
                        profileData.put(mContext.getString(R.string.radius_preference), radius);

                        view.updateUI(profileData);
                    } else{
                        view.displayCouldNotFindProfileInfoToast();
                        view.hideBackButton();
                    }
                } catch (JSONException jsonEx) {
                    Log.e(this.getClass().toString(), jsonEx.toString());
                    view.displayCouldNotFindProfileInfoToast();
                    view.hideBackButton();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("GET Request Error", error.toString());
                view.displayCouldNotFindProfileInfoToast();
            }
        };

        dataManager.getJSONObject(url, null, listener, errorListener);
    }

    public void updateProfileInfo(HashMap<String, String> newProfileData){

        try {
            JSONObject postparams = new JSONObject();

            String displayName = "";
            String bio = "";
            String preferences = "";
            String radius = "";
            // If values are null just leave them empty
            if (newProfileData.containsKey(mContext.getString(R.string.display_name))) {
                displayName = newProfileData.get(mContext.getString(R.string.display_name));
            }
            if (newProfileData.containsKey(mContext.getString(R.string.bio))) {
                bio = newProfileData.get(mContext.getString(R.string.bio));
            }
            if (newProfileData.containsKey(mContext.getString(R.string.food_preferences))) {
                preferences = newProfileData.get(mContext.getString(R.string.food_preferences));
            }

            if(newProfileData.containsKey(mContext.getString(R.string.radius_preference))){
                radius = newProfileData.get(mContext.getString(R.string.radius_preference));
            }

            if("".equals(displayName) ||
                    "".equals(bio) ||
                    "".equals(preferences)){
                throw new StringIndexOutOfBoundsException();
            }

            postparams.put(mContext.getString(R.string.email), mUser.getEmail());
            postparams.put(mContext.getString(R.string.display_name), displayName);
            postparams.put(mContext.getString(R.string.bio), bio);
            postparams.put(mContext.getString(R.string.food_preferences), preferences);
            postparams.put(mContext.getString(R.string.radius_preference), radius);


            String url = MyApplication.getProfileInfoPOSTRequestURL();
            Log.e(this.getClass().toString(), postparams.toString());

            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
                        Boolean success_response = response.getBoolean("updateProfileInfo");
                        if (success_response) {
                            Log.e(this.getClass().toString(), "updateProfile success");

                            view.displaySavedToast();
                            view.displayBackButton();
                            view.setIngrediListActivityIntent();
                        }
                    } catch (JSONException jsonEx) {
                        Log.e(this.getClass().toString(), jsonEx.toString());
                        view.hideBackButton();
                        view.toastError();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("POST Request Error", error.toString());
                    view.toastError();
                }
            };

            dataManager.postJSONObject(url, postparams, listener, errorListener);

        } catch(JSONException e){

        } catch(StringIndexOutOfBoundsException e){
            view.displayInputAllFieldsToast();
        }
    }

    public void setNewUser(Boolean newUser){
        this.newUser = newUser;

        if(newUser){
            view.hideBackButton();
        }
    }

    public void backButtonPressed(){
        view.setIngrediListActivityIntent();
    }




}
