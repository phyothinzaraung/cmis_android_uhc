package com.koekoetech.clinic.helper;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by ZMN on 3/6/18.
 **/

abstract class JsonStreamReader<D> {

    private static final String TAG = "JsonStreamReader";

    private File jsonFile;
    private Gson gson;
    private Class<D> clazz;


    JsonStreamReader(File jsonFile, Class<D> clazz) {
        Log.d(TAG, "JsonStreamReader() called with: jsonFile = [" + jsonFile.getAbsolutePath() + "]");
        this.jsonFile = jsonFile;
        this.clazz = clazz;
        gson = ServiceHelper.getGson();
    }

    void read() {
        Log.d(TAG, "read() called");
        //noinspection CharsetObjectCanBeUsed
        try (JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"))) {
            Log.d(TAG, "read: Starting Array");
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                D data = gson.fromJson(jsonReader, clazz);
                if (data != null) {
                    Log.d(TAG, data.toString());
                    onEachRead(data);
                }
            }
            jsonReader.endArray();
            Log.d(TAG, "read: Ending Array");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract void onEachRead(D data);

//    private Gson getGson() {
//        try {
//            return new GsonBuilder()
//                    .setExclusionStrategies(new ExclusionStrategy() {
//                        @Override
//                        public boolean shouldSkipField(FieldAttributes f) {
//                            return f.getDeclaringClass().equals(RealmObject.class);
//                        }
//
//                        @Override
//                        public boolean shouldSkipClass(Class<?> clazz) {
//                            return false;
//                        }
//                    })
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_StaffModelRealmProxy"), new StaffModelSerializer())
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_SuggestionWordModelRealmProxy"), new SuggestionWordModelSerializer())
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_UhcPatientRealmProxy"), new UhcPatientSerializer())
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_UhcPatientProgressRealmProxy"), new UhcPatientProgressSerializer())
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_UhcPatientProgressNoteRealmProxy"), new UhcPatientProgressNoteSerializer())
//                    .registerTypeAdapter(Class.forName("io.realm.com_koekoetech_clinic_model_UhcPatientProgressNotePhotoRealmProxy"), new UhcPatientProgressNotePhotoSerializer())
//                    .excludeFieldsWithoutExposeAnnotation()
//                    .create();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return new GsonBuilder().create();
//        }
//    }

}
