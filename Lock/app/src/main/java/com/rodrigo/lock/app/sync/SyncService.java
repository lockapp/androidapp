package com.rodrigo.lock.app.sync;

/**
 * Created by Rodrigo on 27/01/2018.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private SyncAdapter syncAdapter;

    /**
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        synchronized (SyncAdapter.class) {
            syncAdapter = setupSyncAdapter();
        }
    }

    /**
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }

    private SyncAdapter setupSyncAdapter() {
        return new SyncAdapter(getApplicationContext(), true);
    }
}