/**
 * GCMNotificationIntentService.java
 *
 * 14.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.limpidgreen.cinevox.MainActivity;
import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.dao.EventsContentProvider;

/**
 * GCMNotificationIntentService for incoming changes calls from the server.
 *
 * @author MajaDobnik
 *
 */
public class GCMNotificationIntentService extends IntentService {
    // Incoming Intent key for extended data
    public static final String KEY_SYNC_REQUEST =
            "com.limpidgreen.cinevox.KEY_SYNC_REQUEST";
    // Incoming Intent key for extended data
    public static final String KEY_MSG_REQUEST =
            "com.limpidgreen.cinevox.KEY_MSG_REQUEST";

    // Sets an ID for the notification, so it can be updated
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                if (intent.getBooleanExtra(KEY_SYNC_REQUEST, false)) {

                    AccountManager mAccountManager = AccountManager.get(this);
                    Account account = mAccountManager
                            .getAccountsByType(Constants.ACCOUNT_TYPE)[0];
                    ContentResolver.requestSync(account, EventsContentProvider.AUTHORITY, new Bundle());
                } else {
                    sendNotification("Message Received from Google GCM Server:\n\n"
                            + extras.get(KEY_MSG_REQUEST));
                    AccountManager mAccountManager = AccountManager.get(this);
                    Account account = mAccountManager
                            .getAccountsByType(Constants.ACCOUNT_TYPE)[0];
                    ContentResolver.requestSync(account, EventsContentProvider.AUTHORITY, new Bundle());
                }
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotification(String msg) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("msg", msg);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Alert")
                .setContentText("You've received new message.")
                .setSmallIcon(R.drawable.ic_launcher);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mNotifyBuilder.build());
    }
}
