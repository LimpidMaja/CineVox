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
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.limpidgreen.cinevox.EventActivity;
import com.limpidgreen.cinevox.FriendsActivity;
import com.limpidgreen.cinevox.MoviesKnockoutActivity;
import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.RateMoviesActivity;
import com.limpidgreen.cinevox.WinnerActivity;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;

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
    public static final String KEY_NEW_EVENT_REQUEST =
            "com.limpidgreen.cinevox.KEY_NEW_EVENT";
    public static final String KEY_EVENT_FRIEND_CONFIRM =
            "com.limpidgreen.cinevox.KEY_EVENT_FRIEND_CONFIRM";
    public static final String KEY_EVENT_VOTING =
            "com.limpidgreen.cinevox.KEY_EVENT_VOTING";
    public static final String KEY_EVENT_WINNER =
            "com.limpidgreen.cinevox.KEY_EVENT_WINNER";
    public static final String KEY_EVENT_KNOCKOUT =
            "com.limpidgreen.cinevox.KEY_EVENT_KNOCKOUT";
    public static final String KEY_FRIEND_REQUEST =
            "com.limpidgreen.cinevox.KEY_FRIEND_REQUEST";
    public static final String KEY_FRIEND_REQUEST_ACCEPTED =
            "com.limpidgreen.cinevox.KEY_FRIEND_REQUEST_ACCEPTED";
    public static final String KEY_EVENT_CANCELLED =
            "com.limpidgreen.cinevox.KEY_EVENT_CANCELED";

    // Sets an ID for the notification, so it can be updated
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        AccountManager mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        if (accounts != null && accounts.length == 1) {
            Account account = accounts[0];

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                        .equals(messageType)) {
                    createNotification("Error", "Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                        .equals(messageType)) {
                    createNotification("Error", "Deleted messages on server: "
                            + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                        .equals(messageType)) {
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        Log.d(Constants.TAG, "BUNDLE:");
                        Log.d(Constants.TAG, String.format("%s %s (%s)", key,
                                value.toString(), value.getClass().getName()));
                    }

                    if (intent.getStringExtra(KEY_NEW_EVENT_REQUEST) != null && Boolean.valueOf(intent.getStringExtra(KEY_NEW_EVENT_REQUEST))) {
                        String eventJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "NEW EVENT:" + intent.getStringExtra("body"));

                        if (eventJson != null && !eventJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(eventJson);

                            GsonBuilder jsonBuilder = new GsonBuilder();
                            jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                            Gson gson = jsonBuilder.create();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("event").toString());
                            Event event = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("event"),
                                    Event.class
                            );

                            Log.d(Constants.TAG, "EVENT:" + event.toString());

                            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, false);
                            if (result) {
                                sendNewEventNotification(event);
                            }
                        }
                    } else if (intent.getStringExtra(KEY_EVENT_VOTING) != null && Boolean.valueOf(intent.getStringExtra(KEY_EVENT_VOTING))) {
                        String eventJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "EVENT VOTING:" + intent.getStringExtra("body"));

                        if (eventJson != null && !eventJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(eventJson);

                            GsonBuilder jsonBuilder = new GsonBuilder();
                            jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                            Gson gson = jsonBuilder.create();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("event").toString());
                            Event event = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("event"),
                                    Event.class
                            );

                            Log.d(Constants.TAG, "EVENT:" + event.toString());

                            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, true);
                            if (result) {
                                sendEventVotingStartedNotification(event);
                            }
                        }
                    } else if (intent.getStringExtra(KEY_EVENT_KNOCKOUT) != null && Boolean.valueOf(intent.getStringExtra(KEY_EVENT_KNOCKOUT))) {
                        String eventJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "EVENT KNOCKOUT:" + intent.getStringExtra("body"));

                        if (eventJson != null && !eventJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(eventJson);

                            GsonBuilder jsonBuilder = new GsonBuilder();
                            jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                            Gson gson = jsonBuilder.create();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("event").toString());
                            Event event = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("event"),
                                    Event.class
                            );

                            Log.d(Constants.TAG, "EVENT:" + event.toString());

                            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, true);
                            if (result) {
                                sendEventKnockoutNotification(event);
                            }
                        }
                    } else if (intent.getStringExtra(KEY_EVENT_WINNER) != null && Boolean.valueOf(intent.getStringExtra(KEY_EVENT_WINNER))) {
                        String eventJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "EVENT WINNER:" + intent.getStringExtra("body"));

                        if (eventJson != null && !eventJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(eventJson);

                            GsonBuilder jsonBuilder = new GsonBuilder();
                            jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                            Gson gson = jsonBuilder.create();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("event").toString());
                            Event event = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("event"),
                                    Event.class
                            );

                            Log.d(Constants.TAG, "EVENT:" + event.toString());

                            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, true);
                            if (result) {
                                sendEventWinnerNotification(event);
                            }
                        }
                    } else if (intent.getStringExtra(KEY_EVENT_CANCELLED) != null && Boolean.valueOf(intent.getStringExtra(KEY_EVENT_CANCELLED))) {
                        String eventJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "EVENT CANCELLED:" + intent.getStringExtra("body"));

                        if (eventJson != null && !eventJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(eventJson);

                            GsonBuilder jsonBuilder = new GsonBuilder();
                            jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                            Gson gson = jsonBuilder.create();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("event").toString());
                            Event event = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("event"),
                                    Event.class
                            );

                            Log.d(Constants.TAG, "EVENT:" + event.toString());

                            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, true);
                            if (result) {
                                sendEventCancelledNotification(event);
                            }
                        }
                    } else if (intent.getStringExtra(KEY_EVENT_FRIEND_CONFIRM) != null && Boolean.valueOf(intent.getStringExtra(KEY_EVENT_FRIEND_CONFIRM))) {
                        sendEventConfirmNotification(intent.getStringExtra("event_name"), intent.getStringExtra("title"), Integer.valueOf(intent.getStringExtra("event_id")));
                        ContentResolver.requestSync(account, EventsContentProvider.AUTHORITY, new Bundle());
                    } else if (intent.getStringExtra(KEY_FRIEND_REQUEST) != null && Boolean.valueOf(intent.getStringExtra(KEY_FRIEND_REQUEST))) {
                        String friendJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "KEY_FRIEND_REQUEST:" + intent.getStringExtra("body"));

                        if (friendJson != null && !friendJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(friendJson);

                            Gson gson = new Gson();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("friend").toString());
                            Friend friend = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("friend"),
                                    Friend.class
                            );

                            Log.d(Constants.TAG, "Friend:" + friend.toString());

                            Friend localFriend = null;
                            Cursor curFriend = getContentResolver().query(ContentUris.withAppendedId(EventsContentProvider.FRIENDS_CONTENT_URI, friend.getId()), null, null, null, null);
                            if (curFriend != null) {
                                while (curFriend.moveToNext()) {
                                    localFriend = Friend.fromCursor(curFriend);
                                }
                                curFriend.close();
                            } // end if

                            if (localFriend != null) {
                                getContentResolver().update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
                            } else {
                                getContentResolver().insert(FriendsContentProvider.CONTENT_URI, friend.getContentValues());
                            }
                            sendFriendRequestNotification(friend);
                        }
                    } else if (intent.getStringExtra(KEY_FRIEND_REQUEST_ACCEPTED) != null && Boolean.valueOf(intent.getStringExtra(KEY_FRIEND_REQUEST_ACCEPTED))) {
                        String friendJson = intent.getStringExtra("body");
                        Log.d(Constants.TAG, "KEY_FRIEND_REQUEST:" + intent.getStringExtra("body"));

                        if (friendJson != null && !friendJson.isEmpty()) {

                            JsonParser parser = new JsonParser();
                            JsonElement jsonElement = parser.parse(friendJson);

                            Gson gson = new Gson();

                            Log.i(Constants.TAG, "RETURNED : jsonObject:" + jsonElement.getAsJsonObject().get("friend").toString());
                            Friend friend = gson.fromJson(
                                    jsonElement.getAsJsonObject().get("friend"),
                                    Friend.class
                            );

                            Log.d(Constants.TAG, "Friend:" + friend.toString());

                            Friend localFriend = null;
                            Cursor curFriend = getContentResolver().query(ContentUris.withAppendedId(EventsContentProvider.FRIENDS_CONTENT_URI, friend.getId()), null, null, null, null);
                            if (curFriend != null) {
                                while (curFriend.moveToNext()) {
                                    localFriend = Friend.fromCursor(curFriend);
                                }
                                curFriend.close();
                            } // end if

                            if (localFriend != null) {
                                getContentResolver().update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
                            } else {
                                getContentResolver().insert(FriendsContentProvider.CONTENT_URI, friend.getContentValues());
                            }
                            sendFriendRequestAcceptedNotification(friend);
                        }
                    } else if (intent.getBooleanExtra(KEY_SYNC_REQUEST, false)) {
                        ContentResolver.requestSync(account, EventsContentProvider.AUTHORITY, new Bundle());
                    } else {
                        createNotification("Message", "Message Received from Google GCM Server:\n\n"
                                + extras.get(KEY_MSG_REQUEST));
                        // ContentResolver.requestSync(account, EventsContentProvider.AUTHORITY, new Bundle());
                    }
                }
            }
            GCMBroadcastReceiver.completeWakefulIntent(intent);
        } else {
            //NO ACCOUNT TO HANDLE MSG
            Log.d(Constants.TAG, "NO ACCOUNT TO HANDLE MSG!");
        }
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

    private void sendEventConfirmNotification(String title, String body, Integer eventId) {
        Intent resultIntent = new Intent(this, EventActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, eventId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
                .setContentText(body);

        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(eventId, mNotifyBuilder.build());
    }

    private void sendNewEventNotification(Event event) {
        Intent resultIntent = new Intent(this, EventActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Intent joinIntent = new Intent(this, ConfirmEventService.class);
        joinIntent.setAction(Constants.ACTION_JOIN_EVENT);
        joinIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        joinIntent.putExtra(Constants.PARAM_ACCEPT, true);
        joinIntent.putExtra(Constants.PARAM_NOTIFICATION_ID, event.getId());
        PendingIntent piJoin = PendingIntent.getService(this, 0, joinIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent declineIntent = new Intent(this, ConfirmEventService.class);
        declineIntent.setAction(Constants.ACTION_DECLINE_EVENT);
        declineIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        declineIntent.putExtra(Constants.PARAM_ACCEPT, false);
        declineIntent.putExtra(Constants.PARAM_NOTIFICATION_ID, event.getId());
        PendingIntent piDecline = PendingIntent.getService(this, 0, declineIntent, PendingIntent.FLAG_ONE_SHOT);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Event: " + event.getName())
                .setContentText("New invitation to a Movie Night!")
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your friend sent you an invitation to a Movie Night!"))
                .addAction(android.R.drawable.ic_menu_add,
                        "Join", piJoin)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        "Decline", piDecline);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(event.getId(), mNotifyBuilder.build());
    }

    private void sendEventVotingStartedNotification(Event event) {
        Intent resultIntent = new Intent(this, RateMoviesActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(event.getName() + " - Voting Started!")
                .setContentText("Start voting now!")
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(event.getId(), mNotifyBuilder.build());
    }

    private void sendEventKnockoutNotification(Event event) {
        Intent resultIntent = new Intent(this, MoviesKnockoutActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(event.getName() + " - Knockout!")
                .setContentText("Choose a movie!")
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(event.getId(), mNotifyBuilder.build());
    }

    private void sendEventWinnerNotification(Event event) {
        Intent resultIntent = new Intent(this, WinnerActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(event.getName() + " - We have a Winner!")
                .setContentText("The winner is: " + event.getWinner().getTitle())
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(event.getId(), mNotifyBuilder.build());
    }

    private void sendEventCancelledNotification(Event event) {
        Intent resultIntent = new Intent(this, EventActivity.class);
        resultIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(event.getName() + " - was cancelled!")
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(event.getId(), mNotifyBuilder.build());
    }

    private void sendFriendRequestNotification(Friend friend) {
        Intent resultIntent = new Intent(this, FriendsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(friend.getName() + " sent you a Friend Request!")
                .setContentText("Will you accept?")
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(friend.getId(), mNotifyBuilder.build());
    }

    private void sendFriendRequestAcceptedNotification(Friend friend) {
        Intent resultIntent = new Intent(this, FriendsActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(friend.getName() + " accepted your Friend Request!")
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
        //mNotifyBuilder.setContentText("New message from Server");
        // Set auto cancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
        mNotificationManager.notify(friend.getId(), mNotifyBuilder.build());
    }
}
