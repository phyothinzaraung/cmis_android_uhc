package com.koekoetech.clinic.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

/**
 * Created by ZMN on 5/29/17.
 **/
@SuppressWarnings("WeakerAccess")
public class PhotoUtils {

    // Picks image directly from image_load_error camera
    public static final int SOURCE_DEFAULT_CAMERA = 60;

    // Picks image from all installed camera apps
    public static final int SOURCE_ALL_CAMERAS = 61;

    // Picks image from galleries
    public static final int SOURCE_GALLERIES = 62;

    //The request code used to start pick image activity to be used on result to identify the this specific request.
    public static final int IMAGE_REQUEST_CODE = 732;

    // Picks image from all possible sources available on device
    public static final int SOURCE_ALL = 63;

    // TAG from Logging
    private static final String TAG = PhotoUtils.class.getSimpleName();

    public static void pickImage(int source, String chooserTitle, @NonNull Fragment fragment) {
        fragment.startActivityForResult(getChooserIntentBySourceType(source, chooserTitle, fragment.requireActivity()), IMAGE_REQUEST_CODE);
    }

    public static void pickImage(int source, String chooserTitle, @NonNull android.app.Fragment fragment) {
        fragment.startActivityForResult(getChooserIntentBySourceType(source, chooserTitle, fragment.getActivity()), IMAGE_REQUEST_CODE);
    }

    public static void pickImage(int source, String chooserTitle, @NonNull Activity activity) {
        activity.startActivityForResult(getChooserIntentBySourceType(source, chooserTitle, activity), IMAGE_REQUEST_CODE);
    }

    public static void pickImage(int source, @NonNull Activity activity) {
        pickImage(source, "Select Source", activity);
    }

    public static void pickImage(int source, @NonNull Fragment fragment) {
        pickImage(source, "Select Source", fragment);
    }

    public static void pickImage(int source, @NonNull android.app.Fragment fragment) {
        pickImage(source, "Select Source", fragment);
    }

    public static void captureImage(@NonNull Activity activity) {
        activity.startActivityForResult(getCameraIntent(activity), IMAGE_REQUEST_CODE);
    }

    public static void captureImage(@NonNull Fragment fragment) {
        fragment.startActivityForResult(getCameraIntent(fragment.requireActivity()), IMAGE_REQUEST_CODE);
    }

    public static void captureImage(@NonNull android.app.Fragment fragment) {
        fragment.startActivityForResult(getCameraIntent(fragment.getActivity()), IMAGE_REQUEST_CODE);
    }

    public static Intent getChooserIntentBySourceType(int source, String chooserTitle, @NonNull Context context) {
        Intent intent;
        switch (source) {
            case SOURCE_ALL_CAMERAS:
                intent = getAllCamerasChooserIntent(chooserTitle, context);
                break;
            case SOURCE_GALLERIES:
                intent = getPickImageChooserIntent(chooserTitle, false, false, context);
                break;
            default:
                intent = getPickImageChooserIntent(chooserTitle, false, true, context);
        }
        return intent;
    }


