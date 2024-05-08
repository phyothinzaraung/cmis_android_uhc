package com.koekoetech.clinic.helper;

import android.text.TextUtils;
import android.util.Log;

import com.koekoetech.clinic.interfaces.ClinicDataImportCallback;
import com.koekoetech.clinic.model.DisabilitySurvey;
import com.koekoetech.clinic.model.ICDChapter;
import com.koekoetech.clinic.model.ICDChapterEntity;
import com.koekoetech.clinic.model.ICDTermGroup;
import com.koekoetech.clinic.model.ICDTermGroupEntity;
import com.koekoetech.clinic.model.StaffModel;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;

import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by ZMN on 2/13/18.
 **/

@SuppressWarnings("WeakerAccess")
public class ClinicDataImportTask extends LCAUiProgressTask<Boolean, String> {

    private static final String TAG = ClinicDataImportTask.class.getSimpleName();

    @NonNull
    private final ClinicDataImportCallback callback;
    private String dataFileName;
    private String dataFileDir;
    private String errMsg;
    private File dataZipFile;
    private File dataZipExtractedDir;

    public ClinicDataImportTask(@NonNull final LifecycleOwner lifecycleOwner, @NonNull ClinicDataImportCallback callback, String dataFileName, String dataFileDir) {
        super(lifecycleOwner);
        this.callback = callback;
        this.dataFileName = dataFileName;
        this.dataFileDir = dataFileDir;
    }

    @Override
    protected void onProgressUpdate(String progressMessage) {
        if (!TextUtils.isEmpty(progressMessage)) {
            callback.onStatusChange(progressMessage);
        }
    }

