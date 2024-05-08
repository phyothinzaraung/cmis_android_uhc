package com.koekoetech.clinic.interfaces;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by ZMN on 6/14/18.
 **/

public interface UhcPatientDownloadCallbacks {

    /**
     * Context Provider
     *
     * @return Context instance
     */
    Context getContext();

    @NonNull
    LifecycleOwner lifecycleOwner();

    /**
     * Called when patients data initDownloadProcess link request started
     */
    void patientDataRequestOnStart();

    /**
     * Called when patients data initDownloadProcess link request failed
     */
    void patientDataRequestOnFail();

    /**
     * Called when preparing to initDownloadProcess the data file
     */
    void dataDownloadOnPrepare();

    /**
     * Called when data file initDownloadProcess starts or resumes
     */
    void dataDownloadOnStart();

    /**
     * Called each time when patients data initDownloadProcess progress changes
     *
     * @param progressMessage Progress Message
     */
    void dataDownloadProgressUpdate(String progressMessage);

    /**
     * Called when patients data file initDownloadProcess failed
     */
    void dataDownloadOnFailed(String failedUrl);

    /**
     * Called when start to import downloaded patient data
     */
    void onStartDataImport();

    /**
     * Called when completed patient data import
     */
    void onCompleteDataImport();

    /**
     * Called when failed to import patient data
     */
    void onErrorDataImport(String errMsg);

    /**
     * Called when each patient data file is imported (e.g, patients, progresses,progress notes,etc.)
     *
     * @param message ready to use message about status change
     */
    void onDataImportProgress(String message);
}
