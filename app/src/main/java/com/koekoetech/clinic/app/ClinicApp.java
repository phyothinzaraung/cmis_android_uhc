package com.koekoetech.clinic.app;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.koekoetech.clinic.BuildConfig;
import com.koekoetech.clinic.helper.RealmMigrator;

import net.gotev.uploadservice.UploadService;

import androidx.multidex.MultiDexApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hello on 2/25/17.
 **/

public class ClinicApp extends MultiDexApplication {

    private static ClinicApp instance;

    public static ClinicApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        setUpRealm();

        setUpPRDownloader();

        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;


    }

    private void setUpRealm() {
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("cmis_db")
                .schemaVersion(17)
                .migration(new RealmMigrator())
                .build();
        Realm.setDefaultConfiguration(realmConfig);

    }

    private void setUpPRDownloader() {
        PRDownloaderConfig.Builder configBuilder = PRDownloaderConfig.newBuilder();

        // Enabling database for resume support even after the application is killed:
        configBuilder.setDatabaseEnabled(true);

        // Setting timeout globally for the download network requests:
        configBuilder.setConnectTimeout(5 * 60 * 1000);
        configBuilder.setReadTimeout(5 * 60 * 1000);

        PRDownloaderConfig prDownloaderConfig = configBuilder.build();

        PRDownloader.initialize(getApplicationContext(), prDownloaderConfig);
    }

}
