
package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.utility.Control;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;

public class LoginSub extends AppCompatActivity {

    private String mUserName = "";
    private String mPassWord = "";
    private int mCountInvalid = 0;
    private boolean mCheckShowPassword = true;
    private ControlFile mControlFile;
    private Control mControl;
    private ControlDatabase mControlDatabae;

    // -------------- User Interface ------------------//
    private ImageView mBackIm;
    private ScrollView mLayoutSc;
    private EditText mEmailEt;
    private Button mClearEmailBtn;
    private EditText mPasswordEt;
    private ImageView mShowPasswordIm;
    private Button mLoginBtn;
    private Button mRegisterBtn;
    private Button mForgetPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_sub);

        bindWidget();

        mControlFile = new ControlFile();
        mControl = new Control();
        mControlDatabae = new ControlDatabase(this);

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
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEmailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
                    checkLogin();
                }
                return false;
            }
        });
        mPasswordEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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

        mLoginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        mRegisterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterPage1.class));
            }
        });

        mForgetPasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkLogin() {
        mUserName = mEmailEt.getText().toString();
        mPassWord = mPasswordEt.getText().toString();

        if (!mUserName.isEmpty() && !mPassWord.isEmpty()) {

            mClearEmailBtn.setVisibility(View.INVISIBLE);

            mPassWord = mControl.md5(mPassWord);

            mControlDatabae.getDatabaseLoginub(mUserName, mPassWord);
        }
    }

    public void saveStausLoginToFile() {

        mControlFile.setFile(this, mUserName, "userName");
        mControlFile.setFile(this, "1" , "stausLogin");

        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    public void countInvalid() {
        mCountInvalid++;

        if (mCountInvalid >= 5) {
            alertDialog(getString(R.string.general_text_2),
                    getString(R.string.general_text_3), true);

        } else {
            alertDialog(getString(R.string.general_text_4),
                    getString(R.string.general_text_5), false);
        }
    }

    private void alertDialog(String title, String Message, final boolean check) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginSub.this);

        if (check) {
            builder.setTitle(Html.fromHtml("<FONT COLOR=#FF5722>" + title + "</FONT>"));
        } else {
            builder.setTitle(title);
        }
        builder.setMessage(Message);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (check) {
                    startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mEmailEt = (EditText) findViewById(R.id.emailEt);
        mClearEmailBtn = (Button) findViewById(R.id.clearEmailBtn);
        mPasswordEt = (EditText) findViewById(R.id.passwordEt);
        mShowPasswordIm = (ImageView) findViewById(R.id.showPasswordIm);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mForgetPasswordBtn = (Button) findViewById(R.id.forgetPasswordBtn);
        mLayoutSc = (ScrollView) findViewById(R.id.layoutSc);
        mLayoutSc.setVerticalScrollBarEnabled(false);
        mLayoutSc.setHorizontalScrollBarEnabled(false);
    }

}
