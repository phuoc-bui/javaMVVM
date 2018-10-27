package com.redhelmet.alert2me.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.util.IconUtils;

public class EventIcon extends FrameLayout {

    private ImageView eventShape, eventOutline, eventImage;
    private TextView eventCount;
    private boolean haveTail;
    private int itemsCount;

    public EventIcon(@NonNull Activity context, Event event, boolean haveTail, int itemsCount) {
        super(context);
        init();
        this.haveTail = haveTail;
        this.itemsCount = itemsCount;
        setEvent(event);
    }

    public EventIcon(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EventIcon,
                0, 0);

        try {
            haveTail = a.getBoolean(R.styleable.EventIcon_have_tail, false);
            itemsCount = a.getInteger(R.styleable.EventIcon_items_count, 0);
        } finally {
            a.recycle();
        }

        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_cluster_layer_icon, this, true);
        eventShape = view.findViewById(R.id.event_shape);
        eventOutline = view.findViewById(R.id.event_outline);
        eventImage = view.findViewById(R.id.event_image);
        eventCount = view.findViewById(R.id.event_count);

        if (!haveTail) {
            LayoutParams outlineParams = (LayoutParams) eventOutline.getLayoutParams();
            outlineParams.topMargin = (int) IconUtils.convertDpToPixel(getContext(), 2f);

            LayoutParams imageParams = (LayoutParams) eventImage.getLayoutParams();
            imageParams.topMargin = (int) IconUtils.convertDpToPixel(getContext(), 2f);
        }
    }

    public void setEvent(Event event) {
        eventShape.setImageResource(getShapeIdByEventType(event));
        eventOutline.setImageResource(getOutlineByEventType(event));
        eventImage.setImageResource(getIconResourceByName(event.getIcon(), event.getCategory()));
        if (itemsCount > 0) {
            eventCount.setVisibility(VISIBLE);
            eventCount.setText(String.valueOf(itemsCount));
        } else {
            eventCount.setVisibility(GONE);
        }

        int color = Color.parseColor(event.getPrimaryColor());
        eventShape.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private int getShapeIdByEventType(Event event) {
        switch (event.getCategory()) {
            case "warning":
                return haveTail ? R.drawable.ic_shape_warning : R.drawable.ic_background_warning;
            case "incident":
                return haveTail ? R.drawable.ic_shape_incident : R.drawable.ic_background_incident;
            case "support_service":
                return haveTail ? R.drawable.ic_shape_support_service : R.drawable.ic_background_support_service;
            case "env_monitoring":
                return haveTail ? R.drawable.ic_shape_env_monitoring : R.drawable.ic_background_env_monitoring;
            case "restriction":
                return haveTail ? R.drawable.ic_shape_restriction : R.drawable.ic_background_restriction;
            case "public_notice":
                return haveTail ? R.drawable.ic_shape_public_notice : R.drawable.ic_background_public_notice;
            case "disruption":
                return haveTail ? R.drawable.ic_shape_disruption : R.drawable.ic_background_disruption;
            case "public_observation":
                return haveTail ? R.drawable.ic_shape_observation : R.drawable.ic_background_observation;
            default:
                return haveTail ? R.drawable.ic_shape_warning : R.drawable.ic_background_warning;
        }
    }

    private int getOutlineByEventType(Event event) {
        boolean isFuture = event.getTemporal().toLowerCase().equals("future");
        switch (event.getCategory()) {
            case "warning":
                return isFuture ? R.drawable.ic_icon_warning_planned : R.drawable.ic_icons_warning_outline;
            case "incident":
                return isFuture ? R.drawable.ic_icon_incident_planned : R.drawable.ic_icons_incident_outline;
            case "support_service":
                return isFuture ? R.drawable.ic_icon_support_planned : R.drawable.ic_icons_support_outline;
            case "env_monitoring":
                return isFuture ? R.drawable.ic_icon_env_monitoring_planned : R.drawable.ic_icons_env_monitoring_outline;
            case "restriction":
                return isFuture ? R.drawable.ic_icon_ban_planned : R.drawable.ic_icons_ban_outline;
            case "public_notice":
                return isFuture ? R.drawable.ic_icons_notice_planned : R.drawable.ic_icons_notice_outline;
            case "disruption":
                return isFuture ? R.drawable.ic_icon_disruption_planned : R.drawable.ic_icons_disruption_outline;
            case "public_observation":
                return isFuture ? R.drawable.ic_icons_observation_planned : R.drawable.ic_icons_observation_outline;
            default:
                return isFuture ? R.drawable.ic_icon_warning_planned : R.drawable.ic_icons_warning_outline;
        }
    }

    public int getIconResourceByName(String icon, String category) {

        String resourceName = String.format("ic_%s", icon);
        int resource = IconUtils.getResId(resourceName, R.drawable.class);
        if (resource == -1) {
            resourceName = String.format("ic_icons_%s_other", category);
            resource = IconUtils.getResId(resourceName, R.drawable.class);
        }
        return (resource != -1) ? resource : R.drawable.icons_warning_empty;
    }

    public Bitmap convertToBitMap() {
        Activity activity = (Activity) getContext();
        if (activity == null) return null;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        setDrawingCacheEnabled(true);
        measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        setDrawingCacheEnabled(false);
        return bitmap;
    }
}
