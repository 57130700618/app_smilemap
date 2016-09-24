package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class Island extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_island);
        getSupportActionBar().hide();

        ImageView profileBtn = (ImageView) findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });

        ImageView rank = (ImageView) findViewById(R.id.rank);
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), RankMain.class);
                startActivity(intent);
            }
        });

        ImageView nearbyCurrent = (ImageView) findViewById(R.id.nearbyCurrent);
        nearbyCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), NearbyCurrent.class);
                startActivity(intent);
            }
        });

        ImageView nearbyLocation = (ImageView) findViewById(R.id.nearbyLocation);
        nearbyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), NearbyPlacesMain.class);
                startActivity(intent);
            }
        });

        ImageView call = (ImageView) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), CallEmergency.class);
                startActivity(intent);
            }
        });

        ImageView setting = (ImageView) findViewById(R.id.settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
            }
        });

    }
}
