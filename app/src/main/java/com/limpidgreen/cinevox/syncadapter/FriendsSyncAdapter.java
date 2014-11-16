/**
 * FriendsSyncAdapter.java
 *
 * 19.10.2014
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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Friends Sync Adapter.
 *
 * @author MajaDobnik
 *
 */
public class FriendsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "FriendsSyncAdapter";

    private final AccountManager mAccountManager;

    public FriendsSyncAdapter(Context context, boolean autoInitialize) {
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

            Log.d(Constants.TAG, TAG + "> Get remote Friends");
            // Get friends from remote
            ArrayList<Friend> remoteFriends = NetworkUtil.getFriends(authToken);

            Log.d(Constants.TAG, TAG + "> Get local Friends");
            // Get friends from local
            ArrayList<Friend> localFriends = new ArrayList<Friend>();
            Cursor curFriends = provider.query(FriendsContentProvider.CONTENT_URI, null, null, null, null);
            if (curFriends != null) {
                while (curFriends.moveToNext()) {
                    localFriends.add(Friend.fromCursor(curFriends));
                }
                curFriends.close();
            }

            // See what Remote Friends are missing on Local
            ArrayList<Friend> friendsToLocal = new ArrayList<Friend>();
            ArrayList<Friend> friendsToUpdate = new ArrayList<Friend>();
            for (Friend remoteFriend : remoteFriends) {
                if (!localFriends.contains(remoteFriend)) {
                    boolean found = false;
                    for (Friend localFriend : localFriends) {
                        if (localFriend.getId().equals(remoteFriend.getId())) {
                            found = true;
                            friendsToUpdate.add(remoteFriend);
                            break;
                        }
                    }
                    if (!found) {
                        friendsToLocal.add(remoteFriend);
                    }
                }
            }

            // See what local Friends to delete on Local
            ArrayList<Friend> friendsToDelete = new ArrayList<Friend>();
            for (Friend localFriend : localFriends) {
                if (!remoteFriends.contains(localFriend)) {
                    boolean found = false;
                    for (Friend remoteFriend : remoteFriends) {
                        if (localFriend.getId().equals(remoteFriend.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (localFriend.isConfirmed() || localFriend.isRequest()) {
                            friendsToDelete.add(localFriend);
                        }
                    }
                }
            }

            if (friendsToLocal.size() == 0 && friendsToDelete.size() == 0 && friendsToUpdate.size() == 0) {
                Log.d(Constants.TAG, TAG + "> No server changes to update local database");
            } else {
                Log.d(Constants.TAG, TAG + "> Updating local database with remote changes");

                // Updating local friends
                if (friendsToLocal.size() != 0) {
                    int i = 0;
                    ContentValues friendsToLocalValues[] = new ContentValues[friendsToLocal.size()];
                    for (Friend localFriend : friendsToLocal) {
                        Log.d(Constants.TAG, TAG + "> Remote -> Local [" + localFriend.getName() + "]");
                        friendsToLocalValues[i++] = localFriend.getContentValues();
                    }

                    provider.bulkInsert(FriendsContentProvider.CONTENT_URI, friendsToLocalValues);
                } // end if

                // Updating local events
                if (friendsToUpdate.size() != 0) {
                    final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                    for (Friend localFriend : friendsToUpdate) {
                        final ContentProviderOperation.Builder builder = ContentProviderOperation
                                .newUpdate(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, localFriend.getId()));
                        builder.withValues(localFriend.getContentValues());
                        batch.add(builder.build());
                    }
                    provider.applyBatch(batch);
                }

                if (friendsToDelete.size() != 0) {
                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                    ContentProviderOperation operation;

                    for (Friend localFriend : friendsToDelete) {
                        Log.d(Constants.TAG, TAG + "> Remote -> Local Delete [" + localFriend.getName() + "]");
                        operation = ContentProviderOperation
                                .newDelete(FriendsContentProvider.CONTENT_URI)
                                .withSelection(CineVoxDBHelper.FRIENDS_COL_ID + " = ?", new String[]{localFriend.getId().toString()})
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
