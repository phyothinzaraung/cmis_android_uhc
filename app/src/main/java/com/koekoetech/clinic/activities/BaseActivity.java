package com.koekoetech.clinic.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.GAHelper;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by hello on 10/13/16.
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class BaseActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        toolbar = findViewById(R.id.toolbar);
        setUpContents(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String screenName = getAnalyticsScreenName();
        if(!TextUtils.isEmpty(screenName)){
            GAHelper.sendScreenName(screenName,this);
        }
    }

    protected void setupToolbar(boolean isChild) {

        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (isChild) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected void setupToolbarText(String text) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(text);
        }
    }

    protected void setupToolbarBgColor(String color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(color));
        }
    }

    protected void setupToolbarTextColor(String color) {
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.parseColor(color));
        }
    }

    protected String getAnalyticsScreenName(){
        return CmisGoogleAnalyticsConstants.getScreenName(getClass().getName());
    }

    protected void sendActionAnalytics(@NonNull String action) {
        String category = CmisGoogleAnalyticsConstants.getScreenName(getClass().getName());
        GAHelper.sendEvent(this,category, action);
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void setUpContents(Bundle savedInstanceState);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

}
