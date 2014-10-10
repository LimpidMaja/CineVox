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
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.limpidgreen.cinevox.adapters.MoviesBarListAdapter;
import com.limpidgreen.cinevox.adapters.MoviesSelectExpandableListAdapter;

import it.sephiroth.android.library.widget.HListView;

/**
 * Select Friends Activity.
 *
 * @author MajaDobnik
 *
 */
public class SelectMoviesActivity extends Activity {
    private static int prev = -1;

    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private MoviesBarListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_movies);

        HListView list = (HListView) findViewById(R.id.listMoviesBar);
        adapter = new MoviesBarListAdapter(this);
        list.setAdapter(adapter);

        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableList);
        MoviesSelectExpandableListAdapter adapter = new MoviesSelectExpandableListAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(prev != -1 && prev != groupPosition) {
                    listView.collapseGroup(prev);
                }
                prev = groupPosition;
            }
        });
    }

}
