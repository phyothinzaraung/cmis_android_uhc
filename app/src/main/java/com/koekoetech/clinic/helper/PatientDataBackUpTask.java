package com.koekoetech.clinic.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatientViewModel;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import io.realm.Realm;

/**
 * Created by ZMN on 8/26/18.
 **/
public abstract class PatientDataBackUpTask extends LCAUiTask<ZipFile> {

    private static final String TAG = "PatientDataBackUpTask";

    private File externalFileDir;

    public PatientDataBackUpTask(@NonNull final LifecycleOwner lifecycleOwner, Context context) {
        super(lifecycleOwner);
        externalFileDir = context.getExternalFilesDir(null);
    }

    private boolean writeStringToFile(@NonNull String str, @NonNull File file) {

        boolean isCreated;

        try {
            isCreated = file.createNewFile();
            Log.d(TAG, "writeStringToFile: Is File Created : " + isCreated);
        } catch (IOException e) {
            Log.e(TAG, "writeStringToFile: ", e);
            isCreated = false;
        }

        if (!isCreated) {
            return false;
        }

        try (
                final FileOutputStream fos = new FileOutputStream(file);
                final OutputStreamWriter osw = new OutputStreamWriter(fos)
        ) {
            osw.write(str);
            fos.flush();
            Log.d(TAG, "writeStringToFile: File written at : " + file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeStringToFile: ", e);
            return false;
        }
    }

    @Override
    protected ZipFile doWork() {
        try (Realm realm = Realm.getDefaultInstance()) {

            final String currentDateTime = DateTimeHelper.formatDate(new Date(), "yyyy-MM-dd-HH-mm-ss");
            String staffId = "";
            AuthenticationModel authenticationModel = realm.where(AuthenticationModel.class).findFirst();

            if (authenticationModel != null) {
                staffId = String.valueOf(authenticationModel.getStaffId());
            }

            final String fileNamePrefix = staffId + "-" + currentDateTime;

            final String patientDataBackUpFileName = fileNamePrefix + "-patient-data.json";
            final String suggestionBackUpFileName = fileNamePrefix + "-suggestions.json";
            final String outputZipName = fileNamePrefix + ".zip";

            Log.d(TAG, "doInBackground: Patient Data Back Up File Name : " + patientDataBackUpFileName);
            Log.d(TAG, "doInBackground: Suggestion Back Up File Name : " + suggestionBackUpFileName);

            final Gson gson = ServiceHelper.getGson();

            @NonNull final List<UhcPatientViewModel> patientsToUpload = RealmAccessHelper.getPatientsToUpload(realm);
            final boolean isPatientDataEmpty = patientsToUpload.isEmpty();

            boolean isPatientDataBackUpWritten = false;
            boolean isSuggestionBackUpFileWritten = false;

            File patientDataBackUpFile = new File(externalFileDir, patientDataBackUpFileName);

            File suggestionDataBackUpFile = new File(externalFileDir, suggestionBackUpFileName);

            if (!patientsToUpload.isEmpty()) {
                String patientDataStr = gson.toJson(patientsToUpload);
                isPatientDataBackUpWritten = writeStringToFile(patientDataStr, patientDataBackUpFile);
            }


            @NonNull final List<SuggestionWordModel> suggestionsToUpload = RealmAccessHelper.getSuggestionsToUpload(realm);
            boolean isSuggestionsEmpty = suggestionsToUpload.isEmpty();

            if (!isSuggestionsEmpty) {
                String suggestionStr = gson.toJson(suggestionsToUpload);
                isSuggestionBackUpFileWritten = writeStringToFile(suggestionStr, suggestionDataBackUpFile);
            }


            // Setting parameters
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            // Setting password
            zipParameters.setPassword(CMISConstant.DATA_EXPORT_ZIP_PASSWORD);
            File zipDestination = new File(externalFileDir, outputZipName);
            ZipFile dataBackUp = new ZipFile(zipDestination);
            ArrayList<File> filesToCompress = new ArrayList<>();

            if (!isPatientDataEmpty && isPatientDataBackUpWritten) {
                filesToCompress.add(patientDataBackUpFile);
            }

            if (!isSuggestionsEmpty && isSuggestionBackUpFileWritten) {
                filesToCompress.add(suggestionDataBackUpFile);
            }

            if (!filesToCompress.isEmpty()) {
                dataBackUp.addFiles(filesToCompress, zipParameters);
                FileHelper.delete(patientDataBackUpFile);
                FileHelper.delete(suggestionDataBackUpFile);
            }

            return dataBackUp;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void thenDoUiRelatedWork(ZipFile zipFile) {
        onComplete(zipFile);
    }

    protected abstract void onComplete(@Nullable final ZipFile zipFile);
}
