package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Tutorial extends AppCompatActivity {

    private ImageView back;
    private ImageView videoIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("type").equals("traffic")) {
            setContentView(R.layout.activity_tutorial_traffic);
        } else {
            setContentView(R.layout.activity_tutorial_location);
        }

        getSupportActionBar().hide();

        bindWidget();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        videoIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/SmileMap.social/videos")));
            }
        });
    }

    private void bindWidget() {
        back = (ImageView) findViewById(R.id.backIm);
        videoIm = (ImageView) findViewById(R.id.videoIm);
    }
}


/*

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                if (x1 < x2) {   // left to right
                    showPage(1);
                }else if (x1 > x2) {  // right to left
                    showPage(2);
                }else if (y1 < y2) { //  UP to Down
                }else if (y1 > y2) { //  Down to UP
                }
                break;
            }
        }
        return false;
    }


    void showPage(int page){
        switch (page){
            case 1:
                backLy.setVisibility(View.INVISIBLE);
                nextLy.setVisibility(View.VISIBLE);
                tutorialIm.setImageResource(R.drawable.tutorial_a);
                break;
            case 2:
                nextLy.setVisibility(View.INVISIBLE);
                backLy.setVisibility(View.VISIBLE);
                tutorialIm.setImageResource(R.drawable.tutorial_b);
                break;
        }
    }

*/
