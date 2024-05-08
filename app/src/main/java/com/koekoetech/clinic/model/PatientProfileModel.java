package com.koekoetech.clinic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import java.io.Serializable;

public class PatientProfileModel implements Pageable, Serializable,Cloneable {

    @SerializedName("Email")
    @Expose
    private String email;

    @SerializedName("Createdby")
    @Expose
    private int createdBy;

    @SerializedName("BloodGroup")
    @Expose
    private String bloodGroup;

    @SerializedName("Address")
    @Expose
    private String address;

    @SerializedName("Sex")
    @Expose
    private String sex;

    @SerializedName("CodeIndex")
    @Expose
    private int codeIndex;

    @SerializedName("IsPhotoTaken")
    @Expose
    private boolean isPhotoTaken;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("Remark")
    @Expose
    private String remark;

    @SerializedName("Education")
    @Expose
    private String education;

    @SerializedName("DOB")
    @Expose
    private String dateOfBirth;

    @SerializedName("Height")
    @Expose
    private double height;

    @SerializedName("ID")
    @Expose
    private int id;

    @SerializedName("HospitalName")
    @Expose
    private String hospitalName;

    @SerializedName("Age")
    @Expose
    private int age;

    @SerializedName("Accesstime")
    @Expose
    private String accessTime;

    @SerializedName("Status")
    @Expose
    private String status;

    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("Township")
    @Expose
    private String township;

    @SerializedName("AgeGroup")
    @Expose
    private String ageGroup;

    @SerializedName("Religion")
    @Expose
    private String religion;

    @SerializedName("StateCode")
    @Expose
    private String stateCode;

    @SerializedName("NRCID")
    @Expose
    private String nrcId;

    @SerializedName("Code")
    @Expose
    private String code;

    @SerializedName("Nationality")
    @Expose
    private String nationality;

    @SerializedName("Contact")
    @Expose
    private String contact;

    @SerializedName("Occupation")
    @Expose
    private String occupation;

    @SerializedName("MaritalStatus")
    @Expose
    private String maritalStatus;

    @SerializedName("Ethnicity")
    @Expose
    private String ethnicity;

    @SerializedName("HospitalCode")
    @Expose
    private String hospitalCode;

    @SerializedName("State")
    @Expose
    private String state;

    @SerializedName("FatherName")
    @Expose
    private String fatherName;

    @SerializedName("PhotoUrl")
    @Expose
    private String photoUrl;

    @SerializedName("PhotoID")
    @Expose
    private String photoID;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    public boolean isPhotoTaken() {
        return isPhotoTaken;
    }

    public void setPhotoTaken(boolean photoTaken) {
        isPhotoTaken = photoTaken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getNrcId() {
        return nrcId;
    }

    public void setNrcId(String nrcId) {
        this.nrcId = nrcId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientProfileModel that = (PatientProfileModel) o;

        if (createdBy != that.createdBy) return false;
        if (codeIndex != that.codeIndex) return false;
        if (isPhotoTaken != that.isPhotoTaken) return false;
        if (Double.compare(that.height, height) != 0) return false;
        if (id != that.id) return false;
        if (age != that.age) return false;
        if (isDeleted != that.isDeleted) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (bloodGroup != null ? !bloodGroup.equals(that.bloodGroup) : that.bloodGroup != null)
            return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (education != null ? !education.equals(that.education) : that.education != null)
            return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(that.dateOfBirth) : that.dateOfBirth != null)
            return false;
        if (hospitalName != null ? !hospitalName.equals(that.hospitalName) : that.hospitalName != null)
            return false;
        if (accessTime != null ? !accessTime.equals(that.accessTime) : that.accessTime != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (township != null ? !township.equals(that.township) : that.township != null)
            return false;
        if (ageGroup != null ? !ageGroup.equals(that.ageGroup) : that.ageGroup != null)
            return false;
        if (religion != null ? !religion.equals(that.religion) : that.religion != null)
            return false;
        if (stateCode != null ? !stateCode.equals(that.stateCode) : that.stateCode != null)
            return false;
        if (nrcId != null ? !nrcId.equals(that.nrcId) : that.nrcId != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (nationality != null ? !nationality.equals(that.nationality) : that.nationality != null)
            return false;
        if (contact != null ? !contact.equals(that.contact) : that.contact != null) return false;
        if (occupation != null ? !occupation.equals(that.occupation) : that.occupation != null)
            return false;
        if (maritalStatus != null ? !maritalStatus.equals(that.maritalStatus) : that.maritalStatus != null)
            return false;
        if (ethnicity != null ? !ethnicity.equals(that.ethnicity) : that.ethnicity != null)
            return false;
        if (hospitalCode != null ? !hospitalCode.equals(that.hospitalCode) : that.hospitalCode != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (fatherName != null ? !fatherName.equals(that.fatherName) : that.fatherName != null)
            return false;
        if (photoUrl != null ? !photoUrl.equals(that.photoUrl) : that.photoUrl != null)
            return false;
        return photoID != null ? photoID.equals(that.photoID) : that.photoID == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = email != null ? email.hashCode() : 0;
        result = 31 * result + createdBy;
        result = 31 * result + (bloodGroup != null ? bloodGroup.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + codeIndex;
        result = 31 * result + (isPhotoTaken ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (education != null ? education.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + id;
        result = 31 * result + (hospitalName != null ? hospitalName.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (accessTime != null ? accessTime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isDeleted ? 1 : 0);
        result = 31 * result + (township != null ? township.hashCode() : 0);
        result = 31 * result + (ageGroup != null ? ageGroup.hashCode() : 0);
        result = 31 * result + (religion != null ? religion.hashCode() : 0);
        result = 31 * result + (stateCode != null ? stateCode.hashCode() : 0);
        result = 31 * result + (nrcId != null ? nrcId.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (nationality != null ? nationality.hashCode() : 0);
        result = 31 * result + (contact != null ? contact.hashCode() : 0);
        result = 31 * result + (occupation != null ? occupation.hashCode() : 0);
        result = 31 * result + (maritalStatus != null ? maritalStatus.hashCode() : 0);
        result = 31 * result + (ethnicity != null ? ethnicity.hashCode() : 0);
        result = 31 * result + (hospitalCode != null ? hospitalCode.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (fatherName != null ? fatherName.hashCode() : 0);
        result = 31 * result + (photoUrl != null ? photoUrl.hashCode() : 0);
        result = 31 * result + (photoID != null ? photoID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PatientProfileModel{" +
                "email='" + email + '\'' +
                ", createdBy=" + createdBy +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", address='" + address + '\'' +
                ", sex='" + sex + '\'' +
                ", codeIndex=" + codeIndex +
                ", isPhotoTaken=" + isPhotoTaken +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", education='" + education + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", height=" + height +
                ", id=" + id +
                ", hospitalName='" + hospitalName + '\'' +
                ", age=" + age +
                ", accessTime='" + accessTime + '\'' +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", township='" + township + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", religion='" + religion + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", nrcId='" + nrcId + '\'' +
                ", code='" + code + '\'' +
                ", nationality='" + nationality + '\'' +
                ", contact='" + contact + '\'' +
                ", occupation='" + occupation + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", hospitalCode='" + hospitalCode + '\'' +
                ", state='" + state + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", photoID='" + photoID + '\'' +
                '}';
    }

    public PatientProfileModel clone() throws CloneNotSupportedException {
        return (PatientProfileModel) super.clone();
    }
}