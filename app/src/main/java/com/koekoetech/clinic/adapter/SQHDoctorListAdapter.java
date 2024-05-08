package com.koekoetech.clinic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.helper.TextViewUtils;
import com.koekoetech.clinic.interfaces.SqhDoctorSelectCallback;
import com.koekoetech.clinic.model.KeyValueText;
import com.koekoetech.clinic.model.StaffModel;

import androidx.recyclerview.widget.RecyclerView;

public class SQHDoctorListAdapter extends BaseRecyclerViewAdapter {

    private final SqhDoctorSelectCallback callback;

    public SQHDoctorListAdapter(SqhDoctorSelectCallback callback) {
        this.callback = callback;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refer_doctor, parent, false);
        return new SQHDoctorViewHolder(view);
    }

    @Override
    protected void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SQHDoctorViewHolder) holder).bindDoctor((StaffModel) getItemsList().get(position));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomHeaderViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindCustomHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    class SQHDoctorViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_doctor_name;
        private TextView txt_job_title;
        private TextView txt_clinic_name;
        private TextView txt_township;

        private ImageView ivDoctorPhotoUrl;

        private final Context context;

        SQHDoctorViewHolder(View itemView) {
            super(itemView);
            bindViews(itemView);
            context = itemView.getContext();
        }

        private void bindViews(View view) {
            txt_doctor_name = view.findViewById(R.id.txt_doctor_name);
            txt_job_title = view.findViewById(R.id.txt_job_title);
            txt_clinic_name = view.findViewById(R.id.txt_clinic_name);
            txt_township = view.findViewById(R.id.txt_township);
            ivDoctorPhotoUrl = view.findViewById(R.id.iv_doc_profile);
        }

        void bindDoctor(final StaffModel model) {

            KeyValueText docName = new KeyValueText(context.getString(R.string.item_sqh_doc_name), model.getName());
            TextViewUtils.populateKeyValueTv(txt_doctor_name, docName);

            KeyValueText docJD = new KeyValueText(context.getString(R.string.item_sqh_doc_job_description), model.getJobTitle());
            TextViewUtils.populateKeyValueTv(txt_job_title, docJD);

            KeyValueText docClinicName = new KeyValueText(context.getString(R.string.item_sqh_doc_clinic_name), model.getFacilityTitle());
            TextViewUtils.populateKeyValueTv(txt_clinic_name, docClinicName);

            KeyValueText docTsp = new KeyValueText(context.getString(R.string.item_sqh_doc_tsp), model.getTownship());
            TextViewUtils.populateKeyValueTv(txt_township, docTsp);

            GlideApp.with(context)
                    .load(model.getPhotoUrl())
                    .placeholder(R.mipmap.sample1)
                    .error(R.mipmap.sample1)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(ivDoctorPhotoUrl);

            itemView.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onDoctorSelected(new StaffModel(model));
                }
            });

        }
    }
}
