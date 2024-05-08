package com.koekoetech.clinic.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by ZMN on 8/17/17.
 **/
@SuppressWarnings({"unused"})
public class SyncUtils {

    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager != null && accountManager.addAccountExplicitly(account, null, null)) {

            newAccount = true;
        }

        if (newAccount || !setupComplete) {

            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).apply();
        }
    }

    public static boolean isPeriodicSyncScheduled() {
        return !ContentResolver.getPeriodicSyncs(GenericAccountService.getAccount(), SyncConstants.AUTHORITY).isEmpty();
    }

    public static void enableAutoSync() {
        ContentResolver.setMasterSyncAutomatically(true);
        ContentResolver.setSyncAutomatically(GenericAccountService.getAccount(), SyncConstants.AUTHORITY, true);
    }

    public static void setStickySync() {
        ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, which -> SyncUtils.enableAutoSync());
    }

    public static void postPatients() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putInt(SyncConstants.SYNC_PARAM, SyncConstants.POST_PATIENTS);
        ContentResolver.requestSync(
                GenericAccountService.getAccount(),
                SyncConstants.AUTHORITY,
                b);
    }

    public static void postSuggestions() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putInt(SyncConstants.SYNC_PARAM, SyncConstants.POST_SUGGESTIONS);
        ContentResolver.requestSync(
                GenericAccountService.getAccount(),
                SyncConstants.AUTHORITY,
                b);
    }

    public static boolean isSyncActive() {
        Account account = GenericAccountService.getAccount();
        String authority = SyncConstants.AUTHORITY;
        for (SyncInfo info : ContentResolver.getCurrentSyncs()) {
            if (info.account.equals(account) && info.authority.equals(authority)) {
                return true;
            }
        }
        return false;
    }

//    public static void syncPatients() {
//        Bundle b = new Bundle();
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        b.putInt(SyncConstants.SYNC_PARAM, SyncConstants.SYNC_PATIENTS);
//        ContentResolver.requestSync(
//                GenericAccountService.getAccount(),
//                SyncConstants.AUTHORITY,
//                b);
//    }
//
//    public static void postAndSyncPatients() {
//        Bundle b = new Bundle();
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        b.putInt(SyncConstants.SYNC_PARAM, SyncConstants.POST_SYNC_PATIENTS);
//        ContentResolver.requestSync(
//                GenericAccountService.getAccount(),
//                SyncConstants.AUTHORITY,
//                b);
//    }
}
