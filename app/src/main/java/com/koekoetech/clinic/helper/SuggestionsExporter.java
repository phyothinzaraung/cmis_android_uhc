package com.koekoetech.clinic.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.koekoetech.clinic.interfaces.DataImportExportCallBack;
import com.koekoetech.clinic.model.SuggestionWordModel;

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
 * Created by ZMN on 1/22/18.
 **/

public class SuggestionsExporter {

    private static final String TAG = SuggestionsExporter.class.getSimpleName();

    private Context context;
    private DataImportExportCallBack callBack;

    public SuggestionsExporter(DataImportExportCallBack callBack) {
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
            String fileName = sdf.format(now) + "_suggestions_export.json";
            File jsonOutputFile = new File(jsonOutputDir, fileName);
            Needle.onBackgroundThread().execute(new SuggestionsExportTask(lifecycleOwner, callBack, jsonOutputFile));
        }

    }

    private static class SuggestionsExportTask extends LCAUiTask<Boolean> {

        private DataImportExportCallBack dataImportExportCallBack;
        private File jsonFile;

        SuggestionsExportTask(@NonNull final LifecycleOwner lifecycleOwner, DataImportExportCallBack dataImportExportCallBack, File jsonFile) {
            super(lifecycleOwner);
            this.dataImportExportCallBack = dataImportExportCallBack;
            this.jsonFile = jsonFile;
        }

        @Override
        protected Boolean doWork() {
            try (Realm realm = Realm.getDefaultInstance()) {
                @NonNull final List<SuggestionWordModel> suggestionsToUpload = RealmAccessHelper.getSuggestionsToUpload(realm);
                Gson gson = ServiceHelper.getGson();

                String jsonString = gson.toJson(suggestionsToUpload);

                Log.d(TAG, "doInBackground: Delete Existing File : " + jsonFile.delete());

                boolean isCreated = jsonFile.createNewFile();
                if (isCreated) {
                    FileOutputStream fos = new FileOutputStream(jsonFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(jsonString);
                    osw.close();
                    fos.flush();
                    fos.close();
                    Log.d(TAG, "doInBackground: Suggestions Exported at " + jsonFile.getAbsolutePath());
                    FileHelper.compressFile(jsonFile, CMISConstant.DATA_EXPORT_ZIP_PASSWORD, true);
                } else {
                    Log.d(TAG, "doInBackground: Failed To Create Suggestions Export File");
                }

                return true;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
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
