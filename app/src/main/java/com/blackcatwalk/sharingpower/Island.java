package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class Island extends AppCompatActivity {

    private ImageView mProfileBtn;
    private ImageView mNearbyLocationBtn;
    private ImageView mNearbyCurrentBtn;
    private ImageView mCallBtn;
    private ImageView mSettingsBtn;
    private ImageView mRankBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);
        getSupportActionBar().hide();

        bindWidget();

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        mRankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RankMain.class));
            }
        });

        mNearbyCurrentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NearbyCurrent.class));
            }
        });


        mNearbyLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NearbyPlacesMain.class));
            }
        });


        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CallEmergencyMain.class));
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Setting.class));
            }
        });
    }

    private void bindWidget() {
        mProfileBtn = (ImageView) findViewById(R.id.profileBtn);
        mRankBtn = (ImageView) findViewById(R.id.rankBtn);
        mCallBtn = (ImageView) findViewById(R.id.callBtn);
        mNearbyCurrentBtn = (ImageView) findViewById(R.id.nearbyCurrentBtn);
        mNearbyLocationBtn = (ImageView) findViewById(R.id.nearbyLocationBtn);
        mSettingsBtn = (ImageView) findViewById(R.id.settingsBtn);
    }
}
