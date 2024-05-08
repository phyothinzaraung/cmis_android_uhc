package com.koekoetech.clinic.adapter;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.PressNoteTypeHelper;
import com.koekoetech.clinic.interfaces.PressnoteOperationCallback;
import com.koekoetech.clinic.interfaces.ProgressCreatedTimeChangeCallback;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

/**
 * Created by ZMN on 8/27/17.
 **/

public class ProgressNotesAdapter extends BaseRecyclerViewAdapter {

    private PressnoteOperationCallback pressnoteOperationCallback;
    private ProgressCreatedTimeChangeCallback createdTimeChangeCallback;

    public void registerCallback(PressnoteOperationCallback callbackClass) {
        this.pressnoteOperationCallback = callbackClass;
    }

    public void setCreatedTimeChangeCallback(ProgressCreatedTimeChangeCallback createdTimeChangeCallback) {
        this.createdTimeChangeCallback = createdTimeChangeCallback;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_press_note_list, parent, false);
        return new ProgressNoteHolder(view);
    }

    @Override
    protected void onBindCustomViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ProgressNoteHolder) holder).bindProgressNote((UhcPatientProgressNoteViewModel) getItemsList().get(position));
    }

    @Override
    protected RecyclerView.ViewHolder onCreateCustomHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_created_time, parent, false);
        return new ProgressCreatedTimeHeader(view);
    }

    @Override
    protected void onBindCustomHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ProgressCreatedTimeHeader createdTimeHeader = (ProgressCreatedTimeHeader) holder;
        RecyclerHeader recyclerHeader = (RecyclerHeader) getItemsList().get(position);
        createdTimeHeader.bindCreatedTime((String) recyclerHeader.getHeaderData());

    }

    public class ProgressNoteHolder extends RecyclerView.ViewHolder {

        private FrameLayout prefixColorView;
        private TextView tv_content;
        private TextView tv_freeText;
        private RelativeLayout editLayout;
        private RelativeLayout deleteLayout;
        private RecyclerView rv_photoList;
        private TextView txt_progressnote_type;

        private UhcPatientProgressNotePhotoAdapter photoAdapter;

        private void allFindViewbyId(View itemView) {
            prefixColorView = itemView.findViewById(R.id.pnl_pressnote_color);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_freeText = itemView.findViewById(R.id.tv_freeText);
            editLayout = itemView.findViewById(R.id.pnl_edit);
            deleteLayout = itemView.findViewById(R.id.pnl_delete);
            rv_photoList = itemView.findViewById(R.id.pnl_rv_press_notes_photos);
            txt_progressnote_type = itemView.findViewById(R.id.txt_progressnote_type);
        }

        ProgressNoteHolder(View itemView) {
            super(itemView);
            allFindViewbyId(itemView);
        }

        void bindProgressNote(UhcPatientProgressNoteViewModel progressNoteViewModel) {

            UhcPatientProgressNote progressNote = progressNoteViewModel.getProgressNote();
            txt_progressnote_type.setText(progressNote.getType());
            try {
                txt_progressnote_type.setBackgroundResource(PressNoteTypeHelper.getTypeHelper().getTypeBackground(progressNote.getType()));
                prefixColorView.setBackgroundResource(PressNoteTypeHelper.getTypeHelper().getTypeBackground(progressNote.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (prefixColorView.getBackground() instanceof StateListDrawable) {
                StateListDrawable stateListDrawable = (StateListDrawable) prefixColorView.getBackground();
                DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) stateListDrawable.getConstantState();
                if (drawableContainerState != null) {
                    Drawable[] children = drawableContainerState.getChildren();
                    GradientDrawable selectedDrawable = (GradientDrawable) children[0];
                    GradientDrawable unSelectedDrawable = (GradientDrawable) children[1];
                    float[] conorRadii = {3, 3, 0, 0, 0, 0, 0, 0};
                    selectedDrawable.setCornerRadii(conorRadii);
                    unSelectedDrawable.setCornerRadii(conorRadii);
                }

            }

            if (!TextUtils.isEmpty(progressNote.getNote())) {
                tv_content.setVisibility(View.VISIBLE);
            } else {
                tv_content.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(progressNote.getFreeNote())) {
                tv_freeText.setVisibility(View.VISIBLE);
                tv_freeText.setText(progressNote.getFreeNote());
            } else {
                tv_freeText.setVisibility(View.GONE);
            }

            tv_content.setText(AppUtils.getProgressNoteKeyValueSpannable(progressNote.getNote()));

            photoAdapter = new UhcPatientProgressNotePhotoAdapter(false, false);
            rv_photoList.setHasFixedSize(true);
            rv_photoList.setNestedScrollingEnabled(false);
            rv_photoList.setAdapter(photoAdapter);

            if (progressNoteViewModel.getPhotos() == null || progressNoteViewModel.getPhotos().isEmpty()) {
                rv_photoList.setVisibility(View.GONE);
            } else {
                photoAdapter.replacePhotos(progressNoteViewModel.getPhotos());
                rv_photoList.setVisibility(View.VISIBLE);
            }

            editLayout.setOnClickListener(view -> pressnoteOperationCallback.update(getAdapterPosition()));

            deleteLayout.setOnClickListener(view -> pressnoteOperationCallback.delete(getAdapterPosition()));
        }

        public UhcPatientProgressNotePhotoAdapter getPhotoAdapter() {
            return photoAdapter;
        }
    }



    class ProgressCreatedTimeHeader extends RecyclerView.ViewHolder {

        private TextView tvCreatedTime;

        ProgressCreatedTimeHeader(View itemView) {
            super(itemView);
            tvCreatedTime = itemView.findViewById(R.id.tv_progress_created_time);
        }

        void bindCreatedTime(String createdTime) {

            tvCreatedTime.setText(DateTimeHelper.convertDateFormat(createdTime, DateTimeHelper.SERVER_DATE_TIME_FORMAT, DateTimeHelper.LOCAL_DATE_TIME_DISPLAY_FORMAT));

            if (createdTimeChangeCallback != null) {
                itemView.setOnClickListener(v -> createdTimeChangeCallback.onRequestCreatedTimeChange());
            }
        }

    }
}