    /**
     * Create a chooser intent to select the  source to get image from.<br>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br>
     * All possible sources are added to the intent chooser.
     *
     * @param chooserTitle     the title to use for the chooser UI
     * @param includeDocuments if to include KitKat documents activity containing all sources
     * @param includeCamera    if to include camera intents
     */
    public static Intent getPickImageChooserIntent(String chooserTitle, boolean includeDocuments, boolean includeCamera, @NonNull Context context) {

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context));
        }

        List<Intent> galleryIntents = getGalleryIntents(Intent.ACTION_GET_CONTENT, includeDocuments, context);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = getGalleryIntents(Intent.ACTION_PICK, includeDocuments, context);
        }
        allIntents.addAll(galleryIntents);

        Intent target;
        if (allIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = allIntents.get(allIntents.size() - 1);
            allIntents.remove(allIntents.size() - 1);
        }

        return getChooserIntent(target, allIntents, chooserTitle);
    }

    public static Intent getAllCamerasChooserIntent(String chooserTitle, @NonNull Context context) {
        Intent mainIntent = new Intent();
        List<Intent> allIntents = new ArrayList<>();
        if (!isExplicitCameraPermissionRequired(context)) {
            allIntents = getCameraIntents(context);
        }
        return getChooserIntent(mainIntent, allIntents, chooserTitle);
    }

    public static Intent getChooserIntent(Intent target, List<Intent> choiceIntents, String chooserTitle) {
        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, chooserTitle);

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, choiceIntents.toArray(new Parcelable[0]));

        return chooserIntent;
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle images.
     */
    public static List<Intent> getGalleryIntents(String action, boolean includeDocuments, @NonNull Context context) {
        List<Intent> intents = new ArrayList<>();
        Intent galleryIntent = action.equalsIgnoreCase(Intent.ACTION_GET_CONTENT) ? new Intent(action)
                : new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = context.getPackageManager().queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intents.add(intent);
        }

        // remove documents intent
        if (!includeDocuments) {
            for (Intent intent : intents) {
                ComponentName component = intent.getComponent();
                if (component != null && component.getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    intents.remove(intent);
                    break;
                }
            }
        }
        return intents;
    }

    /**
     * Get all Camera intents for capturing image using device camera apps.
     */
    public static List<Intent> getCameraIntents(@NonNull Context context) {

        List<Intent> allIntents = new ArrayList<>();

        // Determine Uri of camera image to  save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = context.getPackageManager().queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        return allIntents;
    }

    /**
     * Get the main Camera intent for capturing image using device camera app.
     * If the outputFileUri is null, a image_load_error Uri will be created with {@link #getCaptureImageOutputUri(Context)}, so
     * then
     * you will be able to get the pictureUri using {@link #getPickImageResultUri(Intent, Context)}. Otherwise, it is
     * just you use
     * the Uri passed to this method.
     */
    public static Intent getCameraIntent(@NonNull Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = getCaptureImageOutputUri(context);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClipData(ClipData.newRawUri("", photoURI));
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent;
    }

    /**
     * Returns the file object reference to the taken image file
     */
    public static File getCapturedImageOutputFile(@NonNull Context context) {
        return new File(context.getCacheDir(), "capturedImage.jpeg");
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    public static Uri getCaptureImageOutputUri(@NonNull Context context) {
        return FileProvider.getUriForFile(context, context.getPackageName(), getCapturedImageOutputFile(context));
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * Check if explicetly requesting camera permission is required.<br>
     * It is required in Android Marshmellow and above if "CAMERA" permission is requested in the manifest.<br>
     * See <a href="http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug">StackOverflow
     * question</a>.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isExplicitCameraPermissionRequired(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasPermissionInManifest("android.permission.CAMERA", context) && context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param permissionName the permission to check
     * @return true - the permission in requested in manifest, false - not.
     */
    public static boolean hasPermissionInManifest(@NonNull String permissionName, @NonNull Context context) {
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equalsIgnoreCase(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "hasPermissionInManifest: ", e);
        }
        return false;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent(String chooserTitle, boolean includeDocuments, boolean includeCamera, Context context)}.<br>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public static Uri getPickImageResultUri(@Nullable Intent data, @NonNull Context context) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera || data.getData() == null ? Uri.fromFile(getCapturedImageOutputFile(context)) : data.getData();
    }

    /**
     * Get the File Path of the selected image from {@link #getPickImageChooserIntent(String chooserTitle, boolean includeDocuments, boolean includeCamera, Context context)}.<br>
     * Will return the correct File Path for camera and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public static String getPickImageResultPath(@Nullable Intent data, @NonNull Context context) {
        return getPath(getPickImageResultUri(data, context), context);
    }

    /**
     * Get the File of the selected image from {@link #getPickImageChooserIntent(String chooserTitle, boolean includeDocuments, boolean includeCamera, Context context)}.<br>
     * Will return the correct File for camera and gallery image.
     *
     * @param data the returned data of the  activity result
     */
    public static File getPickImageResultFile(@NonNull Intent data, @NonNull Context context) {
        File file = new File(getPickImageResultPath(data, context));
        return file.exists() ? file : null;
    }

    /**
     * Get Path from Image URI
     *
     * @param uri Image Uri
     * @return Image File Path
     */

    public static String getPath(@NonNull Uri uri, @NonNull Context context) {
        String filePath = MediaStore.Images.Media.DATA;
        String[] projection = {filePath};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index;
        if (cursor != null) {
            try {
                column_index = cursor.getColumnIndexOrThrow(filePath);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "getPath: " + uri.getPath(), e);
                return uri.getPath();
            } finally {
                cursor.close();
            }
        } else
            return uri.getPath();
    }

    /**
     * Returns the file name including file extension of given Uri.
     * .i.e. the last segment of uri (split by "/")
     */
    public static String getFileNameWithExtension(@NonNull Uri uri, @NonNull Context context) {
        String path = getPath(uri, context);
        String[] pathSegments = path.split(File.separator);
        return pathSegments[pathSegments.length - 1];
    }

    /**
     * Returns the file name excluding file extension of given Uri.
     */
    public static String getFileName(@NonNull Uri uri, @NonNull Context context) {
        String fileNameWithExtension = getFileNameWithExtension(uri, context);
        Log.i(TAG, "getFileName: File Name With Extension " + fileNameWithExtension);
        if (fileNameWithExtension.contains(".")) {
            int extensionPosition = fileNameWithExtension.lastIndexOf(".");
            if (extensionPosition != -1 && extensionPosition < fileNameWithExtension.length()) {
                return fileNameWithExtension.substring(0, extensionPosition);
            } else {
                return fileNameWithExtension;
            }
        } else {
            return fileNameWithExtension;
        }
    }

    /**
     * Returns the file extension of given Uri
     * If the file doesn't have any extension (for example an Uri for Folder),
     * an empty string is returned
     */
    public static String getFileExtension(@NonNull Uri uri, @NonNull Context context) {
        String fileNameWithExtension = getFileNameWithExtension(uri, context);
        Log.i(TAG, "getFileName: File Name With Extension " + fileNameWithExtension);
        if (fileNameWithExtension.contains(".")) {
            int extensionPosition = fileNameWithExtension.lastIndexOf(".");
            if (extensionPosition != -1 && extensionPosition < fileNameWithExtension.length()) {
                return fileNameWithExtension.substring(extensionPosition + 1);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

}