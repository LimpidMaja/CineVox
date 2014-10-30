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

import com.google.gson.annotations.SerializedName;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;

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

    public Friend(Integer id, String name, String username, String facebookUID) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.facebookUID = facebookUID;
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
        return values;
    }

    // Create a Friend object from a cursor
    public static Friend fromCursor(Cursor curFriend) {
        Integer id = curFriend.getInt(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_ID));
        String name = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_NAME));
        String username = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_USERNAME));
        String facebookUID = curFriend.getString(curFriend.getColumnIndex(CineVoxDBHelper.FRIENDS_COL_FACEBOOK_UID));

        return new Friend(id, name, username, facebookUID);
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

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", facebookUID='" + facebookUID + '\'' +
                '}';
    }
}
