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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.EventStatus;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class MoviesKnockoutActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;
    private Event mEvent;
    private Integer selectedMovieId;
    private ProgressDialog mProgressDialog = null;
    private VoteEventEventTask mVoteEventEventTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_knockout);

        mApplication = ((CineVoxApplication) getApplication());

        Integer eventId = null;

        if (savedInstanceState != null) {
            finish();
        } else {
            Bundle bundle = getIntent().getExtras();
            eventId = bundle.getInt(Constants.PARAM_EVENT_ID);
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

        if (mEvent ==  null || mEvent.getKnockout() == null) {
            finish();
        }

        if (mApplication.getAPIToken() == null) {
            mApplication.startAuthTokenFetch(this);
        }

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mEvent.getId());

        TextView roundTxt = (TextView) findViewById(R.id.round_txt);
        TextView title1 = (TextView) findViewById(R.id.movie_1_title);
        TextView title2 = (TextView) findViewById(R.id.movie_2_title);
        ImageView moviePoster1 = (ImageView) findViewById(R.id.movie_1_poster);
        ImageView moviePoster2 = (ImageView) findViewById(R.id.movie_2_poster);

        if (mEvent.getKnockout().getRound() == 1) {
            roundTxt.setText("Final");
        } else if (mEvent.getKnockout().getRound() == 2) {
            roundTxt.setText("Semi-Final");
        } else if (mEvent.getKnockout().getRound() == 4) {
            roundTxt.setText("Quarter-Final");
        } else if (mEvent.getKnockout().getRound() == 8) {
            roundTxt.setText("Eighth-Final");
        } else {
            roundTxt.setText(mEvent.getKnockout().getRound() + "th-Final");
        } // end if-else

        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(android.R.drawable.ic_menu_crop)
                .showImageForEmptyUri(android.R.drawable.ic_menu_crop)
                .showImageOnFail(android.R.drawable.ic_menu_crop)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        if (mEvent.getKnockout().getMovie1().getPoster().contains("original")) {
            ImageLoader.getInstance().displayImage(mEvent.getKnockout().getMovie1().getPoster().replace("original", "w92"), moviePoster1, mOptions);
        } else {
            ImageLoader.getInstance().displayImage(mEvent.getKnockout().getMovie1().getPoster(), moviePoster1, mOptions);
        } // end if-else
        title1.setText(mEvent.getKnockout().getMovie1().getTitle());

        if (mEvent.getKnockout().getMovie2().getPoster().contains("original")) {
            ImageLoader.getInstance().displayImage(mEvent.getKnockout().getMovie2().getPoster().replace("original", "w92"), moviePoster2, mOptions);
        } else {
            ImageLoader.getInstance().displayImage(mEvent.getKnockout().getMovie2().getPoster(), moviePoster2, mOptions);
        } // end if-else
        title2.setText(mEvent.getKnockout().getMovie2().getTitle());


        final TextView vote1 = (TextView) findViewById(R.id.vote_1);
        final TextView vote2 = (TextView) findViewById(R.id.vote_2);

        vote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovieId = mEvent.getKnockout().getMovie1().getId();

                vote1.setBackgroundColor(Color.parseColor("#ffa172f1"));
                vote2.setBackgroundColor(Color.parseColor("#ff877ac3"));
            }
        });

        vote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMovieId = mEvent.getKnockout().getMovie2().getId();
                vote2.setBackgroundColor(Color.parseColor("#ffa172f1"));
                vote1.setBackgroundColor(Color.parseColor("#ff877ac3"));
            }
        });

    }

    /**
     * Handle Done button click.
     *
     * @param v view
     */
    public void handleKnockoutDone(View v) {
        if (selectedMovieId != null) {
            if (mVoteEventEventTask == null) {
                showProgress("Submitting Vote");
                mVoteEventEventTask = new VoteEventEventTask();
                mVoteEventEventTask.execute();
            }
        } else {
            Toast toast = Toast.makeText(this,
                    "Choose a movie!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress(String msg) {
        mProgressDialog = ProgressDialog.show(this, null,
                msg, true, true, null);
    } // end showProgress()

    /**
     * Hides the progress UI for a lengthy operation.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        } // end if
    } // end hideProgress()

    public void onVoteEventResult(Event event) {
        mVoteEventEventTask = null;
        hideProgress();
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
                    "There was a problem voting for the movie. Try again!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onVoteEventError() {
        mVoteEventEventTask = null;
        hideProgress();
        Toast toast = Toast.makeText(this,
                "There was a problem voting for the movie. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create Vote Event Task to send an async call to the server to vote for the knockout movie of an event.
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
            return NetworkUtil.knockoutEventMovies(mApplication.getAPIToken(), mEvent.getId(), selectedMovieId, mEvent.getKnockout().getId());
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
    } // end VoteEventEventTask()

}
