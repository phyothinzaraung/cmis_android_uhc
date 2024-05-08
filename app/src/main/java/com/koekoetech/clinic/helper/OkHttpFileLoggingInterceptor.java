package com.koekoetech.clinic.helper;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Rahul Kumar on 26/05/2020.
 *
 * <pre>
 *     OkHTTP File Logging Interceptor
 *
 *     This internally uses {@link HttpLoggingInterceptor} with a custom logger instance
 *     to write logs to file.
 *
 *     It's highly recommended to use for debugging purpose only due to security reasons
 *     and the burden to write to file each and every request/response.
 * </pre>
 **/
@SuppressWarnings("WeakerAccess")
public final class OkHttpFileLoggingInterceptor implements HttpLoggingInterceptor.Logger, Interceptor {

    private static final String TAG = "OkHttpFileLogging";

    private static final String REQUEST_SYMBOL = "-->";

    private static final String RESPONSE_SYMBOL = "<--";

    @NonNull
    private final File logDir;

    @NonNull
    private final Gson gson;

    @NonNull
    private final HttpLoggingInterceptor httpLoggingInterceptor;

    @NonNull
    private final StringBuilder contentBuilder = new StringBuilder();

    /**
     * @param logDir The Directory where network logs are meant to be stored.
     */
    public OkHttpFileLoggingInterceptor(@NonNull File logDir) {
        this.logDir = logDir;
        this.httpLoggingInterceptor = new HttpLoggingInterceptor(this);
        this.httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        contentBuilder.setLength(0);

        //region Prepare Log File
        checkAndCreateDir(logDir);
        final String fileName = readableCurrentTime() + ".txt";
        final Request request = chain.request();
        final String requestPath = getPath(request.url());
        final File logContainerDir = TextUtils.isEmpty(requestPath) ? logDir : new File(logDir, requestPath);
        checkAndCreateDir(logContainerDir);
        final File logFile = new File(logContainerDir, fileName);
        //endregion

        final Response response;

        try {
            response = httpLoggingInterceptor.intercept(chain);
        } finally {
            commitLog(logFile);
            contentBuilder.setLength(0);
        }

        return response;
    }

    @Override
    public void log(@NonNull String message) {
        @NonNull String messageToLog = message.trim();
        if (messageToLog.startsWith("{") || messageToLog.startsWith("[")) {
            messageToLog = formatJson(messageToLog);
        } else if (messageToLog.startsWith(REQUEST_SYMBOL) && contentBuilder.indexOf(REQUEST_SYMBOL) == -1) {
            final String messageWithHeader = contentHeader("REQUEST") + "\n\n" + messageToLog;
            messageToLog = contentBuilder.length() == 0 ? messageWithHeader : "\n" + messageWithHeader;
        } else if (messageToLog.startsWith(RESPONSE_SYMBOL) && contentBuilder.indexOf(RESPONSE_SYMBOL) == -1) {
            messageToLog = "\n" + contentHeader("RESPONSE") + "\n\n" + messageToLog;
        }
        contentBuilder.append(messageToLog).append("\n");
    }

    /**
     * @param httpUrl HttpUrl Instance
     * @return the request path in camel-cased style
     */
    @NonNull
    private String getPath(@Nullable HttpUrl httpUrl) {
        if (httpUrl == null) return "";
        @NonNull final List<String> pathSegments = httpUrl.pathSegments();
        if (pathSegments.isEmpty()) return "";
        @NonNull final List<String> capitalizedPathSegments = new ArrayList<>();
        for (String pathSegment : pathSegments) {
            @Nullable final String capitalizedSegment = capitalize(pathSegment);
            if (!TextUtils.isEmpty(capitalizedSegment)) {
                capitalizedPathSegments.add(capitalizedSegment);
            }
        }
        final String path = TextUtils.join("", capitalizedPathSegments);
        Log.d(TAG, "getPath() returned: " + path);
        return path;
    }

    @NonNull
    private String readableCurrentTime() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.US);
        return simpleDateFormat.format(new Date());
    }

    /**
     * @param str Raw String
     * @return Capitalized version of input string or <code>null</code> if input is null or empty
     */
    @Nullable
    private String capitalize(@Nullable String str) {
        Log.d(TAG, "capitalize() called with: str = [" + str + "]");
        if (TextUtils.isEmpty(str)) return null;
        final String firstLetter = str.substring(0, 1).toUpperCase();
        final String restLetters = str.substring(1).toLowerCase();
        final String capitalized = firstLetter + restLetters;
        Log.d(TAG, "capitalize() returned: " + capitalized);
        return capitalized;
    }

    /**
     * Check whether a directory exists and create that directory if not exists
     *
     * @param dir Directory to check
     */
    private void checkAndCreateDir(@NonNull final File dir) {
        if (!dir.exists()) {
            Log.d(TAG, "intercept: Creating " + dir.getAbsolutePath());
            final boolean isDirCreated = dir.mkdirs();
            Log.d(TAG, "intercept: Created ? : " + isDirCreated);
        }
    }

    /**
     * Writes the collected OkHttp Logs to file
     *
     * @param logFile File instance to be written
     */
    private void commitLog(@NonNull final File logFile) {
        final String content = contentBuilder.toString();
        if (TextUtils.isEmpty(content)) return;
        try {
            final boolean isCreated = logFile.createNewFile();
            Log.d(TAG, "commitLog: Log File Created : " + isCreated);
        } catch (IOException e) {
            Log.e(TAG, "commitLog: ", e);
            return;
        }

        try (final FileOutputStream fos = new FileOutputStream(logFile);
             final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos)
        ) {
            outputStreamWriter.append(content);
            outputStreamWriter.append("\n\n\n");
            fos.flush();
        } catch (Exception e) {
            Log.e(TAG, "commitLog: ", e);
        }
    }

    @NonNull
    private String formatJson(@NonNull final String rawJson) {
        try {
            return gson.toJson(new JsonParser().parse(rawJson));
        } catch (Exception e) {
            Log.e(TAG, "formatJson: ", e);
            return rawJson;
        }
    }

    @NonNull
    private String contentHeader(@NonNull String title) {
        final char[] headerDividerCharArray = new char[75];
        Arrays.fill(headerDividerCharArray, '*');
        final String headerDivider = new String(headerDividerCharArray);
        return headerDivider + " " + title + " " + headerDivider;
    }
}
