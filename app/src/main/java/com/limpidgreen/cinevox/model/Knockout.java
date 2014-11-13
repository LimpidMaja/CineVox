/**
 * Knockout.java
 *
 * 31.10.2014
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
 * Knockout class.
 *
 * @author MajaDobnik
 *
 */
public class Knockout {

    private Integer id;
    private Movie movie1;
    private Movie movie2;
    private Integer round;

    public Knockout(Integer id, Movie movie1, Movie movie2, Integer round) {
        this.id = id;
        this.movie1 = movie1;
        this.movie2 = movie2;
        this.round = round;
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

        values.put(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID, id);
        values.put(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_1 , movie1.getId());
        values.put(CineVoxDBHelper.EVENT_KNOCKOUT_COL_MOVIE_ID_2, movie2.getId());
        values.put(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND, round);
        return values;
    }

    // Create a Movie object from a cursor
    public static Knockout fromCursor(Cursor curMovie, Movie movie1, Movie movie2) {
        Integer id = curMovie.getInt(curMovie.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ID));
        Integer round = curMovie.getInt(curMovie.getColumnIndex(CineVoxDBHelper.EVENT_KNOCKOUT_COL_ROUND));

        return new Knockout(id, movie1, movie2, round);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof Knockout)) return false;

        Knockout knockout = (Knockout) o;

        if (!id.equals(knockout.id)) return false;
        if (!movie1.getId().equals(knockout.movie1.getId())) return false;
        if (!movie2.getId().equals(knockout.movie2.getId())) return false;
        if (!round.equals(knockout.round)) return false;
         return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + id;
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Movie getMovie1() {
        return movie1;
    }

    public void setMovie1(Movie movie1) {
        this.movie1 = movie1;
    }

    public Movie getMovie2() {
        return movie2;
    }

    public void setMovie2(Movie movie2) {
        this.movie2 = movie2;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    @Override
    public String toString() {
        return "Knockout{" +
                "id=" + id +
                ", movie1=" + movie1 +
                ", movie2=" + movie2 +
                ", round=" + round +
                '}';
    }
}
