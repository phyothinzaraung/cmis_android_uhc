package com.koekoetech.clinic.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by ZMN on 7/24/18.
 * <p>
 * <pre>
 *     Google Analytics Helper
 * </pre>
 **/
public class GAHelper {

    private static final String TAG = "GAHelper";

    /**
     * Send Analytics For Activity
     *
     * @param screenName Screen (Activity) Name
     * @param activity   Calling Activity
     */
    public static void sendScreenName(@NonNull String screenName, @NonNull Activity activity) {
        Log.d(TAG, "sendScreenName() called with: screenName = [" + screenName + "]");
        final Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.getClass().getSimpleName());
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(activity.getApplicationContext());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    /**
     * Send Analytics For Fragment
     *
     * @param screenName Screen (Fragment) Name
     * @param fragment   Calling Fragment
     */
    public static void sendScreenName(@NonNull String screenName, @NonNull Fragment fragment) {
        Log.d(TAG, "sendScreenName() called with: screenName = [" + screenName + "]");

        @Nullable final FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        final Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, fragment.getClass().getSimpleName());
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(activity.getApplicationContext());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }


    /**
     * Send Analytics For Events
     *
     * @param category Event Category
     * @param action   Action
     */
    public static void sendEvent(@Nullable Context context, @NonNull String category, @NonNull String action) {
        Log.d(TAG, "sendEvent() called with: category = [" + category + "], action = [" + action + "]");

        if (context == null) {
            return;
        }

        final Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, category);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, action);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

//    /**
//     * Send Analytics For Exceptions
//     *
//     * @param e Exception Instance
//     */
//    public static void sendException(@NonNull Exception e) {
//        Log.d(TAG, "sendException() called with: e = [" + e + "]");
//        Tracker defaultTracker = ClinicApp.getInstance().getDefaultTracker();
//        HitBuilders.ExceptionBuilder exceptionBuilder = new HitBuilders.ExceptionBuilder();
//        StandardExceptionParser standardExceptionParser = new StandardExceptionParser(ClinicApp.getInstance(), Collections.emptyList());
//        String description = standardExceptionParser.getDescription(Thread.currentThread().getName(), e);
//        exceptionBuilder.setDescription(description);
//        exceptionBuilder.setFatal(false);
//        defaultTracker.send(exceptionBuilder.build());
//    }

}
