package com.koekoetech.clinic.interfaces;

/**
 * Created by ZMN on 2/13/18.
 **/

public interface ClinicDataImportCallback {

    void onStart();

    void onComplete();

    void onError(String errMsg);

    void onStatusChange(String message);
}
