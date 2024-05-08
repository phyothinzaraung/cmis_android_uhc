package com.koekoetech.clinic.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.RealmAccessHelper;
import com.koekoetech.clinic.helper.ServiceHelper;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatientViewModel;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.realm.Realm;
import retrofit2.Response;

/**
 * Created by ZMN on 8/16/17.
 **/

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();

    private ServiceHelper.ApiService apiService;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        apiService = ServiceHelper.getClient(context);
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult
    ) {

        Log.i(TAG, "onPerformSync: Started Syncing");

        int syncTriggerTask = extras.getInt(SyncConstants.SYNC_PARAM);
//        realm = Realm.getDefaultInstance();

        try (final Realm realm = Realm.getDefaultInstance()) {
            switch (syncTriggerTask) {
                case SyncConstants.POST_PATIENTS:
                    Log.i(TAG, "onPerformSync: POSTING PATIENTS");
                    realm.executeTransaction(realm1 -> syncPatientRecords(realm));
                    break;
                case SyncConstants.POST_SUGGESTIONS:
                    Log.i(TAG, "onPerformSync: Posting Suggestions");
                    syncSuggestions(realm);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "onPerformSync: Syncing Finished");
    }

    private void syncPatientRecords(@NonNull final Realm realm) {
        Log.d(TAG, "syncPatientRecords() called");
        broadcastSyncStart(SyncConstants.POST_PATIENTS);
        Log.d(TAG, "syncPatientRecords: Syncing Patient records.");
        int totalRecordToUpload = 0;
        int uploadedRecordsCount = 0;
        try {
            final List<UhcPatientViewModel> patientsToUpload = RealmAccessHelper.getPatientsToUpload(realm);
            totalRecordToUpload = patientsToUpload.size();

            for (UhcPatientViewModel pvm : patientsToUpload) {
                if (pvm == null) continue;
                final String progressMessage = "Posting Patients Records (" + (uploadedRecordsCount + 1) + " / " + totalRecordToUpload + ")";
                broadcastSyncProgress(progressMessage, SyncConstants.POST_PATIENTS);
                final String patientCode = pvm.getPatientCode();
                if (TextUtils.isEmpty(patientCode)) continue;
                @Nullable final UhcPatientViewModel pvmResponse = uploadPatientRecord(pvm);
                if (pvmResponse != null) {
                    if (RealmAccessHelper.swapPatientViewModel(realm, pvm, pvmResponse)) {
                        final File patientStorageDir = new File(FileHelper.getStorageDir(getContext()), patientCode);
                        FileHelper.delete(patientStorageDir);
                        uploadedRecordsCount += 1;
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "syncPatientRecords: ", e);
        }

        Log.d(TAG, "syncPatientRecords: Synced " + uploadedRecordsCount + " patient records of " + totalRecordToUpload);
        final boolean allPatientsSynced = totalRecordToUpload == uploadedRecordsCount;
        broadcastSyncStop(allPatientsSynced, SyncConstants.POST_PATIENTS);
    }

    @Nullable
    private UhcPatientViewModel uploadPatientRecord(@NonNull final UhcPatientViewModel pvm) {
        try {
            final Response<UhcPatientViewModel> callPatientUpload = apiService.patientSyncUpstream(pvm).execute();
            if (callPatientUpload.isSuccessful()) {
                Log.d(TAG, "uploadPatientRecord: Patient uploaded successfully");
                return callPatientUpload.body();
            }
        } catch (Exception e) {
            Log.e(TAG, "uploadPatientRecord: Failed to upload ", e);
        }

        return null;
    }

    private void syncSuggestions(@NonNull final Realm realm) {
        Log.d(TAG, "syncSuggestions() called");
        broadcastSyncStart(SyncConstants.POST_SUGGESTIONS);
        Log.d(TAG, "syncSuggestions: Syncing Suggestion Word Models.");
        int totalRecordToUpload = 0;
        int uploadedRecordsCount = 0;

        try {
            final List<SuggestionWordModel> suggestionsToUpload = RealmAccessHelper.getSuggestionsToUpload(realm);
            totalRecordToUpload = suggestionsToUpload.size();

            for (SuggestionWordModel suggestionWordModel : suggestionsToUpload) {
                if (suggestionWordModel == null) continue;
                final String progressMessage = "Posting Suggestions ( " + (uploadedRecordsCount + 1) + "/" + totalRecordToUpload + " )";
                broadcastSyncProgress(progressMessage, SyncConstants.POST_SUGGESTIONS);
                @Nullable final SuggestionWordModel swmResponse = uploadSuggestionWord(suggestionWordModel);
                if (swmResponse != null) {
                    swmResponse.setLocalID(suggestionWordModel.getLocalID());
                    swmResponse.setHasSynced(true);
                    swmResponse.setOnline(true);
                    realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(swmResponse));
                    uploadedRecordsCount += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "syncPatientRecords: Synced " + uploadedRecordsCount + " suggestion words of " + totalRecordToUpload);
        final boolean allSuggestionWordsSynced = totalRecordToUpload == uploadedRecordsCount;
        broadcastSyncStop(allSuggestionWordsSynced, SyncConstants.POST_SUGGESTIONS);
    }

    @Nullable
    private SuggestionWordModel uploadSuggestionWord(@NonNull final SuggestionWordModel swm) {
        try {
            final Response<SuggestionWordModel> callPostSuggestion = ServiceHelper.getClient(getContext()).postSuggestions(swm).execute();
            if (callPostSuggestion.isSuccessful()) {
                Log.d(TAG, "uploadSuggestionWord: Suggestion word model uploaded successfully.");
                return callPostSuggestion.body();
            }

        } catch (Exception e) {
            Log.e(TAG, "uploadSuggestionWord: ", e);
        }
        return null;
    }

    private void broadcastSyncStart(int syncParam) {
        Intent intent = new Intent();
        intent.setAction(SyncConstants.SYNC_STATUS_BROADCAST);
        intent.putExtra(SyncConstants.SYNC_STATUS, SyncConstants.SYNC_STATUS_START);
        intent.putExtra(SyncConstants.SYNC_PARAM, syncParam);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void broadcastSyncStop(boolean syncResult, int syncParam) {
        Intent intent = new Intent();
        intent.setAction(SyncConstants.SYNC_STATUS_BROADCAST);
        intent.putExtra(SyncConstants.SYNC_STATUS, SyncConstants.SYNC_STATUS_STOP);
        intent.putExtra(SyncConstants.SYNC_RESULT, syncResult);
        intent.putExtra(SyncConstants.SYNC_PARAM, syncParam);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void broadcastSyncProgress(String progressMsg, int syncParam) {
        Intent intent = new Intent();
        intent.setAction(SyncConstants.SYNC_STATUS_BROADCAST);
        intent.putExtra(SyncConstants.SYNC_STATUS, SyncConstants.SYNC_STATUS_PROGRESS);
        intent.putExtra(SyncConstants.SYNC_PROGRESS_MSG, progressMsg);
        intent.putExtra(SyncConstants.SYNC_PARAM, syncParam);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

}
