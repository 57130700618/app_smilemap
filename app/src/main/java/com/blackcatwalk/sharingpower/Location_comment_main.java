package com.blackcatwalk.sharingpower;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location_comment_main extends AppCompatActivity {

    // ----------- User Interface  --------------//
    private List<Location_comment> Location_comment = new ArrayList<Location_comment>();
    private ListView listView;
    private Location_comment_CustomListAdapter adapter;
    private ImageView send;

    // ----------------- Url to database ------------------//
    private String set = "https://www.smilemap.me/android/set.php";
    private String get = "https://www.smilemap.me/android/get.php?main=comment_location&lat=";
    private String tempUrl;

    // ---------------- Data  ------------------//
    private String type;
    private String detail;
    private Double lat;
    private Double lng;
    private String temp;
    private String username;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_comment);
        getSupportActionBar().hide(); // hide ActionBar

        Control.sDialog(this);
        Control.hideKeyboard(this);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");

        username = Control.getUsername(this);

        tempUrl = get + lat + "&lng=" + lng + "&type=" + type;

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        send = (ImageView) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Control.sDialog(Location_comment_main.this);
                setDatabase();
            }
        });

        editText = (EditText) findViewById(R.id.editText);
        editText.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (editText.getText().toString().length() > 0) {
                    send.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                send.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().length() > 0) {
                    send.setVisibility(View.VISIBLE);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    send.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new Location_comment_CustomListAdapter(this, Location_comment);
        listView.setAdapter(adapter);

        getDatabase();
    }

    private void setDatabase() {

        if (Control.checkInternet(Location_comment_main.this)) {

            RequestQueue requestQueue = Volley.newRequestQueue(Location_comment_main.this);
            StringRequest jor = new StringRequest(Request.Method.POST, set,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            editText.setText("");
                            Location_comment = null;
                            Location_comment = new ArrayList<Location_comment>();

                            adapter = new Location_comment_CustomListAdapter(Location_comment_main.this, Location_comment);
                            listView.setAdapter(adapter);

                            getDatabase();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            Control.hDialog();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "comment_location");
                    params.put("username", username);
                    params.put("lat", String.valueOf(lat));
                    params.put("lng", String.valueOf(lng));
                    params.put("comment", editText.getText().toString());
                    params.put("type", type);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);

        } else {
            Control.alertCurrentInternet(Location_comment_main.this);
            Control.hDialog();
        }

    }

    public void getDatabase(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(tempUrl + "&ramdom=" + Control.randomNumber() , new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Location_comment item = new Location_comment();

                        item.setComment(obj.getString("comment"));
                        item.setUpdate_date(obj.getString("update_date"));
                        item.setUsers(obj.getString("name"));
                        item.setSex(obj.getString("sex"));

                        //add to array
                        Location_comment.add(item);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                Control.hDialog();
                adapter.notifyDataSetChanged();
                listView.setSelection(listView.getAdapter().getCount()-1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Control.hDialog();
            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }
}
