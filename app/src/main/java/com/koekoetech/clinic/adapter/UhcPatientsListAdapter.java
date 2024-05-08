package com.koekoetech.clinic.adapter;

import android.view.ViewGroup;

import com.koekoetech.clinic.adapter.viewholders.UhcPatientHolder;
import com.koekoetech.clinic.model.UhcPatient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZMN on 6/6/18.
 **/
public class UhcPatientsListAdapter extends RealmRecyclerViewAdapter<UhcPatient, UhcPatientHolder> {

    private final boolean isUhcDoctor;
    private final boolean hasDoctorRole;

    public UhcPatientsListAdapter(boolean isUhcDoctor, boolean hasDoctorRole) {
        super(null, true, true);
        this.isUhcDoctor = isUhcDoctor;
        this.hasDoctorRole = hasDoctorRole;
    }

    @NonNull
    @Override
    public UhcPatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return UhcPatientHolder.create(parent, isUhcDoctor, hasDoctorRole);
    }

    @Override
    public void onBindViewHolder(@NonNull UhcPatientHolder holder, int position) {
        @Nullable final UhcPatient uhcPatient = getItem(position);
        if (uhcPatient != null) {
            holder.bindPatient(uhcPatient);
        }
    }

}
