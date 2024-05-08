package com.koekoetech.clinic.helper;

import android.text.TextUtils;
import android.util.Log;

import com.koekoetech.clinic.model.DisabilitySurvey;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;
import com.koekoetech.clinic.model.UhcPatientProgressViewModel;
import com.koekoetech.clinic.model.UhcPatientViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ZMN on 01/10/2020.
 **/
public class RealmAccessHelper {
    private static final String TAG = "RealmAccessHelper";

    //region Deletion Methods

    /**
     * Hard deletion of {@link DisabilitySurvey} Records of a specific patient
     *
     * @param realm       Realm instance
     * @param patientCode Patient Code
     */
    public static void deleteDisabilitySurveys(
            @NonNull final Realm realm,
            @NonNull final String patientCode
    ) {
        Log.d(TAG, "deleteDisabilitySurveys() called with: patientCode = [" + patientCode + "]");
        boolean isDeleted = false;
        try {
            final RealmResults<DisabilitySurvey> disabilitySurveys = realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", patientCode)
                    .findAll();
            Log.d(TAG, "deleteDisabilitySurveys: Deleting " + disabilitySurveys.size() + " DisabilitySurveys");
            isDeleted = disabilitySurveys.deleteAllFromRealm();
        } catch (Exception e) {
            Log.e(TAG, "deleteDisabilitySurveys: ", e);
        }
        Log.d(TAG, "deleteDisabilitySurveys() returned: " + isDeleted);
    }

    /**
     * Delete all ProgressNotePhotos of a specific ProgressNote
     *
     * @param realm            Realm instance
     * @param progressNoteCode Progress Note Code
     * @param isHardDelete     Should perform hard delete?
     */
    public static void deleteProgressNotePhotosByProgressNoteCode(
            @NonNull final Realm realm,
            @NonNull final String progressNoteCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgressNotePhotos() called with: progressNoteCode = [" + progressNoteCode + "], isHardDelete = [" + isHardDelete + "]");
        try {
            RealmResults<UhcPatientProgressNotePhoto> progressNotePhotos = realm
                    .where(UhcPatientProgressNotePhoto.class)
                    .equalTo("progressNoteCode", progressNoteCode)
                    .findAll();
            final int totalCountToDelete = progressNotePhotos.size();
            int deletedCount = 0;

            for (UhcPatientProgressNotePhoto progressNotePhoto : progressNotePhotos) {
                final boolean isDeleted = deleteProgressNotePhoto(realm, progressNotePhoto, isHardDelete);
                if (isDeleted) {
                    deletedCount += 1;
                }
            }

            Log.d(TAG, "deleteProgressNotePhotos: Deleted " + deletedCount + " ProgressNotePhotos of " + totalCountToDelete);

        } catch (Exception e) {
            Log.e(TAG, "deleteProgressNotePhotos: ", e);
        }
    }

