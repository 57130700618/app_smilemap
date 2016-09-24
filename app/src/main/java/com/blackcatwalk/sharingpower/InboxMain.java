package com.blackcatwalk.sharingpower;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InboxMain extends AppCompatActivity {

    // ----------- User Interface  --------------//
    private List<Inbox> InboxList = new ArrayList<Inbox>();
    private ListView listView;
    private InboxCustomListAdapter adapter;

    // ----------- Url get form database --------------//
    private static final String url = "https://www.smilemap.me/android/get.php?main=inbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_main);
        getSupportActionBar().hide(); // hide ActionBar

        Control.sDialog(this);

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new InboxCustomListAdapter(this, InboxList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //try to open page in facebook native app.
                    String uri = "fb://page/" + "704870926322640";    //Cutsom URL
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }catch (ActivityNotFoundException ex){
                    //facebook native app isn't available, use browser.
                    String uri = "http://facebook.com/SmileMap.social";  //Normal URL
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(i);
                }
            }
        });
        getDatabase();
    }

    public void getDatabase(){

        String getUrl = url + "&ramdom=" + Control.randomNumber();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getUrl, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Inbox item = new Inbox();
                        item.setTitle(obj.getString("title"));
                        item.setDetail(obj.getString("detail"));

                        //add to array
                        InboxList.add(item);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                Control.hDialog();
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }
}


