package com.example.andriod.ingredishare;

import android.app.usage.UsageEvents;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> items;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        public TextView event;
        public TextView id;
        public TextView data;

        public EventViewHolder(View v) {
            super(v);
            event = (TextView) v.findViewById(R.id.event);
            id = (TextView) v.findViewById(R.id.id);
            data = (TextView) v.findViewById(R.id.data);
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

        viewHolder.event.setText(event.getName());
        viewHolder.id.setText(event.getId());
        viewHolder.data.setText(event.getData());
    }
}
