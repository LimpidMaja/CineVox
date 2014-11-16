/**
 * Friend.java
 *
 * 19.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.util.Constants;

import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Friend class.
 *
 * @author MajaDobnik
 *
 */
public class Friend implements Serializable, Parcelable {

    private Integer id;
    private String name;
    private String username;
    private String facebookUID;
    private Boolean confirmed;
    private Boolean request;

    public Friend(Integer id, String name, String username, String facebookUID, Boolean confirmed, Boolean request) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.facebookUID = facebookUID;
        this.confirmed = confirmed;
        this.request = request;
    }

    /**
     * Constructor.
     *
     * @param source
     */
    public Friend(Parcel source) {
        readFromParcel(source);
    }

    /**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CineVoxDBHelper.FRIENDS_COL_ID, id);
        values.put(CineVoxDBHelper.FRIENDS_COL_NAME, name);
        values.put(CineVoxDBHelper.FRIENDS_COL_USERNAME , username);
        values.put(CineVoxDBHelper.FRIENDS_COL_FACEBOOK_UID, facebookUID);
        values.put(CineVoxDBHelper.FRIENDS_COL_CONFIRMED, confirmed);
        values.put(CineVoxDBHelper.FRIENDS_COL_REQUEST, request);
        return values;
    }

    // Create a Friend object from a cursor
    public static Friend fromCursor(Cursor curFriend) {
        Integer id = curFriend.getInt(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_ID));
        String name = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_NAME));
        String username = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_USERNAME));
        String facebookUID = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_FACEBOOK_UID));
        Boolean confirmed = (curFriend.getInt(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_CONFIRMED)) == 1)? true : false;
        Boolean request = (curFriend.getInt(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_REQUEST)) == 1)? true : false;

        return new Friend(id, name, username, facebookUID, confirmed, request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof Friend)) return false;

        Friend friend = (Friend) o;

        if (!name.equals(friend.name)) return false;
        if (!id.equals(friend.id)) return false;
        if (!username.equals(friend.username)) return false;
        if (!facebookUID.equals(friend.facebookUID)) return false;
        if (!confirmed.equals(friend.confirmed)) return false;
        if (!request.equals(friend.request)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id;
        return result;
    }

    public static final Parcelable.Creator<Friend> CREATOR =
            new Parcelable.Creator<Friend>() {

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
                 */
                @Override
                public Friend createFromParcel(Parcel source) {
                    return new Friend(source);
                } // end createFromParcel()

                /*
                 * (non-Javadoc)
                 *
                 * @see android.os.Parcelable.Creator#newArray(int)
                 */
                @Override
                public Friend[] newArray(int size) {
                    return new Friend[size];
                } // end newArray()
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(facebookUID);
        dest.writeString(confirmed.toString());
        dest.writeString(request.toString());
    }

    /**
     * Reads from Parcel.
     *
     * @param source
     */
    public void readFromParcel(Parcel source) {
        id = source.readInt();
        name = source.readString();
        username = source.readString();
        facebookUID = source.readString();
        confirmed = source.readString().equals("true") ? true : false;
        request = source.readString().equals("true") ? true : false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacebookUID() {
        return facebookUID;
    }

    public void setFacebookUID(String facebookUID) {
        this.facebookUID = facebookUID;
    }

    public Boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean isRequest() {
        return request;
    }

    public void setRequest(Boolean request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", facebookUID='" + facebookUID + '\'' +
                ", confirmed=" + confirmed +
                ", request=" + request +
                '}';
    }
}
