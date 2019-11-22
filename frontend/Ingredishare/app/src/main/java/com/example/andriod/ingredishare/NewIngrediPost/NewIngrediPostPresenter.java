package com.example.andriod.ingredishare.NewIngrediPost;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.Event.EventAdapter;
import com.example.andriod.ingredishare.GlobalRequestQueue;
import com.example.andriod.ingredishare.IngredientList.IngredientListActivity;
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
                        Log.e(this.getClass().toString(), jsonEx.toString());
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("POST Request Error", error.toString());
                }
            };

            dataManager.postJSONObject(url, postparams, listener, errorListener);

        } catch(JSONException e){

        }
    }
}
