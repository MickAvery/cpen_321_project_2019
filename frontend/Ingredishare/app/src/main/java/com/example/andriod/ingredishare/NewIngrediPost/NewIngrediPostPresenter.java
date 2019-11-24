package com.example.andriod.ingredishare.NewIngrediPost;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class NewIngrediPostPresenter {


    private NewIngrediPostView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;

    public NewIngrediPostPresenter(DataManager dataManager, NewIngrediPostView view) {
        this.dataManager = dataManager;
        this.view = view;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void savePost(String description, String name, String type) {

        // TODO(developer): send ID Token to server and validate
        String url = MyApplication.urlCreateRequest();

        JSONObject postparams = new JSONObject();

        if(description.equals("") ||
           name.equals("")) {
            view.displayInputAllFieldsToast();
            return;
        }

        try {
            postparams.put(mContext.getString(R.string.name), name);
            postparams.put(mContext.getString(R.string.description), description);
            postparams.put(mContext.getString(R.string.userId), mUser.getEmail());
            postparams.put(mContext.getString(R.string.type), type);

            Double[] loc = view.getLocation();

            if(loc.length > 0) {
                postparams.put("lat", loc[0]);
                postparams.put("long", loc[1]);
            } else{
                postparams.put("lat", null);
                postparams.put("long", null);
            }
            Log.d("latitude", loc[0].toString());
            Log.d("longitude", loc[1].toString());

            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
                        Boolean success_response = response.getBoolean("createRequestResponse");

                        if (success_response) {
                            Log.d("resp", "Sent to backend successfully");
                            view.startIngredientListActivity();
                        } else {
                            view.toastCouldNotPost();
                        }
                    } catch (JSONException jsonEx) {
                        view.toastCouldNotPost();
                        Log.e(this.getClass().toString(), jsonEx.toString());
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("POST Request Error", error.toString());
                    view.toastCouldNotPost();
                }
            };

            if (dataManager != null) {
                dataManager.postJSONObject(url, postparams, listener, errorListener);
            }

            notifyAfterPost(mUser.getEmail(), name, description, type);

        } catch(JSONException e){
            view.toastCouldNotPost();
        }
    }

    private void notifyAfterPost(String userId, String name, String description, String type) {
        String url = MyApplication.getServerURL();

        Uri uriQuery = Uri.parse(url)
                .buildUpon()
                .appendPath("notifyOtherUsers")
                .appendQueryParameter(mContext.getString(R.string.userId), userId)
                .appendQueryParameter(mContext.getString(R.string.name), name)
                .appendQueryParameter(mContext.getString(R.string.description), description)
                .appendQueryParameter(mContext.getString(R.string.type), type)
                .build();

        StringRequest req = new StringRequest(Request.Method.POST, uriQuery.toString(),
                (String response) -> {
                },

                (VolleyError error) -> {
                });

        GlobalRequestQueue.getInstance().addToRequestQueue(req, "TAG");
    }
}
