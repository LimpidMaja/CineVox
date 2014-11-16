/**
 * SelectFriendsActivity.java
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesSelectListAdapter;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.util.ArrayList;

import it.sephiroth.android.library.widget.HListView;

/**
 * Select Friends Activity.
 *
 * @author MajaDobnik
 *
 */
public class SelectMoviesActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private SearchMoviesAsyncTask mSearchMoviesAsyncTask;
    private SearchListsAsyncTask mSearchListsAsyncTask;

    private ArrayList<Movie> mSelectedMovies;

    private MoviesBarListAdapter adapter;

    private MoviesSelectListAdapter adapterManual;
    private MoviesSelectListAdapter adapterList;

    private EditText mSearchManually;
    private EditText mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_movies);

        if (savedInstanceState != null) {
            mSelectedMovies = new ArrayList<Movie>();
        } else {
            Bundle bundle = getIntent().getExtras();
            mSelectedMovies = bundle.getParcelableArrayList(Constants.PARAM_MOVIE_LIST);
            Log.i(Constants.TAG, "mSelectedMovies: " + mSelectedMovies);
        } // end if-else

        mApplication = ((CineVoxApplication) getApplication());

        HListView list = (HListView) findViewById(R.id.listMoviesBar);
        adapter = new MoviesBarListAdapter(mSelectedMovies, this);
        list.setAdapter(adapter);

        if (mApplication.getAPIToken() == null) {
            mApplication.startAuthTokenFetch(this);
        }

        mSearchManually = (EditText) findViewById(R.id.searchManualMovies);
        mSearchManually.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSearchManually.getText().toString().length() > 1) {
                    if (mSearchMoviesAsyncTask != null) {
                        mSearchMoviesAsyncTask.cancel(true);
                    }
                    mSearchMoviesAsyncTask = new SearchMoviesAsyncTask();
                    mSearchMoviesAsyncTask.execute();
                }
            }
        });

        ListView listManually = (ListView) findViewById(R.id.listManualMovies);
        adapterManual = new MoviesSelectListAdapter(new ArrayList<Movie>(), mSelectedMovies, this);
        listManually.setAdapter(adapterManual);

        mSearchList = (EditText) findViewById(R.id.searchListMovies);
        mSearchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSearchList.getText().toString().length() > 1) {
                    if (mSearchListsAsyncTask != null) {
                        mSearchListsAsyncTask.cancel(true);
                    }
                    mSearchListsAsyncTask = new SearchListsAsyncTask();
                    mSearchListsAsyncTask.execute();
                }
            }
        });

        ListView listList = (ListView) findViewById(R.id.listListMovies);
        adapterList = new MoviesSelectListAdapter(new ArrayList<Movie>(), mSelectedMovies, this);
        listList.setAdapter(adapterList);


        //final ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableList);
        //MoviesSelectExpandableListAdapter adapter = new MoviesSelectExpandableListAdapter(this);
        //listView.setAdapter(adapter);

        /*listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(prev != -1 && prev != groupPosition) {
                    listView.collapseGroup(prev);
                }
                prev = groupPosition;
            }
        });*/
    }

    public void updateManualSearchList(ArrayList<Movie> movies) {
        adapterManual.updateList(movies);
    }

    public void updateListSearchList(ArrayList<Movie> movies) {
        adapterList.updateList(movies);
    }

    public void updateSelectedMoviesList() {
        adapter.notifyDataSetChanged();
    }

    /**
     * Handle Done in Movies select.
     *
     * @param v view
     */
    public void handleDoneClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(Constants.PARAM_MOVIE_LIST, mSelectedMovies);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Fetches Movie from AutoComplete Web Service.
     *
     * @author MajaDobnik
     *
     */
    private class SearchMoviesAsyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... v) {
            String term = mSearchManually.getText().toString().trim();
            Log.i(Constants.TAG, "TOKEN:" + mApplication.getAPIToken());

            return NetworkUtil.getMoviesBySearch(mApplication.getAPIToken(), term);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                Log.i(Constants.TAG, "MOVIES: " + movies);
                updateManualSearchList(movies);
            }
        } // end onPostExecute()
    } // end SearchMoviesAsyncTask()

    /**
     * Fetches Movie Lists from AutoComplete Web Service.
     *
     * @author MajaDobnik
     *
     */
    private class SearchListsAsyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... v) {
            String term = mSearchList.getText().toString().trim();
            Log.i(Constants.TAG, "TOKEN:" + mApplication.getAPIToken());

            return NetworkUtil.getMovieListsBySearch(mApplication.getAPIToken(), term);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                Log.i(Constants.TAG, "MOVIES: " + movies);
                updateListSearchList(movies);
            }
        } // end onPostExecute()
    } // end SearchListsAsyncTask()
}
