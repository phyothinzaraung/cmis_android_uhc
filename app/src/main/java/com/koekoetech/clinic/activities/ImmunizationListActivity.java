package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.adapter.ImmunizationRVAdapter;
import com.koekoetech.clinic.model.ImmunizationModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ZMN on 3/28/18.
 **/
public class ImmunizationListActivity extends BaseActivity {

    public static final String RESULT_STATUS = "ImmunizationStatus";
    public static final String RESULT_SELECTED_IMMUNIZATIONS = "SelectedImmunization";

    public static final String STATUS_COMPLETE = "Completed";
    public static final String STATUS_INCOMPLETE = "Incomplete";

    private static final String EXTRA_PREV_IMMUNIZATIONS = "PreviousImmunizations";
    private static final String EXTRA_MODE = "Mode";
    private static final String MODE_NEW = "New";
    private static final String MODE_EDIT = "Edit";
    private static final String MODE_VIEW = "View";

    private static final String TAG = ImmunizationListActivity.class.getSimpleName();

    private RecyclerView immunizationsRV;

    String currentMode = MODE_NEW;

    private ImmunizationRVAdapter immunizationRVAdapter;

    public static Intent getNewIntent(Context context) {
        Intent intent = new Intent(context, ImmunizationListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_NEW);
        return intent;
    }

    public static Intent getEditIntent(Context context, String immunizationKV) {
        Intent intent = new Intent(context, ImmunizationListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        intent.putExtra(EXTRA_PREV_IMMUNIZATIONS, immunizationKV);
        return intent;
    }

    public static Intent getViewIntent(Context context, String immunizationKV) {
        Intent intent = new Intent(context, ImmunizationListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_VIEW);
        intent.putExtra(EXTRA_PREV_IMMUNIZATIONS, immunizationKV);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_immunization_list;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(true);
        setupToolbarText("Immunizations");

        bindViews();

        currentMode = getIntent().getStringExtra(EXTRA_MODE);
        Log.d(TAG, "setUpContents: Current Mode : " + currentMode);
        ArrayList<ImmunizationModel> immunizationModels;

        if (currentMode.equals(MODE_EDIT) || currentMode.equals(MODE_VIEW)) {
            immunizationModels = getImmunizationModels(getIntent().getStringExtra(EXTRA_PREV_IMMUNIZATIONS));
        } else {
            immunizationModels = getAllImmunizationModels();
        }

        if (immunizationModels.isEmpty()) {
            immunizationModels = getAllImmunizationModels();
        }

//        if(currentMode.equals(MODE_NEW) || currentMode.equals(MODE_EDIT)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        // Setup RecyclerView
        immunizationRVAdapter = new ImmunizationRVAdapter(immunizationModels, !currentMode.equals(MODE_VIEW));
        immunizationsRV.setAdapter(immunizationRVAdapter);

    }

    private void bindViews(){
        immunizationsRV = findViewById(R.id.activityImmunizationList_rvImmunizations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_immunizations, menu);
        MenuItem saveItem = menu.findItem(R.id.immunizations_save);
        saveItem.setVisible(!currentMode.equals(MODE_VIEW));
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return false;
        } else if (itemId == R.id.immunizations_save) {
            Intent results = new Intent();
            results.putExtra(RESULT_SELECTED_IMMUNIZATIONS, prepareImmunizationKV());
            results.putExtra(RESULT_STATUS, immunizationRVAdapter.isCompleted() ? STATUS_COMPLETE : STATUS_INCOMPLETE);
            setResult(Activity.RESULT_OK, results);
            finish();
            return false;
        }
        return super.onOptionsItemSelected(item);

    }

    private ArrayList<ImmunizationModel> getAllImmunizationModels() {
        ArrayList<ImmunizationModel> immunizationModels = new ArrayList<>();
        String[] immunizations = getResources().getStringArray(R.array.immunizations);
        for (String im : immunizations) {
            immunizationModels.add(new ImmunizationModel(im, false));
        }
        return immunizationModels;
    }

    private ArrayList<ImmunizationModel> getImmunizationModels(String immunizationKV) {
        Log.d(TAG, "getImmunizationModels() called with: immunizationKV = [" + immunizationKV + "]");
        ArrayList<ImmunizationModel> immunizationModels = new ArrayList<>();
        if (!TextUtils.isEmpty(immunizationKV)) {
            String[] splitStrings = TextUtils.split(immunizationKV, ";");
            for (String s : splitStrings) {
                if (!TextUtils.isEmpty(s)) {
                    Log.d(TAG, "getImmunizationModels: Adding " + s);
                    immunizationModels.add(new ImmunizationModel(s));
                }
            }
        }
        return immunizationModels;
    }

    private String prepareImmunizationKV() {
        StringBuilder sb = new StringBuilder();
        for (ImmunizationModel im : immunizationRVAdapter.getImmunizationModels()) {
            sb.append(im.toKVPair());
            sb.append(";");
        }
        String immunizationKV = sb.toString();
        Log.d(TAG, "prepareResult: Immunization KV " + immunizationKV);
        immunizationKV = immunizationKV.substring(0, immunizationKV.length() - 1);
        Log.d(TAG, "prepareResult() returned: " + immunizationKV);
        return immunizationKV;
    }

}
