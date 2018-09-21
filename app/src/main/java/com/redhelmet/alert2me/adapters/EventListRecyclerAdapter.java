package com.redhelmet.alert2me.adapters;


import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.domain.util.EventUtils;
import com.redhelmet.alert2me.domain.util.IconUtils;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;


public class EventListRecyclerAdapter extends RecyclerView.Adapter<EventListRecyclerAdapter.EventsViewHolder> {

    private final List<Event> events;
    private EventUtils eventUtils;
  //  private ConfigUtils configUtils;
    private IconUtils iconUtils;
    private boolean isStateWide;

    class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventLocation, timeAgo, eventDistance;
        ImageView eventIcon;

        EventsViewHolder(View view) {
            super(view);
            eventName = (TextView) view.findViewById(R.id.list_event_name);
            eventLocation = (TextView) view.findViewById(R.id.list_event_location);
            eventIcon = (ImageView) view.findViewById(R.id.list_event_icon);
            timeAgo = (TextView) view.findViewById(R.id.list_event_time_ago);
            eventDistance = (TextView) view.findViewById(R.id.list_event_distance);
        }
    }


    public EventListRecyclerAdapter(Activity context, List<Event> eventList, boolean isStateWide) {
        events = eventList;
        eventUtils = new EventUtils();
        iconUtils = new IconUtils(context);
      //  this.configUtils = configUtils;
        this.isStateWide = isStateWide;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row_template, parent, false);


        return new EventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {
        Event event = events.get(position);
        Date updated = new Date(event.getUpdated());
        holder.eventName.setText(event.getType());
        if (isStateWide)
            holder.eventDistance.setVisibility(View.GONE);
        List<Area> areas = event.getArea();
        Area area = areas.get(0);
        String location = (area.getLocation() == null) ? "" : area.getLocation();
        String eventState = (area.getState() == null) ? "" : area.getState();
        holder.eventLocation.setText(String.format("%s %s", location, eventState));
        try {
            holder.timeAgo.setText(eventUtils.getTimeAgo(updated));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double distance = (event.getDistanceTo() / 1000);


        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        String output = numberFormat.format(distance);
        String formattedDistance = String.format("%s km", output);
        holder.eventDistance.setText(String.valueOf(formattedDistance));
        String backgroundColor =event.getPrimaryColor();
        Bitmap eventIcon = iconUtils.createEventIcon(R.layout.custom_list_layer_icon, event, backgroundColor, true, false,"");
        holder.eventIcon.setImageBitmap(eventIcon);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
