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
import android.content.ContentUris;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Knockout;
import com.limpidgreen.cinevox.model.Movie;
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
                    Event event = Event.fromCursor(curEvents);

                    Uri EVENT_FRIENDS_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + event.getId() + "/friends" );
                    Cursor curFriends = provider.query(EVENT_FRIENDS_CONTENT_URI, null, null, null, null);

                    if (curFriends != null) {
                        while (curFriends.moveToNext()) {
                            Friend friend = Friend.fromCursor(curFriends);

                            Integer eventAccepted = curFriends.getInt(curFriends.getColumnIndex(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT));
                            if (eventAccepted == 0) {
                                event.getFriendList().add(friend);
                            } else if (eventAccepted == 1) {
                                event.getFriendAcceptedList().add(friend);
                            } else if (eventAccepted == 2) {
                                event.getFriendDeclinedList().add(friend);
                            } // end if-else
                        }
                        curFriends.close();
                    } // end if

                    Uri EVENT_MOVIES_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + event.getId() + "/movies" );
                    Cursor curMovies = provider.query(EVENT_MOVIES_CONTENT_URI, null, null, null, null);

                    Log.d(Constants.TAG, TAG + "> curMovies: " + curMovies.getCount());
                    if (curMovies != null) {
                        while (curMovies.moveToNext()) {
                            Movie movie = Movie.fromCursor(curMovies);

                            Integer winnerMovie = curMovies.getInt(curMovies.getColumnIndex(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER));
                            Log.d(Constants.TAG, TAG + "> MOVIE winnerMovieId: " + winnerMovie);
                            if (winnerMovie != null && winnerMovie == 1) {
                                event.setWinner(movie);
                                Log.d(Constants.TAG, TAG + "> MOVIE WINNER: " + movie);
                            } // end if

                            event.getMovieList().add(movie);
                            Log.d(Constants.TAG, TAG + "> MOVIE: " + movie);
                        }
                        curMovies.close();
                    } // end if

                    localEvents.add(event);

                    Uri EVENT_KNOCKOUT_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + event.getId() + "/" + EventsContentProvider.PATH_KNOCKOUTS);
                    Cursor curKnockouts = provider.query(EVENT_KNOCKOUT_CONTENT_URI, null, null, null, null);

                    Log.d(Constants.TAG, TAG + "> curKnockouts: " + curKnockouts.getCount());
                    if (curKnockouts != null) {
                        while (curKnockouts.moveToNext()) {
                            Integer movie1Id = curKnockouts.getInt(curKnockouts.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1));
                            Integer movie2Id = curKnockouts.getInt(curKnockouts.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2));

                            Movie movie1 = null;
                            Movie movie2 = null;
                            for (Movie movie : event.getMovieList()) {
                                if (movie.getId().equals(movie1Id)) {
                                    movie1 = movie;
                                }
                                if (movie.getId().equals(movie2Id)) {
                                    movie2 = movie;
                                } // end if
                            } // end for

                            Knockout knockout = Knockout.fromCursor(curKnockouts, movie1, movie2);
                            event.setKnockout(knockout);
                            Log.d(Constants.TAG, TAG + "> KNOCKOUT: " + knockout);
                        }
                        curKnockouts.close();
                    } // end if
                }
                curEvents.close();
            }

            // See what Remote events are missing on Local
            ArrayList<Event> eventsToLocal = new ArrayList<Event>();
            ArrayList<Event> eventsToUpdate = new ArrayList<Event>();
            for (Event remoteEvent : remoteEvents) {
                if (!localEvents.contains(remoteEvent)) {
                    boolean found = false;
                    for (Event localEvent : localEvents) {
                        if (localEvent.getId().equals(remoteEvent.getId())) {
                            found = true;
                            eventsToUpdate.add(remoteEvent);
                            break;
                        }
                    }
                    if (!found) {
                        eventsToLocal.add(remoteEvent);
                    }
                }
            }

            // See what local events to delete on Local
            ArrayList<Event> eventsToDelete = new ArrayList<Event>();
            for (Event localEvent : localEvents) {
                if (!remoteEvents.contains(localEvent)) {
                    boolean found = false;
                    for (Event remoteEvent : remoteEvents) {
                        if (localEvent.getId().equals(remoteEvent.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        eventsToDelete.add(localEvent);
                    }
                }
            }

            if (eventsToLocal.size() == 0 && eventsToDelete.size() == 0 && eventsToUpdate.size() == 0) {
                Log.d(Constants.TAG, TAG + "> No server changes to update local database");
            } else {
                Log.d(Constants.TAG, TAG + "> Updating local database with remote changes");

                // Inserting local events
                if (eventsToLocal.size() != 0) {
                    final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                    for (Event localEvent : eventsToLocal) {

                        final ContentProviderOperation.Builder builder = ContentProviderOperation
                                .newInsert(EventsContentProvider.CONTENT_URI);
                        builder.withValues(localEvent.getContentValues());
                        batch.add(builder.build());

                        ArrayList<Friend> allFriends = new ArrayList<Friend>();
                        allFriends.addAll(localEvent.getFriendList());
                        allFriends.addAll(localEvent.getFriendAcceptedList());
                        allFriends.addAll(localEvent.getFriendDeclinedList());

                        for (Friend friend : allFriends) {
                            Friend localFriend = null;
                            Cursor curFriend = provider.query(ContentUris.withAppendedId(EventsContentProvider.FRIENDS_CONTENT_URI, friend.getId()), null, null, null, null);
                            Log.d(Constants.TAG, TAG + "> cur friend:" + curFriend);
                            if (curFriend != null) {
                                while (curFriend.moveToNext()) {
                                    localFriend = Friend.fromCursor(curFriend);
                                }
                                curFriend.close();
                            } // end if

                            if (localFriend == null) {
                                final ContentProviderOperation.Builder friendBuilder = ContentProviderOperation
                                        .newInsert(EventsContentProvider.FRIENDS_CONTENT_URI);
                                friendBuilder.withValues(friend.getContentValues());
                                batch.add(friendBuilder.build());
                            }

                            if (localEvent.getFriendList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                        .appendPath(FriendsContentProvider.PATH).build())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 0)
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            } else if (localEvent.getFriendAcceptedList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                    .appendPath(FriendsContentProvider.PATH).build())
                                    .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                    .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 1)
                                    .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            } else if (localEvent.getFriendDeclinedList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                        .appendPath(FriendsContentProvider.PATH).build())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 2)
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            }
                        }

                        for (Movie movie : localEvent.getMovieList()) {
                            Movie localMovie = null;
                            Cursor curMovie = provider.query(ContentUris.withAppendedId(EventsContentProvider.MOVIES_CONTENT_URI, movie.getId()), null, null, null, null);
                            if (curMovie != null) {
                                while (curMovie.moveToNext()) {
                                    localMovie = Movie.fromCursor(curMovie);
                                }
                                curMovie.close();
                            } // end if

                            if (localMovie == null) {
                                final ContentProviderOperation.Builder movieBuilder = ContentProviderOperation
                                        .newInsert(EventsContentProvider.MOVIES_CONTENT_URI);
                                movieBuilder.withValues(movie.getContentValues());
                                batch.add(movieBuilder.build());
                            }

                            if (localEvent.getWinner() != null && movie.getId().equals(localEvent.getWinner().getId())) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 1)
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
                            } else {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 0)
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
                            }
                        }

                        if (localEvent.getKnockout() != null) {
                            Knockout localKnockout = null;
                            Cursor curKnockout = provider.query(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                    .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build(), null, null, null, null);
                            if (curKnockout != null) {
                                while (curKnockout.moveToNext()) {
                                    Integer movie1Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1));
                                    Integer movie2Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2));

                                    Movie movie1 = null;
                                    Movie movie2 = null;
                                    for (Movie movie : localEvent.getMovieList()) {
                                        if (movie.getId().equals(movie1Id)) {
                                            movie1 = movie;
                                        }
                                        if (movie.getId().equals(movie2Id)) {
                                            movie2 = movie;
                                        } // end if
                                    } // end for
                                    localKnockout = localKnockout.fromCursor(curKnockout, movie1, movie2);
                                }
                                curKnockout.close();
                            } // end if

                            if (localKnockout == null) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, localEvent.getKnockout().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, localEvent.getKnockout().getMovie1().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, localEvent.getKnockout().getMovie2().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, localEvent.getKnockout().getRound()).build());
                            } else {
                                batch.add(ContentProviderOperation.newUpdate(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, localEvent.getKnockout().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, localEvent.getKnockout().getMovie1().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, localEvent.getKnockout().getMovie2().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, localEvent.getKnockout().getRound()).build());
                            } //end if-else
                        } // end if
                    }
                    provider.applyBatch(batch);
                } // end if

                // Updating local events
                if (eventsToUpdate.size() != 0) {
                    final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                    for (Event localEvent : eventsToUpdate) {

                        final ContentProviderOperation.Builder builder = ContentProviderOperation
                                .newUpdate(ContentUris.withAppendedId(EventsContentProvider.CONTENT_URI, localEvent.getId()));
                        builder.withValues(localEvent.getContentValues());
                        batch.add(builder.build());

                        ArrayList<Friend> allFriends = new ArrayList<Friend>();
                        allFriends.addAll(localEvent.getFriendList());
                        allFriends.addAll(localEvent.getFriendAcceptedList());
                        allFriends.addAll(localEvent.getFriendDeclinedList());

                        for (Friend friend : allFriends) {
                            Friend localFriend = null;
                            Cursor curFriend = provider.query(ContentUris.withAppendedId(EventsContentProvider.FRIENDS_CONTENT_URI, friend.getId()), null, null, null, null);

                            Log.d(Constants.TAG, TAG + "> cur friend:" + curFriend);
                            if (curFriend != null) {
                                while (curFriend.moveToNext()) {
                                    localFriend = Friend.fromCursor(curFriend);
                                }
                                curFriend.close();
                            } // end if

                            if (localFriend == null) {
                                final ContentProviderOperation.Builder friendBuilder = ContentProviderOperation
                                        .newInsert(EventsContentProvider.FRIENDS_CONTENT_URI);
                                friendBuilder.withValues(friend.getContentValues());
                                batch.add(friendBuilder.build());
                            }

                            if (localEvent.getFriendList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                        .appendPath(FriendsContentProvider.PATH).build())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 0)
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            } else if (localEvent.getFriendAcceptedList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                        .appendPath(FriendsContentProvider.PATH).build())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 1)
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            } else if (localEvent.getFriendDeclinedList().contains(localFriend)) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                        .appendPath(FriendsContentProvider.PATH).build())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 2)
                                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
                            }
                        }

                        for (Movie movie : localEvent.getMovieList()) {
                            Movie localMovie = null;
                            Cursor curMovie = provider.query(ContentUris.withAppendedId(EventsContentProvider.MOVIES_CONTENT_URI, movie.getId()), null, null, null, null);
                            if (curMovie != null) {
                                while (curMovie.moveToNext()) {
                                    localMovie = Movie.fromCursor(curMovie);
                                }
                                curMovie.close();
                            } // end if

                            if (localMovie == null) {
                                final ContentProviderOperation.Builder movieBuilder = ContentProviderOperation
                                        .newInsert(EventsContentProvider.MOVIES_CONTENT_URI);
                                movieBuilder.withValues(movie.getContentValues());
                                batch.add(movieBuilder.build());
                            }

                            if (localEvent.getWinner() != null && movie.getId().equals(localEvent.getWinner().getId())) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 1)
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
                            } else {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 0)
                                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
                            }
                        }

                        if (localEvent.getKnockout() != null) {
                            Knockout localKnockout = null;
                            Cursor curKnockout = provider.query(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                    .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build(), null, null, null, null);
                            if (curKnockout != null) {
                                while (curKnockout.moveToNext()) {
                                    Integer movie1Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1));
                                    Integer movie2Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2));

                                    Movie movie1 = null;
                                    Movie movie2 = null;
                                    for (Movie movie : localEvent.getMovieList()) {
                                        if (movie.getId().equals(movie1Id)) {
                                            movie1 = movie;
                                        }
                                        if (movie.getId().equals(movie2Id)) {
                                            movie2 = movie;
                                        } // end if
                                    } // end for
                                    localKnockout = localKnockout.fromCursor(curKnockout, movie1, movie2);
                                }
                                curKnockout.close();
                            } // end if

                            if (localKnockout == null) {
                                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, localEvent.getKnockout().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, localEvent.getKnockout().getMovie1().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, localEvent.getKnockout().getMovie2().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, localEvent.getKnockout().getRound()).build());
                            } else {
                                batch.add(ContentProviderOperation.newUpdate(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, localEvent.getKnockout().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, localEvent.getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, localEvent.getKnockout().getMovie1().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, localEvent.getKnockout().getMovie2().getId())
                                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, localEvent.getKnockout().getRound()).build());
                            } //end if-else
                        } // end if
                    }
                    provider.applyBatch(batch);
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

                        for (Friend friend : localEvent.getFriendList()) {
                            operations.add(ContentProviderOperation.newDelete(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                                    .appendPath(FriendsContentProvider.PATH).build())
                                    .withSelection(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID + " = ?", new String[]{localEvent.getId().toString()})
                                    .build());
                        }

                        for (Movie movie : localEvent.getMovieList()) {
                            operations.add(ContentProviderOperation.newDelete(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                                    .appendPath(EventsContentProvider.PATH_MOVIES).build())
                                    .withSelection(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID + " = ?", new String[]{localEvent.getId().toString()})
                                    .build());
                        }

                        if (localEvent.getKnockout() != null) {
                            operations.add(ContentProviderOperation.newDelete(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(localEvent.getId().toString())
                                    .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                                    .withSelection(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID + " = ?", new String[]{localEvent.getId().toString()})
                                    .build());
                        }

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
