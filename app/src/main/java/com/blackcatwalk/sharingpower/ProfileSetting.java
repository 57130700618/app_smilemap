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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ProfileSetting extends AppCompatActivity {

    // -------------- User Interface ------------------//
    private EditText editNickname;
    private TextView alertText;
    private EditText editStaus;
    private Button update;
    private ImageView btnClose;
    private LinearLayout menuPassword;
    private LinearLayout menuLogout;

    // ----------- Data send to database --------------//
    private String name;
    private String staus;
    private String username;
    private String passwordCurrent;
    private String passwordNew;
    private String passwordNewAgain;

    // ----------- Url set form database --------------//
    private static final String setUrl = "https://www.smilemap.me/android/set.php";

    // ----------- Url get form database --------------//
    private static final String url = "https://www.smilemap.me/android/get.php?main=check_password";
    private String tempUrl;

    // ----------- FIle System --------------//
    private static final String stauslogin = "Stauslogin.txt";
    private String stausShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile_setting);

        Control.hideKeyboardEditext(this);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        staus = bundle.getString("staus");

        username = Control.getUsername(this);

        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        editNickname = (EditText) findViewById(R.id.nickname);
        editNickname.setText(name);
        alertText = (TextView) findViewById(R.id.alertText);

        editStaus = (EditText) findViewById(R.id.staus);
        editStaus.setText(staus);

        editStaus.setSelection(staus.length());
        editNickname.setSelection(name.length());

        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = editNickname.getText().toString();
                staus = editStaus.getText().toString();

                if (name.length() < 5) {
                    alertText.setVisibility(View.VISIBLE);
                } else {
                    alertText.setVisibility(View.INVISIBLE);
                    Control.sDialog(ProfileSetting.this);
                    updateName();
                }
            }
        });

        menuPassword = (LinearLayout) findViewById(R.id.password);
        menuPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChagePassword();
            }
        });

        menuLogout = (LinearLayout) findViewById(R.id.logout);
        menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Control.hideKeyboard(this);
    }

    private void updateName() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jor = new StringRequest(Request.Method.POST, setUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateStaus();
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
                params.put("main", "profile");
                params.put("job", "changename");
                params.put("username", username);
                params.put("name", name);
                return params;
            }
        };
        jor.setShouldCache(false);
        requestQueue.add(jor);
    }

    private void updateStaus() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jor = new StringRequest(Request.Method.POST, setUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Control.hDialog();
                        onBackPressed();
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
                params.put("main", "profile");
                params.put("job", "changestaus");
                params.put("username", username);
                params.put("name", staus);
                return params;
            }
        };
        jor.setShouldCache(false);
        requestQueue.add(jor);
    }

    public void dialogChagePassword() {
        final Dialog dialog = new Dialog(ProfileSetting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // no titlebar
        dialog.setContentView(R.layout.activity_dialog_chage_password);

        ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final EditText password1 = (EditText) dialog.findViewById(R.id.password1);
        final EditText password2 = (EditText) dialog.findViewById(R.id.password2);
        final EditText password3 = (EditText) dialog.findViewById(R.id.password3);
        final TextView alertText = (TextView) dialog.findViewById(R.id.alertText);
        final TextView forgetPassword = (TextView) dialog.findViewById(R.id.forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
                startActivity(intent);
            }
        });

        final ImageView picPassword1 = (ImageView) dialog.findViewById(R.id.picPassword1);
        final ImageView picPassword2 = (ImageView) dialog.findViewById(R.id.picPassword2);
        final ImageView picPassword3 = (ImageView) dialog.findViewById(R.id.picPassword3);

        Button btnok = (Button) dialog.findViewById(R.id.ok);
        btnok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                passwordCurrent = password1.getText().toString();
                passwordNew = password2.getText().toString();
                passwordNewAgain = password3.getText().toString();

                tempUrl = url + "&email=" + username + "&password_current=" + Control.md5(passwordCurrent);

                Control.sDialog(ProfileSetting.this);

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, tempUrl + "&ramdom=" + Control.randomNumber(), null,
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

                                    if (test.equals("false")) {
                                        alertText.setText("รหัสผ่านปัจจุบันไม่ถูกต้อง");
                                        picPassword1.setImageResource(R.drawable.icon_password1);
                                        picPassword2.setImageResource(R.drawable.icon_password);
                                        picPassword3.setImageResource(R.drawable.icon_password);
                                    } else {

                                        if (passwordNew.length() < 8) {
                                            alertText.setText("โปรดป้อนรหัสผ่านใหม่อย่างน้อย 8 ตัวอักษร");
                                            picPassword1.setImageResource(R.drawable.icon_password);
                                            picPassword2.setImageResource(R.drawable.icon_password1);
                                            picPassword3.setImageResource(R.drawable.icon_password);
                                            return;
                                        }

                                        picPassword1.setImageResource(R.drawable.icon_password);
                                        picPassword2.setImageResource(R.drawable.icon_password);
                                        picPassword3.setImageResource(R.drawable.icon_password1);

                                        if (passwordNewAgain.length() < 8) {
                                            alertText.setText("โปรดป้อนรหัสผ่านใหม่อีกครั้ง");
                                            return;
                                        }

                                        if (passwordNewAgain.equals(passwordNew)) {
                                            picPassword1.setImageResource(R.drawable.icon_password);
                                            picPassword2.setImageResource(R.drawable.icon_password);
                                            picPassword3.setImageResource(R.drawable.icon_password);
                                            updatePassword(dialog);
                                        } else {
                                            alertText.setText("รหัสผ่านใหม่ไม่ตรงกัน");
                                            return;
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
                                Control.hDialog();
                            }
                        }
                );
                jor.setShouldCache(false);
                requestQueue.add(jor);
            }
        });
        dialog.show();
    }

    private void updatePassword(final Dialog tempDialog) {

        final String tempPassword = Control.md5(passwordNew);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jor = new StringRequest(Request.Method.POST, setUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
                        builder.setMessage("เปลี่ยนรหัสผ่านสำเร็จ");
                        builder.setCancelable(false);
                        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                tempDialog.cancel();
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
                params.put("main", "update_password");
                params.put("email", username);
                params.put("password_new", tempPassword);
                return params;
            }
        };
        jor.setShouldCache(false);
        requestQueue.add(jor);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
        builder.setMessage("ท่านต้องการออกจากระบบหรือไม่");
        builder.setPositiveButton("ออกจากระบบ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                stausShared = "";
                try {
                    FileInputStream fIn = openFileInput("stausShared.txt");
                    InputStreamReader reader = new InputStreamReader(fIn);

                    char[] buffer = new char[100];
                    int charReadCount;
                    while ((charReadCount = reader.read(buffer)) > 0) {
                        String readString = String.copyValueOf(buffer, 0, charReadCount);

                        stausShared += readString;
                        buffer = new char[100];
                    }
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                if(stausShared.length() >1 && stausShared.substring(0, 1).equals("1")){
                    // Stop Service && show staus not shared
                    Intent i = new Intent(ProfileSetting.this, MyService.class);
                    stopService(i);


                    final String typeDelete = stausShared.substring(2, stausShared.indexOf("*"));
                    final String usernameDelete = username;

                    RequestQueue requestQueue = Volley.newRequestQueue(ProfileSetting.this);
                    StringRequest jor = new StringRequest(Request.Method.POST, "https://www.smilemap.me/android/set.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Control.hDialog();

                                    // save staus shared in file
                                    stausShared = "0+";
                                    try {
                                        FileOutputStream fOut = openFileOutput("stausShared.txt", MODE_PRIVATE);
                                        OutputStreamWriter writer = new OutputStreamWriter(fOut);

                                        writer.write(stausShared);
                                        writer.flush();
                                        writer.close();
                                    } catch (IOException ioe) {
                                        ioe.printStackTrace();
                                    }

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
                            params.put("main", "delete");
                            params.put("username", usernameDelete);
                            params.put("type", typeDelete);
                            return params;
                        }
                    };
                    jor.setShouldCache(false);
                    requestQueue.add(jor);
                }

                try {
                    FileOutputStream fOut = openFileOutput(stauslogin, MODE_PRIVATE);
                    OutputStreamWriter writer = new OutputStreamWriter(fOut);

                    writer = new OutputStreamWriter(fOut);
                    writer.write("0");
                    writer.flush();
                    writer.close();

                    LoginManager.getInstance().logOut();

                    Intent login = new Intent(getApplicationContext(), LoginMain.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

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
}
