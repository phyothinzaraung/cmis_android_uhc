package com.koekoetech.clinic.helper;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Created by ZMN on 7/24/18.
 **/
@SuppressWarnings({"WeakerAccess"})
public class CmisGoogleAnalyticsConstants {

    // User Properties
    public static final String UP_STAFF_ID = "StaffId";

    public static final String UP_STAFF_USER_NAME = "StaffUserName";

    // Screen Names

    public static final String BAR_CODE_SCANNER = "Barcode Scanner";

    public static final String DATA_SYNC = "Data Sync";

    public static final String FEEDBACK = "Feedback";

    public static final String FOLLOW_UP_PATIENTS = "FollowUp Patients";

    public static final String HOME = "Home";

    public static final String IMAGE_CROPPER = "Image Cropper";

    public static final String IMMUNIZATION_LIST = "Immunizations List";

    public static final String LOGIN = "Login";

    public static final String MANAGE_SUGGESTIONS = "Manage Suggestions";

    public static final String PASS_CODE_EDIT = "Passcode Edit";

    public static final String PASS_CODE_LOCK = "Passcode Lock";

    public static final String PASSWORD_EDIT = "Password Edit";

    public static final String PATIENT_ADVANCE_SEARCH = "Patients Advance Search";

    public static final String PATIENT_DETAIL = "Patient Detail";

    public static final String PATIENT_REG = "Patient Registration";

    public static final String PATIENTS_LIST = "Patients List";

    public static final String PROFILE_EDIT = "Profile Edit";

    public static final String PROGRESS_NOTE_CREATE = "Progress Note Create";

    public static final String PROGRESS_NOTE_PHOTO_VIEWER = "Progress Note Photo Viewer";

    public static final String PROGRESS_NOTES_LIST = "Progress Note List";

    public static final String REPORTS = "Reports";

    public static final String RH_SHORT_TERM_FORM = "RH Short Term Form";

    public static final String RH_SHORT_TERM_REPORTS = "RH Short Term Reports";

    public static final String SETTING = "Settings";

    public static final String SPLASH = "Splash";

    public static final String SQH_DOCTOR_LIST = "SQH Doctors List Chooser";

    public static final String WRAP_UP = "Visit Wrap Up";

    public static final String ICD_TERM_CHOOSER = "ICD Term Chooser";

    public static final String DISABILITY_SURVEY_FORM = "Disability Survey Form";

    // Actions

    public static final String ACTION_ADD_NEW_SUGGESTION_WORD = "Add New Suggestion Word";

    public static final String ACTION_CAPTURE_PATIENT_IMAGE = "Capture Patient Image";

    public static final String ACTION_CHANGE_PROGRESS_CREATED_TIME = "Change Progress Created Time";

    public static final String ACTION_CREATE_PROGRESS = "Create Progress";

    public static final String ACTION_DELETE_PATIENT = "Delete Patient";

    public static final String ACTION_DELETE_PATIENT_PROGRESS = "Delete Patient Progress";

    public static final String ACTION_DOWNLOAD_PATIENT_DATA = "Download Patient Data";

    public static final String ACTION_EDIT_PATIENT_INFO = "Edit Patient Info";

    public static final String ACTION_EDIT_PROGRESS = "Edit Progress";

    public static final String ACTION_LOGIN = "Perform Login";

    public static final String ACTION_LOGOUT = "Logout";

    public static final String ACTION_PATIENT_ADVANCE_SEARCH = "Advance Search Patients";

    public static final String ACTION_PATIENT_SEARCH = "Search Patients";

    public static final String ACTION_READ_BAR_CODE = "Read Barcode";

    public static final String ACTION_REFER_PATIENT = "Refer Patient";

    public static final String ACTION_REGISTER_NEW_PATIENT = "Register New Patient";

    public static final String ACTION_SELECT_IMMUNIZATION = "Select Immunizations";

    public static final String ACTION_SKIP_PASS_CODE = "Skip Passcode";

    public static final String ACTION_TAKE_PROGRESS_NOTE_PHOTO = "Take Progress Note Photo";

    public static final String ACTION_UPLOAD_PATIENT_DATA = "Upload Patient Data";

