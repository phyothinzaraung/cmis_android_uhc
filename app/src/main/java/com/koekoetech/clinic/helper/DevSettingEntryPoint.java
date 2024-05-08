package com.koekoetech.clinic.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.koekoetech.clinic.R;
import com.koekoetech.clinic.activities.DevSettingsActivity;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by ZMN on 1/22/18.
 **/

public class DevSettingEntryPoint {


    public static void goToDevSetting(final Activity activity) {

        if(activity == null){
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dev_setting_password);
        View view = View.inflate(activity, R.layout.dialog_export_password, null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final TextInputLayout tilPwd = view.findViewById(R.id.data_export_dialog_input_layout_password);
        final EditText edtPwd = view.findViewById(R.id.data_export_edt_password);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtPwd.getText().toString().trim())) {
                tilPwd.setError("Please Enter Password");
                return;
            }

            String enteredPassword = edtPwd.getText().toString().trim();
            if (!enteredPassword.equals(CMISConstant.DATA_EXPORT_DEV_PASSWORD)) {
                tilPwd.setError("Incorrect Password");
                return;
            }

            dialog.dismiss();
            activity.startActivity(new Intent(activity, DevSettingsActivity.class));

//                PatientDataExporter dataExporter = new PatientDataExporter(new DataExportListener());
//                dataExporter.init(getContext());
//                dialog.dismiss();
//                dataExporter.export();
        });
    }
}
