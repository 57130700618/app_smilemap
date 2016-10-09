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

import com.blackcatwalk.sharingpower.utility.ControlDatabase;

public class CallEmergencyMain extends AppCompatActivity {

    private ImageView mBackIm;
    private LinearLayout mGeneralLy;
    private LinearLayout mHospitalLy;
    private LinearLayout mPoliceLy;
    private LinearLayout mReportLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_emergency);
        getSupportActionBar().hide();

        bindWidget();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mGeneralLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity("general");
            }
        });

        mHospitalLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity("hospital");
            }
        });

        mPoliceLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity("police");
            }
        });

        mReportLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void startActivity(String _typename) {
        startActivity(new Intent(CallEmergencyMain.this, CallEmergencyDetail.class).putExtra("type", _typename));
    }

    private void openDialog() {
        final Dialog _dialog = new Dialog(CallEmergencyMain.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_report_call);

        ImageView _btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        final EditText _editText = (EditText) _dialog.findViewById(R.id.editText);

        Button _btnSend = (Button) _dialog.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(_editText.getText().length() > 1){
                    ControlDatabase _controlDatabae = new ControlDatabase(CallEmergencyMain.this);
                    _controlDatabae.reportGeneral(_editText.getText().toString(),"nearby_call");
                    _dialog.cancel();
                }
            }
        });
        _dialog.show();
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mGeneralLy = (LinearLayout) findViewById(R.id.generalLy);
        mHospitalLy = (LinearLayout) findViewById(R.id.hospitalLy);
        mPoliceLy = (LinearLayout) findViewById(R.id.policeLy);
        mReportLy = (LinearLayout) findViewById(R.id.reportLy);
    }
}
