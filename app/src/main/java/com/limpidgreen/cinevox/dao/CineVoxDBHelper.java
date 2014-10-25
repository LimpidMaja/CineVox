/**
 * CineVoxDBHelper.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Events Database Helper.
 *
 * @author MajaDobnik
 *
 */
public class CineVoxDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "cinevox.db";
    private static final int DATABASE_VERSION = 1;

    // Event DB Table constants
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String EVENTS_COL_ID = "_id";
    public static final String EVENTS_COL_NAME = "name";
    public static final String EVENTS_COL_DESCRIPTION = "description";
    public static final String EVENTS_COL_DATE = "event_date";
    public static final String EVENTS_COL_TIME = "event_time";
    public static final String EVENTS_COL_TIME_LIMIT = "time_limit";
    public static final String EVENTS_COL_PLACE = "place";
    public static final String EVENTS_COL_MINIMUM_VOTING_PERCENT = "minimum_voting_percent";
    public static final String EVENTS_COL_USER_ID = "user_id";
    public static final String EVENTS_COL_FINISHED = "finished";
    public static final String EVENTS_COL_USERS_CAN_ADD_MOVIES = "users_can_add_movies";
    public static final String EVENTS_COL_NUM_ADD_MOVIES_BY_USER = "num_add_movies_by_user";
    public static final String EVENTS_COL_RATING_SYSTEM = "rating_system";
    public static final String EVENTS_COL_NUM_VOTES_PER_USER = "num_votes_per_user";
    public static final String EVENTS_COL_VOTING_RANGE = "voting_range";
    public static final String EVENTS_COL_TIE_KNOCKOUT = "tie_knockout";
    public static final String EVENTS_COL_KNOCKOUT_ROUNDS = "knockout_rounds";
    public static final String EVENTS_COL_KNOCKOUT_TIME_LIMIT = "knockout_time_limit";
    public static final String EVENTS_COL_WAIT_TIME_LIMIT = "wait_time_limit";
    public static final String EVENTS_COL_UPDATED_AT = "updated_at";
    public static final String EVENTS_COL_CRATED_AT = "created_at";
    public static final String EVENTS_COL_RATING_PHASE = "rating_phase";
    public static final String EVENTS_COL_KNOCKOUT_PHASE = "knockout_phase";

    // Friend DB Table constants
    public static final String FRIENDS_TABLE_NAME = "friends";
    public static final String FRIENDS_COL_ID = "_id";
    public static final String FRIENDS_COL_NAME = "name";
    public static final String FRIENDS_COL_USERNAME = "username";
    public static final String FRIENDS_COL_FACEBOOK_UID = "facebook_uid";


    // Event-Friends DB Table constants
    public static final String EVENT_FRIENDS_TABLE_NAME = "event_friends";
    public static final String EVENT_FRIENDS_COL_ID = "_id";
    public static final String EVENT_FRIENDS_COL_EVENT_ID = "event_id";
    public static final String EVENT_FRIENDS_COL_FRIEND_ID = "friend_id";


    // Event Database creation sql statement
    public static final String DATABASE_CREATE_EVENTS = "create table "
            + EVENTS_TABLE_NAME + "(" +
            EVENTS_COL_ID + " integer primary key, " +
            EVENTS_COL_NAME + " text not null, " +
            EVENTS_COL_DESCRIPTION + " text not null, " +
            EVENTS_COL_DATE + " date not null, " +
            EVENTS_COL_TIME + " time not null, " +
            EVENTS_COL_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_PLACE + " text not null, " +
            EVENTS_COL_MINIMUM_VOTING_PERCENT + " integer not null, " +
            EVENTS_COL_USER_ID + " integer not null, " +
            EVENTS_COL_FINISHED + " integer not null, " +
            EVENTS_COL_USERS_CAN_ADD_MOVIES + " integer not null, " +
            EVENTS_COL_NUM_ADD_MOVIES_BY_USER + " integer not null, " +
            EVENTS_COL_RATING_SYSTEM + " integer not null, " +
            EVENTS_COL_NUM_VOTES_PER_USER + " integer not null, " +
            EVENTS_COL_VOTING_RANGE + " integer not null, " +
            EVENTS_COL_TIE_KNOCKOUT + " integer not null, " +
            EVENTS_COL_KNOCKOUT_ROUNDS + " integer not null, " +
            EVENTS_COL_KNOCKOUT_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_WAIT_TIME_LIMIT + " integer not null, " +
            EVENTS_COL_UPDATED_AT + " datetime not null, " +
            EVENTS_COL_CRATED_AT + " datetime not null, " +
            EVENTS_COL_RATING_PHASE + " integer not null, " +
            EVENTS_COL_KNOCKOUT_PHASE + " integer not null" +
            ");";

    // Friend Database creation sql statement
    public static final String DATABASE_CREATE_FRIENDS = "create table "
            + FRIENDS_TABLE_NAME + "(" +
            FRIENDS_COL_ID + " integer primary key, " +
            FRIENDS_COL_NAME + " text not null, " +
            FRIENDS_COL_USERNAME + " text not null, " +
            FRIENDS_COL_FACEBOOK_UID + " text not null" +
            ");";

    public static final String DATABASE_CREATE_EVENT_FRIENDS = "create table "
            + EVENT_FRIENDS_TABLE_NAME + "(" +
            EVENT_FRIENDS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EVENT_FRIENDS_COL_EVENT_ID + " integer not null REFERENCES " + EVENTS_TABLE_NAME + "(" + EVENTS_COL_ID + ")," +
            EVENT_FRIENDS_COL_FRIEND_ID + " integer not null REFERENCES " + FRIENDS_TABLE_NAME + "(" + FRIENDS_COL_ID + ")," +
            "UNIQUE (" + EVENT_FRIENDS_COL_EVENT_ID + ","
            + EVENT_FRIENDS_COL_FRIEND_ID + ") ON CONFLICT REPLACE);";

    public CineVoxDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(DATABASE_CREATE_EVENTS);
        database.execSQL(DATABASE_CREATE_FRIENDS);
        database.execSQL(DATABASE_CREATE_EVENT_FRIENDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CineVoxDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_FRIENDS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FRIENDS_TABLE_NAME);
        onCreate(db);
    }

}
