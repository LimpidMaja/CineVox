/**
 * NetworkUtil.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.limpidgreen.cinevox.exception.APICallException;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.model.MovieList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Utility methods for API communication.
 *
 * @author MajaDobnik
 *
 */
public class NetworkUtil {
    /** POST parameter name for the user's facebook id */
    public static final String PARAM_FACEBOOK_UID = "uid";
    /** POST parameter name for the user's email */
    public static final String PARAM_EMAIL = "email";
    /** POST parameter name for the user's facebook access token */
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    /** POST parameter name for the user's GCM Registration Id */
    public static final String PARAM_GCM_REG_ID = "gcm_reg_id";
    /** POST parameter name for the user's facebook access token expiration date */
    public static final String PARAM_ACCESS_TOKEN_EXPIRES = "expires_at";
    /** POST parameter name for result's info data */
    public static final String PARAM_INFO = "info";
    /** GET parameter name for search term */
    public static final String PARAM_TERM = "term";
    /** POST parameter name for trakt username */
    public static final String PARAM_TRAKT_USERNAME = "trakt_username";
    /** POST parameter name for trakt password */
    public static final String PARAM_TRAKT_PASSWORD = "trakt_password";


    /** Request timeout */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** Base URL for the API Service */
    //public static final String BASE_URL = "http://192.168.1.103:3000";
    public static final String BASE_URL = "http://cinevox.herokuapp.com";

    /** URI for authentication service */
    public static final String AUTH_URI = BASE_URL + "/api/auth/facebook/callback";

    /** URI for events */
    public static final String EVENTS_URI = BASE_URL + "/api/events";
    /** URI for events confirm */
    public static final String EVENTS_CONFIRM_URI = "/confirm";
    /** URI for events cancel */
    public static final String EVENTS_CANCEL_URI = "/cancel";
    /** URI for events continue */
    public static final String EVENTS_CONTINUE_URI = "/continue";
    /** URI for events increase time limit */
    public static final String EVENTS_TIME_LIMIT_URI = "/time_limit";
    /** URI for events start */
    public static final String EVENTS_START_URI = "/start";
    /** URI for events movies vote */
    public static final String EVENTS_VOTE_URI = "/vote";
    /** URI for events movies knockout */
    public static final String EVENTS_KNOCKOUT_URI = "/knockout_vote";
    /** URI for friends */
    public static final String FRIENDS_URI = BASE_URL + "/api/friends";
    /** URI for friend request confirm */
    public static final String FRIENDS_CONFIRM_REQUEST_URI = "/confirm_friend_request";
    /** URI for friend request send */
    public static final String FRIENDS_SEND_REQUEST_URI = "/send_friend_request";
    /** URI for movies */
    public static final String MOVIES_URI = BASE_URL + "/api/movies";
    /** URI for movies search */
    public static final String MOVIES_SEARCH_URI = MOVIES_URI + "/autocomplete";
    /** URI for movie list search */
    public static final String MOVIES_SEARCH_LISTS_URI = MOVIES_URI + "/search_lists";
    /** URI for movies collection search */
    public static final String MOVIES_SEARCH_COLLECTION_URI = MOVIES_URI + "/collection";
    /** URI for trakt account save */
    public static final String TRAKT_URI = "/trakt";
    /** URI for trakt import */
    public static final String TRAKT_IMPORT_URI = MOVIES_URI + "/import_trakt";
    /** URI for friends search */
    public static final String FRIENDS_SEARCH_URI = FRIENDS_URI + "/autocomplete";

