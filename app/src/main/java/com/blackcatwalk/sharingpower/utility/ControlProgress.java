package com.blackcatwalk.sharingpower.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;

import com.blackcatwalk.sharingpower.R;

public class ControlProgress {

    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context) {
        mProgressDialog = new ProgressDialog(context, R.style.MyTheme);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        mProgressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_circle));
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.getWindow().setDimAmount(0);
        mProgressDialog.show();
    }

    public static void showProgressDialogDonTouch(Context context) {
        mProgressDialog = new ProgressDialog(context, R.style.MyTheme);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_circle));
        mProgressDialog.show();
    }

    public static void hideDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public static void delayHideDialog(int timeDelay) {
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        }, timeDelay);
    }

    public int[] getColorSchemeSwipeRefresh() {
        int color[] = {R.color.red_bus, R.color.yellow, R.color.green, R.color.blue};
        return color;
    }
}
