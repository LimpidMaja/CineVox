package com.limpidgreen.cinevox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.ExpandableHeightListView;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.util.ArrayList;


public class FriendsActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private ContentResolver mResolver;
    private TableObserver mObserver;

    private LinearLayout mRequestLayout;
    private LinearLayout mSuggestionsLayout;

    private FriendListAdapter adapter;
    private FriendListAdapter adapterRequests;
    private FriendListAdapter adapterSuggestions;
    private ArrayList<Friend> mFriendList;
    private ArrayList<Friend> mFriendRequestList;
    private ArrayList<Friend> mFriendSuggestionsList;

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

        ScrollView scrollView = (ScrollView) findViewById(R.id.friendsScrollView);
        mRequestLayout = (LinearLayout) findViewById(R.id.friendRequestsLayout);
        mSuggestionsLayout = (LinearLayout) findViewById(R.id.friendSuggestionsLayout);

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
        mResolver.update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
        Toast toast = Toast.makeText(this,
                    "You confirmed the request Successfully!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onFriendConfirmRequestError() {
        Toast toast = Toast.makeText(this,
                "There was a problem confirming the friend request. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void confirmFriendRequest(Integer friendId) {
        ConfirmFriendRequestTask task = new ConfirmFriendRequestTask();
        task.execute(friendId);
    }

    public void onFriendSendRequestResult(Friend friend) {
        mResolver.update(ContentUris.withAppendedId(FriendsContentProvider.CONTENT_URI, friend.getId()), friend.getContentValues(), null, null);
        Toast toast = Toast.makeText(this,
                "You sent the request Successfully!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onFriendSemdRequestError() {
        Toast toast = Toast.makeText(this,
                "There was a problem sending the friend request. Try again!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void sendFriendRequest(Integer friendId) {
        SendFriendRequestTask task = new SendFriendRequestTask();
        task.execute(friendId);
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
}