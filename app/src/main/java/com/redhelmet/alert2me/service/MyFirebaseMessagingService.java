package com.redhelmet.alert2me.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.ui.splash.SplashScreen;

import java.util.Map;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static int NOTIFICATION_ID = 123;
    @Inject
    public Gson gson;

    private static final String TAG = "MyFirebaseMsgService";

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
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> data = null;
        RemoteMessage.Notification notification = null;

//        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
        }

//        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notification = remoteMessage.getNotification();
        }

        sendPayloadNotification(data, notification);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendPayloadNotification(Map<String, String> data, RemoteMessage.Notification notification) {

        String title = null;
        String body = null;
        Event event = null;

        if (data != null) {
            title = data.get("title");
            body = data.get("body");
            String eventJson = data.get("event");
            event = gson.fromJson(eventJson, Event.class);
        }

        if ((title == null || body == null) && notification != null) {
            title = notification.getTitle();
            body = notification.getBody();
        }

        sendLocalNotification(title, body, event);
    }

    private void sendLocalNotification(String title, String body, Event event) {

        if (title == null) title = "Title";
        if (body == null) body = "Body";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        builder.setColor(Color.argb(0, 2, 145, 161));
        builder.setSmallIcon(R.drawable.notification_bar_icon);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(body);

        // Create an Intent for the activity you want to start
        Intent resultIntent;
        if (event != null) {
            resultIntent = HomeActivity.newInstance(this, event);
        } else {
            resultIntent = new Intent(this, SplashScreen.class);
        }
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}