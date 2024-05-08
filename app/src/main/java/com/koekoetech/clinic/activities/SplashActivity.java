package com.koekoetech.clinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.LocationModel;
import com.koekoetech.clinic.model.StaffModel;
import com.koekoetech.clinic.sync.SyncUtils;

import io.realm.Realm;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private ProgressBar progressBarLoading;

    private TextView tvLoadingStatus;
    private TextView tvLangDetect;

    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();

        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);

        new Handler(Looper.getMainLooper()).postDelayed(this::initProcess, 1500);

    }

    private void bindViews(){
        progressBarLoading = findViewById(R.id.splash_loading);
        tvLoadingStatus = findViewById(R.id.splash_loading_tv);
        tvLangDetect = findViewById(R.id.splash_tv_lang_detect);
    }

    private void initProcess() {
        checkLanguage();
        checkAndImportLocations();
        SyncUtils.createSyncAccount(this);
    }

    private void appEntry() {
        Class activity;
        Realm realm = Realm.getDefaultInstance();
        StaffModel staffModel = realm.where(StaffModel.class).findFirst();
        AuthenticationModel authenticationModel = realm.where(AuthenticationModel.class).findFirst();
        if (sharePreferenceHelper.isLogIn() && staffModel != null && authenticationModel != null) {
//            if (PrerequisiteCheckingsHelper.isInternetAvailable(this)) {
//                activity = DataSyncActivity.class;
//            } else {
//                activity = CustomPassActivity.class;
//            }
            activity = CustomPassActivity.class;
        } else {
            activity = LoginActivity.class;
        }
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void checkAndImportLocations() {
        final Realm realm = Realm.getDefaultInstance();
        boolean isLocationsTableEmpty = realm.where(LocationModel.class).findAll().isEmpty();

        if (isLocationsTableEmpty) {

            Log.d(TAG, "checkAndImportLocations: Suggestions Table is Empty");

            progressBarLoading.setVisibility(View.VISIBLE);
            progressBarLoading.setIndeterminate(true);

            String preparingSuggestions = "Setting Up...";
            tvLoadingStatus.setText(preparingSuggestions);

            realm.executeTransactionAsync(realm1 -> {
                try {
                    // Import JSON contents to Realm Database
                    realm1.createAllFromJson(LocationModel.class, getAssets().open("location.json"));
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }, () -> {
                progressBarLoading.setVisibility(View.GONE);
                tvLoadingStatus.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess: Locations Database Successfully Prepared!");
                appEntry();
            }, error -> {
                progressBarLoading.setVisibility(View.GONE);
                tvLoadingStatus.setVisibility(View.GONE);
                Log.e(TAG, "onError: Locations Import Error", error);
                appEntry();
            });
        } else {
            Log.d(TAG, "checkAndImportLocations: Locations Table is NOT Empty");
            Log.d(TAG, "Number Of Records " + realm.where(LocationModel.class).count());
            appEntry();
        }


    }

    private void checkLanguage() {
        if (TextUtils.isEmpty(sharePreferenceHelper.getDeviceLanguage())) {
            sharePreferenceHelper.setDeviceLanguage(detectDeviceLanguage());
        }
    }

    private String detectDeviceLanguage() {
        tvLangDetect.setText("\u1000");
        tvLangDetect.measure(0, 0);
        int tvDetectOneLength = tvLangDetect.getMeasuredWidth();

        tvLangDetect.setText("\u1000\u1039\u1000");
        tvLangDetect.measure(0, 0);
        int tvDetectTwoLength = tvLangDetect.getMeasuredWidth();

        String detectedLanguage = tvDetectOneLength == tvDetectTwoLength ? CMISConstant.DEVICE_LANG_UNICODE : CMISConstant.DEVICE_LANG_ZAWGYI;
        Log.d(TAG, "detectDeviceLanguage() returned: " + detectedLanguage);
        return detectedLanguage;
    }

}
