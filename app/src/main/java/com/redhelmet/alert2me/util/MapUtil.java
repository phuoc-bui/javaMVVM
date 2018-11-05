package com.redhelmet.alert2me.util;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.redhelmet.alert2me.data.model.Event;

public class MapUtil {
    private MapUtil() {
    }

    public static PolygonOptions createPolygonFromEvent(Event event) {
        if (event.getGeometry() != null && event.getGeometry().getCoordinates() != null)
            return createPolygonFromCoordinates(event.getGeometry().getCoordinates()[0],
                    Color.parseColor(event.getPrimaryColor()),
                    Color.parseColor(event.getPrimaryColor()));
        return null;
    }

    public static PolygonOptions createPolygonFromCoordinates(double[][] coordinates, int strokeColor, int fillColor) {
        if (coordinates != null) {
            PolygonOptions polygon = new PolygonOptions();
            polygon.strokeColor(strokeColor);
            polygon.fillColor(IconUtils.getColorWithAlpha(fillColor, 0.4f));
            for (double[] coordinate : coordinates) {
                polygon.add(new LatLng(coordinate[1], coordinate[0]));
            }
            return polygon;
        } else return null;
    }

    public static Circle drawCircle(GoogleMap map, LatLng center, double radius, int strokeColor, int fillColor) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeColor(strokeColor)
                .strokeWidth(5.0f)
                .fillColor(fillColor));
    }

    public static Circle drawCircle(GoogleMap map, LatLng center, double radius, String strokeColor, String fillColor) {
        return drawCircle(map, center, radius, Color.parseColor(strokeColor), Color.parseColor(fillColor));
    }
}
