package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class Profile extends AppCompatActivity {

    // ------------- User Interface ----------------//

    private de.hdodenhof.circleimageview.CircleImageView imageProfile;
    private TextView name;
    private TextView countTraffic;
    private TextView countLocation;
    private TextView point;
    private TextView stausDetail;
    private TextView nickName;
    private TextView nickNameDetail;
    private TextView sumHour;


    // ----------- Url get form database --------------//
    private String url = "https://www.smilemap.me/android/get.php?main=users&sub=";

    private String username;
    private String tempStatus;
    private String tempName;
    //private String picture;

    @Override
    protected void onResume() {
        super.onResume();
        getDatabase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        Control.sDialog(this);

        username = Control.getUsername(this);
        url = url + username;

        ImageView menu = (ImageView) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileSetting.class);
                intent.putExtra("name", tempName);
                intent.putExtra("staus", tempStatus);
                startActivity(intent);
            }
        });

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        imageProfile = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.imageProfile);

        name = (TextView) findViewById(R.id.name);
        countTraffic = (TextView) findViewById(R.id.countTraffic);
        countLocation = (TextView) findViewById(R.id.countLocation);
        point = (TextView) findViewById(R.id.point);
        stausDetail = (TextView) findViewById(R.id.stausDetail);

        nickName = (TextView) findViewById(R.id.nickName);
        nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailNickName();
            }
        });

        nickNameDetail = (TextView) findViewById(R.id.nickNameDetail);
        nickNameDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailNickName();
            }
        });

        sumHour = (TextView) findViewById(R.id.sumHour);

    }

    //  ------------------  Database Mysql -----------------//
    private void getDatabase() {

        if (Control.checkInternet(this)) {

            String getUrl = url + "&ramdom=" + Control.randomNumber();

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray ja = response.getJSONArray("users");

                                JSONObject jsonObject = null;

                                NumberFormat temp = NumberFormat.getInstance();
                                double tempDouble;

                                for (int i = 0; i < ja.length(); i++) {

                                    jsonObject = ja.getJSONObject(i);

                                    tempName = jsonObject.getString("name");

                                    name.setText(tempName);

                                   /* picture = jsonObject.getString("picture");*/

                                   /* if(picture.length() > 0){
                                        Glide.with(Profile.this).load(picture).into(imageProfile);
                                    }else */

                                    if(jsonObject.getString("sex").equals("หญิง")) {
                                        imageProfile.setImageResource(R.drawable.sex_female);
                                    } else {
                                        imageProfile.setImageResource(R.drawable.sex_male);
                                    }

                                    tempDouble = (double) jsonObject.getInt("count_traffic");
                                    if(tempDouble >0) {
                                        countTraffic.setText(temp.format(tempDouble).toString());
                                    }else{
                                        countTraffic.setText("-");
                                    }

                                    tempDouble = (double) jsonObject.getInt("count_location");
                                    if(tempDouble >0) {
                                        countLocation.setText(temp.format(tempDouble).toString());
                                    }else{
                                        countLocation.setText("-");
                                    }

                                    tempDouble = (double) jsonObject.getInt("point");
                                    if(tempDouble >0) {
                                        point.setText(temp.format(tempDouble).toString());
                                    }else{
                                        point.setText("-");
                                    }

                                    tempStatus = jsonObject.getString("staus");

                                    if(tempStatus.length() > 0){
                                        stausDetail.setVisibility(View.VISIBLE);
                                        stausDetail.setText(tempStatus);
                                    }else {
                                        stausDetail.setVisibility(View.GONE);
                                    }

                                    if (((jsonObject.getInt("sum_hour") / 60) >= 0) && ((jsonObject.getInt("sum_hour") / 60) <= 50)) {
                                        nickNameDetail.setText("ผู้แบ่งปันเริ่มต้น");
                                    } else if (((jsonObject.getInt("sum_hour") / 60) > 50) && ((jsonObject.getInt("sum_hour") / 60) <= 500)) {
                                        nickNameDetail.setText("ผู้แบ่งปันขั้นกลาง");
                                    } else {
                                        nickNameDetail.setText("ผู้แบ่งปันสูงสูด");


                                    }

                                    tempDouble = (double) (jsonObject.getInt("sum_hour") / 60);
                                    if(tempDouble >0) {
                                        sumHour.setText(temp.format(tempDouble).toString() + " ชั่วโมง");
                                    }else{
                                        sumHour.setText("- ชั่วโมง");
                                    }
                                }

                                final Handler handler2 = new Handler();
                                handler2.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Control.hDialog();
                                    }
                                }, 3000);

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
        } else {
            Control.alertInternet(Profile.this);
            Control.hDialog();
        }
    }

    //  ------------------  User Interface -----------------//

    public void dialogDetailNickName() {
        final Dialog dialog = new Dialog(Profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_nickname_detail);

        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }
}
