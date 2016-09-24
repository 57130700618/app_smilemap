package com.blackcatwalk.sharingpower;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LocationDetail extends AppCompatActivity {

    // ----------- User Interface  --------------//
    private TextView text_five;
    private TextView text_four;
    private TextView text_three;
    private TextView text_two;
    private TextView text_one;
    private TextView titleTv;
    private TextView detailLocation;
    private TextView distanceTv;
    private TextView durationTv;
    private TextView addressTv;
    private ImageView btnClose;
    private ImageView favoriteBtn;
    private ImageView reportBtn;
    private Button btnNavigation;
    private Button vote;
    private Button comment;

    // ----------------- Url to database ------------------//
    private String set = "https://www.smilemap.me/android/set.php";
    private String get = "https://www.smilemap.me/android/get.php?main=vote&lat=";
    private String tempUrl;

    // ---------------- Data  ------------------//
    private String type;
    private String detail;
    private Double lat;
    private Double lng;
    private String temp;
    private String username;
    private String duration;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        getSupportActionBar().hide(); // hide ActionBar

        Control.sDialog(this);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");
        detail = bundle.getString("detail");
        username = bundle.getString("username");
        duration = bundle.getString("duration");
        distance = bundle.getString("distance");

        tempUrl = get + lat + "&lng=" + lng;

        final SupportStreetViewPanoramaFragment streetViewFracment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewFracment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                showStreetView(new LatLng(lat, lng), streetViewPanorama);
            }
        });

        LinearLayout googleStreetLy = (LinearLayout) findViewById(R.id.googleStreetLy);
        googleStreetLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), GoogleMapStreet.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                startActivity(intent);
            }
        });

        detailLocation = (TextView) findViewById(R.id.detailLocation);
        addressTv = (TextView) findViewById(R.id.addressTv);
        distanceTv = (TextView) findViewById(R.id.distanceTv);
        durationTv = (TextView) findViewById(R.id.durationTv);

        text_one = (TextView) findViewById(R.id.text_one);
        text_two = (TextView) findViewById(R.id.text_two);
        text_three = (TextView) findViewById(R.id.text_three);
        text_four = (TextView) findViewById(R.id.text_four);
        text_five = (TextView) findViewById(R.id.text_five);

        titleTv = (TextView) findViewById(R.id.titleTv);

        switch (type) {
            case "restroom":
                titleTv.setText("ห้องน้ำ");
                break;
            case "pharmacy":
                titleTv.setText("ร้านขายยา");
                break;
            case "veterinary_clinic":
                titleTv.setText("รักษาสัตว์");
                break;
            case "daily_home":
                titleTv.setText("ที่พักรายวัน");
                break;
            case "officer":
                titleTv.setText("จุดพักผ่อน");
                break;
            case "garage":
                titleTv.setText("ร้านปะยาง");
                break;
        }

        if (detail.length() > 0) {
            detailLocation.setText("รายละเอียด: " + detail);
        } else {
            detailLocation.setText("รายละเอียด: -");
        }
        addressTv.setText("ที่อยู่: " + GoogleMapAddress.getAddress(this, lat, lng));

        int _tempIndexString = distance.indexOf(" ");

        if (distance.substring(_tempIndexString + 1, _tempIndexString + 2).equals("ก")) {
            distanceTv.setText("ระยะทาง: " + distance.substring(0, _tempIndexString) + " กิโลเมตร");
        } else {
            distanceTv.setText("ระยะทาง: " + distance);
        }

        durationTv.setText("ระยะเวลา: " + duration);

        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        comment = (Button) findViewById(R.id.comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Location_comment_main.class);
                intent.putExtra("type", type);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        vote = (Button) findViewById(R.id.vote);
        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LocationDetail.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_vote);

                final RatingBar ratingbar = (RatingBar) dialog.findViewById(R.id.ratingbar);
                LayerDrawable stars = (LayerDrawable) ratingbar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor("#FF4081"), PorterDuff.Mode.SRC_ATOP);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Control.checkInternet(LocationDetail.this)) {

                            if (ratingbar.getRating() > 0) {

                                RequestQueue requestQueue = Volley.newRequestQueue(LocationDetail.this);
                                StringRequest jor = new StringRequest(Request.Method.POST, set,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                getDatabase();
                                                Control.showToast(LocationDetail.this, "ให้คะแนนเรียบร้อย");
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Control.showToast(LocationDetail.this, "ให้คะแนนไม่สำเร็จ");
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() {

                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("main", "vote");
                                        params.put("lat", String.valueOf(lat).toString());
                                        params.put("lng", String.valueOf(lng).toString());
                                        params.put("vote", String.valueOf((int) ratingbar.getRating()));
                                        return params;
                                    }
                                };

                                jor.setShouldCache(false);
                                requestQueue.add(jor);
                            }

                            dialog.cancel();
                        } else {
                            Control.alertCurrentInternet(LocationDetail.this);
                        }
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });


        favoriteBtn = (ImageView) findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LocationDetail.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_favorite_detail);

                Control.showKeyboard(LocationDetail.this);

                final EditText editDetailName = (EditText) dialog.findViewById(R.id.editDetailName);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Control.hideKeyboard(LocationDetail.this);
                        dialog.cancel();
                    }
                });

                Button save = (Button) dialog.findViewById(R.id.btnSave);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Control.hideKeyboard(LocationDetail.this);
                        final String detailName = editDetailName.getText().toString();

                        if (Control.checkInternet(LocationDetail.this)) {

                            RequestQueue requestQueue = Volley.newRequestQueue(LocationDetail.this);
                            StringRequest jor = new StringRequest(Request.Method.POST, set,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Control.showToast(LocationDetail.this, "บันทึกสำเร็จ");
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Control.showToast(LocationDetail.this, "บันทึกไม่สำเร็จ");
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("main", "favorite");
                                    params.put("job", "savefavorite");
                                    params.put("username", username);
                                    params.put("lat", String.valueOf(lat));
                                    params.put("lng", String.valueOf(lng));
                                    params.put("detailnew", detailName);
                                    params.put("type", type);

                                    return params;
                                }
                            };
                            jor.setShouldCache(false);
                            requestQueue.add(jor);

                            dialog.cancel();
                        } else {
                            Control.alertCurrentInternet(LocationDetail.this);
                        }
                        dialog.cancel();
                    }
                });
                dialog.show();

            }
        });

        reportBtn = (ImageView) findViewById(R.id.reportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LocationDetail.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_report_location);

                Control.showKeyboard(LocationDetail.this);

                btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Control.hideKeyboard(LocationDetail.this);
                    }
                });

                final EditText editText = (EditText) dialog.findViewById(R.id.editText);

                Button btnSend = (Button) dialog.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Control.hideKeyboard(LocationDetail.this);
                        if (Control.checkInternet(LocationDetail.this)) {

                            RequestQueue requestQueue = Volley.newRequestQueue(LocationDetail.this);
                            StringRequest jor = new StringRequest(Request.Method.POST, set,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Control.showToast(LocationDetail.this, "ส่งข้อมูลสำเร็จ");
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Control.showToast(LocationDetail.this, "ส่งข้อมูลไม่สำเร็จ");
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("main", "report");
                                    params.put("username", username);
                                    params.put("lat", String.valueOf(lat));
                                    params.put("lng", String.valueOf(lng));
                                    params.put("detail", editText.getText().toString());
                                    return params;
                                }
                            };
                            jor.setShouldCache(false);
                            requestQueue.add(jor);

                            dialog.cancel();
                        } else {
                            Control.alertCurrentInternet(LocationDetail.this);
                        }
                    }
                });
                dialog.show();
            }
        });

        btnNavigation = (Button) findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] lists = {"ขับรถ", "เดิน"};
                new AlertDialog.Builder(LocationDetail.this).setTitle("เลือกประเภทการเดินทาง").setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String parse = "google.navigation:q=" + lat + "," + lng;

                        switch (which) {
                            case 0:
                                temp = parse + "&mode=d";
                                break;
                            case 1:
                                temp = parse + "&mode=w";
                                break;
                        }
                        dialog.cancel();

                        AlertDialog.Builder buildercheck = new AlertDialog.Builder(LocationDetail.this);
                        buildercheck.setTitle(Html.fromHtml("<b>" + "โปรดตรวจสอบว่าท่านไม่ได้อยู่จุดอับสัญญาณ" + "</b>"));
                        buildercheck.setMessage("เมนูนำทางอาจใช้เวลาในการโหลดข้อมูลนาน เมื่อท่านอยู่จุดอับสัญญาณ");
                        buildercheck.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri gmmIntentUri = Uri.parse(temp);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = buildercheck.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.parseColor("#147cce"));
                        pbutton.setTypeface(null, Typeface.BOLD);
                    }
                }).show();
            }
        });

        getDatabase();

    }

    //  ------------------  Database Mysql -----------------//

    public void getDatabase() {

        RequestQueue requestQueue = Volley.newRequestQueue(LocationDetail.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, tempUrl + "&ramdom=" + Control.randomNumber(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            JSONArray ja = response.getJSONArray("vote");
                            JSONObject jsonObject = null;

                            for (int i = 0; i < ja.length(); i++) {

                                jsonObject = ja.getJSONObject(i);


                                switch (jsonObject.getString("vote")) {
                                    case "5":
                                        text_five.setText(jsonObject.getString("count(vote)"));
                                        break;
                                    case "4":
                                        text_four.setText(jsonObject.getString("count(vote)"));
                                        break;
                                    case "3":
                                        text_three.setText(jsonObject.getString("count(vote)"));
                                        break;
                                    case "2":
                                        text_two.setText(jsonObject.getString("count(vote)"));
                                        break;
                                    case "1":
                                        text_one.setText(jsonObject.getString("count(vote)"));
                                        break;
                                }
                            }
                            Control.hDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Control.hDialog();
                    }
                }
        );
        jor.setShouldCache(false);
        requestQueue.add(jor);
    }

    private void showStreetView(LatLng latLng, StreetViewPanorama streetViewPanorama) {
        if (streetViewPanorama == null)
            return;

        StreetViewPanoramaCamera.Builder builder = new StreetViewPanoramaCamera.Builder(streetViewPanorama.getPanoramaCamera());
        builder.tilt(0.0f);
        builder.zoom(0.0f);
        builder.bearing(0.0f);
        streetViewPanorama.animateTo(builder.build(), 0);

        streetViewPanorama.setPosition(latLng, 300);
        streetViewPanorama.setStreetNamesEnabled(true);
    }
}


