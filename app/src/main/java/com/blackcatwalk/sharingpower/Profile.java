
package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.utility.ControlDatabase;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {


    private String tempStatus;
    private ControlDatabase mControlDatabase;
    private String mTempName;

    // ------------- User Interface ----------------//

    private ImageView mBackIm;
    private ImageView mMenuIm;
    private CircleImageView imageProfile;
    private TextView mNameTv;
    private TextView mCountTrafficTv;
    private TextView mCountLocationTv;
    private TextView mPointTv;
    private TextView mStausDetailTv;
    private TextView mNickNameTv;
    private TextView mNickNameDetailTv;
    private TextView mSumHourTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        bindWidget();

        mControlDatabase = new ControlDatabase(this);

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mMenuIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileSetting.class)
                .putExtra("name", mTempName).putExtra("staus", tempStatus));
            }
        });

        mNickNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailNickName();
            }
        });

        mNickNameDetailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailNickName();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mControlDatabase.getDatabaseProfile();
    }

    public void setProfile(String _tempName,String _sex,double _countTraffic,double _countLocation,double _point
                      ,String _staus,int sum_hour) {
        NumberFormat temp = NumberFormat.getInstance();
        double tempDouble;

        mTempName = _tempName;

        mNameTv.setText(_tempName);


        /*  picture = jsonObject.getString("picture");
             if(picture.length() > 0){
                  Glide.with(Profile.this).load(picture).into(imageProfile);
             }else */


        if (_sex.equals("หญิง")) {
            imageProfile.setImageResource(R.drawable.sex_female);
        } else {
            imageProfile.setImageResource(R.drawable.sex_male);
        }

        tempDouble = _countTraffic;
        if (tempDouble > 0) {
            mCountTrafficTv.setText(temp.format(tempDouble).toString());
        } else {
            mCountTrafficTv.setText("-");
        }

        tempDouble = _countLocation;
        if (tempDouble > 0) {
            mCountLocationTv.setText(temp.format(tempDouble).toString());
        } else {
            mCountLocationTv.setText("-");
        }

        tempDouble = _point;
        if (tempDouble > 0) {
            mPointTv.setText(temp.format(tempDouble).toString());
        } else {
            mPointTv.setText("-");
        }

        tempStatus = _staus;

        if (tempStatus.length() > 0) {
            mStausDetailTv.setVisibility(View.VISIBLE);
            mStausDetailTv.setText(tempStatus);
        } else {
            mStausDetailTv.setVisibility(View.GONE);
        }

        if (((sum_hour / 60) >= 0) && ((sum_hour / 60) <= 50)) {
            mNickNameDetailTv.setText("ผู้แบ่งปันเริ่มต้น");
        } else if (((sum_hour / 60) > 50) && ((sum_hour / 60) <= 500)) {
            mNickNameDetailTv.setText("ผู้แบ่งปันขั้นกลาง");
        } else {
            mNickNameDetailTv.setText("ผู้แบ่งปันสูงสูด");
        }

        tempDouble = (double) (sum_hour / 60);
        if (tempDouble > 0) {
            mSumHourTv.setText(temp.format(tempDouble).toString() + " ชั่วโมง");
        } else {
            mSumHourTv.setText("- ชั่วโมง");
        }

        /*         final Handler handler2 = new Handler();
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ControlProgress.hideDialog();
                                    }
                                }, 3000);*/
    }

    public void dialogDetailNickName() {
        final Dialog _dialog = new Dialog(Profile.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_nickname_detail);

        ImageView _closeIm = (ImageView) _dialog.findViewById(R.id.btnClose);
        _closeIm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        _dialog.show();
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mMenuIm = (ImageView) findViewById(R.id.menuIm);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        mNameTv = (TextView) findViewById(R.id.nameTv);
        mCountTrafficTv = (TextView) findViewById(R.id.countTrafficTv);
        mCountLocationTv = (TextView) findViewById(R.id.countLocationTv);
        mPointTv = (TextView) findViewById(R.id.pointTv);
        mStausDetailTv = (TextView) findViewById(R.id.stausDetailTv);
        mNickNameTv = (TextView) findViewById(R.id.nickNameTv);
        mNickNameDetailTv = (TextView) findViewById(R.id.nickNameDetailTv);
        mSumHourTv = (TextView) findViewById(R.id.sumHourTv);
    }
}
