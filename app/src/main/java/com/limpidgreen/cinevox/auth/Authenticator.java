/**
 * Authenticator.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.limpidgreen.cinevox.R;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

/**
 * Authenticator.
 *
 * @author MajaDobnik
 *
 */
public class Authenticator extends AbstractAccountAuthenticator {
    // Authentication Service context
    private final Context mContext;

    /**
     * Constructor.
     *
     * @param context
     */
    public Authenticator(Context context) {
        super(context);
        mContext = context;
    } // end Authenticator()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#addAccount(android.accounts
     * .AccountAuthenticatorResponse, java.lang.String, java.lang.String,
     * java.lang.String[], android.os.Bundle)
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options) {
        Log.i(Constants.TAG, "ADD ACOUNT AUTHENTICATOR");
        final Intent intent = new Intent(mContext, FBLoginActivity.class);
        intent.putExtra(Constants.API_TOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    } // end addAccount()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#confirmCredentials(android
     * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
     * android.os.Bundle)
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account, Bundle options) {
        return null;
    } // end confirmCredentials()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#editProperties(android.
     * accounts.AccountAuthenticatorResponse, java.lang.String)
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        throw new UnsupportedOperationException();
    } // end editProperties()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#getAuthToken(android.accounts
     * .AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String, android.os.Bundle)
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account, String authTokenType, Bundle loginOptions)
            throws NetworkErrorException {

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(Constants.API_TOKEN_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, mContext
                    .getText(R.string.invalid_token).toString());
            return result;
        } // edn if
        final AccountManager am = AccountManager.get(mContext);

        // See if there is already an authentication token stored
        String authToken = am.peekAuthToken(account, authTokenType);
        // If we have no token, use the account credentials to fetch
        // a new one, effectively another logon
        if (TextUtils.isEmpty(authToken)) {
            /*final String password = am.getPassword(account);
            if (password != null) {
                authToken = fetchTokenFromCredentials(account.name, password, authTokenType)
            }*/
        } // end if

        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our FBLoginActivity panel.
        final Intent intent = new Intent(mContext, FBLoginActivity.class);
        intent.putExtra(Constants.PARAM_USERNAME, account.name);
        intent.putExtra(Constants.ACCOUNT_TYPE, account.type);
        intent.putExtra(Constants.API_TOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    } // end getAuthToken()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#getAuthTokenLabel(java.
     * lang.String)
     */
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // null means we don't support multiple authToken types
        return null;
    } // end getAuthTokenLabel()

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#hasFeatures(android.accounts
     * .AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String[])
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) {
        // This call is used to query whether the Authenticator supports
        // specific features. We don't expect to get called, so we always
        // return false (no) for any queries.
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    } // end hasFeatures(

    /*
     * (non-Javadoc)
     *
     * @see
     * android.accounts.AbstractAccountAuthenticator#updateCredentials(android
     * .accounts.AccountAuthenticatorResponse, android.accounts.Account,
     * java.lang.String, android.os.Bundle)
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account, String authTokenType, Bundle loginOptions) {
        return null;
    } // end updateCredentials()
}
