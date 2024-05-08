package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UhcPatientProgressNote extends RealmObject implements Parcelable {

    @PrimaryKey
    private String localId;

    @SerializedName("DoctorName")
    @Expose
    private String doctorName;

    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("ProgressNoteCode")
    @Expose
    private String progressNoteCode;

    @SerializedName("CreatedTime")
    @Expose
    private String createdTime;

    @SerializedName("PatientCode")
    @Expose
    private String patientCode;

    @SerializedName("DoctorCode")
    @Expose
    private String doctorCode;

    @SerializedName("PatientName")
    @Expose
    private String patientName;

    @SerializedName("FacilityCode")
    @Expose
    private String facilityCode;

    @SerializedName("Specialty")
    @Expose
    private String specialty;

    @SerializedName("FacilityId")
    @Expose
    private int facilityId;

    @SerializedName("Type")
    @Expose
    private String type;

    @SerializedName("ProgressCode")
    @Expose
    private String progressCode;

    @SerializedName("Note")
    @Expose
    private String note;

    @SerializedName("FreeNote")
    @Expose
    private String freeNote;

    @SerializedName("DoctorPhoto")
    @Expose
    private String doctorPhoto;

    @SerializedName("DoctorPhotoUrl")
    @Expose
    private String doctorPhotoUrl;

    @SerializedName("FacilityTitle")
    @Expose
    private String facilityTitle;

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("FacilityType")
    @Expose
    private String facilityType;

    @SerializedName("Accesstime")
    @Expose
    private String accesstime;
    private boolean isOnline;
    private boolean hasChanges;

    public UhcPatientProgressNote() {
    }

    public UhcPatientProgressNote(UhcPatientProgressNote uhcPatientProgressNote) {

        if (uhcPatientProgressNote == null) {
            return;
        }

        this.localId = uhcPatientProgressNote.localId;
        this.doctorName = uhcPatientProgressNote.doctorName;
        this.isDeleted = uhcPatientProgressNote.isDeleted;
        this.progressNoteCode = uhcPatientProgressNote.progressNoteCode;
        this.createdTime = uhcPatientProgressNote.createdTime;
        this.patientCode = uhcPatientProgressNote.patientCode;
        this.doctorCode = uhcPatientProgressNote.doctorCode;
        this.patientName = uhcPatientProgressNote.patientName;
        this.facilityCode = uhcPatientProgressNote.facilityCode;
        this.specialty = uhcPatientProgressNote.specialty;
        this.facilityId = uhcPatientProgressNote.facilityId;
        this.type = uhcPatientProgressNote.type;
        this.progressCode = uhcPatientProgressNote.progressCode;
        this.note = uhcPatientProgressNote.note;
        this.freeNote = uhcPatientProgressNote.freeNote;
        this.doctorPhoto = uhcPatientProgressNote.doctorPhoto;
        this.doctorPhotoUrl = uhcPatientProgressNote.doctorPhotoUrl;
        this.facilityTitle = uhcPatientProgressNote.facilityTitle;
        this.id = uhcPatientProgressNote.id;
        this.facilityType = uhcPatientProgressNote.facilityType;
        this.accesstime = uhcPatientProgressNote.accesstime;
        this.isOnline = uhcPatientProgressNote.isOnline;
        this.hasChanges = uhcPatientProgressNote.hasChanges;
    }

    protected UhcPatientProgressNote(Parcel in) {
        localId = in.readString();
        doctorName = in.readString();
        isDeleted = in.readByte() != 0;
        progressNoteCode = in.readString();
        createdTime = in.readString();
        patientCode = in.readString();
        doctorCode = in.readString();
        patientName = in.readString();
        facilityCode = in.readString();
        specialty = in.readString();
        facilityId = in.readInt();
        type = in.readString();
        progressCode = in.readString();
        note = in.readString();
        freeNote = in.readString();
        doctorPhoto = in.readString();
        doctorPhotoUrl = in.readString();
        facilityTitle = in.readString();
        id = in.readInt();
        facilityType = in.readString();
        accesstime = in.readString();
        isOnline = in.readByte() != 0;
        hasChanges = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localId);
        dest.writeString(doctorName);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(progressNoteCode);
        dest.writeString(createdTime);
        dest.writeString(patientCode);
        dest.writeString(doctorCode);
        dest.writeString(patientName);
        dest.writeString(facilityCode);
        dest.writeString(specialty);
        dest.writeInt(facilityId);
        dest.writeString(type);
        dest.writeString(progressCode);
        dest.writeString(note);
        dest.writeString(freeNote);
        dest.writeString(doctorPhoto);
        dest.writeString(doctorPhotoUrl);
        dest.writeString(facilityTitle);
        dest.writeInt(id);
        dest.writeString(facilityType);
        dest.writeString(accesstime);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeByte((byte) (hasChanges ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UhcPatientProgressNote> CREATOR = new Creator<UhcPatientProgressNote>() {
        @Override
        public UhcPatientProgressNote createFromParcel(Parcel in) {
            return new UhcPatientProgressNote(in);
        }

        @Override
        public UhcPatientProgressNote[] newArray(int size) {
            return new UhcPatientProgressNote[size];
        }
    };

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getProgressNoteCode() {
        return progressNoteCode;
    }

    public void setProgressNoteCode(String progressNoteCode) {
        this.progressNoteCode = progressNoteCode;
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

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProgressCode() {
        return progressCode;
    }

    public void setProgressCode(String progressCode) {
        this.progressCode = progressCode;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFreeNote() {
        return freeNote;
    }

    public void setFreeNote(String freeNote) {
        this.freeNote = freeNote;
    }

    public String getDoctorPhoto() {
        return doctorPhoto;
    }

    public void setDoctorPhoto(String doctorPhoto) {
        this.doctorPhoto = doctorPhoto;
    }

    public String getDoctorPhotoUrl() {
        return doctorPhotoUrl;
    }

    public void setDoctorPhotoUrl(String doctorPhotoUrl) {
        this.doctorPhotoUrl = doctorPhotoUrl;
    }

    public String getFacilityTitle() {
        return facilityTitle;
    }

    public void setFacilityTitle(String facilityTitle) {
        this.facilityTitle = facilityTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
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

    public boolean isHasChanges() {
        return hasChanges;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    @Override
    @NonNull
    public String toString() {
        return "UhcPatientProgressNote{" +
                "localId='" + localId + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", isDeleted=" + isDeleted +
                ", progressNoteCode='" + progressNoteCode + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", patientCode='" + patientCode + '\'' +
                ", doctorCode='" + doctorCode + '\'' +
                ", patientName='" + patientName + '\'' +
                ", facilityCode='" + facilityCode + '\'' +
                ", specialty='" + specialty + '\'' +
                ", facilityId=" + facilityId +
                ", type='" + type + '\'' +
                ", progressCode='" + progressCode + '\'' +
                ", note='" + note + '\'' +
                ", freeNote='" + freeNote + '\'' +
                ", doctorPhoto='" + doctorPhoto + '\'' +
                ", doctorPhotoUrl='" + doctorPhotoUrl + '\'' +
                ", facilityTitle='" + facilityTitle + '\'' +
                ", id=" + id +
                ", facilityType='" + facilityType + '\'' +
                ", accesstime='" + accesstime + '\'' +
                ", isOnline=" + isOnline +
                ", hasChanges=" + hasChanges +
                '}';
    }
}