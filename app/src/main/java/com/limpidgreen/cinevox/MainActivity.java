package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
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
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;

    private ContentResolver mResolver;
    private EventListAdapter adapter;
    private TableObserver mObserver;

    private ArrayList<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = ((CineVoxApplication) getApplication());

        // Intent Message sent from Broadcast Receiver
        String str = getIntent().getStringExtra("msg");
        // When Message sent from Broadcase Receiver is not empty
        if (str != null) {
            Toast.makeText(
                    getApplicationContext(),
                    str,
                    Toast.LENGTH_LONG).show();
        } // end if

        // Get the content resolver object for your app
        mResolver = getContentResolver();
        mObserver = new TableObserver(false);
        mResolver.registerContentObserver(EventsContentProvider.CONTENT_URI, true, mObserver);

        eventList = new ArrayList<Event>();
        Cursor curEvents = mResolver.query(EventsContentProvider.CONTENT_URI, null, null, null, null);
        if (curEvents != null) {
            while (curEvents.moveToNext()) {
                eventList.add(Event.fromCursor(curEvents));
            } // end while
            curEvents.close();
        } // end if

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
        adapter = new EventListAdapter(eventList, this);
        list.setAdapter(adapter);
    } // end onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mResolver.unregisterContentObserver(mObserver);
    }

    /**
     * Handle New Event button click in the Main View.
     *
     * @param v view
     */
    public void handleNewEvent(View v) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
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
            /*
             * Ask the framework to run your sync adapter.
             * To maintain backward compatibility, assume that
             * changeUri is null.*/
            //ContentResolver.requestSync(mAccount, EventsContentProvider.AUTHORITY, null);
            eventList = new ArrayList<Event>();
            Cursor curEvents = mResolver.query(EventsContentProvider.CONTENT_URI, null, null, null, null);
            if (curEvents != null) {
                while (curEvents.moveToNext()) {
                    eventList.add(Event.fromCursor(curEvents));
                } // end while
                curEvents.close();
            } // end if
            adapter.updateEventList(eventList);
        }
    }
}