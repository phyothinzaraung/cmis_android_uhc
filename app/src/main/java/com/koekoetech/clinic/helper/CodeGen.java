package com.koekoetech.clinic.helper;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.UUID;

/**
 * Created by ZMN on 8/25/17.
 **/

@SuppressLint("DefaultLocale")
public class CodeGen {

    private static final String TAG = CodeGen.class.getSimpleName();

    public static String generatePatientCode(int stateCode, int townshipCode, int staffId, int codeIndex) {
        Log.d("generatePatientCode", "generatePatientCode() called with: stateCode = [" + stateCode + "], townshipCode = [" + townshipCode + "], staffId = [" + staffId + "], codeIndex = [" + codeIndex + "]");
        return String.format("%02d", stateCode) +
                String.format("%02d", townshipCode) +
                String.format("%04d", staffId) +
                String.format("%05d", codeIndex + 1);
    }

    public static String generateUIDCode(String facilityCode, String townshipCode, String hhCode, String hhMemberCode, int codeIndex) {
        String codeDigit = String.format("%05d", codeIndex + 1);
        return facilityCode + townshipCode + hhCode + hhMemberCode + codeDigit;
    }

    public static String generate12DigitUIDCode(String stateCode, String townshipCode, String hhCode, String hhMemberCode) {
        Log.d(TAG, "generate12DigitUIDCode() called with: stateCode = [" + stateCode + "], townshipCode = [" + townshipCode + "], hhCode = [" + hhCode + "], hhMemberCode = [" + hhMemberCode + "]");
        return stateCode + townshipCode + hhCode + hhMemberCode;
    }

    public static String generateProgressCode() {
        return UUID.randomUUID().toString();
    }

    public static String generateProgressNoteCode() {
        return UUID.randomUUID().toString();
    }
}
