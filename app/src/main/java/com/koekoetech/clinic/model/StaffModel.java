package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import io.realm.RealmObject;

/**
 * Created by Ko Pyae on 3/21/2017.
 */

public class StaffModel extends RealmObject implements Pageable,Parcelable{

    @Expose
    @SerializedName("ID")
    private int ID;

    @Expose
    @SerializedName("Code")
    private String Code;

    @Expose
    @SerializedName("Name")
    private String Name;

    @Expose
    @SerializedName("JobTitle")
    private String JobTitle;

    @Expose
    @SerializedName("MobilePhone")
    private String MobilePhone;

    @Expose
    @SerializedName("HomePhone")
    private String HomePhone;

    @Expose
    @SerializedName("Email")
    private String Email;

    @Expose
    @SerializedName("Address")
    private String Address;

    @Expose
    @SerializedName("Username")
    private String Username;

    @Expose
    @SerializedName("Password")
    private String Password;

    @Expose
    @SerializedName("Status")
    private String Status;

    @Expose
    @SerializedName("Accesstime")
    private String Accesstime;

    @Expose
    @SerializedName("FacilityName")
    private String FacilityName;

    @Expose
    @SerializedName("DOB")
    private String DOB;

    @Expose
    @SerializedName("DivisionState")
    private String DivisionState;

    @Expose
    @SerializedName("Township")
    private String Township;

    @Expose
    @SerializedName("RoleCode")
    private String RoleCode;

    @Expose
    @SerializedName("Nationality")
    private String Nationality;

    @Expose
    @SerializedName("Religion")
    private String Religion;

    @Expose
    @SerializedName("Education")
    private String Education;

    @Expose
    @SerializedName("FacilityCode")
    private String FacilityCode;

    @Expose
    @SerializedName("CodeIndex")
    private int CodeIndex;

    @Expose
    @SerializedName("Qualification")
    private String Qualification;

    @Expose
    @SerializedName("ExQualification")
    private String ExQualification;

    @Expose
    @SerializedName("FacilityTitle")
    private String FacilityTitle;

    @Expose
    @SerializedName("FacilityType")
    private String FacilityType;

    @Expose
    @SerializedName("Createdby")
    private String Createdby;


    @Expose
    @SerializedName("IsDeleted")
    private boolean IsDeleted;

    @Expose
    @SerializedName("PhotoUrl")
    private String PhotoUrl;

    @Expose
    @SerializedName("PhotoID")
    private String PhotoID;

    @Expose
    @SerializedName("ReferredStatus")
    private String ReferredStatus;


    public StaffModel() {
    }

    public StaffModel(StaffModel other) {
        this.ID = other.ID;
        this.Code = other.Code;
        this.Name = other.Name;
        this.JobTitle = other.JobTitle;
        this.MobilePhone = other.MobilePhone;
        this.HomePhone = other.HomePhone;
        this.Email = other.Email;
        this.Address = other.Address;
        this.Username = other.Username;
        this.Password = other.Password;
        this.Status = other.Status;
        this.Accesstime = other.Accesstime;
        this.FacilityName = other.FacilityName;
        this.DOB = other.DOB;
        this.DivisionState = other.DivisionState;
        this.Township = other.Township;
        this.RoleCode = other.RoleCode;
        this.Nationality = other.Nationality;
        this.Religion = other.Religion;
        this.Education = other.Education;
        this.FacilityCode = other.FacilityCode;
        this.CodeIndex = other.CodeIndex;
        this.Qualification = other.Qualification;
        this.ExQualification = other.ExQualification;
        this.FacilityTitle = other.FacilityTitle;
        this.FacilityType = other.FacilityType;
        this.Createdby = other.Createdby;
        this.IsDeleted = other.IsDeleted;
        this.PhotoUrl = other.PhotoUrl;
        this.PhotoID = other.PhotoID;
        this.ReferredStatus = other.ReferredStatus;
    }

