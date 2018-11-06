package com.redhelmet.alert2me.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.redhelmet.alert2me.data.model.Event;

import java.util.List;

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

    public static void updateCamera(Context context, GoogleMap map, List<LatLng> points) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < points.size(); i++) {
            builder.include(points.get(i));
        }

        LatLngBounds bounds = builder.build();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        map.animateCamera(cu);
    }

    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
