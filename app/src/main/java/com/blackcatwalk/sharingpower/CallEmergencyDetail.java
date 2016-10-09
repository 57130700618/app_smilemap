package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.customAdapter.CallEmergencyDetailCustomListAdapter;
import com.blackcatwalk.sharingpower.google.GoogleMapNearby;
import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class CallEmergencyDetail extends AppCompatActivity {


    private String mClassReferrence;
    private String mType;
    private double mLatitude;
    private double mLongitude;
    public CallEmergencyDetailCustomListAdapter adapter;
    public List<CallEmergency> mListCallEmergency;

    // ----------------- Google_Map -------------------//
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // ---------------- User Interface ------------- //
    private ListView mListView;
    private TextView mLabelTv;
    private ImageView mBackIm;
    private ImageView mUserManualIm;

    public void setmClassReferrence(String _classReferrence) {
        this.mClassReferrence = _classReferrence;
    }

    public String getmClassReferrence() {
        return mClassReferrence;
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_emergency_detail);
        getSupportActionBar().hide();

        bindWidget();

        ControlProgress.showProgressDialogDonTouch(this);

        mType = getIntent().getExtras().getString("type");

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mUserManualIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("ท่านสามารถดูชื่อสถานที่แบบเต็ม โดยกดเลือกรายการที่ท่านต้องการค้างไว้ จะมีกล่องข้อความแสดงขึ้นมา");
            }
        });

        mListCallEmergency = new ArrayList<CallEmergency>();
        adapter = new CallEmergencyDetailCustomListAdapter(CallEmergencyDetail.this, mListCallEmergency);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CallEmergency _item = mListCallEmergency.get(position);
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + _item.getmTel())));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setupDialog(position);
                return true;
            }
        });

        if (mType.equals("general")) {
            mLabelTv.setText("สายด่วน แจ้งเหตุ");

            mListCallEmergency.add(new CallEmergency("แจ้งเหตุด่วน-เหตุร้ายทุกชนิด","191"));
            mListCallEmergency.add(new CallEmergency("หน่วยแพทย์ฉุกเฉิน(ทั่วไทย)","1669"));
            mListCallEmergency.add(new CallEmergency("หน่วยแพทย์ฉุกเฉิน(กทม.)","1646"));
            mListCallEmergency.add(new CallEmergency("หน่วยกู้ชีพ วชิรพยาบาล","1554"));

            //---------------------------------------
            mListCallEmergency.add(new CallEmergency("กรมเจ้าท่า, เหตุด่วนทางน้ำ","1199"));
            mListCallEmergency.add(new CallEmergency("สายด่วนตำรวจท่องเที่ยว","1155"));
            mListCallEmergency.add(new CallEmergency("สายด่วนทางหลวง","1586"));
            mListCallEmergency.add(new CallEmergency("รับแจ้งอัคคีภัย สัตว์เข้าบ้าน","199"));
            mListCallEmergency.add(new CallEmergency("รับแจ้งรถหาย, ถูกขโมย","1192"));
            mListCallEmergency.add(new CallEmergency("ศูนย์เตือนภัยพิบัติแห่งชาติ","192"));
            mListCallEmergency.add(new CallEmergency("ศูนย์ควบคุมและสั่งการจราจร","1197"));

            //---------------------------------------
            mListCallEmergency.add(new CallEmergency("สถานีวิทยุร่วมด้วยช่วยกัน","1677"));
            mListCallEmergency.add(new CallEmergency("สถานีวิทยุ จส.100","1586"));

            adapter.notifyDataSetChanged();
            ControlProgress.hideDialog();
        } else {
            final ControlCheckConnect _controlCheckConnect = new ControlCheckConnect();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected( Bundle bundle) {
                            mLastLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient);

                            if (mLastLocation != null) {

                                mLatitude = mLastLocation.getLatitude();
                                mLongitude = mLastLocation.getLongitude();

                                setmClassReferrence("CallNearby");
                                //Search Data Nearby
                                new GoogleMapNearby(CallEmergencyDetail.this,mLatitude,mLongitude, mType);
                            } else {
                                ControlProgress.hideDialog();
                                _controlCheckConnect.alertCurrentGps(CallEmergencyDetail.this);
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            if (mGoogleApiClient != null) {
                                mGoogleApiClient.connect();
                            }
                            ControlProgress.hideDialog();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            ControlProgress.hideDialog();
                            _controlCheckConnect.alertCurrentGps(CallEmergencyDetail.this);
                        }
                    })
                    .build();

            mGoogleApiClient.connect();

            if (mType.equals("hospital")) {
                mLabelTv.setText("โรงพยาบาลโดยรอบ");
            } else {
                mLabelTv.setText("สถานีตำรวจโดยรอบ");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void showDialog(String _message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CallEmergencyDetail.this);
        builder.setMessage(_message);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }


    private void setupDialog(int _position) {
        CallEmergency _item = mListCallEmergency.get(_position);
        showDialog(_item.getmName());
    }

    public void showAlertDialogNotFoundTelephone() {

        String tempType = null;

        switch (mType) {
            case "hospital":
                tempType = "โรงพยาบาล";
                break;
            case "police":
                tempType = "สถานีตำรวจ";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ไม่พบข้อมูลเบอร์โทรติดต่อของ" + tempType + " รอบๆตัวท่าน");
        builder.setCancelable(false);

        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }

    private void bindWidget() {
        mLabelTv = (TextView) findViewById(R.id.labelTv);
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mUserManualIm = (ImageView) findViewById(R.id.userManualIm);
        mListView = (ListView) findViewById(R.id.listView);
    }
}
