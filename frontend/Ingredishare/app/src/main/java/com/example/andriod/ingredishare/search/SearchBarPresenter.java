package com.example.andriod.ingredishare.search;

import android.content.Context;
import android.util.Log;

import com.example.andriod.ingredishare.DataManager;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.event.Event;
import com.example.andriod.ingredishare.event.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SearchBarPresenter {

    private SearchBarView view;
    private DataManager dataManager;
    private FirebaseUser mUser;
    private Context mContext;
    private Boolean newUser;
    private EventAdapter localEventAdapter;

    public SearchBarPresenter(SearchBarView view, EventAdapter eventAdapter) {
        this.view = view;
        this.localEventAdapter = eventAdapter;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getEventsFromQuery(String query){
        List<Event> queryList = MyApplication.getEventAdapter().getEventWithSpecificName(query);

        for(int i=0; i<queryList.size();i++){
            localEventAdapter.addEvent(queryList.get(i));
            Log.e(this.getClass().toString(), queryList.get(i).getUserId());
        }


        view.updateUI();
    }
}
