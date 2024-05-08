package com.koekoetech.clinic.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ICDTermGroupEntity extends RealmObject {

    @PrimaryKey
    private int termGroupId;

    private String title;

    private RealmList<String> terms;

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

    public RealmList<String> getTerms() {
        return terms;
    }

    public void setTerms(RealmList<String> terms) {
        this.terms = terms;
    }
}
