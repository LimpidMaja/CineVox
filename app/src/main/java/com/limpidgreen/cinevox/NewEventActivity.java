/**
 * NewEventActivity.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.limpidgreen.cinevox.adapters.FriendsBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.exception.APICallException;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.sephiroth.android.library.widget.HListView;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class NewEventActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private Event mEvent;

    private EditText mTitleEdit;
    private EditText mDescriptionEdit;
    private EditText mPlaceEdit;
    private Button mDatePickerButton;
    private Button mTimePickerButton;
    private MoviesBarListAdapter mAdapterMovies;
    private FriendsBarListAdapter mAdapterFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = ((CineVoxApplication) getApplication());

        setContentView(R.layout.activity_new_event);

        mTitleEdit = (EditText) findViewById(R.id.eventNameEdit);
        mDescriptionEdit = (EditText) findViewById(R.id.eventDescriptionEdit);
        mPlaceEdit = (EditText) findViewById(R.id.eventPlaceEdit);
        mDatePickerButton = (Button) findViewById(R.id.eventDate);
        mTimePickerButton = (Button) findViewById(R.id.eventTime);

        Calendar mCurrentTime = Calendar.getInstance();
        int date = mCurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mCurrentTime.get(Calendar.MONTH);
        int year = mCurrentTime.get(Calendar.YEAR);
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        mDatePickerButton.setText(date + "." + (month + 1) + "." + year);
        mTimePickerButton.setText(hour + 1 + ":00");

        Date eventDate = mCurrentTime.getTime();
        Time eventTime = Time.valueOf((hour + 1) + ":00:00");
        mEvent = new Event(eventDate, eventTime);

        HListView list = (HListView) findViewById(R.id.listMoviesBar);
        mAdapterMovies = new MoviesBarListAdapter(mEvent.getMovieList(), this);
        list.setAdapter(mAdapterMovies);

        HListView listFriends = (HListView) findViewById(R.id.listFriendsBar);
        mAdapterFriends = new FriendsBarListAdapter(mEvent.getFriendList(), mEvent.getFriendAcceptedList(), mEvent.getFriendDeclinedList(), this);
        listFriends.setAdapter(mAdapterFriends);
    }

    /**
     * Handle Date Picker button click.
     *
     * @param v view
     */
    public void showDatePickerDialog(View v) {
        Calendar mcCurrentTime = Calendar.getInstance();
        int date = mcCurrentTime.get(Calendar.DAY_OF_MONTH);
        int month = mcCurrentTime.get(Calendar.MONTH);
        int year = mcCurrentTime.get(Calendar.YEAR);
        final DatePickerDialog mDatePicker;

        mDatePicker = new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat formatter= new SimpleDateFormat( "yyyy-MM-dd");
                try {
                    Date eventDate = formatter.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    mEvent.setDate(eventDate);
                    mDatePickerButton.setText( dayOfMonth + "." + (monthOfYear + 1) + "." + year);
                    Log.i(Constants.TAG, "EVENT: " + mEvent.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, date);

        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    /**
     * Handle Time Picker button click.
     *
     * @param v view
     */
    public void showTimePickerDialog(View v) {
        Calendar mcCurrentTime = Calendar.getInstance();
        int hour = mcCurrentTime.get(Calendar.HOUR_OF_DAY);
        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Time eventTime = Time.valueOf(selectedHour + ":" + selectedMinute + ":00");
                mEvent.setTime(eventTime);

                if (selectedMinute < 10) {
                    mTimePickerButton.setText(selectedHour + ":0" + selectedMinute);
                } else {
                    mTimePickerButton.setText(selectedHour + ":" + selectedMinute);
                } // end if-else
                Log.i(Constants.TAG, "EVENT: " + mEvent.toString());
            }
        }, hour + 1, 0, true);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    /**
     * Handle Select Friends button click.
     *
     * @param v view
     */
    public void handleSelectFriends(View v) {
        Intent intent = new Intent(this, SelectFriendsActivity.class);
        intent.putParcelableArrayListExtra(Constants.PARAM_FRIEND_LIST, mEvent.getFriendList());
        startActivityForResult(intent, Constants.EVENT_FRIEND_SELECT_REQUEST_CODE);
    }

    /**
     * Handle Select Movies button click.
     *
     * @param v view
     */
    public void handleSelectMovies(View v) {
        Intent intent = new Intent(this, SelectMoviesActivity.class);
        intent.putParcelableArrayListExtra(Constants.PARAM_MOVIE_LIST, mEvent.getMovieList());
        startActivityForResult(intent, Constants.EVENT_MOVIE_SELECT_REQUEST_CODE);
    }

    /**
     * Handle Edit Rating button click.
     *
     * @param v view
     */
    public void handleEditVoting(View v) {
        Intent intent = new Intent(this, EditVotingActivity.class);
        startActivity(intent);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.EVENT_FRIEND_SELECT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<Friend> friends = data
                            .getParcelableArrayListExtra(Constants.PARAM_FRIEND_LIST);
                    mEvent.setFriendList(friends);
                    mAdapterFriends.updateList(mEvent.getFriendList(), mEvent.getFriendAcceptedList(), mEvent.getFriendDeclinedList());
                    Log.i(Constants.TAG, "SELECTED FRIEND: " + friends);
                } else {
                    this.setResult(Activity.RESULT_CANCELED);
                } // end if-else
                break;
            case Constants.EVENT_MOVIE_SELECT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<Movie> movies = data
                            .getParcelableArrayListExtra(Constants.PARAM_MOVIE_LIST);
                    mEvent.setMovieList(movies);
                    mAdapterMovies.updateList(mEvent.getMovieList());
                    Log.i(Constants.TAG, "SELECTED MOVIES: " + movies);
                } else {
                    this.setResult(Activity.RESULT_CANCELED);
                } // end if-else
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        } // end switch
    } // end onActivityResult()

    /**
     * Handle Create Event button click.
     *
     * @param v view
     */
    public void handleCreateEventClick(View v) {
        if (!TextUtils.isEmpty(mTitleEdit.getText().toString()) &&
                !TextUtils.isEmpty(mDescriptionEdit.getText().toString())
                && !TextUtils.isEmpty(mPlaceEdit.getText().toString())) {
            mEvent.setName(mTitleEdit.getText().toString());
            mEvent.setDescription(mDescriptionEdit.getText().toString());
            mEvent.setPlace(mPlaceEdit.getText().toString());

            if (mEvent.getFriendList().isEmpty()) {
                Toast toast = Toast.makeText(this,
                        "You did not invite any friends!",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (mEvent.getMovieList().isEmpty()) {
                Toast toast = Toast.makeText(this,
                        "You did not add any movies!",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            new CreateEventTask().execute();
        } else {
            Toast toast = Toast.makeText(this,
                    "Missing fields!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onCreateEventResult(JsonElement element) {
        GsonBuilder jsonBuilder = new GsonBuilder();
        jsonBuilder.registerTypeAdapter(Event.class, new Event.EventDeserializer());
        Gson gson = jsonBuilder.create();

        Log.i(Constants.TAG, "RETURNED : jsonObject:" +  element.getAsJsonObject().get("event").toString());
        Event eventDB = gson.fromJson(
                element.getAsJsonObject().get("event"),
                Event.class
        );
        mEvent = eventDB;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, false);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "Event Added Successfully!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem creating the event. Try again!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    public void onCreateEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem creating the event. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create Event Task to send an async call to the server to create an event.
     *
     * @author MajaDobnik
     *
     */
    private class CreateEventTask extends AsyncTask<Void, Void, JsonElement> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected JsonElement doInBackground(Void... value) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Event.class, new Event.EventSerializer());
            Gson gson = builder.create();

            JsonObject jsonObject = gson.toJsonTree(mEvent).getAsJsonObject();

            JsonArray moviesJson = new JsonArray();
            for (Movie movie : mEvent.getMovieList()) {
                moviesJson.add(new JsonPrimitive(movie.getId()));
            }
            jsonObject.add("movies", moviesJson);

            JsonArray friendsJson = new JsonArray();
            for (Friend friend : mEvent.getFriendList()) {
                friendsJson.add(new JsonPrimitive(friend.getId()));
            }
            jsonObject.add("friends", friendsJson);

            Log.i(Constants.TAG, "jsonObject:" +  jsonObject.toString());
            try {
                JsonElement element = NetworkUtil.postWebService(jsonObject, NetworkUtil.EVENTS_URI, mApplication.getAPIToken());

                if (element != null) {
                    return element;
                } else {
                    return null;
                }
            } catch (APICallException e) {
                Log.e(Constants.TAG, "HTTP ERROR when getting events - STATUS:" +  e.getMessage(), e);
                return null;
            } catch (IOException e) {
                Log.e(Constants.TAG, "IOException when getting events", e);
                return null;
            } // end try-catch
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(JsonElement result) {
            if (result != null) {
                onCreateEventResult(result);
            } else {
                onCreateEventError();
            } // end if
        } // end onPostExecute()
    } // end DirectionsTask()
}