    @Override
    protected Boolean doWork() {
        try {
            File dir = new File(dataFileDir);
            dataZipFile = new File(dir, dataFileName);
            Log.d(TAG, "doInBackground: Data File : " + dataZipFile.getAbsolutePath());

            if (dataZipFile.exists()) {
                final ZipFile dataZip = new ZipFile(dataZipFile);
                if (dataZip.isValidZipFile()) {
                    dataZipExtractedDir = new File(dir, "TempExtract");
                    if (!dataZipExtractedDir.exists()) {
                        Log.d(TAG, "doInBackground: Temp Dir Creation " + dataZipExtractedDir.mkdirs());
                    }
                    dataZip.extractAll(dataZipExtractedDir.getAbsolutePath());
                    Log.d(TAG, "doInBackground: Zip File Extracted");

                    if (validateFileSchema(dataZipExtractedDir)) {
                        Log.d(TAG, "doInBackground: File Schema is Valid");
                        Realm.getDefaultInstance().executeTransaction(realm -> {

                            // Clean Up if there's incomplete imports
                            realm.delete(StaffModel.class);
                            realm.delete(SuggestionWordModel.class);
                            realm.delete(UhcPatient.class);
                            realm.delete(UhcPatientProgress.class);
                            realm.delete(UhcPatientProgressNote.class);
                            realm.delete(UhcPatientProgressNotePhoto.class);
                            realm.delete(ICDChapterEntity.class);
                            realm.delete(ICDTermGroupEntity.class);
                            realm.delete(DisabilitySurvey.class);

                            // Import Doctors list
                            publishProgress("Importing Doctors List");
                            File doctorDir = new File(dataZipExtractedDir, "doctor");
                            Log.d(TAG, doctorDir.getAbsolutePath());
                            final File[] doctorFiles = doctorDir.listFiles();
                            if (doctorFiles != null && doctorFiles.length > 0) {
                                for (File docFile : doctorFiles) {
                                    new JsonStreamReader<StaffModel>(docFile, StaffModel.class) {

                                        @Override
                                        void onEachRead(StaffModel data) {
                                            Log.d(TAG, "onEachRead() called with: data = [" + data.toString() + "]");
                                            realm.copyToRealm(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Suggestions
                            publishProgress("Importing Suggestions");
                            File wordsDir = new File(dataZipExtractedDir, "word");
                            final File[] wordFiles = wordsDir.listFiles();
                            if (wordFiles != null && wordFiles.length > 0) {
                                for (File word : wordFiles) {
                                    new JsonStreamReader<SuggestionWordModel>(word, SuggestionWordModel.class) {

                                        @Override
                                        void onEachRead(SuggestionWordModel data) {
                                            data.setLocalID(UUID.randomUUID().toString());
                                            data.setOnline(true);
                                            data.setHasSynced(true);
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Patients List
                            publishProgress("Importing Patients List");
                            File patientDir = new File(dataZipExtractedDir, "patient");
                            final File[] patientFiles = patientDir.listFiles();
                            if (patientFiles != null && patientFiles.length > 0) {
                                for (File pFile : patientFiles) {
                                    new JsonStreamReader<UhcPatient>(pFile, UhcPatient.class) {

                                        @Override
                                        void onEachRead(UhcPatient data) {
                                            data.setLocalId(UUID.randomUUID().toString());
                                            data.setHasSynced(true);
                                            data.setOnline(true);
                                            final String createdTime = data.getCreatedTime();
                                            if (!TextUtils.isEmpty(createdTime)) {
                                                data.setCreatedTime(createdTime.replace("T", " "));
                                            }
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Progress
                            publishProgress("Importing Patients' Progresses");
                            File progressDir = new File(dataZipExtractedDir, "progress");
                            final File[] progressFiles = progressDir.listFiles();
                            if (progressFiles != null && progressFiles.length > 0) {
                                for (File progress : progressFiles) {
                                    new JsonStreamReader<UhcPatientProgress>(progress, UhcPatientProgress.class) {

                                        @Override
                                        void onEachRead(UhcPatientProgress data) {
                                            data.setLocalId(UUID.randomUUID().toString());
                                            data.setOnline(true);
                                            final String createdTime = data.getCreatedTime();
                                            if (!TextUtils.isEmpty(createdTime)) {
                                                data.setCreatedTime(createdTime.replace("T", " "));
                                            }
                                            final String visitDate = data.getVisitDate();
                                            if (!TextUtils.isEmpty(visitDate)) {
                                                data.setVisitDate(visitDate.replace("T", " "));
                                            }
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Progress Notes
                            publishProgress("Importing Patients' Progress Notes");
                            File progressNoteDir = new File(dataZipExtractedDir, "progress_note");
                            final File[] progressNoteFiles = progressNoteDir.listFiles();
                            if (progressNoteFiles != null && progressNoteFiles.length > 0) {
                                for (File pnf : progressNoteFiles) {
                                    new JsonStreamReader<UhcPatientProgressNote>(pnf, UhcPatientProgressNote.class) {

                                        @Override
                                        void onEachRead(UhcPatientProgressNote data) {
                                            data.setLocalId(UUID.randomUUID().toString());
                                            data.setOnline(true);
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Progress Note Photos
                            publishProgress("Importing Progress Note Photos");
                            File progressNotePhotoDir = new File(dataZipExtractedDir, "photo");
                            final File[] progressNotePhotoFiles = progressNotePhotoDir.listFiles();
                            if (progressNotePhotoFiles != null && progressNotePhotoFiles.length > 0) {
                                for (File pnpf : progressNotePhotoFiles) {
                                    new JsonStreamReader<UhcPatientProgressNotePhoto>(pnpf, UhcPatientProgressNotePhoto.class) {

                                        @Override
                                        void onEachRead(UhcPatientProgressNotePhoto data) {
                                            data.setLocalId(UUID.randomUUID().toString());
                                            data.setOnline(true);
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                            // Import Progress Note Photos
                            publishProgress("Importing ICD Terms");
                            File icdTermsDir = new File(dataZipExtractedDir, "icd10");
                            final File[] icdTermFiles = icdTermsDir.listFiles();
                            if (icdTermFiles != null && icdTermFiles.length > 0) {
                                for (File icdTermFile : icdTermFiles) {
                                    new JsonStreamReader<ICDChapter>(icdTermFile, ICDChapter.class) {

                                        @Override
                                        void onEachRead(ICDChapter chapter) {

                                            ICDChapterEntity chapterEntity = new ICDChapterEntity();
                                            chapterEntity.setChapterId(chapter.getChapterId());
                                            chapterEntity.setTitle(chapter.getTitle());

                                            List<ICDTermGroup> termsGroup = chapter.getTermsGroup();
                                            if (termsGroup != null) {
                                                RealmList<ICDTermGroupEntity> termGroupEntities = new RealmList<>();

                                                for (ICDTermGroup group : termsGroup) {

                                                    ICDTermGroupEntity termGroupEntity = new ICDTermGroupEntity();
                                                    termGroupEntity.setTermGroupId(group.getTermGroupId());
                                                    termGroupEntity.setTitle(group.getTitle());

                                                    List<String> termsList = group.getTerms();
                                                    if (termsList != null) {
                                                        RealmList<String> termsEntitiesList = new RealmList<>();
                                                        termsEntitiesList.addAll(termsList);
                                                        termGroupEntity.setTerms(termsEntitiesList);
                                                    }

                                                    termGroupEntities.add(termGroupEntity);
                                                }

                                                chapterEntity.setTermsGroup(termGroupEntities);

                                            }
                                            realm.copyToRealmOrUpdate(chapterEntity);
                                        }
                                    }.read();
                                }
                            }

                            // Import Disability Surveys
                            publishProgress("Importing Disability Survey");
                            File disabilitySurveysDir = new File(dataZipExtractedDir, "disability_survey");
                            final File[] disabilitySurveyFiles = disabilitySurveysDir.listFiles();
                            if (disabilitySurveyFiles != null && disabilitySurveyFiles.length > 0) {
                                for (File disabilitySurveyFile : disabilitySurveyFiles) {
                                    new JsonStreamReader<DisabilitySurvey>(disabilitySurveyFile, DisabilitySurvey.class) {

                                        @Override
                                        void onEachRead(DisabilitySurvey data) {
                                            data.setLocalId(UUID.randomUUID().toString());
                                            realm.copyToRealmOrUpdate(data);
                                        }
                                    }.read();
                                }
                            }

                        });

                    } else {
                        errMsg = "Incomplete data! Some data are missing! Please retry or contact authorized person!";
                        return false;
                    }

                } else {
                    errMsg = "Invalid Data File";
                    return false;
                }

            } else {
                errMsg = "Data file not found";
                return false;
            }

            return true;
        } catch (Exception e) {
            errMsg = "Something went wrong while importing data";
            return false;
        } finally {
            try {
                publishProgress("Cleaning Up");
                if (dataZipFile != null && dataZipFile.exists()) {
                    FileHelper.delete(dataZipFile);
                }
                if (dataZipExtractedDir != null && dataZipExtractedDir.exists()) {
                    FileHelper.delete(dataZipExtractedDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void thenDoUiRelatedWork(Boolean isSuccess) {
        if (isSuccess != null && isSuccess) {
            callback.onComplete();
        } else {
            callback.onError(errMsg);
        }
    }

    private boolean validateFileSchema(File clinicDataFile) {
        if (clinicDataFile == null || !clinicDataFile.exists() || !clinicDataFile.isDirectory()) {
            return false;
        } else {
            File doctorDir = new File(clinicDataFile, "doctor");
            File patientDir = new File(clinicDataFile, "patient");
            File photoDir = new File(clinicDataFile, "photo");
            File progressDir = new File(clinicDataFile, "progress");
            File progressNoteDir = new File(clinicDataFile, "progress_note");
            File wordDir = new File(clinicDataFile, "word");

            return checkDir(doctorDir) && checkDir(patientDir) && checkDir(photoDir) && checkDir(progressDir) && checkDir(progressNoteDir) && checkDir(wordDir);

        }
    }

    private boolean checkDir(File dir) {
        if (dir != null && dir.isDirectory() && dir.exists()) {
            final File[] files = dir.listFiles();
            return files != null && files.length > 0;
        }

        return false;
    }

}
