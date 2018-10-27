package com.redhelmet.alert2me.ui.services;

import android.app.ActivityManager;
import android.app.PendingIntent;
        import android.app.Service;
import android.content.Context;
import android.content.Intent;
        import android.os.Binder;
        import android.os.IBinder;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import android.util.Log;
        import android.widget.Toast;

import com.redhelmet.alert2me.core.Constants;
import com.google.android.gms.location.ActivityRecognitionClient;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;

public class BackgroundDetectedActivitiesService extends Service {
    private static final String TAG = BackgroundDetectedActivitiesService.class.getSimpleName();

    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;


    private static BackgroundDetectedActivitiesService sharedInstance;


    IBinder mBinder = new BackgroundDetectedActivitiesService.LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundDetectedActivitiesService getServerInstance() {
            return BackgroundDetectedActivitiesService.this;
        }
    }

    public BackgroundDetectedActivitiesService() {

    }

    // Providing Global point of access
    public static BackgroundDetectedActivitiesService getSharedInstance() {
        if (sharedInstance == null ) {
            sharedInstance = new BackgroundDetectedActivitiesService();
        }
        return sharedInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, ActivityRecognizedService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler() {

        stopServices();
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {

                Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();

        Log.e("onDestroy", "BackgroundDetectedActivitiesService servicess.........");
    }
}
