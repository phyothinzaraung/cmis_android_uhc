package com.koekoetech.clinic.helper;

import android.content.Context;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Created by ZMN on 6/1/17.
 **/

@SuppressWarnings({"WeakerAccess", "unused"})
public class FileHelper {

    public static final String PROFILE_DIR = "Profiles";
    public static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final int NOT_FOUND = -1;
    private static final String TAG = FileHelper.class.getSimpleName();

    public static String getFileNameWithoutExtension(File file) {
        String nameWithoutExtension = "";

        if (file != null) {
            String fileName = file.getName();
            int extensionPosition = fileName.lastIndexOf(".");
            if (extensionPosition > 0) {
                nameWithoutExtension = fileName.substring(0, extensionPosition);
            }
        }

        return nameWithoutExtension;
    }

    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static void compressFiles(String zipOutputPath, ArrayList<File> filesToCompress, String password)
            throws ZipException {
        ZipFile zipFile = new ZipFile(zipOutputPath);
        // Setting parameters
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        // Setting password
        zipParameters.setPassword(password);
        zipFile.addFiles(filesToCompress, zipParameters);
    }

    public static void compressFile(File fileToCompress, String password, boolean deleteFile) throws ZipException {
        if (fileToCompress != null) {
            String secureZipFileName = FileHelper.getFileNameWithoutExtension(fileToCompress) + ".zip";
            String secureZipFilePath = new File(fileToCompress.getParentFile(), secureZipFileName).getAbsolutePath();
            compressFile(secureZipFilePath, fileToCompress, password);
            if (deleteFile) {
                delete(fileToCompress);
            }
        }
    }

    public static void compressFile(String zipOutputPath, File fileToCompress, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(zipOutputPath);
        // Setting parameters
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        // Setting password
        zipParameters.setPassword(password);
        zipFile.addFile(fileToCompress, zipParameters);
    }

    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        return filename.lastIndexOf(UNIX_SEPARATOR);
    }

    public static File getStorageDir(Context context) {
        String hmisRootStoragePath = context.getFilesDir().getAbsolutePath() + UNIX_SEPARATOR + "CMIS_UHC";
        File rootStorage = new File(hmisRootStoragePath);
        if (!rootStorage.exists()) {
            if (rootStorage.mkdir()) {
                Log.d(TAG, "getStorageDir: HMIS Storage Created at " + rootStorage.getAbsolutePath());
            }
        }

        return rootStorage;
    }

    public static void delete(@Nullable final File file) {
        if (file == null || !file.exists()) {
            Log.d(TAG, "delete: File/Directory is null or doesn't exists.");
            return;
        }
        try {
            if (file.isDirectory()) {
                //directory is empty, then delete it
                @Nullable final String[] files = file.list();
                if (files == null || files.length == 0) {
                    final boolean isDirectoryDeleted = file.delete();
                    Log.d(TAG, "delete: Is " + file.getAbsolutePath() + " deleted ? : " + isDirectoryDeleted);
                } else {
                    //list all the directory contents
                    for (String temp : files) {
                        //construct the file structure
                        File fileDelete = new File(file, temp);

                        //recursive delete
                        delete(fileDelete);
                    }

                    delete(file);
                }

            } else {
                //if file, then delete it
                final boolean isFileDeleted = file.delete();
                Log.d(TAG, "delete: Is " + file.getAbsolutePath() + " deleted ? : " + isFileDeleted);
            }
        } catch (Exception e) {
            Log.e(TAG, "delete: ", e);
        }
    }

    public static void createDir(@Nullable final File directory) {
        if (directory == null) {
            Log.d(TAG, "createDir: Skipping Directory Creation as parameter is null.");
            return;
        }

        if (!directory.exists()) {
            final boolean isDirectoryCreated = directory.mkdirs();
            Log.d(TAG, "createDir: Is directory created at " + directory.getAbsolutePath() + " : " + isDirectoryCreated);
        } else {
            Log.d(TAG, "createDir: No need to create directory. Already exists.");
        }
    }

}
