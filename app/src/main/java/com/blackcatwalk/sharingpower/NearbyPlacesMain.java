package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class NearbyPlacesMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_location);
        getSupportActionBar().hide(); // hide ActionBar

        TextView headName = (TextView) findViewById(R.id.headName);
        headName.setText("รอบตัวสถานที่");

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        int[] resId = {R.drawable.nearby_1
                , R.drawable.nearby_2
                , R.drawable.nearby_3
                , R.drawable.nearby_4
                , R.drawable.nearby_5
                , R.drawable.nearby_6};

        String[] list = {"BTS station", "MRT station"
                , "Airport Rail Link", "BRT station", "Boat station", "Temples"};

        NearByPlaceCustomListAdapter adapter = new NearByPlaceCustomListAdapter(getApplicationContext(), list, resId);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }
}
