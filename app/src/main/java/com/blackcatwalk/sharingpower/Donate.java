package com.blackcatwalk.sharingpower;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


public class Donate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_donate);
        getSupportActionBar().hide(); // hide ActionBar

        ImageView backIm = (ImageView) findViewById(R.id.backIm);
        backIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }
}
