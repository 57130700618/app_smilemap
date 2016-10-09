
package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.camera.CameraIntentHelperActivity;
import com.blackcatwalk.sharingpower.camera.PreviewPicture;
import com.blackcatwalk.sharingpower.camera.UploadImageUtils;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.FileOutputStream;
import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile extends CameraIntentHelperActivity {

    private String tempStatus;
    private ControlDatabase mControlDatabase;
    private String mTempName;
    private Bitmap mPhotoBitMap;
    private String mUploadedFileName;
    private final String[] mMenuUpload = {"ถ่ายรูป", "เลือกจากอัลบั้ม", "ยกเลิก"};
    private boolean mCheckuploadPicture = false;

    // ------------- User Interface ----------------//

    private ImageView mBackIm;
    private ImageView mMenuIm;
    private CircleImageView mImageProfile;
    private TextView mNameTv;
    private TextView mCountTrafficTv;
    private TextView mCountLocationTv;
    private TextView mPointTv;
    private TextView mStausDetailTv;
    private TextView mNickNameTv;
    private TextView mNickNameDetailTv;
    private TextView mSumHourTv;
    private ImageView mTakePhotoIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        mControlDatabase = new ControlDatabase(this);

        bindWidget();
        setUpEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCheckuploadPicture == false) {
            mControlDatabase.getDatabaseProfile();
        } else {
            mCheckuploadPicture = false;
        }
    }


    private void setUpEvent() {
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
                mCheckuploadPicture = false;
                startActivity(new Intent(getApplicationContext(), ProfileSetting.class)
                        .putExtra("name", mTempName).putExtra("staus", tempStatus));
            }
        });

        mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewPicture();
            }
        });

        mTakePhotoIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChageImageProfile();
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

    private void previewPicture() {

        try {
            mCheckuploadPicture = false;

            String _filename = "bitmap.png";
            FileOutputStream stream = getApplicationContext().
                    openFileOutput(_filename, Context.MODE_PRIVATE);
            mPhotoBitMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            startActivity(new Intent(getApplicationContext(),
                    PreviewPicture.class).putExtra("image", _filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogChageImageProfile() {
        new AlertDialog.Builder(this).setTitle("เพิ่มรูปภาพ").setItems(mMenuUpload,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != 2) {
                            switch (which) {
                                case 0:
                                    startCameraIntent(800);
                                    break;
                                case 1:
                                    startGalleryIntent(800);
                                    break;
                            }
                            mCheckuploadPicture = true;
                        }
                        dialog.cancel();
                    }
                }).show();
    }


    public void setProfile(String _urlPicure, String _tempName, String _sex, double _countTraffic, double _countLocation, double _point
            , String _staus, int sum_hour) {

        NumberFormat temp = NumberFormat.getInstance();
        double tempDouble;

        mTempName = _tempName;

        mNameTv.setText(_tempName);

        if (_urlPicure.length()<=1) {
            if (_sex.equals("หญิง")) {
                mImageProfile.setImageResource(R.drawable.sex_female);
            } else {
                mImageProfile.setImageResource(R.drawable.sex_male);
            }
        }else{
            Glide.with(this).load(_urlPicure).asBitmap().placeholder(R.drawable.loading)
                    .error(R.drawable.error).into(new BitmapImageViewTarget(mImageProfile) {
                @Override
                protected void setResource(Bitmap resource) {
                    mPhotoBitMap = resource;
                    mImageProfile.setImageBitmap(resource);
                }
            });
        }

        mTakePhotoIm.setVisibility(View.VISIBLE);

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

    //----------------- photo upload --------------------

    @Override
    protected void onPhotoUriFound(Uri _photoUri, Bitmap _photoBitMap, String _filePath) {
        super.onPhotoUriFound(_photoUri, _photoBitMap, _filePath);

        mPhotoBitMap = _photoBitMap;

        mImageProfile.setImageBitmap(mPhotoBitMap);
        new UploadImageTask().execute(mControlDatabase.getMSetDatabase());
    }

    public class UploadImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            UploadImageUtils _uploadImageUtils = new UploadImageUtils();

            String _url = params[0];
            mUploadedFileName = _uploadImageUtils.getRandomFileName();
            String _result = _uploadImageUtils.uploadFile(mUploadedFileName,
                    _url, mPhotoBitMap);
            return _result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result == null && mPhotoBitMap != null) {
                alertDialogNotSuccess();
                return;
            }
            mControlDatabase.setDatabaseProfileUploadPic(result);
        }
    }

    public void alertDialogNotSuccess() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("โพสต์ไม่สำเร็จ กรุณาโพสต์ใหม่อีกครั้ง");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }

    private void clearTempPic() {
        //glide don't use .recycle(), So when call .recycle() glide is error now
        if (mPhotoBitMap != null) {
            mPhotoBitMap.recycle();
        }
        mPhotoBitMap = null;
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mMenuIm = (ImageView) findViewById(R.id.menuIm);
        mImageProfile = (CircleImageView) findViewById(R.id.imageProfile);
        mTakePhotoIm = (ImageView) findViewById(R.id.takePhotoIm);
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
