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
    public List<String> mNameList = new ArrayList<String>();
    public List<String> mTelList = new ArrayList<String>();

    // ----------------- Google_Map -------------------//
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    // ---------------- User Interface ------------- //
    private ListView mListView;
    private TextView mLabelTv;
    private ImageView mBackIm;


    public void setmClassReferrence(String _classReferrence) {
        this.mClassReferrence = _classReferrence;
    }

    public String getmType(){
        return mType;
    }

    public String getmClassReferrence() {
        return mClassReferrence;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
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

        adapter = new CallEmergencyDetailCustomListAdapter(CallEmergencyDetail.this, mNameList, mTelList);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mTelList.get(position))));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showAlertDialog(position);
                return true;
            }
        });

        if (mType.equals("general")) {
            mLabelTv.setText("สายด่วน แจ้งเหตุ");

            mNameList.add("แจ้งเหตุด่วน-เหตุร้ายทุกชนิด");
            mTelList.add("191");

            mNameList.add("หน่วยแพทย์ฉุกเฉิน(ทั่วไทย)");
            mTelList.add("1669");

            mNameList.add("หน่วยแพทย์ฉุกเฉิน(กทม.)");
            mTelList.add("1646");

            mNameList.add("หน่วยกู้ชีพ วชิรพยาบาล");
            mTelList.add("1554");

            //---------------------------------------

            mNameList.add("กรมเจ้าท่า, เหตุด่วนทางน้ำ");
            mTelList.add("1199");

            mNameList.add("สายด่วนตำรวจท่องเที่ยว");
            mTelList.add("1155");

            mNameList.add("สายด่วนทางหลวง");
            mTelList.add("1586");

            mNameList.add("รับแจ้งอัคคีภัย สัตว์เข้าบ้าน");
            mTelList.add("199");

            mNameList.add("รับแจ้งรถหาย, ถูกขโมย");
            mTelList.add("1192");

            mNameList.add("ศูนย์เตือนภัยพิบัติแห่งชาติ");
            mTelList.add("192");

            mNameList.add("ศูนย์ควบคุมและสั่งการจราจร");
            mTelList.add("1197");

            //---------------------------------------

            mNameList.add("สถานีวิทยุร่วมด้วยช่วยกัน");
            mTelList.add("1677");

            mNameList.add("สถานีวิทยุ จส.100");
            mTelList.add("1586");

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
                                new GoogleMapNearby(CallEmergencyDetail.this);
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
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
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

    private void showAlertDialog(int _position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CallEmergencyDetail.this);
        builder.setMessage(mNameList.get(_position));
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
        mListView = (ListView) findViewById(R.id.listView);
    }
}
