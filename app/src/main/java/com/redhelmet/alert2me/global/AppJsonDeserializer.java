package com.redhelmet.alert2me.global;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.Geometry;

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

    public static class GeometryDeserializer implements JsonDeserializer<Geometry> {

        @Override
        public Geometry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Geometry geometry = new Geometry();
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            geometry.setType(type);
            if (type.equals("Polygon")) {
                 JsonArray array = jsonObject.getAsJsonArray("coordinates");
                 double[][][] coordinates = context.deserialize(array, double[][][].class);
                 geometry.setCoordinates(coordinates);
            }
            return geometry;
        }
    }
}
