package com.koekoetech.clinic.model;

import android.net.Uri;

/**
 * Created by ZMN on 7/24/18.
 **/
public class ImageCropperSaveResult {

    private Uri newAddedIMGUri;

    private String filePath;

    public ImageCropperSaveResult(Uri newAddedIMGUri, String filePath) {
        this.newAddedIMGUri = newAddedIMGUri;
        this.filePath = filePath;
    }

    public Uri getNewAddedIMGUri() {
        return newAddedIMGUri;
    }

    public void setNewAddedIMGUri(Uri newAddedIMGUri) {
        this.newAddedIMGUri = newAddedIMGUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "ImageCropperSaveResult{" +
                "newAddedIMGUri=" + newAddedIMGUri +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
