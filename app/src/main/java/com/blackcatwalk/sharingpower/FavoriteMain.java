package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class FavoriteMain extends AppCompatActivity {

    // ----------- User Interface  --------------//
    private List<Favorite> favoriteList = new ArrayList<Favorite>();
    private ListView listView;
    private FavoriteCustomListAdapter adapter;
    private TextView mFavotiteTv;
    private ImageView mFavotiteIm;

    // ----------- Url set form database --------------//
    private String set = "https://www.smilemap.me/android/set.php";

    // ----------- Url get form database --------------//
    private static final String url = "https://www.smilemap.me/android/get.php?main=favorite&sub=";
    private String tempUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().hide();

        Control.sDialog(this);

        tempUrl = url + Control.getUsername(this);

        listView = (ListView) findViewById(R.id.list_view);
        mFavotiteTv = (TextView) findViewById(R.id.favoriteTv);
        mFavotiteTv.setVisibility(View.INVISIBLE);
        mFavotiteIm = (ImageView) findViewById(R.id.favoriteIm);
        mFavotiteIm.setVisibility(View.INVISIBLE);
        adapter = new FavoriteCustomListAdapter(this, favoriteList);

        listView.setAdapter(adapter);

        ImageView refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                favoriteList = null;
                favoriteList = new ArrayList<Favorite>();

                adapter = new FavoriteCustomListAdapter(FavoriteMain.this, favoriteList);
                listView.setAdapter(adapter);

                Control.sDialog(FavoriteMain.this);
                getDatabase();
            }
        });

        ImageView sort = (ImageView) findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] lists = {"สถานที่", "วันที่"};
                new AlertDialog.Builder(FavoriteMain.this).setTitle("เรียงลำดับ").setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        favoriteList = null;
                        favoriteList = new ArrayList<Favorite>();

                        adapter = new FavoriteCustomListAdapter(FavoriteMain.this, favoriteList);
                        listView.setAdapter(adapter);

                        tempUrl = null;
                        tempUrl = url + Control.getUsername(FavoriteMain.this);

                        switch (which) {
                            case 0:
                                tempUrl = tempUrl + "&job=location";
                                break;
                            case 1:
                                tempUrl = tempUrl + "&job=date";
                                break;
                        }
                        Control.sDialog(FavoriteMain.this);
                        getDatabase();

                        dialog.cancel();
                    }
                }).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()

                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                final Favorite item = favoriteList.get(position);

                                                final String[] lists = {"ดูแผนที่", "แก้ไข", "ลบรายการ",};
                                                new AlertDialog.Builder(FavoriteMain.this).setTitle("รายการโปรด").setItems(lists, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        switch (which) {
                                                            case 0:
                                                                Intent map = new Intent(getApplicationContext(), DetailMap.class);
                                                                map.putExtra("latFavorite", item.getLat());
                                                                map.putExtra("lngFavorite", item.getLng());
                                                                map.putExtra("typeFavorite", item.getType());
                                                                map.putExtra("detailFavorite", item.getDetail());
                                                                map.putExtra("typeMoveMap", 9);
                                                                startActivity(map);
                                                                break;
                                                            case 1:
                                                                final Dialog dialogFavorite = new Dialog(FavoriteMain.this);
                                                                dialogFavorite.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                                dialogFavorite.setContentView(R.layout.activity_dialog_favorite_detail);

                                                                Control.showKeyboard(FavoriteMain.this);

                                                                final EditText editDetailName = (EditText) dialogFavorite.findViewById(R.id.editDetailName);
                                                                editDetailName.setText(item.getDetail());
                                                                editDetailName.setSelection(editDetailName.getText().length());

                                                                ImageView btnClose = (ImageView) dialogFavorite.findViewById(R.id.btnClose);
                                                                btnClose.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        Control.hideKeyboard(FavoriteMain.this);
                                                                        dialogFavorite.cancel();
                                                                    }
                                                                });

                                                                Button save = (Button) dialogFavorite.findViewById(R.id.btnSave);
                                                                save.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                        final String detailName = editDetailName.getText().toString();

                                                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
                                                                        StringRequest jor = new StringRequest(Request.Method.POST, set,
                                                                                new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {
                                                                                        favoriteList = null;
                                                                                        favoriteList = new ArrayList<Favorite>();

                                                                                        adapter = new FavoriteCustomListAdapter(FavoriteMain.this, favoriteList);
                                                                                        listView.setAdapter(adapter);

                                                                                        Control.sDialog(FavoriteMain.this);
                                                                                        getDatabase();
                                                                                    }
                                                                                },
                                                                                new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                        Toast.makeText(getApplicationContext(), "บันทึกไม่สำเร็จ", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                }) {
                                                                            @Override
                                                                            protected Map<String, String> getParams() {


                                                                                Map<String, String> params = new HashMap<String, String>();
                                                                                params.put("main", "favorite");
                                                                                params.put("job", "updatefavorite");
                                                                                params.put("username", Control.getUsername(FavoriteMain.this));
                                                                                params.put("lat", item.getLat().toString());
                                                                                params.put("lng", item.getLng().toString());
                                                                                params.put("detailold", item.getDetail());
                                                                                params.put("detailnew", detailName);
                                                                                params.put("type", item.getType());

                                                                                return params;
                                                                            }
                                                                        };
                                                                        jor.setShouldCache(false);
                                                                        requestQueue.add(jor);

                                                                        dialogFavorite.cancel();
                                                                    }
                                                                });
                                                                dialogFavorite.show();

                                                                break;
                                                            case 2:

                                                                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteMain.this);
                                                                builder.setMessage("ลบรายการนี้หรือไม่");
                                                                builder.setPositiveButton("ลบรายการ", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                                        StringRequest jor = new StringRequest(Request.Method.POST, set,
                                                                                new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {
                                                                                        favoriteList = null;
                                                                                        favoriteList = new ArrayList<Favorite>();

                                                                                        adapter = new FavoriteCustomListAdapter(FavoriteMain.this, favoriteList);
                                                                                        listView.setAdapter(adapter);

                                                                                        Control.sDialog(FavoriteMain.this);
                                                                                        getDatabase();
                                                                                    }
                                                                                },
                                                                                new Response.ErrorListener() {
                                                                                    @Override
                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                    }
                                                                                }) {
                                                                            @Override
                                                                            protected Map<String, String> getParams() {
                                                                                Map<String, String> params = new HashMap<String, String>();
                                                                                params.put("main", "favorite");
                                                                                params.put("job", "deletefavorite");
                                                                                params.put("username", Control.getUsername(FavoriteMain.this));
                                                                                params.put("lat", item.getLat().toString());
                                                                                params.put("lng", item.getLng().toString());
                                                                                params.put("type", item.getType());
                                                                                params.put("detailold", item.getDetail());

                                                                                return params;
                                                                            }
                                                                        };
                                                                        jor.setShouldCache(false);
                                                                        requestQueue.add(jor);

                                                                        dialog.cancel();
                                                                    }
                                                                });
                                                                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.cancel();
                                                                    }
                                                                });

                                                                AlertDialog alert = builder.create();
                                                                alert.show();
                                                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                                                pbutton.setTextColor(Color.parseColor("#147cce"));
                                                                pbutton.setTypeface(null, Typeface.BOLD);
                                                                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                                                nbutton.setTextColor(Color.parseColor("#147cce"));
                                                                nbutton.setTypeface(null, Typeface.BOLD);
                                                                break;
                                                        }
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                            }
                                        }

        );
        getDatabase();
    }

    public void getDatabase() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(tempUrl + "&ramdom=" + Control.randomNumber(), new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Favorite item = new Favorite();

                        item.setDetail(obj.getString("detail"));
                        item.setLat(obj.getDouble("lat"));
                        item.setLng(obj.getDouble("lng"));
                        item.setTime(obj.getString("update_date"));
                        item.setType(obj.getString("type"));
                        favoriteList.add(item);

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                if(response.length() > 0){
                    mFavotiteIm.setVisibility(View.INVISIBLE);
                    mFavotiteTv.setVisibility(View.INVISIBLE);
                }else{
                    mFavotiteIm.setVisibility(View.VISIBLE);
                    mFavotiteTv.setVisibility(View.VISIBLE);
                }
                Control.hDialog();
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getmInstance().
                addToRequesQueue(jsonArrayRequest);
    }
}


