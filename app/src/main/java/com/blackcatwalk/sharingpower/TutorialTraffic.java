package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

public class TutorialTraffic extends AppCompatActivity {

    private ImageView mBackIm;
    private ImageView mVideoIm;

    private ImageView mTutorial_p1Im;
    private ImageView mTutorial_p2Im;
    private ImageView mTutorial_p3Im;

    ImageView[] mResId = new ImageView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tutorial_traffic);

        bindWidget();
        mResId[0] = mTutorial_p1Im;
        mResId[1] = mTutorial_p2Im;
        mResId[2] = mTutorial_p3Im;

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        mVideoIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/SmileMap.social/videos")));
            }
        });


        new ControlDatabase(this).getDatabaeTutorial("traffic");
    }

    public void setPicture(String[] _url) {

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

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mVideoIm = (ImageView) findViewById(R.id.videoIm);
        mTutorial_p1Im = (ImageView) findViewById(R.id.tutorial_p1Im);
        mTutorial_p2Im = (ImageView) findViewById(R.id.tutorial_p2Im);
        mTutorial_p3Im = (ImageView) findViewById(R.id.tutorial_p3Im);
    }
}


