package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassword extends AppCompatActivity {

    // -------------- User Interface ------------------//
    private ImageView mBackIm;
    private ScrollView mLayoutSc;
    private TextView mAlertEmailTv;
    private EditText mEmailEt;
    private Button mClearEdittextBtn;
    private Button mNextBtn;

    // ----------- Data send to database --------------//
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forget_password);

        bindWidget();

        Control.showKeyboard(this);

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
                mClearEdittextBtn.setVisibility(View.INVISIBLE);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEmailEt.getText().toString().length() > 0) {
                    mClearEdittextBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        mEmailEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkForgetPassword();
                }
                return false;
            }
        });

        mClearEdittextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailEt.setText("");
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForgetPassword();
            }
        });
    }

    private void checkForgetPassword() {
        mEmail = mEmailEt.getText().toString();

        if (!Control.isValidEmail(mEmail)) {
            mAlertEmailTv.setTextColor(Color.parseColor("#f25d5d"));
            mAlertEmailTv.setText(R.string.general_text_6);
        } else {
            Control.sDialog(ForgetPassword.this);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
            StringRequest jor = new StringRequest(Request.Method.POST, Control.getMGetDatabaseResetPassword(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Control.hDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassword.this);
                            builder.setCancelable(false);
                            builder.setTitle(R.string.general_text_20);
                            builder.setMessage(R.string.general_text_21);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                    finish();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(Color.parseColor("#147cce"));
                            pbutton.setTypeface(null, Typeface.BOLD);
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
                    params.put("email", mEmail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        }
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mAlertEmailTv = (TextView) findViewById(R.id.alertEmailTv);
        mEmailEt = (EditText) findViewById(R.id.emailEt);
        mClearEdittextBtn = (Button) findViewById(R.id.clearEdittextBtn);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mLayoutSc = (ScrollView) findViewById(R.id.layoutSc);
        mLayoutSc.setVerticalScrollBarEnabled(false);
        mLayoutSc.setHorizontalScrollBarEnabled(false);
    }
}
