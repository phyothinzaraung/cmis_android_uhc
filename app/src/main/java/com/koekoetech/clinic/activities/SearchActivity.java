package com.koekoetech.clinic.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.CMISConstant;
import com.koekoetech.clinic.helper.CmisGoogleAnalyticsConstants;
import com.koekoetech.clinic.helper.DateTimeHelper;
import com.koekoetech.clinic.helper.PermissionHelper;
import com.koekoetech.clinic.model.AuthenticationModel;
import com.koekoetech.clinic.model.PatientsListFilter;

import java.util.Calendar;

import io.realm.Realm;


public class SearchActivity extends BaseActivity {

    public static final String RESULT_EXTRA_NAME = "patientName";

    public static final String RESULT_EXTRA_GENDER = "gender";

    public static final String RESULT_EXTRA_START_AGE = "startAge";

    public static final String RESULT_EXTRA_END_AGE = "endAge";

    public static final String RESULT_EXTRA_DOB = "dob";

    public static final String RESULT_EXTRA_REG_DATE = "reg";

    public static final String RESULT_EXTRA_PHONE = "phone";

    public static final String RESULT_EXTRA_UID = "uid";

    public static final String RESULT_EXTRA_HH_CODE = "hhcode";

    public static final String RESULT_EXTRA_FILTER = "extra_patient_filter";

    private static final int REQ_BAR_CODE = 3543;

    private EditText editTextName;

    private RadioButton radioButtonBoth;
    private RadioButton radioButtonFemale;
    private RadioButton radioButtonMale;

    private Button buttonSearch;

    private CrystalRangeSeekbar ageRangeSeekBar;

    private TextView tvUICCodeLbl;
    private TextView tvHHCodeLbl;
    private TextView tv_max;
    private TextView tv_min;

    private EditText edtDob;
    private EditText edtUid;
    private EditText edtPhone;
    private EditText edtReg;
    private EditText edtHHCode;

    private ImageView btn_scan_barcode;

    private RelativeLayout uicCodeScanContainer;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        setupToolbar(true);
        setupToolbarText(getString(R.string.txt_menu_search));

        bindViews();

        ageRangeSeekBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            tv_min.setText(String.valueOf(minValue));
            tv_max.setText(String.valueOf(maxValue));
        });

        // set final value listener
        ageRangeSeekBar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) ->
                Log.d("CRS=>", minValue + " : " + maxValue));

        edtDob.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int nowYear = c.get(Calendar.YEAR);
            int nowMonth = c.get(Calendar.MONTH);
            int newDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,
                    (view, year, month, dayOfMonth) -> {
                        String strDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        edtDob.setText(strDate);
                    }, nowYear, nowMonth, newDay);

            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        });

        edtDob.setInputType(InputType.TYPE_NULL);

        edtReg.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int nowYear = c.get(Calendar.YEAR);
            int nowMonth = c.get(Calendar.MONTH);
            int nowDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, DatePickerDialog.THEME_HOLO_LIGHT,
                    (view, year, month, dayOfMonth) -> {

                        String dateSTR = dayOfMonth + "/" + (month + 1) + "/" + year;
                        edtReg.setText(dateSTR);

                    }, nowYear, nowMonth, nowDay);
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackground(new ColorDrawable(Color.TRANSPARENT));
        });

        buttonSearch.setOnClickListener(v -> {

            sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_PATIENT_ADVANCE_SEARCH);
            AppUtils.dismissSoftKeyboard(this);

            buttonSearch.setEnabled(false);
            String name = getEditTextContent(editTextName);
            String gender = "Both";
            String phone = getEditTextContent(edtPhone);
            String uid = getEditTextContent(edtUid);
            String hhdcode = getEditTextContent(edtHHCode);
            String dob = DateTimeHelper.convertDateFormat(getEditTextContent(edtDob), DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.SERVER_DATE_FORMAT);
            String reg = DateTimeHelper.convertDateFormat(getEditTextContent(edtReg), DateTimeHelper.LOCAL_DATE_FORMAT, DateTimeHelper.SERVER_DATE_FORMAT);
            int startAge, endAge;

            if (radioButtonBoth.isChecked()) {
                gender = "Both";
            } else if (radioButtonMale.isChecked()) {
                gender = "Male";
            } else if (radioButtonFemale.isChecked()) {
                gender = "Female";
            }

            startAge = Integer.parseInt(tv_min.getText().toString());
            endAge = Integer.parseInt(tv_max.getText().toString());

            final PatientsListFilter patientsListFilter = new PatientsListFilter(name, gender, dob, reg, uid, hhdcode, phone, startAge, endAge);
            Intent intent = new Intent();
            intent.putExtra(RESULT_EXTRA_FILTER, patientsListFilter);
//            intent.putExtra(RESULT_EXTRA_NAME, name);
//            intent.putExtra(RESULT_EXTRA_GENDER, gender);
//            intent.putExtra(RESULT_EXTRA_START_AGE, startAge);
//            intent.putExtra(RESULT_EXTRA_END_AGE, endAge);
//            intent.putExtra(RESULT_EXTRA_DOB, dob);
//            intent.putExtra(RESULT_EXTRA_REG_DATE, reg);
//            intent.putExtra(RESULT_EXTRA_PHONE, phone);
//            intent.putExtra(RESULT_EXTRA_UID, uid);
//            intent.putExtra(RESULT_EXTRA_HH_CODE, hhdcode);
            setResult(RESULT_OK, intent);
            finish();

        });

        btn_scan_barcode.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionHelper.requiresPermissions(SearchActivity.this, PermissionHelper.PERMISSIONS_TO_REQUEST)) {
                    PermissionHelper.handlePermissions(SearchActivity.this);
                } else {
                    Intent intent = new Intent(SearchActivity.this, BarcodeScannerActivity.class);
                    startActivityForResult(intent, REQ_BAR_CODE);
                }
            } else {
                Intent intent = new Intent(SearchActivity.this, BarcodeScannerActivity.class);
                startActivityForResult(intent, REQ_BAR_CODE);
            }

        });

        boolean isUhcDoctor = false;
        AuthenticationModel authenticationModel = Realm.getDefaultInstance().where(AuthenticationModel.class).findFirst();
        if (authenticationModel != null) {
            isUhcDoctor = authenticationModel.isPermitted(CMISConstant.FORM_CARD_HOLDER_REG);
        }

        int visibility = isUhcDoctor ? View.VISIBLE : View.GONE;
        tvHHCodeLbl.setVisibility(visibility);
        tvUICCodeLbl.setVisibility(visibility);
        uicCodeScanContainer.setVisibility(visibility);
        edtHHCode.setVisibility(visibility);
    }

    private void bindViews() {
        editTextName = findViewById(R.id.edt_search_name);
        radioButtonBoth = findViewById(R.id.patient_search_radioBoth);
        radioButtonFemale = findViewById(R.id.patient_search_radioFemale);
        radioButtonMale = findViewById(R.id.patient_search_radioMale);
        buttonSearch = findViewById(R.id.btn_search);
        ageRangeSeekBar = findViewById(R.id.rangeSeekbarAge);
        tvUICCodeLbl = findViewById(R.id.search_tv_lbl_uhc_code);
        tvHHCodeLbl = findViewById(R.id.search_tv_lbl_hh_code);
        tv_max = findViewById(R.id.tv_max);
        tv_min = findViewById(R.id.tv_min);
        edtDob = findViewById(R.id.edt_search_dob);
        edtUid = findViewById(R.id.edt_search_UID);
        edtPhone = findViewById(R.id.edt_search_phone);
        edtReg = findViewById(R.id.edt_search_reg_date);
        edtHHCode = findViewById(R.id.edt_search_hhCode);
        btn_scan_barcode = findViewById(R.id.btn_scan_barcode);
        uicCodeScanContainer = findViewById(R.id.search_uic_code_scan_container);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_BAR_CODE) {
                if (data != null) {
                    String barCode = data.getStringExtra(BarcodeScannerActivity.SCANNED_UIC_CODE);
                    edtUid.setText(barCode);
                    sendActionAnalytics(CmisGoogleAnalyticsConstants.ACTION_SEARCH_BY_QR);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getEditTextContent(EditText editText) {
        String edtContent = editText.getText().toString().trim();
        return TextUtils.isEmpty(edtContent) ? "" : edtContent;
    }
}
