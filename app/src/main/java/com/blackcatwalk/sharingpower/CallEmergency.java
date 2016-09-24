package com.blackcatwalk.sharingpower;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CallEmergency extends AppCompatActivity {

    private Intent intent;

    // ----------- Url set form database --------------//
    private String set = "https://www.smilemap.me/android/set.php";
    private LinearLayout backgroundAdsLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_emergency);
        getSupportActionBar().hide();

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        LinearLayout general = (LinearLayout) findViewById(R.id.general);
        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CallEmergency.this, CallEmergencyDetail.class);
                intent.putExtra("type", "general");
                startActivity(intent);
            }
        });

        LinearLayout hospital = (LinearLayout) findViewById(R.id.hospital);
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CallEmergency.this, CallEmergencyDetail.class);
                intent.putExtra("type", "hospital");
                startActivity(intent);
            }
        });

        LinearLayout police = (LinearLayout) findViewById(R.id.police);
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CallEmergency.this, CallEmergencyDetail.class);
                intent.putExtra("type", "police");
                startActivity(intent);
            }
        });

        LinearLayout report = (LinearLayout) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CallEmergency.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_report_call);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                final EditText editText = (EditText) dialog.findViewById(R.id.editText);

                Button btnSend = (Button) dialog.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (Control.checkInternet(CallEmergency.this)) {

                            RequestQueue requestQueue = Volley.newRequestQueue(CallEmergency.this);
                            StringRequest jor = new StringRequest(Request.Method.POST, set,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(getApplicationContext(), "ส่งข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("main", "report");
                                    params.put("username", Control.getUsername(CallEmergency.this));
                                    params.put("detail", editText.getText().toString());
                                    params.put("type_report", "nearby_call");
                                    return params;
                                }
                            };
                            jor.setShouldCache(false);
                            requestQueue.add(jor);

                            dialog.cancel();
                        } else {
                            Control.alertCurrentInternet(CallEmergency.this);
                        }
                    }
                });
                dialog.show();
            }
        });

    }
}
