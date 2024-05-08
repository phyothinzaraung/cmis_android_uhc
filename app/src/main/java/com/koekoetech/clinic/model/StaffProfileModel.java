package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import java.io.Serializable;

public class StaffProfileModel implements Cloneable, Pageable, Serializable {

    @Expose
    @SerializedName("ID")
    private int iD;

    @Expose
    @SerializedName("Code")
    private String code;

    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("JobTitle")
    private String jobTitle;

    @Expose
    @SerializedName("MobilePhone")
    private String mobilePhone;

    @Expose
    @SerializedName("HomePhone")
    private String homePhone;

    @Expose
    @SerializedName("Email")
    private String email;

    @Expose
    @SerializedName("Address")
    private String address;

    @Expose
    @SerializedName("Username")
    private String username;

    @Expose
    @SerializedName("Password")
    private String password;

    @Expose
    @SerializedName("Status")
    private String status;

    @Expose
    @SerializedName("Accesstime")
    private String accesstime;

    @Expose
    @SerializedName("FacilityCode")
    private String facilityCode;

    @Expose
    @SerializedName("FacilityName")
    private String facilityName;

    @Expose
    @SerializedName("DOB")
    private String dOB;

    @Expose
    @SerializedName("DivisionState")
    private String divisionState;

    @Expose
    @SerializedName("Township")
    private String township;

    @Expose
    @SerializedName("RoleCode")
    private String roleCode;

    @Expose
    @SerializedName("Nationality")
    private String nationality;

    @Expose
    @SerializedName("Religion")
    private String religion;

    @Expose
    @SerializedName("Education")
    private String education;

    @Expose
    @SerializedName("CodeIndex")
    private int codeIndex;

    @Expose
    @SerializedName("Qualification")
    private String qualification;

    @Expose
    @SerializedName("ExQualification")
    private String exQualification;

    @Expose
    @SerializedName("FacilityTitle")
    private String facilityTitle;

    @Expose
    @SerializedName("FacilityType")
    private String facilityType;

    @Expose
    @SerializedName("Createdby")
    private int createdby;

    @Expose
    @SerializedName("IsDeleted")
    private boolean isDeleted;

    @Expose
    @SerializedName("PhotoUrl")
    private String photoUrl;

    @Expose
    @SerializedName("ReferredStatus")
    private String referredStatus;

    @Expose
    @SerializedName("PhotoID")
    private String photoId;

    public StaffProfileModel() {
    }

    public StaffProfileModel(StaffProfileModel profileModel) {

        if (profileModel == null) {
            return;
        }

        this.iD = profileModel.iD;
        this.code = profileModel.code;
        this.name = profileModel.name;
        this.jobTitle = profileModel.jobTitle;
        this.mobilePhone = profileModel.mobilePhone;
        this.homePhone = profileModel.homePhone;
        this.email = profileModel.email;
        this.address = profileModel.address;
        this.username = profileModel.username;
        this.password = profileModel.password;
        this.status = profileModel.status;
        this.accesstime = profileModel.accesstime;
        this.facilityCode = profileModel.facilityCode;
        this.facilityName = profileModel.facilityName;
        this.dOB = profileModel.dOB;
        this.divisionState = profileModel.divisionState;
        this.township = profileModel.township;
        this.roleCode = profileModel.roleCode;
        this.nationality = profileModel.nationality;
        this.religion = profileModel.religion;
        this.education = profileModel.education;
        this.codeIndex = profileModel.codeIndex;
        this.qualification = profileModel.qualification;
        this.exQualification = profileModel.exQualification;
        this.facilityTitle = profileModel.facilityTitle;
        this.facilityType = profileModel.facilityType;
        this.createdby = profileModel.createdby;
        this.isDeleted = profileModel.isDeleted;
        this.photoUrl = profileModel.photoUrl;
        this.referredStatus = profileModel.referredStatus;
        this.photoId = profileModel.photoId;
    }

    public int getiD() {
        return iD;
    }

