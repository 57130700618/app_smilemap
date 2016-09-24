package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage2 extends AppCompatActivity {

    // -------------- User Interface ------------------//
    private ImageView mBackIm;
    private ScrollView mLayoutSc;
    private TextView mAlertTv;
    private EditText mNicknameEt;
    private Button mClearTextBtn;
    private RadioButton mMaleRBtn;
    private RadioButton mFemaleRBtn;
    private Button mNextBtn;

    // ----------- Data send to database --------------//
    private String mEmail;
    private String mPassword;
    private String mTempPassword;
    private String mNickname;
    private String mSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_page2);

        Control.showKeyboard(this);

        bindWidget();

        Bundle bundle = getIntent().getExtras();
        mPassword = bundle.getString("password");
        mEmail = bundle.getString("email");

        mTempPassword = mPassword;
        mPassword = Control.md5(mPassword);

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mNicknameEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mClearTextBtn.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNicknameEt.getText().toString().length() > 0) {
                    mClearTextBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        mNicknameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mClearTextBtn.setVisibility(View.INVISIBLE);
                    if (mNicknameEt.getText().toString().length() < 5) {
                        checkForm();
                    }else{
                        mAlertTv.setTextColor(Color.parseColor("#717171"));
                        mAlertTv.setText("ชื่อผู้ใช้ถูกต้อง");
                    }
                }
                return false;
            }
        });
        mNicknameEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mNicknameEt.getText().toString().length() > 0) {
                    mClearTextBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mClearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNicknameEt.setText("");
            }
        });

        mMaleRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFemaleRBtn.setChecked(false);
                mMaleRBtn.setChecked(true);
            }
        });

        mFemaleRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaleRBtn.setChecked(false);
                mFemaleRBtn.setChecked(true);
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        mNickname = mNicknameEt.getText().toString();

        if (mMaleRBtn.isChecked()) {
            mSex = "ชาย";
        } else {
            mSex = "หญิง";
        }

        if (mNickname.length() < 5) {
            mAlertTv.setTextColor(Color.parseColor("#f25d5d"));
            mAlertTv.setText(R.string.general_text_14);
        } else {
            if (Control.checkInternet(RegisterPage2.this)) {

                Control.sDialog(RegisterPage2.this);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
                StringRequest jor = new StringRequest(Request.Method.POST, Control.getMSetDatabase(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Control.hDialog();
                                Intent login = new Intent(getApplicationContext(), RegisterPage3.class);
                                login.putExtra("email", mEmail);
                                login.putExtra("password", mTempPassword);
                                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(login);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Control.hDialog();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("main", "register");
                        params.put("password", mPassword);
                        params.put("email", mEmail);
                        params.put("nickname", mNickname);
                        params.put("sex", mSex);
                        return params;
                    }
                };
                jor.setShouldCache(false);
                requestQueue.add(jor);

            } else {
                Control.alertInternet(RegisterPage2.this);
            }
        }
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mAlertTv = (TextView) findViewById(R.id.alertTv);
        mNicknameEt = (EditText) findViewById(R.id.nicknameEt);
        mClearTextBtn = (Button) findViewById(R.id.clearTextBtn);
        mMaleRBtn = (RadioButton) findViewById(R.id.maleRBtn);
        mFemaleRBtn = (RadioButton) findViewById(R.id.femaleRBtn);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mLayoutSc = (ScrollView) findViewById(R.id.layoutSc);
        mLayoutSc.setVerticalScrollBarEnabled(false);
        mLayoutSc.setHorizontalScrollBarEnabled(false);
    }
}
