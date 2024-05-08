package com.koekoetech.clinic.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by Zaw Myo Naing on 11/15/2016.
 **/

@SuppressWarnings({"unused"})
public class PrerequisiteCheckingsHelper {

//    /**
//     * Helper method to check whether GooglePlayServices Framework is installed or not
//     *
//     * @param context
//     * @return
//     */
//    public static boolean isGooglePlayServicesAvailable(Activity context) {
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
//
//        if (status == ConnectionResult.SUCCESS) {
//            return true;
//        } else if (googleApiAvailability.isUserResolvableError(status)) {
//            googleApiAvailability.getErrorDialog(context, status, 2404).show();
//            return false;
//        } else {
//            Toast.makeText(context, "Cannot connnect to mapping Service", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }

    /**
     * Helper method to check if internet (WIFI or Data) is available on or not
     *
     * @param context Context Instance
     * @return True if internet connection is available, false otherwise
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Helper mehtod to check if GPS is turned on or not
     *
     * @param context Context Instance
     * @return True if GPS is turned on, false otherwise
     */
    public static boolean isGpsOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
        if (activityManager != null) {
            appProcesses = activityManager.getRunningAppProcesses();
        }
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
