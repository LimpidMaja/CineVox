/**
 * Movie.java
 *
 * 25.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.limpidgreen.cinevox.dao.CineVoxDBHelper;

/**
 * Movie class.
 *
 * @author MajaDobnik
 *
 */
public class Movie implements Parcelable {

    private Integer id;
    private String title;
    private String poster;
    private String year;

    public Movie(Integer id, String title, String poster, String year) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.year = year;
    }

    /**
     * Constructor.
     *
     * @param source
     */
    public Movie(Parcel source) {
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

        values.put(CineVoxDBHelper.MOVIES_COL_ID, id);
        values.put(CineVoxDBHelper.MOVIES_COL_TITLE, title);
        values.put(CineVoxDBHelper.MOVIES_COL_POSTER , poster);
        values.put(CineVoxDBHelper.MOVIES_COL_YEAR, year);
        return values;
    }

    // Create a Movie object from a cursor
    public static Movie fromCursor(Cursor curMovie) {
        Integer id = curMovie.getInt(curMovie.getColumnIndex(CineVoxDBHelper.MOVIES_COL_ID));
        String title = curMovie.getString(curMovie.getColumnIndex(CineVoxDBHelper.MOVIES_COL_TITLE));
        String poster = curMovie.getString(curMovie.getColumnIndex(CineVoxDBHelper.MOVIES_COL_POSTER));
        String year = curMovie.getString(curMovie.getColumnIndex(CineVoxDBHelper.MOVIES_COL_YEAR));

        return new Movie(id, title, poster, year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof Movie)) return false;

        Movie movie = (Movie) o;

        if (!id.equals(movie.id)) return false;
         return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + id;
        return result;
    }

    public static final Creator<Movie> CREATOR =
            new Creator<Movie>() {

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
                 */
                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                } // end createFromParcel()

                /*
                 * (non-Javadoc)
                 *
                 * @see android.os.Parcelable.Creator#newArray(int)
                 */
                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                } // end newArray()
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(year);
    }

    /**
     * Reads from Parcel.
     *
     * @param source
     */
    public void readFromParcel(Parcel source) {
        id = source.readInt();
        title = source.readString();
        poster = source.readString();
        year = source.readString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", poster='" + poster + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
