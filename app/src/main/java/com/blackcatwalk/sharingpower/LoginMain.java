package com.blackcatwalk.sharingpower;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class LoginMain extends AppCompatActivity {

    private Boolean mCheckPermission = false;
    private CallbackManager mCallbackFacebook;
    private int mCountText = 0;
    private Handler mHandler = new Handler();
    private Timer mTimer = new Timer();
    private Animation mAnimationIn = new AlphaAnimation(0.0f, 1.0f);
    private Animation mAnimationOut = new AlphaAnimation(1.0f, 0.0f);
    private ControlCheckConnect mControlCheckConnect;
    private ControlFile mControlFile;

    // -------------- User Interface ------------------//
    private Button mLoginFacebookBtn;
    private Button mLoginSmileMapBtn;
    private LoginButton mTempLoginFacebookBtn;
    private TextView mAnimationTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackFacebook = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login_main);

        bindWidget();

        mControlCheckConnect = new ControlCheckConnect();
        mControlFile = new ControlFile();

        requestRunTimePermission();

        setAnimation();

        mLoginSmileMapBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    startActivity(new Intent(LoginMain.this, LoginSub.class));
                }
            }
        });

        mLoginFacebookBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    facebookLogin();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mControlCheckConnect.checkInternet(this)) {
            mControlCheckConnect.alertInternet(this);
        }

        if (!mControlCheckConnect.checkGPS(this)) {
            mControlCheckConnect.alertGps(this);
        }

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        mCallbackFacebook.onActivityResult(_requestCode, _resultCode, _data);
    }

    private void setAnimation() {
        mAnimationIn.setDuration(2500);
        mAnimationOut.setDuration(2500);

        mAnimationTxt.setText(R.string.animation_text_1);
        TimerTask myTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        mAnimationTxt.startAnimation(mAnimationOut);
                    }
                });
            }
        };
        mTimer.schedule(myTask, 0, 8000); // TimerTask, delay, period

        mAnimationOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                switch (mCountText) {
                    case 0:
                        mAnimationTxt.setText(R.string.animation_text_2);
                        break;
                    case 1:
                        mAnimationTxt.setText(R.string.animation_text_3);
                        break;
                    case 2:
                        mAnimationTxt.setText(R.string.animation_text_1);
                        mCountText = -1;
                        break;
                }
                mCountText++;
                mAnimationTxt.startAnimation(mAnimationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void facebookLogin() {

        mTempLoginFacebookBtn.setReadPermissions("email,public_profile");//,user_friends");
        mTempLoginFacebookBtn.performClick();
        mTempLoginFacebookBtn.registerCallback(mCallbackFacebook, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                GraphRequest data_request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject json_object,
                                    GraphResponse response) {
                                    try {
                                        // JSONObject profile_pic_data, profile_pic_url;

                                        final String _userName = json_object.get("email").toString();
                                        final String _nickName = json_object.get("name").toString();
                                        final String _gender = json_object.get("gender").toString();
                                        final String _password = json_object.get("id").toString();

                                        String _sex = "หญิง";
                                        if (_gender.equals("male")) {
                                            _sex = "ชาย";
                                        }

                                       /* final String _urlPic;

                                        profile_pic_data = new JSONObject(json_object.get("picture").toString());
                                        profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

                                        _urlPic = profile_pic_url.getString("url");*/

                                        new ControlDatabase(LoginMain.this).setDabaseLoginMain(_userName, _password,
                                                _nickName, _sex);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                            }
                        });
                Bundle permission_param = new Bundle();
                permission_param.putString("fields", "id,name,email,gender"); //,picture.width(150).height(150)");
                data_request.setParameters(permission_param);
                data_request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    public void updateFile(String _userName) {
        mControlFile.setFile(this, _userName,"userName");
        mControlFile.setFile(this, "1","stausLogin");

        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void requestRunTimePermission() {

        Nammu.init(this);
        Nammu.askForPermission(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        }, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                mCheckPermission = true;

                if (mControlFile.getFile(LoginMain.this,"stausLogin").equals("1")) {
                    startActivity(new Intent(LoginMain.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void permissionRefused() {
                mCheckPermission = false;
                check();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean check() {
        if (mCheckPermission == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginMain.this);
            builder.setMessage(getString(R.string.general_text_1));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    requestRunTimePermission();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.parseColor("#147cce"));
            pbutton.setTypeface(null, Typeface.BOLD);
            return false;
        }
        return true;
    }

    private void bindWidget() {
        mAnimationTxt = (TextView) findViewById(R.id.animationTxt);
        mLoginSmileMapBtn = (Button) findViewById(R.id.loginSmileMapBtn);
        mLoginFacebookBtn = (Button) findViewById(R.id.loginFacebookBtn);
        mTempLoginFacebookBtn = (LoginButton) findViewById(R.id.tempLoginFacebookBtn);
    }
}










