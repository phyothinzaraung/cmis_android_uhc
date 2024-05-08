package com.koekoetech.clinic.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

/**
 * Created by ZMN on 5/30/17.
 **/

@SuppressWarnings("WeakerAccess, unused")
public class PermissionHelper {

    // Request Code for Permissions (External Storage Read and Write and Camera)
    public static final int REQ_PERMISSIONS = 43;

    public static final int REQUEST_PERMISSION_SETTING = 101;


    public static final String[] PERMISSIONS_TO_REQUEST = {Manifest.permission.CAMERA};

    public static void handlePermissions(@NonNull Activity activity) {
        if (requiresPermissions(activity, PERMISSIONS_TO_REQUEST)) {
            if (shouldShowRationlaeDialog(activity)) {
                getCameraStorageRationaleDialogBuilder(activity).show();
                return;
            }
            ActivityCompat.requestPermissions(activity, PERMISSIONS_TO_REQUEST, REQ_PERMISSIONS);
        }
    }

    public static boolean requiresPermission(@NonNull Context context, @NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requiresPermissions(@NonNull Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (requiresPermission(context, permission)) {
                return true;
            }
        }
        return false;
    }


    public static AlertDialog.Builder getCameraStorageRationaleDialogBuilder(@NonNull Activity activity) {
        String title = "Requires Following Permissions";
        String message = "This app needs to access your device camera and storage and this app ensures not to cause anything harmful. Please Grant requested permissions to continue.";
        return getRationaleDialogBuilder(activity, title, message, PERMISSIONS_TO_REQUEST, REQ_PERMISSIONS);
    }

    public static AlertDialog.Builder getRationaleDialogBuilder(@NonNull final Activity activity, @NonNull String title, @NonNull String message, @NonNull final String[] permissions, final int reqCode) {
        //Show Information about why you need the permission
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Grant", (dialog, which) -> {
            dialog.cancel();
            ActivityCompat.requestPermissions(activity, permissions, reqCode);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder;
    }

    public static boolean shouldShowRationlaeDialog(@NonNull Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    public static AlertDialog.Builder getPermissionFailureDialogBuilder(@NonNull final Activity activity) {
        //Show Information about why you need the permission
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Cannot Get Required Permission");
        builder.setMessage("Please grant the permissions manually from the settings page to continue");
        builder.setPositiveButton("Settings", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(activity, "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        return builder;
    }

}
