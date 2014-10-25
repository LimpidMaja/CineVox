/**
 * EventsSyncAdapter.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.syncadapter;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Events Sync Adapter.
 *
 * @author MajaDobnik
 *
 */
public class EventsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "EventsSyncAdapter";

    private final AccountManager mAccountManager;

    public EventsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.d(Constants.TAG,  TAG + "> onPerformSync for account[" + account.name + "]. Extras: " + sb.toString());

        try {
            // Get the auth token for the current account
            String authToken = mAccountManager.blockingGetAuthToken(account,
                    Constants.API_TOKEN_TYPE, true);

            Log.d(Constants.TAG, TAG + "> Get remote Events");
            // Get events from remote
            ArrayList<Event> remoteEvents = NetworkUtil.getEvents(authToken);

            Log.d(Constants.TAG, TAG + "> Get local Events");
            // Get events from local
            ArrayList<Event> localEvents = new ArrayList<Event>();
            Cursor curEvents = provider.query(EventsContentProvider.CONTENT_URI, null, null, null, null);
            if (curEvents != null) {
                while (curEvents.moveToNext()) {
                    localEvents.add(Event.fromCursor(curEvents));
                }
                curEvents.close();
            }

            // See what Remote events are missing on Local
            ArrayList<Event> eventsToLocal = new ArrayList<Event>();
            for (Event remoteEvent : remoteEvents) {
                if (!localEvents.contains(remoteEvent)) {
                    eventsToLocal.add(remoteEvent);
                }
            }

            // See what local events to delete on Local
            ArrayList<Event> eventsToDelete = new ArrayList<Event>();
            for (Event localEvent : localEvents) {
                if (!remoteEvents.contains(localEvent)) {
                    eventsToDelete.add(localEvent);
                }
            }

            if (eventsToLocal.size() == 0 && eventsToDelete.size() == 0) {
                Log.d(Constants.TAG, TAG + "> No server changes to update local database");
            } else {
                Log.d(Constants.TAG, TAG + "> Updating local database with remote changes");

                // Updating local events
                if (eventsToLocal.size() != 0) {
                    int i = 0;
                    ContentValues eventsToLocalValues[] = new ContentValues[eventsToLocal.size()];
                    for (Event localEvent : eventsToLocal) {
                        Log.d(Constants.TAG, TAG + "> Remote -> Local [" + localEvent.getName() + " date: " + localEvent.getDate() + " TIME: " + localEvent.getTime() + "]");
                        eventsToLocalValues[i++] = localEvent.getContentValues();
                    }

                    provider.bulkInsert(EventsContentProvider.CONTENT_URI, eventsToLocalValues);
                } // end if

                if (eventsToDelete.size() != 0) {
                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                    ContentProviderOperation operation;

                    for (Event localEvent : eventsToDelete) {
                        Log.d(Constants.TAG, TAG + "> Remote -> Local Delete [" + localEvent.getName() + " date: " + localEvent.getDate() + " TIME: " + localEvent.getTime() + "]");
                        operation = ContentProviderOperation
                                .newDelete(EventsContentProvider.CONTENT_URI)
                                .withSelection(CineVoxDBHelper.EVENTS_COL_ID + " = ?", new String[]{localEvent.getId().toString()})
                                .build();
                        operations.add(operation);
                    } // end for
                    provider.applyBatch(operations);
                } // end if
            }

            Log.d(Constants.TAG, TAG + "> Finished.");

        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
