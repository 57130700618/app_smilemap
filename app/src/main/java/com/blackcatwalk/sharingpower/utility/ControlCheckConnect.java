package com.blackcatwalk.sharingpower.utility;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.R;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class ControlCheckConnect {

    private Dialog mDialogGps;
    private static Dialog mDialogIntetnet;

    public boolean checkGPS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }
        return true;
    }

    public void alertGps(final Context context) {

        if (mDialogGps == null) {
            mDialogGps = new Dialog(context);
            mDialogGps.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogGps.setContentView(R.layout.activity_dialog_checkconnect);
            mDialogGps.setCancelable(false);

            ImageView label = (ImageView) mDialogGps.findViewById(R.id.label);
            label.setImageResource(R.drawable.label_gps);

            Button button1 = (Button) mDialogGps.findViewById(R.id.btnTry);
            button1.setText(R.string.ok_bold);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                    mDialogGps.cancel();
                    mDialogGps = null;
                }
            });

            TextView textView = (TextView) mDialogGps.findViewById(R.id.textView);
            textView.setText(R.string.title_open_gps);
            TextView txtDetail = (TextView) mDialogGps.findViewById(R.id.txtDetail);
            txtDetail.setText(R.string.detail_open_gps);
            TextView txtDetailSetting = (TextView) mDialogGps.findViewById(R.id.txtDetailSetting);
            txtDetailSetting.setVisibility(View.VISIBLE);

            mDialogGps.show();
        }
    }

    public void alertCurrentGps(Context context) {

        if (mDialogGps == null) {
            mDialogGps = new Dialog(context);
            mDialogGps.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogGps.setContentView(R.layout.activity_dialog_current_gps);
            mDialogGps.setCancelable(false);

            Button btnClose = (Button) mDialogGps.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogGps.cancel();
                    mDialogGps = null;
                }
            });
            mDialogGps.show();
        }
    }

    //---------------------- Check internet ----------------------//

    public static boolean checkInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting(); //For 3G check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting(); //For WiFi Check
        if (!is3g && !isWifi) {
            return false;
        }
        return true;
    }

    public static void alertInternet(final Context context) {

        if (mDialogIntetnet == null) {
            mDialogIntetnet = new Dialog(context);
            mDialogIntetnet.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogIntetnet.setContentView(R.layout.activity_dialog_checkconnect);
            mDialogIntetnet.setCancelable(false);

            ImageView label = (ImageView) mDialogIntetnet.findViewById(R.id.label);
            label.setImageResource(R.drawable.label_internet);

            Button button1 = (Button) mDialogIntetnet.findViewById(R.id.btnTry);
            button1.setText(R.string.try_again);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (checkInternet(context)) {
                        mDialogIntetnet.cancel();
                        mDialogIntetnet = null;
                    }
                }
            });
            TextView textView = (TextView) mDialogIntetnet.findViewById(R.id.textView);
            textView.setText(R.string.title_open_network);
            TextView txtDetail = (TextView) mDialogIntetnet.findViewById(R.id.txtDetail);
            txtDetail.setText(R.string.detail_open_network);

            mDialogIntetnet.show();
        }
    }

    public static void alertCurrentInternet(Context context) {
        if (mDialogIntetnet == null) {
            mDialogIntetnet = new Dialog(context);
            mDialogIntetnet.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialogIntetnet.setContentView(R.layout.activity_dialog_current_internet);
            mDialogIntetnet.setCancelable(false);

            Button btnClose = (Button) mDialogIntetnet.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogIntetnet.cancel();
                    mDialogIntetnet = null;
                }
            });
            mDialogIntetnet.show();
        }
    }
}
