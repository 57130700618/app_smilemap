package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


public class Setting extends AppCompatActivity {

    // ------------- FIle System ----------------//
    private static final String fileName = "setting.txt";
    private static final int readSize = 100; // Read 100byte
    private String stausTaffic = "1";
    private String stausNearby = "6";
    private String stausMapType = "0";
    private String tempResultNearby = null;

    // ----------- User Interface --------------//
    private LinearLayout facebook;
    private LinearLayout instagram;
    private LinearLayout twitter;

    private LinearLayout typemap;
    private LinearLayout nearby;
    private LinearLayout traffic;
    private LinearLayout versionLy;
    private LinearLayout commentLy;
    private LinearLayout clearCacheLy;
    private LinearLayout donateLy;

    private TextView versionTv;

    private ImageView back;

    // ----------- Data --------------//
    String version;

    // ----------- Url set form database --------------//
    private String set = "https://www.smilemap.me/android/set.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        version = Control.getVersionApp(this);

        readFile();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        traffic = (LinearLayout) findViewById(R.id.traffic);
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Setting.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_setting_traffic);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Switch showTraffic = (Switch) dialog.findViewById(R.id.showTraffic);
                showTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            stausTaffic = "1";
                        } else {
                            stausTaffic = "0";
                        }
                        saveFile();
                    }
                });

                if (stausTaffic.equals("0")) {
                    showTraffic.setChecked(false);
                }

                dialog.show();
            }
        });


        nearby = (LinearLayout) findViewById(R.id.nearby);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Setting.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_setting_nearby);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


                final TextView resultNearbyTxt = (TextView) dialog.findViewById(R.id.resultNearbyTxt);

                SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                        switch (progresValue) {
                            case 0:
                                stausNearby = "3";
                                break;
                            case 1:
                                stausNearby = "6";
                                break;
                            case 2:
                                stausNearby = "12";
                                break;
                            case 3:
                                stausNearby = "23";
                                break;
                            case 4:
                                stausNearby = "32";
                                break;
                            case 5:
                                stausNearby = "55";
                                break;
                        }
                        saveFile();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        showTxtkm(seekBar, resultNearbyTxt);
                    }
                });

                showTxtkm(seekBar, resultNearbyTxt);

                dialog.show();
            }

            void showTxtkm(SeekBar seekBar, TextView resultNearbyTxt) {

                switch (stausNearby) {
                    case "3":
                        seekBar.setProgress(0);
                        tempResultNearby = "2";
                        break;
                    case "6":
                        seekBar.setProgress(1);
                        tempResultNearby = "5";
                        break;
                    case "12":
                        seekBar.setProgress(2);
                        tempResultNearby = "10";
                        break;
                    case "23":
                        seekBar.setProgress(3);
                        tempResultNearby = "20";
                        break;
                    case "32":
                        seekBar.setProgress(4);
                        tempResultNearby = "30";
                        break;
                    case "55":
                        seekBar.setProgress(5);
                        tempResultNearby = "50";
                        break;
                }
                resultNearbyTxt.setText("รอบตัว " + tempResultNearby + " km");
            }
        });

        typemap = (LinearLayout) findViewById(R.id.typemap);
        typemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Setting.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_setting_map_type);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                final ImageView imagemap = (ImageView) dialog.findViewById(R.id.imagemap);

                final RadioButton satelltte = (RadioButton) dialog.findViewById(R.id.satelltte);
                final RadioButton normal = (RadioButton) dialog.findViewById(R.id.normal);

                normal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagemap.setImageResource(R.drawable.map_normal);
                        stausMapType = "0";
                        saveFile();
                        satelltte.setChecked(false);
                        normal.setChecked(true);
                    }
                });

                satelltte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagemap.setImageResource(R.drawable.map_satelltte);
                        stausMapType = "1";
                        saveFile();
                        satelltte.setChecked(true);
                        normal.setChecked(false);
                    }
                });

                if (stausMapType.equals("0")) {
                    imagemap.setImageResource(R.drawable.map_normal);
                    normal.setChecked(true);
                } else {
                    imagemap.setImageResource(R.drawable.map_satelltte);
                    satelltte.setChecked(true);
                }

                dialog.show();
            }
        });

        clearCacheLy = (LinearLayout) findViewById(R.id.clearCacheLy);
        clearCacheLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
                builder.setMessage("ท่านต้องการล้างข้อมูลแคชหรือไม่");
                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Control.clearCache(Setting.this);
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
            }
        });

        donateLy = (LinearLayout) findViewById(R.id.donateLy);
        donateLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting.this, Donate.class));
            }
        });



        facebook = (LinearLayout) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "704870926322640"));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/SmileMap.social")));
                }
            }
        });

        instagram = (LinearLayout) findViewById(R.id.instagram);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/smile_map_app"));
                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/smile_map_app")));
                }
            }
        });

        twitter = (LinearLayout) findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=smile_map_app"));
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/smile_map_app")));
                }
            }
        });


        commentLy = (LinearLayout) findViewById(R.id.commentLy);
        commentLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Setting.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_report_recommend);

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

                        if (Control.checkInternet(Setting.this)) {

                            RequestQueue requestQueue = Volley.newRequestQueue(Setting.this);
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
                                    params.put("username", Control.getUsername(Setting.this));
                                    params.put("detail", editText.getText().toString());
                                    params.put("type_report", "recommend");
                                    return params;
                                }
                            };
                            jor.setShouldCache(false);
                            requestQueue.add(jor);

                            dialog.cancel();
                        } else {
                            Control.alertCurrentInternet(Setting.this);
                        }
                    }
                });
                dialog.show();
            }
        });

        versionTv = (TextView) findViewById(R.id.versionTv);
        versionTv.setText("ตรวจสอบเวอร์ชันปัจจุบัน: v." + version);

        versionLy = (LinearLayout) findViewById(R.id.versionLy);
        versionLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Control.openGooglePlay(Setting.this,"ads");
            }
        });
    }

    private void readFile() {

        String temp = "";

        try {
            FileInputStream fIn = openFileInput(fileName);
            InputStreamReader reader = new InputStreamReader(fIn);

            char[] buffer = new char[readSize];
            int charReadCount;
            while ((charReadCount = reader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charReadCount);

                temp += readString;
                buffer = new char[readSize];
            }
            if (temp.length() > 1) {
                stausTaffic = temp.substring(0, 1);
                stausMapType = temp.substring(1, 2);
                stausNearby = temp.substring(2, temp.length());
            }

            reader.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void saveFile() {
        try {
            FileOutputStream fOut = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(stausTaffic + stausMapType + stausNearby);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
