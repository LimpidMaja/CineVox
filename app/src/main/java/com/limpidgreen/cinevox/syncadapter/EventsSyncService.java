/**
 * EventsSyncService.java
 *
 * 13.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Events Sync Service.
 *
 * @author MajaDobnik
 *
 */
public class EventsSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static EventsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new EventsSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}


