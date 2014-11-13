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
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.limpidgreen.cinevox.adapters.FriendSelectListAdapter;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.model.Friend;
import com.limpidgreen.cinevox.util.Constants;

import java.util.ArrayList;

/**
 * Select Friends Activity.
 *
 * @author MajaDobnik
 *
 */
public class SelectFriendsActivity extends Activity {
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private ContentResolver mResolver;
    private FriendSelectListAdapter adapter;
    private ArrayList<Friend> mFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        ArrayList<Friend> selectedFriends;
        if (savedInstanceState != null) {
            selectedFriends = new ArrayList<Friend>();
        } else {
            Bundle bundle = getIntent().getExtras();
            selectedFriends = bundle.getParcelableArrayList(Constants.PARAM_FRIEND_LIST);
            Log.i(Constants.TAG, "selectedFriends: " + selectedFriends);
        } // end if-else


        // Get the content resolver object for your app
        mResolver = getContentResolver();

        mFriendList = new ArrayList<Friend>();
        Cursor curFriends = mResolver.query(FriendsContentProvider.CONTENT_URI, null, CineVoxDBHelper.FRIENDS_COL_CONFIRMED + " = 1", null, null);
        if (curFriends != null) {
            while (curFriends.moveToNext()) {
                mFriendList.add(Friend.fromCursor(curFriends));
            } // end while
            curFriends.close();
        } // end if
        Log.i(Constants.TAG, "FRIENDS: " + mFriendList);

        ListView list = (ListView) findViewById(R.id.listFriends);
        adapter = new FriendSelectListAdapter(mFriendList, selectedFriends, this);
        list.setAdapter(adapter);
    }

    /**
     * Handle Done in Friends select.
     *
     * @param v view
     */
    public void handleDoneClick(View v) {
        ArrayList<Friend> selectedFriends = adapter.getmSelectedFriendList();
        Intent intent = new Intent();
        intent.putExtra(Constants.PARAM_FRIEND_LIST, selectedFriends);
        setResult(RESULT_OK, intent);
        finish();
    }
}
