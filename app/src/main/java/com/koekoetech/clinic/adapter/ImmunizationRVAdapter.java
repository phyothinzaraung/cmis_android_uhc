package com.koekoetech.clinic.adapter;

import android.view.ViewGroup;

import com.koekoetech.clinic.adapter.viewholders.ImmunizationHolder;
import com.koekoetech.clinic.model.ImmunizationModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 3/28/18.
 **/
public class ImmunizationRVAdapter extends RecyclerView.Adapter<ImmunizationHolder> {

    private final ArrayList<ImmunizationModel> immunizationModels;
    private final boolean isItemClickable;

    public ImmunizationRVAdapter(ArrayList<ImmunizationModel> immunizationModels, boolean isClickable) {
        this.immunizationModels = immunizationModels != null ? immunizationModels : new ArrayList<>();
        this.isItemClickable = isClickable;
    }

    @NonNull
    @Override
    public ImmunizationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ImmunizationHolder.create(parent, isItemClickable);
    }

    @Override
    public void onBindViewHolder(@NonNull ImmunizationHolder holder, int position) {
        holder.bindImmunization(immunizationModels.get(position));
    }

    @Override
    public int getItemCount() {
        return immunizationModels.size();
    }

    public ArrayList<ImmunizationModel> getImmunizationModels() {
        return immunizationModels;
    }

    public boolean isCompleted() {
        for (ImmunizationModel im : immunizationModels) {
            if (!im.isDone()) {
                return false;
            }
        }
        return true;
    }

}
