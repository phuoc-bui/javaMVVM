package com.redhelmet.alert2me.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.PnsNotification;
import com.redhelmet.alert2me.ui.splash.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private Gson gson;
    private final static AtomicInteger c = new AtomicInteger(0);

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

//        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendPayloadNotification(remoteMessage.getData());
        }

//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendPayloadNotification(remoteMessage.getNotification().getBody());
//
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendPayloadNotification(Map<String, String> data) {
        if (data != null) {
            String notification = data.get("notification");
            String taskData = data.get("task");
Boolean responseEnabled = false;
            if(data.get("responseEnabled") != null)
            {
                responseEnabled = Boolean.valueOf(data.get("responseEnabled"));
            }
            if (notification != null && !notification.equals("")) {

                Intent intent = new Intent(this.getApplicationContext(), SplashScreen.class);
               intent.setAction("update-message");
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                String eventData = data.get("data");

                if (eventData != null && !eventData.equals("")) {
                    if (eventData.contains("testNotification")) {
                        sendTestNotification(getApplicationContext(), intent, notification);

                    }else{
                    Event event = gson.fromJson(eventData, Event.class);
                    if (event.getId() > 0) {
                     //   sendLocalNotifications(notification, event, intent);
                    }
                    }
                }
            }
        }
    }

    private void sendLocalNotification(String notification, String taskData,Boolean responseEnabled,Intent intent) throws JSONException {
        String callbackURL =  BuildConfig.API_ENDPOINT;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setColor(Color.argb(0, 2, 145, 161));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent.setAction(Long.toString(System.currentTimeMillis()));
        JSONObject data = null,taskDetail = null;

        try {
             data = new JSONObject(notification);
            taskDetail = new JSONObject(taskData);
            builder.setContentTitle(data.getString("title"));
            builder.setContentText(data.getString("body"));

            if (data.getString("sound") != null) {

                Uri tempSound = Uri.parse(data.getString("sound"));
                try {
                    InputStream inputStream = getContentResolver().openInputStream(tempSound);
                    if (inputStream != null && inputStream.read() > 0) {
                        sound = tempSound;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            builder.setSound(sound);
            if(taskDetail.getInt("id") > 0)
            {
                callbackURL = BuildConfig.API_ENDPOINT+"notification/"+taskDetail.getInt("id")+"/"+responseEnabled;
                Log.e("10222333",callbackURL);
                intent.putExtra("callbackURL",callbackURL);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManagerCompat.from(this).notify(taskDetail.getInt("id"), builder.build());
            }
            else {
                intent.putExtra("callbackURL",callbackURL);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                NotificationManagerCompat.from(this).notify(0, builder.build());
            }


        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void sendTestNotification(Context context, Intent intent, String notification) {

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
                PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
                wl_cpu.acquire(10000);
            }
        }

        Gson gson = new Gson();
        PnsNotification pnsNotification = gson.fromJson(notification, PnsNotification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (pnsNotification != null && pnsNotification.getTitle() != null && pnsNotification.getBody() != null) {
            builder.setContentTitle(pnsNotification.getTitle());
            builder.setContentText(pnsNotification.getBody());
        } else {
            builder.setContentTitle("Test Notification");
            builder.setContentText("This is a test notification");
        }

// This is the answer to OP's question, set the visibility of notification to public.
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCategory(Notification.CATEGORY_EVENT);
        builder.setColor(Color.argb(0, 0, 63, 94));
        builder.setSmallIcon(R.drawable.notification_bar_icon);
        builder.setAutoCancel(true);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notification_bar_icon);
        builder.setLargeIcon(icon);



        builder.setContentIntent(pendingIntent);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(sound);
        builder.setLights(Color.RED, 2000, 3000);
        builder.setColor(Color.BLUE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
       // NotificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(c.incrementAndGet(), builder.build());
    }
    }