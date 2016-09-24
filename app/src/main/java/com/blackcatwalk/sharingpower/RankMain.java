package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankMain extends AppCompatActivity {

    private static final String url = "https://www.smilemap.me/android/get.php?main=rank";
    private List<Rank> rankList = new ArrayList<Rank>();
    private ListView listView;
    private RankCustomListAdapter adapter;

    // ------------- FIle System ----------------//
    private static final String fileName = "stausRank.txt";
    private static final String fileUserName = "Username.txt";
    private static final int readSize = 100; // Read 100byte
    private String stausRank = "0";
    private String username;

    // ----------- Url set form database --------------//
    private String setUrl = "https://www.smilemap.me/android/set.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        getSupportActionBar().hide(); // hide ActionBar

        Control.sDialog(RankMain.this);
        readFile(fileUserName);

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        ImageView settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readFile(fileName);

                final Dialog dialog = new Dialog(RankMain.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_rank_setting);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                final Switch showRank = (Switch) dialog.findViewById(R.id.showRank);
                showRank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean on = ((Switch) v).isChecked();
                        if(on) {
                            stausRank = "1";
                        }
                        else {
                            stausRank = "0";
                        }
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest jor = new StringRequest(Request.Method.POST, setUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        saveFile();
                                        rankList = null;
                                        rankList = new ArrayList<Rank>();
                                        adapter = new RankCustomListAdapter(RankMain.this, rankList);
                                        listView.setAdapter(adapter);

                                        Control.sDialog(RankMain.this);
                                        getDatabase();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "บันทึกไม่สำเร็จ", Toast.LENGTH_LONG);
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("main", "profile");
                                params.put("job", "changeStausRank");
                                params.put("username", username);
                                params.put("name", stausRank);
                                return params;
                            }
                        };
                        jor.setShouldCache(false);
                        requestQueue.add(jor);
                    }
                });

                if (stausRank.equals("1")) {
                    showRank.setChecked(true);
                }
                dialog.show();
            }
        });

      listView = (ListView) findViewById(R.id.list_view);
        adapter = new RankCustomListAdapter(this, rankList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Control.sDialog(RankMain.this);
                Rank item = rankList.get(position);
                showDetail(item.getName(), item.getNickName(), item.getStaus(), position);
            }
        });
        getDatabase();
    }

    private void getDatabase() {
        final String getUrl = url + "&ramdom=" + Control.randomNumber();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                int count = 1;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Rank item = new Rank();
                        item.setSequeneNumber(String.valueOf(count));
                        item.setName(obj.getString("name"));
                        item.setStaus(obj.getString("staus"));
                        item.setSex(obj.getString("sex"));
                        item.setSumHour(obj.getInt("sum_hour") / 60);
                        item.setPoint(obj.getInt("point"));
                        //item.setPicture(obj.getString("picture"));

                        if (((obj.getInt("sum_hour") / 60) >= 0) && ((obj.getInt("sum_hour") / 60) <= 50)) {
                            item.setNickName("ผู้แบ่งปันเริ่มต้น");
                        } else if (((obj.getInt("sum_hour") / 60) > 50) && ((obj.getInt("sum_hour") / 60) <= 500)) {
                            item.setNickName("ผู้แบ่งปันขั้นกลาง");
                        } else {
                            item.setNickName("ผู้แบ่งปันสูงสูด");
                        }

                        count++;
                        rankList.add(item);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();

                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Control.hDialog();
                    }
                }, 3000);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }


    public void showDetail(String name, String nickName, String staus, int position) {
        final Dialog dialog = new Dialog(RankMain.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_rank_detail);

        ImageView crown = (ImageView) dialog.findViewById(R.id.crown);

        switch (position) {
            case 0:
                crown.setImageResource(R.drawable.icon_crown_gold);
                break;
            case 1:
                crown.setImageResource(R.drawable.icon_crown_sliver);
                break;
            case 2:
                crown.setImageResource(R.drawable.icon_crown_broze);
                break;
        }

        position++;
        TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText("อันดับ " + position);

        TextView nametemp = (TextView) dialog.findViewById(R.id.name);
        nametemp.setText(name);

        TextView nickNameTemp = (TextView) dialog.findViewById(R.id.nickName);
        nickNameTemp.setText(nickName);


        if(staus.length() > 0){
            TextView stausTemp = (TextView) dialog.findViewById(R.id.staus);
            stausTemp.setVisibility(View.VISIBLE);
            stausTemp.setText(staus);
        }

        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Control.hDialog();

        dialog.show();
    }

    private void readFile(String tempFileName) {

        String temp = "";

        try {
            FileInputStream fIn = openFileInput(tempFileName);
            InputStreamReader reader = new InputStreamReader(fIn);

            char[] buffer = new char[readSize];
            int charReadCount;
            while ((charReadCount = reader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charReadCount);

                temp += readString;
                buffer = new char[readSize];
            }
            reader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (temp.length() > 0) {
            switch (tempFileName) {
                case "Username.txt":
                    username = temp;
                    break;
                case "stausRank.txt":
                    stausRank = temp.substring(0,temp.length());
                    break;
            }
        }
    }

    private void saveFile(){
        try {
            FileOutputStream fOut = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(stausRank);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
