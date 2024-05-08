package com.koekoetech.clinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.SharePreferenceHelper;

import androidx.core.content.ContextCompat;

public class CustomPassActivity extends BaseActivity implements PinLockListener {

    private static final String TAG = CustomPassActivity.class.getSimpleName();

    private Button btnSkip;

    private TextView tvPinMsg;

    private IndicatorDots pinLockIndicatorDots;

    private PinLockView pinLockView;

    private LinearLayout passcodeFooterContainer;

    SharePreferenceHelper preferenceHelper;
    private String passCode;
    private boolean hasError = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_custom_pass;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        bindViews();

        preferenceHelper = SharePreferenceHelper.getHelper(CustomPassActivity.this);

        passCode = preferenceHelper.getPasscode();

        pinLockView.attachIndicatorDots(pinLockIndicatorDots);
        pinLockView.setPinLockListener(this);

        btnSkip.setOnClickListener(v -> {
            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_SKIP_PASS_CODE);
            preferenceHelper.setNurse();
            goToHome();
        });

        passcodeFooterContainer.setOnClickListener(v -> {
            startActivity(GenericWebViewActivity.getNewIntent("http://www.koekoetech.com", "Koe Koe Tech", true, this));
        });

    }

    private void bindViews(){
        btnSkip = findViewById(R.id.btnSkip);
        tvPinMsg = findViewById(R.id.tv_pin_msg);
        pinLockIndicatorDots = findViewById(R.id.indicator_dots);
        pinLockView = findViewById(R.id.pin_lock_view);
        passcodeFooterContainer = findViewById(R.id.pass_code_footer_container);
    }

    @Override
    public void onComplete(String pin) {
        Log.d(TAG, "onComplete() called with: pin = [" + pin + "]");
        if (TextUtils.equals(pin, passCode)) {
            preferenceHelper.setDoctor();
            goToHome();
        } else {
            pinLockView.resetPinLockView();
            showError();
        }
    }

    @Override
    public void onEmpty() {
        Log.d(TAG, "onEmpty() called");
    }

    @Override
    public void onPinChange(int pinLength, String intermediatePin) {
        Log.d(TAG, "onPinChange() called with: pinLength = [" + pinLength + "], intermediatePin = [" + intermediatePin + "]");
        if (hasError) {
            clearError();
        }
    }

    private void goToHome() {
        Intent intent = new Intent(CustomPassActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void showError() {
        hasError = true;
        tvPinMsg.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        tvPinMsg.setText(R.string.hint_invalid_pass_code);
    }

    private void clearError() {
        hasError = false;
        tvPinMsg.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
        tvPinMsg.setText(R.string.hint_enter_pass_code);

    }

}
