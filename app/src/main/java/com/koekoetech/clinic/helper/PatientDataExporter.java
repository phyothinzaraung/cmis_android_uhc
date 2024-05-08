package com.koekoetech.clinic.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.koekoetech.clinic.interfaces.DataImportExportCallBack;
import com.koekoetech.clinic.model.UhcPatientViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import io.realm.Realm;
import needle.Needle;

/**
 * Created by ZMN on 1/19/18.
 **/

public class PatientDataExporter {

    private static final String TAG = PatientDataExporter.class.getSimpleName();

    private Context context;

    private DataImportExportCallBack callBack;

    public PatientDataExporter(DataImportExportCallBack callBack) {
        this.callBack = callBack;
    }

    public void init(Context context) {
        this.context = context;
    }

    public void export(@NonNull final LifecycleOwner lifecycleOwner) {
        File jsonOutputDir = context.getExternalFilesDir(null);
        if (jsonOutputDir == null) {
            callBack.onError();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            Date now = new Date();
            String fileName = sdf.format(now) + "_patients_data_export.json";
            File jsonOutputFile = new File(jsonOutputDir, fileName);
            Needle.onBackgroundThread().execute(new DataExportTask(lifecycleOwner, callBack, jsonOutputFile));
        }

    }

    private static class DataExportTask extends LCAUiTask<Boolean> {

        private DataImportExportCallBack dataImportExportCallBack;
        private File jsonFile;

        DataExportTask(@NonNull final LifecycleOwner lifecycleOwner, DataImportExportCallBack dataImportExportCallBack, File file) {
            super(lifecycleOwner);
            this.dataImportExportCallBack = dataImportExportCallBack;
            this.jsonFile = file;
        }

        @Override
        protected Boolean doWork() {
            try (Realm realm = Realm.getDefaultInstance()) {

                @NonNull final List<UhcPatientViewModel> patientViewModels = RealmAccessHelper.getPatientsToUpload(realm);

                Gson gson = ServiceHelper.getGson();

                String jsonString = gson.toJson(patientViewModels);

                Log.d(TAG, "syncPatientsUpStream: Delete Old Log File : " + jsonFile.delete());
                boolean isCreated = jsonFile.createNewFile();
                if (isCreated) {
                    FileOutputStream fos = new FileOutputStream(jsonFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(jsonString);
                    osw.close();
                    fos.flush();
                    fos.close();
                    Log.d(TAG, "syncPatientsUpStream: File Written at : " + jsonFile.getAbsolutePath());
                    FileHelper.compressFile(jsonFile, CMISConstant.DATA_EXPORT_ZIP_PASSWORD, true);
                } else {
                    Log.d(TAG, "syncPatientsUpStream: Failed to create file.");
                }


                return true;
            } catch (Exception e) {
                Log.e(TAG, "export: Data Export Error", e);
                return false;
            }
        }

        @Override
        protected void thenDoUiRelatedWork(Boolean result) {
            if (result == null || !result) {
                dataImportExportCallBack.onError();
            } else {
                dataImportExportCallBack.onComplete();
            }
        }
    }
}
