package com.blackcatwalk.sharingpower.camera;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class CameraIntentHelperActivity extends AppCompatActivity {
    private final String DATE_CAMERA_INTENT_STARTED_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.dateCameraIntentStarted";
    private final String CAMERA_PIC_URI_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.CAMERA_PIC_URI_STATE";
    private final String PHOTO_URI_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.PHOTO_URI_STATE";
    private final String ROTATE_X_DEGREES_STATE = "de.ecotastic.android.camerautil.example.TakePhotoActivity.ROTATE_X_DEGREES_STATE";

    private Date dateCameraIntentStarted = null;

    private Uri preDefinedCameraUri = null;

    private Uri photoUriIn3rdLocation = null;

    private Uri photoUri = null;

    private int rotateXDegrees = 0;

    private int mMaxPixel = 0;

    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    private Bitmap mPhotoBitMap;
    private DateParser mDateParser;
    private BitmapHelper mBitmapHelper;
    private UploadImageUtils mUploadImageUtils;


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (dateCameraIntentStarted != null) {
            savedInstanceState.putString(DATE_CAMERA_INTENT_STARTED_STATE, mDateParser.dateToString(dateCameraIntentStarted));
        }
        if (preDefinedCameraUri != null) {
            savedInstanceState.putString(CAMERA_PIC_URI_STATE, preDefinedCameraUri.toString());
        }
        if (photoUri != null) {
            savedInstanceState.putString(PHOTO_URI_STATE, photoUri.toString());
        }
        savedInstanceState.putInt(ROTATE_X_DEGREES_STATE, rotateXDegrees);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(DATE_CAMERA_INTENT_STARTED_STATE)) {
            dateCameraIntentStarted = mDateParser.stringToDate(savedInstanceState.getString(DATE_CAMERA_INTENT_STARTED_STATE));
        }
        if (savedInstanceState.containsKey(CAMERA_PIC_URI_STATE)) {
            preDefinedCameraUri = Uri.parse(savedInstanceState.getString(CAMERA_PIC_URI_STATE));
        }
        if (savedInstanceState.containsKey(PHOTO_URI_STATE)) {
            photoUri = Uri.parse(savedInstanceState.getString(PHOTO_URI_STATE));
        }
        rotateXDegrees = savedInstanceState.getInt(ROTATE_X_DEGREES_STATE);
    }

    protected void startCameraIntent(final int maxPixel) {
        mMaxPixel = maxPixel;

        initObject();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String manufacturer = android.os.Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);
                String model = android.os.Build.MODEL.toLowerCase(Locale.ENGLISH);
                String buildType = android.os.Build.TYPE.toLowerCase(Locale.ENGLISH);
                String buildDevice = android.os.Build.DEVICE.toLowerCase(Locale.ENGLISH);
                String buildId = android.os.Build.ID.toLowerCase(Locale.ENGLISH);

                boolean setPreDefinedCameraUri = false;
                if (!(manufacturer.contains("samsung")) && !(manufacturer.contains("sony"))) {
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("galaxy nexus")) { //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("samsung") && model.contains("gt-n7000") && buildId.contains("imm76l")) { //TESTED
                    setPreDefinedCameraUri = true;
                }

                if (buildType.contains("userdebug") && buildDevice.contains("ariesve")) {  //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("crespo")) {   //TESTED
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9100")) { //TESTED
                    setPreDefinedCameraUri = true;
                }

                /////////////////////////  TEST ////////////////////////////////
                if (manufacturer.contains("samsung")) { //T-Mobile LTE enabled Samsung S3
                    setPreDefinedCameraUri = true;
                }
                if (buildDevice.contains("cooper")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t0lte")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("kot49h")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("t03g")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9300")) {
                    setPreDefinedCameraUri = true;
                }
                if (buildType.contains("userdebug") && buildDevice.contains("gt-i9195")) {
                    setPreDefinedCameraUri = true;
                }
                if (manufacturer.contains("asus")) {
                    setPreDefinedCameraUri = true;
                }

                dateCameraIntentStarted = new Date();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (setPreDefinedCameraUri) {
                    String filename = System.currentTimeMillis() + "tx.jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    preDefinedCameraUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, preDefinedCameraUri);
                }
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
               // logException(e);
                onCouldNotTakePhoto();
            }
        } else {
            onSdCardNotMounted();
        }
    }

    protected void startGalleryIntent(final int maxPixel) {
        mMaxPixel = maxPixel;

        initObject();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void initObject() {
        mUploadImageUtils = new UploadImageUtils();
        mDateParser = new DateParser();
        mBitmapHelper = new BitmapHelper();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                onCameraIntentResult(resultCode, intent);
                break;
            }
            case GALLERY_IMAGE_ACTIVITY_REQUEST_CODE: {
                try {
                    photoUri = intent.getData();

                    final String filePath = mUploadImageUtils.getImageFilePath(photoUri, this);
                    mPhotoBitMap = mBitmapHelper.readBitmap(this, photoUri);

                    getOrientationRotate(filePath);

                    if (mPhotoBitMap != null && mMaxPixel != -1) {
                        mPhotoBitMap = BitmapHelper.shrinkBitmap(mPhotoBitMap, mMaxPixel, rotateXDegrees);
                    }

                    onPhotoUriFound(photoUri, mPhotoBitMap, filePath);
                } catch (Exception e) {

                }
                break;
            }
        }
    }

    protected void onCameraIntentResult( int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Cursor myCursor = null;
            Date dateOfPicture = null;
            try {
                String[] largeFileProjection = {MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.ORIENTATION,
                        MediaStore.Images.ImageColumns.DATE_TAKEN};
                String largeFileSort = MediaStore.Images.ImageColumns._ID + " DESC";
                myCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        largeFileProjection,
                        null, null,
                        largeFileSort);
                myCursor.moveToFirst();

                if (!myCursor.isAfterLast()) {
                    String largeImagePath = myCursor.getString(myCursor
                            .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));

                    getOrientationRotate(largeImagePath);

                    photoUri = Uri.fromFile(new File(largeImagePath));
                    if (photoUri != null) {
                        dateOfPicture = new Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                        if (dateOfPicture != null && dateOfPicture.after(dateCameraIntentStarted)) {
                        } else {
                            photoUri = null;
                        }
                    }
                    if (myCursor.moveToNext() && !myCursor.isAfterLast()) {
                        String largeImagePath3rdLocation = myCursor.getString(myCursor
                                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                        Date dateOfPicture3rdLocation = new Date(myCursor.getLong(myCursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                        if (dateOfPicture3rdLocation != null && dateOfPicture3rdLocation.after(dateCameraIntentStarted)) {
                            photoUriIn3rdLocation = Uri.fromFile(new File(largeImagePath3rdLocation));
                        }
                    }
                }
            } catch (Exception e) {
              //  logException(e);
            } finally {
                if (myCursor != null && !myCursor.isClosed()) {
                    myCursor.close();
                }
            }

            if (photoUri == null) {
                try {
                    photoUri = intent.getData();
                } catch (Exception e) {
                }
            }

            if (photoUri == null) {
                photoUri = preDefinedCameraUri;
            }

            try {
                if (photoUri != null && new File(photoUri.getPath()).length() <= 0) {
                    if (preDefinedCameraUri != null) {
                        Uri tempUri = photoUri;
                        photoUri = preDefinedCameraUri;
                        preDefinedCameraUri = tempUri;
                    }
                }
            } catch (Exception e) {
            }

            photoUri = getFileUriFromContentUri(photoUri);
            preDefinedCameraUri = getFileUriFromContentUri(preDefinedCameraUri);
            try {
                if (photoUriIn3rdLocation != null) {
                    if (photoUriIn3rdLocation.equals(photoUri) || photoUriIn3rdLocation.equals(preDefinedCameraUri)) {
                        photoUriIn3rdLocation = null;
                    } else {
                        photoUriIn3rdLocation = getFileUriFromContentUri(photoUriIn3rdLocation);
                    }
                }
            } catch (Exception e) {
            }

            if (photoUri != null) {
                final String filePath = mUploadImageUtils.getImageFilePath(photoUri, this);
                mPhotoBitMap = mBitmapHelper.readBitmap(this, photoUri);
                mPhotoBitMap = mBitmapHelper.readBitmap(this, photoUri);

                getOrientationRotate(filePath);

                if (mPhotoBitMap != null && mMaxPixel != -1) {
                    mPhotoBitMap = BitmapHelper.shrinkBitmap(mPhotoBitMap, mMaxPixel, rotateXDegrees);
                }
                onPhotoUriFound(photoUri, mPhotoBitMap, filePath);
            } else {
                //onPhotoUriNotFound();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
          //  onCanceled();
        } else {
          //  onCanceled();
        }
    }

    protected void onCouldNotTakePhoto() {
        Toast.makeText(getApplicationContext(), "Could not take photo", Toast.LENGTH_LONG).show();
    }

    protected void onSdCardNotMounted() {
        Toast.makeText(getApplicationContext(), "Could not mount sdcard", Toast.LENGTH_LONG).show();
    }

    private Uri getFileUriFromContentUri(Uri cameraPicUri) {
        try {
            if (cameraPicUri != null
                    && cameraPicUri.toString().startsWith("content")) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(cameraPicUri, proj, null, null, null);
                cursor.moveToFirst();
                String largeImagePath = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
                return Uri.fromFile(new File(largeImagePath));
            }
            return cameraPicUri;
        } catch (Exception e) {
            return cameraPicUri;
        }
    }

    public void getOrientationRotate(final String photoPath) {

       rotateXDegrees = 0;
       ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateXDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateXDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotateXDegrees = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onPhotoUriFound(Uri _photoUri, Bitmap _photoBitMap, final String _filePath) {
        //logMessage("Your photo is stored under: " + _photoUri.toString());
    }



}


/*

Log event

  protected void logMessage(String exceptionMessage) {
        //Log.d(getClass().getName(), exceptionMessage);
    }


    protected void onPhotoUriNotFound() {
        logMessage("Could not find a photoUri that is != null");
    }

    protected void onCanceled() {
        logMessage("Camera Intent was canceled");
    }


    protected void logException(Exception exception) {
        logMessage(exception.toString());
    }


    protected void logMessage(String exceptionMessage) {
        //Log.d(getClass().getName(), exceptionMessage);
    }
*/
