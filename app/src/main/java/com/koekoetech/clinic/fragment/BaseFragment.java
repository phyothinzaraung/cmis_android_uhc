package com.koekoetech.clinic.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.GAHelper;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by ZMN on 10/10/17.
 **/

public abstract class BaseFragment extends Fragment {

    private boolean isActive;

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void onViewReady(View view, @Nullable Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewReady(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        String screenName = getAnalyticsScreenName();
        if (!TextUtils.isEmpty(screenName)) {
            GAHelper.sendScreenName(screenName,this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }

    protected void sendActionAnalytics(@NonNull String action) {
        String category = CmisGoogleAnalyticsConstants.getScreenName(getClass().getName());
        GAHelper.sendEvent(getContext(),category, action);
    }

    protected String getAnalyticsScreenName() {
        return CmisGoogleAnalyticsConstants.getScreenName(getClass().getName());
    }

    protected boolean isActive() {
        return isActive;
    }
}
