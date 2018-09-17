package com.redhelmet.alert2me.domain.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.redhelmet.alert2me.core.Constants;

public class Utility {


    public static boolean isInternetConnected(Context context) {
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        else
            return false;

    }
    public static boolean isLocationEnabled(Context context) {
        if(context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        else
            return false;
    }
    public static boolean isProximityEnabled(Context context) {
        boolean enableProxi = false;
        if(context != null) {
          enableProxi = (boolean) PreferenceUtils.getFromPrefs(context, Constants.KEY_VALUE_ENABLEPROXI, false);
        }

        return enableProxi;

    }
}
