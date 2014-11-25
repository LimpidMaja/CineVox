/**
 * Movie.java
 *
 * 25.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Movie List class.
 *
 * @author MajaDobnik
 *
 */
public class MovieList {

    private String title;

    @SerializedName("movies")
    private ArrayList<Movie> movieList;

    public MovieList(String title, ArrayList<Movie> movieList) {
        this.title = title;
        this.movieList = movieList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof MovieList)) return false;

        MovieList movie = (MovieList) o;

        if (!title.equals(movie.title)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result;
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(ArrayList<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public String toString() {
        return "MovieList{" +
                "title='" + title + '\'' +
                ", movieList=" + movieList +
                '}';
    }

    /*public static class MovieListDeserializer implements JsonDeserializer<Event> {
        @Override
        public MovieList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            MovieList movieList = gson.fromJson(json, MovieList.class);

            if (movieList.getMovieList() == null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                ArrayList<Movie> movies = gson.fromJson(
                        json.get("movies"),
                        new TypeToken<ArrayList<Movie>>() {
                        }.getType());
            } // end if

            return movieList;
        }
    }*/
}
