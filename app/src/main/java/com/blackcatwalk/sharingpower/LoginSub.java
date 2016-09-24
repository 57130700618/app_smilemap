
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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

public class LoginSub extends AppCompatActivity {

    private String mUserName = "";
    private String mPassWord = "";
    private int mCountInvalid = 0;
    private boolean mCheckShowPassword = true;

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

        Control.hideKeyboardEditext(this);

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
                    mCheckShowPassword = false;
                } else {
                    mPasswordEt.setInputType(129);
                    mCheckShowPassword = true;
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
        Control.hideKeyboard(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Control.hDialog();
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

    private void checkLogin() {
        mUserName = mEmailEt.getText().toString();
        mPassWord = mPasswordEt.getText().toString();

        if (!mUserName.isEmpty() && !mPassWord.isEmpty()) {

            mClearEmailBtn.setVisibility(View.INVISIBLE);

            mPassWord = Control.md5(mPassWord);

            Control.sDialog(this);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Control.getMGetDatabase() + "login&email="
                    + mUserName + "&password=" + mPassWord + "&ramdom=" + Control.randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONArray ja = response.getJSONArray("users");

                                String test = "";
                                JSONObject jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {
                                    jsonObject = ja.getJSONObject(i);
                                    test = jsonObject.getString("check");
                                }

                                Control.hDialog();
                                if (test.equals("true")) {
                                    saveStausLoginToFile();
                                } else {
                                    mCountInvalid++;

                                    if (mCountInvalid >= 5) {
                                        alertDialog(getString(R.string.general_text_2),
                                                getString(R.string.general_text_3), true);

                                    } else {
                                        alertDialog(getString(R.string.general_text_4),
                                                getString(R.string.general_text_5), false);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    private void saveStausLoginToFile() {

        Control.setUserNameToFile(this, mUserName);
        Control.setStausLoginToFile(this, "1");

        startActivity(new Intent(this, MainActivity.class));
        finish();
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
}