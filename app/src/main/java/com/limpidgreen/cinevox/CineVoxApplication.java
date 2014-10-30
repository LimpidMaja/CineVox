/**
 * CineVoxApplication.java
 *
 * 10.10.2014
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
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.model.GraphUser;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.dao.FriendsContentProvider;
import com.limpidgreen.cinevox.util.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;

/**
 * CineVox Application.
 *
 * @author MajaDobnik
 *
 */
public class CineVoxApplication extends Application {
    private String APIToken;
    private Account mAccount;
    private GraphUser user;
    private String facebookAccessToken;

    public String getAPIToken() {
        return APIToken;
    }

    public void setAPIToken(String APIToken) {
        this.APIToken = APIToken;
    }

    @Override
    public void onCreate() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).build();
        ImageLoader.getInstance().init(config);
    }

    public Account getmAccount() {
        return mAccount;
    }

    public void setmAccount(Account mAccount) {
        this.mAccount = mAccount;
    }

    private void onFetchAuthTokenResult() {

    }

    public void startAuthTokenFetch(Activity activity) {
        Bundle options = new Bundle();
        AccountManager mAccountManager = AccountManager.get(this);

        if (mAccount == null) {
            Account[] userAccounts = mAccountManager
                    .getAccountsByType(Constants.ACCOUNT_TYPE);
             mAccount = userAccounts[0];
        }
        mAccountManager.getAuthToken(mAccount, Constants.API_TOKEN_TYPE, options, activity, new OnAccountManagerComplete(), new Handler(new OnError()));
    }

    public void startAuthTokenFetch() {
        Bundle options = new Bundle();
        AccountManager mAccountManager = AccountManager.get(this);

        if (mAccount == null) {
            Account[] userAccounts = mAccountManager
                    .getAccountsByType(Constants.ACCOUNT_TYPE);
            mAccount = userAccounts[0];
        }
        mAccountManager.getAuthToken(mAccount, Constants.API_TOKEN_TYPE, options, false, new OnAccountManagerComplete(), new Handler(new OnError()));
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
            APIToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(Constants.TAG, "Received authentication token " + APIToken);
            onFetchAuthTokenResult();
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
