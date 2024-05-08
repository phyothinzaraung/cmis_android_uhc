package com.koekoetech.clinic.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.model.AuthenticationModel;

public class SharePreferenceHelper {

    private static final String PREF_AUTH_STAFF_ID = "StaffId";
    private static final String PREF_AUTH_STAFF_CODE = "StaffCode";
    private static final String PREF_AUTH_STAFF_NAME = "StaffName";
    private static final String PREF_AUTH_LOGIN_TIME = "LoginTime";
    private static final String PREF_AUTH_LOGOUT_TIME = "LogoutTime";
    private static final String PREF_AUTH_ROLE = "Role";
    private static final String PREF_AUTH_HOSPITAL_NAME = "HospitalName";
    private static final String PREF_AUTH_FACILITY_CODE = "FacilityCode";
    private static final String PREF_AUTH_FACILITY_TITLE = "FacilityTitle";
    private static final String PREF_AUTH_FACILITY_TYPE = "FacilityType";
    private static final String PREF_AUTH_PHOTO_URL = "PhotoUrl";
    private static final String PREF_IS_LOG_IN = "isLoggedIn";
    private static final String PREF_PASSCODE = "passcode";
    private static final String PREF_USER_TYPE = "userType";
    private static final String PREF_BASE_URL = "base_url";
    private static final String PREF_DEVICE_LANG = "device_lang";


    private ContextHelper contextHelper;

    public static SharePreferenceHelper getHelper(Context context) {
        SingletonHelper.accessor.init(context);
        return SingletonHelper.accessor;
    }

    private void init(Context context) {
        contextHelper = new ContextHelper(context);
    }

    private SharedPreferences getSharedPreferences() {
        return contextHelper.getContext().getSharedPreferences(contextHelper.getContext().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    /* [START] Save Login Authentication Model */

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        return getSharedPreferences().edit();
    }

    public void clearSharedPreferences() {
        getSharedPreferencesEditor().clear().apply();
    }

    public AuthenticationModel getAuthenticationModel() {
        AuthenticationModel model = new AuthenticationModel();
        model.setStaffId(getSharedPreferences().getInt(PREF_AUTH_STAFF_ID, 0));
        model.setStaffCode(getSharedPreferences().getString(PREF_AUTH_STAFF_CODE, ""));
        model.setStaffName(getSharedPreferences().getString(PREF_AUTH_STAFF_NAME, ""));
        model.setLoginTime(getSharedPreferences().getString(PREF_AUTH_LOGIN_TIME, ""));
        model.setLogoutTime(getSharedPreferences().getString(PREF_AUTH_LOGOUT_TIME, ""));
        model.setRoleCode(getSharedPreferences().getString(PREF_AUTH_ROLE, ""));
        model.setFacilityName(getSharedPreferences().getString(PREF_AUTH_HOSPITAL_NAME, ""));
        model.setFacilityCode(getSharedPreferences().getString(PREF_AUTH_FACILITY_CODE, ""));
        model.setFacilityTitle(getSharedPreferences().getString(PREF_AUTH_FACILITY_TITLE, ""));
        model.setFacilityType(getSharedPreferences().getString(PREF_AUTH_FACILITY_TYPE, ""));
        model.setPhotoUrl(getSharedPreferences().getString(PREF_AUTH_PHOTO_URL, ""));
        return model;
    }

    /* [END] Save Login Authentication Model */

    public void setAuthenticationModel(AuthenticationModel authenticationModel) {
        if (authenticationModel == null) {
            return;
        }

        getSharedPreferencesEditor().putInt(PREF_AUTH_STAFF_ID, authenticationModel.getStaffId()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_STAFF_CODE, authenticationModel.getStaffCode()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_STAFF_NAME, authenticationModel.getStaffName()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_LOGIN_TIME, authenticationModel.getLoginTime()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_LOGOUT_TIME, authenticationModel.getLogoutTime()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_ROLE, authenticationModel.getRoleCode()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_HOSPITAL_NAME, authenticationModel.getFacilityName()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_FACILITY_CODE, authenticationModel.getFacilityCode()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_FACILITY_TITLE, authenticationModel.getFacilityTitle()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_FACILITY_TYPE, authenticationModel.getFacilityType()).apply();
        getSharedPreferencesEditor().putString(PREF_AUTH_PHOTO_URL, authenticationModel.getPhotoUrl()).apply();

    }

    public void clearAuthenticationModel() {
        setAuthenticationModel(new AuthenticationModel());
    }

    public String getPasscode() {
        return getSharedPreferences().getString(PREF_PASSCODE, "");
    }

    public void setPasscode(String passcode) {
        getSharedPreferencesEditor().remove(PREF_PASSCODE).putString(PREF_PASSCODE, passcode).apply();
    }

    public boolean isLogIn() {
        return getSharedPreferences().getBoolean(PREF_IS_LOG_IN, false);
    }

    public void setLogIn(boolean flag) {
        getSharedPreferencesEditor().putBoolean(PREF_IS_LOG_IN, flag).apply();
    }

    public void setDoctor() {
        getSharedPreferencesEditor().putString(PREF_USER_TYPE, "DOCTOR").apply();
    }

    public void setNurse() {
        getSharedPreferencesEditor().putString(PREF_USER_TYPE, "NURSE").apply();
    }

    public boolean isDoctor() {
        return getUserType().equals("DOCTOR");
    }

    public boolean isNurse() {
        return getUserType().equals("NURSE");
    }

    public String getBaseUrl() {
        return getSharedPreferences().getString(PREF_BASE_URL, BaseUrlProvider.getApiBaseUrl());
    }

    public void setBaseUrl(String baseUrl) {
        getSharedPreferencesEditor().putString(PREF_BASE_URL, baseUrl).apply();
    }

    private String getUserType() {
        return getSharedPreferences().getString(PREF_USER_TYPE, "");
    }

    public String getDeviceLanguage() {
        return getSharedPreferences().getString(PREF_DEVICE_LANG, "");
    }

    public void setDeviceLanguage(String lang) {
        getSharedPreferencesEditor().putString(PREF_DEVICE_LANG, lang).apply();
    }

    /* SharedPreferences Helper Classes (For Singleton and Context)*/

    private static class SingletonHelper {
        private static final SharePreferenceHelper accessor = new SharePreferenceHelper();
    }

    private static class ContextHelper {
        private Context context;

        ContextHelper(Context context) {
            this.context = context;
        }

        Context getContext() {
            return context;
        }
    }

}
