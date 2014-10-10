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
import android.os.Bundle;
import android.widget.ListView;

import com.limpidgreen.cinevox.adapters.EventListAdapter;
import com.limpidgreen.cinevox.adapters.FriendListAdapter;

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

    private FriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        ListView list = (ListView) findViewById(R.id.listFriends);
        adapter = new FriendListAdapter(this);
        list.setAdapter(adapter);
    }

}
