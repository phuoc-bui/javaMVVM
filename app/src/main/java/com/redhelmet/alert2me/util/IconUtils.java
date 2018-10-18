package com.redhelmet.alert2me.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;

import java.lang.reflect.Field;


public class IconUtils {

    private IconUtils() {
    }

    public int getResourceByName(Context context, String resourceName, String definitionType) {
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        int resource = resources.getIdentifier(resourceName, definitionType, packageName);
        return (resource != 0) ? resource : R.drawable.icons_warning_empty;
    }

    public static GradientDrawable createRoundedBackgroundStroke(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_rounded_red_stroke);
        shape.setStroke(1, color);
        return shape;
    }

    public static GradientDrawable createRoundedBackground(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_rounded_white);
        shape.setColor(color);
        return shape;
    }

    public static GradientDrawable createTopRoundedBackground(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_top_rounded_white);
        shape.setColor(color);
        return shape;
    }

    public static Bitmap createEventIcon(Activity context, int resourceId, Event event, String backgroundColor, boolean isListIcon, boolean isClustered, String itemsCount) {

        LayoutInflater layoutInflater;
        String packageName;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Resources resources;

        layoutInflater = LayoutInflater.from(context);
        packageName = context.getPackageName();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        resources = context.getResources();


        View layers = layoutInflater.inflate(resourceId, null);
        ImageView eventShape = (ImageView) layers.findViewById(R.id.event_shape);
        ImageView eventOutline = (ImageView) layers.findViewById(R.id.event_outline);
        ImageView eventImage = (ImageView) layers.findViewById(R.id.event_image);


        layers.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        layers.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layers.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        layers.buildDrawingCache();


        Drawable shape;


        if (isListIcon) {
            shape = resources.getDrawable(getListShapeIdByEventType(event));
        } else {
            shape = resources.getDrawable(getShapeIdByEventType(event));
        }
        if (isClustered) {
            TextView eventCount = (TextView) layers.findViewById(R.id.event_count);
            eventCount.setText(itemsCount);
        }
        Drawable outline = resources.getDrawable(getOutline(event));
        Drawable icon = resources.getDrawable(getIconResourceByName(resources, event.getIcon(), packageName, event.getCategory()));

        Bitmap bitmap = Bitmap.createBitmap(layers.getMeasuredWidth(),
                layers.getMeasuredHeight(), Bitmap.Config.ARGB_8888);


        int color;
        if (backgroundColor == null) {
            color = Color.TRANSPARENT;
        } else
            color = Color.parseColor(backgroundColor);
        shape = DrawableCompat.wrap(shape);

        DrawableCompat.setTint(shape, color);
        DrawableCompat.setTintMode(shape, PorterDuff.Mode.SRC_IN);
        eventShape.setImageDrawable(shape);
        eventOutline.setImageDrawable(outline);
        eventImage.setImageDrawable(icon);

        Canvas canvas = new Canvas(bitmap);
        layers.draw(canvas);
        return bitmap;
    }

    public static int getShapeIdByEventType(Event event) {

        switch (event.getCategory()) {
            case "warning":
                return R.drawable.ic_shape_warning;
            case "incident":
                return R.drawable.ic_shape_incident;
            case "support_service":
                return R.drawable.ic_shape_support_service;
            case "env_monitoring":
                return R.drawable.ic_shape_env_monitoring;
            case "restriction":
                return R.drawable.ic_shape_restriction;
            case "public_notice":
                return R.drawable.ic_shape_public_notice;
            case "disruption":
                return R.drawable.ic_shape_disruption;
            case "public_observation":
                return R.drawable.ic_shape_observation;
            default:
                return R.drawable.ic_shape_warning;
        }
    }

    public static int getListShapeIdByEventType(Event event) {

        switch (event.getCategory()) {
            case "warning":
                return R.drawable.ic_background_warning;
            case "incident":
                return R.drawable.ic_background_incident;
            case "support_service":
                return R.drawable.ic_background_support_service;
            case "env_monitoring":
                return R.drawable.ic_background_env_monitoring;
            case "restriction":
                return R.drawable.ic_background_restriction;
            case "public_notice":
                return R.drawable.ic_background_public_notice;
            case "disruption":
                return R.drawable.ic_background_disruption;
            case "public_observation":
                return R.drawable.ic_background_observation;
            default:
                return R.drawable.ic_background_warning;
        }

    }

    public static int getOutline(Event event) {
        if (event.getTemporal() == null)
            return getOutlineByEventType(event);

        if (event.getTemporal().toLowerCase().equals("current")) {
            return getOutlineByEventType(event);

        } else if (event.getTemporal().toLowerCase().equals("future")) {
            return getFutureOutlineByEventType(event);
        } else {
            return getOutlineByEventType(event);
        }
    }

    private static int getOutlineByEventType(Event event) {

        switch (event.getCategory()) {
            case "warning":
                return R.drawable.ic_icons_warning_outline;
            case "incident":
                return R.drawable.ic_icons_incident_outline;
            case "support_service":
                return R.drawable.ic_icons_support_outline;
            case "env_monitoring":
                return R.drawable.ic_icons_env_monitoring_outline;
            case "restriction":
                return R.drawable.ic_icons_ban_outline;
            case "public_notice":
                return R.drawable.ic_icons_notice_outline;
            case "disruption":
                return R.drawable.ic_icons_disruption_outline;
            case "public_observation":
                return R.drawable.ic_icons_observation_outline;
            default:
                return R.drawable.ic_icons_warning_outline;
        }
    }

    private static int getFutureOutlineByEventType(Event event) {

        switch (event.getCategory()) {
            case "warning":
                return R.drawable.ic_icon_warning_planned;
            case "incident":
                return R.drawable.ic_icon_incident_planned;
            case "support_service":
                return R.drawable.ic_icon_support_planned;
            case "env_monitoring":
                return R.drawable.ic_icon_env_monitoring_planned;
            case "restriction":
                return R.drawable.ic_icon_ban_planned;
            case "public_notice":
                return R.drawable.ic_icons_notice_planned;
            case "disruption":
                return R.drawable.ic_icon_disruption_planned;
            case "public_observation":
                return R.drawable.ic_icons_observation_planned;
            default:
                return R.drawable.ic_icon_warning_planned;
        }
    }

    public static int getIconResourceByName(Resources resources, String icon, String packageName, String category) {

        String resourceName = String.format("ic_%s", icon);
        int resource = resources.getIdentifier(resourceName, "drawable", packageName);
        if (resource == 0) {
            resourceName = String.format("ic_icons_%s_other", category);
            resource = resources.getIdentifier(resourceName, "drawable", packageName);
        }
        return (resource != 0) ? resource : R.drawable.icons_warning_empty;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getColorWithAlpha(int color, float ratio) {
        int newColor;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(Context context, float pixel) {
        return pixel / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
