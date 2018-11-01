package com.redhelmet.alert2me.global;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.redhelmet.alert2me.data.model.Event;

import java.lang.reflect.Type;

public class AppJsonDeserializer {
    public static class EventsDeserializer implements JsonDeserializer<Event.EventList> {

        @Override
        public Event.EventList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Event.EventList events = new Event.EventList();
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                try {
                    Event event = context.deserialize(element, Event.class);
                    events.add(event);
                } catch (JsonParseException e) {
                    Log.e("EventsDeserializer", "Error when parse event: " + e);
                }

            }
            return events;
        }
    }
}
