package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Tutorial extends AppCompatActivity {

    private ImageView mBackIm;
    private ImageView mVideoIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        if (getIntent().getExtras().getString("type").equals("traffic")) {
            setContentView(R.layout.activity_tutorial_traffic);
        } else {
            setContentView(R.layout.activity_tutorial_location);
        }

        bindWidget();

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
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mVideoIm = (ImageView) findViewById(R.id.videoIm);
    }
}
