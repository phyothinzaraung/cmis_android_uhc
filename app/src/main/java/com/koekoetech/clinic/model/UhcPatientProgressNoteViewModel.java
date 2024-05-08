package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.koekoetech.clinic.helper.Pageable;

import java.util.ArrayList;

/**
 * Created by ZMN on 8/29/17.
 **/

public class UhcPatientProgressNoteViewModel implements Parcelable, Pageable {

    public static final Creator<UhcPatientProgressNoteViewModel> CREATOR = new Creator<UhcPatientProgressNoteViewModel>() {
        @Override
        public UhcPatientProgressNoteViewModel createFromParcel(Parcel in) {
            return new UhcPatientProgressNoteViewModel(in);
        }

        @Override
        public UhcPatientProgressNoteViewModel[] newArray(int size) {
            return new UhcPatientProgressNoteViewModel[size];
        }
    };

    private UhcPatientProgressNote progressNote;
    private ArrayList<UhcPatientProgressNotePhoto> photos;

    public UhcPatientProgressNoteViewModel() {
    }

    protected UhcPatientProgressNoteViewModel(Parcel in) {
        progressNote = in.readParcelable(UhcPatientProgressNote.class.getClassLoader());
        photos = in.createTypedArrayList(UhcPatientProgressNotePhoto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(progressNote, flags);
        dest.writeTypedList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UhcPatientProgressNote getProgressNote() {
        return progressNote;
    }

    public void setProgressNote(UhcPatientProgressNote progressNote) {
        this.progressNote = progressNote;
    }

    public ArrayList<UhcPatientProgressNotePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<UhcPatientProgressNotePhoto> photos) {
        this.photos = photos;
    }
}
