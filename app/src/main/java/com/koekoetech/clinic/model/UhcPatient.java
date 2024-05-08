package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UhcPatient extends RealmObject implements Parcelable, Pageable {

    @PrimaryKey
    private String localId;

    @SerializedName("Email")
    @Expose
    private String email;

    @SerializedName("Createdby")
    @Expose
    private int createdBy;

    @SerializedName("CreatedTime")
    @Expose
    private String createdTime;

    @SerializedName("Address")
    @Expose
    private String address;

    @SerializedName("NameInMyanmar")
    @Expose
    private String nameInMyanmar;

    @SerializedName("Gender")
    @Expose
    private String gender;

    @SerializedName("FacilityCode")
    @Expose
    private String facilityCode;

    @SerializedName("FacilityName")
    @Expose
    private String facilityName;

    @SerializedName("Remark")
    @Expose
    private String remark;

    @SerializedName("HH_Code")
    @Expose
    private String hhCode;

    @SerializedName("HeightIn_in")
    @Expose
    private double heightIn_in;

    @SerializedName("ID")
    @Expose
    private int id;

    @SerializedName("ReferForTB_Screening")
    @Expose
    private boolean referForTBScreening;

    @SerializedName("Accesstime")
    @Expose
    private String accesstime;

    @SerializedName("Status")
    @Expose
    private String status;

    @SerializedName("RBS")
    @Expose
    private Double rbs;

    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("MAC")
    @Expose
    private String mac;

    @SerializedName("MaritalStatus")
    @Expose
    private String maritalStatus;

    @SerializedName("TempInF")
    @Expose
    private double tempInF;

    @SerializedName("RiskFactor")
    @Expose
    private String riskGroup;

    @SerializedName("Problem")
    @Expose
    private String problem;

    @SerializedName("ReferForContraception")
    @Expose
    private boolean referForContraception;

    @SerializedName("StateDivision")
    @Expose
    private String stateDivision;

    @SerializedName("CurrentContraceptionStatus")
    @Expose
    private String currentContraceptionStatus;

    @SerializedName("BodyWeight")
    @Expose
    private double bodyWeight;

    @SerializedName("RegistrationDate")
    @Expose
    private String registrationDate;

    @SerializedName("PatientCode")
    @Expose
    private String patientCode;

    @SerializedName("CodeIndex")
    @Expose
    private int codeIndex;

    @SerializedName("SystolicBP")
    @Expose
    private String systolicBP;

    @SerializedName("DiastolicBP")
    @Expose
    private String diastolicBP;

    @SerializedName("Phone")
    @Expose
    private String phone;

    @SerializedName("NameInEnglish")
    @Expose
    private String nameInEnglish;

    @SerializedName("Age")
    @Expose
    private int age;

    @SerializedName("AgeType")
    @Expose
    private String ageType;

    @SerializedName("BMI")
    @Expose
    private double bmi;

    @SerializedName("Township")
    @Expose
    private String township;

    @SerializedName("StateCode")
    @Expose
    private String stateCode;
    private boolean hasSynced;

    @SerializedName("HH_MemberCode")
    @Expose
    private String hHMemberCode;

    @SerializedName("PregnancyStatus")
    @Expose
    private boolean pregnancy;

    @SerializedName("ImmunizationStatus")
    @Expose
    private String immunizationStatus;

    @SerializedName("Immunizations")
    @Expose
    private String immunizations;

    @SerializedName("UIC_Code")
    @Expose
    private String uicCode;

    @SerializedName("HE")
    @Expose
    private boolean he;

    @SerializedName("FacilityType")
    @Expose
    private String facilityType;

    @SerializedName("DateOfBirth")
    @Expose
    private String dateOfBirth;

    @SerializedName("Occupation")
    @Expose
    private String occupation;

    @SerializedName("MUAC")
    @Expose
    private String MUAC;

    @SerializedName("IsUHC")
    @Expose
    private boolean isUHC;

    @SerializedName("PreviousMedicalHistory")
    @Expose
    private String previousMedicalHistory;

    @SerializedName("PreviousSurgicalHistory")
    @Expose
    private String previousSurgicalHistory;

    @SerializedName("KnownAllergies")
    @Expose
    private String knownAllergies;

    @SerializedName("RelevantFamilyHistory")
    @Expose
    private String relevantFamilyHistory;

    @SerializedName("PhotoUrl")
    @Expose
    private String photoUrl;

    @SerializedName("ContactPerson")
    @Expose
    private String contactPerson;

    private boolean isOnline;
    private String local_filepath;

    public UhcPatient() {
    }

    public UhcPatient(UhcPatient uhcPatient) {
        if (uhcPatient == null) {
            return;
        }
        this.localId = uhcPatient.localId;
        this.email = uhcPatient.email;
        this.createdBy = uhcPatient.createdBy;
        this.createdTime = uhcPatient.createdTime;
        this.address = uhcPatient.address;
        this.nameInMyanmar = uhcPatient.nameInMyanmar;
        this.gender = uhcPatient.gender;
        this.facilityCode = uhcPatient.facilityCode;
        this.facilityName = uhcPatient.facilityName;
        this.remark = uhcPatient.remark;
        this.hhCode = uhcPatient.hhCode;
        this.heightIn_in = uhcPatient.heightIn_in;
        this.id = uhcPatient.id;
        this.referForTBScreening = uhcPatient.referForTBScreening;
        this.accesstime = uhcPatient.accesstime;
        this.status = uhcPatient.status;
        this.rbs = uhcPatient.rbs;
        this.isDeleted = uhcPatient.isDeleted;
        this.mac = uhcPatient.mac;
        this.maritalStatus = uhcPatient.maritalStatus;
        this.tempInF = uhcPatient.tempInF;
        this.riskGroup = uhcPatient.riskGroup;
        this.problem = uhcPatient.problem;
        this.referForContraception = uhcPatient.referForContraception;
        this.stateDivision = uhcPatient.stateDivision;
        this.currentContraceptionStatus = uhcPatient.currentContraceptionStatus;
        this.bodyWeight = uhcPatient.bodyWeight;
        this.registrationDate = uhcPatient.registrationDate;
        this.patientCode = uhcPatient.patientCode;
        this.codeIndex = uhcPatient.codeIndex;
        this.systolicBP = uhcPatient.systolicBP;
        this.diastolicBP = uhcPatient.diastolicBP;
        this.phone = uhcPatient.phone;
        this.nameInEnglish = uhcPatient.nameInEnglish;
        this.age = uhcPatient.age;
        this.ageType = uhcPatient.ageType;
        this.bmi = uhcPatient.bmi;
        this.township = uhcPatient.township;
        this.stateCode = uhcPatient.stateCode;
        this.hasSynced = uhcPatient.hasSynced;
        this.hHMemberCode = uhcPatient.hHMemberCode;
        this.pregnancy = uhcPatient.pregnancy;
        this.immunizationStatus = uhcPatient.immunizationStatus;
        this.immunizations = uhcPatient.immunizations;
        this.uicCode = uhcPatient.uicCode;
        this.he = uhcPatient.he;
        this.facilityType = uhcPatient.facilityType;
        this.dateOfBirth = uhcPatient.dateOfBirth;
        this.occupation = uhcPatient.occupation;
        this.MUAC = uhcPatient.MUAC;
        this.isUHC = uhcPatient.isUHC;
        this.previousMedicalHistory = uhcPatient.previousMedicalHistory;
        this.previousSurgicalHistory = uhcPatient.previousSurgicalHistory;
        this.knownAllergies = uhcPatient.knownAllergies;
        this.relevantFamilyHistory = uhcPatient.relevantFamilyHistory;
        this.photoUrl = uhcPatient.photoUrl;
        this.isOnline = uhcPatient.isOnline;
        this.local_filepath = uhcPatient.local_filepath;
        this.contactPerson = uhcPatient.contactPerson;
    }

    protected UhcPatient(Parcel in) {
        localId = in.readString();
        email = in.readString();
        createdBy = in.readInt();
        createdTime = in.readString();
        address = in.readString();
        nameInMyanmar = in.readString();
        gender = in.readString();
        facilityCode = in.readString();
        facilityName = in.readString();
        remark = in.readString();
        hhCode = in.readString();
        heightIn_in = in.readDouble();
        id = in.readInt();
        referForTBScreening = in.readByte() != 0;
        accesstime = in.readString();
        status = in.readString();
        if (in.readByte() == 0) {
            rbs = null;
        } else {
            rbs = in.readDouble();
        }
        isDeleted = in.readByte() != 0;
        mac = in.readString();
        maritalStatus = in.readString();
        tempInF = in.readDouble();
        riskGroup = in.readString();
        problem = in.readString();
        referForContraception = in.readByte() != 0;
        stateDivision = in.readString();
        currentContraceptionStatus = in.readString();
        bodyWeight = in.readDouble();
        registrationDate = in.readString();
        patientCode = in.readString();
        codeIndex = in.readInt();
        systolicBP = in.readString();
        diastolicBP = in.readString();
        phone = in.readString();
        nameInEnglish = in.readString();
        age = in.readInt();
        ageType = in.readString();
        bmi = in.readDouble();
        township = in.readString();
        stateCode = in.readString();
        hasSynced = in.readByte() != 0;
        hHMemberCode = in.readString();
        pregnancy = in.readByte() != 0;
        immunizationStatus = in.readString();
        immunizations = in.readString();
        uicCode = in.readString();
        he = in.readByte() != 0;
        facilityType = in.readString();
        dateOfBirth = in.readString();
        occupation = in.readString();
        MUAC = in.readString();
        isUHC = in.readByte() != 0;
        previousMedicalHistory = in.readString();
        previousSurgicalHistory = in.readString();
        knownAllergies = in.readString();
        relevantFamilyHistory = in.readString();
        photoUrl = in.readString();
        contactPerson = in.readString();
        isOnline = in.readByte() != 0;
        local_filepath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localId);
        dest.writeString(email);
        dest.writeInt(createdBy);
        dest.writeString(createdTime);
        dest.writeString(address);
        dest.writeString(nameInMyanmar);
        dest.writeString(gender);
        dest.writeString(facilityCode);
        dest.writeString(facilityName);
        dest.writeString(remark);
        dest.writeString(hhCode);
        dest.writeDouble(heightIn_in);
        dest.writeInt(id);
        dest.writeByte((byte) (referForTBScreening ? 1 : 0));
        dest.writeString(accesstime);
        dest.writeString(status);
        if (rbs == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rbs);
        }
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(mac);
        dest.writeString(maritalStatus);
        dest.writeDouble(tempInF);
        dest.writeString(riskGroup);
        dest.writeString(problem);
        dest.writeByte((byte) (referForContraception ? 1 : 0));
        dest.writeString(stateDivision);
        dest.writeString(currentContraceptionStatus);
        dest.writeDouble(bodyWeight);
        dest.writeString(registrationDate);
        dest.writeString(patientCode);
        dest.writeInt(codeIndex);
        dest.writeString(systolicBP);
        dest.writeString(diastolicBP);
        dest.writeString(phone);
        dest.writeString(nameInEnglish);
        dest.writeInt(age);
        dest.writeString(ageType);
        dest.writeDouble(bmi);
        dest.writeString(township);
        dest.writeString(stateCode);
        dest.writeByte((byte) (hasSynced ? 1 : 0));
        dest.writeString(hHMemberCode);
        dest.writeByte((byte) (pregnancy ? 1 : 0));
        dest.writeString(immunizationStatus);
        dest.writeString(immunizations);
        dest.writeString(uicCode);
        dest.writeByte((byte) (he ? 1 : 0));
        dest.writeString(facilityType);
        dest.writeString(dateOfBirth);
        dest.writeString(occupation);
        dest.writeString(MUAC);
        dest.writeByte((byte) (isUHC ? 1 : 0));
        dest.writeString(previousMedicalHistory);
        dest.writeString(previousSurgicalHistory);
        dest.writeString(knownAllergies);
        dest.writeString(relevantFamilyHistory);
        dest.writeString(photoUrl);
        dest.writeString(contactPerson);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeString(local_filepath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UhcPatient> CREATOR = new Creator<UhcPatient>() {
        @Override
        public UhcPatient createFromParcel(Parcel in) {
            return new UhcPatient(in);
        }

        @Override
        public UhcPatient[] newArray(int size) {
            return new UhcPatient[size];
        }
    };

    public static Creator<UhcPatient> getCREATOR() {
        return CREATOR;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

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

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNameInMyanmar() {
        return nameInMyanmar;
    }

    public void setNameInMyanmar(String nameInMyanmar) {
        this.nameInMyanmar = nameInMyanmar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHhCode() {
        return hhCode;
    }

    public void setHhCode(String hhCode) {
        this.hhCode = hhCode;
    }

    public double getHeightIn_in() {
        return heightIn_in;
    }

    public void setHeightIn_in(double heightIn_in) {
        this.heightIn_in = heightIn_in;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReferForTBScreening() {
        return referForTBScreening;
    }

    public void setReferForTBScreening(boolean referForTBScreening) {
        this.referForTBScreening = referForTBScreening;
    }

    public String getAccesstime() {
        return accesstime;
    }

    public void setAccesstime(String accesstime) {
        this.accesstime = accesstime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getRbs() {
        return rbs;
    }

    public void setRbs(Double rbs) {
        this.rbs = rbs;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public double getTempInF() {
        return tempInF;
    }

    public void setTempInF(double tempInF) {
        this.tempInF = tempInF;
    }

    public String getRiskGroup() {
        return riskGroup;
    }

    public void setRiskGroup(String riskGroup) {
        this.riskGroup = riskGroup;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public boolean isReferForContraception() {
        return referForContraception;
    }

    public void setReferForContraception(boolean referForContraception) {
        this.referForContraception = referForContraception;
    }

    public String getStateDivision() {
        return stateDivision;
    }

    public void setStateDivision(String stateDivision) {
        this.stateDivision = stateDivision;
    }

    public String getCurrentContraceptionStatus() {
        return currentContraceptionStatus;
    }

    public void setCurrentContraceptionStatus(String currentContraceptionStatus) {
        this.currentContraceptionStatus = currentContraceptionStatus;
    }

    public double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public void setCodeIndex(int codeIndex) {
        this.codeIndex = codeIndex;
    }

    public String getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(String systolicBP) {
        this.systolicBP = systolicBP;
    }

    public String getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(String diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNameInEnglish() {
        return nameInEnglish;
    }

    public void setNameInEnglish(String nameInEnglish) {
        this.nameInEnglish = nameInEnglish;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAgeType() {
        return ageType;
    }

    public void setAgeType(String ageType) {
        this.ageType = ageType;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public boolean isHasSynced() {
        return hasSynced;
    }

    public void setHasSynced(boolean hasSynced) {
        this.hasSynced = hasSynced;
    }

    public String gethHMemberCode() {
        return hHMemberCode;
    }

    public void sethHMemberCode(String hHMemberCode) {
        this.hHMemberCode = hHMemberCode;
    }

    public boolean isPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(boolean pregnancy) {
        this.pregnancy = pregnancy;
    }

    public String getImmunizationStatus() {
        return immunizationStatus;
    }

    public void setImmunizationStatus(String immunizationStatus) {
        this.immunizationStatus = immunizationStatus;
    }

    public String getImmunizations() {
        return immunizations;
    }

    public void setImmunizations(String immunizations) {
        this.immunizations = immunizations;
    }

    public String getUicCode() {
        return uicCode;
    }

    public void setUicCode(String uicCode) {
        this.uicCode = uicCode;
    }

    public boolean isHe() {
        return he;
    }

    public void setHe(boolean he) {
        this.he = he;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMUAC() {
        return MUAC;
    }

    public void setMUAC(String MUAC) {
        this.MUAC = MUAC;
    }

    public boolean isUHC() {
        return isUHC;
    }

    public void setUHC(boolean UHC) {
        isUHC = UHC;
    }

    public String getPreviousMedicalHistory() {
        return previousMedicalHistory;
    }

    public void setPreviousMedicalHistory(String previousMedicalHistory) {
        this.previousMedicalHistory = previousMedicalHistory;
    }

    public String getPreviousSurgicalHistory() {
        return previousSurgicalHistory;
    }

    public void setPreviousSurgicalHistory(String previousSurgicalHistory) {
        this.previousSurgicalHistory = previousSurgicalHistory;
    }

    public String getKnownAllergies() {
        return knownAllergies;
    }

    public void setKnownAllergies(String knownAllergies) {
        this.knownAllergies = knownAllergies;
    }

    public String getRelevantFamilyHistory() {
        return relevantFamilyHistory;
    }

    public void setRelevantFamilyHistory(String relevantFamilyHistory) {
        this.relevantFamilyHistory = relevantFamilyHistory;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLocal_filepath() {
        return local_filepath;
    }

    public void setLocal_filepath(String local_filepath) {
        this.local_filepath = local_filepath;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return "UhcPatient{" +
                "localId='" + localId + '\'' +
                ", email='" + email + '\'' +
                ", createdBy=" + createdBy +
                ", createdTime='" + createdTime + '\'' +
                ", address='" + address + '\'' +
                ", nameInMyanmar='" + nameInMyanmar + '\'' +
                ", gender='" + gender + '\'' +
                ", facilityCode='" + facilityCode + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", remark='" + remark + '\'' +
                ", hhCode='" + hhCode + '\'' +
                ", heightIn_in=" + heightIn_in +
                ", id=" + id +
                ", referForTBScreening=" + referForTBScreening +
                ", accesstime='" + accesstime + '\'' +
                ", status='" + status + '\'' +
                ", rbs=" + rbs +
                ", isDeleted=" + isDeleted +
                ", mac='" + mac + '\'' +
                ", maritalStatus='" + maritalStatus + '\'' +
                ", tempInF=" + tempInF +
                ", riskGroup='" + riskGroup + '\'' +
                ", problem='" + problem + '\'' +
                ", referForContraception=" + referForContraception +
                ", stateDivision='" + stateDivision + '\'' +
                ", currentContraceptionStatus='" + currentContraceptionStatus + '\'' +
                ", bodyWeight=" + bodyWeight +
                ", registrationDate='" + registrationDate + '\'' +
                ", patientCode='" + patientCode + '\'' +
                ", codeIndex=" + codeIndex +
                ", systolicBP='" + systolicBP + '\'' +
                ", diastolicBP='" + diastolicBP + '\'' +
                ", phone='" + phone + '\'' +
                ", nameInEnglish='" + nameInEnglish + '\'' +
                ", age=" + age +
                ", ageType='" + ageType + '\'' +
                ", bmi=" + bmi +
                ", township='" + township + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", hasSynced=" + hasSynced +
                ", hHMemberCode='" + hHMemberCode + '\'' +
                ", pregnancy=" + pregnancy +
                ", immunizationStatus='" + immunizationStatus + '\'' +
                ", immunizations='" + immunizations + '\'' +
                ", uicCode='" + uicCode + '\'' +
                ", he=" + he +
                ", facilityType='" + facilityType + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", occupation='" + occupation + '\'' +
                ", MUAC='" + MUAC + '\'' +
                ", isUHC=" + isUHC +
                ", previousMedicalHistory='" + previousMedicalHistory + '\'' +
                ", previousSurgicalHistory='" + previousSurgicalHistory + '\'' +
                ", knownAllergies='" + knownAllergies + '\'' +
                ", relevantFamilyHistory='" + relevantFamilyHistory + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", isOnline=" + isOnline +
                ", local_filepath='" + local_filepath + '\'' +
                '}';
    }
}