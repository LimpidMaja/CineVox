/**
 * FBLoginActivity.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.*;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;
import com.limpidgreen.cinevox.util.Utility;

import java.io.IOException;
import java.util.Date;

/**
 * Facebook Login Activity.
 *
 * @author MajaDobnik
 *
 */
public class FBLoginActivity extends AccountAuthenticatorActivity {
    private GoogleCloudMessaging mGcmObject;
    private Context mApplicationContext;
    private AccountManager mAccountManager;
    private String mGcmRegId;
    private String accessToken;
    private String userId;
    private String email;
    private Date expirationDate;

    private LinearLayout mEmailEditLayout;
    private Button mFBSigninButton;
    private Button mEmailSigninButton;

    /** Keep track of the login task so can cancel it if requested */
    private UserAuthenticateTask mAuthTask = null;

    /** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AccountAuthenticatorActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplicationContext = getApplicationContext();

        setContentView(R.layout.activity_fb_login);

        mEmailEditLayout = (LinearLayout) findViewById(R.id.email_edit_layout);
        mEmailEditLayout.setVisibility(View.GONE);

        mEmailSigninButton = (Button) findViewById(R.id.email_edit_button);
        mEmailSigninButton.setVisibility(View.GONE);

        mAccountManager = AccountManager.get(this);
    } // end onCreate()

    /**
     * Handle Login button click.
     *
     * @param v view
     */
    public void handleLoginClick(View v) {
        showProgress();

        // start Facebook Login
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    accessToken = session.getAccessToken();
                    expirationDate = session.getExpirationDate();
                    Log.i(Constants.TAG, "SESSION TOKEN: " + session.getAccessToken());
                    Log.i(Constants.TAG, "SESSION TOKEN EXPIRATION: " + session.getExpirationDate());

                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                TextView welcome = (TextView) findViewById(R.id.welcome);
                                welcome.setText("Hello " + user.getName() + "!");

                                userId = user.getId();
                                email = (String) user.getProperty("email");
                                Log.i(Constants.TAG, "USER ID: " + user.getId());
                                //Log.i(Constants.TAG, "EMAIL: " + email);

                                if (email != null && !email.isEmpty()) {
                                    if (Utility.checkPlayServices(FBLoginActivity.this, mApplicationContext)) {
                                        registerGcmInBackground();
                                    } else {
                                        hideProgress();
                                    } // end if-else

                                    /*if (mAuthTask == null) {
                                        mAuthTask = new UserAuthenticateTask();
                                        mAuthTask.execute();
                                    } // end if*/
                                } else {
                                    hideProgress();

                                    mEmailEditLayout = (LinearLayout) findViewById(R.id.email_edit_layout);
                                    mEmailEditLayout.setVisibility(View.VISIBLE);

                                    mEmailSigninButton = (Button) findViewById(R.id.email_edit_button);
                                    mEmailSigninButton.setVisibility(View.VISIBLE);

                                    mFBSigninButton = (Button) findViewById(R.id.authButton);
                                    mFBSigninButton.setVisibility(View.GONE);
                                }
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    /**
     * Handle Email Submit button click.
     *
     * @param v view
     */
    public void handleEmailSubmit(View v) {
        EditText emailEdit = (EditText) findViewById(R.id.email_edit);

        Log.i(Constants.TAG, "EMAIL text: " + emailEdit );
        Log.i(Constants.TAG, "EMAIL 2: " + emailEdit.getText().toString() );

        if (emailEdit != null && !emailEdit.getText().toString().trim().isEmpty()
                && com.limpidgreen.cinevox.util.Utility.validate(emailEdit.getText().toString().trim())) {
            email = emailEdit.getText().toString();
            Log.i(Constants.TAG, "EMAIL: " + email);

            if (Utility.checkPlayServices(FBLoginActivity.this, mApplicationContext)) {
                showProgress();
                registerGcmInBackground();
            } // end if

            /*if (mAuthTask == null) {
                mAuthTask = new UserAuthenticateTask();
                mAuthTask.execute();
            } // end if*/
        } else {
            Toast toast = Toast.makeText(this,
                    getText(R.string.email_empty),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress() {
        mProgressDialog = ProgressDialog.show(this, null,
                getText(R.string.ui_activity_authenticating), true, true,
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        if (mAuthTask != null) {
                            mAuthTask.cancel(true);
                        } // end if
                    } // end onCancel()
                });
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

    public void onRegisterGcmResult(String regId) {
        boolean success = ((regId != null) && (regId.length() > 0));

        mGcmRegId = null;

        if (success) {
            mGcmRegId = regId;

            //Toast.makeText(
            //        mApplicationContext,
            //        "Registered with GCM Server successfully: " + mGcmRegId, Toast.LENGTH_SHORT).show();

            if (mAuthTask == null) {
                mAuthTask = new UserAuthenticateTask();
                mAuthTask.execute();
            } // end if
        } else {
            hideProgress();
            Toast toast = Toast.makeText(this,
                    getText(R.string.register_gcm_error),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onRegisterGcmError() {
        hideProgress();
        Toast toast = Toast.makeText(this,
                getText(R.string.register_gcm_error),
                Toast.LENGTH_SHORT);
        toast.show();
    } // end onRegisterGcmError()

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken
     *            the authentication token returned by the server, or NULL if
     *            authentication failed.
     */
    public void onAuthenticationResult(String authToken) {
        boolean success = ((authToken != null) && (authToken.length() > 0));


        /*if (!success) {
            authToken = "test_token";
            success = true;
        }*/
        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog
        hideProgress();

        if (success) {
            final Account account = new Account(email, Constants.ACCOUNT_TYPE);

            Account[] userAccounts = mAccountManager
                    .getAccountsByType(Constants.ACCOUNT_TYPE);
            if (userAccounts.length  < 1) {
                Bundle data = new Bundle();
                Bundle userData = new Bundle();
                userData.putString(Constants.USER_DATA_GCM_REG_ID, mGcmRegId);
                data.putBundle(AccountManager.KEY_USERDATA, userData);

                mAccountManager.addAccountExplicitly(account, null, data);
                // Set events sync for this account.
                ContentResolver.setSyncAutomatically(account,
                        EventsContentProvider.AUTHORITY, true);
                // Set friends sync for this account.
                ContentResolver.setSyncAutomatically(account,
                        FriendsContentProvider.AUTHORITY, true);

            } else {
                mAccountManager.setPassword(account, null);
            } // end if-else
            mAccountManager.setAuthToken(account, Constants.API_TOKEN_TYPE, authToken);

            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

            /*SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.API_TOKEN_TYPE, authToken);
            editor.commit();*/

            Toast toast = Toast.makeText(this,
                    getText(R.string.activity_login_success), Toast.LENGTH_SHORT);
            toast.show();

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast toast = Toast.makeText(this,
                        getText(R.string.login_activity_error),
                        Toast.LENGTH_SHORT);
            toast.show();
        } // end if-else
    } // end onAuthenticationResult()

    /**
     * On Authentication Cancel.
     */
    public void onAuthenticationCancel() {
        // Our task is complete, so clear it out
        mAuthTask = null;

        // Hide the progress dialog
        hideProgress();
    } // end onAuthenticationCancel()

    /**
     * Authentication Error
     */
    public void onAuthenticationError() {
        hideProgress();
        Toast toast = Toast.makeText(this,
                getText(R.string.login_activity_error),
                Toast.LENGTH_SHORT);
        toast.show();
    } // end onAuthenticationError()

    /**
     * Represents an asynchronous task used to authenticate the user with an API call.
     */
    public class UserAuthenticateTask extends AsyncTask<Void, Void, String> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected String doInBackground(Void... params) {
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            try {
                return NetworkUtil.authenticate(userId, accessToken, mGcmRegId, expirationDate, email);
            } catch (Exception ex) {

                return null;
            } // end try-catch
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final String authToken) {
            if (authToken != null) {
                onAuthenticationResult(authToken);
            } else {
                onAuthenticationError();
            } // end if-else
        } // end onPostExecute()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onCancelled()
         */
        @Override
        protected void onCancelled() {
            onAuthenticationCancel();
        } // end onCancelled()
    } // end UserLoginTask()

    private void registerGcmInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (mGcmObject == null) {
                        mGcmObject = GoogleCloudMessaging
                                .getInstance(mApplicationContext);
                    } // end if
                    return mGcmObject
                            .register(Constants.GOOGLE_PROJ_ID);
                } catch (IOException ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String regId) {
                if (regId != null) {
                    onRegisterGcmResult(regId);
                } else {
                    onRegisterGcmError();
                } // end if-else
            }
        }.execute(null, null, null);
    }
}
