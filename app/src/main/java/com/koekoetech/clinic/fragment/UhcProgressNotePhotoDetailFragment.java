package com.koekoetech.clinic.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;

/**
 * Created by ZMN on 8/29/17.
 **/

public class UhcProgressNotePhotoDetailFragment extends BaseFragment {

    private static final String ARG_PHOTO = "photo";

    private PhotoView imageViewLargeFoto;
    private ProgressBar progressBar;
    private LinearLayout retryContainerLayout;
    private Button reLoad;

    private UhcPatientProgressNotePhoto photoModel;

    public static UhcProgressNotePhotoDetailFragment newInstance(UhcPatientProgressNotePhoto photo) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photo);
        UhcProgressNotePhotoDetailFragment fragment = new UhcProgressNotePhotoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_photo_detail;
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {

        allFindViewbyId();

        if (getArguments() == null) {
            return;
        }

        photoModel = getArguments().getParcelable(ARG_PHOTO);
        if (photoModel == null) {
            Toast.makeText(getActivity(), "Invalid Photo", Toast.LENGTH_SHORT).show();
            return;
        }

        loadPhoto();

        reLoad.setOnClickListener(v -> loadPhoto());
    }

    private void allFindViewbyId() {
        imageViewLargeFoto = getView().findViewById(R.id.iv_lg_photo);
        progressBar = getView().findViewById(R.id.pb_photo_loading);
        retryContainerLayout = getView().findViewById(R.id.retry_container_layout);
        reLoad = getView().findViewById(R.id.btn_reload);
    }

    private void loadPhoto() {
        if (photoModel != null) {
            if (photoModel.isOnline()) {
                loadOnline(photoModel.getPhotoLink());
            } else {
                loadOffline(photoModel.getPhoto());
            }
        }
    }

    private void loadOffline(String photoLoc) {
        retryContainerLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        GlideApp.with(this)
                .load(photoLoc)
                .override(800, 600)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.image_loading)
                .into(imageViewLargeFoto);
    }

    private void loadOnline(String photoLink) {
        retryContainerLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        GlideApp.with(this)
                .load(photoLink)
                .override(800, 600)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageViewLargeFoto.setVisibility(View.GONE);
                        retryContainerLayout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        retryContainerLayout.setVisibility(View.GONE);
                        imageViewLargeFoto.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(imageViewLargeFoto);
    }

}
