package com.koekoetech.clinic.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.BaseRecyclerViewAdapter;
import com.koekoetech.clinic.adapter.ProgressNotesAdapter;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.CodeGen;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.Pageable;
import com.koekoetech.clinic.helper.PressNoteTypeHelper;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.TimeUtils;
import com.koekoetech.clinic.interfaces.PressnoteOperationCallback;
import com.koekoetech.clinic.interfaces.ProgressCreatedTimeChangeCallback;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNotesFragmentCallBacks;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ZMN on 11/29/17.
 **/

public class UhcPatientProgressNotesFragment extends BaseFragment implements PressnoteOperationCallback, ProgressCreatedTimeChangeCallback {

    private static final String TAG = UhcPatientProgressNotesFragment.class.getSimpleName();

    private static final String MODE_CREATE = "mode_create";
    private static final String MODE_EDIT = "mode_edit";

    private RecyclerView createdPressNotesRecyclerView;

    private boolean doChangesExists;
    private String currentMode;
    private String patientCode;
    private Realm realm;
    private UhcPatient patient;
    private UhcPatientProgress progressEditSource;
    private ProgressNotesAdapter progressNotesAdapter;
    private boolean isSaveInProgress = false;
    private String ageUnit;

    private UhcPatientProgressNotesFragmentCallBacks progressNoteCallBack;

    private Calendar pickedCreatedTime;
    private String progressCreatedTime;

    // Removed ProgressNotes which reside on server
    private ArrayList<UhcPatientProgressNote> removedProgressNotes;
    // Removed ProgressNotePhotos which reside on server
    private ArrayList<UhcPatientProgressNotePhoto> removedProgressNotePhotos;
    // Removed ProgressNotes which don't reside on server
    private ArrayList<UhcPatientProgressNote> progressNotesToDelete;
    // Removed ProgressNotePhotos which don't reside on server
    private ArrayList<UhcPatientProgressNotePhoto> progressNotePhotosToDelete;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_progress_notes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {

        createdPressNotesRecyclerView = view.findViewById(R.id.rv_created_press_notes);

        removedProgressNotes = new ArrayList<>();
        removedProgressNotePhotos = new ArrayList<>();
        progressNotesToDelete = new ArrayList<>();
        progressNotePhotosToDelete = new ArrayList<>();
        pickedCreatedTime = Calendar.getInstance();

        realm = Realm.getDefaultInstance();
        progressNotesAdapter = new ProgressNotesAdapter();
        progressNotesAdapter.registerCallback(this);
        progressNotesAdapter.setCreatedTimeChangeCallback(this);
        createdPressNotesRecyclerView.setAdapter(progressNotesAdapter);
    }

    @Override
    public void delete(int position) {
        manageProgressNoteDelete(position);
    }

    @Override
    public void update(int position) {
        Pageable pageable = progressNotesAdapter.getItemsList().get(position);
        UhcPatientProgressNoteViewModel progressNoteViewModel;
        if (pageable instanceof UhcPatientProgressNoteViewModel) {
            progressNoteViewModel = (UhcPatientProgressNoteViewModel) pageable;
            if (progressNoteCallBack != null) {
                progressNoteCallBack.onProgressNoteEdit(progressNoteViewModel);
            }
        }
    }

