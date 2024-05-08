package com.koekoetech.clinic.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.ICDChooserActivity;
import com.koekoetech.clinic.adapter.PressNoteAdapter;
import com.koekoetech.clinic.adapter.PressNoteSubjectWordAdapter;
import com.koekoetech.clinic.adapter.UhcPatientProgressNotePhotoAdapter;
import com.koekoetech.clinic.helper.AddNewSuggestionDialogHelper;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.CodeGen;
import com.koekoetech.clinic.helper.FileHelper;
import com.koekoetech.clinic.helper.ImageCompressor;
import com.koekoetech.clinic.helper.LCAUiTask;
import com.koekoetech.clinic.helper.PermissionHelper;
import com.koekoetech.clinic.helper.PhotoUtils;
import com.koekoetech.clinic.helper.PressNoteTypeHelper;
import com.koekoetech.clinic.helper.TimeUtils;
import com.koekoetech.clinic.interfaces.PressNoteCallback;
import com.koekoetech.clinic.interfaces.PressNoteTextCallback;
import com.koekoetech.clinic.interfaces.UhcPatientProgressNoteCreateFragmentCallback;
import com.koekoetech.clinic.interfaces.UhcProgressNotePhotoCallback;
import com.koekoetech.clinic.model.PressNoteModel;
import com.koekoetech.clinic.model.SuggestionWordModel;
import com.koekoetech.clinic.model.UhcPatient;
import com.koekoetech.clinic.model.UhcPatientProgressNote;
import com.koekoetech.clinic.model.UhcPatientProgressNotePhoto;
import com.koekoetech.clinic.model.UhcPatientProgressNoteViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import needle.Needle;

/**
 * Created by ZMN on 12/5/17.
 **/

public class UhcPatientProgressNoteCreateFragment extends BaseFragment implements PressNoteCallback, PressNoteTextCallback, UhcProgressNotePhotoCallback, AddNewSuggestionDialogHelper.SuggestionCreatedListener {

    private static final String TAG = UhcPatientProgressNoteCreateFragment.class.getSimpleName();

    private static final int REQ_ICD_TERMS = 812;


    private EditText editTextPressNoteContent;
    private TextView txtProgressNoteFreeText;
    private FrameLayout layout_freeText;
    private TabLayout tabLayoutInputType;
    private RecyclerView recyclerViewPressNoteTypes;
    private CardView rvPressNoteTypeContainerCard;
    private RecyclerView recyclerViewPressNotePhotos;
    private TagContainerLayout pressNoteWordsContainer;
    private ScrollView pressNoteWordsScrollView;
    private RecyclerView rv_press_notes_subject;
    private CardView rvPressNoteSubjectContainerCard;

    private boolean doChangesExists;

    private String patientCode;
    private String selectedPressNoteType;
    private PressNoteAdapter pressnoteAdapter;
    private UhcPatientProgressNotePhotoAdapter photoAdapter;
    private UhcPatientProgressNote progressNoteEditSource;
    private PressNoteSubjectWordAdapter pressNoteSubjectWordAdapter;
    private AppProgressDialog progressDialog;

    private InputMethodManager inputMethodManager;
    private KeyListener keyListener;

    private ArrayList<String> progressNoteTypes;
    private Realm realm;

