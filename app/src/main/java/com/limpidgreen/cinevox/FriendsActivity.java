package com.limpidgreen.cinevox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.adapters.EventListAdapter;
import com.limpidgreen.cinevox.adapters.FriendListAdapter;
import com.limpidgreen.cinevox.adapters.FriendSelectListAdapter;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.model.EventStatus;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.model.Movie;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.ExpandableHeightListView;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.util.ArrayList;


public class FriendsActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private ContentResolver mResolver;
    private TableObserver mObserver;
    private ProgressDialog mProgressDialog = null;
    private SendFriendRequestTask mSendFriendRequestTask;
    private ConfirmFriendRequestTask mConfirmFriendRequestTask;

    private SearchFriendsAsyncTask mSearchFriendsAsyncTask;

    private LinearLayout mFriendsLayout;
    private LinearLayout mRequestLayout;
    private LinearLayout mSuggestionsLayout;
    private LinearLayout mSearchLayout;

    private FriendListAdapter adapter;
    private FriendListAdapter adapterRequests;
    private FriendListAdapter adapterSuggestions;
    private FriendListAdapter adapterSearch;
    private ArrayList<Friend> mFriendList;
    private ArrayList<Friend> mFriendRequestList;
    private ArrayList<Friend> mFriendSuggestionsList;
    private EditText mSearchFriends;
    private TextView noResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mApplication = ((CineVoxApplication) getApplication());

        // Get the content resolver object for your app
        mResolver = getContentResolver();
        mObserver = new TableObserver(false);
        mResolver.registerContentObserver(FriendsContentProvider.CONTENT_URI, true, mObserver);

        ArrayList<Friend> friendList = new ArrayList<Friend>();
        Cursor curFriends = mResolver.query(FriendsContentProvider.CONTENT_URI, null, null, null, null);
        if (curFriends != null) {
            while (curFriends.moveToNext()) {
                friendList.add(Friend.fromCursor(curFriends));
            } // end while
            curFriends.close();
        } // end if

        mFriendList = new ArrayList<Friend>();
        mFriendRequestList = new ArrayList<Friend>();
        mFriendSuggestionsList = new ArrayList<Friend>();
        for (Friend friend : friendList) {
            if (friend.isConfirmed()) {
                mFriendList.add(friend);
            } else if (friend.isRequest()) {
                mFriendRequestList.add(friend);
            } else {
                mFriendSuggestionsList.add(friend);
            }
        }

        noResults = (TextView) findViewById(R.id.friends_no_results);
        mSearchFriends = (EditText) findViewById(R.id.searchFriends);
        mSearchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSearchFriends.getText().toString().length() > 1) {
                    if (mSearchFriendsAsyncTask != null) {
                        mSearchFriendsAsyncTask.cancel(true);
                    } else {
                        showProgress("Searching...");
                    }
                    mSearchFriendsAsyncTask = new SearchFriendsAsyncTask();
                    mSearchFriendsAsyncTask.execute();

                    noResults.setVisibility(View.GONE);
                    mSearchLayout.setVisibility(View.VISIBLE);
                    mFriendsLayout.setVisibility(View.GONE);
                    mRequestLayout.setVisibility(View.GONE);
                    mSuggestionsLayout.setVisibility(View.GONE);
                } else if (mSearchFriends.getText().toString().length() == 1) {
                    mSearchLayout.setVisibility(View.VISIBLE);
                    mFriendsLayout.setVisibility(View.GONE);
                    mRequestLayout.setVisibility(View.GONE);
                    mSuggestionsLayout.setVisibility(View.GONE);

                    noResults.setVisibility(View.VISIBLE);
                    adapterSearch.update(new ArrayList<Friend>());
                } else if (mSearchFriends.getText().toString().length() == 0) {
                    mSearchLayout.setVisibility(View.GONE);
                    mFriendsLayout.setVisibility(View.VISIBLE);
                    if (mFriendRequestList.isEmpty()) {
                        mRequestLayout.setVisibility(View.GONE);
                    } else {
                        mRequestLayout.setVisibility(View.VISIBLE);
                    }
                    if (mFriendSuggestionsList.isEmpty()) {
                        mSuggestionsLayout.setVisibility(View.GONE);
                    } else {
                        mSuggestionsLayout.setVisibility(View.VISIBLE);
                    }
                    noResults.setVisibility(View.GONE);
                    adapterSearch.update(new ArrayList<Friend>());
                }
            }
        });

        ExpandableHeightListView list = (ExpandableHeightListView) findViewById(R.id.listFriends);
        adapter = new FriendListAdapter(mFriendList, this);
        list.setAdapter(adapter);
        list.setExpanded(true);

        ExpandableHeightListView listRequests = (ExpandableHeightListView) findViewById(R.id.listFriendRequests);
        adapterRequests = new FriendListAdapter(mFriendRequestList, this);
        listRequests.setAdapter(adapterRequests);
        listRequests.setExpanded(true);

        ExpandableHeightListView listSuggestion = (ExpandableHeightListView) findViewById(R.id.listSuggestedFriends);
        adapterSuggestions = new FriendListAdapter(mFriendSuggestionsList, this);
        listSuggestion.setAdapter(adapterSuggestions);
        listSuggestion.setExpanded(true);

        ExpandableHeightListView listSearch = (ExpandableHeightListView) findViewById(R.id.listFriendSearch);
        adapterSearch = new FriendListAdapter(new ArrayList<Friend>(), this);
        listSearch.setAdapter(adapterSearch);
        listSearch.setExpanded(true);

        ScrollView scrollView = (ScrollView) findViewById(R.id.friendsScrollView);
        mFriendsLayout = (LinearLayout) findViewById(R.id.friendsLayout);
        mRequestLayout = (LinearLayout) findViewById(R.id.friendRequestsLayout);
        mSuggestionsLayout = (LinearLayout) findViewById(R.id.friendSuggestionsLayout);
        mSearchLayout = (LinearLayout) findViewById(R.id.friendSearchLayout);

        if (mFriendRequestList.isEmpty()) {
            mRequestLayout.setVisibility(View.GONE);
        }
        if (mFriendSuggestionsList.isEmpty()) {
            mSuggestionsLayout.setVisibility(View.GONE);
        }

        scrollView.scrollTo(0, 0);
    } // end onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResolver.unregisterContentObserver(mObserver);
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

    public void updateSearchList(ArrayList<Friend> friends) {
        if (friends.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
            adapterSearch.update(new ArrayList<Friend>());
        } else {
            noResults.setVisibility(View.GONE);
            for (Friend friend : friends) {
                Friend localFriend = null;
                Cursor curFriend = mResolver.query(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), null, null, null, null);
                if (curFriend != null) {
                    while (curFriend.moveToNext()) {
                        localFriend = Friend.fromCursor(curFriend);
                    }
                    curFriend.close();
                } // end if

                if (localFriend != null) {
                    friends.remove(friend);
                    friends.add(localFriend);
                } // end if
            } // end for
            adapterSearch.update(friends);
        }
    }

    public class TableObserver extends ContentObserver {
        boolean selfChange;

        public TableObserver(boolean selfChange) {
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
            Log.i(Constants.TAG, "ON CHANGE");
            ArrayList<Friend> friendList = new ArrayList<Friend>();
            Cursor curFriends = mResolver.query(FriendsContentProvider.CONTENT_URI, null, null, null, null);
            if (curFriends != null) {
                while (curFriends.moveToNext()) {
                    friendList.add(Friend.fromCursor(curFriends));
                } // end while
                curFriends.close();
            } // end if

            mFriendList = new ArrayList<Friend>();
            mFriendRequestList = new ArrayList<Friend>();
            mFriendSuggestionsList = new ArrayList<Friend>();
            for (Friend friend : friendList) {
                if (friend.isConfirmed()) {
                    mFriendList.add(friend);
                } else if (friend.isRequest()) {
                    mFriendRequestList.add(friend);
                } else {
                    mFriendSuggestionsList.add(friend);
                }
            }

            adapter.update(mFriendList);
            adapterRequests.update(mFriendRequestList);
            adapterSuggestions.update(mFriendSuggestionsList);
        }
    }

    public void onFriendConfirmRequestResult(Friend friend) {
        mConfirmFriendRequestTask = null;
        hideProgress();
        mResolver.update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
        Toast toast = Toast.makeText(this,
                    "You confirmed the request Successfully!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onFriendConfirmRequestError() {
        mConfirmFriendRequestTask = null;
        hideProgress();
        Toast toast = Toast.makeText(this,
                "There was a problem confirming the friend request. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void confirmFriendRequest(Integer friendId) {
        if (mConfirmFriendRequestTask == null) {
            showProgress("Sending Confirmation");
            mConfirmFriendRequestTask = new ConfirmFriendRequestTask();
            mConfirmFriendRequestTask.execute(friendId);
        }
    }

    public void onFriendSendRequestResult(Friend friend) {
        mSendFriendRequestTask = null;
        hideProgress();
        mResolver.update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
        Toast toast = Toast.makeText(this,
                "You sent the request Successfully!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onFriendSemdRequestError() {
        mSendFriendRequestTask = null;
        hideProgress();

        Toast toast = Toast.makeText(this,
                "There was a problem sending the friend request. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sendFriendRequest(Integer friendId) {
        if (mSendFriendRequestTask == null) {
            showProgress("Sending Request");
            mSendFriendRequestTask = new SendFriendRequestTask();
            mSendFriendRequestTask.execute(friendId);
        }
    }

    /**
     * Confirm Friend Request Task to send an async call to the server to confirm a friend request.
     *
     * @author MajaDobnik
     *
     */
    private class ConfirmFriendRequestTask extends AsyncTask<Integer, Void, Friend> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Friend doInBackground(Integer... value) {
            Integer friendId = value[0];
            return NetworkUtil.confirmFriendRequest(mApplication.getAPIToken(), friendId);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Friend result) {
            if (result != null) {
                onFriendConfirmRequestResult(result);
            } else {
                onFriendConfirmRequestError();
            } // end if
        } // end onPostExecute()
    } // end ConfirmFriendRequestTask()

    /**
     * Send Friend Request Task to send an async call to the server to send a friend request.
     *
     * @author MajaDobnik
     *
     */
    private class SendFriendRequestTask extends AsyncTask<Integer, Void, Friend> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Friend doInBackground(Integer... value) {
            Integer friendId = value[0];
            return NetworkUtil.sendFriendRequest(mApplication.getAPIToken(), friendId);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Friend result) {
            if (result != null) {
                onFriendSendRequestResult(result);
            } else {
                onFriendSemdRequestError();
            } // end if
        } // end onPostExecute()
    } // end SendFriendRequestTask()

    /**
     * Fetches Movie from AutoComplete Web Service.
     *
     * @author MajaDobnik
     *
     */
    private class SearchFriendsAsyncTask extends AsyncTask<Void, Void, ArrayList<Friend>> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected ArrayList<Friend> doInBackground(Void... v) {
            String term = mSearchFriends.getText().toString().trim();
            return NetworkUtil.getFriendsBySearch(mApplication.getAPIToken(), term);
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            mSearchFriendsAsyncTask = null;
            hideProgress();
            if (friends != null) {
                Log.i(Constants.TAG, "FRIENDS: " + friends);
                updateSearchList(friends);
            }
        } // end onPostExecute()
    } // end SearchFriendsAsyncTask()
}