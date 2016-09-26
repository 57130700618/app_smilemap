package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.utility.Control;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;
import com.blackcatwalk.sharingpower.utility.ControlKeyboard;
import com.facebook.login.LoginManager;

public class ProfileSetting extends AppCompatActivity {

    private ControlDatabase mControlDatabae;
    private Control mControl;
    private Dialog mDialogDialog;

    // -------------- User Interface ------------------
    private EditText mNameEt;
    private TextView mAlertTextTv;
    private EditText mStausEt;
    private Button mUpdateBtn;
    private ImageView mBackIm;
    private LinearLayout mPasswordLy;
    private LinearLayout mLogoutLy;

    private ImageView mPicPassword1Dialog;
    private ImageView mPicPassword2Dialog;
    private ImageView mPicPassword3Dialog;
    private TextView mAlertTextDialog;

    private EditText mPassword1EtDialog;
    private EditText mPassword2EtDialog;
    private EditText mPassword3EtDialog;

    //----------- Data send to database --------------
    private String name;
    private String staus;
    private String passwordCurrent;
    private String passwordNew;
    private String passwordNewAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile_setting);

        bindWidget();

        new ControlKeyboard().setupUIHidenKeyboardWhenClickOutEdittext(findViewById(R.id.parent),this);

        mControlDatabae = new ControlDatabase(this);
        mControl = new Control();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        staus = bundle.getString("staus");

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mNameEt.setText(name);
        mStausEt.setText(staus);
        mStausEt.setSelection(staus.length());
        mNameEt.setSelection(name.length());

        mNameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mNameEt.clearFocus();
                    mStausEt.requestFocus();
                }
                return false;
            }
        });
        mNameEt.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mNameEt.setInputType(InputType.TYPE_CLASS_TEXT);
                return false;
            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    checkUpdate();
            }
        });

        mPasswordLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChagePassword();
            }
        });

        mLogoutLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void checkUpdate() {
        name = mNameEt.getText().toString();
        staus = mStausEt.getText().toString();

        if (name.length() < 5) {
            mAlertTextTv.setVisibility(View.VISIBLE);
        } else {
            mAlertTextTv.setVisibility(View.INVISIBLE);
            mControlDatabae.setDatabaseProfileSettingUpdateName(name,staus);
        }
    }

    public void dialogChagePassword() {
        mDialogDialog = new Dialog(ProfileSetting.this);
        mDialogDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogDialog.setContentView(R.layout.activity_dialog_chage_password);

        ImageView btnClose = (ImageView) mDialogDialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogDialog.cancel();
            }
        });

        mPassword1EtDialog = (EditText) mDialogDialog.findViewById(R.id.password1);
        mPassword2EtDialog = (EditText) mDialogDialog.findViewById(R.id.password2);
        mPassword3EtDialog = (EditText) mDialogDialog.findViewById(R.id.password3);

        mPassword1EtDialog.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPassword();
                }
                return false;
            }
        });

        mPassword2EtDialog.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPassword();
                }
                return false;
            }
        });

        mPassword3EtDialog.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPassword();
                }
                return false;
            }
        });

        final TextView forgetPassword = (TextView) mDialogDialog.findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
            }
        });

        mPicPassword1Dialog = (ImageView) mDialogDialog.findViewById(R.id.picPassword1);
        mPicPassword2Dialog = (ImageView) mDialogDialog.findViewById(R.id.picPassword2);
        mPicPassword3Dialog = (ImageView) mDialogDialog.findViewById(R.id.picPassword3);
        mAlertTextDialog = (TextView) mDialogDialog.findViewById(R.id.alertText);

        Button btnok = (Button) mDialogDialog.findViewById(R.id.ok);
        btnok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPassword();
            }
        });
        mDialogDialog.show();
    }


    public void checkPassword() {
        passwordCurrent = mPassword1EtDialog.getText().toString();
        passwordNew = mPassword2EtDialog.getText().toString();
        passwordNewAgain = mPassword3EtDialog.getText().toString();
        mControlDatabae.getDatabaseProfileSettingPassword("&password_current=" + mControl.md5(passwordCurrent));
    }

   public void setCheckPassword(String _test){
       if (_test.equals("false")) {
           mAlertTextDialog.setText("รหัสผ่านปัจจุบันไม่ถูกต้อง");
           mPicPassword1Dialog.setImageResource(R.drawable.icon_password1);
           mPicPassword2Dialog.setImageResource(R.drawable.icon_password);
           mPicPassword3Dialog.setImageResource(R.drawable.icon_password);
           mPassword1EtDialog.requestFocus();
           mPassword2EtDialog.clearFocus();
           mPassword3EtDialog.clearFocus();
       } else {
           if (passwordNew.length() < 8) {
               mAlertTextDialog.setText("โปรดป้อนรหัสผ่านใหม่อย่างน้อย 8อักขระ");
               mPicPassword1Dialog.setImageResource(R.drawable.icon_password);
               mPicPassword2Dialog.setImageResource(R.drawable.icon_password1);
               mPicPassword3Dialog.setImageResource(R.drawable.icon_password);
               mPassword1EtDialog.clearFocus();
               mPassword2EtDialog.requestFocus();
               mPassword3EtDialog.clearFocus();
               return;
           }

           mPicPassword1Dialog.setImageResource(R.drawable.icon_password);
           mPicPassword2Dialog.setImageResource(R.drawable.icon_password);
           mPicPassword3Dialog.setImageResource(R.drawable.icon_password1);
           mPassword1EtDialog.clearFocus();
           mPassword2EtDialog.clearFocus();
           mPassword3EtDialog.requestFocus();

           if (passwordNewAgain.length() < 8) {
               mAlertTextDialog.setText("โปรดป้อนรหัสผ่านใหม่อีกครั้ง");
               return;
           }

           if (passwordNewAgain.equals(passwordNew)) {
               mPicPassword1Dialog.setImageResource(R.drawable.icon_password);
               mPicPassword2Dialog.setImageResource(R.drawable.icon_password);
               mPicPassword3Dialog.setImageResource(R.drawable.icon_password);

               mControlDatabae.setDatabaseProfileSettingPassword(mControl.md5(passwordNew));
               return;
           } else {
               mAlertTextDialog.setText("รหัสผ่านใหม่ไม่ตรงกัน");
               return;
           }
       }
    }

    public void setDialogPasswordSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
        builder.setMessage("เปลี่ยนรหัสผ่านสำเร็จ");
        builder.setCancelable(false);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mDialogDialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
        builder.setMessage("ท่านต้องการออกจากระบบหรือไม่");
        builder.setPositiveButton("ออกจากระบบ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                ControlFile _controlFile = new ControlFile();

                String _stausShared = _controlFile.getFile(getApplicationContext(),"stausShared");

                if (_stausShared.length() > 1 && _stausShared.substring(0, 1).equals("1")) {
                    stopService(new Intent(ProfileSetting.this, MyService.class));
                    mControlDatabae.setDatabaseProfileSettingStopSharedBus(_stausShared.substring(2, _stausShared.indexOf("*")));
                }

                _controlFile.setFile(getApplicationContext(),"0","stausLogin");

                LoginManager.getInstance().logOut();
                startActivity(new Intent(getApplicationContext(), LoginMain.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

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
        mNameEt = (EditText) findViewById(R.id.nameEt);
        mNameEt.setInputType(InputType.TYPE_NULL);
        mStausEt = (EditText) findViewById(R.id.stausEt);
        mAlertTextTv = (TextView) findViewById(R.id.alertTextTv);
        mUpdateBtn = (Button) findViewById(R.id.updateBtn);
        mPasswordLy = (LinearLayout) findViewById(R.id.passwordLy);
        mLogoutLy = (LinearLayout) findViewById(R.id.logoutLy);
    }
}
