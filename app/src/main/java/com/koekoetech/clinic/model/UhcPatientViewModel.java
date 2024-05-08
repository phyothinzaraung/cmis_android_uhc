package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZMN on 8/24/17.
 **/

public class UhcPatientViewModel implements Parcelable {

    public static final Creator<UhcPatientViewModel> CREATOR = new Creator<UhcPatientViewModel>() {
        @Override
        public UhcPatientViewModel createFromParcel(Parcel in) {
            return new UhcPatientViewModel(in);
        }

        @Override
        public UhcPatientViewModel[] newArray(int size) {
            return new UhcPatientViewModel[size];
        }
    };
    @SerializedName("reg")
    @Expose
    private UhcPatient patient;
    @SerializedName("progresses")
    @Expose
    private ArrayList<UhcPatientProgressViewModel> progresses = new ArrayList<>();
    @SerializedName("DisabilitySurveyList")
    @Expose
    private ArrayList<DisabilitySurvey> disabilitySurveys = new ArrayList<>();

    public UhcPatientViewModel() {
    }

    protected UhcPatientViewModel(Parcel in) {
        patient = in.readParcelable(UhcPatient.class.getClassLoader());
        progresses = in.createTypedArrayList(UhcPatientProgressViewModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(patient, flags);
        dest.writeTypedList(progresses);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UhcPatient getPatient() {
        return patient;
    }

    public void setPatient(UhcPatient patient) {
        this.patient = patient;
    }

    public ArrayList<UhcPatientProgressViewModel> getProgresses() {
        return progresses;
    }

    public void setProgresses(ArrayList<UhcPatientProgressViewModel> progresses) {
        this.progresses = progresses;
    }

    public ArrayList<DisabilitySurvey> getDisabilitySurveys() {
        return disabilitySurveys;
    }

    public void setDisabilitySurveys(ArrayList<DisabilitySurvey> disabilitySurveys) {
        this.disabilitySurveys = disabilitySurveys;
    }

    @Nullable
    public String getPatientCode() {
        if (this.patient != null) {
            return this.patient.getPatientCode();
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return "UhcPatientViewModel{" +
                "patient=" + patient +
                ", progresses=" + progresses +
                ", disabilitySurveys=" + disabilitySurveys +
                '}';
    }
}
