package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SuggestionWordModel extends RealmObject implements Pageable {

    @PrimaryKey
    private String localID;

    private boolean isOnline;

    @SerializedName("ID")
    @Expose
    private int id;

    @SerializedName("Code")
    @Expose
    private String code;

    @SerializedName("StaffId")
    @Expose
    private int staffId;

    @SerializedName("Source")
    @Expose
    private String source;

    @SerializedName("WordName")
    @Expose
    private String word;

    @SerializedName("Value")
    @Expose
    private String value;

    @SerializedName("Accesstime")
    @Expose
    private String accesstime;

    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("Subject")
    @Expose
    private String subject;

    @SerializedName("IsProblem")
    @Expose
    private String isProblem;

    @SerializedName("IsTreatment")
    @Expose
    private String isTreatment;

    @SerializedName("IsReferral")
    @Expose
    private String isReferral;

    @SerializedName("IsRequest")
    @Expose
    private String isRequest;

    @SerializedName("IsDiagnosis")
    @Expose
    private String isDiagnosis;

    @SerializedName("IsObservation")
    @Expose
    private String isObservation;

    @SerializedName("IsService")
    @Expose
    private String isService;

    @SerializedName("IsReason")
    @Expose
    private String isReason;

    @SerializedName("IsInvestigation")
    @Expose
    private String isInvestigation;

    @SerializedName("IsCare")
    @Expose
    private String isCare;

    @SerializedName("Type")
    @Expose
    private String type;

    private boolean hasSynced;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAccesstime() {
        return accesstime;
    }

    public void setAccesstime(String accesstime) {
        this.accesstime = accesstime;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIsReason() {
        return isReason;
    }

    public void setIsReason(String isReason) {
        this.isReason = isReason;
    }

    public String getIsInvestigation() {
        return isInvestigation;
    }

    public void setIsInvestigation(String isInvestigation) {
        this.isInvestigation = isInvestigation;
    }

    public String getIsCare() {
        return isCare;
    }

    public void setIsCare(String isCare) {
        this.isCare = isCare;
    }

    public String getIsProblem() {
        return isProblem;
    }

    public void setIsProblem(String isProblem) {
        this.isProblem = isProblem;
    }

    public String getIsTreatment() {
        return isTreatment;
    }

    public void setIsTreatment(String isTreatment) {
        this.isTreatment = isTreatment;
    }

    public String getIsReferral() {
        return isReferral;
    }

    public void setIsReferral(String isReferral) {
        this.isReferral = isReferral;
    }

    public String getIsRequest() {
        return isRequest;
    }

    public void setIsRequest(String isRequest) {
        this.isRequest = isRequest;
    }

    public String getIsDiagnosis() {
        return isDiagnosis;
    }

    public void setIsDiagnosis(String isDiagnosis) {
        this.isDiagnosis = isDiagnosis;
    }

    public String getIsObservation() {
        return isObservation;
    }

    public void setIsObservation(String isObservation) {
        this.isObservation = isObservation;
    }

    public String getIsService() {
        return isService;
    }

    public void setIsService(String isService) {
        this.isService = isService;
    }

    public boolean isHasSynced() {
        return hasSynced;
    }

    public void setHasSynced(boolean hasSynced) {
        this.hasSynced = hasSynced;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SuggestionWordModel{" +
                "localID='" + localID + '\'' +
                ", isOnline=" + isOnline +
                ", id=" + id +
                ", code='" + code + '\'' +
                ", staffId=" + staffId +
                ", source='" + source + '\'' +
                ", word='" + word + '\'' +
                ", value='" + value + '\'' +
                ", accesstime='" + accesstime + '\'' +
                ", isDeleted=" + isDeleted +
                ", subject='" + subject + '\'' +
                ", isProblem='" + isProblem + '\'' +
                ", isTreatment='" + isTreatment + '\'' +
                ", isReferral='" + isReferral + '\'' +
                ", isRequest='" + isRequest + '\'' +
                ", isDiagnosis='" + isDiagnosis + '\'' +
                ", isObservation='" + isObservation + '\'' +
                ", isService='" + isService + '\'' +
                ", isReason='" + isReason + '\'' +
                ", isInvestigation='" + isInvestigation + '\'' +
                ", isCare='" + isCare + '\'' +
                ", type='" + type + '\'' +
                ", hasSynced=" + hasSynced +
                '}';
    }
}