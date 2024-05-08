package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.AppProgressDialog;
import com.koekoetech.clinic.helper.FileHelper;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageCropperActivity extends BaseActivity {

    public static final String TAG = "ImageCropperActivity";
    public static final String EXTRA_CROPPED_IMAGE_PATH = "extra_cropped_image_path";
    private static final String EXTRA_IMAGE_URI = "image_uri";
    private static final String EXTRA_SOURCE_IMG_URI = "extra_source_image_uri";
    private static final String EXTRA_OUTPUT_DIRECTORY = "extra_output_directory";
    private static final String DEFAULT_OUTPUT_DIRECTORY = "Profiles";

    private CropImageView mCropImageView;
    private Button btnCancel;
    private Button btnCrop;
    private ImageButton btnRotateLeft;
    private ImageButton btnRotateRight;

    private Uri imageUriToCrop;

    private String outputDirectoryName;

    private AppProgressDialog pDialog;

    public static Intent getCropperIntent(@NonNull final Context context, @NonNull final Uri sourceImageUri, @Nullable String outputDirectoryName) {
        Log.d(TAG, "getCropperIntent() called with: sourceImageUri = [" + sourceImageUri + "], outputDirectoryName = [" + outputDirectoryName + "]");
        return new Intent(context, ImageCropperActivity.class)
                .putExtra(EXTRA_SOURCE_IMG_URI, sourceImageUri)
                .putExtra(EXTRA_OUTPUT_DIRECTORY, outputDirectoryName);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_image_cropper;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        imageUriToCrop = getIntent().getParcelableExtra(EXTRA_SOURCE_IMG_URI);
        if (imageUriToCrop == null) {
            Toast.makeText(this, "No image to crop.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        outputDirectoryName = getIntent().getStringExtra(EXTRA_OUTPUT_DIRECTORY);
        if (TextUtils.isEmpty(outputDirectoryName)) {
            outputDirectoryName = DEFAULT_OUTPUT_DIRECTORY;
        }

        init();

        Log.d(TAG, "onCreate: " + imageUriToCrop.toString());
        mCropImageView.startLoad(imageUriToCrop, new LoadCallback() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "Successfully loaded image to crop view.");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "Failed to load image to crop view.");
            }
        });

        triggerOnClickListener();
    }

    private void init() {
        mCropImageView = findViewById(R.id.activity_image_cropper_cropImageView);
        btnCancel = findViewById(R.id.activity_image_cropper_btn_cancel);
        btnCrop = findViewById(R.id.activity_image_cropper_btn_crop);
        btnRotateLeft = findViewById(R.id.activity_image_cropper_btn_crop_rotate_left);
        btnRotateRight = findViewById(R.id.activity_image_cropper_btn_crop_rotate_right);

        mCropImageView.setCropMode(CropImageView.CropMode.SQUARE);
        mCropImageView.setOutputMaxSize(250, 250);

        pDialog = AppProgressDialog.getProgressDialog("Processing Image...");
        pDialog.setCancelable(false);
    }

    private void triggerOnClickListener() {
        btnCrop.setOnClickListener(v -> {
            showPDialog();

            mCropImageView.crop(imageUriToCrop)
                    .outputMaxWidth(300)
                    .outputMaxHeight(300)
                    .execute(new CropCallback() {
                        @Override
                        public void onSuccess(Bitmap cropped) {
                            final String fileName = System.currentTimeMillis() + ".jpg";
                            final File profilesDir = getPhotoStorageDir();
                            if (!profilesDir.exists()) {
                                final boolean isCreated = profilesDir.mkdirs();
                                Log.d(TAG, "onSuccess: Creating directory for storing profile images : " + isCreated);
                            }
                            final File image = new File(profilesDir, fileName);
                            final Uri saveUri = Uri.fromFile(image);
                            mCropImageView
                                    .save(cropped)
                                    .compressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressQuality(85)
                                    .execute(saveUri, new SaveCallback() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            hidePDialog();
                                            Intent intent = new Intent();
                                            intent.putExtra(EXTRA_CROPPED_IMAGE_PATH, uri.getPath());
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            hidePDialog();
                                            Toast.makeText(ImageCropperActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });

        });

        btnCancel.setOnClickListener(v -> finish());

        btnRotateLeft.setOnClickListener(v -> mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D));

        btnRotateRight.setOnClickListener(v -> mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_IMAGE_URI, imageUriToCrop);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        imageUriToCrop = savedInstanceState.getParcelable(EXTRA_IMAGE_URI);
    }

    @NonNull
    private File getPhotoStorageDir() {
        return new File(FileHelper.getStorageDir(getApplicationContext()), outputDirectoryName);
    }

    private void showPDialog() {
        if (!pDialog.isDisplaying()) {
            pDialog.display(getSupportFragmentManager());
        }
    }

    private void hidePDialog() {
        if (pDialog.isDisplaying()) {
            pDialog.safeDismiss();
        }
    }
}
