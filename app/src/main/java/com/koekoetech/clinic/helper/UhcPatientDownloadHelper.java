package com.koekoetech.clinic.helper;

import android.text.TextUtils;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.koekoetech.clinic.interfaces.ClinicDataImportCallback;
import com.koekoetech.clinic.interfaces.UhcPatientDownloadCallbacks;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import androidx.annotation.NonNull;
import needle.Needle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ZMN on 6/14/18.
 **/
public class UhcPatientDownloadHelper {

    private static final String TAG = UhcPatientDownloadHelper.class.getSimpleName();

    private UhcPatientDownloadCallbacks callback;

    public UhcPatientDownloadHelper(UhcPatientDownloadCallbacks callback) {
        this.callback = callback;
    }

    public void initDownloadProcess(int staffId) {
        Call<String> requestClinicDataFile = ServiceHelper.getClient(callback.getContext()).requestClinicDataFile(staffId);
        callback.patientDataRequestOnStart();
        requestClinicDataFile.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String dataDownloadLink = response.body();
                    if (!TextUtils.isEmpty(dataDownloadLink)) {
                        downloadClinicData(dataDownloadLink);
                    } else {
                        handleError();
                    }
                } else {
                    Log.d(TAG, "onResponse: Response Code : " + response.code());
                    handleError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                handleError();
            }

            private void handleError() {
                callback.patientDataRequestOnFail();
            }
        });
    }

    public void downloadClinicData(final String dataDownloadUrl) {
        final String fileName = "clinic_data.zip";
        final String dirPath = callback.getContext().getFilesDir().getAbsolutePath();
        Log.d(TAG, "downloadClinicData: DIR PATH : " + dirPath);
        DownloadRequest downloadRequest = PRDownloader.download(dataDownloadUrl, dirPath, fileName).build();
        callback.dataDownloadOnPrepare();
        downloadRequest.setOnStartOrResumeListener(() -> callback.dataDownloadOnStart());
        downloadRequest.setOnProgressListener(progress -> {
            Log.d(TAG, "downloadFile: Total Bytes : " + progress.totalBytes + " CurrentBytes : " + progress.currentBytes);
            NumberFormat numberFormat = new DecimalFormat("#0.0");
            double progressPercent = progress.currentBytes * 100.0 / progress.totalBytes;
            double mb = 1024 * 1024;
            double totalBytesInMb = progress.totalBytes / mb;
            double currentBytesInMb = progress.currentBytes / mb;
            String progressMessage = "Downloaded " + numberFormat.format(currentBytesInMb) + " MB of " + numberFormat.format(totalBytesInMb) + " MB (" + numberFormat.format(progressPercent) + " % )";
            callback.dataDownloadProgressUpdate(progressMessage);
        });
        downloadRequest.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                callback.onStartDataImport();
                Needle.onBackgroundThread().execute(new ClinicDataImportTask(callback.lifecycleOwner(), new ClinicDataImportCallback() {
                    @Override
                    public void onStart() {
                        callback.onStartDataImport();
                    }

                    @Override
                    public void onComplete() {
                        callback.onCompleteDataImport();
                    }

                    @Override
                    public void onError(String errMsg) {
                        callback.onErrorDataImport(errMsg);
                    }

                    @Override
                    public void onStatusChange(String message) {
                        callback.onDataImportProgress(message);
                    }
                }, fileName, dirPath));
            }

            @Override
            public void onError(Error error) {
                callback.dataDownloadOnFailed(dataDownloadUrl);
            }
        });

    }

}