    /**
     * Returns true if the Internet connection is available.
     *
     * @return true if the Internet connection is available
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } // end isNetworkAvailable()

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params,
                HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    } // end getHttpClient()

    /**
     * Connects to server, authenticates the provided user data and returns an API access key.
     *
     * @param userId
     *            The facebook user id
     * @param accessToken
     *            The facebook access token
     * @param expirationDate
     *            The facebook access token expiration date
     * @return API Access key returned by the server (or null)
     */
    public static String authenticate(String userId, String accessToken, String gcmRegId, Date expirationDate, String email) {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PARAM_FACEBOOK_UID, userId));
        params.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN, accessToken));
        params.add(new BasicNameValuePair(PARAM_GCM_REG_ID, gcmRegId));
        params.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN_EXPIRES, String.valueOf(expirationDate.getTime())));
        params.add(new BasicNameValuePair(PARAM_EMAIL, email));

        try {
            String result = postWebService(params, AUTH_URI, null);

            final JSONObject obj = new JSONObject(result);
            String APIToken = obj.getString(PARAM_ACCESS_TOKEN);
            if (APIToken != null && APIToken.length() > 0) {
                Log.i(Constants.TAG, "API KEY: " + APIToken);
                return APIToken;
            } else {
                Log.e(Constants.TAG,
                        "Error authenticating" + obj.getString(PARAM_INFO));
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when getting API token - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when getting API token", e);
            return null;
        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSONException when getting API token", e);
            return null;
        } // end try-catch
    } // end authenticate()

    /**
     * Connects to server, returns events.
     *
     * @param accessToken
     * @return list of events
     */
    public static ArrayList<Event> getEvents(String accessToken) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try {
            JsonObject eventsJson =  getWebService(params, EVENTS_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "RESULT: " + eventsJson.toString());
            if (eventsJson != null) {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Event.class, new Event.EventDeserializer());

                Gson gson = builder.create();
                ArrayList<Event> eventList = gson.fromJson(
                        eventsJson.get("events"),
                        new TypeToken<ArrayList<Event>>() {
                        }.getType());
                return eventList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when getting events - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when getting events", e);
            return null;
        } // end try-catch
    } // end getEvents()

    /**
     * Connects to server, confirms to join event.
     *
     * @param accessToken
     * @param join
     * @return flag if successful
     */
    public static Event confirmEventJoin(String accessToken, Integer eventId, Boolean join) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("accept", join);
        jsonObject.add("event", new JsonObject());
        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_CONFIRM_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when confirming event - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when confirming event", e);
            return null;
        } // end try-catch
    } // end confirmEventJoin()

    /**
     * Connects to server, cancels event.
     *
     * @param accessToken
     * @return flag if successful
     */
    public static Event cancelEvent(String accessToken, Integer eventId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("event", new JsonObject());
        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_CANCEL_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when canceling event - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when canceling event", e);
            return null;
        } // end try-catch
    } // end cancelEvent()

    /**
     * Connects to server, continues event.
     *
     * @param accessToken
     * @return flag if successful
     */
    public static Event continueEvent(String accessToken, Integer eventId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("event", new JsonObject());
        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_CONTINUE_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when continuing event - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when continuing event", e);
            return null;
        } // end try-catch
    } // end cancelEvent()

    /**
     * Connects to server, starts event.
     *
     * @param accessToken
     * @return flag if successful
     */
    public static Event startEvent(String accessToken, Integer eventId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("event", new JsonObject());
        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_START_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when starting event - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when starting event", e);
            return null;
        } // end try-catch
    } // end startEvent()

    /**
     * Connects to server, starts event.
     *
     * @param accessToken
     * @return flag if successful
     */
    public static Event increaseEventTimeLimit(String accessToken, Integer eventId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("time_limit", 30);
        jsonObject.add("event", new JsonObject());
        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_TIME_LIMIT_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when increasing time limit for event - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when increasing time limit for starting event", e);
            return null;
        } // end try-catch
    } // end startEvent()

    /**
     * Connects to server, votes for movies.
     *
     * @param accessToken
     * @param eventId
     * @param ratedMoviesMap
     * @return event
     */
    public static Event voteEventMovies(String accessToken, Integer eventId, HashMap<Movie, Integer> ratedMoviesMap) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("event", new JsonObject());
        JsonArray array = new JsonArray();
        for (Movie movie : ratedMoviesMap.keySet()) {
            JsonObject movieJson = new JsonObject();
            movieJson.addProperty("id", movie.getId());
            movieJson.addProperty("score", ratedMoviesMap.get(movie));
            array.add(movieJson);
        }
        jsonObject.add("rated_movies", array);

        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_VOTE_URI, accessToken);
            Log.i(Constants.TAG, "result:" +  element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when voting event movies - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when voting event movies", e);
            return null;
        } // end try-catch
    } // end voteEventMovies()

    /**
     * Connects to server, votes for movies.
     *
     * @param accessToken
     * @param eventId
     * @param movieId
     * @return event
     */
    public static Event knockoutEventMovies(String accessToken, Integer eventId, Integer movieId, Integer knockoutId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("event", new JsonObject());
        jsonObject.addProperty("movie_id", movieId);
        jsonObject.addProperty("knockout_id", knockoutId);

        Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI + "/" + eventId + EVENTS_KNOCKOUT_URI, accessToken);
            Log.i(Constants.TAG, "result:" +  element.toString());
            if (element != null) {
                GsonBuilder jsonBuilder = new GsonBuilder();
                jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
                Gson gson = jsonBuilder.create();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
                Event event = gson.fromJson(
                        element.getAsJsonObject().get("event"),
                        Event.class
                );
                return event;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when voting event knockout - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when voting event knockout", e);
            return null;
        } // end try-catch
    } // end voteEventMovies()

    /**
     * Connects to server, returns friends.
     *
     * @param accessToken
     * @return list of friends
     */
    public static ArrayList<Friend> getFriends(String accessToken) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        try {
            JsonObject friendsJson =  getWebService(params, FRIENDS_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "FRIENDS RESULT: " + friendsJson.toString());
            if (friendsJson != null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                ArrayList<Friend> friendList = gson.fromJson(
                        friendsJson.get("friends"),
                        new TypeToken<ArrayList<Friend>>() {
                        }.getType());
                return friendList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when getting friends - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when getting friends", e);
            return null;
        } // end try-catch
    } // end getFriends()

    /**
     * Connects to server, confirms friend request.
     *
     * @param accessToken
     * @param friendId
     * @return friend if successful
     */
    public static Friend confirmFriendRequest(String accessToken, Integer friendId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("friend", new JsonObject());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.FRIENDS_URI + "/" + friendId + FRIENDS_CONFIRM_REQUEST_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                Gson gson = new Gson();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("friend").toString());
                Friend friend = gson.fromJson(
                        element.getAsJsonObject().get("friend"),
                        Friend.class
                );
                return friend;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when confirming friend request - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when confirming friend request", e);
            return null;
        } // end try-catch
    } // end confirmFriendRequest()

    /**
     * Connects to server, confirms friend request.
     *
     * @param accessToken
     * @param friendId
     * @return friend if successful
     */
    public static Friend sendFriendRequest(String accessToken, Integer friendId) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add("friend", new JsonObject());

        try {
            JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.FRIENDS_URI + "/" + friendId + FRIENDS_SEND_REQUEST_URI, accessToken);
            Log.i(Constants.TAG, "result:" + element.toString());
            if (element != null) {
                Gson gson = new Gson();

                Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("friend").toString());
                Friend friend = gson.fromJson(
                        element.getAsJsonObject().get("friend"),
                        Friend.class
                );
                return friend;
            } else {
                return null;
            }
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when confirming friend request - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when confirming friend request", e);
            return null;
        } // end try-catch
    } // end sendFriendRequest()

    /**
     * Connects to server, returns friends by search term.
     *
     * @param accessToken
     * @return list of friends
     */
    public static ArrayList<Friend> getFriendsBySearch(String accessToken, String term) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(PARAM_TERM, term));
        try {
            JsonObject friendsJson =  getWebService(params, FRIENDS_SEARCH_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "FRIENDS RESULT: " + friendsJson.toString());
            if (friendsJson != null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                ArrayList<Friend> FriendsList = gson.fromJson(
                        friendsJson.get("friends"),
                        new TypeToken<ArrayList<Friend>>() {
                        }.getType());
                return FriendsList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when searching friends - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when searching friends", e);
            return null;
        } // end try-catch
    } // end getFriendsBySearch()

    /**
     * Connects to server, returns movies by title.
     *
     * @param accessToken
     * @return list of movies
     */
    public static ArrayList<Movie> getMoviesBySearch(String accessToken, String term) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(PARAM_TERM, term));
        try {

            Log.i(Constants.TAG, "MOVIES URI: " + MOVIES_SEARCH_URI);
            JsonObject moviesJson =  getWebService(params, MOVIES_SEARCH_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "MOVIES RESULT: " + moviesJson.toString());
            if (moviesJson != null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                ArrayList<Movie> movieList = gson.fromJson(
                        moviesJson.get("movies"),
                        new TypeToken<ArrayList<Movie>>() {
                        }.getType());
                return movieList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when searching movies - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when searching movies", e);
            return null;
        } // end try-catch
    } // end getMoviesBySearch()

    /**
     * Connects to server, returns movies by title.
     *
     * @param accessToken
     * @return list of movies
     */
    public static ArrayList<Movie> getMoviesFromCollectionSearch(String accessToken, String term) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        if (term != null) {
            params.add(new BasicNameValuePair(PARAM_TERM, term));
        } // end if
        try {

            Log.i(Constants.TAG, "MOVIES URI: " + MOVIES_SEARCH_COLLECTION_URI);
            JsonObject moviesJson =  getWebService(params, MOVIES_SEARCH_COLLECTION_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "MOVIES RESULT: " + moviesJson.toString());
            if (moviesJson != null) {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .setDateFormat("yyyy-MM-dd HH:mm")
                        .create();

                ArrayList<Movie> movieList = gson.fromJson(
                        moviesJson.get("movies"),
                        new TypeToken<ArrayList<Movie>>() {
                        }.getType());
                return movieList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when searching movies - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when searching movies", e);
            return null;
        } // end try-catch
    } // end getMoviesFromCollectionSearch()

    /**
     * Connects to server, returns movie lists by term.
     *
     * @param accessToken
     * @return list of movie lists
     */
    public static ArrayList<MovieList> getMovieListsBySearch(String accessToken, String term) {
        final ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(PARAM_TERM, term));
        try {

            Log.i(Constants.TAG, "MOVIES URI: " + MOVIES_SEARCH_LISTS_URI);
            JsonObject moviesJson =  getWebService(params, MOVIES_SEARCH_LISTS_URI, accessToken).getAsJsonObject();
            Log.i(Constants.TAG, "MOVIES RESULT: " + moviesJson.toString());
            if (moviesJson != null) {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        .create();

                ArrayList<MovieList> movieList = gson.fromJson(
                        moviesJson.get("lists"),
                        new TypeToken<ArrayList<MovieList>>() {
                        }.getType());
                return movieList;
            } else {
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when searching movies - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when searching movies", e);
            return null;
        } // end try-catch
    } // end getMoviesBySearch()

    /**
     * Connects to API server, sends post service.
     *
     * @param params
     * @param url
     * @return server result
     */
    public static String postWebService(ArrayList<NameValuePair> params,
                                        String url, String accessToken) throws APICallException, IOException {
        final HttpResponse resp;
        final HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } // end try-catch
        final HttpPost post = new HttpPost(url);
        post.addHeader(entity.getContentType());
        if (accessToken != null) {
           Log.i(Constants.TAG, "ADD TOKEN");
           post.setHeader("Authorization", "Token token='" + accessToken + "'");
        } // end if
        post.setEntity(entity);
        try {
            resp = getHttpClient().execute(post);
            StringBuilder builder = new StringBuilder();
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = (resp.getEntity() != null) ? resp
                        .getEntity().getContent() : null;
                if (inputStream != null) {
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = inputReader.readLine()) != null) {
                        builder.append(line);
                    } // end while
                    inputReader.close();
                    inputStream.close();
                } // end if
            } else {
                throw new APICallException(String.valueOf(resp.getStatusLine().getStatusCode()));
            } // end if-else
            return builder.toString();
        } catch (final IOException e) {
            throw  e;
        } finally {
        } // end try-catch-finally
    } // end postWebService()

    /**
     * Connects to API server, sends json post service.
     *
     * @param jsonObject
     * @param url
     * @return server result
     */
    public static JsonElement postWebService(JsonObject jsonObject, String url, String accessToken) throws APICallException, IOException {
        final HttpResponse resp;
        final HttpPost post = new HttpPost(url);

        post.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(jsonObject.toString());
        Log.i(Constants.TAG, "JSON entity: " + entity.toString());
        post.setEntity(entity);

        HttpClient client = new DefaultHttpClient();

        if (accessToken != null) {
            Log.i(Constants.TAG, "ADD TOKEN" + accessToken);
            post.setHeader("Authorization", "Token token=" + accessToken );
        } // end if

        for (Header header : post.getAllHeaders()) {
            Log.i(Constants.TAG, "header:" + header);
        }

        try {
            resp = client.execute(post);
            StringBuilder builder = new StringBuilder();
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK
                    || resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED
                    || resp.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                InputStream inputStream = (resp.getEntity() != null) ? resp
                        .getEntity().getContent() : null;
                if (inputStream != null) {
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = inputReader.readLine()) != null) {
                        builder.append(line);
                    } // end while
                    JsonParser parser = new JsonParser();
                    JsonElement jsonElementResponse = parser.parse(builder.toString());
                    inputReader.close();
                    inputStream.close();
                    return jsonElementResponse;
                } else {
                    return null;
                } // end if
            } else {
                throw new APICallException(String.valueOf(resp.getStatusLine().getStatusCode()));
            } // end if-else
        } catch (final IOException e) {
            throw  e;
        } finally {
        } // end try-catch-finally
    } // end postWebService()

    /**
     * Connects to server, sends get service.
     *
     * @param url
     * @return server result as JSON Element
     */
    public static JsonElement getWebService(ArrayList<BasicNameValuePair> params, String url, String accessToken) throws APICallException, IOException {
        StringBuilder builder = new StringBuilder();
        JsonElement jsonElement;
        HttpClient client = new DefaultHttpClient();
        final HttpGet httpGet;

        if (params != null) {
            String paramString = URLEncodedUtils.format(params, "utf-8");
            Log.i(Constants.TAG, "PARAMS " + paramString);
            httpGet = new HttpGet(url + "?" + paramString);
        } else {
           httpGet = new HttpGet(url);
        } // end if

        Log.i(Constants.TAG, "URL " + httpGet.getURI());

        if (accessToken != null) {
            Log.i(Constants.TAG, "ADD TOKEN:" + accessToken);
            httpGet.setHeader("Authorization", "Token token=" + accessToken );
        } // end if
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                } // end while
                JsonParser parser = new JsonParser();
                jsonElement = parser.parse(builder.toString());
                reader.close();
                content.close();
            } else {
                throw new APICallException(String.valueOf(statusCode));
            } // end if-else
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } // end try-catch
        return jsonElement;
    } // end readWebService()
}
