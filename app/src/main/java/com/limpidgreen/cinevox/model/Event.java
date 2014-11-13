/**
 * Event.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.util.Constants;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Event class.
 *
 * @author MajaDobnik
 *
 */
public class Event implements Serializable {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String description;
    @SerializedName("event_date")
    private Date date;
    @SerializedName("event_time")
    private Time time;
    @Expose
    private Integer timeLimit;
    @Expose
    private String place;
    @Expose
    private Integer minimumVotingPercent;
    @Expose
    private Integer userId;
    @Expose
    private Boolean finished;
    @Expose
    private Boolean usersCanAddMovies;
    @Expose
    private Integer numAddMoviesByUser;
    @Expose
    private RatingSystem ratingSystem;
    @Expose
    private Integer numVotesPerUser;
    @Expose
    private VotingRange votingRange;
    @Expose
    private Boolean tieKnockout;
    @Expose
    private Integer knockoutRounds;
    @Expose
    private Integer knockoutTimeLimit;
    @Expose
    private Boolean waitTimeLimit;
    @Expose
    private Date updatedAt;
    @Expose
    private Date createdAt;
    @Expose
    private RatingPhase ratingPhase;
    @Expose
    private Integer knockoutPhase;
    @Expose
    private EventStatus eventStatus;

    private ArrayList<Friend> friendList;

    private ArrayList<Friend> friendAcceptedList;

    private ArrayList<Friend> friendDeclinedList;

    private ArrayList<Movie> movieList;

    private Movie winner;

    private Knockout knockout;

    public Event() {
        super();
    }

    public Event(Date date, Time time) {
        this.date = date;
        this.time = time;
        this.timeLimit = 120;
        this.minimumVotingPercent = 100;
        this.finished = false;
        this.usersCanAddMovies = false;
        this.numAddMoviesByUser = 0;
        this.ratingSystem = RatingSystem.VOTING;
        this.numVotesPerUser = 2;
        this.votingRange = VotingRange.ONE_TO_FIVE;
        this.tieKnockout = true;
        this.knockoutRounds = 4;
        this.knockoutTimeLimit = 15;
        this.waitTimeLimit = true;
        this.knockoutPhase = 0;
        this.friendList = new ArrayList<Friend>();
        this.friendAcceptedList = new ArrayList<Friend>();
        this.friendDeclinedList = new ArrayList<Friend>();
        this.movieList = new ArrayList<Movie>();
        this.winner = null;
        this.knockout = null;
    }

