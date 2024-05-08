package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ICDChapter {

    @Expose
    @SerializedName("LevelId")
    private int chapterId;

    @Expose
    @SerializedName("Level1Title")
    private String title;

    @Expose
    @SerializedName("level2s")
    private List<ICDTermGroup> termsGroup;

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ICDTermGroup> getTermsGroup() {
        return termsGroup;
    }

    public void setTermsGroup(List<ICDTermGroup> termsGroup) {
        this.termsGroup = termsGroup;
    }

    @Override
    public String toString() {
        return "ICDChapter{" +
                "chapterId=" + chapterId +
                ", title='" + title + '\'' +
                ", termsGroup=" + termsGroup +
                '}';
    }
}
