package com.koekoetech.clinic.adapter.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.UhcPatientDetailActivity;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.helper.TextViewUtils;
import com.koekoetech.clinic.model.KeyValueText;
import com.koekoetech.clinic.model.UhcPatient;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 12/16/20.
 **/
public final class UhcPatientHolder extends RecyclerView.ViewHolder {

    private final TextView tvPatientType;
    private final TextView tvPatientName;
    private final TextView tvPatientAge;
    private final TextView tvPatientGender;
    private final AppCompatImageView ivPatientPhoto;
    private final FrameLayout layoutPatientTypeIndicator;
    private final CardView containerCard;

    private final Context mContext;
    private final boolean isUhcDoctor;
    private final boolean hasDoctorRole;

    private UhcPatientHolder(@NonNull View itemView, boolean isUhcDoctor, boolean hasDoctorRole) {
        super(itemView);

        this.isUhcDoctor = isUhcDoctor;
        this.hasDoctorRole = hasDoctorRole;
        mContext = itemView.getContext();

        // Bind Views
        tvPatientType = itemView.findViewById(R.id.itemUhcPatientTvType);
        tvPatientName = itemView.findViewById(R.id.itemUhcPatientTvName);
        tvPatientAge = itemView.findViewById(R.id.itemUhcPatientTvAge);
        tvPatientGender = itemView.findViewById(R.id.itemUhcPatientTvGender);
        ivPatientPhoto = itemView.findViewById(R.id.itemUhcPatientIvPhoto);
        layoutPatientTypeIndicator = itemView.findViewById(R.id.itemUhcPatientTypeIndicator);
        containerCard = itemView.findViewById(R.id.itemUhcPatientContainer);

    }

    public static UhcPatientHolder create(@NonNull final ViewGroup parent, boolean isUhcDoctor, boolean hasDoctorRole) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View itemView = layoutInflater.inflate(R.layout.layout_item_uhc_patient, parent, false);
        return new UhcPatientHolder(itemView, isUhcDoctor, hasDoctorRole);
    }

    public void bindPatient(@NonNull final UhcPatient uhcPatient) {
        final KeyValueText patientName = new KeyValueText(mContext.getString(R.string.item_patient_list_name), uhcPatient.getNameInEnglish());
        TextViewUtils.populateKeyValueTv(tvPatientName, patientName);

        String ageTypeUnit = "";

        String ageType = uhcPatient.getAgeType();
        if (!TextUtils.isEmpty(ageType)) {
            if (ageType.equalsIgnoreCase("Year")) {
                ageTypeUnit = "yrs";
            } else if (ageType.equalsIgnoreCase("Month")) {
                ageTypeUnit = "months";
            }
        }

        String ageWithUnit = uhcPatient.getAge() + " " + ageTypeUnit;

        final KeyValueText patientAge = new KeyValueText(mContext.getString(R.string.item_patient_list_age), ageWithUnit);
        TextViewUtils.populateKeyValueTv(tvPatientAge, patientAge);

        final KeyValueText patientGender = new KeyValueText(mContext.getString(R.string.item_patient_list_gender), uhcPatient.getGender());
        TextViewUtils.populateKeyValueTv(tvPatientGender, patientGender);

        tvPatientType.setVisibility(uhcPatient.isUHC() ? View.VISIBLE : View.GONE);

        if (isUhcDoctor) {
            @DrawableRes int indicatorColor = uhcPatient.isUHC() ? R.drawable.progress_note_problem_bg : R.drawable.progress_note_observation_bg;
            layoutPatientTypeIndicator.setVisibility(View.VISIBLE);
            layoutPatientTypeIndicator.setBackground(ContextCompat.getDrawable(mContext, indicatorColor));
        } else {
            layoutPatientTypeIndicator.setVisibility(View.GONE);
        }

        String photoLocation;

        if (!TextUtils.isEmpty(uhcPatient.getLocal_filepath())) {
            photoLocation = uhcPatient.getLocal_filepath();
        } else {
            photoLocation = uhcPatient.getPhotoUrl();
        }

        GlideApp.with(mContext)
                .load(photoLocation)
                .placeholder(R.drawable.img_placeholder_patient)
                .fallback(R.drawable.img_placeholder_patient)
                .error(R.mipmap.sample1)
                .apply(RequestOptions.circleCropTransform())
                .into(ivPatientPhoto);

        containerCard.setOnClickListener(v -> {
            if (hasDoctorRole) {
                mContext.startActivity(UhcPatientDetailActivity.getNewIntent(uhcPatient.getPatientCode(), mContext));
            } else {
                Toast.makeText(mContext, "Only doctor is authorized to view patient's detail.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