    public Event(Integer id, String name, String description,
            Date date,
            Time time,
            Integer timeLimit,
            String place,
            Integer minimumVotingPercent,
            Integer userId,
             Boolean finished,
             Boolean usersCanAddMovies,
             Integer numAddMoviesByUser,
             RatingSystem ratingSystem,
             Integer numVotesPerUser,
             VotingRange votingRange,
             Boolean tieKnockout,
             Integer knockoutRounds,
             Integer knockoutTimeLimit,
             Boolean waitTimeLimit,
             Date updatedAt,
             Date createdAt,
             RatingPhase ratingPhase,
             Integer knockoutPhase,
             EventStatus eventStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.timeLimit = timeLimit;
        this.place = place;
        this.minimumVotingPercent = minimumVotingPercent;
        this.userId = userId;
        this.finished = finished;
        this.usersCanAddMovies = usersCanAddMovies;
        this.numAddMoviesByUser = numAddMoviesByUser;
        this.ratingSystem = ratingSystem;
        this.numVotesPerUser = numVotesPerUser;
        this.votingRange = votingRange;
        this.tieKnockout = tieKnockout;
        this.knockoutRounds = knockoutRounds;
        this.knockoutTimeLimit = knockoutTimeLimit;
        this.waitTimeLimit = waitTimeLimit;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.ratingPhase = ratingPhase;
        this.knockoutPhase = knockoutPhase;
        this.eventStatus = eventStatus;
        this.movieList = new ArrayList<Movie>();
        this.friendList = new ArrayList<Friend>();
        this.friendAcceptedList = new ArrayList<Friend>();
        this.friendDeclinedList = new ArrayList<Friend>();
        this.winner = null;
        this.knockout = null;
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

        values.put(CineVoxDBHelper.EVENTS_COL_ID, id);
        values.put(CineVoxDBHelper.EVENTS_COL_NAME, name);
        values.put(CineVoxDBHelper.EVENTS_COL_DESCRIPTION , description);
        values.put(CineVoxDBHelper.EVENTS_COL_DATE, date.toString());
        values.put(CineVoxDBHelper.EVENTS_COL_TIME, time.toString());
        values.put(CineVoxDBHelper.EVENTS_COL_TIME_LIMIT, timeLimit);
        values.put(CineVoxDBHelper.EVENTS_COL_PLACE, place);
        values.put(CineVoxDBHelper.EVENTS_COL_MINIMUM_VOTING_PERCENT, minimumVotingPercent);
        values.put(CineVoxDBHelper.EVENTS_COL_USER_ID, userId);
        values.put(CineVoxDBHelper.EVENTS_COL_FINISHED, finished);
        values.put(CineVoxDBHelper.EVENTS_COL_USERS_CAN_ADD_MOVIES, usersCanAddMovies);
        values.put(CineVoxDBHelper.EVENTS_COL_NUM_ADD_MOVIES_BY_USER, numAddMoviesByUser);
        values.put(CineVoxDBHelper.EVENTS_COL_RATING_SYSTEM, ratingSystem.ordinal());
        values.put(CineVoxDBHelper.EVENTS_COL_NUM_VOTES_PER_USER, numVotesPerUser);
        values.put(CineVoxDBHelper.EVENTS_COL_VOTING_RANGE, votingRange.ordinal());
        values.put(CineVoxDBHelper.EVENTS_COL_TIE_KNOCKOUT, tieKnockout);
        values.put(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_ROUNDS, knockoutRounds);
        values.put(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_TIME_LIMIT, knockoutTimeLimit);
        values.put(CineVoxDBHelper.EVENTS_COL_WAIT_TIME_LIMIT, waitTimeLimit);
        values.put(CineVoxDBHelper.EVENTS_COL_UPDATED_AT, updatedAt.toString());
        values.put(CineVoxDBHelper.EVENTS_COL_CRATED_AT, createdAt.toString());
        values.put(CineVoxDBHelper.EVENTS_COL_RATING_PHASE, ratingPhase.ordinal());
        values.put(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_PHASE, knockoutRounds);
        values.put(CineVoxDBHelper.EVENTS_COL_EVENT_STATUS, eventStatus.ordinal());
        return values;
    }

