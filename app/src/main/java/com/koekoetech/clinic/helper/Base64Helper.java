package com.koekoetech.clinic.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.Nullable;

/**
 * Created by ZMN on 9/4/17.
 **/

public class Base64Helper {

    public static String fileToBase64(@Nullable File file) {
        if (file == null || !file.exists()) {
            return "";
        } else {
            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
    }


    public static byte[] Base64ToImage(String base64) {
        if (!TextUtils.isEmpty(base64)) {
            try {
                return Base64.decode(base64, Base64.DEFAULT);
            } catch (Exception e) {
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }


}
