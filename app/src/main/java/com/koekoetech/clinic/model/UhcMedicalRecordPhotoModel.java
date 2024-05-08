package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.koekoetech.clinic.helper.Pageable;

/**
 * Created by Ko Pyae on 7/24/2017.
 */

public class UhcMedicalRecordPhotoModel implements Pageable,Parcelable {

    public static final Creator<UhcMedicalRecordPhotoModel> CREATOR = new Parcelable.Creator<UhcMedicalRecordPhotoModel>() {
        @Override
        public UhcMedicalRecordPhotoModel createFromParcel(Parcel in) {
            return new UhcMedicalRecordPhotoModel(in);
        }

        @Override
        public UhcMedicalRecordPhotoModel[] newArray(int size) {
            return new UhcMedicalRecordPhotoModel[size];
        }
    };
    private String photoLink;
    private String photoNameId;
    private String type;
    private String typePrefix;
    private int typeColor;

    public UhcMedicalRecordPhotoModel() {
    }

    protected UhcMedicalRecordPhotoModel(Parcel in) {
        photoLink = in.readString();
        photoNameId = in.readString();
        type = in.readString();
        typePrefix = in.readString();
        typeColor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoLink);
        dest.writeString(photoNameId);
        dest.writeString(type);
        dest.writeString(typePrefix);
        dest.writeInt(typeColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypePrefix() {
        return typePrefix;
    }

    public void setTypePrefix(String typePrefix) {
        this.typePrefix = typePrefix;
    }

    public int getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(int typeColor) {
        this.typeColor = typeColor;
    }

    public String getPhotoNameId() {
        return photoNameId;
    }

    public void setPhotoNameId(String photoNameId) {
        this.photoNameId = photoNameId;
    }

    @Override
    public String toString() {
        return "UhcMedicalRecordPhotoModel{" +
                "photoLink='" + photoLink + '\'' +
                ", type='" + type + '\'' +
                ", typePrefix='" + typePrefix + '\'' +
                ", typeColor=" + typeColor +
                '}';
    }
}
