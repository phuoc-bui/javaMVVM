package com.redhelmet.alert2me.domain.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.redhelmet.alert2me.model.CustomMarker;
import com.redhelmet.alert2me.model.Event;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.redhelmet.alert2me.R;


public class CustomClusterRenderer extends DefaultClusterRenderer<CustomMarker> {
    private EventUtils eventUtils;
    private IconUtils iconUtils;
    private Context _context;
    private DisplayMetrics metrics;
    private ConfigUtils configUtils;

    public CustomClusterRenderer(Activity context, GoogleMap map, ClusterManager<CustomMarker> clusterManager) {
        super(context, map, clusterManager);
        _context = context;
        eventUtils = new EventUtils();
        iconUtils = new IconUtils(context);
        metrics = _context.getResources().getDisplayMetrics();

    }


    @Override
    protected void onBeforeClusterItemRendered(CustomMarker item,
                                               MarkerOptions markerOptions) {
        Event event = item.getEvent();
        if (event != null) {
            Bitmap eventIcon = iconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, "");
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(eventIcon));
            markerOptions.title(String.format("%s",  event.getName()));
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<CustomMarker> cluster, MarkerOptions markerOptions) {
        Collection<CustomMarker> items = cluster.getItems();
        List<CustomMarker> markers = new ArrayList<>(items);

        Collections.sort(markers, new Comparator<CustomMarker>() {
            @Override
            public int compare(CustomMarker customMarker, CustomMarker customMarker1) {
                return customMarker.getEvent().getSeverity() < customMarker1.getEvent().getSeverity() ? 1 : (customMarker.getEvent().getSeverity() > customMarker1.getEvent().getSeverity() ? -1 : 0);
            }
        });
         List<Event> events = new ArrayList<>();
        // for each loop
        for (CustomMarker item : markers)
        {
            if (item.getEvent() != null)
            {
                events.add(item.getEvent());
            }
        }
        CustomMarker customMarker = markers.get(0);

        Event event = customMarker.getEvent();
        if (event != null) {
            int size = items.size();
            Bitmap clusteredIcon = iconUtils.createEventIcon(R.layout.custom_cluster_layer_icon, event,  event.getPrimaryColor(), false, true, String.valueOf(size));
            if (clusteredIcon != null)
//                markerOptions.title(String.format("%s events\nTap to see all", size));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(clusteredIcon));
            if(!events.isEmpty()) {
                customMarker.setObjects(events);
            }

        }
    }
    @Override
    protected boolean shouldRenderAsCluster(Cluster<CustomMarker> cluster) {
        return cluster.getSize() > 1;
    }


}
