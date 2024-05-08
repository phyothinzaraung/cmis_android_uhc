package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UhcPatientProgressNotePhoto extends RealmObject implements Parcelable {

    @PrimaryKey
    private String localId;

    @SerializedName("PhotoNameId")
    @Expose
    private String photoNameId;

    @SerializedName("IsDeleted")
    @Expose
    private boolean isDeleted;

    @SerializedName("CreatedTime")
    @Expose
    private String createdTime;

    @SerializedName("ProgressNoteCode")
    @Expose
    private String progressNoteCode;

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("ProgressNoteId")
    @Expose
    private int progressNoteId;

    @SerializedName("PhotoLink")
    @Expose
    private String photoLink;

    @SerializedName("PhotoData")
    @Expose
    private String photo;

    @SerializedName("Accesstime")
    @Expose
    private String accesstime;

    private boolean isOnline;
    private String type;
    private boolean hasChanges;

    public UhcPatientProgressNotePhoto() {
    }

    public UhcPatientProgressNotePhoto(UhcPatientProgressNotePhoto progressNotePhoto) {
        if (progressNotePhoto == null) {
            return;
        }

        this.localId = progressNotePhoto.localId;
        this.photoNameId = progressNotePhoto.photoNameId;
        this.isDeleted = progressNotePhoto.isDeleted;
        this.createdTime = progressNotePhoto.createdTime;
        this.progressNoteCode = progressNotePhoto.progressNoteCode;
        this.id = progressNotePhoto.id;
        this.progressNoteId = progressNotePhoto.progressNoteId;
        this.photoLink = progressNotePhoto.photoLink;
        this.photo = progressNotePhoto.photo;
        this.accesstime = progressNotePhoto.accesstime;
        this.isOnline = progressNotePhoto.isOnline;
        this.type = progressNotePhoto.type;
        this.hasChanges = progressNotePhoto.hasChanges;
    }

    protected UhcPatientProgressNotePhoto(Parcel in) {
        localId = in.readString();
        photoNameId = in.readString();
        isDeleted = in.readByte() != 0;
        createdTime = in.readString();
        progressNoteCode = in.readString();
        id = in.readInt();
        progressNoteId = in.readInt();
        photoLink = in.readString();
        photo = in.readString();
        accesstime = in.readString();
        isOnline = in.readByte() != 0;
        type = in.readString();
        hasChanges = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localId);
        dest.writeString(photoNameId);
        dest.writeByte((byte) (isDeleted ? 1 : 0));
        dest.writeString(createdTime);
        dest.writeString(progressNoteCode);
        dest.writeInt(id);
        dest.writeInt(progressNoteId);
        dest.writeString(photoLink);
        dest.writeString(photo);
        dest.writeString(accesstime);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeString(type);
        dest.writeByte((byte) (hasChanges ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UhcPatientProgressNotePhoto> CREATOR = new Creator<UhcPatientProgressNotePhoto>() {
        @Override
        public UhcPatientProgressNotePhoto createFromParcel(Parcel in) {
            return new UhcPatientProgressNotePhoto(in);
        }

        @Override
        public UhcPatientProgressNotePhoto[] newArray(int size) {
            return new UhcPatientProgressNotePhoto[size];
        }
    };

    public String getPhotoNameId() {
        return photoNameId;
    }

    public void setPhotoNameId(String photoNameId) {
        this.photoNameId = photoNameId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getProgressNoteCode() {
        return progressNoteCode;
    }

    public void setProgressNoteCode(String progressNoteCode) {
        this.progressNoteCode = progressNoteCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgressNoteId() {
        return progressNoteId;
    }

    public void setProgressNoteId(int progressNoteId) {
        this.progressNoteId = progressNoteId;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isHasChanges() {
        return hasChanges;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UhcPatientProgressNotePhoto)) return false;

        UhcPatientProgressNotePhoto that = (UhcPatientProgressNotePhoto) o;

        if (isDeleted != that.isDeleted) return false;
        if (id != that.id) return false;
        if (progressNoteId != that.progressNoteId) return false;
        if (isOnline != that.isOnline) return false;
        if (hasChanges != that.hasChanges) return false;
        if (localId != null ? !localId.equals(that.localId) : that.localId != null) return false;
        if (photoNameId != null ? !photoNameId.equals(that.photoNameId) : that.photoNameId != null)
            return false;
        if (createdTime != null ? !createdTime.equals(that.createdTime) : that.createdTime != null)
            return false;
        if (progressNoteCode != null ? !progressNoteCode.equals(that.progressNoteCode) : that.progressNoteCode != null)
            return false;
        if (photoLink != null ? !photoLink.equals(that.photoLink) : that.photoLink != null)
            return false;
        if (photo != null ? !photo.equals(that.photo) : that.photo != null) return false;
        if (accesstime != null ? !accesstime.equals(that.accesstime) : that.accesstime != null)
            return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = localId != null ? localId.hashCode() : 0;
        result = 31 * result + (photoNameId != null ? photoNameId.hashCode() : 0);
        result = 31 * result + (isDeleted ? 1 : 0);
        result = 31 * result + (createdTime != null ? createdTime.hashCode() : 0);
        result = 31 * result + (progressNoteCode != null ? progressNoteCode.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + progressNoteId;
        result = 31 * result + (photoLink != null ? photoLink.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (accesstime != null ? accesstime.hashCode() : 0);
        result = 31 * result + (isOnline ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (hasChanges ? 1 : 0);
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        return "UhcPatientProgressNotePhoto{" +
                "localId='" + localId + '\'' +
                ", photoNameId='" + photoNameId + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdTime='" + createdTime + '\'' +
                ", progressNoteCode='" + progressNoteCode + '\'' +
                ", id=" + id +
                ", progressNoteId=" + progressNoteId +
                ", photoLink='" + photoLink + '\'' +
                ", photo='" + photo + '\'' +
                ", accesstime='" + accesstime + '\'' +
                ", isOnline=" + isOnline +
                ", type='" + type + '\'' +
                ", hasChanges=" + hasChanges +
                '}';
    }
}