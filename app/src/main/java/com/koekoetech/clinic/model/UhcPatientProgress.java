package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koekoetech.clinic.helper.Pageable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UhcPatientProgress extends RealmObject implements Parcelable, Pageable {

    @PrimaryKey
    private String localId;
    @SerializedName("Id")
    @Expose
    private int id;
    @SerializedName("ProgressCode")
    @Expose
    private String progressCode;
    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("Accesstime")
    @Expose
    private String accesstime;
    @SerializedName("PatientCode")
    @Expose
    private String patientCode;
    @SerializedName("CreatedTime")
    @Expose
    private String createdTime;
    @SerializedName("OutDiagnosis")
    @Expose
    private String outDiagnosis;
    @SerializedName("Charges")
    @Expose
    private Double charges;
    @SerializedName("Followup_Reason")
    @Expose
    private String followUpReason;
    @SerializedName("Medication")
    @Expose
    private String medication;
    @SerializedName("PlanOfCare")
    @Expose
    private String planOfCare;
    @SerializedName("ReferToDoctor")
    @Expose
    private int referToDoctor;
    @SerializedName("ReferToDoctorName")
    @Expose
    private String referToDoctorName;
    @SerializedName("ReferToFacility")
    @Expose
    private String referToFacility;
    @SerializedName("ReferToReason")
    @Expose
    private String referToReason;
    @SerializedName("ReferToDateTime")
    @Expose
    private String referToDateTime;
    @SerializedName("Followup_DateTime")
    @Expose
    private String followUpDateTime;
    @SerializedName("Createdby")
    @Expose
    private int createdBy;
    @SerializedName("ReferType")
    @Expose
    private int referType;
    @SerializedName("VisitTime")
    @Expose
    private String visitDate;
    private boolean isOnline;
    private boolean hasChanges;

    public UhcPatientProgress() {
    }

    public UhcPatientProgress(UhcPatientProgress uhcPatientProgress) {
        if (uhcPatientProgress == null) {
            return;
        }

        this.localId = uhcPatientProgress.localId;
        this.id = uhcPatientProgress.id;
        this.progressCode = uhcPatientProgress.progressCode;
        this.isDeleted = uhcPatientProgress.isDeleted;
        this.accesstime = uhcPatientProgress.accesstime;
        this.patientCode = uhcPatientProgress.patientCode;
        this.createdTime = uhcPatientProgress.createdTime;
        this.outDiagnosis = uhcPatientProgress.outDiagnosis;
        this.charges = uhcPatientProgress.charges;
        this.followUpReason = uhcPatientProgress.followUpReason;
        this.medication = uhcPatientProgress.medication;
        this.planOfCare = uhcPatientProgress.planOfCare;
        this.referToDoctor = uhcPatientProgress.referToDoctor;
        this.referToDoctorName = uhcPatientProgress.referToDoctorName;
        this.referToFacility = uhcPatientProgress.referToFacility;
        this.referToReason = uhcPatientProgress.referToReason;
        this.referToDateTime = uhcPatientProgress.referToDateTime;
        this.followUpDateTime = uhcPatientProgress.followUpDateTime;
        this.createdBy = uhcPatientProgress.createdBy;
        this.isOnline = uhcPatientProgress.isOnline;
        this.referType = uhcPatientProgress.referType;
        this.visitDate = uhcPatientProgress.visitDate;
        this.hasChanges = uhcPatientProgress.hasChanges;
    }

    protected UhcPatientProgress(Parcel in) {
        localId = in.readString();
        id = in.readInt();
        progressCode = in.readString();
        isDeleted = in.readByte() != 0;
        accesstime = in.readString();
        patientCode = in.readString();
        createdTime = in.readString();
        outDiagnosis = in.readString();
        if (in.readByte() == 0) {
            charges = null;
        } else {
            charges = in.readDouble();
        }
        followUpReason = in.readString();
        medication = in.readString();
        planOfCare = in.readString();
        referToDoctor = in.readInt();
        referToDoctorName = in.readString();
        referToFacility = in.readString();
        referToReason = in.readString();
        referToDateTime = in.readString();
        followUpDateTime = in.readString();
        createdBy = in.readInt();
        referType = in.readInt();
        visitDate = in.readString();
        isOnline = in.readByte() != 0;
        hasChanges = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localId);
        dest.writeInt(id);
        dest.writeString(progressCode);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(accesstime);
        dest.writeString(patientCode);
        dest.writeString(createdTime);
        dest.writeString(outDiagnosis);
        if (charges == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(charges);
        }
        dest.writeString(followUpReason);
        dest.writeString(medication);
        dest.writeString(planOfCare);
        dest.writeInt(referToDoctor);
        dest.writeString(referToDoctorName);
        dest.writeString(referToFacility);
        dest.writeString(referToReason);
        dest.writeString(referToDateTime);
        dest.writeString(followUpDateTime);
        dest.writeInt(createdBy);
        dest.writeInt(referType);
        dest.writeString(visitDate);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeByte((byte) (hasChanges ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UhcPatientProgress> CREATOR = new Creator<UhcPatientProgress>() {
        @Override
        public UhcPatientProgress createFromParcel(Parcel in) {
            return new UhcPatientProgress(in);
        }

        @Override
        public UhcPatientProgress[] newArray(int size) {
            return new UhcPatientProgress[size];
        }
    };

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccesstime() {
        return accesstime;
    }

    public void setAccesstime(String accesstime) {
        this.accesstime = accesstime;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getOutDiagnosis() {
        return outDiagnosis;
    }

    public void setOutDiagnosis(String outDiagnosis) {
        this.outDiagnosis = outDiagnosis;
    }

    public Double getCharges() {
        return charges;
    }

    public void setCharges(Double charges) {
        this.charges = charges;
    }

    public String getFollowUpReason() {
        return followUpReason;
    }

    public void setFollowUpReason(String followUpReason) {
        this.followUpReason = followUpReason;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getPlanOfCare() {
        return planOfCare;
    }

    public void setPlanOfCare(String planOfCare) {
        this.planOfCare = planOfCare;
    }

    public int getReferToDoctor() {
        return referToDoctor;
    }

    public void setReferToDoctor(int referToDoctor) {
        this.referToDoctor = referToDoctor;
    }

    public String getReferToDoctorName() {
        return referToDoctorName;
    }

    public void setReferToDoctorName(String referToDoctorName) {
        this.referToDoctorName = referToDoctorName;
    }

    public String getReferToFacility() {
        return referToFacility;
    }

    public void setReferToFacility(String referToFacility) {
        this.referToFacility = referToFacility;
    }

    public String getReferToReason() {
        return referToReason;
    }

    public void setReferToReason(String referToReason) {
        this.referToReason = referToReason;
    }

    public String getReferToDateTime() {
        return referToDateTime;
    }

    public void setReferToDateTime(String referToDateTime) {
        this.referToDateTime = referToDateTime;
    }

    public String getFollowUpDateTime() {
        return followUpDateTime;
    }

    public void setFollowUpDateTime(String followUpDateTime) {
        this.followUpDateTime = followUpDateTime;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getReferType() {
        return referType;
    }

    public void setReferType(int referType) {
        this.referType = referType;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public boolean isHasChanges() {
        return hasChanges;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    @Override
    @NonNull
    public String toString() {
        return "UhcPatientProgress{" +
                "localId='" + localId + '\'' +
                ", id=" + id +
                ", progressCode='" + progressCode + '\'' +
                ", isDeleted=" + isDeleted +
                ", accesstime='" + accesstime + '\'' +
                ", patientCode='" + patientCode + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", outDiagnosis='" + outDiagnosis + '\'' +
                ", charges=" + charges +
                ", followUpReason='" + followUpReason + '\'' +
                ", medication='" + medication + '\'' +
                ", planOfCare='" + planOfCare + '\'' +
                ", referToDoctor=" + referToDoctor +
                ", referToDoctorName='" + referToDoctorName + '\'' +
                ", referToFacility='" + referToFacility + '\'' +
                ", referToReason='" + referToReason + '\'' +
                ", referToDateTime='" + referToDateTime + '\'' +
                ", followUpDateTime='" + followUpDateTime + '\'' +
                ", createdBy=" + createdBy +
                ", referType=" + referType +
                ", visitDate='" + visitDate + '\'' +
                ", isOnline=" + isOnline +
                ", hasChanges=" + hasChanges +
                '}';
    }
}