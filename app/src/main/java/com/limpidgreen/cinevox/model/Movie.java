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
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.util.Constants;

import java.util.Comparator;
import java.util.Date;

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
    private Float imdbRating;
    private Date releaseDate;
    @SerializedName("date_collected")
    private Date dateAdded;

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

    public Float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", poster='" + poster + '\'' +
                ", year='" + year + '\'' +
                ", imdbRating=" + imdbRating +
                ", releaseDate=" + releaseDate +
                ", dateAdded=" + dateAdded +
                '}';
    }

    /**
     * Ascending Title comparator.
     */
    public static Comparator<Movie> TITLE_ASC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            return movie1.getTitle().compareTo(movie2.getTitle());
        }
    };

    /**
     * Descending Title comparator.
     */
    public static Comparator<Movie> TITLE_DESC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            return movie2.getTitle().compareTo(movie1.getTitle());
        }
    };

    /**
     * Descending Movie release date comparator.
     */
    public static Comparator<Movie> RELEASE_DATE_DESC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            return movie1.getReleaseDate().compareTo(movie2.getReleaseDate());
        }
    };


    /**
     *  Ascending Movie release date comparator.
     */
    public static Comparator<Movie> RELEASE_DATE_ASC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            Log.i(Constants.TAG, "RELEASE 1:" + movie1.getReleaseDate() + " RELESE 2: " + movie2.getReleaseDate());
            return movie2.getReleaseDate().compareTo(movie1.getReleaseDate());
        }
    };

    /**
     * Ascending imdb rating comparator.
     */
    public static Comparator<Movie> RATING_ASC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            if (movie1.getImdbRating() == null) {
                movie1.setImdbRating(0f);
            }
            if (movie2.getImdbRating() == null) {
                movie2.setImdbRating(0f);
            }
            return movie1.getImdbRating().compareTo(movie2.getImdbRating());
        }
    };

    /**
     * Descending imdb rating comparator.
     */
    public static Comparator<Movie> RATING_DESC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            if (movie1.getImdbRating() == null) {
                movie1.setImdbRating(0f);
            }
            if (movie2.getImdbRating() == null) {
                movie2.setImdbRating(0f);
            }
            return movie2.getImdbRating().compareTo(movie1.getImdbRating());
        }
    };

    /**
     * Descending Movie date added comparator.
     */
    public static Comparator<Movie> DATE_ADDED_DESC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            if (movie1.getDateAdded() == null) {
                movie1.setDateAdded(new Date(Long.MIN_VALUE));
            }
            if (movie2.getDateAdded() == null) {
                movie2.setDateAdded(new Date(Long.MIN_VALUE));
            }
            return movie1.getDateAdded().compareTo(movie2.getDateAdded());
        }
    };


    /**
     *  Ascending Movie date added comparator.
     */
    public static Comparator<Movie> DATE_ADDED_ASC_COMPARATOR = new Comparator<Movie>() {
        public int compare(Movie movie1, Movie movie2) {
            Log.i(Constants.TAG, "added 1:" + movie1.getDateAdded() + " added 2: " + movie2.getDateAdded());
            if (movie1.getDateAdded() == null) {
                movie1.setDateAdded(new Date(Long.MIN_VALUE));
            }
            if (movie2.getDateAdded() == null) {
                movie2.setDateAdded(new Date(Long.MIN_VALUE));
            }
            return movie2.getDateAdded().compareTo(movie1.getDateAdded());
        }
    };
}
