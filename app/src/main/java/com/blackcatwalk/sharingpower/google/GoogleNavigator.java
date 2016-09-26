package com.blackcatwalk.sharingpower.google;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.widget.Button;

public class GoogleNavigator {

    private String mTypeNavigator;

    public void showDialogNavigator(final Context _context, final String _lat, final String _lng) {

        final String[] lists = {"ขับรถ", "เดิน"};
        new AlertDialog.Builder(_context).setTitle("เลือกประเภทการเดินทาง").setItems(lists, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String parse = "google.navigation:q=" + _lat + "," + _lng;

                switch (which) {
                    case 0:
                        mTypeNavigator = parse + "&mode=d";
                        break;
                    case 1:
                        mTypeNavigator = parse + "&mode=w";
                        break;
                }
                dialog.cancel();

                AlertDialog.Builder buildercheck = new AlertDialog.Builder(_context);
                buildercheck.setTitle(Html.fromHtml("<b>" + "โปรดตรวจสอบว่าท่านไม่ได้อยู่จุดอับสัญญาณ" + "</b>"));
                buildercheck.setMessage("เมนูนำทางอาจใช้เวลาในการโหลดข้อมูลนาน เมื่อท่านอยู่จุดอับสัญญาณ");
                buildercheck.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri gmmIntentUri = Uri.parse(mTypeNavigator);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        _context.startActivity(mapIntent);

                        dialog.cancel();
                    }
                });
                AlertDialog alert = buildercheck.create();
                alert.show();
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#147cce"));
                pbutton.setTypeface(null, Typeface.BOLD);
            }
        }).show();
    }
}
