package com.redhelmet.alert2me.global;

import android.app.Activity;
import android.graphics.Bitmap;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.util.IconUtils;

public enum EventIcon {
    MAP_ICON {
        @Override
        public Bitmap createIcon(Activity context) {
            if (event == null) throw new Error("Haven't set event");
            return IconUtils.createEventIcon(context, R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, "");
        }
    },
    CLUSTER_ICON {
        @Override
        public Bitmap createIcon(Activity context) {
            if (event == null) throw new Error("Haven't set event");
            return IconUtils.createEventIcon(context, R.layout.custom_cluster_layer_icon, event, event.getPrimaryColor(), false, true, "");
        }
    },
    DETAIL_ICON {
        @Override
        public Bitmap createIcon(Activity context) {
            if (event == null) throw new Error("Haven't set event");
            return IconUtils.createEventIcon(context, R.layout.custom_list_layer_icon, event, event.getPrimaryColor(), true, false, "");
        }
    };

    public abstract Bitmap createIcon(Activity context);

    public EventIcon setEvent(Event event) {
        this.event = event;
        return this;
    }

    Event event;
}
