package com.koekoetech.clinic.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * Created by ZMN on 8/16/17.
 **/

public class SyncService extends Service {

    private static final String TAG = SyncService.class.getSimpleName();
    private static final Object SYNC_ADAPTER_LOCK = new Object();
    private static SyncAdapter syncAdapter = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: SyncService Created!");
        synchronized (SYNC_ADAPTER_LOCK) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: SyncService Destroyed!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
