package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.utility.ControlDatabase;

public class RegisterPage1 extends AppCompatActivity {

    private boolean mCheckShowPassword = true;
    private ControlDatabase mControlDatabase;

    // -------------- User Interface ------------------//
    private ImageView mBackIm;
    private ScrollView mLayoutSc;
    private TextView mAlertTv;
    private EditText mEmailEt;
    private Button mClearEmailBtn;
    private EditText mPasswordEt;
    private ImageView mShowPasswordIm;
    private Button mNextBtn;

    // ----------- Data send to database --------------//
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_page1);

        bindWidget();

        mControlDatabase = new ControlDatabase(this);

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mEmailEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mClearEmailBtn.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmailEt.getText().toString().length() > 0) {
                    mClearEmailBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        mEmailEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mEmailEt.clearFocus();
                    mPasswordEt.requestFocus();
                    mClearEmailBtn.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
        mEmailEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mEmailEt.getText().toString().length() > 0) {
                    mClearEmailBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mClearEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailEt.setText("");
            }
        });

        mPasswordEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mShowPasswordIm.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPasswordEt.getText().toString().length() > 0) {
                    mShowPasswordIm.setVisibility(View.VISIBLE);
                }
            }
        });
        mPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkForm();
                }
                return false;
            }
        });
        mPasswordEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mPasswordEt.getText().toString().length() > 0) {
                    mShowPasswordIm.setVisibility(View.VISIBLE);
                }
                mClearEmailBtn.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        mShowPasswordIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckShowPassword) {
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mShowPasswordIm.setImageResource(R.drawable.icon_show_password);
                    mCheckShowPassword = false;
                } else {
                    mPasswordEt.setInputType(129);
                    mCheckShowPassword = true;
                    mShowPasswordIm.setImageResource(R.drawable.icon_hiden_password);
                }
                mPasswordEt.setSelection(mPasswordEt.getText().length());
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
        mEmail = mEmailEt.getText().toString();
        mPassword = mPasswordEt.getText().toString();

        if (!isValidEmail(mEmail) || mEmail.length() <=0 ) {
            mAlertTv.setTextColor(Color.parseColor("#F4511E"));
            mAlertTv.setText(R.string.general_text_6);
            return;
        }

        if (mPassword.length() < 8) {
            mAlertTv.setTextColor(Color.parseColor("#F4511E"));
            mAlertTv.setText(R.string.general_text_7);
            return;
        }

        mControlDatabase.getDatabaseRegisPage1(mEmail);

    }

    public final static boolean isValidEmail(String _target) {
        return !TextUtils.isEmpty(_target) && android.util.Patterns.EMAIL_ADDRESS.matcher(_target).matches();
    }

    public void checkEmail(String _temp) {
        if (_temp.equals("false")) {
            startActivity(new Intent(getApplicationContext(), RegisterPage2.class)
                    .putExtra("email", mEmail).putExtra("password", mPassword));
            mAlertTv.setTextColor(Color.parseColor("#717171"));
            mAlertTv.setText(R.string.general_text_10);
        } else {
            mAlertTv.setTextColor(Color.parseColor("#F4511E"));
            mAlertTv.setText(R.string.general_text_8);
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPage1.this);
            builder.setMessage(R.string.general_text_9);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.parseColor("#147cce"));
            pbutton.setTypeface(null, Typeface.BOLD);
        }
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mAlertTv = (TextView) findViewById(R.id.alertTv);
        mEmailEt = (EditText) findViewById(R.id.emailEt);
        mClearEmailBtn = (Button) findViewById(R.id.clearEmailBtn);
        mPasswordEt = (EditText) findViewById(R.id.passwordEt);
        mShowPasswordIm = (ImageView) findViewById(R.id.showPasswordIm);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mLayoutSc = (ScrollView) findViewById(R.id.layoutSc);
        mLayoutSc.setVerticalScrollBarEnabled(false);
        mLayoutSc.setHorizontalScrollBarEnabled(false);
    }
}
