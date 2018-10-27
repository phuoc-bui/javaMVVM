package com.redhelmet.alert2me.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redhelmet.alert2me.R;


public class EmptyListRecyclerAdapter extends RecyclerView.Adapter<EmptyListRecyclerAdapter.EventsViewHolder> {

    private String emptyText;

    public EmptyListRecyclerAdapter(String emptyText) {
        this.emptyText = emptyText;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_empty_template, parent, false);
        TextView emptyTextView = (TextView) itemView.findViewById(R.id.empty_text);
        emptyTextView.setText(this.emptyText);

        return new EmptyListRecyclerAdapter.EventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class EventsViewHolder extends RecyclerView.ViewHolder {
        EventsViewHolder(View view) {
            super(view);
        }
    }
}

