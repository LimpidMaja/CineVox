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
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.adapters.MoviesRateListAdapter;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.EventStatus;
import com.limpidgreen.cinevox.model.VotingRange;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class RateMoviesActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private MoviesRateListAdapter mAdapterMovies;

    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movies);
        mApplication = ((CineVoxApplication) getApplication());

        Integer eventId = null;

        if (savedInstanceState != null) {
            finish();
        } else {
            Bundle bundle = getIntent().getExtras();
            eventId = bundle.getInt(Constants.PARAM_EVENT_ID);
            Log.i(Constants.TAG, "mEvent: " + mEvent);
        } // end if-else

        if (eventId == null) {
            finish();
        }

        mEvent = EventsContentProvider.queryEvent(getContentResolver(), eventId);

        if (mEvent ==  null) {
            finish();
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        if (mApplication.getAPIToken() == null) {
            mApplication.startAuthTokenFetch(this);
        }

        TextView rateText = (TextView) findViewById(R.id.rate_text);
        if (VotingRange.ONE_TO_FIVE.equals(mEvent.getVotingRange())) {
            if (mEvent.getNumVotesPerUser() == 1) {
                rateText.setText("Rate " + mEvent.getNumVotesPerUser() + " a Movie from 1 to 5");
            } else {
                rateText.setText("Rate " + mEvent.getNumVotesPerUser() + " different Movies from 1 to 5");
            }
        } else if (VotingRange.ONE_TO_TEN.equals(mEvent.getVotingRange())) {
            rateText.setText("You can use 10 points to vote on movies");
        }

        ListView listMovies = (ListView) findViewById(R.id.listMoviesRate);
        mAdapterMovies = new MoviesRateListAdapter(mEvent, this);
        listMovies.setAdapter(mAdapterMovies);
    }

    /**
     * Handle Decline button click.
     *
     * @param v view
     */
    public void handleVoteClick(View v) {
        if (VotingRange.ONE_TO_FIVE.equals(mEvent.getVotingRange())) {
            int size = mAdapterMovies.getmRatedMovieMap().size();
            if (size == mEvent.getNumVotesPerUser()) {
                VoteEventEventTask task = new VoteEventEventTask();
                task.execute();
            } else {
                Toast toast = Toast.makeText(this,
                        "You rated only " + size + " Movie/s. You can rate " + (mEvent.getNumVotesPerUser() - size) + " more!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (VotingRange.ONE_TO_TEN.equals(mEvent.getVotingRange())) {
            if (mAdapterMovies.getmPointsUsed() == 10) {
                VoteEventEventTask task = new VoteEventEventTask();
                task.execute();
            } else {
                Toast toast = Toast.makeText(this,
                        "You did not spent all 10 points. You can use " + (10 - mAdapterMovies.getmPointsUsed()) + " more!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void onVoteEventResult(Event event) {
        mEvent = event;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, true);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "You voted Successfully!", Toast.LENGTH_SHORT);
            toast.show();

            if (EventStatus.KNOCKOUT_CHOOSE.equals(mEvent.getEventStatus())) {
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                setResult(RESULT_OK, intent);
                finish();

                Intent intent2 = new Intent(this, MoviesKnockoutActivity.class);
                intent2.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivityForResult(intent2, Constants.EVENT_KNOCKOUT_MOVIE_REQUEST_CODE);
            } else if (EventStatus.WINNER.equals(mEvent.getEventStatus())) {
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                setResult(RESULT_OK, intent);
                finish();

                Intent intent2 = new Intent(this, WinnerActivity.class);
                intent2.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                startActivity(intent2);
            } else {
                Intent intent = new Intent();
                intent.putExtra(Constants.PARAM_EVENT_ID, mEvent.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem voting for movies. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onVoteEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem voting for movies. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create Vote Event Task to send an async call to the server to vote for movies of an event.
     *
     * @author MajaDobnik
     *
     */
    private class VoteEventEventTask extends AsyncTask<Void, Void, Event> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Event doInBackground(Void... value) {
            return NetworkUtil.voteEventMovies(mApplication.getAPIToken(), mEvent.getId(), mAdapterMovies.getmRatedMovieMap());
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Event result) {
            if (result != null) {
                onVoteEventResult(result);
            } else {
                onVoteEventError();
            } // end if
        } // end onPostExecute()
    } // end ConfirmEventTask()

}
