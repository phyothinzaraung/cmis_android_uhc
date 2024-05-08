package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.UhcPatientProgressNotesFragment;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNotesFragmentCallBacks;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import io.realm.Realm;

/**
 * Created by ZMN on 11/30/17.
 **/

public class UhcPatientProgressNotesFragmentActivity extends BaseActivity implements UhcPatientProgressNotesFragmentCallBacks {

    private static final String TAG = UhcPatientProgressNotesFragmentActivity.class.getSimpleName();

    private static final String EXTRA_PATIENT_CODE = "PatientCode";
    private static final String EXTRA_PROGRESS_CODE = "ProgressCode";
    private static final String EXTRA_MODE = "mode";
    private static final String MODE_CREATE = "mode_create";
    private static final String MODE_EDIT = "mode_edit";

    private static final int REQ_CREATE_PROGRESS_NOTE = 7865;

    private FloatingActionButton fabCreatePressNote;

    private String currentMode;

    private String patientCode;

    private UhcPatientProgressNotesFragment progressNotesFragment;

    public static Intent getCreateIntent(String patientCode, Context context) {
        Intent intent = new Intent(context, UhcPatientProgressNotesFragmentActivity.class);
        intent.putExtra(EXTRA_PATIENT_CODE, patientCode);
        intent.putExtra(EXTRA_MODE, MODE_CREATE);
        return intent;
    }

    public static Intent getEditIntent(String progressCode, Context context) {
        Intent intent = new Intent(context, UhcPatientProgressNotesFragmentActivity.class);
        intent.putExtra(EXTRA_PROGRESS_CODE, progressCode);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        return intent;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uhc_progress_notes;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        fabCreatePressNote = findViewById(R.id.fab_create_press_note);

        setupToolbar(true);

        Realm realm = Realm.getDefaultInstance();
        currentMode = getIntent().getStringExtra(EXTRA_MODE);

        progressNotesFragment = (UhcPatientProgressNotesFragment) getSupportFragmentManager().findFragmentById(R.id.progress_notes_fragment);
        progressNotesFragment.registerFragmentCallBack(this);

        switch (currentMode) {
            case MODE_CREATE:
                patientCode = getIntent().getStringExtra(EXTRA_PATIENT_CODE);

                if (TextUtils.isEmpty(patientCode)) {
                    Toast.makeText(this, "Got Invalid Data!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                progressNotesFragment.loadToCreate(patientCode);
                Intent createIntent = UhcPatientProgressNoteCreateFragmentActivity.getCreateIntent(this, progressNotesFragment.getFilteredPressNoteType(), patientCode);
                startActivityForResult(createIntent, REQ_CREATE_PROGRESS_NOTE);
                break;
            case MODE_EDIT:
                String progressCode = getIntent().getStringExtra(EXTRA_PROGRESS_CODE);
                UhcPatientProgress progressEditSource = realm.where(UhcPatientProgress.class)
                        .equalTo("progressCode", progressCode)
                        .equalTo("isDeleted", false)
                        .findFirst();
                if (progressEditSource == null) {
                    Toast.makeText(this, "Corrupt Data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                } else {
                    patientCode = progressEditSource.getPatientCode();

                    if (TextUtils.isEmpty(patientCode)) {
                        Toast.makeText(this, "Got Invalid Data!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    progressNotesFragment.loadToEdit(progressEditSource);
                    manageFabVisibility();
                    invalidateOptionsMenu();
                }
                break;
            default:
                Toast.makeText(this, "Got Invalid Data!", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }


        fabCreatePressNote.setOnClickListener(v -> {
            Intent createIntent = UhcPatientProgressNoteCreateFragmentActivity.getCreateIntent(getApplicationContext(), progressNotesFragment.getFilteredPressNoteType(), patientCode);
            startActivityForResult(createIntent, REQ_CREATE_PROGRESS_NOTE);
        });

    }

    @Override
    public void onBackPressed() {
        if (progressNotesFragment.hasChanges()) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> UhcPatientProgressNotesFragmentActivity.super.onBackPressed()).create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        hideKeyboard();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_created_pressnotes, menu);
        MenuItem saveMenu = menu.findItem(R.id.action_commit);
        saveMenu.setVisible(progressNotesFragment.hasChanges() || progressNotesFragment.shouldShowSaveButton() && progressNotesFragment.hasChanges());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return false;
        } else if (itemId == R.id.action_commit) {
            progressNotesFragment.save();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CREATE_PROGRESS_NOTE) {
            if (resultCode == Activity.RESULT_OK) {
                UhcPatientProgressNoteViewModel progressNoteViewModel = data.getParcelableExtra("progressNote");
                progressNotesFragment.addUpdateProgressNote(progressNoteViewModel);
                manageFabVisibility();
                invalidateOptionsMenu();
                Toast.makeText(this, "Progress Note Kept Successfully", Toast.LENGTH_SHORT).show();
                if (currentMode.equals(MODE_CREATE)) {
                    ArrayList<String> filteredPressNotes = progressNotesFragment.getFilteredPressNoteType();
                    if (!filteredPressNotes.isEmpty()) {
                        Intent createIntent = UhcPatientProgressNoteCreateFragmentActivity.getCreateIntent(getApplicationContext(), filteredPressNotes, patientCode);
                        startActivityForResult(createIntent, REQ_CREATE_PROGRESS_NOTE);
                    }
                }
            } else {
                Log.d(TAG, "onActivityResult: RESULT CANCELED");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onToolbarTextChange(String title) {
        setupToolbarText(title);
    }

    @Override
    public void onProgressNoteDeleted() {
        manageFabVisibility();
        invalidateOptionsMenu();
    }

    @Override
    public void onProgressNoteEdit(UhcPatientProgressNoteViewModel editTarget) {
        startActivityForResult(UhcPatientProgressNoteCreateFragmentActivity.getEditIntent(getApplicationContext(), editTarget), REQ_CREATE_PROGRESS_NOTE);
    }

    @Override
    public void onProgressCreatedTimeEdited() {
        invalidateOptionsMenu();
    }

    private void manageFabVisibility() {
        if (progressNotesFragment.getFilteredPressNoteType().isEmpty()) {
            fabCreatePressNote.hide();
        } else {
            fabCreatePressNote.show();
        }
    }

    private void hideKeyboard() {
        //hide window soft input method
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
