package com.koekoetech.clinic.model;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by ZMN on 3/28/18.
 **/
public class ImmunizationModel {

    private static final String TAG = ImmunizationModel.class.getSimpleName();

    private String title;
    private boolean isDone;

    public ImmunizationModel() {
    }

    public ImmunizationModel(String kvPair) {
        Log.d(TAG, "ImmunizationModel() called with: kvPair = [" + kvPair + "]");
        if (!TextUtils.isEmpty(kvPair)) {
            String[] splitStrings = TextUtils.split(kvPair, ":");
            if (splitStrings.length > 0) {
                String title = splitStrings[0];
                String isDone = splitStrings[1];
                boolean isDoneValid = !TextUtils.isEmpty(isDone) && (isDone.equalsIgnoreCase("true") || isDone.equalsIgnoreCase("false"));
                if (!TextUtils.isEmpty(title)) {
                    this.title = title;
                    this.isDone = isDoneValid ? Boolean.valueOf(isDone) : false;
                }
            }
        }
    }

    public ImmunizationModel(String title, boolean isDone) {
        this.title = title;
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String toKVPair() {
        return title + ":" + isDone;
    }
}
