package com.koekoetech.clinic.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ICDChapterEntity extends RealmObject {

    @PrimaryKey
    private int chapterId;

    private String title;

    private RealmList<ICDTermGroupEntity> termsGroup;

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

    public RealmList<ICDTermGroupEntity> getTermsGroup() {
        return termsGroup;
    }

    public void setTermsGroup(RealmList<ICDTermGroupEntity> termsGroup) {
        this.termsGroup = termsGroup;
    }
}
