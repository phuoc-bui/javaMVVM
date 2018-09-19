package com.redhelmet.alert2me.ui.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.google.gson.Gson;

import net.grandcentrix.tray.AppPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.redhelmet.alert2me.R;

public class ProximityLocationManager extends Service implements LocationListener {

    private static final String TAG = "PROXIMITYLOCATION";
    private LocationManager mLocationManager = null;
    Location location;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;

    //    private static final int LOCATION_INTERVAL = 1000;
//    private static final float LOCATION_DISTANCE = 0f;
    private Context _context;

    private String apiUrl;
    private String deviceId;
    float accuracy = 0;
    String statusOfMotion = "Not Moving";
    String speed = "0 km/hr";
    private int distance = Constants.DEFAULT_VALUE_RADIUS;
    private int timeMilliSecs;
    private Context context;
    private PreferenceUtils preferenceUtils;
    private Timer timer = null;
    private Handler handler = new Handler();

    private static final int REQUEST_COARSE_LOCATION = 8;

    public ProximityLocationManager() {


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




    private void fn_getlocation(boolean instantProximityUpdate) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }

                mLocationManager. requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (mLocationManager!=null){
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");

                        PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLATITUDE,String.valueOf(location.getLatitude()));
                        PreferenceUtils.saveToPrefs(_context,Constants.KEY_USERLONGITUDE,String.valueOf(location.getLongitude()));



                        if(instantProximityUpdate) {
                            speed = location.getSpeed() + " km/hr" ;
                            accuracy = location.getAccuracy();
                            proximityLocationCheckin();
                        }
                        else {
                            onNewLocationAvailable(location);
                        }
                    }
                }

            }
else  if (isGPSEnable){
                location = null;
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (mLocationManager!=null){
                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");

                        if(instantProximityUpdate) {
                            speed = location.getSpeed() + " km/hr" ;
                            accuracy = location.getAccuracy();
                            proximityLocationCheckin();
                        }
                        else {
                            onNewLocationAvailable(location);
                        }
                    }
                }
            }


        }

    }


    private void onNewLocationAvailable(Location location) {

        if (location != null && context != null) {


            final AppPreferences appPreferences = new AppPreferences(context);

            Double latitude = Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_LASTUPDATEDUSERLATITUDE, "0"));
            Double longitude = Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_LASTUPDATEDUSERLONGITUDE, "0"));

            Location storedLocation = new Location("");
            storedLocation.setLongitude(latitude);
            storedLocation.setLatitude(longitude);


//            Log.e(TAG, "date:" + currentDate);
            Log.e(TAG, "New Location lat: " + location.getLatitude() + "lon" + location.getLongitude());
            Log.e(TAG, "Old Location lat: " + storedLocation.getLatitude() + "lon" + storedLocation.getLongitude() + "--");

            float locationDistance = storedLocation.distanceTo(location);

            Log.e(TAG, "dist 1: " + distance);
            Log.e(TAG, "dist 2: " + locationDistance);

//            if((int) locationDistance >= (int) ((1000 * distance) / 2))
//            {

                int movement = appPreferences.getInt(Constants.PROXIMITY_MOVEMENT, -1);
                Log.e(TAG, "Movement:" + movement);
                switch (movement){
                    case 0:
                        statusOfMotion = "On vehicle";
                        break;
                    case 1:
                        statusOfMotion = "On bicycle";
                        break;
                    case 7:
                        statusOfMotion = "Walking";
                        break;
                    case 8:
                        statusOfMotion = "Running";
                        break;
                    default:
                        statusOfMotion = "Not moving";
                        break;
                }

                speed = location.getSpeed() + " km/hr" ;
                accuracy = location.getAccuracy();
                proximityLocationCheckin();
            //}
        }
    }


//    private void getLocation(Context context) {
//        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        if (isNetworkEnabled) {
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//
//            mLocationManager.requestSingleUpdate(criteria, new android.location.LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//                    onNewLocationAvailable(location);
//                }
//
//                @Override
//                public void onStatusChanged(String s, int i, Bundle bundle) {
//                }
//
//                @Override
//                public void onProviderEnabled(String s) {
//                }
//
//                @Override
//                public void onProviderDisabled(String s) {
//                }
//            }, null);
//        } else {
//            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            if (isGPSEnabled) {
//                Criteria criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                mLocationManager.requestSingleUpdate(criteria, new android.location.LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        onNewLocationAvailable(location);
//                    }
//
//                    @Override
//                    public void onStatusChanged(String s, int i, Bundle bundle) {
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String s) {
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String s) {
//                    }
//                }, null);
//            }
//        }
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        if(intent != null ){
            Bundle extras = intent.getExtras();
            distance =    extras.getInt("distance");
            timeMilliSecs = extras.getInt("timeMilliSecs");
            Log.e(TAG, "device" + deviceId + "--" + apiUrl + "--" + distance + "--" + timeMilliSecs);

            if (timer != null) {
                timer.cancel();
            } else {

                //To update users' location
                fn_getlocation(true);

                timer = new Timer();
            }
            timer.scheduleAtFixedRate(new TimeElapsedTask(), 0, timeMilliSecs);
        }


        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");
        context = getApplicationContext();
        initializeLocationManager();

//
//        if (context != null) {
//
//        }

/*        long UPDATE_INTERVAL = 20000;
        long FASTEST_INTERVAL = 10000;

        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COARSE_LOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this); */

//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]
//
////                    LocationManager.PASSIVE_PROVIDER,
////                    LOCATION_INTERVAL,
////                    LOCATION_DISTANCE,
////                    mLocationListeners[0]
//            );
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
//
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    LOCATION_INTERVAL,
//                    LOCATION_DISTANCE,
//                    mLocationListeners[1]
//            );
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        timer.cancel();
        mLocationManager.removeUpdates(this);
        Log.e("onDestroy", "ProximityLocationManager onDestroy location");
    }

    private void initializeLocationManager() {
      //  Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }






    public void proximityLocationCheckin()
    {

        final Double latitude  =  Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, "0"));
        final Double longitude  =  Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, "0"));

        String apiURL = BuildConfig.API_ENDPOINT + "device/" + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity/location";
        Log.d("location", PreferenceUtils. getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " ").toString());

        HashMap<String, Object> mParams = new HashMap < String, Object> ();

        Gson gson = new Gson();
        mParams.put("accuracy",accuracy);
        mParams.put("speed",speed);
        mParams.put("movement",statusOfMotion);
        mParams.put("latitude", Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " ")));
        mParams.put("longitude",  Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, " ")));
        Log.d("proximityDictlocation", mParams.toString());


        // create a handler to post messages to the main thread
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Proximity update created successfully",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });


       /* RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, apiURL, new JSONObject(mParams),
                new Response.Listener < JSONObject > () {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e("EA", response.toString());

                                    PreferenceUtils.saveToPrefs(_context, Constants.KEY_LASTUPDATEDUSERLATITUDE,String.valueOf(latitude));
                                    PreferenceUtils.saveToPrefs(_context,Constants.KEY_LASTUPDATEDUSERLONGITUDE,String.valueOf(longitude));


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), getString(R.string.timeOut), Toast.LENGTH_LONG).show();
            }
        }) {
            /**
             * Passing some request headers
             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_token), ""));
//                return headers;
//            }
//
//        };
//
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(jsonObjReq);

//*/
    }


    class TimeElapsedTask extends TimerTask {

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
                    String currentDate = sdf.format(new Date());
                    Log.e(TAG, "Runnable :" + currentDate);
                    fn_getlocation(false);
                }
            });
        }
    }
}