    // Create a Event object from a cursor
    public static Event fromCursor(Cursor curEvent) {
        Integer id = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_ID));
        String name = curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_NAME));
        String description = curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_DESCRIPTION));

        Date eventDate = null;
        Time eventTime = null;
        Date updatedAt = null;
        Date createdAt = null;
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            eventDate =  df.parse(curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_DATE)));
            eventTime = Time.valueOf(curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_TIME)));
            updatedAt = df.parse(curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_UPDATED_AT)));
            createdAt = df.parse(curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_CRATED_AT)));
        } catch (ParseException e) {
            e.printStackTrace();
        } // end try-catch
        Integer timeLimit = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_TIME_LIMIT));
        String place = curEvent.getString(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_PLACE));
        Integer minimumVotingPercent = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_MINIMUM_VOTING_PERCENT));
        Integer userId = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_USER_ID));
        Boolean finished = (curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_FINISHED)) == 1)? true : false;
        Boolean usersCanAddMovies = (curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_USERS_CAN_ADD_MOVIES)) == 1)? true : false;
        Integer numAddMoviesByUser = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_NUM_ADD_MOVIES_BY_USER));
        RatingSystem ratingSystem = RatingSystem.fromInteger(curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_RATING_SYSTEM)));
        Integer numVotesPerUser = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_NUM_VOTES_PER_USER));
        VotingRange votingRange = VotingRange.fromInteger(curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_VOTING_RANGE)));
        Boolean tieKnockout = (curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_TIE_KNOCKOUT)) == 1)? true : false;
        Integer knockoutRounds = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_ROUNDS));
        Integer knockoutTimeLimit = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_TIME_LIMIT));
        Boolean waitTimeLimit = (curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_WAIT_TIME_LIMIT)) == 1)? true : false;
        RatingPhase ratingPhase = RatingPhase.fromInteger(curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_RATING_PHASE)));
        Integer knockoutPhase = curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_KNOCKOUT_PHASE));
        EventStatus eventStatus = EventStatus.fromInteger(curEvent.getInt(curEvent.getColumnIndex(CineVoxDBHelper.EVENTS_COL_EVENT_STATUS)));

        return new Event(id, name, description, eventDate, eventTime, timeLimit,place, minimumVotingPercent, userId, finished, usersCanAddMovies, numAddMoviesByUser, ratingSystem, numVotesPerUser, votingRange, tieKnockout, knockoutRounds, knockoutTimeLimit, waitTimeLimit, updatedAt, createdAt, ratingPhase, knockoutPhase, eventStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || !(o instanceof Event)) return false;

        Event event = (Event) o;

        if (!name.equals(event.name)) return false;
        if (!id.equals(event.id)) return false;
        if (!updatedAt.equals(event.updatedAt)) return false;

        Log.i(Constants.TAG, "eventStatus: " + eventStatus + ", es2:" + event.eventStatus);
        if (eventStatus != null ? (eventStatus.ordinal() != event.eventStatus.ordinal()) : event.eventStatus != null) return false;

        if (winner != null ? !winner.getId().equals(event.winner != null ? event.winner.getId() : null) : event.winner != null)
            return false;

        if (knockout != null ? !knockout.getId().equals(event.knockout != null ? event.knockout.getId() : null) : event.knockout != null)
            return false;

        if (friendList.size() != event.friendList.size()) return false;
        for (Friend friend : friendList) {
            if (!event.friendList.contains(friend)) return false;
        } // end for

        if (friendAcceptedList.size() != event.friendAcceptedList.size()) return false;
        for (Friend friend : friendAcceptedList) {
            if (!event.friendAcceptedList.contains(friend)) return false;
        } // end for

        if (friendDeclinedList.size() != event.friendDeclinedList.size()) return false;
        for (Friend friend : friendDeclinedList) {
            if (!event.friendDeclinedList.contains(friend)) return false;
        } // end for

        if (movieList.size() != event.movieList.size()) return false;
        for (Movie movie : movieList) {
            if (!event.movieList.contains(movie)) return false;
        } // end for

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", timeLimit=" + timeLimit +
                ", place='" + place + '\'' +
                ", minimumVotingPercent=" + minimumVotingPercent +
                ", userId=" + userId +
                ", finished=" + finished +
                ", usersCanAddMovies=" + usersCanAddMovies +
                ", numAddMoviesByUser=" + numAddMoviesByUser +
                ", ratingSystem=" + ratingSystem +
                ", numVotesPerUser=" + numVotesPerUser +
                ", votingRange=" + votingRange +
                ", tieKnockout=" + tieKnockout +
                ", knockoutRounds=" + knockoutRounds +
                ", knockoutTimeLimit=" + knockoutTimeLimit +
                ", waitTimeLimit=" + waitTimeLimit +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", ratingPhase=" + ratingPhase +
                ", knockoutPhase=" + knockoutPhase +
                ", eventStatus=" + eventStatus +
                ", winner=" + winner +
                ", friendList=" + friendList +
                ", friendAcceptedList=" + friendAcceptedList +
                ", friendDeclinedList=" + friendDeclinedList +
                ", movieList=" + movieList +
                '}';
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getMinimumVotingPercent() {
        return minimumVotingPercent;
    }

    public void setMinimumVotingPercent(Integer minimumVotingPercent) {
        this.minimumVotingPercent = minimumVotingPercent;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Boolean getUsersCanAddMovies() {
        return usersCanAddMovies;
    }

    public void setUsersCanAddMovies(Boolean usersCanAddMovies) {
        this.usersCanAddMovies = usersCanAddMovies;
    }

    public Integer getNumAddMoviesByUser() {
        return numAddMoviesByUser;
    }

    public void setNumAddMoviesByUser(Integer numAddMoviesByUser) {
        this.numAddMoviesByUser = numAddMoviesByUser;
    }

    public Integer getNumVotesPerUser() {
        return numVotesPerUser;
    }

    public void setNumVotesPerUser(Integer numVotesPerUser) {
        this.numVotesPerUser = numVotesPerUser;
    }

    public Boolean getTieKnockout() {
        return tieKnockout;
    }

    public void setTieKnockout(Boolean tieKnockout) {
        this.tieKnockout = tieKnockout;
    }

    public Integer getKnockoutRounds() {
        return knockoutRounds;
    }

    public void setKnockoutRounds(Integer knockoutRounds) {
        this.knockoutRounds = knockoutRounds;
    }

    public Integer getKnockoutTimeLimit() {
        return knockoutTimeLimit;
    }

    public void setKnockoutTimeLimit(Integer knockoutTimeLimit) {
        this.knockoutTimeLimit = knockoutTimeLimit;
    }

    public Boolean getWaitTimeLimit() {
        return waitTimeLimit;
    }

    public void setWaitTimeLimit(Boolean waitTimeLimit) {
        this.waitTimeLimit = waitTimeLimit;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getKnockoutPhase() {
        return knockoutPhase;
    }

    public void setKnockoutPhase(Integer knockoutPhase) {
        this.knockoutPhase = knockoutPhase;
    }

    public RatingSystem getRatingSystem() {
        return ratingSystem;
    }

    public void setRatingSystem(RatingSystem ratingSystem) {
        this.ratingSystem = ratingSystem;
    }

    public VotingRange getVotingRange() {
        return votingRange;
    }

    public void setVotingRange(VotingRange votingRange) {
        this.votingRange = votingRange;
    }

    public RatingPhase getRatingPhase() {
        return ratingPhase;
    }

    public void setRatingPhase(RatingPhase ratingPhase) {
        this.ratingPhase = ratingPhase;
    }

    public ArrayList<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<Friend> friendList) {
        this.friendList = friendList;
    }

    public ArrayList<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(ArrayList<Movie> movieList) {
        this.movieList = movieList;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public ArrayList<Friend> getFriendAcceptedList() {
        return friendAcceptedList;
    }

    public void setFriendAcceptedList(ArrayList<Friend> friendAcceptedList) {
        this.friendAcceptedList = friendAcceptedList;
    }

    public ArrayList<Friend> getFriendDeclinedList() {
        return friendDeclinedList;
    }

    public void setFriendDeclinedList(ArrayList<Friend> friendDeclinedList) {
        this.friendDeclinedList = friendDeclinedList;
    }

    public Movie getWinner() {
        return winner;
    }

    public void setWinner(Movie winner) {
        this.winner = winner;
    }

    public Knockout getKnockout() {
        return knockout;
    }

    public void setKnockout(Knockout knockout) {
        this.knockout = knockout;
    }

    public static class EventDeserializer implements JsonDeserializer<Event> {
        @Override
        public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").excludeFieldsWithoutExposeAnnotation()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            Event event = gson.fromJson(json, Event.class);

            //Log.i(Constants.TAG, "JSON DESERIALIZE OBJECT:" +  event.toString());
            String a = json.getAsJsonObject().get("event_date").getAsString();
            String b = json.getAsJsonObject().get("event_time").getAsString();

            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfDateWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            Integer winnerMovieId = !json.getAsJsonObject().get("winner_movie").isJsonNull() ? json.getAsJsonObject().get("winner_movie").getAsInt() : null;

            JsonArray movies = json.getAsJsonObject().get("movies").getAsJsonArray();
            ArrayList<Movie> movieList = new ArrayList<Movie>(movies.size());
            for (JsonElement movieJson : movies) {
                Movie movie = new Gson().fromJson(movieJson, Movie.class);
                //Log.i(Constants.TAG, "MOVIES OBJECT:" + movie.toString());
                movieList.add(movie);

                if (movie.getId().equals(winnerMovieId)) {
                    Log.i(Constants.TAG, "MOVIES WINNER:" +  movie);
                    event.setWinner(movie);
                } // end if
            } // end for

            event.setMovieList(movieList);

            JsonObject jsonKnockoutObject = !json.getAsJsonObject().get("knockout_matches").isJsonNull() ? json.getAsJsonObject().get("knockout_matches").getAsJsonObject() : null;
            if (jsonKnockoutObject != null) {
                Log.i(Constants.TAG, "KNOCKOUT:" + jsonKnockoutObject.toString());
                Integer movie1Id = jsonKnockoutObject.get("movie_id_1").getAsInt();
                Integer movie2Id = jsonKnockoutObject.get("movie_id_2").getAsInt();

                Movie movie1 = null;
                Movie movie2 = null;
                for (Movie movie : event.getMovieList()) {
                    if (movie.getId().equals(movie1Id)) {
                        movie1 = movie;
                    }
                    if (movie.getId().equals(movie2Id)) {
                        movie2 = movie;
                    } // end if
                } // end for

                Knockout knockout = new Knockout(jsonKnockoutObject.get("id").getAsInt(), movie1, movie2, jsonKnockoutObject.get("round").getAsInt());
                event.setKnockout(knockout);
                Log.i(Constants.TAG, "KNOCKOUT:" + knockout);
            } // end if

            JsonArray friends = json.getAsJsonObject().get("friends").getAsJsonArray();
            ArrayList<Friend> friendList = new ArrayList<Friend>();
            ArrayList<Friend> friendAcceptedList = new ArrayList<Friend>();
            ArrayList<Friend> friendDeclinedList = new ArrayList<Friend>();
            //Log.i(Constants.TAG, "JSON FRIENDS OBJECT:" + friends.toString());
            for (JsonElement friendJson : friends) {
                Friend friend = new Gson().fromJson(friendJson, Friend.class);
                friend.setFacebookUID(friendJson.getAsJsonObject().get("fb_uid").getAsString());
                String eventAccepted = friendJson.getAsJsonObject().get("event_accepted").getAsString();

                if (eventAccepted.equals("waiting")) {
                    friendList.add(friend);
                } else if (eventAccepted.equals("accepted")) {
                    friendAcceptedList.add(friend);
                } else if (eventAccepted.equals("declined")) {
                    friendDeclinedList.add(friend);
                } // end if-else

                //Log.i(Constants.TAG, "FRIEND OBJECT:" + friend.toString());
            } // end for

            event.setFriendList(friendList);
            event.setFriendAcceptedList(friendAcceptedList);
            event.setFriendDeclinedList(friendDeclinedList);

            Date date;
            Time time;
            try {
                date = sdfDate.parse(a);
                Log.i(Constants.TAG, "DATE: " + date);
                event.setDate(date);

                Date t = sdfDateWithTime.parse(b);
                time = new Time(t.getTime());
                event.setTime(time);
                Log.i(Constants.TAG, "time: " + time);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } // end try-catch


            Log.i(Constants.TAG, "EVENT: " + event.toString());
            return event;
        }
    }

    public static class EventSerializer implements JsonSerializer<Event> {
        @Override
        public JsonElement serialize(Event src, Type typeOfSrc, JsonSerializationContext context) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("event", gson.toJsonTree(src));
            jsonObject.getAsJsonObject("event").addProperty("event_time", src.getTime().toString());
            jsonObject.getAsJsonObject("event").addProperty("event_date", src.getDate().toString());

            return jsonObject;
        }

    }
}
