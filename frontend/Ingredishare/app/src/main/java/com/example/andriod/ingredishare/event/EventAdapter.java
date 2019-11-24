package com.example.andriod.ingredishare.event;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.andriod.ingredishare.email.EmailActivity;
import com.example.andriod.ingredishare.MyApplication;
import com.example.andriod.ingredishare.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> items;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        public TextView event;
        public TextView id;
        public TextView data;
        public View sendButton;
        public TextView email;

        public EventViewHolder(View view) {
            super(view);
            email = (TextView) view.findViewById(R.id.email);
            event = (TextView) view.findViewById(R.id.event);
            id = (TextView) view.findViewById(R.id.id);
            data = (TextView) view.findViewById(R.id.data);
            sendButton = (View) view.findViewById(R.id.email_button);

            sendButton.setOnClickListener(v -> {
                EditText mDescription;
                EditText mName;

                Context mContext = MyApplication.getContext();
                Intent intent = new Intent(mContext, EmailActivity.class);
                intent.putExtra(mContext.getString(R.string.email),email.getText().toString());
                intent.putExtra(mContext.getString(R.string.email_subject),
                        "ingrediShare Post Response: " + id.getText().toString());
                mContext.startActivity(intent);
            });
        }
    }

    public EventAdapter(List<Event> items) {
        this.items = items;
    }

    public void addEvent(Event event) {
        // Add the event at the beginning of the list
        items.add(0, event);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.eventrow, viewGroup, false);

        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int i) {
        Event event = items.get(i);

        viewHolder.event.setText(event.getType());
        viewHolder.id.setText(event.getName());
        viewHolder.data.setText(event.getDescription());
        viewHolder.email.setText(event.getEmail());
    }
}
