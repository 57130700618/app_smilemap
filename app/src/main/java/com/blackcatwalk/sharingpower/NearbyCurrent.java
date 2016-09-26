package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class NearbyCurrent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_current);
        getSupportActionBar().hide();

        ImageView backIm = (ImageView) findViewById(R.id.backIm);
        backIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Button currentBtn = (Button) findViewById(R.id.currentBtn);
        currentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DetailMap.class).putExtra("typeMoveMap", 7));
            }
        });

        Button customBtn = (Button) findViewById(R.id.customBtn);
        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DetailMap.class).putExtra("typeMoveMap", 8));
            }
        });
    }
}
