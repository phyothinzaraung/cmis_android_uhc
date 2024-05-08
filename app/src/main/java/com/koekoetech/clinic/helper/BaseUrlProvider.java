package com.koekoetech.clinic.helper;

import com.koekoetech.clinic.BuildConfig;

/**
 * Created by ZMN on 1/30/19.
 **/
@SuppressWarnings("WeakerAccess")
public class BaseUrlProvider {

    public static String getApiBaseUrl() {
        if (BuildConfig.DEBUG) {
            return BuildConfig.DEV_BASE_URL;
        } else {
            return BuildConfig.DEFAULT_BASE_URL;
        }
    }

    public static String getWebViewBaseUrl() {
//        if (BuildConfig.DEBUG) {
//            return BuildConfig.WEB_VIEW_BASE_URL_DEV;
//        } else {
//            return BuildConfig.WEB_VIEW_BASE_URL;
//        }
        return BuildConfig.WEB_VIEW_BASE_URL;
    }
}
