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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content Provider for Events.
 *
 * @author MajaDobnik
 *
 */
public class EventsContentProvider extends ContentProvider {

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.events";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.cinevox.events";

    public static final String AUTHORITY = "com.limpidgreen.cinevox.events.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");

    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    public static final String PATH = "events";
    public static final int PATH_TOKEN = 100;
    public static final String PATH_FOR_ID = "events/*";
    public static final int PATH_FOR_ID_TOKEN = 101;

    private CineVoxDBHelper dbHelper;

    // Uri Matcher for the content provider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;
        matcher.addURI(authority, PATH, PATH_TOKEN);
        matcher.addURI(authority, PATH_FOR_ID, PATH_FOR_ID_TOKEN);
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
            case (PATH_FOR_ID_TOKEN):
                String eventIdWhereClause = CineVoxDBHelper.EVENTS_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    eventIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.EVENTS_TABLE_NAME, eventIdWhereClause, selectionArgs);
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
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
