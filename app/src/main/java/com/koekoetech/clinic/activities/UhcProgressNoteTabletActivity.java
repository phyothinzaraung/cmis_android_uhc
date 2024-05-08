package com.koekoetech.clinic.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.fragment.UhcPatientProgressNoteCreateFragment;
import com.koekoetech.clinic.fragment.UhcPatientProgressNotesFragment;
import com.koekoetech.clinic.helper.keyboard.KeyboardVisibilityEvent;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNoteCreateFragmentCallback;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNotesFragmentCallBacks;
import com.koekoetech.clinic.model.UhcPatientProgress;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

import androidx.appcompat.app.AlertDialog;
import io.realm.Realm;

/**
 * Created by ZMN on 12/6/17.
 **/

public class UhcProgressNoteTabletActivity extends BaseActivity implements UhcPatientProgressNotesFragmentCallBacks, UhcPatientProgressNoteCreateFragmentCallback {

    private static final String TAG = UhcProgressNoteTabletActivity.class.getSimpleName();

    private static final String EXTRA_PATIENT_CODE = "PatientCode";
    private static final String EXTRA_PROGRESS_CODE = "ProgressCode";
    private static final String EXTRA_MODE = "mode";
    private static final String MODE_CREATE = "mode_create";
    private static final String MODE_EDIT = "mode_edit";

    private TextView emptyView;

    private UhcPatientProgressNotesFragment masterFragment;
    private UhcPatientProgressNoteCreateFragment detailFragment;

    public static Intent getCreateIntent(String patientCode, Context context) {
        Intent intent = new Intent(context, UhcProgressNoteTabletActivity.class);
        intent.putExtra(EXTRA_PATIENT_CODE, patientCode);
        intent.putExtra(EXTRA_MODE, MODE_CREATE);
        return intent;
    }

    public static Intent getEditIntent(String progressCode, Context context) {
        Intent intent = new Intent(context, UhcProgressNoteTabletActivity.class);
        intent.putExtra(EXTRA_PROGRESS_CODE, progressCode);
        intent.putExtra(EXTRA_MODE, MODE_EDIT);
        return intent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_uhc_progress_tablet;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        emptyView = findViewById(R.id.tv_empty_view);

        setupToolbar(true);
        Realm realm = Realm.getDefaultInstance();

        String currentMode = getIntent().getStringExtra(EXTRA_MODE);

        if (TextUtils.isEmpty(currentMode)){
            currentMode = MODE_CREATE;
        }

        masterFragment = (UhcPatientProgressNotesFragment) getSupportFragmentManager().findFragmentById(R.id.progress_notes_fragment);
        detailFragment = (UhcPatientProgressNoteCreateFragment) getSupportFragmentManager().findFragmentById(R.id.progress_note_create_fragment);

        masterFragment.registerFragmentCallBack(this);
        detailFragment.registerCallBack(this);

        KeyboardVisibilityEvent.setEventListener(this, detailFragment::onKeyboardOpenClose);

        switch (currentMode) {
            case MODE_CREATE:
                String patientCode = getIntent().getStringExtra(EXTRA_PATIENT_CODE);
                masterFragment.loadToCreate(patientCode);
                detailFragment.setProgressNotesTypes(masterFragment.getFilteredPressNoteType());
                detailFragment.setPatientCode(patientCode);
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
                    masterFragment.loadToEdit(progressEditSource);
                    detailFragment.setProgressNotesTypes(masterFragment.getFilteredPressNoteType());
                    detailFragment.setPatientCode(progressEditSource.getPatientCode());
                    invalidateOptionsMenu();
                }
                manageDetailEmptyView();
                break;
            default:

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_progress_notes_tablet, menu);
        MenuItem saveMenu = menu.findItem(R.id.action_save);
        MenuItem keepMenu = menu.findItem(R.id.action_keep);

        boolean shouldShowKeep = detailFragment.shouldShowSubmitOption() && detailFragment.doChangesExists();
        boolean shouldShowSave = masterFragment.hasChanges() || masterFragment.shouldShowSaveButton() && masterFragment.hasChanges();
        saveMenu.setVisible(shouldShowSave && !shouldShowKeep);
        keepMenu.setVisible(shouldShowKeep);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return false;
        } else if (itemId == R.id.action_keep) {
            masterFragment.addUpdateProgressNote(detailFragment.prepareProgressNote());
            detailFragment.reset();
            detailFragment.setProgressNotesTypes(masterFragment.getFilteredPressNoteType());
            invalidateOptionsMenu();
            manageDetailEmptyView();
            return false;
        } else if (itemId == R.id.action_save) {
            masterFragment.save();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (masterFragment.hasChanges() || detailFragment.doChangesExists()) {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> UhcProgressNoteTabletActivity.super.onBackPressed()).create().show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onToolbarTextChange(String title) {
        setupToolbarText(title);
    }

    @Override
    public void onProgressNoteDeleted() {
        invalidateOptionsMenu();
        manageDetailEmptyView();
        if (!(detailFragment.shouldShowSubmitOption() && detailFragment.doChangesExists())) {
            detailFragment.reset();
            detailFragment.setProgressNotesTypes(masterFragment.getFilteredPressNoteType());
        }
    }

    @Override
    public void onProgressNoteEdit(final UhcPatientProgressNoteViewModel editTarget) {
        if (detailFragment.shouldShowSubmitOption() && detailFragment.doChangesExists()) {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Unsaved changes in currently working progress note will be lost. Continue?")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> loadToEdit(editTarget))
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss()).show();
        } else {
            loadToEdit(editTarget);
        }
    }

    @Override
    public void onToolbarTitleChange(String title) {
        setupToolbarText(title);
    }

    @Override
    public void onContentStateChange() {
        invalidateOptionsMenu();
    }

    @Override
    public void onProgressCreatedTimeEdited() {
        invalidateOptionsMenu();
    }

    private void loadToEdit(UhcPatientProgressNoteViewModel editTarget) {
        if (detailFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().show(detailFragment).commit();
        }
        emptyView.setVisibility(View.GONE);
        detailFragment.reset();
        detailFragment.loadToEdit(editTarget);
    }

    private void manageDetailEmptyView() {
        if (masterFragment.getFilteredPressNoteType().isEmpty()) {
            detailFragment.reset();
            getSupportFragmentManager().beginTransaction().hide(detailFragment).commit();
            emptyView.setVisibility(View.VISIBLE);
        } else {
            if (detailFragment.isHidden()) {
                getSupportFragmentManager().beginTransaction().show(detailFragment).commit();
            }
            emptyView.setVisibility(View.GONE);
        }
    }
}
