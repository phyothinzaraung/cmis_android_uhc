package com.koekoetech.clinic.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.PrerequisiteCheckingsHelper;
import com.koekoetech.clinic.sync.SyncConstants;
import com.koekoetech.clinic.sync.SyncUtils;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by ZMN on 8/31/17.
 **/

public class DataSyncActivity extends BaseActivity {

    public static final String EXTRA_SHOULD_SHOW_LOCK = "ExtraShouldShowLockAfterSync";
    private static final String TAG = DataSyncActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private TextView textViewSyncMessage;
    private Button btnDataSyncRetry;
    private Button btnLater;
    private SyncStatusReceiver syncStatusReceiver;
    private boolean hasAllPatientsSynced;
    private LocalBroadcastManager localBroadcastManager;

    public static Intent getNewIntent(Context context, boolean shouldShowLockAfterSync) {
        Intent intent = new Intent(context, DataSyncActivity.class);
        intent.putExtra(EXTRA_SHOULD_SHOW_LOCK, shouldShowLockAfterSync);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_data_sync;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        SyncUtils.enableAutoSync();
        SyncUtils.setStickySync();

        progressBar = findViewById(R.id.dataSync_Progress);
        textViewSyncMessage = findViewById(R.id.dataSync_InfoMessage);
        btnDataSyncRetry = findViewById(R.id.dataSync_btnRetry);
        btnLater = findViewById(R.id.dataSync_btnLater);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        syncStatusReceiver = new SyncStatusReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncConstants.SYNC_STATUS_BROADCAST);
        localBroadcastManager.registerReceiver(syncStatusReceiver, intentFilter);

        textViewSyncMessage.setText(R.string.msg_info_data_sync_progress);

        btnDataSyncRetry.setOnClickListener(view -> startSync(SyncConstants.POST_PATIENTS));

        btnLater.setOnClickListener(v -> goToHome());
    }

    @Override
    protected void onStart() {
        super.onStart();
        process();
    }

    @Override
    protected void onDestroy() {
        if (localBroadcastManager != null && syncStatusReceiver != null) {
            localBroadcastManager.unregisterReceiver(syncStatusReceiver);
        }
        super.onDestroy();
    }

    private void startSync(int syncParam) {
        if (PrerequisiteCheckingsHelper.isInternetAvailable(this)) {
            switch (syncParam) {
                case SyncConstants.POST_PATIENTS:
                    SyncUtils.postPatients();
                    break;
                case SyncConstants.POST_SUGGESTIONS:
                    SyncUtils.postSuggestions();
                    break;
            }
        } else {
            btnLater.setVisibility(View.VISIBLE);
            btnDataSyncRetry.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            textViewSyncMessage.setText(R.string.msg_info_data_sync_internet);
        }
    }

    private void process() {
        if (SyncUtils.isSyncActive()) {
            Log.d(TAG, "process: Sync Was Already Running!");
            btnDataSyncRetry.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textViewSyncMessage.setText(R.string.msg_info_data_sync_progress);
        } else {
            startSync(SyncConstants.POST_PATIENTS);
        }
    }

    private void goToHome() {
        boolean shouldShowLock = getIntent().getBooleanExtra(EXTRA_SHOULD_SHOW_LOCK, true);
        Intent i = new Intent(DataSyncActivity.this, shouldShowLock ? CustomPassActivity.class : HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @NonNull
    private String getSyncMessage(int syncParam) {
        switch (syncParam) {
            case SyncConstants.POST_PATIENTS:
                return "Uploading Patients Data";
            case SyncConstants.POST_SUGGESTIONS:
                return "Uploading Suggestions";
            default:
                return getString(R.string.msg_info_data_sync_progress);
        }
    }

    class SyncStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String syncStatus = intent.getStringExtra(SyncConstants.SYNC_STATUS);
            int syncParam = intent.getIntExtra(SyncConstants.SYNC_PARAM, -1);
            Log.d(TAG, "onReceive: SyncStatusReceived : " + syncStatus);

            if (TextUtils.isEmpty(syncStatus)) {
                goToHome();
                return;
            }

            switch (syncStatus) {
                case SyncConstants.SYNC_STATUS_START:
                    btnLater.setVisibility(View.GONE);
                    btnDataSyncRetry.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    textViewSyncMessage.setText(getSyncMessage(syncParam));
                    break;
                case SyncConstants.SYNC_STATUS_PROGRESS:
                    String progressMessage = intent.getStringExtra(SyncConstants.SYNC_PROGRESS_MSG);
                    Log.d(TAG, "onReceive: Get Progress Broadcast");
                    if (!TextUtils.isEmpty(progressMessage)) {
                        textViewSyncMessage.setText(progressMessage);
                    }
                    break;
                case SyncConstants.SYNC_STATUS_STOP:
                    boolean syncResult = intent.getBooleanExtra(SyncConstants.SYNC_RESULT, false);
                    switch (syncParam) {
                        case SyncConstants.POST_PATIENTS:
                            hasAllPatientsSynced = syncResult;
                            startSync(SyncConstants.POST_SUGGESTIONS);
                            break;
                        case SyncConstants.POST_SUGGESTIONS:
                            if (!hasAllPatientsSynced || !syncResult) {
                                btnLater.setVisibility(View.VISIBLE);
                                btnDataSyncRetry.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                textViewSyncMessage.setText(R.string.msg_err_data_sync_fail);
                            } else {
                                goToHome();
                            }
                            break;
                        default:
                            break;
                    }
//                    if (syncResult) {
//                        if (syncParam == SyncConstants.POST_PATIENTS) {
//                            startSync(SyncConstants.POST_SUGGESTIONS);
//                        } else {
//                            goToHome();
//                        }
//                    } else {
//                        btnLater.setVisibility(View.VISIBLE);
//                        btnDataSyncRetry.setVisibility(View.VISIBLE);
//                        progressBar.setVisibility(View.GONE);
//                        textViewSyncMessage.setText(R.string.msg_err_data_sync_fail);
//                    }
                    break;
            }
        }
    }
}
