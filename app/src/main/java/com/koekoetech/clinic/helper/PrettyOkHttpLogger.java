package com.koekoetech.clinic.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import okhttp3.logging.HttpLoggingInterceptor;

final class PrettyOkHttpLogger implements HttpLoggingInterceptor.Logger {

    private static final String TAG = "okhttp";

    @Override
    public void log(@NonNull String message) {
        final String messageToLog = message.trim();
        if (messageToLog.startsWith("{") || messageToLog.startsWith("[")) {
            try {
                final String formattedJson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(new JsonParser().parse(messageToLog));
                Log.d(TAG, formattedJson);
                return;
            } catch (JsonSyntaxException e) {
                // do nothing
            }
        }

        Log.d(TAG, messageToLog);
    }
}
