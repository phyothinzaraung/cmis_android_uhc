package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by ZMN on 8/24/17.
 **/

public class UhcPatientProgressViewModel implements Parcelable {

    public static final Creator<UhcPatientProgressViewModel> CREATOR = new Creator<UhcPatientProgressViewModel>() {
        @Override
        public UhcPatientProgressViewModel createFromParcel(Parcel in) {
            return new UhcPatientProgressViewModel(in);
        }

        @Override
        public UhcPatientProgressViewModel[] newArray(int size) {
            return new UhcPatientProgressViewModel[size];
        }
    };
    @SerializedName("prog")
    @Expose
    private UhcPatientProgress progress;

    @SerializedName("progNoteList")
    @Expose
    private ArrayList<UhcPatientProgressNote> progressNotes = new ArrayList<>();

    @SerializedName("photoList")
    @Expose
    private ArrayList<UhcPatientProgressNotePhoto> progressNotePhotos = new ArrayList<>();

    public UhcPatientProgressViewModel() {
    }

    protected UhcPatientProgressViewModel(Parcel in) {
        progress = in.readParcelable(UhcPatientProgress.class.getClassLoader());
        progressNotes = in.createTypedArrayList(UhcPatientProgressNote.CREATOR);
        progressNotePhotos = in.createTypedArrayList(UhcPatientProgressNotePhoto.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(progress, flags);
        dest.writeTypedList(progressNotes);
        dest.writeTypedList(progressNotePhotos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UhcPatientProgress getProgress() {
        return progress;
    }

    public void setProgress(UhcPatientProgress progress) {
        this.progress = progress;
    }

    public ArrayList<UhcPatientProgressNote> getProgressNotes() {
        return progressNotes;
    }

    public void setProgressNotes(ArrayList<UhcPatientProgressNote> progressNotes) {
        this.progressNotes = progressNotes;
    }


    public ArrayList<UhcPatientProgressNotePhoto> getProgressNotePhotos() {
        return progressNotePhotos;
    }

    public void setProgressNotePhotos(ArrayList<UhcPatientProgressNotePhoto> progressNotePhotos) {
        this.progressNotePhotos = progressNotePhotos;
    }

    @Override
    public String toString() {
        return "UhcPatientProgressViewModel{" +
                "progress=" + progress +
                ", progressNotes=" + progressNotes +
                ", progressNotePhotos=" + progressNotePhotos +
                '}';
    }
}
