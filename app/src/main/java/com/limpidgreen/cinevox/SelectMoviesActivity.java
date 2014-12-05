/**
 * SelectFriendsActivity.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesSelect4ListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesSelectListAdapter;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.model.MovieList;
import com.limpidgreen.cinevox.util.ClickSpinner;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.util.ArrayList;
import java.util.Collections;

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
    private SearchCollectionMoviesAsyncTask mSearchCollectionAsyncTask;
    private SearchCollectionAllMoviesAsyncTask mSearchCollectionAllAsyncTask;
    private ProgressDialog mProgressDialog = null;

    private ArrayList<Movie> mSelectedMovies;
    private ArrayList<Movie> mMovieCollection;

    private MoviesBarListAdapter adapter;

    private MoviesSelectListAdapter adapterManual;
    private MoviesSelect4ListAdapter adapterList;
    private MoviesSelectListAdapter adapterCollection;

    private EditText mSearchManually;
    private EditText mSearchList;
    private EditText mSearchCollection;

    private Button mManuallyButton;
    private Button mListButton;
    private Button mCollectionButton;

    private LinearLayout mManualLayout;
    private LinearLayout mListLayout;
    private LinearLayout mCollectionLayout;
    private ClickSpinner mSpinner;

    private boolean mDescending;
    private int mSorting;

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

        mManuallyButton = (Button) findViewById(R.id.select_movies_manually_button);
        mManualLayout = (LinearLayout) findViewById(R.id.select_movies_manually_layout);
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

        mListButton = (Button) findViewById(R.id.select_movies_list_button);
        mListLayout = (LinearLayout) findViewById(R.id.select_movies_list_layout);
        mSearchList = (EditText) findViewById(R.id.searchListMovies);
        Button movieListSearchButton = (Button) findViewById(R.id.movie_list_search_button);
        movieListSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchList.getText().toString().length() > 1) {
                    if (mSearchListsAsyncTask != null) {
                        mSearchListsAsyncTask.cancel(true);
                    } else {
                        showProgress("Searching for IMDB Lists");
                    }
                    mSearchListsAsyncTask = new SearchListsAsyncTask();
                    mSearchListsAsyncTask.execute();
                }
            }
        });

        ExpandableListView listList = (ExpandableListView) findViewById(R.id.listListMovies);
        adapterList = new MoviesSelect4ListAdapter(new ArrayList<MovieList>(), mSelectedMovies, this);
        listList.setAdapter(adapterList);

        mCollectionLayout = (LinearLayout) findViewById(R.id.select_movies_collection_layout);

        mCollectionButton = (Button) findViewById(R.id.select_movies_collection_button);
        mSpinner = (ClickSpinner) findViewById(R.id.sort_select);
        mSearchCollection = (EditText) findViewById(R.id.searchCollectionMovies);
        mSearchCollection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSearchCollection.getText().toString().length() > 1) {
                    if (mSearchCollectionAsyncTask != null) {
                        mSearchCollectionAsyncTask.cancel(true);
                    }
                    mSearchCollectionAsyncTask = new SearchCollectionMoviesAsyncTask();
                    mSearchCollectionAsyncTask.execute();
                } else {
                    if (mMovieCollection == null || mMovieCollection.isEmpty()) {
                        updateCollectionSearchList(new ArrayList<Movie>());
                        if (mSearchCollectionAllAsyncTask == null) {
                            showProgress("Getting collection");
                            mSearchCollectionAllAsyncTask = new SearchCollectionAllMoviesAsyncTask();
                            mSearchCollectionAllAsyncTask.execute();
                        } // end if
                    } else {
                        updateCollectionSearchList(mMovieCollection);
                    } // end if-else
                }
            }
        });

//        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Constants.TAG, "onItemSelected : " + position);
                if (mMovieCollection != null && !mMovieCollection.isEmpty()) {
                    switch (mSpinner.getSelectedItemPosition()) {
                        case 0:
                            if (mSorting == 0) {
                                if (mDescending) {
                                    mDescending = false;
                                } else {
                                    mDescending = true;
                                } // end if-else
                            } // end if
                            if (mDescending) {
                                Collections.sort(mMovieCollection, Movie.TITLE_DESC_COMPARATOR);
                            } else {
                                Collections.sort(mMovieCollection, Movie.TITLE_ASC_COMPARATOR);
                            } // end if-else
                            mSearchCollection.setText("");
                            //updateCollectionSearchList(mMovieCollection);
                            mSorting = 0;
                            break;
                        case 1:
                            if (mSorting == 1) {
                                if (mDescending) {
                                    mDescending = false;
                                } else {
                                    mDescending = true;
                                } // end if-else
                            } // end if
                            if (mDescending) {
                                Collections.sort(mMovieCollection, Movie.RATING_ASC_COMPARATOR);
                            } else {
                                Collections.sort(mMovieCollection, Movie.RATING_DESC_COMPARATOR);
                            } // end if-else
                            mSearchCollection.setText("");
                            mSorting = 1;
                            break;
                        case 2:
                            if (mSorting == 2) {
                                if (mDescending) {
                                    mDescending = false;
                                } else {
                                    mDescending = true;
                                } // end if-else
                            } // end if
                            if (mDescending) {
                                Collections.sort(mMovieCollection, Movie.DATE_ADDED_DESC_COMPARATOR);
                            } else {
                                Collections.sort(mMovieCollection, Movie.DATE_ADDED_ASC_COMPARATOR);
                            } // end if-else

                            mSearchCollection.setText("");
                            mSorting = 2;
                            break;
                        case 3:
                            if (mSorting == 3) {
                                if (mDescending) {
                                    mDescending = false;
                                } else {
                                    mDescending = true;
                                } // end if-else
                            } // end if
                            if (mDescending) {
                                Collections.sort(mMovieCollection, Movie.RELEASE_DATE_DESC_COMPARATOR);
                            } else {
                                Collections.sort(mMovieCollection, Movie.RELEASE_DATE_ASC_COMPARATOR);
                            } // end if-else
                            mSearchCollection.setText("");
                            mSorting = 3;
                            break;
                        case 4:
                            if (mSorting == 4) {
                                if (mDescending) {
                                    mDescending = false;
                                } else {
                                    mDescending = true;
                                } // end if-else
                            } // end if
                            if (mDescending) {
                                Collections.sort(mMovieCollection, Movie.RUNTIME_DESC_COMPARATOR);
                            } else {
                                Collections.sort(mMovieCollection, Movie.RUNTIME_ASC_COMPARATOR);
                            } // end if-else
                            mSearchCollection.setText("");
                            mSorting = 4;
                            break;
                    } // end switch
                } // end if
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(Constants.TAG, "ON NOTHING SELECTED");
            }
        });

        ListView listCollection = (ListView) findViewById(R.id.listCollectionMovies);
        adapterCollection = new MoviesSelectListAdapter(new ArrayList<Movie>(), mSelectedMovies, this);
        listCollection.setAdapter(adapterCollection);

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

    public void handleShowMoviesManually(View v) {
        mListLayout.setVisibility(View.GONE);
        mManualLayout.setVisibility(View.VISIBLE);
        mCollectionLayout.setVisibility(View.GONE);

        mManuallyButton.setBackgroundColor(Color.parseColor("#ffa172f1"));
        mListButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
        mCollectionButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
    }

    public void handleShowMoviesList(View v) {
        mListLayout.setVisibility(View.VISIBLE);
        mManualLayout.setVisibility(View.GONE);
        mCollectionLayout.setVisibility(View.GONE);

        mManuallyButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
        mListButton.setBackgroundColor(Color.parseColor("#ffa172f1"));
        mCollectionButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
    }

    public void handleShowMoviesCollection(View v) {
        mListLayout.setVisibility(View.GONE);
        mManualLayout.setVisibility(View.GONE);
        mCollectionLayout.setVisibility(View.VISIBLE);
        if (mMovieCollection == null || mMovieCollection.isEmpty()) {
            if (mSearchCollectionAllAsyncTask != null) {
                mSearchCollectionAllAsyncTask.cancel(true);
            } else {
                showProgress("Getting collection");
            } // end if-else
            mSearchCollectionAllAsyncTask = new SearchCollectionAllMoviesAsyncTask();
            mSearchCollectionAllAsyncTask.execute();
        } // end if

        mManuallyButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
        mListButton.setBackgroundColor(Color.parseColor("#ffd9b8f1"));
        mCollectionButton.setBackgroundColor(Color.parseColor("#ffa172f1"));
    }

    public void updateManualSearchList(ArrayList<Movie> movies) {
        adapterManual.updateList(movies);
    }

    public void updateListSearchList(ArrayList<MovieList> movieLists) {
        adapterList.updateList(movieLists);
    }

    public void updateCollectionSearchList(ArrayList<Movie> movies) {
        adapterCollection.updateList(movies);
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
                hideProgress();
                mSearchMoviesAsyncTask = null;
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
    private class SearchListsAsyncTask extends AsyncTask<Void, Void, ArrayList<MovieList>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<MovieList> doInBackground(Void... v) {
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
        protected void onPostExecute(ArrayList<MovieList> movies) {
            if (movies != null) {
                Log.i(Constants.TAG, "MOVIES: " + movies);
                hideProgress();
                mSearchListsAsyncTask = null;
                updateListSearchList(movies);
            }
        } // end onPostExecute()
    } // end SearchListsAsyncTask()

    /**
     * Fetches Movie Collection from AutoComplete Web Service.
     *
     * @author MajaDobnik
     *
     */
    private class SearchCollectionMoviesAsyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... v) {
            String term = mSearchCollection.getText().toString().trim();
            Log.i(Constants.TAG, "TOKEN:" + mApplication.getAPIToken());

            return NetworkUtil.getMoviesFromCollectionSearch(mApplication.getAPIToken(), term);
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
                hideProgress();
                mSearchCollectionAsyncTask = null;
                updateCollectionSearchList(movies);
            } // end if
        } // end onPostExecute()
    } // end SearchCollectionMoviesAsyncTask()

    /**
     * Fetches whole Collection from Web Service.
     *
     * @author MajaDobnik
     *
     */
    private class SearchCollectionAllMoviesAsyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... v) {
            return NetworkUtil.getMoviesFromCollectionSearch(mApplication.getAPIToken(), null);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            hideProgress();
            mSearchCollectionAllAsyncTask = null;
            if (movies != null) {
                Log.i(Constants.TAG, "MOVIES: " + movies);
                mMovieCollection = movies;
                updateCollectionSearchList(movies);
            } // end if
        } // end onPostExecute()
    } // end SearchCollectionAllMoviesAsyncTask()
}
