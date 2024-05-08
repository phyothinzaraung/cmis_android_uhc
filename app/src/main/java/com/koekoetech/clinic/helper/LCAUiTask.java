package com.koekoetech.clinic.helper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import needle.UiRelatedTask;

/**
 * Lifecycle aware UI Related Needle Task
 */
public abstract class LCAUiTask<Result> extends UiRelatedTask<Result> implements LifecycleEventObserver {

    private static final String TAG = "LCAUiTask";

    public LCAUiTask(@NonNull final LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY){
            Log.d(TAG, "onStateChanged: State changed to DESTROY");
            try {
                if (!isCanceled()){
                    Log.d(TAG, "onStateChanged: Canceling task.");
                    cancel();
                }
                source.getLifecycle().removeObserver(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
