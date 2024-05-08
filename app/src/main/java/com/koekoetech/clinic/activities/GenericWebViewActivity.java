package com.koekoetech.clinic.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.PrerequisiteCheckingsHelper;


/**
 * Created by Zaw Myo Naing on 7/26/2018.
 **/
public class GenericWebViewActivity extends BaseActivity {

    private static final String TAG = GenericWebViewActivity.class.getSimpleName();
    private static final String BLANK_URL = "about:blank";
    private static final String EXTRA_WEB_URL = "web_url_to_load";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_CACHE_ENABLED = "isCacheEnabled";

    private WebView wvGeneric;
    private ProgressBar wvProgress;
    private RelativeLayout wvErrorLayout;

    private String urlToLoad;
    private boolean isCacheEnabled;

    public static Intent getNewIntent(String url, String title, boolean cacheEnable, Context context) {
        Intent webViewIntent = new Intent(context, GenericWebViewActivity.class);
        webViewIntent.putExtra(EXTRA_WEB_URL, url);
        webViewIntent.putExtra(EXTRA_TITLE, title);
        webViewIntent.putExtra(EXTRA_CACHE_ENABLED, cacheEnable);
        return webViewIntent;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {
        setupToolbar(true);

        bindViews();

        urlToLoad = getIntent().getStringExtra(EXTRA_WEB_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        isCacheEnabled = getIntent().getBooleanExtra(EXTRA_CACHE_ENABLED, false);

        setupToolbar(true);
        setupToolbarText(title);

        init();
        loadWeb();
    }

    private void bindViews(){
        wvGeneric = findViewById(R.id.activity_genericWv);
        wvProgress = findViewById(R.id.activity_genericWvProgress);
        wvErrorLayout = findViewById(R.id.activity_genericWebViewErrorView);
    }

    @Override
    protected void onDestroy() {
        wvGeneric.setWebViewClient(null);
        super.onDestroy();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_generic_web_view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                navigateWebViewHistory();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        wvErrorLayout.setOnClickListener(v -> loadWeb());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWeb() {
        wvGeneric.postDelayed(() -> {

            if (isCacheEnabled) {
                manageCacheLoading();
            }

            wvGeneric.getSettings().setDomStorageEnabled(true);
            wvGeneric.getSettings().setJavaScriptEnabled(true);
            wvGeneric.loadUrl(urlToLoad);
            wvGeneric.setHorizontalScrollBarEnabled(false);
            wvGeneric.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.i(TAG, "shouldOverrideUrlLoading: " + url);
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    Log.d(TAG, "onPageStarted() called with: url = [" + url + "]");
                    wvProgress.setVisibility(View.VISIBLE);
                    wvGeneric.setVisibility(View.VISIBLE);
                    wvErrorLayout.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    wvProgress.setVisibility(View.GONE);
                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    Log.e(TAG, "onReceivedError: fail!");

                    if (wvGeneric != null) {

                        try {
                            wvGeneric.stopLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (wvGeneric.canGoBack()) {
                            wvGeneric.goBack();
                        }

                        wvGeneric.loadUrl(BLANK_URL);

                        wvProgress.setVisibility(View.GONE);
                        wvGeneric.setVisibility(View.GONE);
                        wvErrorLayout.setVisibility(View.VISIBLE);
                        super.onReceivedError(view, request, error);
                    }

                }
            });

        }, 500);
    }

    private void navigateWebViewHistory() {

        if (!PrerequisiteCheckingsHelper.isInternetAvailable(this)) {
            Log.d(TAG, "navigateWebViewHistory: No Internet!");
            finish();
            return;
        }

        WebBackForwardList webBackForwardList = wvGeneric.copyBackForwardList();
        int index = -1;
        String url = "";
        String currentUrl = "";

        WebHistoryItem currentItem = webBackForwardList.getCurrentItem();
        if (currentItem != null) {
            currentUrl = currentItem.getUrl();
        }

        while (wvGeneric.canGoBackOrForward(index)) {
            int targetIndex = webBackForwardList.getCurrentIndex() + index;
            WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(targetIndex);
            String targetUrl = webHistoryItem.getUrl();
            Log.d(TAG, "navigateWebViewHistory: Target URL : " + targetUrl);
            Log.d(TAG, "navigateWebViewHistory: Current URL : " + currentUrl);
            Log.d(TAG, "navigateWebViewHistory: Index : " + index);
            boolean isBlankPage = TextUtils.equals(targetUrl, BLANK_URL);
            boolean isCurrentUrl = TextUtils.equals(targetUrl, currentUrl);
            if (!isBlankPage && !isCurrentUrl) {
                Log.d(TAG, "navigateWebViewHistory: Found Non-blank and different URL");
                wvGeneric.goBackOrForward(index);
                url = webBackForwardList.getItemAtIndex(-index).getUrl();
                break;
            }

            index--;
        }

        if (TextUtils.isEmpty(url)) {
            finish();
        }

    }

    private void manageCacheLoading() {
        if (PrerequisiteCheckingsHelper.isInternetAvailable(this)) {
            wvGeneric.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            wvGeneric.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

}