    public void setiD(int iD) {
        this.iD = iD;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccesstime() {
        return accesstime;
    }

    public void setAccesstime(String accesstime) {
        this.accesstime = accesstime;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getdOB() {
        return dOB;
    }

    public void setdOB(String dOB) {
        this.dOB = dOB;
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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getExQualification() {
        return exQualification;
    }

    public void setExQualification(String exQualification) {
        this.exQualification = exQualification;
    }

    public String getFacilityTitle() {
        return facilityTitle;
    }

    public void setFacilityTitle(String facilityTitle) {
        this.facilityTitle = facilityTitle;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getReferredStatus() {
        return referredStatus;
    }

    public void setReferredStatus(String referredStatus) {
        this.referredStatus = referredStatus;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StaffProfileModel that = (StaffProfileModel) obj;

        if (iD != that.iD) return false;
        if (code != that.code) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (jobTitle != null ? !jobTitle.equals(that.jobTitle) : that.jobTitle != null)
            return false;
        if (mobilePhone != null ? !mobilePhone.equals(that.mobilePhone) : that.mobilePhone != null)
            return false;
        if (homePhone != null ? !homePhone.equals(that.homePhone) : that.homePhone != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (accesstime != null ? !accesstime.equals(that.accesstime) : that.accesstime != null)
            return false;
        if (facilityCode != null ? !facilityCode.equals(that.facilityCode) : that.facilityCode != null)
            return false;
        if (facilityName != null ? !facilityName.equals(that.facilityName) : that.facilityName != null)
            return false;
        if (dOB != null ? !dOB.equals(that.dOB) : that.dOB != null) return false;
        if (divisionState != null ? !divisionState.equals(that.divisionState) : that.divisionState != null)
            return false;
        if (township != null ? !township.equals(that.township) : that.township != null)
            return false;
        if (roleCode != null ? !roleCode.equals(that.roleCode) : that.roleCode != null)
            return false;
        if (nationality != null ? !nationality.equals(that.nationality) : that.nationality != null)
            return false;
        if (religion != null ? !religion.equals(that.religion) : that.religion != null)
            return false;
        if (education != null ? !education.equals(that.education) : that.education != null)
            return false;
        if (codeIndex != that.codeIndex) return false;
        if (qualification != null ? !qualification.equals(that.qualification) : that.qualification != null)
            return false;
        if (exQualification != null ? !exQualification.equals(that.exQualification) : that.exQualification != null)
            return false;
        if (facilityTitle != null ? !facilityTitle.equals(that.facilityTitle) : that.facilityTitle != null)
            return false;
        if (facilityType != null ? !facilityType.equals(that.facilityType) : that.facilityType != null)
            return false;
        if (createdby != that.createdby) return false;
        if (isDeleted != that.isDeleted) return false;
        if (photoUrl != null ? !photoUrl.equals(that.photoUrl) : that.photoUrl != null)
            return false;
        return photoId != null ? photoId.equals(that.photoId) : that.photoId == null;
    }

    @Override
    public int hashCode() {
        int result = iD;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (mobilePhone != null ? mobilePhone.hashCode() : 0);
        result = 31 * result + (homePhone != null ? homePhone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (accesstime != null ? accesstime.hashCode() : 0);
        result = 31 * result + (facilityCode != null ? facilityCode.hashCode() : 0);
        result = 31 * result + (facilityName != null ? facilityName.hashCode() : 0);
        result = 31 * result + (dOB != null ? dOB.hashCode() : 0);
        result = 31 * result + (divisionState != null ? divisionState.hashCode() : 0);
        result = 31 * result + (township != null ? township.hashCode() : 0);
        result = 31 * result + (roleCode != null ? roleCode.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (religion != null ? religion.hashCode() : 0);
        result = 31 * result + (education != null ? education.hashCode() : 0);
        result = 31 * result + codeIndex;
        result = 31 * result + (qualification != null ? qualification.hashCode() : 0);
        result = 31 * result + (exQualification != null ? exQualification.hashCode() : 0);
        result = 31 * result + (facilityTitle != null ? facilityTitle.hashCode() : 0);
        result = 31 * result + (facilityType != null ? facilityType.hashCode() : 0);
        result = 31 * result + createdby;
        result = 31 * result + (isDeleted ? 1 : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (referredStatus != null ? referredStatus.hashCode() : 0);
        result = 31 * result + (photoId != null ? photoId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StaffProfileModel{" +
                "iD=" + iD +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", accesstime='" + accesstime + '\'' +
                ", facilityCode='" + facilityCode + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", dOB='" + dOB + '\'' +
                ", divisionState='" + divisionState + '\'' +
                ", township='" + township + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", nationality='" + nationality + '\'' +
                ", religion='" + religion + '\'' +
                ", education='" + education + '\'' +
                ", codeIndex=" + codeIndex +
                ", qualification='" + qualification + '\'' +
                ", exQualification='" + exQualification + '\'' +
                ", facilityTitle='" + facilityTitle + '\'' +
                ", facilityType='" + facilityType + '\'' +
                ", createdby=" + createdby +
                ", isDeleted=" + isDeleted +
                ", photoUrl='" + photoUrl + '\'' +
                ", referredStatus='" + referredStatus + '\'' +
                ", photoId='" + photoId + '\'' +
                '}';
    }

    public StaffProfileModel clone() throws CloneNotSupportedException {
        return (StaffProfileModel) super.clone();
    }
}