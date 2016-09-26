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

import com.blackcatwalk.sharingpower.utility.Control;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;


public class Setting extends AppCompatActivity {

    private String stausTaffic = "1";
    private String stausNearby = "6";
    private String stausMapType = "0";
    private String tempResultNearby = null;
    private String version;
    private Control mControl;
    private ControlFile mControlFile;

    // ----------- User Interface --------------//
    private ImageView mBackIm;
    private LinearLayout mTrafficLy;
    private LinearLayout mNearbyLy;
    private LinearLayout mTypemapLy;
    private LinearLayout mFacebookLy;
    private LinearLayout mInstagram;
    private LinearLayout mTwitter;
    private LinearLayout versionLy;
    private LinearLayout commentLy;
    private LinearLayout clearCacheLy;
    private LinearLayout donateLy;
    private TextView versionTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();

        bindWidget();

        mControl = new Control();
        mControlFile = new ControlFile();

        version = mControl.getVersionApp(this);

        readFile();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        mTrafficLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTraffic();
            }
        });

        mNearbyLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNearBy();
            }
        });

        mTypemapLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTypeMap();
            }
        });

        clearCacheLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogClearCache();
            }
        });

        donateLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting.this, Donate.class));
            }
        });

        mFacebookLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "704870926322640")));
                } catch (ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.com/SmileMap.social")));
                }
            }
        });

        mInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/smile_map_app"))
                    .setPackage("com.instagram.android"));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/smile_map_app")));
                }
            }
        });

        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=smile_map_app")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/smile_map_app")));
                }
            }
        });

        commentLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        versionTv.setText("ตรวจสอบเวอร์ชันปัจจุบัน: v." + version);

        versionLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControl.openGooglePlay(Setting.this,"ads");
            }
        });
    }

    private void readFile() {
        String _temp = mControlFile.getFile(this,"setting");
        if (_temp.length() > 1) {
            stausTaffic = _temp.substring(0, 1);
            stausMapType = _temp.substring(1, 2);
            stausNearby = _temp.substring(2, _temp.length());
        }
    }

    private void saveFile() {
        mControlFile.setSetting(this,stausTaffic,stausMapType,stausNearby);
    }

    private void showDialogTraffic() {
        final Dialog _dialog = new Dialog(Setting.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_setting_traffic);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Switch showTraffic = (Switch) _dialog.findViewById(R.id.showTraffic);
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
        _dialog.show();
    }

    private void showDialogTypeMap() {
        final Dialog _dialog = new Dialog(Setting.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_setting_map_type);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final ImageView imagemap = (ImageView) _dialog.findViewById(R.id.imagemap);

        final RadioButton satelltte = (RadioButton) _dialog.findViewById(R.id.satelltte);
        final RadioButton normal = (RadioButton) _dialog.findViewById(R.id.normal);

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
        _dialog.show();
    }

    private void showDialogNearBy() {
        final Dialog _dialog = new Dialog(Setting.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_setting_nearby);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final TextView resultNearbyTxt = (TextView) _dialog.findViewById(R.id.resultNearbyTxt);

        SeekBar seekBar = (SeekBar) _dialog.findViewById(R.id.seekBar);
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

        _dialog.show();
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

    private void showDialogComment() {
        final Dialog _dialog = new Dialog(Setting.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_report_recommend);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final EditText editText = (EditText) _dialog.findViewById(R.id.editText);

        Button btnSend = (Button) _dialog.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ControlDatabase(Setting.this).setDatabaseSettingComment(editText.getText().toString());
                _dialog.cancel();
            }
        });
        _dialog.show();
    }

    private void showDialogClearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setMessage("ท่านต้องการล้างข้อมูลแคชหรือไม่");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mControl.clearCache(Setting.this);
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

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mTrafficLy = (LinearLayout) findViewById(R.id.trafficLy);
        mNearbyLy = (LinearLayout) findViewById(R.id.nearbyLy);
        mTypemapLy = (LinearLayout) findViewById(R.id.typemapLy);
        mFacebookLy = (LinearLayout) findViewById(R.id.facebookLy);
        mInstagram = (LinearLayout) findViewById(R.id.instagramLy);
        mTwitter = (LinearLayout) findViewById(R.id.twitterLy);
        clearCacheLy = (LinearLayout) findViewById(R.id.clearCacheLy);
        donateLy = (LinearLayout) findViewById(R.id.donateLy);
        commentLy = (LinearLayout) findViewById(R.id.commentLy);
        versionTv = (TextView) findViewById(R.id.versionTv);
        versionLy = (LinearLayout) findViewById(R.id.versionLy);
    }
}


