/**
 * EventsContentProvider.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.dao;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Knockout;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.util.Constants;

import java.util.ArrayList;

/**
 * Content Provider for Events.
 *
 * @author MajaDobnik
 *
 */
public class EventsContentProvider extends ContentProvider {

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.events";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.cinevox.events";
    public static final String MOVIES_CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.cinevox.movies";
    public static final String MOVIES_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.movies";
    public static final String FRIENDS_CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.cinevox.friends";
    public static final String FRIENDS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.friends";
    public static final String EVENT_KNOCKOUT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.knockouts";

    public static final String AUTHORITY = "com.limpidgreen.cinevox.events.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");
    public static final Uri MOVIES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/movies");
    public static final Uri FRIENDS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/friends");
    public static final Uri KNOCKOUT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/knockouts");

    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    public static final String PATH = "events";
    public static final String PATH_FOR_ID = "events/*";
    public static final String PATH_FOR_MOVIES = "events/*/movies";
    public static final String PATH_FOR_FRIENDS = "events/*/friends";
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FOR_MOVIES_ID = "movies/*";
    public static final String PATH_FRIENDS = "friends";
    public static final String PATH_FOR_FRIENDS_ID = "friends/*";
    public static final String PATH_FOR_KNOCKOUTS = "events/*/knockouts";
    public static final String PATH_KNOCKOUTS = "knockouts";

    public static final int PATH_TOKEN = 100;
    public static final int PATH_FOR_ID_TOKEN = 101;
    public static final int PATH_FOR_ID_MOVIES_TOKEN = 102;
    public static final int PATH_FOR_ID_FRIENDS_TOKEN = 103;
    public static final int PATH_FOR_MOVIES_TOKEN = 104;
    public static final int PATH_FOR_FRIENDS_TOKEN = 105;
    public static final int PATH_FOR_FRIENDS_ID_TOKEN = 106;
    public static final int PATH_FOR_MOVIES_ID_TOKEN = 107;
    public static final int PATH_FOR_ID_KNOCKOUTS_TOKEN = 108;

    private CineVoxDBHelper dbHelper;

    // Uri Matcher for the content provider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;
        matcher.addURI(authority, PATH, PATH_TOKEN);
        matcher.addURI(authority, PATH_FOR_ID, PATH_FOR_ID_TOKEN);
        matcher.addURI(authority, PATH_MOVIES, PATH_FOR_MOVIES_TOKEN);
        matcher.addURI(authority, PATH_FRIENDS, PATH_FOR_FRIENDS_TOKEN);
        matcher.addURI(authority, PATH_FOR_MOVIES, PATH_FOR_ID_MOVIES_TOKEN);
        matcher.addURI(authority, PATH_FOR_FRIENDS, PATH_FOR_ID_FRIENDS_TOKEN);
        matcher.addURI(authority, PATH_FOR_MOVIES_ID, PATH_FOR_MOVIES_ID_TOKEN);
        matcher.addURI(authority, PATH_FOR_FRIENDS_ID, PATH_FOR_FRIENDS_ID_TOKEN);
        matcher.addURI(authority, PATH_FOR_KNOCKOUTS, PATH_FOR_ID_KNOCKOUTS_TOKEN);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        dbHelper = new CineVoxDBHelper(ctx);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            // retrieve events list
            case PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.EVENTS_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_TOKEN: {
                int eventId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.EVENTS_TABLE_NAME);
                builder.appendWhere(CineVoxDBHelper.EVENTS_COL_ID + "=" + eventId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_MOVIES_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.MOVIES_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_MOVIES_ID_TOKEN: {
                int movieId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.MOVIES_TABLE_NAME);
                builder.appendWhere(CineVoxDBHelper.MOVIES_COL_ID + "=" + movieId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_FRIENDS_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.FRIENDS_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_FRIENDS_ID_TOKEN: {
                int movieId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.FRIENDS_TABLE_NAME);
                builder.appendWhere(CineVoxDBHelper.FRIENDS_COL_ID + "=" + movieId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_MOVIES_TOKEN: {
                int eventId = Integer.valueOf(uri.getPathSegments().get(1));
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME
                        + " LEFT OUTER JOIN " + CineVoxDBHelper.MOVIES_TABLE_NAME + " ON "
                        + CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME + "." + CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID + "="
                        + CineVoxDBHelper.MOVIES_TABLE_NAME + "." + CineVoxDBHelper.MOVIES_COL_ID);
                builder.appendWhere(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID + "=" + eventId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_FRIENDS_TOKEN: {
                int eventId = Integer.valueOf(uri.getPathSegments().get(1));
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME
                        + " LEFT OUTER JOIN " + CineVoxDBHelper.FRIENDS_TABLE_NAME + " ON "
                        + CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME + "." + CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID + "="
                        + CineVoxDBHelper.FRIENDS_TABLE_NAME + "." + CineVoxDBHelper.FRIENDS_COL_ID);
                builder.appendWhere(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID + "=" + eventId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_KNOCKOUTS_TOKEN: {
                int eventId = Integer.valueOf(uri.getPathSegments().get(1));
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.EVENT_KNOCKOUT_TABLE_NAME);
                builder.appendWhere(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID + "=" + eventId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PATH_TOKEN:
                return CONTENT_TYPE_DIR;
            case PATH_FOR_ID_TOKEN:
                return CONTENT_ITEM_TYPE;
            case PATH_FOR_ID_FRIENDS_TOKEN:
                return FriendsContentProvider.CONTENT_TYPE_DIR;
            case PATH_FOR_ID_MOVIES_TOKEN:
                return MOVIES_CONTENT_TYPE_DIR;
            case PATH_FOR_FRIENDS_TOKEN:
                return FRIENDS_CONTENT_TYPE_DIR;
            case PATH_FOR_MOVIES_TOKEN:
                return MOVIES_CONTENT_TYPE_DIR;
            case PATH_FOR_FRIENDS_ID_TOKEN:
                return FriendsContentProvider.CONTENT_ITEM_TYPE;
            case PATH_FOR_MOVIES_ID_TOKEN:
                return MOVIES_CONTENT_ITEM_TYPE;
            case PATH_FOR_ID_KNOCKOUTS_TOKEN:
                return EVENT_KNOCKOUT_CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case PATH_TOKEN: {
                long id = db.insert(CineVoxDBHelper.EVENTS_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case PATH_FOR_MOVIES_TOKEN: {
                long id = db.insert(CineVoxDBHelper.MOVIES_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return MOVIES_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case PATH_FOR_FRIENDS_TOKEN: {
                long id = db.insert(CineVoxDBHelper.FRIENDS_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return FRIENDS_CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
            }
            case PATH_FOR_ID_FRIENDS_TOKEN: {
                long id = db.insert(CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return FriendsContentProvider.CONTENT_URI.buildUpon().appendPath(values.getAsString(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID)).build();
            }
            case PATH_FOR_ID_MOVIES_TOKEN: {
                long id = db.insert(CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return MOVIES_CONTENT_URI.buildUpon().appendPath(values.getAsString(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID)).build();
            }
            case PATH_FOR_ID_KNOCKOUTS_TOKEN: {
                long id = db.insert(CineVoxDBHelper.EVENT_KNOCKOUT_TABLE_NAME, null, values);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return KNOCKOUT_CONTENT_URI.buildUpon().appendPath(values.getAsString(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID)).build();
            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        switch (token) {
            case (PATH_TOKEN):
                rowsDeleted = db.delete(CineVoxDBHelper.EVENTS_TABLE_NAME, selection, selectionArgs);
                break;
            case (PATH_FOR_MOVIES_TOKEN):
                rowsDeleted = db.delete(CineVoxDBHelper.MOVIES_TABLE_NAME, selection, selectionArgs);
                break;
            case (PATH_FOR_FRIENDS_TOKEN):
                rowsDeleted = db.delete(CineVoxDBHelper.FRIENDS_TABLE_NAME, selection, selectionArgs);
                break;
            case (PATH_FOR_ID_TOKEN):
                String eventIdWhereClause = CineVoxDBHelper.EVENTS_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    eventIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.EVENTS_TABLE_NAME, eventIdWhereClause, selectionArgs);
                break;
            case (PATH_FOR_ID_FRIENDS_TOKEN):
                eventIdWhereClause = CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID + "=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection))
                    eventIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME, eventIdWhereClause, selectionArgs);
                break;
            case (PATH_FOR_ID_MOVIES_TOKEN):
                eventIdWhereClause = CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID + "=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection))
                    eventIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME, eventIdWhereClause, selectionArgs);
                break;
            case (PATH_FOR_ID_KNOCKOUTS_TOKEN):
                eventIdWhereClause = CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID + "=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(selection))
                    eventIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.EVENT_KNOCKOUT_TABLE_NAME, eventIdWhereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.i(Constants.TAG, "USRI: " + uri.getPath());
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case PATH_TOKEN:
                rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENTS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PATH_FOR_MOVIES_TOKEN:
                rowsUpdated = sqlDB.update(CineVoxDBHelper.MOVIES_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PATH_FOR_FRIENDS_TOKEN:
                rowsUpdated = sqlDB.update(CineVoxDBHelper.FRIENDS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PATH_FOR_ID_TOKEN:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENTS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENTS_COL_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENTS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENTS_COL_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PATH_FOR_ID_FRIENDS_TOKEN:
                id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_FRIENDS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PATH_FOR_ID_MOVIES_TOKEN:
                id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_MOVIES_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PATH_FOR_ID_KNOCKOUTS_TOKEN:
                id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_KNOCKOUT_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.EVENT_KNOCKOUT_TABLE_NAME,
                            values,
                            CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    public static boolean insertEvent(ContentResolver mResolver, Event mEvent, boolean update) {

        final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        final ContentProviderOperation.Builder builder;
        if (update) {
            builder = ContentProviderOperation
                    .newUpdate(ContentUris.withAppendedId(EventsContentProvider.CONTENT_URI, mEvent.getId()));
        } else {
            builder = ContentProviderOperation
                    .newInsert(EventsContentProvider.CONTENT_URI);
        } // end if-else

        builder.withValues(mEvent.getContentValues());
        batch.add(builder.build());

        ArrayList<Friend> allFriends = new ArrayList<Friend>();
        allFriends.addAll(mEvent.getFriendList());
        allFriends.addAll(mEvent.getFriendAcceptedList());
        allFriends.addAll(mEvent.getFriendDeclinedList());

        for (Friend friend : allFriends) {
            Friend localFriend = null;
            Cursor curFriend = mResolver.query(ContentUris.withAppendedId(EventsContentProvider.FRIENDS_CONTENT_URI, friend.getId()), null, null, null, null);

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

            if (mEvent.getFriendList().contains(localFriend)) {
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                        .appendPath(FriendsContentProvider.PATH).build())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 0)
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
            } else if (mEvent.getFriendAcceptedList().contains(localFriend)) {
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                        .appendPath(FriendsContentProvider.PATH).build())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 1)
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
            } else if (mEvent.getFriendDeclinedList().contains(localFriend)) {
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(friend.getId().toString())
                        .appendPath(FriendsContentProvider.PATH).build())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT, 2)
                        .withValue(CineVoxDBHelper.EVENT_FRIENDS_COL_FRIEND_ID, friend.getId()).build());
            }
        }

        for (Movie movie : mEvent.getMovieList()) {
            Movie localMovie = null;
            Cursor curMovie = mResolver.query(ContentUris.withAppendedId(EventsContentProvider.MOVIES_CONTENT_URI, movie.getId()), null, null, null, null);
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

            if (mEvent.getWinner() != null && movie.getId().equals(mEvent.getWinner().getId())) {
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 1)
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
            } else {
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(movie.getId().toString())
                        .appendPath(EventsContentProvider.PATH_MOVIES).build())
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER, 0)
                        .withValue(CineVoxDBHelper.EVENT_MOVIES_COL_MOVIE_ID, movie.getId()).build());
            }
        }

        if (mEvent.getKnockout() != null) {
            Knockout localKnockout = null;
            Cursor curKnockout = mResolver.query(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(mEvent.getId().toString())
                    .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build(), null, null, null, null);
            if (curKnockout != null) {
                while (curKnockout.moveToNext()) {
                    Integer movie1Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1));
                    Integer movie2Id = curKnockout.getInt(curKnockout.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2));

                    Movie movie1 = null;
                    Movie movie2 = null;
                    for (Movie movie : mEvent.getMovieList()) {
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
                batch.add(ContentProviderOperation.newInsert(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(mEvent.getId().toString())
                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, mEvent.getKnockout().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, mEvent.getKnockout().getMovie1().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, mEvent.getKnockout().getMovie2().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, mEvent.getKnockout().getRound()).build());
            } else {
                batch.add(ContentProviderOperation.newUpdate(EventsContentProvider.CONTENT_URI.buildUpon().appendPath(mEvent.getId().toString())
                        .appendPath(EventsContentProvider.PATH_KNOCKOUTS).build())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, mEvent.getKnockout().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_EVENT_ID, mEvent.getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1, mEvent.getKnockout().getMovie1().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, mEvent.getKnockout().getMovie2().getId())
                        .withValue(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, mEvent.getKnockout().getRound()).build());
            } //end if-else
        } // end if

        try {
            mResolver.applyBatch(EventsContentProvider.AUTHORITY, batch);

            return true;

        } catch (RemoteException re) {
            return false;
        } catch (OperationApplicationException oae) {
            return false;
        }
    }

    public static Event queryEvent(ContentResolver mResolver, Integer eventId) {
        Event mEvent = null;
        Cursor curEvent = mResolver.query(ContentUris.withAppendedId(EventsContentProvider.CONTENT_URI, eventId), null, null, null, null);
        if (curEvent != null) {
            while (curEvent.moveToNext()) {
                mEvent = Event.fromCursor(curEvent);

                Uri EVENT_FRIENDS_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + mEvent.getId() + "/friends" );
                Cursor curFriends = mResolver.query(EVENT_FRIENDS_CONTENT_URI, null, null, null, null);

                if (curFriends != null) {
                    while (curFriends.moveToNext()) {
                        Friend friend = Friend.fromCursor(curFriends);
                        Integer eventAccepted = curFriends.getInt(curFriends.getColumnIndex(CineVoxDBHelper.EVENT_FRIENDS_COL_ACCEPT));
                        if (eventAccepted == 0) {
                            mEvent.getFriendList().add(friend);
                        } else if (eventAccepted == 1) {
                            mEvent.getFriendAcceptedList().add(friend);
                        } else if (eventAccepted == 2) {
                            mEvent.getFriendDeclinedList().add(friend);
                        } // end if-else
                    }
                    curFriends.close();
                } // end if

                Uri EVENT_MOVIES_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + mEvent.getId() + "/movies" );
                Cursor curMovies = mResolver.query(EVENT_MOVIES_CONTENT_URI, null, null, null, null);

                if (curMovies != null) {
                    while (curMovies.moveToNext()) {
                        Movie movie = Movie.fromCursor(curMovies);

                        Integer winnerMovie = curMovies.getInt(curMovies.getColumnIndex(CineVoxDBHelper.EVENT_MOVIES_COL_WINNER));
                        if (winnerMovie != null && winnerMovie == 1) {
                            mEvent.setWinner(movie);
                        } // end if

                        mEvent.getMovieList().add(movie);
                    }
                    curMovies.close();
                } // end if

                Uri EVENT_KNOCKOUT_CONTENT_URI = Uri.parse("content://" + EventsContentProvider.AUTHORITY + "/events/" + mEvent.getId() + "/" + EventsContentProvider.PATH_KNOCKOUTS);
                Cursor curKnockouts = mResolver.query(EVENT_KNOCKOUT_CONTENT_URI, null, null, null, null);

                if (curKnockouts != null) {
                    while (curKnockouts.moveToNext()) {
                        Integer movie1Id = curKnockouts.getInt(curKnockouts.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1));
                        Integer movie2Id = curKnockouts.getInt(curKnockouts.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2));

                        Movie movie1 = null;
                        Movie movie2 = null;
                        for (Movie movie : mEvent.getMovieList()) {
                            if (movie.getId().equals(movie1Id)) {
                                movie1 = movie;
                            }
                            if (movie.getId().equals(movie2Id)) {
                                movie2 = movie;
                            } // end if
                        } // end for

                        Knockout knockout = Knockout.fromCursor(curKnockouts, movie1, movie2);
                        mEvent.setKnockout(knockout);
                    }
                    curKnockouts.close();
                } // end if
            }
            curEvent.close();
        } // end if
        return mEvent;
    }
}
