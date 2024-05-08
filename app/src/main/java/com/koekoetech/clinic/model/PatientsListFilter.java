package com.koekoetech.clinic.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

/**
 * Created by ZMN on 12/17/20.
 **/
public final class PatientsListFilter implements Parcelable {

    public static final Creator<PatientsListFilter> CREATOR = new Creator<PatientsListFilter>() {
        @Override
        public PatientsListFilter createFromParcel(Parcel in) {
            return new PatientsListFilter(in);
        }

        @Override
        public PatientsListFilter[] newArray(int size) {
            return new PatientsListFilter[size];
        }
    };
    private final String patientName;
    private final String patientGender;
    private final String patientDob;
    private final String patientRegDate;
    private final String patientUid;
    private final String patientHHCode;
    private final String patientPhone;
    private final int patientStartAge;
    private final int patientEndAge;

    public PatientsListFilter(String patientName, String patientGender, String patientDob, String patientRegDate, String patientUid, String patientHHCode, String patientPhone, int patientStartAge, int patientEndAge) {
        this.patientName = patientName;
        this.patientGender = patientGender;
        this.patientDob = patientDob;
        this.patientRegDate = patientRegDate;
        this.patientUid = patientUid;
        this.patientHHCode = patientHHCode;
        this.patientPhone = patientPhone;
        this.patientStartAge = patientStartAge;
        this.patientEndAge = patientEndAge;
    }

    protected PatientsListFilter(Parcel in) {
        patientName = in.readString();
        patientGender = in.readString();
        patientDob = in.readString();
        patientRegDate = in.readString();
        patientUid = in.readString();
        patientHHCode = in.readString();
        patientPhone = in.readString();
        patientStartAge = in.readInt();
        patientEndAge = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientName);
        dest.writeString(patientGender);
        dest.writeString(patientDob);
        dest.writeString(patientRegDate);
        dest.writeString(patientUid);
        dest.writeString(patientHHCode);
        dest.writeString(patientPhone);
        dest.writeInt(patientStartAge);
        dest.writeInt(patientEndAge);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "PatientsListFilter{" +
                "patientName='" + patientName + '\'' +
                ", patientGender='" + patientGender + '\'' +
                ", patientDob='" + patientDob + '\'' +
                ", patientRegDate='" + patientRegDate + '\'' +
                ", patientUid='" + patientUid + '\'' +
                ", patientHHCode='" + patientHHCode + '\'' +
                ", patientPhone='" + patientPhone + '\'' +
                ", patientStartAge=" + patientStartAge +
                ", patientEndAge=" + patientEndAge +
                '}';
    }

    public RealmQuery<UhcPatient> getRealmQuery(@NonNull final Realm realm) {
        RealmQuery<UhcPatient> query = realm.where(UhcPatient.class)
                .equalTo("isDeleted", false);

        if (!TextUtils.isEmpty(patientUid)) {
            query.equalTo("uicCode", patientUid, Case.INSENSITIVE);
        } else {

            boolean containsConditions = false;

            // Filter Gender
            if (!TextUtils.isEmpty(patientGender) && !patientGender.equals("Both")) {
                containsConditions = true;
                query.equalTo("gender", patientGender, Case.INSENSITIVE);
            }

            // Filter Phone Number
            if (!TextUtils.isEmpty(patientPhone)) {
                containsConditions = true;
                query.equalTo("phone", patientPhone, Case.INSENSITIVE);
            }

            // Filter DOB
            if (!TextUtils.isEmpty(patientDob)) {
                containsConditions = true;
                query.contains("dateOfBirth", patientDob, Case.INSENSITIVE);
            }

            // Filter Reg Date
            if (!TextUtils.isEmpty(patientRegDate)) {
                containsConditions = true;
                query.contains("registrationDate", patientRegDate, Case.INSENSITIVE);
            }

            // Filter HH Code
            if (!TextUtils.isEmpty(patientHHCode)) {
                containsConditions = true;
                query.equalTo("hhCode", patientHHCode, Case.INSENSITIVE);
            }

            // Filter Name
            if (!TextUtils.isEmpty(patientName)) {
                containsConditions = true;
                query.beginGroup()
                        .contains("nameInEnglish", patientName, Case.INSENSITIVE)
                        .or()
                        .contains("nameInMyanmar", patientName, Case.INSENSITIVE)
                        .endGroup();
            }

            if (containsConditions) {
                query = query.findAll().where().beginGroup()
                        .equalTo("ageType", "Year", Case.INSENSITIVE)
                        .between("age", patientStartAge, patientEndAge)
                        .endGroup()
                        .or()
                        .beginGroup()
                        .equalTo("ageType", "Month", Case.INSENSITIVE)
                        .between("age", (patientStartAge * 12), (patientEndAge * 12))
                        .endGroup();
            } else {
                // Filter Age Range
                query
                        .beginGroup()
                        .equalTo("ageType", "Year", Case.INSENSITIVE)
                        .between("age", patientStartAge, patientEndAge)
                        .endGroup()
                        .or()
                        .beginGroup()
                        .equalTo("ageType", "Month", Case.INSENSITIVE)
                        .between("age", (patientStartAge * 12), (patientEndAge * 12))
                        .endGroup();
            }


        }

        return query.sort("createdTime", Sort.DESCENDING);
    }
}
