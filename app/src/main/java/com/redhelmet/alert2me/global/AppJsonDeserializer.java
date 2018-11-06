package com.redhelmet.alert2me.global;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
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
            JsonArray array = jsonObject.getAsJsonArray("coordinates");
            double[][][] coordinates = null;
            if (Geometry.POLYGON_TYPE.equals(type)) {
                 coordinates = context.deserialize(array, double[][][].class);
            } else if (Geometry.POINT_TYPE.equals(type)) {
                double[] point = context.deserialize(array, double[].class);
                coordinates = new double[1][1][2];
                coordinates[0][0] = point;
            }
            geometry.setCoordinates(coordinates);
            return geometry;
        }
    }

    public static class GeometrySerializer implements JsonSerializer<Geometry> {

        @Override
        public JsonElement serialize(Geometry src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", src.getType());
            if (src.getCoordinates() != null) {
                JsonArray coordinates = new JsonArray();
                if (Geometry.POINT_TYPE.equals(src.getType())) {
                    double[] point = src.getCoordinates()[0][0];
                    coordinates = context.serialize(point).getAsJsonArray();
                } else if (Geometry.POLYGON_TYPE.equals(src.getType())) {
                    coordinates = context.serialize(src.getCoordinates()).getAsJsonArray();
                }
                jsonObject.add("coordinates", coordinates);
            }
            return jsonObject;
        }
    }
}
