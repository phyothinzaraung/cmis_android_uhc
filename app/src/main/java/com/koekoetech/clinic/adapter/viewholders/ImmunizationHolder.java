package com.koekoetech.clinic.adapter.viewholders;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.model.ImmunizationModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 12/23/20.
 **/
public final class ImmunizationHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ImmunizationHolder";

    private final ToggleButton toggleButton;

    private final boolean isItemClickable;

    private ImmunizationHolder(@NonNull View itemView, boolean isItemClickable) {
        super(itemView);
        this.isItemClickable = isItemClickable;

        // bind views
        this.toggleButton = itemView.findViewById(R.id.itemImmunization_btnCompleteToggle);
    }

    public static ImmunizationHolder create(@NonNull final ViewGroup parent, boolean isItemClickable) {
        final Context context = parent.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View itemView = layoutInflater.inflate(R.layout.item_immunization, parent, false);
        return new ImmunizationHolder(itemView, isItemClickable);
    }

    public void bindImmunization(@NonNull final ImmunizationModel immunizationModel) {
        Log.d(TAG, "bindImmunization() called with: immunizationModel = [" + immunizationModel + "]");
        toggleButton.setTextOff(immunizationModel.getTitle());
        toggleButton.setTextOn(immunizationModel.getTitle());
        toggleButton.setChecked(immunizationModel.isDone());
        toggleButton.setOnClickListener(v -> {
            if (isItemClickable) {
                immunizationModel.setDone(toggleButton.isChecked());
            } else {
                toggleButton.setChecked(!toggleButton.isChecked());
            }
        });
    }
}
