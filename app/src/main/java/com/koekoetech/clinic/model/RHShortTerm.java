package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import java.io.Serializable;

/**
 * Created by Ko Pyae on 6/12/2017.
 */

public class RHShortTerm implements Serializable,Pageable {
    @Expose
    @SerializedName("Id")
    private int id;

    @Expose
    @SerializedName("ClientType")
    private String ClientType ;

    @Expose
    @SerializedName("Method")
    private String Method;

    @Expose
    @SerializedName("AgeGroup")
    private String AgeGroup;

    @Expose
    @SerializedName("PatientCount")
    private int PatientCount;

    @Expose
    @SerializedName("IsDeleted")
    private boolean IsDeleted;

    @Expose
    @SerializedName("Accesstime")
    private String Accesstime;

    @Expose
    @SerializedName("PatientCode")
    private String patientCode;

    @Expose
    @SerializedName("UHCRegistrationNameInEnglish")
    private String name;

    @Expose
    @SerializedName("FacilityCode")
    private String FacilityCode;

    @Expose
    @SerializedName("FacilityTitle")
    private String FacilityTitle;

    @Expose
    @SerializedName("FacilityType")
    private String FacilityType;

    @Expose
    @SerializedName("ProgressCode")
    private String progressCode;

    @Expose
    @SerializedName("CreatedTime")
    private String createdTime;

    @Expose
    @SerializedName("DoctorCode")
    private String doctorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientType() {
        return ClientType;
    }

    public void setClientType(String clientType) {
        ClientType = clientType;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public String getAgeGroup() {
        return AgeGroup;
    }

    public void setAgeGroup(String ageGroup) {
        AgeGroup = ageGroup;
    }

    public int getPatientCount() {
        return PatientCount;
    }

    public void setPatientCount(int patientCount) {
        PatientCount = patientCount;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public String getAccesstime() {
        return Accesstime;
    }

    public void setAccesstime(String accesstime) {
        Accesstime = accesstime;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacilityCode() {
        return FacilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        FacilityCode = facilityCode;
    }

    public String getFacilityTitle() {
        return FacilityTitle;
    }

    public void setFacilityTitle(String facilityTitle) {
        FacilityTitle = facilityTitle;
    }

    public String getFacilityType() {
        return FacilityType;
    }

    public void setFacilityType(String facilityType) {
        FacilityType = facilityType;
    }

    public String getProgressCode() {
        return progressCode;
    }

    public void setProgressCode(String progressCode) {
        this.progressCode = progressCode;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }
}
