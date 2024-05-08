package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.koekoetech.clinic.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Phyo Thinzar Aung on 11/29/2017.
 */

public class BarcodeScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler{

    public static final String SCANNED_UIC_CODE = "uic_code";


    private ViewGroup contentFrame;
    private ZXingScannerView mScannerView;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_simple_scanner;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        contentFrame = findViewById(R.id.content_frame);

        setupToolbar(true);
        setupToolbarText("Scan Bar Code");

        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        Intent intent = new Intent();
        intent.putExtra(SCANNED_UIC_CODE, result.getText());

        setResult(Activity.RESULT_OK,intent);
        finish();

//        // Note:
//        // * Wait 2 seconds to resume the preview.
//        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
//        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(BarcodeScannerActivity.this);
//            }
//        }, 2000);
    }
}
