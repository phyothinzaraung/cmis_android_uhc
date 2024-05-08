package com.koekoetech.clinic.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Ko Pyae on 3/21/2017.
 */

public class AuthenticationModel extends RealmObject {

    @Expose
    @SerializedName("StaffId")
    private int StaffId;

    @Expose
    @SerializedName("StaffCode")
    private String StaffCode;

    @Expose
    @SerializedName("StaffName")
    private String StaffName;

    @Expose
    @SerializedName("LoginTime")
    private String LoginTime;

    @Expose
    @SerializedName("LogoutTime")
    private String LogoutTime;

    @Expose
    @SerializedName("RoleCode")
    private String roleCode;

    @Expose
    @SerializedName("FacilityName")
    private String facilityName;

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
    @SerializedName("PhotoUrl")
    private String PhotoUrl;

    @Expose
    @SerializedName("Status")
    private String status;

    @Expose
    @SerializedName("DivisionState")
    private String divisionState;

    @Expose
    @SerializedName("Township")
    private String township;

    @Expose
    @SerializedName("PermittedScreens")
    private RealmList<String> permittedScreens;

    public int getStaffId() {
        return StaffId;
    }

    public void setStaffId(int staffId) {
        StaffId = staffId;
    }

    public String getStaffCode() {
        return StaffCode;
    }

    public void setStaffCode(String staffCode) {
        StaffCode = staffCode;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String staffName) {
        StaffName = staffName;
    }

    public String getLoginTime() {
        return LoginTime;
    }

    public void setLoginTime(String loginTime) {
        LoginTime = loginTime;
    }

    public String getLogoutTime() {
        return LogoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        LogoutTime = logoutTime;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
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

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDivisionState() {
        return divisionState;
    }

    public void setDivisionState(String divisionState) {
        this.divisionState = divisionState;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public RealmList<String> getPermittedScreens() {
        return permittedScreens;
    }

    public void setPermittedScreens(RealmList<String> permittedScreens) {
        this.permittedScreens = permittedScreens;
    }

    public boolean isPermitted(String formOrReport) {
        if (permittedScreens != null && !TextUtils.isEmpty(formOrReport)) {
            for (String screen : permittedScreens) {
                if (screen.equalsIgnoreCase(formOrReport)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "AuthenticationModel{" +
                "StaffId=" + StaffId +
                ", StaffCode='" + StaffCode + '\'' +
                ", StaffName='" + StaffName + '\'' +
                ", LoginTime='" + LoginTime + '\'' +
                ", LogoutTime='" + LogoutTime + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", FacilityCode='" + FacilityCode + '\'' +
                ", FacilityTitle='" + FacilityTitle + '\'' +
                ", FacilityType='" + FacilityType + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                ", status='" + status + '\'' +
                ", divisionState='" + divisionState + '\'' +
                ", township='" + township + '\'' +
                ", permittedScreens=" + permittedScreens +
                '}';
    }
}
