package com.redhelmet.alert2me.ui.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;

import net.grandcentrix.tray.AppPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.redhelmet.alert2me.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognizedService extends IntentService {
    protected static final String TAG = ActivityRecognizedService.class.getSimpleName();
    private Context _context;
    private String apiUrl;
    private String userId;
private static int startProcessId;
    public static volatile boolean shouldContinue = true;
  //  private static ActivityRecognizedService sharedInstance;


    public ActivityRecognizedService() {
// Use the TAG to name the worker thread.
        super(TAG);
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

//    // Providing Global point of access
//    public static ActivityRecognizedService getSharedInstance() {
//        if (null == sharedInstance) {
//            sharedInstance = new ActivityRecognizedService();
//        }
//        return sharedInstance;
//    }

//    public void stopActivityRecognizedService(){
//        stopServices();
//        Log.d("StopedService","SErvice stoped in the background");
//        stopSelf(startProcessId);
//    }

    public  void  startActivityRecognizedService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        if (_context != null) {

            apiUrl = BuildConfig.API_ENDPOINT;
            userId = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), "");
        }

        Log.e("StartedService","SErvice started in the background");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onHandleIntent(Intent intent) {


                    // check the condition
            if (shouldContinue == false) {
                Log.e("shouldContinue","shouldContinue false clicked");
                stopServices();
                return;
            }
        try {

            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivities(result.getProbableActivities());
            }
        } catch (Throwable t) {
            Log.e("Handle", t.getMessage());
        }

    }


    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {



//        for( DetectedActivity activity : probableActivities ) {
//            switch( activity.getType() ) {
//                case DetectedActivity.IN_VEHICLE: {
//                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.ON_BICYCLE: {
//                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.ON_FOOT: {
//                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.RUNNING: {
//                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.STILL: {
//                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.TILTING: {
//                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
//                    break;
//                }
//                case DetectedActivity.WALKING: {
//                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
//                    if( activity.getConfidence() >= 75 ) {
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText( "Are you walking?" );
//                        builder.setSmallIcon( R.mipmap.ic_launcher );
//                        builder.setContentTitle( getString( R.string.app_name ) );
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                    }
//                    break;
//                }
//                case DetectedActivity.UNKNOWN: {
//                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
//                    break;
//                }
//            }
//        }





        for( DetectedActivity activity : probableActivities ) {


            if (activity.getConfidence() > 80) {

                switch( activity.getType() ) {

                    case DetectedActivity.WALKING: case DetectedActivity.RUNNING: {
                        Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );

                        createTimer(activity.getType(), Constants.SlOWMOTIONTIMER);
                        break;
                    }
                    case DetectedActivity.IN_VEHICLE: case DetectedActivity.ON_BICYCLE: {
                        Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );

                        createTimer(activity.getType(),Constants.AUTOMOTIVETIMER);
                        break;
                    }

                    case DetectedActivity.STILL: {
                        Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );

                        createTimer(activity.getType(),Constants.NOTMOVINGVETIMER);
                        break;
                    }
                    default:
                        Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );

//                case DetectedActivity.UNKNOWN: {
//                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
//
//                    break;
//                }
                }
            }

        }






