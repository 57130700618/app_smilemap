package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterPage3 extends AppCompatActivity {

    // -------------- User Interface ------------------//
    private TextView mEmailTv;
    private TextView mPasswordTv;
    private Button mLoginBtn;

    // ---------------- Data Preview -----------------//
    private String mPassword = "";
    private String mHidePassword = "";
    private String mEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page3);

        bindWidget();

        Bundle bundle = getIntent().getExtras();
        mPassword = bundle.getString("password");
        mEmail = bundle.getString("email");

        mEmailTv.setText(mEmail);

        for (int i = 1; i <= mPassword.length(); i++) {
            mHidePassword = mHidePassword + "*";
        }
        mPasswordTv.setText(mHidePassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Control.setUserNameToFile(RegisterPage3.this, mEmail);
                Control.setStausLoginToFile(RegisterPage3.this, "1");

                startActivity(new Intent(getApplicationContext(), MainActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }

    private void bindWidget() {
        mEmailTv = (TextView) findViewById(R.id.emailTv);
        mPasswordTv = (TextView) findViewById(R.id.passwordTv);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);

    }
}