    public static final String ACTION_NCD_MODULE = "Use NCD Module";

    public static final String ACTION_ANSWER_DISABILITY_SURVEY = "Answer Disability Survey";

    public static final String ACTION_SEARCH_BY_QR = "Search Patient By QR Scanning";

    public static final String ACTION_SEARCH_BY_NAME = "Search Patient By Name/UIC";

    public static String getScreenName(@NonNull String key) {
        return getScreenNameDictionary().getString(key, "");
    }

    private static Bundle getScreenNameDictionary() {
        Bundle screenNames = new Bundle();

        // Activities
        screenNames.putString("com.koekoetech.clinic.activities.BarcodeScannerActivity", BAR_CODE_SCANNER);
        screenNames.putString("com.koekoetech.clinic.activities.ChangePassCodeActivity", PASS_CODE_EDIT);
        screenNames.putString("com.koekoetech.clinic.activities.CustomPassActivity", PASS_CODE_LOCK);
        screenNames.putString("com.koekoetech.clinic.activities.DataSyncActivity", DATA_SYNC);
        screenNames.putString("com.koekoetech.clinic.activities.HomeActivity", HOME);
        screenNames.putString("com.koekoetech.clinic.activities.ImageCropperActivity", IMAGE_CROPPER);
        screenNames.putString("com.koekoetech.clinic.activities.ImmunizationListActivity", IMMUNIZATION_LIST);
        screenNames.putString("com.koekoetech.clinic.activities.LoginActivity", LOGIN);
        screenNames.putString("com.koekoetech.clinic.activities.ManageSuggestionsActivity", MANAGE_SUGGESTIONS);
        screenNames.putString("com.koekoetech.clinic.activities.PasswordEditActivity", PASSWORD_EDIT);
        screenNames.putString("com.koekoetech.clinic.activities.ProfileEditActivity", PROFILE_EDIT);
        screenNames.putString("com.koekoetech.clinic.activities.RHShortTermActivity", RH_SHORT_TERM_FORM);
        screenNames.putString("com.koekoetech.clinic.activities.SearchActivity", PATIENT_ADVANCE_SEARCH);
        screenNames.putString("com.koekoetech.clinic.activities.SplashActivity", SPLASH);
        screenNames.putString("com.koekoetech.clinic.activities.SQHDoctorListActivity", SQH_DOCTOR_LIST);
        screenNames.putString("com.koekoetech.clinic.activities.UhcFormsChoiceActivity", WRAP_UP);
        screenNames.putString("com.koekoetech.clinic.activities.UhcPatientDetailActivity", PATIENT_DETAIL);
        screenNames.putString("com.koekoetech.clinic.activities.UhcPatientRegistrationActivity", PATIENT_REG);
        screenNames.putString("com.koekoetech.clinic.activities.UhcProgressNotePhotoDetailActivity", PROGRESS_NOTE_PHOTO_VIEWER);
        screenNames.putString("com.koekoetech.clinic.UHCReportActivity", RH_SHORT_TERM_REPORTS);
        screenNames.putString("com.koekoetech.clinic.activities.ICDChooserActivity", ICD_TERM_CHOOSER);
        screenNames.putString("com.koekoetech.clinic.activities.DisabilitySurveyActivity", DISABILITY_SURVEY_FORM);

        // Fragments
        screenNames.putString("com.koekoetech.clinic.fragment.FeedbackFragment", FEEDBACK);
        screenNames.putString("com.koekoetech.clinic.fragment.SettingsFragment", SETTING);
        screenNames.putString("com.koekoetech.clinic.fragment.UhcFollowUpPatientsListFragment", FOLLOW_UP_PATIENTS);
        screenNames.putString("com.koekoetech.clinic.fragment.UhcPatientProgressNoteCreateFragment", PROGRESS_NOTE_CREATE);
        screenNames.putString("com.koekoetech.clinic.fragment.UhcPatientProgressNotesFragment", PROGRESS_NOTES_LIST);
        screenNames.putString("com.koekoetech.clinic.fragment.UhcPatientsListFragment", PATIENTS_LIST);
        screenNames.putString("com.koekoetech.clinic.fragment.UHCReportFragment", REPORTS);

        return screenNames;
    }

}
