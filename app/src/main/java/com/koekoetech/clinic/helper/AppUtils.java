package com.koekoetech.clinic.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZMN on 4/10/17.
 **/
@SuppressWarnings({"unused"})
public class AppUtils {

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static void dismissSoftKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null && imm != null) {
            imm.hideSoftInputFromWindow(currentFocus.getApplicationWindowToken(), 0);
        }
    }

    public static Spannable getProgressNoteKeyValueSpannable(String progressNote) {
        if (!TextUtils.isEmpty(progressNote)) {
            Spannable spannable = new SpannableString(progressNote);

            int boldStart = 0;
            int boldEnd;

            char[] characters = progressNote.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                if (characters[i] == ':') {
                    boldEnd = i;
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), boldStart, boldEnd, 0);
                } else if (characters[i] == ';') {
                    boldStart = i + 1;
                }
            }

            return spannable;
        }
        return new SpannableString("");
    }

    public static String emptyOnNull(@Nullable String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }

    public static void applyVisibility(@NonNull final View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }
}
