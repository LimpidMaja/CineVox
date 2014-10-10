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
import android.view.View;
import android.widget.ListView;

import com.limpidgreen.cinevox.adapters.MoviesRateListAdapter;

/**
 * New Event Activity.
 *
 * @author MajaDobnik
 *
 */
public class MoviesKnockoutActivity extends Activity {
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_knockout);

    }
}
