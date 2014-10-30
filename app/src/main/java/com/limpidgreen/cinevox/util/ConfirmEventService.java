package com.limpidgreen.cinevox.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.limpidgreen.cinevox.CineVoxApplication;
import com.limpidgreen.cinevox.EventActivity;
import com.limpidgreen.cinevox.MainActivity;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Event;

import java.io.IOException;

/**
 * Created by Maja on 30/10/14.
 */
public class ConfirmEventService extends IntentService {
    /** Application */
    private CineVoxApplication mApplication;
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    public ConfirmEventService() {
        super("ConfirmEventService");
    }

    @Override
    public void onCreate() {
        mApplication = ((CineVoxApplication) getApplication());
        Log.i(Constants.TAG, "SERVICE CREATE");
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(Constants.TAG, "SERVICE  onStartCommand");
        Bundle bundle = intent.getExtras();

        Integer notificationId = bundle.getInt(Constants.PARAM_NOTIFICATION_ID);
        if (notificationId != null) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
        } // end if

        if (mApplication.getAPIToken() == null) {
            mApplication.startAuthTokenFetch();
        }

        Integer eventId = bundle.getInt(Constants.PARAM_EVENT_ID);
        Boolean accept = bundle.getBoolean(Constants.PARAM_ACCEPT);
        Log.i(Constants.TAG, "SERVICE  token" + mApplication.getAPIToken());

        Event event = NetworkUtil.confirmEventJoin(mApplication.getAPIToken(), eventId, accept);
        if (event != null) {
            Log.i(Constants.TAG, "EVENT: " + event);
            boolean result = EventsContentProvider.insertEvent(getContentResolver(), event, true);
            if (result) {
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                mApplication.getApplicationContext().sendBroadcast(it);

                Intent eventIntent = new Intent(mApplication.getApplicationContext(), EventActivity.class);
                eventIntent.putExtra(Constants.PARAM_EVENT_ID, event.getId());
                eventIntent.putExtra(Constants.PARAM_EVENT_CONFIRM_STATUS, "success");
                eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.getApplicationContext().startActivity(eventIntent);
            } else {
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                mApplication.getApplicationContext().sendBroadcast(it);

                Intent eventIntent = new Intent(mApplication.getApplicationContext(), EventActivity.class);
                eventIntent.putExtra(Constants.PARAM_EVENT_ID, eventId);
                eventIntent.putExtra(Constants.PARAM_EVENT_CONFIRM_STATUS, "failed");
                eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mApplication.getApplicationContext().startActivity(eventIntent);
            }
        } else {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mApplication.getApplicationContext().sendBroadcast(it);

            Intent eventIntent = new Intent(mApplication.getApplicationContext(), EventActivity.class);
            eventIntent.putExtra(Constants.PARAM_EVENT_ID, eventId);
            eventIntent.putExtra(Constants.PARAM_EVENT_CONFIRM_STATUS, "failed");
            eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplication.getApplicationContext().startActivity(eventIntent);
        }
    }
}