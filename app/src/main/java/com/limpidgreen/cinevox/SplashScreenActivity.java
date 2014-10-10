/**
 * SplashScreenActivity.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
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
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;

/**
 * Splash Screen Activity.
 *
 * @author MajaDobnik
 *
 */
public class SplashScreenActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this, getText(R.string.noInternet),
                    Toast.LENGTH_LONG);
            toast.show();
        } else {

            mApplication = ((CineVoxApplication) getApplication());
            mAccountManager = AccountManager.get(this);
            Account[] userAccounts = mAccountManager
                    .getAccountsByType(Constants.ACCOUNT_TYPE);

            if (userAccounts.length == 1) {
                mAccount = userAccounts[0];

                Log.i(Constants.TAG, "START SESSION: " + Session.getActiveSession());
                // start Facebook Login
                if (Session.getActiveSession() == null) {
                    Session.openActiveSession(this, true, new Session.StatusCallback() {

                        // callback when session changes state
                        @Override
                        public void call(Session session, SessionState state, Exception exception) {
                            if (session.isOpened()) {
                                Log.i(Constants.TAG, "SESSION OPEN");
                                startAuthTokenFetch();
                            }
                        }
                    });
                } else {
                    startAuthTokenFetch();
                } // end if-else
            } else {
                if (userAccounts.length > 1) {
                    for (Account userAccount : userAccounts) {
                        mAccountManager.removeAccount(userAccount, null, null);
                    } // end for
                } // end if
                mAccount = null;
                mAccountManager.addAccount(
                        Constants.ACCOUNT_TYPE,
                        Constants.API_TOKEN_TYPE,
                        null,
                        new Bundle(),
                        this,
                        new OnAccountAddComplete(),
                        null);
            } // end if-else
        } // end if-else
    } // end onCreate()

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    private void onFetchAuthTokenResult() {
        mApplication.setAPIToken(mAuthToken);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startAuthTokenFetch() {
        Bundle options = new Bundle();
        mAccountManager.getAuthToken(
                mAccount,
                Constants.API_TOKEN_TYPE,
                options,
                this,
                new OnAccountManagerComplete(),
                new Handler(new OnError())
        );
    }

    private class OnAccountManagerComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(Constants.TAG, "Received authentication token " + mAuthToken);
            onFetchAuthTokenResult();
        }
    }

    private class OnAccountAddComplete implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            Bundle bundle;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
                return;
            } catch (AuthenticatorException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mAccount = new Account(
                    bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                    bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
            Log.d(Constants.TAG, "Added account " + mAccount.name + ", fetching");
            startAuthTokenFetch();
        }
    }

    public class OnError implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            Log.e("onError","ERROR");
            return false;
        }
    }
}
