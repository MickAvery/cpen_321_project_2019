package com.example.andriod.ingredishare.search;

import android.content.Context;

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
    private EventAdapter eventAdapter;

    public SearchBarPresenter(SearchBarView view,
                                   EventAdapter eventAdapter) {
        this.view = view;
        this.eventAdapter = eventAdapter;
        mContext = MyApplication.getContext();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getEventsFromQuery(String query){
        List<Event> queryList = eventAdapter.getEventWithID(query);

        EventAdapter localEventAdapter = new EventAdapter(queryList);
        view.setActivityEventAdapter(localEventAdapter);
    }
}
