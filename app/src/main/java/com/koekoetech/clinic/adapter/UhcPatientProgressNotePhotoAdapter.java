package com.koekoetech.clinic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.UhcProgressNotePhotoDetailActivity;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.helper.PressNoteTypeHelper;
import com.koekoetech.clinic.interfaces.UhcProgressNotePhotoCallback;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;

import java.util.ArrayList;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 8/27/17.
 **/

@SuppressWarnings("WeakerAccess")
public class UhcPatientProgressNotePhotoAdapter extends RecyclerView.Adapter<UhcPatientProgressNotePhotoAdapter.UhcPatientProgressNotePhotoHolder> {

    private static final String TAG = UhcPatientProgressNotePhotoAdapter.class.getSimpleName();

    private boolean showDeleteButton;
    private boolean showPhotoType;
    private ArrayList<UhcPatientProgressNotePhoto> photos;
    private ArrayList<UhcPatientProgressNotePhoto> removedPhotos;
    private UhcProgressNotePhotoCallback photoCallback;

    public UhcPatientProgressNotePhotoAdapter(boolean showDeleteButton, boolean showPhotoType) {
        this.showDeleteButton = showDeleteButton;
        this.showPhotoType = showPhotoType;
        this.photos = new ArrayList<>();
        this.removedPhotos = new ArrayList<>();
    }

    public void registerPhotoCallback(UhcProgressNotePhotoCallback photoCallback) {
        this.photoCallback = photoCallback;
    }

    @NonNull
    @Override
    public UhcPatientProgressNotePhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_record_photo_item, parent, false);
        return new UhcPatientProgressNotePhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UhcPatientProgressNotePhotoHolder holder, int position) {
        holder.bindProgressNotePhoto(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void addPhoto(UhcPatientProgressNotePhoto photo) {
        if (!photo.isDeleted()) {
            photos.add(photo);
            notifyItemInserted(photos.size());
        } else {
            removedPhotos.add(photo);
        }
    }

    public void addPhotos(ArrayList<UhcPatientProgressNotePhoto> newPhotos) {
        ArrayList<UhcPatientProgressNotePhoto> photosToAdd = new ArrayList<>();

        for (UhcPatientProgressNotePhoto photo : newPhotos) {
            if (photo.isDeleted()) {
                removedPhotos.add(photo);
            } else {
                photosToAdd.add(photo);
            }
        }

        photos.addAll(photosToAdd);
        notifyItemRangeInserted(photos.size() - photosToAdd.size(), photosToAdd.size());
    }

    public void replacePhotos(ArrayList<UhcPatientProgressNotePhoto> newPhotos) {
        clear();
        addPhotos(newPhotos);
    }

    public void clear() {
        photos.clear();
        removedPhotos.clear();
        notifyDataSetChanged();
    }

    public void removePhoto(int position) {
        Log.d(TAG, "removePhoto: Position = " + position + " Total = " + getItemCount());
        if (position >= 0 && position < getItemCount()) {
            photos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ArrayList<UhcPatientProgressNotePhoto> getPhotos() {
        ArrayList<UhcPatientProgressNotePhoto> allPhotos = new ArrayList<>();
        allPhotos.addAll(photos);
        allPhotos.addAll(removedPhotos);
        return allPhotos;
    }

    class UhcPatientProgressNotePhotoHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewPhoto;
        private ImageView imageViewDelete;

        private TextView textViewType;

        private CardView container;

        private Context mContext;

        UhcPatientProgressNotePhotoHolder(View itemView) {
            super(itemView);
            bindViews(itemView);
            mContext = itemView.getContext();
        }

        private void bindViews(View view){
            imageViewPhoto = view.findViewById(R.id.iv_progress_photo);
            imageViewDelete = view.findViewById(R.id.iv_progress_delete);
            textViewType = view.findViewById(R.id.tv_progress_type);
            container = view.findViewById(R.id.photo_item_container);
        }

        void bindProgressNotePhoto(final UhcPatientProgressNotePhoto photo) {

            if (showPhotoType) {
                if (!TextUtils.isEmpty(photo.getType())) {
                    textViewType.setVisibility(View.VISIBLE);
                    PressNoteTypeHelper typeHelper = PressNoteTypeHelper.getTypeHelper();
                    String prefix = typeHelper.getPrefixByType(photo.getType());
                    if (!TextUtils.isEmpty(prefix)) {
                        textViewType.setText(prefix);
                    }
                    try {
                        @DrawableRes int background = typeHelper.getTypeBackground(photo.getType());
                        textViewType.setBackgroundResource(background);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                textViewType.setVisibility(View.GONE);
            }

            if (showDeleteButton) {
                imageViewDelete.setVisibility(View.VISIBLE);
                imageViewDelete.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure to delete progress photo?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            dialog.dismiss();
                            photo.setDeleted(true);
                            photo.setHasChanges(true);
                            removedPhotos.add(photo);
                            removePhoto(getAdapterPosition());
                            if (photoCallback != null) {
                                photoCallback.onDeleted(getAdapterPosition());
                            }
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss()).create().show());
            } else {
                imageViewDelete.setVisibility(View.GONE);
            }

            if (photo.isOnline()) {
                Log.d(TAG, "bindProgressNotePhoto: Binding Photo " + photo.getPhotoLink());
                GlideApp.with(mContext)
                        .load(photo.getPhotoLink())
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_loading)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imageViewDelete.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imageViewPhoto);
            } else {
                Log.d(TAG, "bindProgressNotePhoto: Binding Photo " + photo.getPhoto());
                GlideApp.with(mContext)
                        .load(photo.getPhoto())
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.image_loading)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageViewPhoto);
            }

            imageViewPhoto.setOnClickListener(v -> {
                int photoPos = photos.indexOf(photo);
                int initPos = photoPos == -1 ? 0 : photoPos;
                mContext.startActivity(UhcProgressNotePhotoDetailActivity.getPhotoIntent(mContext, photos, initPos));
            });
        }

    }

}
