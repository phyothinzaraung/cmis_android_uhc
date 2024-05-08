package com.koekoetech.clinic.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

/**
 * Created by ZMN on 1/22/18.
 **/

public abstract class MultiClickListener implements View.OnClickListener {

    private static final String TAG = MultiClickListener.class.getSimpleName();

    private final int targetClickCount;
    private int totalClickCount;
    private final Handler handler;
    private final ClickCountResetRunnable resetRunnable;

    public MultiClickListener(int targetClickCount) {
        this.targetClickCount = targetClickCount;
        handler = new Handler(Looper.getMainLooper());
        resetRunnable = new ClickCountResetRunnable();
    }

    @Override
    public void onClick(View v) {

        handler.removeCallbacks(resetRunnable);

        totalClickCount++;

        onClickEachTime(totalClickCount);

        if (totalClickCount == targetClickCount) {
            onTargetReached(v);
            totalClickCount = 0;
        }

        handler.postDelayed(resetRunnable, 2000);

    }

    abstract protected void onTargetReached(View v);

    abstract protected void onClickEachTime(int clickedCount);

    private class ClickCountResetRunnable implements Runnable {

        @Override
        public void run() {
            totalClickCount = 0;
            Log.d(TAG, "run: Click Count has been reset");
        }
    }
}