    @Override
    public void onRequestCreatedTimeChange() {
        Calendar calendar = Calendar.getInstance();
        final int year, month, day, hour, minute, second;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date oldCreatedTime = sdf.parse(progressCreatedTime);
            if (oldCreatedTime != null) {
                calendar.setTime(oldCreatedTime);
            }
        } catch (ParseException e) {
            Log.e(TAG, "onRequestCreatedTimeChange: ", e);
        }

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        Log.d(TAG, "onRequestCreatedTimeChange: hour : " + hour);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), DatePickerDialog.THEME_HOLO_LIGHT, (view, year1, month1, dayOfMonth) -> {
            doChangesExists = true;
            pickedCreatedTime.set(Calendar.YEAR, year1);
            pickedCreatedTime.set(Calendar.MONTH, month1);
            pickedCreatedTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            TimePickerDialog tpd = new TimePickerDialog(getContext(), TimePickerDialog.THEME_HOLO_LIGHT, (view1, hourOfDay, minute1) -> {
                pickedCreatedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                pickedCreatedTime.set(Calendar.MINUTE, minute1);
                progressCreatedTime = TimeUtils.dateToString(pickedCreatedTime.getTime());
                updateCreatedTimeHeader(progressCreatedTime);
                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_CHANGE_PROGRESS_CREATED_TIME);
                progressNoteCallBack.onProgressCreatedTimeEdited();
            }, hour, minute, false);
            tpd.setOnCancelListener(dialog -> {
                pickedCreatedTime.set(Calendar.HOUR_OF_DAY, hour);
                pickedCreatedTime.set(Calendar.MINUTE, minute);
                pickedCreatedTime.set(Calendar.SECOND, second);
                progressCreatedTime = TimeUtils.dateToString(pickedCreatedTime.getTime());
                updateCreatedTimeHeader(progressCreatedTime);
                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_CHANGE_PROGRESS_CREATED_TIME);
                progressNoteCallBack.onProgressCreatedTimeEdited();
            });
            tpd.show();
            tpd.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
            tpd.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        }, year, month, day);

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.setTime(calendar.getTime());
        maxCalendar.set(Calendar.HOUR_OF_DAY, maxCalendar.getMaximum(Calendar.HOUR_OF_DAY));
        maxCalendar.set(Calendar.MINUTE, maxCalendar.getMaximum(Calendar.MINUTE));
        maxCalendar.set(Calendar.SECOND, maxCalendar.getMaximum(Calendar.SECOND));
        maxCalendar.set(Calendar.MILLISECOND, maxCalendar.getMaximum(Calendar.MILLISECOND));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.setTime(calendar.getTime());
        minCalendar.add(Calendar.DAY_OF_MONTH, -4);
        minCalendar.set(Calendar.HOUR_OF_DAY, minCalendar.getMaximum(Calendar.HOUR_OF_DAY));
        minCalendar.set(Calendar.MINUTE, minCalendar.getMaximum(Calendar.MINUTE));
        minCalendar.set(Calendar.SECOND, minCalendar.getMaximum(Calendar.SECOND));
        minCalendar.set(Calendar.MILLISECOND, minCalendar.getMaximum(Calendar.MILLISECOND));

        Log.d(TAG, "onRequestCreatedTimeChange: Max Date : " + DateTimeHelper.formatDate(maxCalendar.getTime(),DateTimeHelper.SERVER_DATE_TIME_FORMAT));
        Log.d(TAG, "onRequestCreatedTimeChange: Existing Date : " + DateTimeHelper.formatDate(calendar.getTime(),DateTimeHelper.SERVER_DATE_TIME_FORMAT));
        Log.d(TAG, "onRequestCreatedTimeChange: Min Date : " + DateTimeHelper.formatDate(minCalendar.getTime(),DateTimeHelper.SERVER_DATE_TIME_FORMAT));

        datePickerDialog.getDatePicker().setMinDate(0);
        datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());

        datePickerDialog.show();
        datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
    }

    public void registerFragmentCallBack(UhcPatientProgressNotesFragmentCallBacks uhcPatientProgressNotesFragmentCallBacks) {
        this.progressNoteCallBack = uhcPatientProgressNotesFragmentCallBacks;
    }

    public void loadToCreate(String patientCode) {
        Log.d(TAG, "loadToCreate: Patient Code : " + patientCode);
        this.currentMode = MODE_CREATE;
        this.patientCode = patientCode;
        this.patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();
        if (patient != null) {
            if (patient.getAgeType().equalsIgnoreCase("Year")) {
                ageUnit = " yrs";
            } else if (patient.getAgeType().equalsIgnoreCase("Month")) {
                ageUnit = " months";
            }
            updateToolbar(patient.getNameInEnglish() + ", " + patient.getAge() + ageUnit);
        }
        progressCreatedTime = TimeUtils.now();
        addCreatedTimeHeader(progressCreatedTime);
        manageEmptyView();
    }

    public void loadToEdit(@NonNull UhcPatientProgress editTarget) {
        this.currentMode = MODE_EDIT;

        if (editTarget.isManaged()) {
            this.progressEditSource = realm.copyFromRealm(editTarget);
        } else {
            this.progressEditSource = editTarget;
        }
        patientCode = progressEditSource.getPatientCode();
        patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();

        if (patient != null) {
            updateToolbar(patient.getNameInEnglish() + ", " + patient.getAge() + " yrs");
        }

        progressNotesAdapter.clear();

        if (!TextUtils.isEmpty(progressEditSource.getVisitDate())) {
            progressCreatedTime = removeTFromServerTime(progressEditSource.getVisitDate());
        } else {
            progressCreatedTime = removeTFromServerTime(progressEditSource.getCreatedTime());
        }

        addCreatedTimeHeader(progressCreatedTime);
        RealmResults<UhcPatientProgressNote> progressNotesQueries = realm.where(UhcPatientProgressNote.class)
                .equalTo("isDeleted", false)
                .equalTo("progressCode", progressEditSource.getProgressCode())
                .findAll();

        ArrayList<UhcPatientProgressNote> progressNotes = new ArrayList<>(realm.copyFromRealm(progressNotesQueries));

        Log.d(TAG, "setUpContents: Numbers of progressNotes " + progressNotesQueries.size());
        ArrayList<Pageable> progressNoteViewModels = new ArrayList<>();

        for (UhcPatientProgressNote progressNote : progressNotes) {

            RealmResults<UhcPatientProgressNotePhoto> progressNotePhotosQuery = realm.where(UhcPatientProgressNotePhoto.class)
                    .equalTo("isDeleted", false)
                    .equalTo("progressNoteCode", progressNote.getProgressNoteCode())
                    .findAll();

            ArrayList<UhcPatientProgressNotePhoto> photos = new ArrayList<>(realm.copyFromRealm(progressNotePhotosQuery));

            UhcPatientProgressNoteViewModel viewModel = new UhcPatientProgressNoteViewModel();
            viewModel.setProgressNote(progressNote);
            viewModel.setPhotos(photos);
            progressNoteViewModels.add(viewModel);
        }

        progressNotesAdapter.add(progressNoteViewModels);
        manageEmptyView();
    }

    public void addUpdateProgressNote(UhcPatientProgressNoteViewModel progressNoteViewModel) {
        if (progressNoteViewModel != null) {
            doChangesExists = true;
            UhcPatientProgressNote progressNote = progressNoteViewModel.getProgressNote();
            progressNote.setHasChanges(true);
            Log.d(TAG, "onActivityResult: ProgressNote Info : " + progressNote);
            Log.d(TAG, "onActivityResult: ProgressNote Photos : " + progressNoteViewModel.getPhotos().size());
            int duplicatePosition = isPressNoteTypeAlreadyExists(progressNote.getType());
            if (duplicatePosition == -1) {
                Log.d(TAG, "onActivityResult: New ProgressNote Created");
                populateInfo(progressNoteViewModel);
//                manageEmptyView();
                progressNotesAdapter.clearFooter();
                progressNotesAdapter.add(progressNoteViewModel);
                createdPressNotesRecyclerView.smoothScrollToPosition(progressNotesAdapter.getItemCount() - 1);
            } else {
                Log.d(TAG, "onActivityResult: Existing ProgressNote Edited");
                progressNotesAdapter.update(progressNoteViewModel, duplicatePosition);
            }
            Toast.makeText(getActivity(), "Progress Note Kept Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasChanges() {
        return doChangesExists;
    }

    public boolean shouldShowSaveButton() {
        return progressNotesAdapter != null && !isProgressNotesListEmpty();
    }

    public ArrayList<String> getFilteredPressNoteType() {
        ArrayList<String> pressNoteTypes = PressNoteTypeHelper.getTypeHelper().getPressNoteTypes();
        ArrayList<String> adapterPressNoteTypes = new ArrayList<>();

        for (Pageable pageable : progressNotesAdapter.getItemsList()) {
            if (pageable instanceof UhcPatientProgressNoteViewModel) {
                UhcPatientProgressNoteViewModel progressNoteViewModel = (UhcPatientProgressNoteViewModel) pageable;
                adapterPressNoteTypes.add(progressNoteViewModel.getProgressNote().getType());
            }
        }

        for (String adapterPressNote : adapterPressNoteTypes) {
            int position = pressNoteTypes.indexOf(adapterPressNote);
            if (position != -1) {
                pressNoteTypes.remove(position);
            }
        }
        Log.d(TAG, "getFilteredPressNoteType: " + pressNoteTypes);
        return pressNoteTypes;
    }

    private void manageEmptyView() {
        if (isProgressNotesListEmpty()) {
            progressNotesAdapter.showEmptyView(R.layout.layout_empty_view_no_progress_create);
        } else {
            progressNotesAdapter.clearFooter();
        }
    }

    private void updateToolbar(String title) {
        if (progressNoteCallBack != null) {
            progressNoteCallBack.onToolbarTextChange(title);
        }
    }

    private void populateInfo(UhcPatientProgressNoteViewModel progressNoteViewModel) {
        AuthenticationModel authenticationModel = SharePreferenceHelper.getHelper(getContext()).getAuthenticationModel();
        progressNoteViewModel.getProgressNote().setDoctorName(authenticationModel.getStaffName());
        progressNoteViewModel.getProgressNote().setDoctorPhotoUrl(authenticationModel.getPhotoUrl());
        progressNoteViewModel.getProgressNote().setDoctorCode(authenticationModel.getStaffCode());
        progressNoteViewModel.getProgressNote().setSpecialty(authenticationModel.getRoleCode());
        progressNoteViewModel.getProgressNote().setFacilityType(authenticationModel.getFacilityType());
        progressNoteViewModel.getProgressNote().setFacilityTitle(authenticationModel.getFacilityType());
        progressNoteViewModel.getProgressNote().setFacilityCode(authenticationModel.getFacilityCode());
        progressNoteViewModel.getProgressNote().setPatientName(patient.getNameInEnglish());
        progressNoteViewModel.getProgressNote().setPatientCode(patient.getPatientCode());
    }

    private int isPressNoteTypeAlreadyExists(String checkTarget) {
        for (Pageable pageable : progressNotesAdapter.getItemsList()) {
            if (pageable instanceof UhcPatientProgressNoteViewModel) {
                UhcPatientProgressNoteViewModel progressNoteViewModel = (UhcPatientProgressNoteViewModel) pageable;
                if (progressNoteViewModel.getProgressNote().getType().equals(checkTarget)) {
                    return progressNotesAdapter.getItemsList().indexOf(progressNoteViewModel);
                }
            }
        }
        return -1;
    }

    private UhcPatientProgress prepareProgress() {
        UhcPatientProgress progress;
        if (currentMode.equals(MODE_EDIT)) {
            progress = new UhcPatientProgress(progressEditSource);
        } else {
            progress = new UhcPatientProgress();
            progress.setLocalId(UUID.randomUUID().toString());
            progress.setDeleted(false);
            progress.setCreatedTime(TimeUtils.now());
            progress.setPatientCode(patientCode);
            progress.setProgressCode(CodeGen.generateProgressCode());
            progress.setCreatedBy(SharePreferenceHelper.getHelper(getActivity()).getAuthenticationModel().getStaffId());
        }
        progress.setVisitDate(progressCreatedTime);
        return progress;
    }

    public void save() {

        if (isSaveInProgress) {
            return;
        }

        isSaveInProgress = true;

        final UhcPatientProgress progress = prepareProgress();

        if (isProgressNotesListEmpty() && currentMode.equals(MODE_CREATE)) {
            requireActivity().finish();
            return;
        }

        final ArrayList<UhcPatientProgressNote> progressNotesToSave = new ArrayList<>();
        final ArrayList<UhcPatientProgressNotePhoto> photosToSave = new ArrayList<>();

        realm.executeTransactionAsync(realm -> {
            for (Pageable p : progressNotesAdapter.getActualItemsList()) {
                if (p instanceof UhcPatientProgressNoteViewModel) {
                    UhcPatientProgressNoteViewModel progressNoteViewModel = (UhcPatientProgressNoteViewModel) p;
                    UhcPatientProgressNote pn = progressNoteViewModel.getProgressNote();
                    pn.setProgressCode(progress.getProgressCode());
                    photosToSave.addAll(progressNoteViewModel.getPhotos());
                    progressNotesToSave.add(pn);
                }
            }

            for (UhcPatientProgressNote pnToRemove : removedProgressNotes) {
                pnToRemove.setDeleted(true);
            }

            progressNotesToSave.addAll(removedProgressNotes);

            for (UhcPatientProgressNotePhoto photo : removedProgressNotePhotos) {
                photo.setDeleted(true);
            }

            photosToSave.addAll(removedProgressNotePhotos);

            for (UhcPatientProgressNote pnToDelete : progressNotesToDelete) {
                if (pnToDelete.isValid()) {
                    UhcPatientProgressNote ptd = realm.where(UhcPatientProgressNote.class).equalTo("localId", pnToDelete.getLocalId()).findFirst();
                    if (ptd != null) {
                        ptd.deleteFromRealm();
                    }
                }
            }

            for (UhcPatientProgressNotePhoto pnPhotoToDelete : progressNotePhotosToDelete) {
                if (pnPhotoToDelete.isValid()) {
                    UhcPatientProgressNotePhoto ptd = realm.where(UhcPatientProgressNotePhoto.class).equalTo("localId", pnPhotoToDelete.getLocalId()).findFirst();
                    if (ptd != null) {
                        ptd.deleteFromRealm();
                    }
                }
            }

            progress.setHasChanges(true);

            realm.copyToRealmOrUpdate(progress);
            realm.copyToRealmOrUpdate(progressNotesToSave);
            realm.copyToRealmOrUpdate(photosToSave);

            UhcPatient uhcPatient = realm.where(UhcPatient.class).equalTo("patientCode", progress.getPatientCode()).findFirst();
            if (uhcPatient != null) {
                uhcPatient.setHasSynced(false);
                realm.copyToRealmOrUpdate(uhcPatient);
            }
        }, () -> {
            isSaveInProgress = false;
            sendActionAnalytics(currentMode.equals(MODE_EDIT) ? CmisGoogleAnalyticsConstants.ACTION_EDIT_PROGRESS : CmisGoogleAnalyticsConstants.ACTION_CREATE_PROGRESS);
            Toast.makeText(getActivity(), "Progress Saved Successfully", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }, error -> {
            isSaveInProgress = false;
            Toast.makeText(requireContext(), "Failed to save progress!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addCreatedTimeHeader(String createdTime) {
        progressNotesAdapter.addHeader(createdTime);
    }

    private void updateCreatedTimeHeader(String createdTime) {
        progressNotesAdapter.update(new BaseRecyclerViewAdapter.RecyclerHeader<>(createdTime), 0);
    }

    private boolean isProgressNotesListEmpty() {

        int progressNotesCount = 0;

        List<Pageable> allItems = progressNotesAdapter.getItemsList();
        for (Pageable p : allItems) {
            if (p instanceof UhcPatientProgressNoteViewModel) {
                progressNotesCount += 1;
            }
        }

        return progressNotesCount == 0;
    }

    private void manageProgressNoteDelete(final int position) {
        if (position < progressNotesAdapter.getItemCount()) {
            Pageable pageable = progressNotesAdapter.getItemsList().get(position);
            if (pageable instanceof UhcPatientProgressNoteViewModel) {
                final UhcPatientProgressNoteViewModel progressNoteViewModel = (UhcPatientProgressNoteViewModel) pageable;
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Delete Confirm")
                        .setMessage("Are you sure to delete this progress note?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            UhcPatientProgressNote rmPN = progressNoteViewModel.getProgressNote();
                            rmPN.setHasChanges(true);
                            rmPN.setDeleted(true);
                            if (rmPN.isOnline()) {
                                removedProgressNotes.add(rmPN);
                            } else {
                                progressNotesToDelete.add(rmPN);
                            }

                            for (UhcPatientProgressNotePhoto photo : progressNoteViewModel.getPhotos()) {
                                photo.setHasChanges(true);
                                photo.setDeleted(true);
                                if (photo.isOnline()) {
                                    removedProgressNotePhotos.add(photo);
                                } else {
                                    progressNotePhotosToDelete.add(photo);
                                }
                            }
                            removeProgressNote(position);
                            doChangesExists = true;
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                        .create().show();
            }
        }
    }

    private void clearPhotos(int position) {
        RecyclerView.ViewHolder viewHolder = createdPressNotesRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof ProgressNotesAdapter.ProgressNoteHolder) {
            ProgressNotesAdapter.ProgressNoteHolder pressNoteHolder = (ProgressNotesAdapter.ProgressNoteHolder) viewHolder;
            pressNoteHolder.getPhotoAdapter().clear();
        }
    }

    private void removeProgressNote(int position) {
        try {
            clearPhotos(position);
            progressNotesAdapter.remove(position);
            manageEmptyView();
            if (progressNoteCallBack != null) {
                progressNoteCallBack.onProgressNoteDeleted();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String removeTFromServerTime(String dateTime) {
        return dateTime.replace("T", " ");
    }

}
