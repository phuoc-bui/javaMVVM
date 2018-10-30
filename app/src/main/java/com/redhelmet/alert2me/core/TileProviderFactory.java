package com.redhelmet.alert2me.core;

import android.text.TextUtils;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class TileProviderFactory {


    private String GetWmsUrl(String baseUrl,ArrayList<String> layers) {
        String baseUri = baseUrl + "?LAYERS={layerName}&ZL=11.364912235636&BBOX=%s,%s,%s,%s";
        String url = "";
        if (layers.size() > 0) {
            String joined = TextUtils.join(",", layers);
            url = baseUri.replace("{layerName}", joined);
        }
        Log.e("URL",url);
        return url;
    }

    public WmsTileProvider GetWmsTileProvider(String baseUrl,ArrayList<String> layers) {

        final String WMS_URL = GetWmsUrl(baseUrl,layers);
        if (WMS_URL.equals("")) return null;

        return new WmsTileProvider(256, 256) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                URL url;
                try {
                    double[] boundingBox = getBoundingBox(x, y, zoom);
                    String s = String.format(Locale.US, WMS_URL, boundingBox[MINX], boundingBox[MINY], boundingBox[MAXX], boundingBox[MAXY]);
                    url = new URL(s);
                } catch (Exception e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
    }
}