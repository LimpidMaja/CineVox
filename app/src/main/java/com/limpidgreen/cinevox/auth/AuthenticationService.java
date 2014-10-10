/**
 * AuthenticationService.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 *
 * @author MajaDobnik
 */
public class AuthenticationService extends Service {
    private Authenticator mAuthenticator;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    } // end onCreate()

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    } // end onBind()
}
