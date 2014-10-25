/**
 * FriendsSyncService.java
 *
 * 19.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Friends Sync Service.
 *
 * @author MajaDobnik
 *
 */
public class FriendsSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static FriendsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new FriendsSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}


