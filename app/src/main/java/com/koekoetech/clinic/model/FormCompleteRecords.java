package com.koekoetech.clinic.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ZMN on 4/19/18.
 **/
public class FormCompleteRecords extends RealmObject {

    public static final String FORM_INTEGRATED_RECORD = "IntegratedRecord";
    public static final String FORM_RH_SHORT_TERM = "RHShortTerm";

    @PrimaryKey
    private String localId;
    private String progressCode;
    private String formType;

    public FormCompleteRecords() {
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getProgressCode() {
        return progressCode;
    }

    public void setProgressCode(String progressCode) {
        this.progressCode = progressCode;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }
}
