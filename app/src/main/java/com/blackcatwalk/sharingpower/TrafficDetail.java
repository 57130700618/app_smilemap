package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.google.GoogleMapAddress;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class TrafficDetail extends AppCompatActivity {

    private double mLat;
    private double mLng;
    private int mType;
    private String mDistance;
    private String mDuration;
    private String mName;
    private String mDetail = "";

    // ----------- User Interface --------------//
    private TextView titleTv;
    private TextView nameTv;
    private TextView detailTv;
    private TextView addressTv;
    private TextView amountPersonTv;
    private TextView distanceTv;
    private TextView durationTv;
    private Button detailBtn;
    private ImageView mBackIm;
    private ImageView reportBtn;
    private SupportStreetViewPanoramaFragment streetviewFm;
    private LinearLayout googleStreetLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_detail);
        getSupportActionBar().hide();

        bindWidget();

        getBundle();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentReport();
            }
        });

        streetviewFm.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                showStreetView(new LatLng(mLat, mLng), streetViewPanorama);
            }
        });

        googleStreetLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GoogleMapStreet.class)
                        .putExtra("lat", mLat).putExtra("lng", mLng));
            }
        });

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail();
            }
        });

        setContent();
    }

    private void setContent() {
        String _detail = mDetail.substring
                (mDetail.indexOf("$") + 1, mDetail.length());

        addressTv.setText("ที่อยู่: " + GoogleMapAddress.getAddress
                (TrafficDetail.this, mLat, mLng));

        switch (mType) {
            case 0:
                titleTv.setText("รถเมล์");
                nameTv.setText("สาย: " + mName);
                detailTv.setVisibility(View.VISIBLE);
                detailTv.setText("ประเภท: รถเมล์" + mDetail.substring
                        (mDetail.indexOf("$") + 3, mDetail.indexOf("%$&*-", 0)));
                amountPersonTv.setText("จำนวณผู้โดยสาย: " + mDetail.substring
                        (mDetail.indexOf("%$&*-", 0) + 5, mDetail.length()));
                detailBtn.setText("ค่าโดยสาร");
                break;
            case 1:
                titleTv.setText("รถไฟฟ้าบีทีเอส");
                nameTv.setText("สาย: " + mName);
                amountPersonTv.setVisibility(View.GONE);
                detailBtn.setText("รายละเอียดสถานี");
                break;
            case 2:
                titleTv.setText("รถโดยสารบีอาที");
                nameTv.setText("สาย: " + mName);
                amountPersonTv.setText("จำนวณผู้โดยสาย: " + mDetail.substring
                        (mDetail.indexOf("%$&*-", 0) + 5, mDetail.length()));
                detailBtn.setText("รายละเอียดสถานี");
                break;
            case 3:
                titleTv.setText("รถตู้");
                nameTv.setText("ชื่อ: " + mName);
                detailTv.setVisibility(View.VISIBLE);
                amountPersonTv.setText("จำนวณที่นั่ง: " + mDetail.substring
                        (mDetail.indexOf("%$&*-", 0) + 5, mDetail.length()));
                detailBtn.setVisibility(View.GONE);
                break;
            case 4:
                titleTv.setText("ตำแหน่งสาธารณะ");
                nameTv.setText("ชื่อ: " + mName);
                detailTv.setVisibility(View.VISIBLE);
                amountPersonTv.setVisibility(View.GONE);
                detailBtn.setVisibility(View.GONE);
                break;
            case 5:
                titleTv.setText("เรือโดยสาร");
                nameTv.setText("สาย: " + mName);
                amountPersonTv.setText("จำนวณผู้โดยสาย: " + mDetail.substring
                        (mDetail.indexOf("%$&*-", 0) + 5, mDetail.length()));
                detailBtn.setText("รายละเอียดสถานี");
                break;
            case 6:
                titleTv.setText("อุบัติเหตุ");
                nameTv.setText("ประเภท: " + mName);
                detailTv.setVisibility(View.VISIBLE);
                amountPersonTv.setText("ปักหมุดเวลา: " + _detail.substring
                        (_detail.indexOf("%$&*-", 0) + 5, _detail.length()));
                detailBtn.setVisibility(View.GONE);
                break;
            case 7:
                titleTv.setText("ก่อสร้าง");
                nameTv.setText("ประเภท: " + mName);
                detailTv.setVisibility(View.VISIBLE);
                amountPersonTv.setText("วันที่ปักหมุด: " + _detail.substring
                        (_detail.indexOf("%$&*-", 0) + 5, _detail.length()));
                detailBtn.setVisibility(View.GONE);
                break;
        }

        if (mType == 3 || mType == 4 || mType == 6 || mType == 7) {

            detailTv.setText("รายละเอียด: -");

            if (mType == 4) {
                if (_detail.length() > 0) {
                    detailTv.setText("รายละเอียด: " + _detail);
                }
            } else {
                if (_detail.substring(0, _detail.indexOf("%$&*-", 0)).length() > 0) {
                    detailTv.setText("รายละเอียด: " + _detail.substring(0, _detail.indexOf("%$&*-", 0)));
                }
            }
        }

        int _tempIndexString = mDistance.indexOf(" ");

        if (mDistance.substring(_tempIndexString + 1, _tempIndexString + 2).equals("ก")) {
            distanceTv.setText("ระยะทาง: " + mDistance.substring(0, _tempIndexString) + " กิโลเมตร");
        } else {
            distanceTv.setText("ระยะทาง: " + mDistance);
        }

        if(mDuration.indexOf("ชม.") != -1){
            durationTv.setText("ระยะเวลา: " + mDuration.substring(0,mDuration.indexOf("ชม.")) + " ชั่วโมง "
                    + mDuration.substring(6,mDuration.indexOf("น.")) + " นาที ");
        }else{
            durationTv.setText("ระยะเวลา: " + mDuration);
        }
    }

    private void showDetail() {
        if (mType == 0) {
            new ControlDatabase(this).getDatabaeTutorial("pricebus");
        } else {
            String _type = "BTS station";
            switch (mType) {
                case 2:
                    _type = "BRT station";
                    break;
                case 5:
                    _type = "Boat station";
                    break;
            }
            startActivity(new Intent(getApplicationContext(), DetailStation.class)
                    .putExtra("typeList", _type));
        }
    }

    public void setPicture(String[] _url) {

        final Dialog dialog = new Dialog(TrafficDetail.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // no titlebar
        dialog.setContentView(R.layout.activity_dialog_price_bus);

        final ImageView ImageView1 = (ImageView) dialog.findViewById(R.id.ImageView1);
        final ImageView ImageView2 = (ImageView) dialog.findViewById(R.id.ImageView2);
        final ImageView ImageView3 = (ImageView) dialog.findViewById(R.id.ImageView3);
        final ImageView ImageView4 = (ImageView) dialog.findViewById(R.id.ImageView4);
        final ImageView ImageView5 = (ImageView) dialog.findViewById(R.id.ImageView5);
        final ImageView ImageView6 = (ImageView) dialog.findViewById(R.id.ImageView6);
        final ImageView ImageView7 = (ImageView) dialog.findViewById(R.id.ImageView7);
        final ImageView ImageView8 = (ImageView) dialog.findViewById(R.id.ImageView8);

        final ImageView[] mResId = new ImageView[8];

        mResId[0] = ImageView1;
        mResId[1] = ImageView2;
        mResId[2] = ImageView3;
        mResId[3] = ImageView4;
        mResId[4] = ImageView5;
        mResId[5] = ImageView6;
        mResId[6] = ImageView7;
        mResId[7] = ImageView8;

        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

        for (int i = 0; i < _url.length; i++) {

            final int _temp = i;
            Glide.with(this).load(_url[i]).asBitmap().placeholder(R.drawable.loading)
                    .error(R.drawable.error).centerCrop().into(new BitmapImageViewTarget(mResId[i]) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCornerRadius(15);
                    mResId[_temp].setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }


    private void showStreetView(LatLng latLng, StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.getPanoramaCamera();

        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder
                (streetViewPanorama.getPanoramaCamera());
        builder.tilt(0.0f);
        builder.zoom(0.0f);
        builder.bearing(0.0f);
        streetViewPanorama.animateTo(builder.build(), 0);

        streetViewPanorama.setPosition(latLng, 300);
        streetViewPanorama.setStreetNamesEnabled(true);
    }

    private void sentReport() {
        final Dialog _dialog = new Dialog(TrafficDetail.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_report_traffic);

        mBackIm = (ImageView) _dialog.findViewById(R.id.btnClose);
        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final EditText _editText = (EditText) _dialog.findViewById(R.id.editText);

        Button _btnSend = (Button) _dialog.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (_editText.getText().length() > 1) {
                    new ControlDatabase(TrafficDetail.this).setDatabaseTrafficDetail(String.valueOf(mLat),
                            String.valueOf(mLng), String.valueOf(mType), mName, _editText.getText().toString());
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    public void getBundle() {
        Bundle bundle = getIntent().getExtras();
        mLat = bundle.getDouble("lat");
        mLng = bundle.getDouble("lng");
        mType = bundle.getInt("type");
        mDistance = bundle.getString("distance");
        mDuration = bundle.getString("duration");
        mName = bundle.getString("name");
        mDetail = bundle.getString("detail");
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        titleTv = (TextView) findViewById(R.id.titleTv);
        nameTv = (TextView) findViewById(R.id.nameTv);
        detailTv = (TextView) findViewById(R.id.detailTv);
        addressTv = (TextView) findViewById(R.id.addressTv);
        amountPersonTv = (TextView) findViewById(R.id.amountPersonTv);
        distanceTv = (TextView) findViewById(R.id.distanceTv);
        durationTv = (TextView) findViewById(R.id.durationTv);
        detailBtn = (Button) findViewById(R.id.detailBtn);
        reportBtn = (ImageView) findViewById(R.id.reportBtn);
        streetviewFm = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.streetviewFm);
        googleStreetLy = (LinearLayout) findViewById(R.id.googleStreetLy);
    }

}
