package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.google.GoogleMapAddress;
import com.blackcatwalk.sharingpower.google.GoogleNavigator;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class LocationDetail extends AppCompatActivity {

    private ControlDatabase mControlDatabase;
    private String mType;
    private String mDetail;
    private Double mLat;
    private Double mLng;
    private String mDuration;
    private String mDistance;

    // ----------- User Interface  --------------//
    private ImageView mBackIm;
    private TextView titleTv;
    private ImageView favoriteBtn;
    private ImageView reportBtn;
    private SupportStreetViewPanoramaFragment streetViewFracment;
    private LinearLayout googleStreetLy;
    private Button mVoteBtn;
    private Button mCommentBtn;
    private TextView mFiveStarTv;
    private TextView mFourStarTv;
    private TextView mThreeStarTv;
    private TextView mTwoStarTv;
    private TextView mOneStarTv;
    private TextView mDetailLocationTv;
    private TextView addressTv;
    private TextView distanceTv;
    private TextView durationTv;
    private Button mNavigationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        getSupportActionBar().hide();

        bindWidget();

        setContent();

        mControlDatabase = new ControlDatabase(this);

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        streetViewFracment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
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

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LocationCommentMain.class)
                        .putExtra("type", mType).putExtra("lat", mLat).putExtra("lng", mLng));
            }
        });

        mVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogVote();
            }
        });

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFavorite();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogReport();
            }
        });

        mNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GoogleNavigator().showDialogNavigator(LocationDetail.this,String.valueOf(mLat),String.valueOf(mLng));
            }
        });

        mControlDatabase.getDatabaseLocationDetail("vote&lat=" + mLat + "&lng=" + mLng);
    }

    private void setContent() {
        Bundle bundle = getIntent().getExtras();
        mType = bundle.getString("type");
        mLat = bundle.getDouble("lat");
        mLng = bundle.getDouble("lng");
        mDetail = bundle.getString("detail");
        mDuration = bundle.getString("duration");
        mDistance = bundle.getString("distance");

        switch (mType) {
            case "restroom":
                titleTv.setText("ห้องน้ำ");
                break;
            case "pharmacy":
                titleTv.setText("ร้านขายยา");
                break;
            case "veterinary_clinic":
                titleTv.setText("รักษาสัตว์");
                break;
            case "daily_home":
                titleTv.setText("ที่พักรายวัน");
                break;
            case "officer":
                titleTv.setText("จุดพักผ่อน");
                break;
            case "garage":
                titleTv.setText("ร้านปะยาง");
                break;
        }

        if (mDetail.length() > 0) {
            mDetailLocationTv.setText(mDetail);
        }else {
            mDetailLocationTv.setVisibility(View.GONE);
        }

        addressTv.setText(GoogleMapAddress.getAddress(this, mLat, mLng));

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

    private void showDialogVote() {
        final Dialog _dialog = new Dialog(LocationDetail.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_vote);

        final RatingBar ratingbar = (RatingBar) _dialog.findViewById(R.id.ratingbar);
        LayerDrawable stars = (LayerDrawable) ratingbar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FF4081"), PorterDuff.Mode.SRC_ATOP);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Button ok = (Button) _dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlDatabase.setVoteDatabaseLocationDetail(String.valueOf(mLat).toString(),
                        String.valueOf(mLng).toString(), String.valueOf((int) ratingbar.getRating()));
                _dialog.cancel();
            }
        });
        _dialog.show();
    }

    private void showDialogFavorite() {
        final Dialog _dialog = new Dialog(LocationDetail.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_favorite_detail);

        final EditText _editDetailName = (EditText) _dialog.findViewById(R.id.editDetailName);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Button save = (Button) _dialog.findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_editDetailName.getText().toString().length() >1){
                    mControlDatabase.setFavoriteDatabaseLocationDetail( String.valueOf(mLat).toString(),
                            String.valueOf(mLng).toString(), _editDetailName.getText().toString(), mType);
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    private void showDialogReport() {
        final Dialog _dialog = new Dialog(LocationDetail.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_report_location);

        mBackIm = (ImageView) _dialog.findViewById(R.id.btnClose);
        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final EditText _editText = (EditText) _dialog.findViewById(R.id.editText);

        Button btnSend = (Button) _dialog.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(_editText.getText().length() >1) {
                    mControlDatabase.setReportDatabaseLocationDetail(String.valueOf(mLat).toString(),
                            String.valueOf(mLng).toString(), _editText.getText().toString());
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    public void setVote(String _case, String _valueVote) {

        switch (_case) {
            case "5":
                mFiveStarTv.setText(_valueVote);
                break;
            case "4":
                mFourStarTv.setText(_valueVote);
                break;
            case "3":
                mThreeStarTv.setText(_valueVote);
                break;
            case "2":
                mTwoStarTv.setText(_valueVote);
                break;
            case "1":
                mOneStarTv.setText(_valueVote);
                break;
        }
    }

    private void showStreetView(LatLng latLng, StreetViewPanorama streetViewPanorama) {
        if (streetViewPanorama == null) {
            return;
        }
        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder(streetViewPanorama.getPanoramaCamera());
        builder.tilt(0.0f);
        builder.zoom(0.0f);
        builder.bearing(0.0f);
        streetViewPanorama.animateTo(builder.build(), 0);

        streetViewPanorama.setPosition(latLng, 300);
        streetViewPanorama.setStreetNamesEnabled(true);
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        titleTv = (TextView) findViewById(R.id.titleTv);
        favoriteBtn = (ImageView) findViewById(R.id.favoriteBtn);
        reportBtn = (ImageView) findViewById(R.id.reportBtn);
        streetViewFracment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        googleStreetLy = (LinearLayout) findViewById(R.id.googleStreetLy);
        mCommentBtn = (Button) findViewById(R.id.commentBtn);
        mVoteBtn = (Button) findViewById(R.id.voteBtn);
        mOneStarTv = (TextView) findViewById(R.id.oneStarTv);
        mTwoStarTv = (TextView) findViewById(R.id.twoStarTv);
        mThreeStarTv = (TextView) findViewById(R.id.threeStarTv);
        mFourStarTv = (TextView) findViewById(R.id.fourStarTv);
        mFiveStarTv = (TextView) findViewById(R.id.fiveStarTv);
        mDetailLocationTv = (TextView) findViewById(R.id.detailLocationTv);
        addressTv = (TextView) findViewById(R.id.addressTv);
        distanceTv = (TextView) findViewById(R.id.distanceTv);
        durationTv = (TextView) findViewById(R.id.durationTv);
        mNavigationBtn = (Button) findViewById(R.id.navigationBtn);
    }
}


