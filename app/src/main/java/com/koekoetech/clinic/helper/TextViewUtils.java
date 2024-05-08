package com.koekoetech.clinic.helper;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.koekoetech.clinic.model.KeyValueText;

/**
 * Created by ZMN on 4/2/18.
 **/
public class TextViewUtils {

    public static void populateKeyValueTv(TextView textView, KeyValueText... keyValues) {
        populateKeyValueTv(textView, "", keyValues);
    }

    public static void populateKeyValueTv(TextView textView, String delimiter, KeyValueText... keyValues) {
        if (textView != null && keyValues != null && keyValues.length > 0) {
            String kvd = TextUtils.isEmpty(delimiter) ? "|" : delimiter;
            int count = 0;
            SpannableStringBuilder sbb = new SpannableStringBuilder();
            for (KeyValueText kv : keyValues) {
                if (kv != null) {
                    String key = kv.getKey();
                    String keyContent = key + kv.getSeparator() + " ";
                    SpannableString keySpannable = new SpannableString(keyContent);
                    keySpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, keyContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sbb.append(keySpannable);
                    String value = kv.getValue().trim();
                    SpannableString valueSpannable = new SpannableString(value);
                    valueSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(textView.getContext(), android.R.color.primary_text_light)), 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sbb.append(valueSpannable);
                    count += 1;

                    if (count < keyValues.length) {
                        String delimiterContent = " " + kvd + " ";
                        sbb.append(new SpannableString(delimiterContent));
                    }
                }

            }
            textView.setText(sbb);
        }
    }

    public static void populateMultiItemTv(TextView textView, String[] values) {
        populateMultiItemTv(textView, "", values);
    }

    public static void populateMultiItemTv(TextView textView, String separator, String[] values) {
        if (textView != null && values != null && values.length > 0) {
            String sep = TextUtils.isEmpty(separator) ? "|" : separator;
            int count = 0;
            SpannableStringBuilder sbb = new SpannableStringBuilder();
            for (String v : values) {
                if (!TextUtils.isEmpty(v)) {
                    sbb.append(new SpannableString(v));
                    count += 1;

                    if (count < values.length) {
                        String separatorContent = " " + sep + " ";
                        sbb.append(new SpannableString(separatorContent));
                    }
                }
            }
            textView.setText(sbb);
        }
    }

}
