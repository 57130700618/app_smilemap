package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterPage3 extends AppCompatActivity {

    private String mPassword = "";
    private String mHidePassword = "";
    private String mEmail = "";

    // -------------- User Interface ------------------//
    private TextView mEmailTv;
    private TextView mPasswordTv;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page3);

        bindWidget();
        getBundle();
        setUpEventWigget();
    }

    private void getBundle() {
        Bundle _bundle = getIntent().getExtras();
        mPassword = _bundle.getString("password");
        mEmail = _bundle.getString("email");
    }

    private void setUpEventWigget() {
        mEmailTv.setText(mEmail);

        for (int i = 1; i <= mPassword.length(); i++) {
            mHidePassword = mHidePassword + "*";
        }
        mPasswordTv.setText(mHidePassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    private void bindWidget() {
        mEmailTv = (TextView) findViewById(R.id.emailTv);
        mPasswordTv = (TextView) findViewById(R.id.passwordTv);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
    }
}


