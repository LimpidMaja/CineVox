/**
 * NewEventActivity.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.limpidgreen.cinevox.adapters.FriendsBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.exception.APICallException;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.EventStatus;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.GCMNotificationIntentService;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class EventActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private ContentResolver mResolver;

    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private TextView mWaitingUsers;
    private LinearLayout mConfirm;
    private Button mAddMovies;
    private Button mVote;
    private Button mKnockoutChoose;
    private Button mWinner;

    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = ((CineVoxApplication) getApplication());

        setContentView(R.layout.activity_event);
        Integer eventId = null;

        if (savedInstanceState != null) {
            finish();
        } else {
            Bundle bundle = getIntent().getExtras();
            eventId = bundle.getInt(Constants.PARAM_EVENT_ID);
            Log.i(Constants.TAG, "mEvent: " + mEvent);

            if (bundle.getString(Constants.PARAM_EVENT_CONFIRM_STATUS) != null) {
                if (bundle.getString(Constants.PARAM_EVENT_CONFIRM_STATUS).equals("success")) {
                    Toast toast = Toast.makeText(this,
                            "Event Confirmed Successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (bundle.getString(Constants.PARAM_EVENT_CONFIRM_STATUS).equals("failed")) {
                    Toast toast = Toast.makeText(this,
                            "There was a problem confirming the event. Try again!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } // end if-else

        if (eventId == null) {
            finish();
        }

        mEvent = EventsContentProvider.queryEvent(getContentResolver(), eventId);

        if (mEvent ==  null) {
            finish();
        }

        if (mApplication.getAPIToken() == null) {
            mApplication.startAuthTokenFetch(this);
        }

        TextView eventName = (TextView) findViewById(R.id.event_title);
        TextView eventDescription = (TextView) findViewById(R.id.event_description);
        TextView eventDate = (TextView) findViewById(R.id.event_date);
        TextView eventTime = (TextView) findViewById(R.id.event_time);
        TextView eventPlace = (TextView) findViewById(R.id.event_place);

        mWaitingUsers = (TextView) findViewById(R.id.status_waiting_users);
        mConfirm = (LinearLayout) findViewById(R.id.status_confirm);

        mAddMovies = (Button) findViewById(R.id.status_add_movies);
        mAddMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, AddMoviesToEventActivity.class);
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivity(intent);
            }
        });

        mVote = (Button) findViewById(R.id.status_vote);
        mVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, RateMoviesActivity.class);
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivityForResult(intent, Constants.EVENT_VOTE_MOVIE_REQUEST_CODE);
            }
        });

        mKnockoutChoose = (Button) findViewById(R.id.status_knockout_choose);
        mKnockoutChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, MoviesKnockoutActivity.class);
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivity(intent);
            }
        });

        mWinner = (Button) findViewById(R.id.status_winner);
        mWinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, WinnerActivity.class);
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivity(intent);
            }
        });

        eventName.setText(mEvent.getName());
        eventDescription.setText(mEvent.getDescription());
        eventPlace.setText(mEvent.getPlace());
        eventDate.setText(mEvent.getDate().toString());
        eventTime.setText(mEvent.getTime().toString());

        HListView list = (HListView) findViewById(R.id.listMoviesBar);
        MoviesBarListAdapter adapter = new MoviesBarListAdapter(mEvent.getMovieList(), this);
        list.setAdapter(adapter);

        HListView listFriends = (HListView) findViewById(R.id.listFriendsBar);
        FriendsBarListAdapter adapterFriends = new FriendsBarListAdapter(mEvent.getFriendList(), mEvent.getFriendAcceptedList(), mEvent.getFriendDeclinedList(), this);
        listFriends.setAdapter(adapterFriends);

        updateStatusLayout();
    }

    public void updateStatusLayout() {
        switch (mEvent.getEventStatus()) {
            case WAITING_OTHERS:
                mWaitingUsers.setVisibility(View.VISIBLE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                break;
            case CONFIRM:
                mConfirm.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                break;
            case ADD_MOVIES:
                mAddMovies.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                break;
            case VOTE:
                mVote.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                break;
            case KNOCKOUT_CHOOSE:
                mKnockoutChoose.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                break;
            case WINNER:
                mWinner.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                break;
            case FINISHED:
                break;
            default:
        }
    }

    /**
     * Handle Select Friends button click.
     *
     * @param v view
     */
    public void handleSelectFriends(View v) {
        Intent intent = new Intent(this, SelectFriendsActivity.class);
        startActivity(intent);
    }

    /**
     * Handle Join button click.
     *
     * @param v view
     */
    public void handleJoinEvent(View v) {
        ConfirmEventTask task = new ConfirmEventTask();
        task.execute(true);
    }

    /**
     * Handle Decline button click.
     *
     * @param v view
     */
    public void handleDeclineEvent(View v) {
        ConfirmEventTask task = new ConfirmEventTask();
        task.execute(false);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
    /*@Override
    public void onBackPressed() {
        finish();
    }*/

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
            case Constants.EVENT_VOTE_MOVIE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Integer eventId = data .getIntExtra(Constants.PARAM_EVENT_ID, 0);
                    mEvent = EventsContentProvider.queryEvent(getContentResolver(), eventId);
                    updateStatusLayout();
                } else {
                    this.setResult(Activity.RESULT_CANCELED);
                } // end if-else
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        } // end switch
    } // end onActivityResult()

    public void onConfirmEventResult(Event event) {
        mEvent = event;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, true);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "Event Confirmed Successfully!", Toast.LENGTH_SHORT);
            toast.show();

            updateStatusLayout();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem confirming the event. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onConfirmEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem confirming the event. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create Confirm Event Task to send an async call to the server to confirm an event.
     *
     * @author MajaDobnik
     *
     */
    private class ConfirmEventTask extends AsyncTask<Boolean, Void, Event> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Event doInBackground(Boolean... value) {
            Boolean accept = value[0];
            Log.i(Constants.TAG, "ACCEPT:  "+ accept);
            return NetworkUtil.confirmEventJoin(mApplication.getAPIToken(), mEvent.getId(), accept);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Event result) {
            if (result != null) {
                onConfirmEventResult(result);
            } else {
                onConfirmEventError();
            } // end if
        } // end onPostExecute()
    } // end ConfirmEventTask()
}
