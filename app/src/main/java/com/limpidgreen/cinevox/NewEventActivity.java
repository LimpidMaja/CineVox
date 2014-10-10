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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.limpidgreen.cinevox.util.Constants;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class NewEventActivity extends Activity {
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

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
     * Handle Select Movies button click.
     *
     * @param v view
     */
    public void handleSelectMovies(View v) {
        Intent intent = new Intent(this, SelectMoviesActivity.class);
        startActivity(intent);
    }

    /**
     * Handle Edit Rating button click.
     *
     * @param v view
     */
    public void handleEditVoting(View v) {
        Intent intent = new Intent(this, EditVotingActivity.class);
        startActivity(intent);
    }
}