    private UhcPatientProgressNoteCreateFragmentCallback callback;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_create_progress_note;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "UhcPatientProgressNoteCreateFragment:onCreate: called");
    }

    private void allFindViewById(@NonNull View root) {
        editTextPressNoteContent = root.findViewById(R.id.edt_press_note_content);
        txtProgressNoteFreeText = root.findViewById(R.id.txt_progressnote_freetext);
        layout_freeText = root.findViewById(R.id.layout_freeText);
        tabLayoutInputType = root.findViewById(R.id.tab_layout_input_type);
        recyclerViewPressNoteTypes = root.findViewById(R.id.rv_press_notes_types);
        rvPressNoteTypeContainerCard = root.findViewById(R.id.press_notes_types_container_card);
        recyclerViewPressNotePhotos = root.findViewById(R.id.rv_press_notes_photos);
        pressNoteWordsContainer = root.findViewById(R.id.press_note_word_container);
        pressNoteWordsScrollView = root.findViewById(R.id.press_note_word_scroll_layout);
        rv_press_notes_subject = root.findViewById(R.id.rv_press_notes_subject);
        rvPressNoteSubjectContainerCard = root.findViewById(R.id.subject_container_card);
    }

    @Override
    protected void onViewReady(View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "UhcPatientProgressNoteCreateFragment:onViewReady: called");

        allFindViewById(view);

        realm = Realm.getDefaultInstance();

        /* ----------------------------- [START] Initial View Setups ----------------------------- */

        // Setup PressNoteType RecyclerView
        pressnoteAdapter = new PressNoteAdapter();
        pressnoteAdapter.registerCallback(this);
        recyclerViewPressNoteTypes.setAdapter(pressnoteAdapter);

        // Setup PressNote EditText
        keyListener = editTextPressNoteContent.getKeyListener();
        inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextPressNoteContent.setOnClickListener(v -> {
            if (TextUtils.isEmpty(selectedPressNoteType)) {
                Toast.makeText(getContext(), "Please Select Press Note Type!", Toast.LENGTH_SHORT).show();
            }
        });
        editTextPressNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (callback != null) {
                    callback.onContentStateChange();
                }

                if (progressNoteEditSource != null) {
                    doChangesExists = !(s.toString().equals(progressNoteEditSource.getNote()));
                } else {
                    doChangesExists = !TextUtils.isEmpty(s);
                }
            }
        });

        // Setup InputTypeTabLayout
        tabLayoutInputType.addOnTabSelectedListener(new InputTabSelectedListener());

        // Setup PressNoteSubjectWord RecyclerView
        pressNoteSubjectWordAdapter = new PressNoteSubjectWordAdapter();
        pressNoteSubjectWordAdapter.registerCallback(this);
        rv_press_notes_subject.setAdapter(pressNoteSubjectWordAdapter);

        // Setup Photo RecyclerView
        photoAdapter = new UhcPatientProgressNotePhotoAdapter(true, false);
        photoAdapter.registerPhotoCallback(this);
        recyclerViewPressNotePhotos.setHasFixedSize(true);
        recyclerViewPressNotePhotos.setNestedScrollingEnabled(false);
        recyclerViewPressNotePhotos.setAdapter(photoAdapter);

        // Free Note TextView Setup
        txtProgressNoteFreeText.setMovementMethod(new ScrollingMovementMethod());

        /* ----------------------------- [END] Initial View Setups ----------------------------- */

        populatePressNotes();

        updateViews();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: UHCRegistrationModel Code " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.IMAGE_REQUEST_CODE:
                    doChangesExists = true;
                    File selectedPhoto = PhotoUtils.getPickImageResultFile(data, requireContext());
                    Log.i(TAG, "onActivityResult: Selected Photo Path : " + selectedPhoto);
                    progressDialog = AppProgressDialog.getProgressDialog("Preparing Image");
                    if (!progressDialog.isDisplaying()) {
                        progressDialog.display(requireActivity().getSupportFragmentManager());
                    }
                    Needle.onBackgroundThread().execute(new LCAUiTask<String>(getViewLifecycleOwner()) {

                        @Override
                        protected String doWork() {
                            if (selectedPhoto == null || !selectedPhoto.exists()) {
                                return null;
                            }
                            @Nullable final Context context = getContext();
                            if (context == null) {
                                return null;
                            }

                            try {
                                final File compressedImage = new ImageCompressor(context)
                                        .setQuality(50)
                                        .compressToFile(selectedPhoto);

                                if (compressedImage != null && compressedImage.exists()) {
                                    final String extension = FileHelper.getExtension(compressedImage.getAbsolutePath());
                                    final String currentTime = PhotoUtils.getCurrentTime();
                                    final String fileId = "PressNote_"
                                            + selectedPressNoteType
                                            + "_"
                                            + currentTime
                                            + FileHelper.EXTENSION_SEPARATOR + extension;

                                    final File containerDirectory = new File(FileHelper.getStorageDir(context), patientCode);
                                    if (!containerDirectory.exists()) {
                                        final boolean isDirectoryCreated = containerDirectory.mkdirs();
                                        Log.d(TAG, "doWork: Create directory : " + containerDirectory.getAbsolutePath() + " : " + isDirectoryCreated);
                                    }

                                    final File photoFile = new File(containerDirectory, fileId);
                                    if (compressedImage.renameTo(photoFile)) {
                                        Log.d(TAG, "doWork: " + compressedImage.getAbsolutePath() + " is renamed to " + photoFile.getAbsolutePath());
                                        if (photoFile.exists()) {
                                            return photoFile.getAbsolutePath();
                                        }
                                    }

                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void thenDoUiRelatedWork(@Nullable String filePath) {
                            if (progressDialog.isDisplaying()) {
                                progressDialog.safeDismiss();
                            }
                            if (!TextUtils.isEmpty(filePath)) {
                                UhcPatientProgressNotePhoto pickedPhoto = new UhcPatientProgressNotePhoto();
                                pickedPhoto.setLocalId(UUID.randomUUID().toString());
                                pickedPhoto.setPhotoNameId(UUID.randomUUID().toString());
                                pickedPhoto.setCreatedTime(TimeUtils.now());
                                pickedPhoto.setHasChanges(true);
                                pickedPhoto.setOnline(false);
                                pickedPhoto.setDeleted(false);
                                pickedPhoto.setPhoto(filePath);
                                pickedPhoto.setType(selectedPressNoteType);
                                photoAdapter.addPhoto(pickedPhoto);
                                sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_TAKE_PROGRESS_NOTE_PHOTO);
                                if (callback != null) {
                                    callback.onContentStateChange();
                                }
                            } else {
                                Log.d(TAG, "onSaved: Didn't Received Compressed File Path");
                            }
                        }
                    });

                    break;
                case REQ_ICD_TERMS:
                    if (data != null) {
                        String selectedICDTerms = data.getStringExtra(ICDChooserActivity.EXTRA_SELECTED_ICD);
                        if (!TextUtils.isEmpty(selectedICDTerms)) {
                            Log.d(TAG, "onActivityResult: " + selectedICDTerms);
                            String enteredNotes = editTextPressNoteContent.getText().toString();
                            Log.d(TAG, "onActivityResult: Entered Notes : " + enteredNotes);
                            if (!TextUtils.isEmpty(enteredNotes) && !enteredNotes.endsWith(";\n")) {
                                selectedICDTerms = ";\n" + selectedICDTerms;
                            }
                            Spannable selectedICDTermsSpannable = AppUtils.getProgressNoteKeyValueSpannable(selectedICDTerms);
                            editTextPressNoteContent.append(selectedICDTermsSpannable);
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.REQ_PERMISSIONS) {

            //check if all permissions are granted
            boolean isAllGranted = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = true;
                } else {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                getImage();
            } else if (PermissionHelper.shouldShowRationlaeDialog(requireActivity())) {
                PermissionHelper.getCameraStorageRationaleDialogBuilder(requireActivity()).show();
            } else {
                PermissionHelper.getPermissionFailureDialogBuilder(requireActivity()).show();
            }
        }
    }

    @Override
    public void onPressNoteSelected(String pressNoteType) {
        boolean hasEnteredPressNoteContent = !TextUtils.isEmpty(editTextPressNoteContent.getText().toString().trim());
        boolean hasCapturedPhotos = photoAdapter.getItemCount() != 0;
        boolean hasEnteredFreeNote = !TextUtils.isEmpty(txtProgressNoteFreeText.getText().toString().trim());

        if (!hasEnteredPressNoteContent && !hasCapturedPhotos && !hasEnteredFreeNote) {
            doChangesExists = true;
            selectedPressNoteType = pressNoteType;
            refreshSuggestionsTags();
            updateViews();

            UhcPatientProgressNote progressNote = new UhcPatientProgressNote();
            progressNote.setType(selectedPressNoteType);
            setupInputTypeTabsLayout();
//            TabLayout.Tab wordsTab = tabLayoutInputType.getTabAt(2);
//            if (wordsTab != null) {
//                if (pressNoteType.equals("Diagnosis")) {
//                    wordsTab.setText("General illness");
//                } else {
//                    wordsTab.setText("Words - " + pressNoteType);
//                }
//            }
        } else {
            pressnoteAdapter.select(selectedPressNoteType);
            Toast.makeText(getActivity(), "You can't select other progress note until it isn't finished", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void sendSuggestionText(String text) {
        Log.d(TAG, "sendSuggestionText() called with: text = [" + text + "]");
        String edtContent = editTextPressNoteContent.getText().toString().trim();
        String target = "";
        if (!TextUtils.isEmpty(edtContent)) {
            if (edtContent.endsWith(":")) {
                target = " " + text.trim();
            } else {
                target = "," + " " + text.trim();
            }
        }

        if (!TextUtils.isEmpty(target)) {
            editTextPressNoteContent.append(target);
        }
    }

    @Override
    public void onDeleted(int position) {
        doChangesExists = true;
        if (callback != null) {
            callback.onContentStateChange();
        }
    }

    @Override
    public void onSuggestionCreated() {
        refreshSuggestionsTags();
        sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_ADD_NEW_SUGGESTION_WORD);
    }

    public void setProgressNotesTypes(ArrayList<String> types) {
        this.progressNoteTypes = types;
        populatePressNotes();
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public void registerCallBack(UhcPatientProgressNoteCreateFragmentCallback createFragmentCallback) {
        this.callback = createFragmentCallback;
    }

    public void onKeyboardOpenClose(boolean isOpen) {
        int visibility = isOpen ? View.GONE : View.VISIBLE;
        pressNoteWordsScrollView.setVisibility(visibility);
        if (!TextUtils.isEmpty(txtProgressNoteFreeText.getText())) {
            layout_freeText.setVisibility(visibility);
        } else {
            layout_freeText.setVisibility(View.GONE);
        }
    }

    public boolean shouldShowSubmitOption() {
        return !(photoAdapter.getItemCount() == 0 && TextUtils.isEmpty(editTextPressNoteContent.getText().toString().trim()));
    }

    public void reset() {
        selectedPressNoteType = "";
        editTextPressNoteContent.setText("");
        pressnoteAdapter.clear();
        pressnoteAdapter.resetSelection();
        photoAdapter.clear();
        pressNoteSubjectWordAdapter.clear();
        txtProgressNoteFreeText.setText("");
        updateViews();
    }

    public UhcPatientProgressNoteViewModel prepareProgressNote() {

        UhcPatientProgressNote progressNote;
        if (progressNoteEditSource == null) {
            progressNote = new UhcPatientProgressNote();
            progressNote.setLocalId(UUID.randomUUID().toString());
            progressNote.setCreatedTime(TimeUtils.now());
            progressNote.setProgressNoteCode(CodeGen.generateProgressNoteCode());
        } else {
            progressNote = new UhcPatientProgressNote(progressNoteEditSource);
        }

        progressNote.setNote(editTextPressNoteContent.getText().toString().trim());
        progressNote.setFreeNote(txtProgressNoteFreeText.getText().toString());
        progressNote.setType(selectedPressNoteType);

        UhcPatientProgressNoteViewModel viewModel = new UhcPatientProgressNoteViewModel();
        viewModel.setProgressNote(progressNote);
        ArrayList<UhcPatientProgressNotePhoto> photos = new ArrayList<>();
        for (UhcPatientProgressNotePhoto p : photoAdapter.getPhotos()) {
            p.setProgressNoteCode(progressNote.getProgressNoteCode());
            p.setType(progressNote.getType());
            photos.add(p);
        }
        viewModel.setPhotos(photos);
        return viewModel;
    }

    private void populatePressNotes() {
        Log.d(TAG, "populatePressNotes: PressNoteTypes : " + progressNoteTypes);
        if (progressNoteTypes == null || progressNoteTypes.isEmpty()) {
            rvPressNoteTypeContainerCard.setVisibility(View.GONE);
            return;
        }

        pressnoteAdapter.clear();
        rvPressNoteTypeContainerCard.setVisibility(View.VISIBLE);
        PressNoteTypeHelper typeHelper = PressNoteTypeHelper.getTypeHelper();
        for (String pst : progressNoteTypes) {
            pressnoteAdapter.add(typeHelper.getPressNoteModelByType(pst));
        }
    }

    private void showAddFreeTextDialog() {
        recyclerViewPressNotePhotos.setVisibility(View.GONE);
        pressNoteWordsScrollView.setVisibility(View.GONE);
        editTextPressNoteContent.setEnabled(false);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());

        final View dialogView = View.inflate(requireContext(), R.layout.custom_dialog_addfreetext, null);
        alertDialog.setView(dialogView);

        final EditText editText_freeText = dialogView.findViewById(R.id.edt_freetext);

        if (!TextUtils.isEmpty(txtProgressNoteFreeText.getText().toString())) {
            editText_freeText.setText(txtProgressNoteFreeText.getText().toString());
            editText_freeText.setSelection(editText_freeText.length());
        } else {
            editText_freeText.setText("");
        }

        alertDialog.setTitle("Enter your free text");

        alertDialog.setPositiveButton("Add", (dialog, which) -> {

            String newFreeText = editText_freeText.getText().toString().trim();

            if (progressNoteEditSource != null) {
                doChangesExists = !(newFreeText.equals(progressNoteEditSource.getFreeNote()));
            } else {
                doChangesExists = !TextUtils.isEmpty(newFreeText);
            }

            if (callback != null) {
                callback.onContentStateChange();
            }

            txtProgressNoteFreeText.setText(editText_freeText.getText().toString().trim());
            dialog.dismiss();

            if (!txtProgressNoteFreeText.getText().toString().isEmpty()) {
                layout_freeText.setVisibility(View.VISIBLE);
            } else {
                layout_freeText.setVisibility(View.GONE);
            }
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void setupInputTypeTabsLayout() {
        Log.d(TAG, "UhcPatientProgressNoteCreateFragment:setupInputTypeTabsLayout: called");

        tabLayoutInputType.removeAllTabs();
        tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("Note"), false);
        tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("Text"), false);
        String wordsLabel = "Words";
        if (!TextUtils.isEmpty(selectedPressNoteType)) {
            if (selectedPressNoteType.equals("Diagnosis")) {
                wordsLabel = "General Illness";
            } else {
                wordsLabel += " - " + selectedPressNoteType;
            }
        }
        tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText(wordsLabel), true);

        boolean isDiagnosis = !TextUtils.isEmpty(selectedPressNoteType) && selectedPressNoteType.equalsIgnoreCase("Diagnosis");

        if (isDiagnosis) {
            tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("NCDs"), false);
            UhcPatient patient = realm.where(UhcPatient.class).equalTo("patientCode", patientCode).findFirst();
            if (patient != null && patient.isUHC()) {
                tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("Child Health"), false);
                tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("CDs"), false);

            }

            tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("ICD-10"), false);
        }

        tabLayoutInputType.addTab(tabLayoutInputType.newTab().setText("Photo"), false);
        tabLayoutInputType.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    private void hideKeyboard() {
        View currentFocus = requireActivity().getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void refreshSuggestionsTags() {
        ArrayList<String> suggestionsListByPressNoteType = getSuggestionsListByPressNoteType(selectedPressNoteType);
        suggestionsListByPressNoteType.add("Add New");
        ArrayList<int[]> tagColors = new ArrayList<>();
        int normalTagBackground = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark);
        int addNewTagBackground = ContextCompat.getColor(requireContext(), R.color.color_investigation_dark);

        for (PressNoteModel pnm : PressNoteTypeHelper.getTypeHelper().getPressNotesList()) {
            if (pnm.getTitle().equals(selectedPressNoteType)) {
                addNewTagBackground = Color.parseColor(pnm.getPreFixTextColor());
                break;
            }
        }

        int tagTextColor = ContextCompat.getColor(requireContext(), android.R.color.white);

        int[] normalTagColor = {normalTagBackground, normalTagBackground, tagTextColor, normalTagBackground};
        int[] addNewTagColor = {addNewTagBackground, addNewTagBackground, tagTextColor, addNewTagBackground,};

        for (int i = 0; i < suggestionsListByPressNoteType.size(); i++) {
            tagColors.add(normalTagColor);
        }

        tagColors.remove(tagColors.size() - 1);
        tagColors.add(addNewTagColor);

        pressNoteWordsContainer.setTags(suggestionsListByPressNoteType, tagColors);

        pressNoteWordsContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                Log.d(TAG, "onTagClick() called with: position = [" + position + "], text = [" + text + "]");
                if (text.equals("Add New")) {
                    AddNewSuggestionDialogHelper dialogHelper = new AddNewSuggestionDialogHelper(getActivity());
                    dialogHelper.setSuggestionType(selectedPressNoteType);
                    dialogHelper.setSuggestionCreatedListener(UhcPatientProgressNoteCreateFragment.this);
                    dialogHelper.showAddNewSuggestionDialog(getLayoutInflater());
                } else {
                    String target;

                    if (!TextUtils.isEmpty(editTextPressNoteContent.getText().toString())) {
                        target = ";\n" + text + ":";
                    } else {
                        target = text + ":";
                    }

                    if (!TextUtils.isEmpty(target)) {
                        SpannableString spannableString = new SpannableString(target);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, target.length(), 0);
                        editTextPressNoteContent.append(spannableString);
                    }

                    setUpSubjectValueRv(text);
                }

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    private void prepareGroupedSuggestionsTags(@NonNull List<String> suggestionByGroup) {

        int tagTextColor = ContextCompat.getColor(requireContext(), android.R.color.white);
        int normalTagBackground = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark);
        int[] normalTagColor = {normalTagBackground, normalTagBackground, tagTextColor};
        ArrayList<int[]> tagColors = new ArrayList<>(suggestionByGroup.size());
        Collections.fill(tagColors, normalTagColor);

        pressNoteWordsContainer.setTags(suggestionByGroup, tagColors);
        pressNoteWordsContainer.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                Log.d(TAG, "onTagClick() called with: position = [" + position + "], text = [" + text + "]");
                if (text.equals("Add New")) {
                    AddNewSuggestionDialogHelper dialogHelper = new AddNewSuggestionDialogHelper(getActivity());
                    dialogHelper.setSuggestionType(selectedPressNoteType);
                    dialogHelper.setSuggestionCreatedListener(UhcPatientProgressNoteCreateFragment.this);
                    dialogHelper.showAddNewSuggestionDialog(getLayoutInflater());
                } else {
                    String target;

                    if (!TextUtils.isEmpty(editTextPressNoteContent.getText().toString())) {
                        target = ";\n" + text + ":";
                    } else {
                        target = text + ":";
                    }

                    if (!TextUtils.isEmpty(target)) {
                        SpannableString spannableString = new SpannableString(target);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, target.length(), 0);
                        editTextPressNoteContent.append(spannableString);
                    }

                    setUpSubjectValueRv(text);
                }

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onSelectedTagDrag(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });
    }

    private ArrayList<String> getSuggestionsListByPressNoteType(String pressNoteType) {
        ArrayList<SuggestionWordModel> words = new ArrayList<>(realm.where(SuggestionWordModel.class).equalTo(getSuggestionColumnByType(pressNoteType), "true", Case.INSENSITIVE).equalTo("isDeleted", false).findAll());

        ArrayList<String> suggestionTitles = new ArrayList<>();
        for (SuggestionWordModel suggestion : words) {
            suggestionTitles.add(suggestion.getWord());
        }
        return suggestionTitles;
    }

    private String getSuggestionColumnByType(String pressNoteType) {
        if (pressNoteType.equals(CMISConstant.PRESSNOTE_PROBLEM)) {
            return "isProblem";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_OBSERVATION)) {
            return "isObservation";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_TREATMENT)) {
            return "isTreatment";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_REFERRAL)) {
            return "isReferral";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_SERVICE)) {
            return "isService";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_REASON)) {
            return "isReason";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_INVESTIGATION)) {
            return "isInvestigation";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_CARE)) {
            return "isCare";
        } else if (pressNoteType.equals(CMISConstant.PRESSNOTE_Diagnosis)) {
            return "isDiagnosis";
        } else {
            return "";
        }
    }

    private void getImage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionHelper.requiresPermissions(requireContext(), PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                PermissionHelper.handlePermissions(requireActivity());
            } else {
                getImageUIUpdate();
                PhotoUtils.captureImage(this);
            }
        } else {
            getImageUIUpdate();
            PhotoUtils.captureImage(this);
        }
    }

    private void getImageUIUpdate() {
        pressNoteWordsScrollView.setVisibility(View.GONE);
        recyclerViewPressNotePhotos.setVisibility(View.VISIBLE);
        editTextPressNoteContent.setEnabled(false);
    }

    public void loadToEdit(UhcPatientProgressNoteViewModel progressNoteViewModel) {
        if (progressNoteViewModel != null) {

            progressNoteEditSource = progressNoteViewModel.getProgressNote();
            if (progressNoteEditSource != null) {
                selectedPressNoteType = progressNoteEditSource.getType();
                patientCode = progressNoteEditSource.getPatientCode();

                if (!TextUtils.isEmpty(progressNoteEditSource.getNote())) {
                    editTextPressNoteContent.setText(AppUtils.getProgressNoteKeyValueSpannable(progressNoteEditSource.getNote()));
                    editTextPressNoteContent.setSelection(editTextPressNoteContent.length());
                }

                if (!TextUtils.isEmpty(progressNoteEditSource.getFreeNote())) {
                    layout_freeText.setVisibility(View.VISIBLE);
                    txtProgressNoteFreeText.setText(progressNoteEditSource.getFreeNote());
                }

                if (callback != null) {
                    callback.onToolbarTitleChange("Edit " + selectedPressNoteType);
                }

            }

            ArrayList<UhcPatientProgressNotePhoto> progressNotePhotosEditSource = progressNoteViewModel.getPhotos();
            if (progressNotePhotosEditSource != null && !progressNotePhotosEditSource.isEmpty()) {
                photoAdapter.replacePhotos(progressNotePhotosEditSource);
            }

            rvPressNoteTypeContainerCard.setVisibility(View.GONE);
            setupInputTypeTabsLayout();
            updateViews();
            refreshSuggestionsTags();
            hideKeyboard();
        }
    }

    private void updateViews() {
        Log.d(TAG, "UhcPatientProgressNoteCreateFragment:updateViews: called");
        boolean isPressNoteTypeSelected = !TextUtils.isEmpty(selectedPressNoteType);
        Log.d(TAG, "updateViews: Is PressNoteSelected : " + isPressNoteTypeSelected);
        int visibility = isPressNoteTypeSelected ? View.VISIBLE : View.GONE;
        editTextPressNoteContent.setEnabled(isPressNoteTypeSelected);
        editTextPressNoteContent.setFocusable(isPressNoteTypeSelected);
        editTextPressNoteContent.setFocusableInTouchMode(isPressNoteTypeSelected);
        tabLayoutInputType.setVisibility(visibility);
        rvPressNoteSubjectContainerCard.setVisibility(visibility);
        pressNoteWordsScrollView.setVisibility(visibility);
        if (isPressNoteTypeSelected) {
            editTextPressNoteContent.setKeyListener(keyListener);
        } else {
            editTextPressNoteContent.setKeyListener(null);
        }
    }

    public boolean doChangesExists() {
        return doChangesExists;
    }

    private void setUpSubjectValueRv(String word) {
        RealmResults<SuggestionWordModel> suggestionsByType = realm.where(SuggestionWordModel.class).equalTo(getSuggestionColumnByType(selectedPressNoteType), "true").findAll();
        RealmResults<SuggestionWordModel> filteredSuggestions = suggestionsByType.where().contains("word", word, Case.INSENSITIVE).findAll();
        ArrayList<SuggestionWordModel> words = new ArrayList<>(filteredSuggestions);
        String suggestionValues;
        String[] values;
        ArrayList<String> valueList = new ArrayList<>();
        for (SuggestionWordModel suggestion : words) {
            suggestionValues = suggestion.getValue();
            if (!TextUtils.isEmpty(suggestionValues)) {
                values = suggestionValues.trim().split(",");
                Collections.addAll(valueList, values);
            }
        }

        Log.d("KK", "setUpSubjectValueRv: Value List " + valueList.size());
//        pressNoteSubjectWordAdapter = new PressNoteSubjectWordAdapter(valueList);
//        pressNoteSubjectWordAdapter.clear();

        valueList.removeAll(Arrays.asList(null, "", " "));
        Log.d("KK", "setUpSubjectValueRv: Value List after clean up" + valueList.size());
        ArrayList<String> filteredValueList = new ArrayList<>();

        for (String v : valueList) {
            if (!TextUtils.isEmpty(v) && !TextUtils.isEmpty(v.trim())) {
                Log.d("KK", "setUpSubjectValueRv:::: " + v);
                filteredValueList.add(v);
            }
        }
        Log.d("KK", "setUpSubjectValueRv: " + filteredValueList.size());
        pressNoteSubjectWordAdapter.clear();
        pressNoteSubjectWordAdapter.addSubjects(filteredValueList);

        rvPressNoteSubjectContainerCard.setVisibility(filteredValueList.isEmpty() ? View.GONE : View.VISIBLE);

    }

    private class InputTabSelectedListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab != null) {
                String tabLabel = TextUtils.isEmpty(tab.getText()) ? "" : tab.getText().toString();
                Log.d(TAG, "onTabSelected: Tab Selected : " + tabLabel);

                if (tabLabel.equalsIgnoreCase("Note")) {
                    showAddFreeTextDialog();
                } else if (tabLabel.equalsIgnoreCase("Text")) {
                    recyclerViewPressNotePhotos.setVisibility(View.GONE);
                    pressNoteWordsScrollView.setVisibility(View.GONE);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);
                    editTextPressNoteContent.setEnabled(true);
                    editTextPressNoteContent.setFocusableInTouchMode(true);
                    editTextPressNoteContent.requestFocus();
                    inputMethodManager.showSoftInput(editTextPressNoteContent, InputMethod.SHOW_FORCED);
                } else if (tabLabel.startsWith("Words -") || tabLabel.equals("General Illness")) {
                    editTextPressNoteContent.setEnabled(true);
                    editTextPressNoteContent.setFocusableInTouchMode(true);
                    recyclerViewPressNotePhotos.setVisibility(View.GONE);
                    pressNoteWordsScrollView.setVisibility(View.VISIBLE);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);

                    hideKeyboard();

                    if (!TextUtils.isEmpty(selectedPressNoteType)) {
                        refreshSuggestionsTags();
                    }
                } else if (tabLabel.equalsIgnoreCase("Photo")) {
                    recyclerViewPressNotePhotos.setVisibility(View.VISIBLE);
                    pressNoteWordsScrollView.setVisibility(View.GONE);
                    editTextPressNoteContent.setEnabled(false);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);

                    hideKeyboard();

                    if (photoAdapter.getItemCount() == 0) {
                        getImage();
                    }

                } else if (tabLabel.equalsIgnoreCase("ICD-10")) {
                    startActivityForResult(new Intent(requireActivity(), ICDChooserActivity.class), REQ_ICD_TERMS);
                } else if (tabLabel.equalsIgnoreCase("Child Health")) {

                    ArrayList<String> cuFiveSuggestions = new ArrayList<>();
                    cuFiveSuggestions.add("Pneumonia");
                    cuFiveSuggestions.add("Other ARI");
                    cuFiveSuggestions.add("Diarrhea");

                    prepareGroupedSuggestionsTags(cuFiveSuggestions);

                    recyclerViewPressNotePhotos.setVisibility(View.GONE);
                    editTextPressNoteContent.setEnabled(true);
                    pressNoteWordsScrollView.setVisibility(View.VISIBLE);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);

                    hideKeyboard();

                } else if (tabLabel.equalsIgnoreCase("CDs")) {
                    ArrayList<String> cdSuggestions = new ArrayList<>();
                    cdSuggestions.add("STI");
                    cdSuggestions.add("TB");
                    cdSuggestions.add("Malaria");
                    cdSuggestions.add("HIV");

                    prepareGroupedSuggestionsTags(cdSuggestions);

                    recyclerViewPressNotePhotos.setVisibility(View.GONE);
                    editTextPressNoteContent.setEnabled(true);
                    pressNoteWordsScrollView.setVisibility(View.VISIBLE);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);

                    hideKeyboard();

                } else if (tabLabel.equalsIgnoreCase("NCDs")) {
                    ArrayList<String> ncdSuggestions = new ArrayList<>();
                    ncdSuggestions.add("Hypertension");
                    ncdSuggestions.add("Diabetes");

                    prepareGroupedSuggestionsTags(ncdSuggestions);

                    recyclerViewPressNotePhotos.setVisibility(View.GONE);
                    editTextPressNoteContent.setEnabled(true);
                    pressNoteWordsScrollView.setVisibility(View.VISIBLE);
                    rvPressNoteSubjectContainerCard.setVisibility(View.GONE);

                    hideKeyboard();
                }

            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

            if (tab != null) {
                String tabLabel = TextUtils.isEmpty(tab.getText()) ? "" : tab.getText().toString();
                Log.d(TAG, "onTabReselected: " + tabLabel);

                if (tabLabel.equalsIgnoreCase("Note")) {
                    showAddFreeTextDialog();
                } else if (tabLabel.equalsIgnoreCase("Photo")) {
                    getImage();
                } else if (tabLabel.equalsIgnoreCase("ICD-10")) {
                    startActivityForResult(new Intent(requireActivity(), ICDChooserActivity.class), REQ_ICD_TERMS);
                }
            }
        }
    }

}
