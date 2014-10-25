/**
 * FriendsContentProvider.java
 *
 * 19.10.2014
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
 * Content Provider for Friends.
 *
 * @author MajaDobnik
 *
 */
public class FriendsContentProvider extends ContentProvider {

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cinevox.friends";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.cinevox.friends";

    public static final String AUTHORITY = "com.limpidgreen.cinevox.friends.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/friends");

    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    public static final String PATH = "friends";
    public static final int PATH_TOKEN = 200;
    public static final String PATH_FOR_ID = "friends/*";
    public static final int PATH_FOR_ID_TOKEN = 201;

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
            // retrieve friends list
            case PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.FRIENDS_TABLE_NAME);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_TOKEN: {
                int friendId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(CineVoxDBHelper.FRIENDS_TABLE_NAME);
                builder.appendWhere(CineVoxDBHelper.FRIENDS_COL_ID + "=" + friendId);
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
                long id = db.insert(CineVoxDBHelper.FRIENDS_TABLE_NAME, null, values);
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
                rowsDeleted = db.delete(CineVoxDBHelper.FRIENDS_TABLE_NAME, selection, selectionArgs);
                break;
            case (PATH_FOR_ID_TOKEN):
                String friendIdWhereClause = CineVoxDBHelper.FRIENDS_COL_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    friendIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(CineVoxDBHelper.FRIENDS_TABLE_NAME, friendIdWhereClause, selectionArgs);
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
                rowsUpdated = sqlDB.update(CineVoxDBHelper.FRIENDS_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PATH_FOR_ID_TOKEN:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.FRIENDS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.FRIENDS_COL_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CineVoxDBHelper.FRIENDS_TABLE_NAME,
                            values,
                            CineVoxDBHelper.FRIENDS_COL_ID + "=" + id
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