//
//        final AppPreferences appPreferences = new AppPreferences(_context);
//
////        final AppPreferences appPreferences = new AppPreferences(_context);
//        for (DetectedActivity activity : probableActivities) {
//
//            Log.e( "ActivityRecogition", "motion: " + activity.getType() + " "+ activity.getConfidence() );
//            if (activity.getConfidence() > 80) {
//                int movement = appPreferences.getInt(Constants.PROXIMITY_MOVEMENT, -1);
//                int activityType = activity.getType();
//
//                if (activityType == DetectedActivity.WALKING ||
//                        activityType == DetectedActivity.RUNNING) {
//                    appPreferences.put(Constants.PROXIMITY_MOVEMENT, activityType);
//                    if (movement != DetectedActivity.WALKING && movement != DetectedActivity.RUNNING) {
//                        int timeMilliSecs = (int) (0.5 * 60000); //15 mins
//                        restartService(timeMilliSecs);
//                    }
//                }
//                if (activityType == DetectedActivity.IN_VEHICLE ||
//                        activityType == DetectedActivity.ON_BICYCLE) {
//                    appPreferences.put(Constants.PROXIMITY_MOVEMENT, activityType);
//                    if (movement != DetectedActivity.IN_VEHICLE && movement != DetectedActivity.ON_BICYCLE) {
//                        int timeMilliSecs = (int) (0.5 * 60000); //6 mins
//                        restartService(timeMilliSecs);
//                    }
//
//                } else if (activityType == DetectedActivity.STILL && activity.getConfidence() > 99) {
//
//                    if (movement == -1) {
//                        appPreferences.put(Constants.PROXIMITY_MOVEMENT, DetectedActivity.STILL);
//                    } else if (movement != DetectedActivity.STILL) {
//                        int stillCount = appPreferences.getInt(Constants.PROXIMITY_STILL, -1);
//                        if (stillCount != -1) {
//                            stillCount++;
//                            appPreferences.put(Constants.PROXIMITY_STILL, stillCount);
//                            if (stillCount > 10) {
//                                appPreferences.put(Constants.PROXIMITY_MOVEMENT, DetectedActivity.STILL);
//                                appPreferences.put(Constants.PROXIMITY_STILL, 0);
//                                int timeMilliSecs = (int) (0.5 * 60000); //30 mins
//                                restartService(timeMilliSecs);
//                            }
//                        } else {
//                            appPreferences.put(Constants.PROXIMITY_STILL, 0);
//                            int timeMilliSecs = (int) (0.5 * 60000); //30 mins
//                            restartService(timeMilliSecs);
//                        }
//
//                    }
//                    else {
//                        appPreferences.put(Constants.PROXIMITY_STILL, 0);
//                        int timeMilliSecs = (1 * 60000); //30 mins
//                        restartService(timeMilliSecs);
//
//                    }
//                }
//            }
//        }

    }

    private void createTimer (int newMovement, int timeMilliSeconds) {

        final AppPreferences appPreferences = new AppPreferences(_context);
        int previousMovement = appPreferences.getInt(Constants.PROXIMITY_MOVEMENT, -1);

        if(previousMovement != newMovement) {
            appPreferences.put(Constants.PROXIMITY_MOVEMENT, newMovement);
            restartService(timeMilliSeconds);
        }
        else {

            if(!isServiceRunning(ProximityLocationManager.class))
            {
                try {
                    boolean mobileServiceEnabled = Utility.isLocationEnabled(_context);
                    if (mobileServiceEnabled && !isServiceRunning(ProximityLocationManager.class) && PreferenceUtils.class != null) {
                        Intent locationIntent = new Intent(this, ProximityLocationManager.class);
                        locationIntent.putExtra("timeMilliSecs", timeMilliSeconds);
                        startService(locationIntent);
                        // create a handler to post messages to the main thread
                        Handler mHandler = new Handler(getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Successfully requested location service",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

                    }
                } catch (Throwable t) {
                    Log.d("Exception:",t.getMessage());
                }
            }
            else {
                Log.d("Elsedsfsfdsdf:","Service running!!!!!");
            }

        }


    }

    private void restartService(int timeMilliSeconds) {
        stopServices();
        startService(timeMilliSeconds);
    }

    private void testNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(message);
        builder.setColor(Color.argb(0, 0, 63, 94));
        builder.setSmallIcon(R.drawable.notification_bar_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_bar_icon));
        Random random = new Random();
        NotificationManagerCompat.from(this).notify(random.nextInt(), builder.build());
    }

    private void startService(int time) {
        try {
            boolean mobileServiceEnabled = Utility.isLocationEnabled(_context);
            if (mobileServiceEnabled && !isServiceRunning(ProximityLocationManager.class) && PreferenceUtils.class != null) {
                Intent locationIntent = new Intent(this, ProximityLocationManager.class);
                locationIntent.putExtra("timeMilliSecs", time);
                locationIntent.putExtra("distance",getProximityDistance());
                startService(locationIntent);
            }
        } catch (Throwable t) {
           Log.d("Exception:",t.getMessage());
        }
    }
    private void stopServices() {
        try {
            if (isServiceRunning(ProximityLocationManager.class)) {
                stopService(new Intent(this, ProximityLocationManager.class));
            }
        } catch (Throwable t) {
            Log.d("Exception:",t.getMessage());
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    private int getProximityDistance(){

        int distance = Constants.DEFAULT_VALUE_RADIUS;

        ArrayList arryMobileWZ = new ArrayList<EditWatchZones>() ;
        if(PreferenceUtils.hasKey(_context, Constants.KEY_VALUE_PROXIMITY_DATA)) {

            Gson gson = new Gson();

            String mobileWZValues = (String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_VALUE_PROXIMITY_DATA,"");

            EditWatchZones[] arryMobileWZmodel =  gson.fromJson(mobileWZValues,EditWatchZones[].class);

            List<EditWatchZones> items  = Arrays.asList(arryMobileWZmodel);
            arryMobileWZ =   new ArrayList<EditWatchZones>(items);

        }

        if (arryMobileWZ != null) {
            EditWatchZones dictMobileWZ = null;
            if (arryMobileWZ.size() > 0) {
                dictMobileWZ = (EditWatchZones) arryMobileWZ.get(0);
            }
            Log.d("dsfsdf", "dfsdfsdf" + arryMobileWZ.toString());

            if(dictMobileWZ.getRadius() != null){
                distance = Integer.valueOf(dictMobileWZ.getRadius()) ;
            }
        }

        return distance;
    }

    public  void stopAcitvityRecognizedService() {

//        stopServices();
//        stopSelf();
    }

}
