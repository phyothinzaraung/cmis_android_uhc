package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.UhcPatientProgressNoteCreateFragment;
import com.koekoetech.clinic.helper.keyboard.KeyboardVisibilityEvent;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNoteCreateFragmentCallback;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by ZMN on 12/5/17.
 **/

public class UhcPatientProgressNoteCreateFragmentActivity extends BaseActivity implements UhcPatientProgressNoteCreateFragmentCallback {

    private static final String TAG = UhcPatientProgressNoteCreateFragmentActivity.class.getSimpleName();
    private static final String EXTRA_PATIENT_CODE = "PatientCode";
    private static final String EXTRA_PROGRESS_TYPES = "ProgressTypes";
    private static final String EXTRA_PROGRESS_NOTE_VIEW_MODEL = "ProgressNoteViewModel";

    private UhcPatientProgressNoteCreateFragment progressNoteCreateFragment;

    public static Intent getCreateIntent(Context context, ArrayList<String> progressTypes, String patientCode) {
        Intent intent = new Intent(context, UhcPatientProgressNoteCreateFragmentActivity.class);
        intent.putStringArrayListExtra(EXTRA_PROGRESS_TYPES, progressTypes);
        intent.putExtra(EXTRA_PATIENT_CODE, patientCode);
        return intent;
    }

    public static Intent getEditIntent(Context context, UhcPatientProgressNoteViewModel progressNoteViewModel) {
        Intent intent = new Intent(context, UhcPatientProgressNoteCreateFragmentActivity.class);
        intent.putExtra(EXTRA_PROGRESS_NOTE_VIEW_MODEL, progressNoteViewModel);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uhc_progress_notes_create;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        progressNoteCreateFragment = (UhcPatientProgressNoteCreateFragment) getSupportFragmentManager().findFragmentById(R.id.progress_note_create_fragment);
        progressNoteCreateFragment.registerCallBack(this);

        setupToolbar(true);
        setupToolbarText("Create Progress Note");

        String patientCode = getIntent().getStringExtra(EXTRA_PATIENT_CODE);
        progressNoteCreateFragment.setPatientCode(patientCode);

        ArrayList<String> progressNoteTypes = getIntent().getStringArrayListExtra(EXTRA_PROGRESS_TYPES);
        progressNoteCreateFragment.setProgressNotesTypes(progressNoteTypes);

        UhcPatientProgressNoteViewModel progressNoteViewModel = getIntent().getParcelableExtra(EXTRA_PROGRESS_NOTE_VIEW_MODEL);
        progressNoteCreateFragment.loadToEdit(progressNoteViewModel);

        KeyboardVisibilityEvent.setEventListener(this, progressNoteCreateFragment::onKeyboardOpenClose);

    }

    @Override
    public void onBackPressed() {
        if (progressNoteCreateFragment.doChangesExists()) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> UhcPatientProgressNoteCreateFragmentActivity.super.onBackPressed()).create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (progressNoteCreateFragment.shouldShowSubmitOption()) {
            getMenuInflater().inflate(R.menu.menu_pressnote, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            save();
            finish();
            return false;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onToolbarTitleChange(String title) {
        setupToolbarText(title);
    }

    @Override
    public void onContentStateChange() {
        invalidateOptionsMenu();
    }

    private void save() {
        if (progressNoteCreateFragment.doChangesExists()) {
            Intent data = new Intent();
            data.putExtra("progressNote", progressNoteCreateFragment.prepareProgressNote());
            setResult(Activity.RESULT_OK, data);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
    }
}
