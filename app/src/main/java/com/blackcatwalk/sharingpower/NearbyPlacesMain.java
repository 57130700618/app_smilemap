package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.blackcatwalk.sharingpower.customAdapter.NearByPlaceCustomListAdapter;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;


public class NearbyPlacesMain extends AppCompatActivity {

    private ImageView mBackIm;
    private ListView mListView;
    private NearByPlaceCustomListAdapter mAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_location);
        getSupportActionBar().hide(); // hide ActionBar

        bindWidget();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailStation.class);
                switch (position) {
                    case 0:
                        intent.putExtra("typeList", "BTS station");
                        break;
                    case 1:
                        intent.putExtra("typeList", "MRT station");
                        break;
                    case 2:
                        intent.putExtra("typeList", "Airport Rail Link");
                        break;
                    case 3:
                        intent.putExtra("typeList", "BRT station");
                        break;
                    case 4:
                        intent.putExtra("typeList", "Boat station");
                        break;
                    case 5:
                        intent.putExtra("typeList", "Temples");
                        break;
                }
                startActivity(intent);
            }
        });

        new ControlDatabase(this).getDatabaeTutorial("nearby");
    }


    public void setPicture(String[] _url) {
        String[] list = {"BTS station", "MRT station"
                , "Airport Rail Link", "BRT station", "Boat station", "Temples"};

        mAdpter = new NearByPlaceCustomListAdapter(getApplicationContext(), list, _url);
        mListView.setAdapter(mAdpter);
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mListView = (ListView) findViewById(R.id.listView);
    }
}
