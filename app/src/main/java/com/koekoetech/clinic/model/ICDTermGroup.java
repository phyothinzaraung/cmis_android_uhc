package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ICDTermGroup {

    @Expose
    @SerializedName("Level2Id")
    private int termGroupId;

    @Expose
    @SerializedName("Level2Title")
    private String title;

    @Expose
    @SerializedName("Level3Titles")
    private List<String> terms;

    public int getTermGroupId() {
        return termGroupId;
    }

    public void setTermGroupId(int termGroupId) {
        this.termGroupId = termGroupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    @Override
    public String toString() {
        return "ICDTermGroup{" +
                "termGroupId=" + termGroupId +
                ", title='" + title + '\'' +
                ", terms=" + terms +
                '}';
    }
}
