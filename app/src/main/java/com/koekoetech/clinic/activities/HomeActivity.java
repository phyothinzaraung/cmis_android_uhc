package com.koekoetech.clinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.app.GlideApp;
import com.koekoetech.clinic.fragment.FeedbackFragment;
import com.koekoetech.clinic.fragment.SettingsFragment;
import com.koekoetech.clinic.fragment.UHCReportFragment;
import com.koekoetech.clinic.fragment.UhcFollowUpPatientsListFragment;
import com.koekoetech.clinic.fragment.UhcPatientsListFragment;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.InMemoryAppStore;
import com.koekoetech.clinic.helper.SharePreferenceHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.UhcPatient;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ZMN on 4/5/17.
 **/

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    // tags used to attach the fragments
    private static final String TAG_UHC_PATIENT = "chs_PatientFragment";
    private static final String TAG_UHC_FOLLOWUP_PATIENT = "uhc_followup_patient";
    private static final String TAG_UHC_REPORT = "uhc_ReportFragment";
    private static final String TAG_SETTING = "settingFragment";
    private static final String TAG_FEEDBACK = "feedbackFragment";

    // index to identify current nav menu item
    private static int NAV_ITEM_INDEX = 0;
    private static String CURRENT_TAG = TAG_UHC_PATIENT;

    // toolbar titles respected to selected nav menu item
    String[] activityTitles;

    // views
    private DrawerLayout drawerLayout;
    private NavigationView leftNavigationView;
    private FloatingActionButton fab_uhc_patient;
    private LinearLayout syncInfoContainer;
    private TextView tvModifiedPatientsCount;
    private TextView tvLblModifiedPatientsCount;
    private MaterialButton btnUpload;
    private View syncInfoDivider;

    private Fragment currentFragment;
    private SharePreferenceHelper preferenceHelper;
    private RealmResults<UhcPatient> modifiedPatients;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_app;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        Log.d(TAG, "setUpContents() called with: savedInstanceState = [" + savedInstanceState + "]");

        bindViews();

        init();

        setupViews();

        openPatientDetail();
    }

    @Override
    protected void onResume() {
        if (leftNavigationView.getHeaderCount() > 0) {
            View header = leftNavigationView.getHeaderView(0);

            ImageView ivDoctorProfile = header.findViewById(R.id.ivStaffProfile);
            TextView doctor_info = header.findViewById(R.id.doctor_info);
            TextView tv_role = header.findViewById(R.id.tv_role);
            TextView tv_ward = header.findViewById(R.id.tv_ward);
            TextView tv_hospital = header.findViewById(R.id.tv_hospital);

            AuthenticationModel model = Realm.getDefaultInstance().where(AuthenticationModel.class).findFirst();
            if (model != null) {
                Log.d(TAG, "setUpContents: Staff Info : " + model.toString());

                if (!TextUtils.isEmpty(model.getPhotoUrl())) {
                    Log.d(TAG, "setUpContents: Staff Photo : " + model.getPhotoUrl());
                    GlideApp.with(this)
                            .load(model.getPhotoUrl())
                            .placeholder(R.mipmap.sample_doctor)
                            .error(R.mipmap.sample_doctor)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(ivDoctorProfile);
                } else {
                    ivDoctorProfile.setImageResource(R.mipmap.sample_doctor);
                }

                doctor_info.setText(model.getStaffName());
                tv_role.setText(model.getRoleCode());
                if (!TextUtils.isEmpty(model.getFacilityType()) && !TextUtils.isEmpty(model.getFacilityTitle())) {
                    tv_ward.setText(String.format("%s | %s", model.getFacilityType(), model.getFacilityTitle()));
                } else {
                    tv_ward.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getFacilityName())) {
                    tv_hospital.setText(model.getFacilityName());
                } else {
                    tv_hospital.setVisibility(View.GONE);
                }
            }

        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (NAV_ITEM_INDEX != 0) {
            NAV_ITEM_INDEX = 0;
            CURRENT_TAG = TAG_UHC_PATIENT;
            loadFragment();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (modifiedPatients != null) {
            modifiedPatients.removeChangeListener(this::onPatientsModified);
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.nav_uhc_followup_patient) {
            NAV_ITEM_INDEX = 1;
            CURRENT_TAG = TAG_UHC_FOLLOWUP_PATIENT;
        } else if (itemId == R.id.nav_uhc_report) {
            NAV_ITEM_INDEX = 2;
            CURRENT_TAG = TAG_UHC_REPORT;
        } else if (itemId == R.id.nav_feedback) {
            NAV_ITEM_INDEX = 3;
            CURRENT_TAG = TAG_FEEDBACK;
        } else if (itemId == R.id.nav_setting) {
            NAV_ITEM_INDEX = 4;
            CURRENT_TAG = TAG_SETTING;
        } else if (itemId == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            (dialog, whichButton) -> Realm.getDefaultInstance().executeTransactionAsync(realm -> realm.deleteAll(), () -> {
                                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_LOGOUT);
                                preferenceHelper.clearAuthenticationModel();
                                preferenceHelper.setLogIn(false);
                                final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
                                firebaseAnalytics.setUserProperty(CmisGoogleAnalyticsConstants.UP_STAFF_ID, null);
                                firebaseAnalytics.setUserProperty(CmisGoogleAnalyticsConstants.UP_STAFF_USER_NAME, null);
                                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }))
                    .setNegativeButton("No",
                            (dialog, whichButton) -> dialog.dismiss()).show();
        } else {
            NAV_ITEM_INDEX = 0;
            CURRENT_TAG = TAG_UHC_PATIENT;
        }

        //Checking if the item is in checked state or not, if not make it in checked state
        if (!item.isChecked()) {
            item.setChecked(true);
        }

        AppUtils.dismissSoftKeyboard(this);

        loadFragment();

        return true;
    }

    private void bindViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        leftNavigationView = findViewById(R.id.nav_view_left);
        fab_uhc_patient = findViewById(R.id.fab_uhc_patient);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        syncInfoContainer = findViewById(R.id.syncInfoContainer);
        syncInfoDivider = findViewById(R.id.syncInfoContainerDivider);
        tvLblModifiedPatientsCount = findViewById(R.id.tvLblModifiedPatientsCount);
        tvModifiedPatientsCount = findViewById(R.id.tvModifiedPatientsCount);
        btnUpload = findViewById(R.id.btnUpload);
    }

    private void init() {
        preferenceHelper = SharePreferenceHelper.getHelper(this);
        //    private Snackbar syncSnack;
        Realm realm = Realm.getDefaultInstance();
        modifiedPatients = realm.where(UhcPatient.class).equalTo("hasSynced", false).findAllAsync();
        modifiedPatients.addChangeListener(this::onPatientsModified);
        if (modifiedPatients.isLoaded()) {
            onPatientsModified(modifiedPatients);
        }
    }

    private void setupViews() {
        setupToolbar(false);

        // initializing navigation menu
        setUpNavigationView(leftNavigationView);

        MenuItem menuItem = leftNavigationView.getMenu().getItem(NAV_ITEM_INDEX);
        onNavigationItemSelected(menuItem);

        btnUpload.setOnClickListener(v -> {
            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_UPLOAD_PATIENT_DATA);
            Intent dataSyncIntent = new Intent(getApplicationContext(), DataSyncActivity.class);
            dataSyncIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dataSyncIntent);
        });
    }

    private void loadFragment() {

        Log.d(TAG, "loadFragment: NAV_ITEM_INDEX : " + NAV_ITEM_INDEX + " CURRENT_TAG : " + CURRENT_TAG);

        // set toolbar title
        setupToolbarText(activityTitles[NAV_ITEM_INDEX]);

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawerLayout.closeDrawers();
            manageFab();
            return;
        }

        currentFragment = getFragment();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = () -> {
            // update the main content by replacing fragments
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.fragment_container, currentFragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        };

        leftNavigationView.post(mPendingRunnable);

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();

        manageFab();
    }

    /**
     * Returns the appropriate Fragment depending on the nav menu item user selected.
     */
    private Fragment getFragment() {
        switch (NAV_ITEM_INDEX) {
            case 1:
                //UHC followup patients fragment
                return new UhcFollowUpPatientsListFragment();

            case 2:
                //UHC Reports Fragment
                return new UHCReportFragment();

            case 3:
                // Feedback Fragment
                return new FeedbackFragment();

            case 4:
                // Settings Fragment
                return new SettingsFragment();
            case 0:
                //UHC Patient Fragment
            default:
                return new UhcPatientsListFragment();
        }
    }

    private void setUpNavigationView(NavigationView nv) {

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        nv.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.action_open_drawer, R.string.action_close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void manageFab() {
        if (TAG_UHC_PATIENT.equals(CURRENT_TAG)) {
            fab_uhc_patient.show();
            fab_uhc_patient.setOnClickListener(v -> startActivity(UhcPatientRegistrationActivity.getCreateIntent(HomeActivity.this)));
        } else {
            fab_uhc_patient.hide();
        }
    }

    private void onPatientsModified(@NonNull final RealmResults<UhcPatient> modifiedPatients) {
        final int modifiedCount = modifiedPatients.size();
        Log.d(TAG, "onPatientsModified: " + modifiedCount + " Modified.");
        if (modifiedCount > 0) {
            tvModifiedPatientsCount.setText(String.valueOf(modifiedCount));

            @ColorRes final int countTextColor;
            if (modifiedCount > 5) {
                countTextColor = R.color.color_investigation;
            } else if (modifiedCount > 10) {
                countTextColor = R.color.color_referral;
            } else {
                countTextColor = R.color.colorPrimary;
            }
            tvModifiedPatientsCount.setTextColor(ContextCompat.getColor(this, countTextColor));
            tvLblModifiedPatientsCount.setText(getResources().getQuantityString(R.plurals.lbl_modified_patients, modifiedCount));
            AppUtils.applyVisibility(syncInfoContainer, View.VISIBLE);
            AppUtils.applyVisibility(syncInfoDivider, View.VISIBLE);
        } else {
            AppUtils.applyVisibility(syncInfoContainer, View.GONE);
            AppUtils.applyVisibility(syncInfoDivider, View.GONE);
        }
    }

    private void openPatientDetail() {
        String detailPatientCode = InMemoryAppStore.getInstance().getDetailPatientCode();
        if (!TextUtils.isEmpty(detailPatientCode)) {
            InMemoryAppStore.getInstance().setDetailPatientCode("");
            startActivity(UhcPatientDetailActivity.getNewIntent(detailPatientCode, this));
        }
    }

}