    protected StaffModel(Parcel in) {
        ID = in.readInt();
        Code = in.readString();
        Name = in.readString();
        JobTitle = in.readString();
        MobilePhone = in.readString();
        HomePhone = in.readString();
        Email = in.readString();
        Address = in.readString();
        Username = in.readString();
        Password = in.readString();
        Status = in.readString();
        Accesstime = in.readString();
        FacilityName = in.readString();
        DOB = in.readString();
        DivisionState = in.readString();
        Township = in.readString();
        RoleCode = in.readString();
        Nationality = in.readString();
        Religion = in.readString();
        Education = in.readString();
        FacilityCode = in.readString();
        CodeIndex = in.readInt();
        Qualification = in.readString();
        ExQualification = in.readString();
        FacilityTitle = in.readString();
        FacilityType = in.readString();
        Createdby = in.readString();
        IsDeleted = in.readByte() != 0;
        PhotoUrl = in.readString();
        ReferredStatus = in.readString();
        PhotoID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Code);
        dest.writeString(Name);
        dest.writeString(JobTitle);
        dest.writeString(MobilePhone);
        dest.writeString(HomePhone);
        dest.writeString(Email);
        dest.writeString(Address);
        dest.writeString(Username);
        dest.writeString(Password);
        dest.writeString(Status);
        dest.writeString(Accesstime);
        dest.writeString(FacilityName);
        dest.writeString(DOB);
        dest.writeString(DivisionState);
        dest.writeString(Township);
        dest.writeString(RoleCode);
        dest.writeString(Nationality);
        dest.writeString(Religion);
        dest.writeString(Education);
        dest.writeString(FacilityCode);
        dest.writeInt(CodeIndex);
        dest.writeString(Qualification);
        dest.writeString(ExQualification);
        dest.writeString(FacilityTitle);
        dest.writeString(FacilityType);
        dest.writeString(Createdby);
        dest.writeByte((byte) (IsDeleted ? 1 : 0));
        dest.writeString(PhotoUrl);
        dest.writeString(ReferredStatus);
        dest.writeString(PhotoID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StaffModel> CREATOR = new Creator<StaffModel>() {
        @Override
        public StaffModel createFromParcel(Parcel in) {
            return new StaffModel(in);
        }

        @Override
        public StaffModel[] newArray(int size) {
            return new StaffModel[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getJobTitle() {
        return JobTitle;
    }

    public void setJobTitle(String jobTitle) {
        JobTitle = jobTitle;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        MobilePhone = mobilePhone;
    }

    public String getHomePhone() {
        return HomePhone;
    }

    public void setHomePhone(String homePhone) {
        HomePhone = homePhone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getAccesstime() {
        return Accesstime;
    }

    public void setAccesstime(String accesstime) {
        Accesstime = accesstime;
    }

    public String getFacilityName() {
        return FacilityName;
    }

    public void setFacilityName(String facilityName) {
        FacilityName = facilityName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getDivisionState() {
        return DivisionState;
    }

    public void setDivisionState(String divisionState) {
        DivisionState = divisionState;
    }

    public String getTownship() {
        return Township;
    }

    public void setTownship(String township) {
        Township = township;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }

    public String getReligion() {
        return Religion;
    }

    public void setReligion(String religion) {
        Religion = religion;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getFacilityCode() {
        return FacilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        FacilityCode = facilityCode;
    }

    public int getCodeIndex() {
        return CodeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        CodeIndex = codeIndex;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

    public String getExQualification() {
        return ExQualification;
    }

    public void setExQualification(String exQualification) {
        ExQualification = exQualification;
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

    public String getCreatedby() {
        return Createdby;
    }

    public void setCreatedby(String createdby) {
        Createdby = createdby;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public String getRoleCode() {
        return RoleCode;
    }

    public void setRoleCode(String roleCode) {
        RoleCode = roleCode;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getReferredStatus() {
        return ReferredStatus;
    }

    public void setReferredStatus(String referredStatus) {
        ReferredStatus = referredStatus;
    }

    public String getPhotoID() {
        return PhotoID;
    }

    public void setPhotoID(String photoID) {
        PhotoID = photoID;
    }
}
