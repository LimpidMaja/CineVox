package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.limpidgreen.cinevox.adapters.EventListAdapter;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private EventListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(Constants.TAG, "SEEESION: " + Session.getActiveSession());
        Session session = Session.getActiveSession();
        if (Session.getActiveSession() != null) {
            // make request to the /me API
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {

                        TextView text = (TextView) findViewById(R.id.welcome);
                        text.setText("Welcome " + user.getName());
                        String email = (String) user.getProperty("email");
                        Log.i(Constants.TAG, "USER ID: " + user.getId());
                        Log.i(Constants.TAG, "EMAIL: " + email);
                    }
                }
            }).executeAsync();
        }

        ListView list = (ListView) findViewById(R.id.listEvents);
        adapter = new EventListAdapter(this);
        list.setAdapter(adapter);
    } // end onCreate()

    /**
     * Handle Terms button click in Settings.
     *
     * @param v view
     */
    public void handleNewEvent(View v) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

}
