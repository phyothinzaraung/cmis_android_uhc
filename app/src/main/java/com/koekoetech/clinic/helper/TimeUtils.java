package com.koekoetech.clinic.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ZMN on 8/31/17.
 **/

public class TimeUtils {

    private static final String TAG = TimeUtils.class.getSimpleName();

    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date now = new Date();
        return sdf.format(now);
    }

    public static String nowWithoutSpace() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        return sdf.format(now);
    }

    public static String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date now = new Date();
        return sdf.format(now);
    }

    public static String time() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        Date now = new Date();
        return sdf.format(now);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        if (date == null) {
            return "";
        } else {
            return sdf.format(date);
        }
    }

    public static boolean validateDateFormat(String dateToValidate, String dateFormat) {
        Log.d(TAG, "validateDateFormat() called with: dateToValidate = [" + dateToValidate + "], dateFormat = [" + dateFormat + "]");
        if (dateToValidate == null || dateToValidate.isEmpty() || dateFormat == null || dateFormat.isEmpty()) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            Log.d(TAG, "validateDateFormat: VALID");
            return true;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }


}
