package com.blackcatwalk.sharingpower.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.blackcatwalk.sharingpower.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.FileInputStream;

public class PreviewPicture extends AppCompatActivity {

    // ---------------------- User Interface -------------------
    private SubsamplingScaleImageView mImageBookIm;
    private ImageView mBackIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_picture);
        getSupportActionBar().hide();

        bindWidget();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Bitmap _bmp = null;
        String _filename = getIntent().getStringExtra("image");
        try {
            FileInputStream _is = this.openFileInput(_filename);
            _bmp = BitmapFactory.decodeStream(_is);
            _is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mImageBookIm.setImage(ImageSource.bitmap(_bmp));
        mImageBookIm.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
    }

    private void bindWidget() {
        mImageBookIm = (SubsamplingScaleImageView) findViewById(R.id.imageBookIm);
        mBackIm = (ImageView) findViewById(R.id.backIm);
    }
}
