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
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.adapters.EventListAdapter;
import com.limpidgreen.cinevox.adapters.FriendsBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

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
    private EventObserver mObserver;

    private TextView mWaitingUsers;
    private LinearLayout mConfirm;
    private Button mAddMovies;
    private Button mVote;
    private Button mKnockoutChoose;
    private Button mWinner;
    private LinearLayout mStartAnyways;
    private LinearLayout mCancelEvent;

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

        mResolver = getContentResolver();
        mObserver = new EventObserver(false);

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
                startActivityForResult(intent, Constants.EVENT_KNOCKOUT_MOVIE_REQUEST_CODE);
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

        mStartAnyways = (LinearLayout) findViewById(R.id.status_start_anyways);
        mCancelEvent = (LinearLayout) findViewById(R.id.status_cancel_event);

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

    @Override
    protected void onResume() {
        mResolver.registerContentObserver(EventsContentProvider.CONTENT_URI, true, mObserver);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mResolver.unregisterContentObserver(mObserver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mResolver.unregisterContentObserver(mObserver);
        super.onDestroy();
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
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case CONFIRM:
                mConfirm.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case ADD_MOVIES:
                mAddMovies.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case VOTE:
                mVote.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case KNOCKOUT_CHOOSE:
                mKnockoutChoose.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mWinner.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case WINNER:
                mWinner.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case FINISHED:
                mWinner.setVisibility(View.VISIBLE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case DECLINED:
                mWinner.setVisibility(View.GONE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case FAILED:
                mWinner.setVisibility(View.GONE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
                mCancelEvent.setVisibility(View.GONE);
                mStartAnyways.setVisibility(View.GONE);
                break;
            case START_WITHOUT_ALL:
                if (mEvent.getFriendAcceptedList().isEmpty()) {
                    mCancelEvent.setVisibility(View.VISIBLE);
                    mStartAnyways.setVisibility(View.GONE);
                } else {
                    mStartAnyways.setVisibility(View.VISIBLE);
                    mCancelEvent.setVisibility(View.GONE);
                } // end if-else
                mWinner.setVisibility(View.GONE);
                mWaitingUsers.setVisibility(View.GONE);
                mConfirm.setVisibility(View.GONE);
                mAddMovies.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);
                mKnockoutChoose.setVisibility(View.GONE);
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
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        ConfirmEventTask task = new ConfirmEventTask();
        task.execute(true);
    }

    /**
     * Handle Decline button click.
     *
     * @param v view
     */
    public void handleDeclineEvent(View v) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        ConfirmEventTask task = new ConfirmEventTask();
        task.execute(false);
    }

    /**
     * Handle Cancel button click.
     *
     * @param v view
     */
    public void handleCancelEvent(View v) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        CancelEventTask task = new CancelEventTask();
        task.execute();
    }

    /**
     * Handle Wait button click.
     *
     * @param v view
     */
    public void handleWaitUsersEvent(View v) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        WaitEventTask task = new WaitEventTask();
        task.execute();
    }

    /**
     * Handle Start Anyways button click.
     *
     * @param v view
     */
    public void handleStartAnywaysEvent(View v) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        StartAnywaysEventTask task = new StartAnywaysEventTask();
        task.execute();
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
            case Constants.EVENT_KNOCKOUT_MOVIE_REQUEST_CODE:
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

    public void onCancelEventResult(Event event) {
        mEvent = event;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, true);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "Event Cancelled Successfully!", Toast.LENGTH_SHORT);
            toast.show();

            updateStatusLayout();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem cancelling the event. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onCancelEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem cancelling the event. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onStartEventResult(Event event) {
        mEvent = event;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, true);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "Event Started Successfully!", Toast.LENGTH_SHORT);
            toast.show();

            updateStatusLayout();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem starting the event. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onStartEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem starting the event. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onWaitEventResult(Event event) {
        mEvent = event;

        boolean result = EventsContentProvider.insertEvent(getContentResolver(), mEvent, true);
        if (result) {
            Toast toast = Toast.makeText(this,
                    "Event Time limit increased Successfully!", Toast.LENGTH_SHORT);
            toast.show();

            updateStatusLayout();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem increasing time limit for the event. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onWaitEventError() {
        Toast toast = Toast.makeText(this,
                "There was a problem increasing time limit for the event. Try again!", Toast.LENGTH_SHORT);
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

    /**
     * Create Cancel Event Task to send an async call to the server to cancel an event.
     *
     * @author MajaDobnik
     *
     */
    private class CancelEventTask extends AsyncTask<Void, Void, Event> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Event doInBackground(Void... value) {
            return NetworkUtil.cancelEvent(mApplication.getAPIToken(), mEvent.getId());
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Event result) {
            if (result != null) {
                onCancelEventResult(result);
            } else {
                onCancelEventError();
            } // end if
        } // end onPostExecute()
    } // end CancelEventTask()

    /**
     * Create Start Anyways Event Task to send an async call to the server to start an event.
     *
     * @author MajaDobnik
     *
     */
    private class StartAnywaysEventTask extends AsyncTask<Void, Void, Event> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Event doInBackground(Void... value) {
            return NetworkUtil.startEvent(mApplication.getAPIToken(), mEvent.getId());
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Event result) {
            if (result != null) {
                onStartEventResult(result);
            } else {
                onStartEventError();
            } // end if
        } // end onPostExecute()
    } // end StartAnywaysEventTask()

    /**
     * Create Wait Event Task to send an async call to the server to increase time limit of an event.
     *
     * @author MajaDobnik
     *
     */
    private class WaitEventTask extends AsyncTask<Void, Void, Event> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Event doInBackground(Void... value) {
            return NetworkUtil.increaseEventTimeLimit(mApplication.getAPIToken(), mEvent.getId());
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Event result) {
            if (result != null) {
                onWaitEventResult(result);
            } else {
                onWaitEventError();
            } // end if
        } // end onPostExecute()
    } // end StartAnywaysEventTask()

    public class EventObserver extends ContentObserver {
        boolean selfChange;

        public EventObserver(boolean selfChange) {
            super(new Handler());
            this.selfChange = selfChange;
        }

        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         * This method signature is provided for compatibility with
         * older platforms.
         */
        @Override
        public void onChange(boolean selfChange) {
            /*
             * Invoke the method signature available as of
             * Android platform version 4.1, with a null URI.
             */
            onChange(selfChange, null);
        }
        /*
         * Define a method that's called when data in the
         * observed content provider changes.
         */
        @Override
        public void onChange(boolean selfChange, Uri changeUri) {
            Log.i(Constants.TAG, "ON CHANGE!");
            mEvent = EventsContentProvider.queryEvent(getContentResolver(), mEvent.getId());
            updateStatusLayout();
        }
    }
}
