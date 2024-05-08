package com.koekoetech.clinic.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.koekoetech.clinic.R;

/**
 * Created by ZMN on 3/6/18.
 **/

@SuppressWarnings("unused")
public class AppProgressDialog extends DialogFragment {

    private static final String TAG = AppProgressDialog.class.getSimpleName();
    private static final String ARG_MSG = "DialogMessage";
    private static final String ARG_TITLE = "DialogTitle";
    private static final String ARG_NO_TITLE = "DialogNoTitle";

    private TextView titleTextView;
    private TextView messageTextView;

    public static AppProgressDialog getProgressDialog(String title, String message) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, message);
        args.putBoolean(ARG_NO_TITLE, false);
        AppProgressDialog fragment = new AppProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static AppProgressDialog getProgressDialog(String message) {
        Bundle args = new Bundle();
        args.putString(ARG_MSG, message);
        args.putBoolean(ARG_NO_TITLE, true);
        AppProgressDialog fragment = new AppProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_progress_dialog, container, false);
        titleTextView = view.findViewById(R.id.tv_progress_title);
        messageTextView = view.findViewById(R.id.tv_progress_message);

        if (getArguments() != null) {
            boolean shouldHideTitle = getArguments().getBoolean(ARG_NO_TITLE, false);
            if (shouldHideTitle) {
                titleTextView.setVisibility(View.GONE);
            } else {
                titleTextView.setText(getArguments().getString(ARG_TITLE, ""));
            }

            messageTextView.setText(getArguments().getString(ARG_MSG, "Loading"));
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getDialog() != null) {
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            }
        }

    }

    public void setMessage(String message) {
        if (messageTextView != null && !TextUtils.isEmpty(message)) {
            Log.d(TAG, "setMessage() called with: message = [" + message + "]");
            messageTextView.setText(message);
        }
    }

    public void setTitle(String title) {
        if (titleTextView != null && !TextUtils.isEmpty(title)) {
            Log.d(TAG, "setTitle() called with: title = [" + title + "]");
            titleTextView.setText(title);
        }
    }

    public boolean isDisplaying() {
        return getDialog() != null && getDialog().isShowing();
    }

    public void display(@NonNull FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment previous = fragmentManager.findFragmentByTag(TAG);

        if (previous != null) {
            fragmentTransaction.remove(previous).commit();
        }

        fragmentTransaction.addToBackStack(null);

        show(fragmentManager, TAG);
    }

    public void safeDismiss() {
        try {
            dismiss();
        } catch (Exception e) {
            Log.e(TAG, "safeDismiss: ", e);
        }
    }

}
