package com.koekoetech.clinic.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.ChangePassCodeActivity;
import com.koekoetech.clinic.activities.DataSyncActivity;
import com.koekoetech.clinic.activities.HomeActivity;
import com.koekoetech.clinic.activities.ManageSuggestionsActivity;
import com.koekoetech.clinic.activities.PasswordEditActivity;
import com.koekoetech.clinic.activities.ProfileEditActivity;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.DevSettingEntryPoint;
import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.MultiClickListener;
import com.koekoetech.clinic.helper.PatientDataBackUpTask;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.helper.UhcPatientDownloadHelper;
import com.koekoetech.clinic.interfaces.UhcPatientDownloadCallbacks;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatient;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.lingala.zip4j.core.ZipFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import io.realm.Realm;
import needle.Needle;

/**
 * Created by ZMN on 5/16/17.
 **/

public class SettingsFragment extends BaseFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();
    private static final int INVALID_STAFF_ID = -324;

    private TextView tvSyncStatus;
    private TextView tvAppVersion;
    private CardView devSettingsCard;
    private CardView cvDataBackUp;

    private boolean hasNotSyncedData;
    private Realm realm;
    private AppProgressDialog appProgressDialog;
    private UhcPatientDownloadHelper patientDownloadHelper;
    private int staffId;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "SettingsFragment:onViewReady: called");
        bindViews(view);

        realm = Realm.getDefaultInstance();

        cvDataBackUp.setOnClickListener((v) -> onClickDataBackUp());

        String appVersion = getString(R.string.app_name) + "\n" + getString(R.string.app_version);
        tvAppVersion.setText(appVersion);

        tvAppVersion.setOnClickListener(new MultiClickListener(7) {
            @Override
            protected void onTargetReached(View v) {
                int visibility = devSettingsCard.isShown() ? View.GONE : View.VISIBLE;
                devSettingsCard.setVisibility(visibility);
            }

            @Override
            protected void onClickEachTime(int clickedCount) {
                Log.d(TAG, "onClickEachTime: " + clickedCount);
            }
        });

        appProgressDialog = new AppProgressDialog();
        prepareDownloadHelper();

        AuthenticationModel authenticationModel = realm.where(AuthenticationModel.class).findFirst();
        if (authenticationModel == null) {
            staffId = INVALID_STAFF_ID;
        } else {
            staffId = authenticationModel.getStaffId();
        }
    }

    private void bindViews(View view) {
        tvSyncStatus = view.findViewById(R.id.settings_tvSyncStatus);
        tvAppVersion = view.findViewById(R.id.settings_tvAppVersion);
        devSettingsCard = view.findViewById(R.id.settings_cvDevSetting);
        cvDataBackUp = view.findViewById(R.id.settings_cvBackUpDataUpload);

        CardView cvProfile = view.findViewById(R.id.settings_cvProfile);
        CardView cvPassword = view.findViewById(R.id.settings_cvPassword);
        CardView cvPasscode = view.findViewById(R.id.settings_cvPasscode);
        CardView cvManageSuggestion = view.findViewById(R.id.settings_cvManageSuggestion);
        CardView cvDataSync = view.findViewById(R.id.settings_cvDataSync);
        CardView cvDataSyncDownload = view.findViewById(R.id.settings_cvDataSyncDownload);


        cvProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileEditActivity.class)));

        cvPassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), PasswordEditActivity.class)));
        cvPasscode.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePassCodeActivity.class);
            startActivity(intent);
        });
        cvManageSuggestion.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManageSuggestionsActivity.class);
            startActivity(intent);
        });
        cvDataSync.setOnClickListener(v -> {
            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_UPLOAD_PATIENT_DATA);
            startActivity(new Intent(getContext(), DataSyncActivity.class));
        });
        devSettingsCard.setOnClickListener(v -> DevSettingEntryPoint.goToDevSetting(getActivity()));
        cvDataSyncDownload.setOnClickListener(v -> {

            if (hasNotSyncedData) {
                AlertDialog dialog = new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.setting_patient_download_confirm_dialog_title))
                        .setMessage(getResources().getString(R.string.setting_patient_download_confirm_dialog_msg))
                        .setPositiveButton(getResources().getString(R.string.proceed), (dialog12, which) -> {
                            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_DOWNLOAD_PATIENT_DATA);
                            patientDownloadHelper.initDownloadProcess(staffId);
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog1, which) -> dialog1.cancel())
                        .show();
                Button buttonProceed = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonProceed.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
            } else if (staffId == INVALID_STAFF_ID) {
                Toast.makeText(requireContext(), getString(R.string.setting_staff_id_err_msg), Toast.LENGTH_SHORT).show();
            } else {
                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_DOWNLOAD_PATIENT_DATA);
                patientDownloadHelper.initDownloadProcess(staffId);
            }
        });
    }

    @Override
    public void onResume() {
        Log.d(TAG, "SettingsFragment:onResume: called");

        realm = Realm.getDefaultInstance();

        long notSyncedCount = realm.where(UhcPatient.class).equalTo("hasSynced", false).count();
        long suggestionsNotSyncedCount = realm.where(SuggestionWordModel.class).equalTo("hasSynced", false).count();

        if (notSyncedCount > 0) {
            hasNotSyncedData = true;
            String message = notSyncedCount + " patients not synced";
            tvSyncStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_text));
            tvSyncStatus.setText(message);
        } else {
            hasNotSyncedData = false;
            String message = "All patients are synced";
            tvSyncStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.nav_bg_badge));
            tvSyncStatus.setText(message);
        }

        if (notSyncedCount > 0 || suggestionsNotSyncedCount > 0) {
            cvDataBackUp.setVisibility(View.VISIBLE);
        } else {
            cvDataBackUp.setVisibility(View.GONE);
        }

        super.onResume();
    }

    private void onClickDataBackUp() {
        getAppProgressDialog().setTitle("Loading");
        getAppProgressDialog().display(requireFragmentManager());
        getAppProgressDialog().setMessage("Backing Up Data");

        Needle.onBackgroundThread().execute(new PatientDataBackUpTask(getViewLifecycleOwner(), requireContext()) {
            @Override
            protected void onComplete(@Nullable ZipFile zipFile) {
                if (zipFile == null) {
                    Toast.makeText(requireContext(), "Back Up Failed", Toast.LENGTH_SHORT).show();
                } else {
                    uploadBackUpFile(zipFile);
                }
            }
        });
    }

    private AppProgressDialog getAppProgressDialog() {
        if (appProgressDialog == null) {
            appProgressDialog = new AppProgressDialog();
        }
        return appProgressDialog;
    }

    private void prepareDownloadHelper() {
        patientDownloadHelper = new UhcPatientDownloadHelper(new UhcPatientDownloadCallbacks() {
            @Override
            public Context getContext() {
                return requireContext();
            }

            @NonNull
            @Override
            public LifecycleOwner lifecycleOwner() {
                return getViewLifecycleOwner();
            }

            @Override
            public void patientDataRequestOnStart() {
                Log.d(TAG, "patientDataRequestOnStart() called");
                getAppProgressDialog().display(requireActivity().getSupportFragmentManager());
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    getAppProgressDialog().setTitle("Requesting Clinic Data");
                    getAppProgressDialog().setMessage("Waiting for clinic data");
                }, 100);

            }

            @Override
            public void patientDataRequestOnFail() {
                Log.d(TAG, "patientDataRequestOnFail() called");
                getAppProgressDialog().safeDismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_req_failed))
                        .setMessage(getResources().getString(R.string.dialog_msg_clinic_data_req_failed))
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), null)
                        .show();
            }

            @Override
            public void dataDownloadOnPrepare() {
                Log.d(TAG, "dataDownloadOnPrepare() called");
                if (!getAppProgressDialog().isDisplaying()) {
                    getAppProgressDialog().display(requireActivity().getSupportFragmentManager());
                    getAppProgressDialog().setTitle("Preparing Download");
                    getAppProgressDialog().setMessage("Preparing to initDownloadProcess clinic data");
                }
            }

            @Override
            public void dataDownloadOnStart() {
                Log.d(TAG, "dataDownloadOnStart() called");
                getAppProgressDialog().setTitle("Downloading Clinic Data");
            }

            @Override
            public void dataDownloadProgressUpdate(String progressMessage) {
                Log.d(TAG, "dataDownloadProgressUpdate() called with: progressMessage = [" + progressMessage + "]");
                getAppProgressDialog().setMessage(progressMessage);
            }

            @Override
            public void dataDownloadOnFailed(String failedUrl) {
                Log.d(TAG, "dataDownloadOnFailed() called with: failedUrl = [" + failedUrl + "]");
                getAppProgressDialog().safeDismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_download_failed))
                        .setMessage(getResources().getString(R.string.dialog_msg_clinic_data_download_failed))
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), (dialog, which) -> patientDownloadHelper.downloadClinicData(failedUrl))
                        .setNegativeButton(getString(R.string.cancel), ((dialog, which) -> dialog.dismiss()))
                        .show();
            }

            @Override
            public void onStartDataImport() {
                Log.d(TAG, "onStartDataImport() called");
                getAppProgressDialog().setTitle("Importing");
                getAppProgressDialog().setMessage("Importing Clinic Data");
            }

            @Override
            public void onCompleteDataImport() {
                Log.d(TAG, "onCompleteDataImport() called");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    getAppProgressDialog().safeDismiss();

                    Intent homeIntent = new Intent(requireContext(), HomeActivity.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    Toast.makeText(requireContext(), "Data Import Completed", Toast.LENGTH_SHORT).show();
                }, 500);
            }

            @Override
            public void onErrorDataImport(String errMsg) {
                Log.d(TAG, "onErrorDataImport() called with: errMsg = [" + errMsg + "]");
                getAppProgressDialog().safeDismiss();

                new AlertDialog.Builder(requireContext())
                        .setTitle(getResources().getString(R.string.dialog_title_clinic_data_import_failed))
                        .setMessage(errMsg)
                        .setPositiveButton(getResources().getString(R.string.txt_btn_ok), (dialog, which) -> patientDownloadHelper.initDownloadProcess(staffId))
                        .show();
            }

            @Override
            public void onDataImportProgress(String message) {
                Log.d(TAG, "onDataImportProgress() called with: message = [" + message + "]");
                getAppProgressDialog().setMessage(message);
            }
        });
    }

    private String getBackUpFileUploadUrl() {
        String baseUrl = SharePreferenceHelper.getHelper(requireContext()).getBaseUrl();
        String uploadPath = "sync/uploadexport?doctorid=" + staffId;
        String uploadUrl = baseUrl + uploadPath;
        Log.d(TAG, "getBackUpFileUploadUrl() returned: " + uploadUrl);
        return uploadUrl.trim();
    }

    private void uploadBackUpFile(@NonNull final ZipFile zipFile) {
        getAppProgressDialog().setTitle("Loading");
        getAppProgressDialog().setMessage("Uploading Data");
        try {
            String backUpFileUploadUrl = getBackUpFileUploadUrl();
            MultipartUploadRequest multipartUploadRequest = new MultipartUploadRequest(requireContext(), backUpFileUploadUrl);
            multipartUploadRequest.addFileToUpload(zipFile.getFile().getAbsolutePath(), "file");
            multipartUploadRequest.setMaxRetries(2);
            multipartUploadRequest.setDelegate(new UploadStatusDelegate() {

                @Override
                public void onProgress(Context context, UploadInfo uploadInfo) {
                    getAppProgressDialog().setMessage("Uploading Data ( " + uploadInfo.getProgressPercent() + " % )");
                    Log.d(TAG, "onProgress: Uploaded : " + uploadInfo.getProgressPercent() + " %");
                }

                @Override
                public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                    Toast.makeText(context, "Failed to upload back up data!", Toast.LENGTH_LONG).show();
                    FileHelper.delete(zipFile.getFile());
                    getAppProgressDialog().safeDismiss();
                    Log.w(TAG, "onError: Zip Uploaded Error : ", exception);
                }

                @Override
                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                    Toast.makeText(context, "Data Back up uploaded successfully", Toast.LENGTH_SHORT).show();
                    FileHelper.delete(zipFile.getFile());
                    NotificationManager notificationManager = (NotificationManager) requireContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancelAll();
                    }
                    getAppProgressDialog().safeDismiss();
                }

                @Override
                public void onCancelled(Context context, UploadInfo uploadInfo) {

                }

            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                multipartUploadRequest.setNotificationConfig(new UploadNotificationConfig());
            }

            multipartUploadRequest.startUpload();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to upload back up data!", Toast.LENGTH_LONG).show();
            getAppProgressDialog().safeDismiss();
        }
    }

}