    private static boolean deleteProgressNotePhoto(
            @NonNull final Realm realm,
            @Nullable final UhcPatientProgressNotePhoto progressNotePhoto,
            final boolean isHardDelete
    ) {
        try {
            final List<String> imagesToDelete = new ArrayList<>();

            if (progressNotePhoto == null || !progressNotePhoto.isValid()) {
                return false;
            }

            if (isHardDelete || !progressNotePhoto.isOnline()) {
                @Nullable final String photo = progressNotePhoto.getPhoto();
                progressNotePhoto.deleteFromRealm();
                if (!TextUtils.isEmpty(photo)) {
                    imagesToDelete.add(photo);
                }
            } else {
                progressNotePhoto.setDeleted(true);
                progressNotePhoto.setHasChanges(true);
                realm.copyToRealmOrUpdate(progressNotePhoto);
            }

            if (!imagesToDelete.isEmpty()) {
                for (String imagePath : imagesToDelete) {
                    if (!TextUtils.isEmpty(imagePath)) {
                        final File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            final boolean isDeleted = imageFile.delete();
                            Log.d(TAG, "deleteProgressNotePhotos: Deleted File at " + imagePath + " : " + isDeleted);
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "deleteProgressNotePhoto: ", e);
            return false;
        }
    }

    /**
     * Delete all ProgressNotes of a specific Progress
     *
     * @param realm        Realm instance
     * @param progressCode Progress Code
     * @param isHardDelete Should perform hard delete?
     */
    public static void deleteProgressNotesByProgressCode(
            @NonNull final Realm realm,
            @NonNull final String progressCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgressNotes() called with: progressCode = [" + progressCode + "], isHardDelete = [" + isHardDelete + "]");

        try {
            RealmResults<UhcPatientProgressNote> progressNotes = realm
                    .where(UhcPatientProgressNote.class)
                    .equalTo("progressCode", progressCode)
                    .findAll();

            final int totalCountToDelete = progressNotes.size();
            int deletedCount = 0;

            for (UhcPatientProgressNote progressNote : progressNotes) {
                final boolean isDeleted = deleteProgressNote(realm, progressNote, isHardDelete);
                if (isDeleted) {
                    deletedCount += 1;
                }
            }

            Log.d(TAG, "deleteProgressNotes: Deleted " + deletedCount + " ProgressNotes of " + totalCountToDelete);

        } catch (Exception e) {
            Log.e(TAG, "deleteProgressNotes: ", e);
        }
    }

    public static void deleteProgressNoteByProgressNoteCode(
            @NonNull final Realm realm,
            @NonNull final String progressNoteCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgressNotesByProgressNoteCode() called with: progressNoteCode = [" + progressNoteCode + "], isHardDelete = [" + isHardDelete + "]");

        final UhcPatientProgressNote progressNote = realm.where(UhcPatientProgressNote.class)
                .equalTo("progressNoteCode", progressNoteCode)
                .findFirst();
        deleteProgressNote(realm, progressNote, isHardDelete);
    }

    private static boolean deleteProgressNote(
            @NonNull final Realm realm,
            @Nullable final UhcPatientProgressNote progressNote,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgressNote() called with: progressNote = [" + progressNote + "], isHardDelete = [" + isHardDelete + "]");

        try {
            if (progressNote == null || !progressNote.isValid()) {
                return false;
            }
            @Nullable final String progressNoteCode = progressNote.getProgressNoteCode();
            if (isHardDelete || !progressNote.isOnline()) {
                progressNote.deleteFromRealm();
            } else {
                progressNote.setDeleted(true);
                progressNote.setHasChanges(true);
                realm.copyToRealmOrUpdate(progressNote);
            }
            if (!TextUtils.isEmpty(progressNoteCode)) {
                deleteProgressNotePhotosByProgressNoteCode(realm, progressNoteCode, isHardDelete);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "deleteProgressNote: ", e);
            return false;
        }
    }

    /**
     * Delete all Progresses of a specific Patient
     *
     * @param realm        Realm instance
     * @param patientCode  Patient Code
     * @param isHardDelete Should perform hard delete?
     */
    public static void deleteProgresses(
            @NonNull final Realm realm,
            @NonNull final String patientCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgresses() called with: patientCode = [" + patientCode + "], isHardDelete = [" + isHardDelete + "]");

        try {
            RealmResults<UhcPatientProgress> progresses = realm.
                    where(UhcPatientProgress.class)
                    .equalTo("patientCode", patientCode)
                    .findAll();

            final int totalCountToDelete = progresses.size();
            int deletedCount = 0;

            for (UhcPatientProgress progress : progresses) {
                final boolean isDeleted = deleteProgress(realm, progress, isHardDelete);
                if (isDeleted) {
                    deletedCount += 1;
                }
            }

            Log.d(TAG, "deleteProgress: Deleted " + deletedCount + " Progresses of " + totalCountToDelete);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a specific Progress Record along with related ProgressNotes & ProgressNotePhotos.
     * Also marks, related patient record as changed.
     *
     * @param realm        Realm instance
     * @param progressCode Progress Code
     */
    public static void deleteByProgressCode(
            @NonNull final Realm realm,
            @NonNull final String progressCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteByProgressCode() called with: progressCode = [" + progressCode + "], isHardDelete = [" + isHardDelete + "]");

        try {
            UhcPatientProgress progress = realm.where(UhcPatientProgress.class).equalTo("progressCode", progressCode).findFirst();
            final boolean isDeleted = deleteProgress(realm, progress, isHardDelete);
            Log.d(TAG, "deleteByProgressCode: Progress deleted : " + isDeleted);
        } catch (Exception e) {
            Log.e(TAG, "deleteByProgressCode: ", e);
        }

    }

    private static boolean deleteProgress(
            @NonNull final Realm realm,
            @Nullable final UhcPatientProgress progress,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deleteProgress() called with: progress = [" + progress + "], isHardDelete = [" + isHardDelete + "]");
        try {
            if (progress == null || !progress.isValid()) {
                return false;
            }
            final String progressCode = progress.getProgressCode();
            if (isHardDelete || !progress.isOnline()) {
                progress.deleteFromRealm();
            } else {
                progress.setDeleted(true);
                progress.setHasChanges(true);
                realm.copyToRealmOrUpdate(progress);
            }
            if (!TextUtils.isEmpty(progressCode)) {
                deleteProgressNotesByProgressCode(realm, progressCode, isHardDelete);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "deleteProgress: ", e);
            return false;
        }
    }

    /**
     * Delete a specific patient all related information
     *
     * @param realm        Realm instance
     * @param patientCode  Patient Code
     * @param isHardDelete Should perform hard delete?
     */
    public static void deletePatient(
            @NonNull final Realm realm,
            @NonNull final String patientCode,
            final boolean isHardDelete
    ) {
        Log.d(TAG, "deletePatient() called with: patientCode = [" + patientCode + "], isHardDelete = [" + isHardDelete + "]");

        try {
            final UhcPatient patient = realm
                    .where(UhcPatient.class)
                    .equalTo("patientCode", patientCode)
                    .findFirst();
            if (patient == null || !patient.isValid()) {
                return;
            }

            if (isHardDelete || !patient.isOnline()) {
                patient.deleteFromRealm();
                deleteDisabilitySurveys(realm, patientCode);
            } else {
                patient.setDeleted(true);
                patient.setHasSynced(false);
                realm.copyToRealmOrUpdate(patient);
            }

            deleteProgresses(realm, patientCode, isHardDelete);


        } catch (Exception e) {
            Log.e(TAG, "deletePatient: ", e);
        }

    }

    //endregion

    public static void markPatientChanged(@NonNull final Realm realm, @NonNull final String patientCode) {
        Log.d(TAG, "markPatientChanged() called with: patientCode = [" + patientCode + "]");
        try {
            final UhcPatient patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();
            if (patient == null || !patient.isValid()) {
                return;
            }
            patient.setHasSynced(false);
            realm.copyToRealmOrUpdate(patient);
        } catch (Exception e) {
            Log.e(TAG, "markPatientChanged: ", e);
        }

    }

    @NonNull
    public static List<UhcPatientViewModel> getPatientsToUpload(@NonNull final Realm realm) {
        final List<UhcPatientViewModel> patientViewModels = new ArrayList<>();

        if (realm.isClosed()) {
            return patientViewModels;
        }

        final RealmResults<UhcPatient> patients = realm.where(UhcPatient.class)
                .equalTo("hasSynced", false)
                .findAll();

        for (UhcPatient patient : patients) {
            final UhcPatientViewModel patientViewModel = new UhcPatientViewModel();

            // Set Patient
            final UhcPatient unManagedPatient = realm.copyFromRealm(patient);
            if (unManagedPatient.isDeleted()) {
                patientViewModel.setPatient(unManagedPatient);
                patientViewModels.add(patientViewModel);
                continue;
            }
            @Nullable final String localProfileImagePath = unManagedPatient.getLocal_filepath();
            if (!TextUtils.isEmpty(localProfileImagePath)) {
                final File profileImageFile = new File(localProfileImagePath);
                final String profileBase64 = Base64Helper.fileToBase64(profileImageFile);
                if (!TextUtils.isEmpty(profileBase64)) {
                    unManagedPatient.setPhotoUrl(profileBase64);
                }
            }
            patientViewModel.setPatient(unManagedPatient);


            //region Set Disability Surveys
            final RealmResults<DisabilitySurvey> disabilitySurveys = realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", patient.getPatientCode())
                    .findAll();
            patientViewModel.setDisabilitySurveys(new ArrayList<>(realm.copyFromRealm(disabilitySurveys)));
            //endregion

            final ArrayList<UhcPatientProgressViewModel> progressViewModels = new ArrayList<>();
            RealmResults<UhcPatientProgress> patientProgresses = realm.where(UhcPatientProgress.class)
                    .equalTo("patientCode", patient.getPatientCode())
                    .equalTo("hasChanges", true)
                    .findAll();

            for (UhcPatientProgress progress : patientProgresses) {
                final UhcPatientProgressViewModel progressViewModel = new UhcPatientProgressViewModel();

                // Set Progress
                final UhcPatientProgress unManagedProgress = realm.copyFromRealm(progress);
                progressViewModel.setProgress(unManagedProgress);

                if (unManagedProgress.isDeleted()) {
                    progressViewModels.add(progressViewModel);
                    continue;
                }

                //region Set ProgressNote List
                RealmResults<UhcPatientProgressNote> patientProgressNotes = realm.where(UhcPatientProgressNote.class)
                        .equalTo("progressCode", progress.getProgressCode())
                        .equalTo("hasChanges", true)
                        .findAll();

                final ArrayList<UhcPatientProgressNote> unManagedProgressNotes = new ArrayList<>();
                final ArrayList<UhcPatientProgressNotePhoto> unManagedProgressNotePhotos = new ArrayList<>();

                for (UhcPatientProgressNote progressNote : patientProgressNotes) {
                    final UhcPatientProgressNote unManagedProgressNote = realm.copyFromRealm(progressNote);
                    unManagedProgressNotes.add(unManagedProgressNote);
                    if (unManagedProgressNote.isDeleted()) {
                        continue;
                    }

                    // Set ProgressNotePhotosList
                    RealmResults<UhcPatientProgressNotePhoto> progressNotePhotos = realm.where(UhcPatientProgressNotePhoto.class)
                            .equalTo("progressNoteCode", progressNote.getProgressNoteCode())
                            .equalTo("hasChanges", true)
                            .findAll();

                    for (UhcPatientProgressNotePhoto progressNotePhoto : progressNotePhotos) {
                        final UhcPatientProgressNotePhoto unManagedProgressNotePhoto = realm.copyFromRealm(progressNotePhoto);
                        if (unManagedProgressNotePhoto.isDeleted()) {
                            unManagedProgressNotePhotos.add(unManagedProgressNotePhoto);
                            continue;
                        }
                        @Nullable final String photoPath = unManagedProgressNotePhoto.getPhoto();
                        if (!TextUtils.isEmpty(photoPath)) {
                            final File photo = new File(photoPath);
                            final String photoBase64 = Base64Helper.fileToBase64(photo);
                            if (!TextUtils.isEmpty(photoBase64)) {
                                unManagedProgressNotePhoto.setPhoto(photoBase64);
                            }
                        }
                        unManagedProgressNotePhotos.add(unManagedProgressNotePhoto);
                    }


                }

                progressViewModel.setProgressNotes(unManagedProgressNotes);
                progressViewModel.setProgressNotePhotos(unManagedProgressNotePhotos);
                progressViewModels.add(progressViewModel);
            }

            patientViewModel.setProgresses(progressViewModels);
            patientViewModels.add(patientViewModel);

        }


        /*//region Collect Data
        RealmResults<UhcPatient> patients = realm.where(UhcPatient.class)
                .equalTo("hasSynced", false)
                .findAll();

        for (UhcPatient patient : patients) {
            final UhcPatientViewModel patientViewModel = new UhcPatientViewModel();

            // Set Patient
            patientViewModel.setPatient(realm.copyFromRealm(patient));

            //region Set Disability Surveys
            final RealmResults<DisabilitySurvey> disabilitySurveys = realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", patient.getPatientCode())
                    .findAll();
            patientViewModel.setDisabilitySurveys(new ArrayList<>(realm.copyFromRealm(disabilitySurveys)));
            //endregion

            //region Set UhcPatientProgressViewModel List
            final ArrayList<UhcPatientProgressViewModel> patientProgressViewModels = new ArrayList<>();
            RealmResults<UhcPatientProgress> patientProgresses = realm.where(UhcPatientProgress.class)
                    .equalTo("patientCode", patient.getPatientCode())
                    .equalTo("hasChanges", true)
                    .findAll();

            for (UhcPatientProgress patientProgress : patientProgresses) {
                final UhcPatientProgressViewModel patientProgressViewModel = new UhcPatientProgressViewModel();
                // Set Progress
                patientProgressViewModel.setProgress(realm.copyFromRealm(patientProgress));

                //region Set ProgressNote List
                RealmResults<UhcPatientProgressNote> patientProgressNotes = realm.where(UhcPatientProgressNote.class)
                        .equalTo("progressCode", patientProgress.getProgressCode())
                        .equalTo("hasChanges", true)
                        .findAll();
                final List<UhcPatientProgressNote> unManagedProgressNotes = realm.copyFromRealm(patientProgressNotes);
                patientProgressViewModel.setProgressNotes(new ArrayList<>(unManagedProgressNotes));
                //endregion

                //region Set ProgressNotePhotosList
                final List<String> progressNoteCodeList = new ArrayList<>();
                for (UhcPatientProgressNote unManagedProgressNote : unManagedProgressNotes) {
                    final String progressNoteCode = unManagedProgressNote.getProgressNoteCode();
                    if (!TextUtils.isEmpty(progressNoteCode)) {
                        progressNoteCodeList.add(progressNoteCode);
                    }
                }

                final String[] progressNoteCodes = progressNoteCodeList.toArray(new String[0]);
                RealmResults<UhcPatientProgressNotePhoto> patientProgressNotePhotos = realm
                        .where(UhcPatientProgressNotePhoto.class)
                        .equalTo("hasChanges", true)
                        .in("progressNoteCode", progressNoteCodes)
                        .findAll();
                final List<UhcPatientProgressNotePhoto> unManagedProgressNotePhotos = realm.copyFromRealm(patientProgressNotePhotos);
                patientProgressViewModel.setProgressNotePhotos(new ArrayList<>(unManagedProgressNotePhotos));
                //endregion

                patientProgressViewModels.add(patientProgressViewModel);

            }

            patientViewModel.setProgresses(patientProgressViewModels);
            //endregion

        }
        //endregion

        for (UhcPatientViewModel patientViewModel : patientViewModels) {
            final ArrayList<String> imagesToDelete = new ArrayList<>();

            final UhcPatient patient = patientViewModel.getPatient();

            @Nullable final String localProfileImagePath = patient.getLocal_filepath();
            if (!TextUtils.isEmpty(localProfileImagePath)) {
                final File profileImageFile = new File(localProfileImagePath);
                final String profileBase64 = Base64Helper.fileToBase64(profileImageFile);
                if (!TextUtils.isEmpty(profileBase64)) {
                    patient.setPhotoUrl(profileBase64);
                    imagesToDelete.add(localProfileImagePath);
                }
            }
            patientViewModel.setPatient(patient);

            for (UhcPatientProgressViewModel progressViewModel : patientViewModel.getProgresses()) {
                for (UhcPatientProgressNotePhoto progressNotePhoto : progressViewModel.getProgressNotePhotos()) {
                    @Nullable final String photoPath = progressNotePhoto.getPhoto();
                    if (!TextUtils.isEmpty(photoPath)) {
                        final File photo = new File(photoPath);
                        final String photoBase64 = Base64Helper.fileToBase64(photo);
                        if (!TextUtils.isEmpty(photoBase64)) {
                            progressNotePhoto.setPhoto(photoBase64);
                            imagesToDelete.add(photoPath);
                        }
                    }
                }
                if (progressViewModel.getProgress().isDeleted()) {
                    progressViewModel.setProgressNotes(new ArrayList<>());
                    progressViewModel.setProgressNotePhotos(new ArrayList<>());
                }
            }

            if (patient.isDeleted()) {
                patientViewModel.setProgresses(new ArrayList<>());
                patientViewModel.setDisabilitySurveys(new ArrayList<>());
            }

            patientViewModel.setLocalImages(imagesToDelete);

        }*/

        return patientViewModels;
    }

    @NonNull
    public static List<SuggestionWordModel> getSuggestionsToUpload(@NonNull final Realm realm) {
        Log.d(TAG, "RealmAccessHelper:getSuggestionsToUpload: called");
        final List<SuggestionWordModel> suggestionWordModelList = new ArrayList<>();

        try {
            RealmResults<SuggestionWordModel> suggestions = realm.where(SuggestionWordModel.class)
                    .notEqualTo("staffId", 0)
                    .and()
                    .equalTo("hasSynced", false)
                    .findAll();
            if (suggestions.isValid()) {
                suggestionWordModelList.addAll(realm.copyFromRealm(suggestions));
            }
        } catch (Exception e) {
            Log.e(TAG, "getSuggestionsToUpload: ", e);
        }

        return suggestionWordModelList;
    }

    public static boolean swapPatientViewModel(
            @NonNull final Realm realm,
            @NonNull final UhcPatientViewModel oldPVM,
            @NonNull final UhcPatientViewModel newPVM
    ) {
        try {
            @Nullable final UhcPatient newPatient = newPVM.getPatient();
            @Nullable final UhcPatient oldPatient = oldPVM.getPatient();

            if (newPatient == null || oldPatient == null) {
                return false;
            }

            final String newPatientCode = newPatient.getPatientCode();
            final String oldPatientCode = oldPatient.getPatientCode();

            if (TextUtils.isEmpty(newPatientCode) || TextUtils.isEmpty(oldPatientCode)) {
                return false;
            }

            if (newPatient.isDeleted()) {
                deletePatient(realm, oldPatientCode, true);
                return true;
            }

            //region Update Patient
            final String oldPatientLocalId = oldPatient.getLocalId();
            if (TextUtils.isEmpty(oldPatientLocalId)) {
                return false;
            }

            newPatient.setLocalId(oldPatientLocalId);
            newPatient.setHasSynced(true);
            newPatient.setOnline(true);
            realm.copyToRealmOrUpdate(newPatient);
            //endregion

            //region Clean Up Old Disability Surveys
            realm.where(DisabilitySurvey.class)
                    .equalTo("patientCode", oldPatientCode)
                    .findAll().deleteAllFromRealm();
            //endregion

            //region Insert new Disability Surveys
            final ArrayList<DisabilitySurvey> disabilitySurveys = newPVM.getDisabilitySurveys();
            if (disabilitySurveys != null && !disabilitySurveys.isEmpty()) {
                realm.copyToRealmOrUpdate(disabilitySurveys);
            }
            //endregion

            //region Clean Up Old Patient Progress Records
            @Nullable final ArrayList<UhcPatientProgressViewModel> oldProgressViewModels = oldPVM.getProgresses();
            if (oldProgressViewModels != null) {
                @NonNull final List<String> progressListToDelete = new ArrayList<>();
                @NonNull final List<String> progressNoteListToDelete = new ArrayList<>();
                @NonNull final List<String> progressNotePhotoListToDelete = new ArrayList<>();

                for (UhcPatientProgressViewModel oldProgressViewModel : oldProgressViewModels) {
                    if (oldProgressViewModel == null) continue;

                    final UhcPatientProgress progress = oldProgressViewModel.getProgress();
                    if (progress != null) {
                        final String progressCode = progress.getProgressCode();
                        if (!TextUtils.isEmpty(progressCode)) {
                            if (progress.isDeleted()) {
                                deleteByProgressCode(realm, progressCode, true);
                            } else {
                                progressListToDelete.add(progressCode);
                            }
                        }
                    }

                    @Nullable final ArrayList<UhcPatientProgressNote> progressNotesList = oldProgressViewModel.getProgressNotes();
                    if (progressNotesList != null) {
                        for (UhcPatientProgressNote progressNote : progressNotesList) {
                            if (progressNote == null) continue;
                            final String progressNoteCode = progressNote.getProgressNoteCode();
                            if (!TextUtils.isEmpty(progressNoteCode)) {
                                if (progressNote.isDeleted()) {
                                    deleteProgressNoteByProgressNoteCode(realm, progressNoteCode, true);
                                } else {
                                    progressNoteListToDelete.add(progressNoteCode);
                                }
                            }
                        }
                    }

                    @Nullable final ArrayList<UhcPatientProgressNotePhoto> progressNotePhotos = oldProgressViewModel.getProgressNotePhotos();
                    if (progressNotePhotos != null) {
                        for (UhcPatientProgressNotePhoto progressNotePhoto : progressNotePhotos) {
                            if (progressNotePhoto == null) continue;
                            final String progressNoteCode = progressNotePhoto.getProgressNoteCode();
                            if (!TextUtils.isEmpty(progressNoteCode)) {
                                if (progressNotePhoto.isDeleted()) {
                                    deleteProgressNoteByProgressNoteCode(realm, progressNoteCode, true);
                                } else {
                                    progressNotePhotoListToDelete.add(progressNoteCode);
                                }
                            }
                        }
                    }
                }

                if (!progressListToDelete.isEmpty()) {
                    final String[] progressCodes = progressListToDelete.toArray(new String[0]);
                    realm.where(UhcPatientProgress.class)
                            .in("progressCode", progressCodes)
                            .findAll()
                            .deleteAllFromRealm();
                }

                if (!progressNoteListToDelete.isEmpty()) {
                    final String[] progressNoteCodes = progressNoteListToDelete.toArray(new String[0]);
                    realm.where(UhcPatientProgressNote.class)
                            .in("progressNoteCode", progressNoteCodes)
                            .findAll()
                            .deleteAllFromRealm();
                }

                if (!progressNotePhotoListToDelete.isEmpty()) {
                    final String[] progressNoteCodes = progressNotePhotoListToDelete.toArray(new String[0]);
                    final RealmResults<UhcPatientProgressNotePhoto> progressNotePhotos = realm
                            .where(UhcPatientProgressNotePhoto.class)
                            .in("progressNoteCode", progressNoteCodes)
                            .findAll();

                    final List<File> imageFilesToDelete = new ArrayList<>();
                    for (UhcPatientProgressNotePhoto progressNotePhoto : progressNotePhotos) {
                        @Nullable final String photo = progressNotePhoto.getPhoto();
                        if (!TextUtils.isEmpty(photo)) {
                            imageFilesToDelete.add(new File(photo));
                        }
                    }

                    if (progressNotePhotos.deleteAllFromRealm()) {
                        if (!imageFilesToDelete.isEmpty()) {
                            for (File file : imageFilesToDelete) {
                                FileHelper.delete(file);
                            }
                        }
                    }

                }
            }
            //endregion

            //region Insert New Patient Progress Records
            @Nullable final ArrayList<UhcPatientProgressViewModel> newProgressViewModels = newPVM.getProgresses();
            if (newProgressViewModels != null) {

                @NonNull final List<UhcPatientProgress> progressListToSave = new ArrayList<>();
                @NonNull final List<UhcPatientProgressNote> progressNoteListToSave = new ArrayList<>();
                @NonNull final List<UhcPatientProgressNotePhoto> progressNotePhotoListToSave = new ArrayList<>();

                for (UhcPatientProgressViewModel newProgressViewModel : newProgressViewModels) {
                    if (newProgressViewModel == null) continue;

                    @Nullable final UhcPatientProgress progress = newProgressViewModel.getProgress();
                    if (progress != null && !progress.isDeleted()) {
                        progress.setLocalId(UUID.randomUUID().toString());
                        progress.setOnline(true);
                        progress.setHasChanges(false);

                        final String createdTime = progress.getCreatedTime();
                        if (!TextUtils.isEmpty(createdTime)) {
                            progress.setCreatedTime(createdTime.replace("T", " "));
                        }

                        final String visitDate = progress.getVisitDate();
                        if (!TextUtils.isEmpty(visitDate)) {
                            progress.setVisitDate(visitDate.replace("T", " "));
                        }
                        progressListToSave.add(progress);
                    }

                    @Nullable final ArrayList<UhcPatientProgressNote> progressNotes = newProgressViewModel.getProgressNotes();
                    if (progressNotes != null && !progressNotes.isEmpty()) {
                        for (UhcPatientProgressNote progressNote : progressNotes) {
                            if (progressNote == null || progressNote.isDeleted()) continue;
                            progressNote.setLocalId(UUID.randomUUID().toString());
                            progressNote.setOnline(true);
                            progressNote.setHasChanges(false);
                            progressNoteListToSave.add(progressNote);
                        }
                    }

                    @Nullable final ArrayList<UhcPatientProgressNotePhoto> progressNotePhotos = newProgressViewModel.getProgressNotePhotos();
                    if (progressNotePhotos != null && !progressNotePhotos.isEmpty()) {
                        for (UhcPatientProgressNotePhoto progressNotePhoto : progressNotePhotos) {
                            if (progressNotePhoto == null || progressNotePhoto.isDeleted())
                                continue;
                            progressNotePhoto.setLocalId(UUID.randomUUID().toString());
                            progressNotePhoto.setOnline(true);
                            progressNotePhoto.setHasChanges(false);
                            progressNotePhotoListToSave.add(progressNotePhoto);
                        }
                    }

                }

                if (!progressListToSave.isEmpty()) {
                    realm.copyToRealmOrUpdate(progressListToSave);
                }

                if (!progressNoteListToSave.isEmpty()) {
                    realm.copyToRealmOrUpdate(progressNoteListToSave);
                }

                if (!progressNotePhotoListToSave.isEmpty()) {
                    realm.copyToRealmOrUpdate(progressNotePhotoListToSave);
                }
            }
            //endregion
            return true;
        } catch (Exception e) {
            Log.e(TAG, "swapPatientViewModel: ", e);
            return false;
        }
    }

}
