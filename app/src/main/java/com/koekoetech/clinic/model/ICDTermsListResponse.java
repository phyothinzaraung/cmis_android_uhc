package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZMN on 9/27/18.
 **/
public class ICDTermsListResponse {

    @Expose
    @SerializedName("icd10s")
    private List<ICDChapter> icdChapters;

    public List<ICDChapter> getIcdChapters() {
        return icdChapters;
    }

    public void setIcdChapters(List<ICDChapter> icdChapters) {
        this.icdChapters = icdChapters;
    }

    @Override
    public String toString() {
        return "ICDTermsListResponse{" +
                "icdChapters=" + icdChapters +
                '}';
    }
}
