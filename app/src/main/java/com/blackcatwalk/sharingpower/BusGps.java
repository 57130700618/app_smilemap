package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusGps extends AppCompatActivity implements LocationListener {

    // ----------------- Google_Map -------------------//
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private LatLng currentLocation;
    private long updatareInterval = 120000;  // 1minute = 60000 milesecond
    private long fastestInterval = 90000;
    private String distance;
    private String duration;
    private MarkerOptions marker;
    private Marker clickMaker;
    private float currentZoom = 30;

    // --------------- User Interface -----------------//
    private de.hdodenhof.circleimageview.CircleImageView btnSharedBus;
    private de.hdodenhof.circleimageview.CircleImageView btnStopShared;
    private boolean checkBtnSharedBus = false;    // true = show button navigator
    private int selectTraffic = 0;                 // 0=bus, 1=brt, 2=bts, 3=boat, 4=checkpoint, 5=accident, 6=public
    private String tempNameType = "";
    private String tempBusType = "";
    private AutoCompleteTextView autoComplete;
    private ImageView btnClose;
    private TextView titleName;
    private ImageView btnCureentLocation;
    private CheckBox checkBoxBusFree;
    private EditText detail;
    private EditText name;
    private TextView textAlert;
    private Spinner spinner;
    private ImageView userManualIm;
    private ImageView menu;
    private SupportMapFragment mapFragment;

    // ------------ Data send to database -------------//
    private String busNo;
    private String busType = "bus";

    // ----------- Url get form database --------------//
    private String getBusUrl = "https://www.smilemap.me/android/get.php?main=bus&sub=";
    private String tempUrl;

    // ----------- Url set form database --------------//
    private String setBusUrl = "https://www.smilemap.me/android/set.php";

    // ---------------- FIle System -------------------//
    private static final String fileStausShared = "stausShared.txt";
    private static final int readSize = 100; // Read 100byte
    private String stausShared = "0+";  // 1 = showStausShared

    private static final String fileUserName = "Username.txt";
    private String username;

    private static final String fileSetting = "setting.txt";
    private String stausTaffic = "1"; // 1 = showTraffic
    private String resultNearby = "55"; // Nearby 50 km.
    private String tempResultNearby = null;
    private String maptype = "0"; // 0 = normal , 1 = map_satelltte

    private static final String fileAlertUsermanual = "showUsermanual.txt";
    private String stausAlertUsermanual = "show";

    private BusGpsCustomSpinnerAdapter adapter;
    private boolean checkAccidentOrCheakpoint;
    private String stausCheckAccidentOrCheakpointt;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    private String _name = null;
    private String _type = null;
    private String _category = null;
    private String _busFree = null;
    private String _detailBus = null;
    private String _amountPerson = null;
    private String _colorMarker = null;

    private double mSizeInInches;

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide();

        mSizeInInches = Control.getSizeScrren(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        checkVersionApp();

        bindWidget();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng bangkok = new LatLng(14.76553, 100.53904);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangkok, 8));
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.setTrafficEnabled(true);
                readFile(fileSetting);

                mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {

                        if (!marker.getTitle().equals("ตำแหน่งปัจจุบัน")) {
                            Control.sDialog(BusGps.this);
                            clickMaker = marker;
                            LatLng destination = marker.getPosition();

                            // Getting URL to the Google Directions API
                            String url = Control.getDirectionsUrl(currentLocation, destination);

                            DownloadTask downloadTask = new DownloadTask();
                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(currentZoom).tilt(30).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                        return true;
                    }
                });

                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition pos) {
                        if (pos.zoom != currentZoom) {
                            currentZoom = pos.zoom;
                        }
                    }
                });

                mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {

                        Intent intent = new Intent(getApplicationContext(), TrafficDetail.class);
                        intent.putExtra("lat", marker.getPosition().latitude);
                        intent.putExtra("lng", marker.getPosition().longitude);
                        intent.putExtra("type", selectTraffic);
                        intent.putExtra("duration", duration);
                        intent.putExtra("distance", distance);
                        intent.putExtra("name", marker.getTitle().substring(0, marker.getTitle().length() - 1));
                        intent.putExtra("detail", marker.getSnippet());
                        startActivity(intent);
                    }
                });
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (mGoogleApiClient != null) {
                            mGoogleApiClient.connect();
                        }
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Control.alertCurrentGps(BusGps.this);
                    }
                }).build();

        titleName.setText("การจราจร");

        userManualIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusGps.this, Tutorial.class).putExtra("type","traffic"));
            }


        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    readFile("setting.txt");

                    final Dialog dialog = new Dialog(BusGps.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.activity_dialog_setting);

                    btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    Switch showTraffic = (Switch) dialog.findViewById(R.id.showTraffic);
                    showTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                stausTaffic = "1";
                                mMap.setTrafficEnabled(true);

                            } else {
                                stausTaffic = "0";
                                mMap.setTrafficEnabled(false);
                            }
                            saveFile();
                        }
                    });

                    if (stausTaffic.equals("0")) {
                        showTraffic.setChecked(false);
                    }

                    final TextView resultNearbyTxt = (TextView) dialog.findViewById(R.id.resultNearbyTxt);

                    SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                            switch (progresValue) {
                                case 0:
                                    resultNearby = "3";
                                    break;
                                case 1:
                                    resultNearby = "6";
                                    break;
                                case 2:
                                    resultNearby = "12";
                                    break;
                                case 3:
                                    resultNearby = "23";
                                    break;
                                case 4:
                                    resultNearby = "32";
                                    break;
                                case 5:
                                    resultNearby = "55";
                                    break;
                            }
                            saveFile();
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            showTxtkm(seekBar, resultNearbyTxt);
                        }
                    });

                    showTxtkm(seekBar, resultNearbyTxt);

                    final RadioButton satelltte = (RadioButton) dialog.findViewById(R.id.satelltte);
                    final RadioButton normal = (RadioButton) dialog.findViewById(R.id.normal);

                    normal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            maptype = "0";
                            saveFile();
                            satelltte.setChecked(false);
                            normal.setChecked(true);
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                    });

                    satelltte.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            maptype = "1";
                            saveFile();
                            satelltte.setChecked(true);
                            normal.setChecked(false);
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        }
                    });

                    if (maptype.equals("0")) {
                        normal.setChecked(true);
                    } else {
                        satelltte.setChecked(true);
                    }

                    dialog.show();
                }
            }

            void showTxtkm(SeekBar seekBar, TextView resultNearbyTxt) {

                switch (resultNearby) {
                    case "3":
                        seekBar.setProgress(0);
                        tempResultNearby = "2";
                        break;
                    case "6":
                        seekBar.setProgress(1);
                        tempResultNearby = "5";
                        break;
                    case "12":
                        seekBar.setProgress(2);
                        tempResultNearby = "10";
                        break;
                    case "23":
                        seekBar.setProgress(3);
                        tempResultNearby = "20";
                        break;
                    case "32":
                        seekBar.setProgress(4);
                        tempResultNearby = "30";
                        break;
                    case "55":
                        seekBar.setProgress(5);
                        tempResultNearby = "50";
                        break;
                }
                resultNearbyTxt.setText("(" + tempResultNearby + " km)");
            }
        });

        btnSharedBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    sharedBus();
                } else {
                    Control.alertCurrentGps(BusGps.this);
                }
            }
        });

        btnStopShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopShared();
            }
        });

        btnCureentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    moveAnimateCamera(currentLocation);
                } else {
                    Control.alertCurrentGps(BusGps.this);
                }
            }
        });

        CustomSpinner bus_data[] = new CustomSpinner[]{
                new CustomSpinner(R.drawable.spinner_search_gray, getString(R.string.select_traffic)),
                new CustomSpinner(R.drawable.traffic_spinner_bus_gray, getString(R.string.bus_name)),
                new CustomSpinner(R.drawable.traffic_spinner_bts_gray, getString(R.string.bts_name)),
                new CustomSpinner(R.drawable.traffic_spinner_brt_gray, getString(R.string.brt_name)),
                new CustomSpinner(R.drawable.traffic_spinner_van_gray, getString(R.string.van_name)),
                new CustomSpinner(R.drawable.traffic_spinner_public_gray, getString(R.string.public_name)),
                new CustomSpinner(R.drawable.traffic_spinner_boat_gray, getString(R.string.boat_name)),
                new CustomSpinner(R.drawable.traffic_spinner_accident_gray, getString(R.string.accident_name)),
                new CustomSpinner(R.drawable.traffic_spinner_checkpoint_gray, getString(R.string.checkpoint_name))};

        adapter = new BusGpsCustomSpinnerAdapter(this, R.layout.activity_spinner, bus_data);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        adapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int position, long id) {

                if (position == 0) {
                    return;
                }

                if (currentLatitude != 0 && currentLongitude != 0) {

                    tempUrl = null;
                    selectTraffic = position - 1;

                    switch (position) {
                        case 1:
                            busType = "bus";
                            break;
                        case 2:
                            busType = "bts";
                            break;
                        case 3:
                            busType = "brt";
                            break;
                        case 4:
                            busType = "van";
                            break;
                        case 5:
                            busType = "public";
                            break;
                        case 6:
                            busType = "boat";
                            break;
                        case 7:
                            busType = "accident";
                            break;
                        case 8:
                            busType = "checkpoint";
                            break;
                    }

                    Control.sDialog(BusGps.this);
                    getDatabase();

                    if (checkBtnSharedBus == false) {
                        addMarkerCurrent(currentLatitude, currentLongitude);
                    } else {
                        moveAnimateCamera(currentLocation);
                    }
                } else {
                    Control.alertCurrentGps(BusGps.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> av) {
                return;
            }
        });

        readFile(fileUserName);
        readFile(fileStausShared);
    }

    private void checkVersionApp() {

        Control.sDialog(this);

        final String _versionApp = Control.getVersionApp(this);

        String _url = "https://www.smilemap.me/android/get.php?main=version";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, _url + "&ramdom=" + Control.randomNumber(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray ja = response.getJSONArray("version");

                            String _versionAppForDatabase = "";
                            JSONObject jsonObject = null;

                            for (int i = 0; i < ja.length(); i++) {
                                jsonObject = ja.getJSONObject(i);
                                _versionAppForDatabase = jsonObject.getString("version");
                            }

                            Control.hDialog();

                            if (!_versionAppForDatabase.equals(_versionApp)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(BusGps.this);
                                builder.setCancelable(false);
                                builder.setTitle("อัพเดทเวอร์ชันใหม่");
                                builder.setMessage("แอพพลิเคชันสมายแมพที่ท่านใช้งานอยู่ยังไม่ใช่เวอร์ชันปัจจุบัน " +
                                        "กรุณาอัพเดทเป็นเวอร์ชันใหม่ new version." + _versionAppForDatabase);
                                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Control.openGooglePlay(BusGps.this,"ads");
                                    }
                                });
                                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                pbutton.setTextColor(Color.parseColor("#147cce"));
                                pbutton.setTypeface(null, Typeface.BOLD);
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

    private void bindWidget() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        spinner = (Spinner) findViewById(R.id.spinner);
        menu = (ImageView) findViewById(R.id.main);
        userManualIm = (ImageView) findViewById(R.id.userManualIm);
        titleName = (TextView) findViewById(R.id.titleName);
        btnSharedBus = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.btnShared);
        btnStopShared = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.btnStopShared);
        btnCureentLocation = (ImageView) findViewById(R.id.btnCureentLocation);
    }

    private void readFile(String tempFileName) {

        String temp = "";

        try {
            FileInputStream fIn = openFileInput(tempFileName);
            InputStreamReader reader = new InputStreamReader(fIn);

            char[] buffer = new char[readSize];
            int charReadCount;
            while ((charReadCount = reader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charReadCount);

                temp += readString;
                buffer = new char[readSize];
            }
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (temp.length() > 1) {

            switch (tempFileName) {
                case "Username.txt":
                    username = temp;
                    break;
                case "stausShared.txt":
                    stausShared = "";
                    stausShared = temp;
                    if (stausShared.substring(0, 1).equals("1")) {    // Set staus shared
                        checkBtnSharedBus = true;
                        btnStopShared.setVisibility(View.VISIBLE);
                        busType = stausShared.substring(2, stausShared.indexOf("*"));
                        tempBusType = busType;
                        selectTraffic = Integer.parseInt(stausShared.substring(stausShared.indexOf("*") + 1, stausShared.length()));
                    }
                    break;
                case "setting.txt":
                    stausTaffic = temp.substring(0, 1);
                    maptype = temp.substring(1, 2);
                    resultNearby = temp.substring(2, temp.length());

                    if (mMap != null) {
                        if (stausTaffic.equals("1")) {
                            mMap.setTrafficEnabled(true);
                        } else {
                            mMap.setTrafficEnabled(false);
                        }

                        if (maptype.equals("0")) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        } else {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        }
                    }
                    break;
                case "showUsermanual.txt":
                    if (temp.equals("not")) {
                        stausAlertUsermanual = "not";
                    }
                    break;
            }
        }
    }

    private void saveFile() {
        try {
            FileOutputStream fOut = openFileOutput(fileSetting, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fOut);

            writer.write(stausTaffic + maptype + resultNearby);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        Control.hDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readFile(fileSetting);

        if (mGoogleApiClient.isConnected()) {
            tempUrl = null;
            tempUrl = getBusUrl + busType + "&lat=" + currentLatitude + "&lng=" + currentLongitude + "&distance=" + resultNearby;
            startLocationUpdates();
        }

        if (!Control.checkInternet(this)) {
            Control.alertInternet(this);
        }

        if (!Control.checkGPS(this)) {
            Control.alertGps(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdate();
        }
        Control.hDialog();
    }

    //  ------------------  google map -----------------//

    @SuppressWarnings({"MissingPermission"})
    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(updatareInterval);
        mLocationRequest.setFastestInterval(fastestInterval);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        getDatabase();
    }

    private void addMarkerDatabase(String busNo, double latitude, double longitude, String busTypeTemp,
                                   int busFree, String busDetail, String tempUsername, String color,
                                   double distanceTemp, String amountPerSon) {

        LatLng latLng = new LatLng(latitude, longitude);
        marker = new MarkerOptions();

        IconGenerator factory = new IconGenerator(this);
        Bitmap icon;
        String tempDistance = distanceTemp + "$";

        // 0=bus, 1=bts, 2=brt, 3=van , 4=public, 5=boat, 6=accident, 7=checkpoint,
        switch (selectTraffic) {
            case 0:
                if (busFree == 1) {
                    factory.setColor(Color.parseColor("#FFFFB405"));    // color: gold
                    busTypeTemp = busTypeTemp + "(ฟรี)";
                } else if (busTypeTemp.equals("รถธรรมดา")) {
                    factory.setColor(Color.parseColor("#dc4545"));   // color: red
                } else {
                    factory.setColor(Color.parseColor("#2196F3"));   // color: blue
                }
                tempDistance = tempDistance + busTypeTemp + "%$&*-" + amountPerSon;
                break;
            case 1:
                factory.setColor(Color.parseColor("#2196F3"));   // color: blue
                tempDistance = tempDistance + "%$&*-" + amountPerSon;
                break;
            case 2:
                factory.setColor(Color.parseColor("#77cc1c"));  // color: green
                tempDistance = tempDistance + "%$&*-" + amountPerSon;
                break;
            case 3:
                factory.setColor(Color.parseColor("#FF21CCB5")); // color: bluegreen
                tempDistance = tempDistance + busDetail + "%$&*-" + amountPerSon;
                break;
            case 4:
                factory.setColor(Color.parseColor(color));    // custom color
                tempDistance = tempDistance + busDetail;
                break;
            case 5:
                factory.setColor(Color.parseColor("#1c68a4"));   // color: blueBold
                tempDistance = tempDistance + "%$&*-" + amountPerSon;
                break;
            case 6:
                factory.setColor(Color.parseColor("#d44444"));   // color: red
                tempDistance = tempDistance + busDetail + "%$&*-" + busTypeTemp;
                break;
            case 7:
                factory.setColor(Color.parseColor("#EF6C00"));   // color: brow
                tempDistance = tempDistance + busDetail + "%$&*-" + busTypeTemp;
                break;
        }

        marker.snippet(tempDistance);

        if (mSizeInInches >= 9.5) {
            factory.setTextAppearance(R.style.iconGenTextTablet);
        } else if (mSizeInInches >= 6.5) {
            factory.setTextAppearance(R.style.iconGenTextTablet);
        } else {
            factory.setTextAppearance(R.style.iconGenText);
        }

        icon = factory.makeIcon(busNo);

        marker.position(latLng).title(busNo + "$" + tempUsername).icon(BitmapDescriptorFactory.fromBitmap(icon)); //.fromResource(R.drawable.bus)
        mMap.addMarker(marker);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                if (marker.getTitle().substring(marker.getTitle().indexOf("$") + 1, marker.getTitle().length()).equals(username)) {

                    LayoutInflater inflater = getLayoutInflater();
                    View infoWindow = inflater.inflate(R.layout.info_window, null);

                    TextView window = (TextView) infoWindow.findViewById(R.id.window);
                    window.setText("ตำแหน่งที่ฉันแชร์");

                    return infoWindow;
                } else {

                    String distanceNumText = marker.getSnippet().substring(0, marker.getSnippet().indexOf("$"));
                    Double distanceNum = Double.parseDouble(distanceNumText);

                    if (distanceNum <= 1.0) {
                        switch (distanceNumText) {
                            case "0.0":
                                distance = "10 เมตร";
                                break;
                            case "0.1":
                                distance = "100 เมตร";
                                break;
                            case "0.2":
                                distance = "200 เมตร";
                                break;
                            case "0.3":
                                distance = "300 เมตร";
                                break;
                            case "0.4":
                                distance = "400 เมตร";
                                break;
                            case "0.5":
                                distance = "500 เมตร";
                                break;
                            case "0.6":
                                distance = "600 เมตร";
                                break;
                            case "0.7":
                                distance = "700 เมตร";
                                break;
                            case "0.8":
                                distance = "800 เมตร";
                                break;
                            case "0.9":
                                distance = "900 เมตร";
                                break;
                            case "1.0":
                                distance = "1 กม.";
                                break;
                        }
                    } else {
                        distance = distanceNumText + " กม.";
                    }

                    LayoutInflater inflater = getLayoutInflater();
                    View infoWindow = inflater.inflate(R.layout.activity_info_window_traffic, null);

                    TextView category = (TextView) infoWindow.findViewById(R.id.category);
                    TextView detail = (TextView) infoWindow.findViewById(R.id.detail);

                    String tempType = null;
                    String detailTraffic = null;

                    switch (busType) {
                        case "bus":
                            tempType = "รถเมล์" + marker.getSnippet().substring(marker.getSnippet().indexOf("$") + 3,
                                    marker.getSnippet().indexOf("%$&*-", 0));
                            detailTraffic = "จำนวนผู้โดยสาร: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length());
                            break;
                        case "bts":
                            tempType = "รถไฟฟ้าบีทีเอส";
                            detail.setVisibility(View.GONE);
                            break;
                        case "brt":
                            tempType = "รถโดยสายบีอาที";
                            detailTraffic = "จำนวนผู้โดยสาร: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length());
                            break;
                        case "van":
                            tempType = "รถตู้";
                            detailTraffic = "จำนวนที่นั่ง: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length());
                            break;
                        case "public":
                            tempType = "ตำแหน่งสาธารณะ";
                            detail.setVisibility(View.GONE);
                            break;
                        case "boat":
                            tempType = "เรือโดยสาร";
                            detailTraffic = "จำนวนผู้โดยสาร: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length());
                            break;
                        case "accident":
                            tempType = "อุบัติเหตุ";
                            detailTraffic = "ปักหมุดเวลา: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length());
                            break;
                        case "checkpoint":
                            tempType = "ก่อสร้าง";
                            detailTraffic = "ปักหมุดวันที่: " + marker.getSnippet().substring
                                    (marker.getSnippet().indexOf("%$&*-", 0) + 5, marker.getSnippet().length() - 6) + "...";
                            break;
                    }

                    category.setText("ประเภท: " + tempType);
                    detail.setText(detailTraffic);

                    TextView distanceTxt = (TextView) infoWindow.findViewById(R.id.distanceTxt);
                    distanceTxt.setText(distance);

                    TextView durationTxt = (TextView) infoWindow.findViewById(R.id.durationTxt);
                    durationTxt.setText(duration);

                    return infoWindow;
                }
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    private void addMarkerCurrent(double currentLatitude, double currentLongitude) {
        marker = new MarkerOptions();
        currentLocation = new LatLng(currentLatitude, currentLongitude);
        if (checkBtnSharedBus == false) {
            marker.position(currentLocation).title(getString(R.string.current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
        } else {
            marker.position(currentLocation).title(getString(R.string.current_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hide));
        }
        mMap.addMarker(marker);
        moveAnimateCamera(currentLocation);
    }

    private void moveAnimateCamera(LatLng currentLocation) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(14).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // -------------------- Get Duration , Distance ---------------------------//

    /**
     * A method to download json data from url
     */
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = Control.downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JsonParserDirections parser = new JsonParserDirections();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            //distance = "";
            duration = "";

            if (result.size() < 1) {
                return;
            }
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        // distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        break;
                    }
                }
                //int a = distance.length();
                //a -= 2;
                //distance = distance.substring(0, a);
                //distance = distance + "กม.";

                int a = duration.length();
                if (a > 7) {
                    int b;
                    String temp = "";

                    b = duration.indexOf("r");
                    temp = duration.substring(b + 2, a - 4);

                    b = duration.indexOf("h");
                    b = b - 1;
                    duration = duration.substring(0, b);
                    duration = duration + " ชม. ";

                    duration = duration + temp + "น.";
                } else {
                    a -= 4;
                    duration = duration.substring(0, a);
                    duration = duration + " นาที";
                }

                clickMaker.showInfoWindow();
                Control.hDialog();

            }
        }
    }

    //  ------------------  Database Mysql -----------------//

    private void getDatabase() {

        if (marker != null) {
            mMap.clear();
        }

        addMarkerCurrent(currentLatitude, currentLongitude);

        tempUrl = null;
        tempUrl = getBusUrl + busType + "&lat=" + currentLatitude + "&lng=" + currentLongitude + "&distance=" + resultNearby;

        if (Control.checkInternet(this)) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, tempUrl + "&ramdom=" + Control.randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONArray ja = response.getJSONArray("shared_bus");


                                String _busNo = null;
                                String _busType = null;
                                String _busDetail = null;
                                String _amountPerson = null;
                                String _username = "";
                                String _color = null;
                                String _tempTime;

                                double _longitude = 0;
                                double _latitude = 0;
                                double _distance = 0;

                                int _busFree = 0;

                                DecimalFormat decim = new DecimalFormat("0.0");

                                JSONObject _jsonObject = null;

                                for (int i = 0; i < ja.length(); i++) {

                                    _jsonObject = ja.getJSONObject(i);
                                    _latitude = Double.parseDouble(_jsonObject.getString("lat"));
                                    _longitude = Double.parseDouble(_jsonObject.getString("lng"));
                                    _busNo = _jsonObject.getString("name");
                                    // _username = _jsonObject.getString("username");
                                    _color = _jsonObject.getString("color");

                                    //---------------------------------------------------------------------------
                                    // 0=bus, 1=bts, 2=brt, 3 = van, 4=public, 5=boat, 6=accident, 7=checkpoint
                                    //---------------------------------------------------------------------------
                                    switch (selectTraffic) {
                                        case 0:
                                            _busType = _jsonObject.getString("category");
                                            _busFree = _jsonObject.getInt("bus_free");
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 2:
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 3:
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 4:
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                        case 5:
                                            _amountPerson = _jsonObject.getString("amount_person");
                                            break;
                                        case 6:
                                            _busNo = _jsonObject.getString("category");
                                            _tempTime = _jsonObject.getString("update_date");
                                            _busType = _tempTime.substring(_tempTime.indexOf(" ") + 1, _tempTime.length()).substring(0, 5) + " นาที";
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                        case 7:
                                            _busNo = _jsonObject.getString("category");
                                            _busType = _jsonObject.getString("update_date");
                                            _busDetail = _jsonObject.getString("bus_detail");
                                            break;
                                    }

                                    _distance = Double.parseDouble(decim.format(Double.parseDouble(_jsonObject.getString("distance"))));

                                    addMarkerDatabase(_busNo, _latitude, _longitude, _busType, _busFree, _busDetail, _username, _color, _distance, _amountPerson);
                                }
                                Control.hDialog();

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
        } else {
            Control.alertInternet(this);
            Control.hDialog();
        }
    }

    //  ------------------  User Interface -----------------//


    private void setItemSpinner() {
        spinner.setSelection(++selectTraffic);
        adapter.notifyDataSetChanged();
        selectTraffic--;
    }

    public void stopShared() {
        if (!tempBusType.equals("bts") && !tempBusType.equals("public")) {
            final String[] lists = {"แก้ไขจำนวนผู้โดยสาร", "ยกเลิกการแชร์"};
            new AlertDialog.Builder(BusGps.this).setTitle("การจราจร").setItems(lists, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:

                            Control.sDialog(BusGps.this);

                            final Dialog dialogMini = new Dialog(BusGps.this);
                            dialogMini.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogMini.setContentView(R.layout.activity_dialog_count_persons);

                            ImageView btnClose = (ImageView) dialogMini.findViewById(R.id.btnClose);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogMini.cancel();
                                }
                            });

                            Button sentBtn = (Button) dialogMini.findViewById(R.id.sentBtn);
                            sentBtn.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    dialogMini.cancel();
                                    stopService(new Intent(BusGps.this, MyService.class));

                                    if (tempBusType.equals("van")) {
                                        if (_amountPerson.equals("น้อย")) {
                                            _amountPerson = "ว่าง";
                                        } else {
                                            _amountPerson = "เต็ม";
                                        }
                                    }

                                    sentService(_type, _name, _category, _busFree,
                                            _detailBus, _colorMarker, _amountPerson);

                                    final Handler handler2 = new Handler();
                                    handler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getDatabase();
                                        }
                                    }, 10000);
                                }
                            });

                            final RadioButton empty = (RadioButton) dialogMini.findViewById(R.id.empty);
                            final RadioButton full = (RadioButton) dialogMini.findViewById(R.id.full);

                            final TextView typeTv = (TextView) dialogMini.findViewById(R.id.typeTv);

                            empty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _amountPerson = "น้อย";
                                    full.setChecked(false);
                                    empty.setChecked(true);
                                }
                            });

                            full.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    _amountPerson = "เยอะ";
                                    full.setChecked(true);
                                    empty.setChecked(false);
                                }
                            });

                            if (tempBusType.equals("van")) {
                                empty.setText("ว่าง");
                                full.setText("เต็ม");
                            } else {
                                empty.setText("น้อย");
                                full.setText("เยอะ");
                            }

                            RequestQueue requestQueue = Volley.newRequestQueue(BusGps.this);
                            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET,
                                    "https://www.smilemap.me/android/get.php?main=game&sub="
                                            + username + "&ramdom=" +
                                            Control.randomNumber(), null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray _ja = response.getJSONArray("temp");

                                                for (int i = 0; i < _ja.length(); i++) {
                                                    JSONObject jsonObject = _ja.getJSONObject(i);

                                                    _name = jsonObject.getString("name");
                                                    _type = jsonObject.getString("type");
                                                    _category = jsonObject.getString("category");
                                                    _busFree = jsonObject.getString("bus_free");
                                                    _detailBus = jsonObject.getString("bus_detail");
                                                    _amountPerson = jsonObject.getString("amount_person");
                                                    _colorMarker = jsonObject.getString("color");
                                                }

                                                if (_amountPerson.equals("น้อย") || _amountPerson.equals("ว่าง")) {
                                                    empty.setChecked(true);
                                                } else {
                                                    full.setChecked(true);
                                                }

                                                switch (_type) {
                                                    case "bus":
                                                        typeTv.setText("รถเมล์");
                                                        break;
                                                    case "bts":
                                                        typeTv.setText("บีทีเอส");
                                                        break;
                                                    case "brt":
                                                        typeTv.setText("บีอาที");
                                                        break;
                                                    case "van":
                                                        typeTv.setText("รถตู้");
                                                        break;
                                                    case "public":
                                                        typeTv.setText("สาธารณะ");
                                                        break;
                                                    case "boat":
                                                        typeTv.setText("เรือ");
                                                        break;
                                                }

                                                Control.hDialog();
                                                dialogMini.show();

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

                            break;
                        case 1:
                            detailStopShared();
                            break;
                    }
                    dialog.cancel();
                }
            }).show();
        } else {
            detailStopShared();
        }
    }


    public void detailStopShared() {
        switch (tempBusType) {
            case "bus":
                tempNameType = "รถเมล์";
                break;
            case "bts":
                tempNameType = "บีทีเอส";
                break;
            case "brt":
                tempNameType = "บีอาที";
                break;
            case "van":
                tempNameType = "รถตู้";
                break;
            case "public":
                tempNameType = "สาธารณะ";
                break;
            case "boat":
                tempNameType = "เรือ";
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(BusGps.this);
        builder.setTitle("ท่านกำลังแชร์ตำแหน่ง" + tempNameType + "อยู่");
        builder.setMessage("ยกเลิกการแชร์ตำแหน่ง");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {

                Control.sDialog(BusGps.this);

                // Stop Service && show staus not shared
                Intent i = new Intent(BusGps.this, MyService.class);
                stopService(i);

                final String typeDelete = stausShared.substring(2, stausShared.indexOf("*"));
                final String usernameDelete = username;

                RequestQueue requestQueue = Volley.newRequestQueue(BusGps.this);
                StringRequest jor = new StringRequest(Request.Method.POST, setBusUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Control.hDialog();

                                // save staus shared in file
                                stausShared = "0+";
                                try {
                                    FileOutputStream fOut = openFileOutput(fileStausShared, MODE_PRIVATE);
                                    OutputStreamWriter writer = new OutputStreamWriter(fOut);

                                    writer.write(stausShared);
                                    writer.flush();
                                    writer.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }

                                checkBtnSharedBus = false;
                                btnStopShared.setVisibility(View.INVISIBLE);

                                getDatabase();
                                moveAnimateCamera(currentLocation);

                                dialog.cancel();
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

    public void sentService(final String _typeBus, final String _busNo, final String _category,
                            final String _stausBusFree, final String _detail, final String _colorMarker, final String _amountPerson) {

        if (Control.checkInternet(this)) {

            readFile(fileUserName);
            readFile(fileAlertUsermanual);

            Control.sDialog(this);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest jor = new StringRequest(Request.Method.POST, setBusUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Control.hDialog();
                            Control.alertCurrentInternet(BusGps.this);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "game");
                    params.put("username", username);
                    params.put("type", _typeBus);
                    params.put("name", _busNo);
                    params.put("category", _category);
                    params.put("bus_free", _stausBusFree);
                    params.put("bus_detail", _detail);
                    params.put("color", _colorMarker);
                    params.put("amount_person", _amountPerson);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);

            //------------------------------------------------------------

            final String lat = Double.toString(currentLatitude);
            final String lng = Double.toString(currentLongitude);

            jor = new StringRequest(Request.Method.POST, setBusUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            // save staus shared in file
                            stausShared = "1+" + _typeBus + "*" + String.valueOf(selectTraffic);
                            try {
                                FileOutputStream fOut = openFileOutput(fileStausShared, MODE_PRIVATE);
                                OutputStreamWriter writer = new OutputStreamWriter(fOut);

                                writer.write(stausShared);
                                writer.flush();
                                writer.close();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }

                            if (stausAlertUsermanual.equals("show")) {
                                final Dialog dialog = new Dialog(BusGps.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.activity_dialog_usermanual_bus);
                                dialog.setCancelable(false);

                                final CheckBox checkBoxAlertUsermanual = (CheckBox) dialog.findViewById(R.id.checkBoxAlertUsermanual);

                                Button buttonOk = (Button) dialog.findViewById(R.id.btnOk);
                                buttonOk.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        dialog.cancel();

                                        if (checkBoxAlertUsermanual.isChecked()) {

                                            try {
                                                FileOutputStream fOut = openFileOutput(fileAlertUsermanual, MODE_PRIVATE);
                                                OutputStreamWriter writer = new OutputStreamWriter(fOut);

                                                writer.write("not");
                                                writer.flush();
                                                writer.close();
                                            } catch (IOException ioe) {
                                                ioe.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                dialog.show();
                            }

                            checkBtnSharedBus = true;
                            btnStopShared.setVisibility(View.VISIBLE);

                            Intent i = new Intent(getApplicationContext(), MyService.class);
                            startService(i);

                            final Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Control.hDialog();
                                    setItemSpinner();

                                    selectImage();
                                }
                            }, 5000);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Control.hDialog();
                            Control.alertCurrentInternet(BusGps.this);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "bus");
                    params.put("username", username);
                    params.put("lat", lat);
                    params.put("lng", lng);
                    params.put("name", _busNo);
                    params.put("type", _typeBus);
                    params.put("category", _category);
                    params.put("amount_person", _amountPerson);
                    params.put("bus_free", _stausBusFree);
                    params.put("bus_detail", _detail);
                    params.put("color", _colorMarker);

                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);

        } else {
            Control.alertInternet(this);
        }
    }

    private void sharedBus(final String _type, final String _trafficName, final String _detail, final String _category) {


        if (Control.checkInternet(this)) {

            readFile(fileUserName);

            Control.sDialog(this);

            final String lat = Double.toString(currentLatitude);
            final String lng = Double.toString(currentLongitude);

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest jor = new StringRequest(Request.Method.POST, setBusUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            final Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Control.hDialog();
                                    setItemSpinner();
                                    moveAnimateCamera(currentLocation);

                                    selectImage();
                                }
                            }, 5000);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Control.hDialog();
                            Control.alertCurrentInternet(BusGps.this);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "bus");
                    params.put("username", username);
                    params.put("lat", lat);
                    params.put("lng", lng);
                    params.put("name", _trafficName);
                    params.put("type", _type);
                    params.put("category", _category);
                    params.put("bus_detail", _detail);
                    return params;
                }
            };
            jor.setShouldCache(false);
            requestQueue.add(jor);
        } else {
            Control.alertInternet(this);
        }
    }

    private void sharedBus(final String type, final Dialog tempDialog) {

        String[] list = new String[0];

        String tempType = null;
        int tempSelectType = 0;

        final Dialog dialog = new Dialog(BusGps.this);
        dialog.setTitle(R.string.title_dialog_sharedbus);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_shared_traffic_bts_brt_boat);


        btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        TextView head = (TextView) dialog.findViewById(R.id.head);


        final RadioButton empty = (RadioButton) dialog.findViewById(R.id.empty);
        final RadioButton full = (RadioButton) dialog.findViewById(R.id.full);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full.setChecked(false);
                empty.setChecked(true);
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empty.setChecked(false);
                full.setChecked(true);
            }
        });

        switch (type) {
            case "brt":
                tempType = "brt";
                tempSelectType = 2;
                list = new String[]{"สาทร - ราชพฤกษ์", "ราชพฤกษ์ - สาทร"};
                head.setText("บีอาที");
                break;
            case "bts":
                LinearLayout layoutCheckBox = (LinearLayout) dialog.findViewById(R.id.layoutCheckBox);
                layoutCheckBox.setVisibility(View.GONE);

                tempType = "bts";
                tempSelectType = 1;
                list = new String[]{"หมอชิต - แบริ่ง", "แบริ่ง - หมอชิต",
                        "สนามกีฬาแห่งชาติ - บางหว้า", "บางหว้า - สนามกีฬาแห่งชาติ"};
                head.setText("บีทีเอส");
                break;
            case "boat":
                tempType = "boat";
                tempSelectType = 5;
                list = new String[]{"สะพานตากสิน - สะพานพระราม7", "สะพานพระราม7 - สะพานตากสิน",
                        "ท่าเรือวัดศรีบุญเรือง - ท่าเรือประตูน้ำ", "ท่าเรือประตูน้ำ - ท่าเรือวัดศรีบุญเรือง",
                        "ท่าเรือประตูน้ำ - ผ่านฟ้าลีลาส", "ผ่านฟ้าลีลาส - ท่าเรือประตูน้ำ"};
                head.setText("เรือ");
                break;
        }

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (type) {
                    case "brt":
                        switch (position) {
                            case 0:
                                busNo = "สาทร - ราชพฤกษ์";
                                break;
                            case 1:
                                busNo = "ราชพฤกษ์ - สาทร";
                                break;
                        }
                        break;
                    case "bts":
                        switch (position) {
                            case 0:
                                busNo = "หมอชิต - แบริ่ง";
                                break;
                            case 1:
                                busNo = "แบริ่ง - หมอชิต";
                                break;
                            case 2:
                                busNo = "สนามกีฬาแห่งชาติ - บางหว้า";
                                break;
                            case 3:
                                busNo = "บางหว้า - สนามกีฬาแห่งชาติ";
                                break;
                        }
                        break;
                    case "boat":
                        switch (position) {
                            case 0:
                                busNo = "สะพานตากสิน - บางโพ";
                                break;
                            case 1:
                                busNo = "บางโพ - สะพานตากสิน";
                                break;
                            case 2:
                                busNo = "ท่าเรือวัดศรีบุญเรือง - ท่าเรือประตูน้ำ";
                                break;
                            case 3:
                                busNo = "ท่าเรือประตูน้ำ - ท่าเรือวัดศรีบุญเรือง";
                                break;
                            case 4:
                                busNo = "ท่าเรือประตูน้ำ - ผ่านฟ้าลีลาส";
                                break;
                            case 5:
                                busNo = "ผ่านฟ้าลีลาส - ท่าเรือประตูน้ำ";
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final String finalTempType = tempType;
        final int finalTempSelectType = tempSelectType;

        Button btnShared = (Button) dialog.findViewById(R.id.btnShared);
        btnShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String stausBus;

                if (full.isChecked()) {
                    stausBus = "เยอะ";
                } else {
                    stausBus = "น้อย";
                }

                selectTraffic = finalTempSelectType;
                tempBusType = finalTempType;
                busType = finalTempType;
                sentService(busType, busNo, "", "", "", "", stausBus);
                dialog.cancel();
                tempDialog.cancel();
            }
        });

        dialog.show();
    }

    private void sharedBus() {

        final Dialog dialog = new Dialog(BusGps.this);
        dialog.setTitle(R.string.title_dialog_sharedbus);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_shared_traffic_menu);

        btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final ImageButton bus = (ImageButton) dialog.findViewById(R.id.bus);
        bus.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       if (checkBtnSharedBus == false) {

                                           final Dialog dialogBus = new Dialog(BusGps.this);
                                           dialogBus.setTitle(R.string.title_dialog_sharedbus);
                                           dialogBus.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                           dialogBus.setContentView(R.layout.activity_dialog_shared_traffic_bus);

                                           String[] bus_number = getResources().getStringArray(R.array.bus_number);
                                           ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, bus_number);

                                           autoComplete = (AutoCompleteTextView) dialogBus.findViewById(R.id.autoComplete);
                                           autoComplete.setAdapter(adapter);
                                           autoComplete.setThreshold(1);
                                           autoComplete.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});

                                           checkBoxBusFree = (CheckBox) dialogBus.findViewById(R.id.checkBox);

                                           final RadioButton empty = (RadioButton) dialogBus.findViewById(R.id.empty);
                                           final RadioButton full = (RadioButton) dialogBus.findViewById(R.id.full);

                                           empty.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   full.setChecked(false);
                                                   empty.setChecked(true);
                                               }
                                           });

                                           full.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   empty.setChecked(false);
                                                   full.setChecked(true);
                                               }
                                           });


                                           textAlert = (TextView) dialogBus.findViewById(R.id.textAlert);

                                           btnClose = (ImageView) dialogBus.findViewById(R.id.btnClose);
                                           btnClose.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   dialogBus.cancel();
                                               }
                                           });

                                           Button type1 = (Button) dialogBus.findViewById(R.id.type1);
                                           type1.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   sharedBus("รถปรับอากาศ", dialogBus, dialog, full);
                                               }
                                           });

                                           Button type2 = (Button) dialogBus.findViewById(R.id.type2);
                                           type2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   sharedBus("รถธรรมดา", dialogBus, dialog, full);
                                               }
                                           });
                                           dialogBus.show();
                                       } else {
                                           stopShared();
                                       }
                                   }

                                   void sharedBus(String _type, Dialog dialogBus, Dialog tempDialog, RadioButton full) {
                                       busNo = autoComplete.getText().toString();

                                       int busFree = 0;

                                       if (busNo.length() <= 0) {
                                           textAlert.setVisibility(View.VISIBLE);
                                       } else {

                                           String stausBus;

                                           if (full.isChecked()) {
                                               stausBus = "เยอะ";
                                           } else {
                                               stausBus = "น้อย";
                                           }

                                           textAlert.setText("");
                                           if (checkBoxBusFree.isChecked()) {
                                               busFree = 1;
                                           } else {
                                               busFree = 0;
                                           }
                                           tempBusType = "bus";
                                           busType = "bus";
                                           selectTraffic = 0;
                                           sentService(busType, busNo, _type, String.valueOf(busFree), "", "", stausBus);
                                           dialogBus.cancel();
                                           tempDialog.cancel();
                                       }
                                   }
                               }
        );

        ImageButton bts = (ImageButton) dialog.findViewById(R.id.bts);
        bts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBtnSharedBus == false) {
                    sharedBus("bts", dialog);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton brt = (ImageButton) dialog.findViewById(R.id.brt);
        brt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBtnSharedBus == false) {
                    sharedBus("brt", dialog);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton van = (ImageButton) dialog.findViewById(R.id.van);
        van.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBtnSharedBus == false) {
                    final Dialog dialogMini = new Dialog(BusGps.this);
                    dialogMini.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogMini.setContentView(R.layout.activity_dialog_shared_traffic_van);

                    final ScrollView scrollView = (ScrollView) dialogMini.findViewById(R.id.scrollView);

                    btnClose = (ImageView) dialogMini.findViewById(R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogMini.cancel();
                        }
                    });

                    final EditText name = (EditText) dialogMini.findViewById(R.id.name);
                    final EditText editText = (EditText) dialogMini.findViewById(R.id.editText);

                    final TextView textAlert = (TextView) dialogMini.findViewById(R.id.textAlert);

                    final RadioButton empty = (RadioButton) dialogMini.findViewById(R.id.empty);
                    final RadioButton full = (RadioButton) dialogMini.findViewById(R.id.full);

                    empty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            full.setChecked(false);
                            empty.setChecked(true);
                        }
                    });

                    full.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            empty.setChecked(false);
                            full.setChecked(true);
                        }
                    });

                    Button btnSend = (Button) dialogMini.findViewById(R.id.btnSend);
                    btnSend.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (name.length() > 0) {
                                String stausBus = "";

                                dialog.cancel();
                                dialogMini.cancel();
                                tempBusType = "van";
                                busType = "van";
                                selectTraffic = 3;

                                if (full.isChecked()) {
                                    stausBus = "เต็ม";
                                } else {
                                    stausBus = "ว่าง";
                                }
                                sentService(busType, name.getText().toString(), "", "", editText.getText().toString(), "", stausBus);
                            } else {
                                textAlert.setVisibility(View.VISIBLE);
                                scrollView.fullScroll(View.FOCUS_UP);
                            }
                        }
                    });
                    dialogMini.show();
                } else {
                    stopShared();
                }
            }
        });

        ImageButton boat = (ImageButton) dialog.findViewById(R.id.boat);
        boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBtnSharedBus == false) {
                    sharedBus("boat", dialog);
                } else {
                    stopShared();
                }
            }
        });

        final ImageButton accident = (ImageButton) dialog.findViewById(R.id.accident);
        accident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogMini = new Dialog(BusGps.this);
                dialogMini.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogMini.setContentView(R.layout.activity_dialog_shared_traffic_accident);

                checkAccidentOrCheakpoint = false;

                final ScrollView scrollView = (ScrollView) dialogMini.findViewById(R.id.scrollView);

                btnClose = (ImageView) dialogMini.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogMini.cancel();
                    }
                });

                final EditText editText = (EditText) dialogMini.findViewById(R.id.editText);

                final TextView textAlert = (TextView) dialogMini.findViewById(R.id.textAlert);

                final LinearLayout accident1 = (LinearLayout) dialogMini.findViewById(R.id.accident1);
                accident1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(1, dialogMini);
                        stausCheckAccidentOrCheakpointt = "รถชนเล็กน้อย";
                    }
                });

                final LinearLayout accident2 = (LinearLayout) dialogMini.findViewById(R.id.accident2);
                accident2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(2, dialogMini);
                        stausCheckAccidentOrCheakpointt = "รถชนสาหัส";
                    }
                });

                final LinearLayout accident3 = (LinearLayout) dialogMini.findViewById(R.id.accident3);
                accident3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(3, dialogMini);
                        stausCheckAccidentOrCheakpointt = "รถชนคน";
                    }
                });

                final LinearLayout accident4 = (LinearLayout) dialogMini.findViewById(R.id.accident4);
                accident4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(4, dialogMini);
                        stausCheckAccidentOrCheakpointt = "รถเสีย";
                    }
                });

                final LinearLayout accident5 = (LinearLayout) dialogMini.findViewById(R.id.accident5);
                accident5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(5, dialogMini);
                        stausCheckAccidentOrCheakpointt = "มอเตอร์ไซค์ล้ม";
                    }
                });

                final LinearLayout accident6 = (LinearLayout) dialogMini.findViewById(R.id.accident6);
                accident6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(6, dialogMini);
                        stausCheckAccidentOrCheakpointt = "อื่นๆ";
                    }
                });

                Button btnSend = (Button) dialogMini.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (checkAccidentOrCheakpoint == true) {
                            dialog.cancel();
                            dialogMini.cancel();
                            busType = "accident";
                            selectTraffic = 6;
                            sharedBus(busType, "อุบัติเหตุ", editText.getText().toString(), stausCheckAccidentOrCheakpointt);
                        } else {
                            textAlert.setTextColor(Color.parseColor("#f25d5d"));
                            textAlert.setText("กรุณาเลือกประเภทอุบัติเหตุ");
                            scrollView.fullScroll(View.FOCUS_UP);
                        }
                    }
                });
                dialogMini.show();
            }

            private void setTypeaccident(int i, Dialog dialog) {
                checkAccidentOrCheakpoint = true;

                final ImageView accidentPic1 = (ImageView) dialog.findViewById(R.id.accidentPic1);
                final ImageView accidentPic2 = (ImageView) dialog.findViewById(R.id.accidentPic2);
                final ImageView accidentPic3 = (ImageView) dialog.findViewById(R.id.accidentPic3);
                final ImageView accidentPic4 = (ImageView) dialog.findViewById(R.id.accidentPic4);
                final ImageView accidentPic5 = (ImageView) dialog.findViewById(R.id.accidentPic5);
                final ImageView accidentPic6 = (ImageView) dialog.findViewById(R.id.accidentPic6);

                accidentPic1.setImageResource(R.drawable.accident1_gray);
                accidentPic2.setImageResource(R.drawable.accident2_gray);
                accidentPic3.setImageResource(R.drawable.accident3_gray);
                accidentPic4.setImageResource(R.drawable.accident4_gray);
                accidentPic5.setImageResource(R.drawable.accident5_gray);
                accidentPic6.setImageResource(R.drawable.accident6_gray);

                final TextView accidentTxt1 = (TextView) dialog.findViewById(R.id.accidentTxt1);
                final TextView accidentTxt2 = (TextView) dialog.findViewById(R.id.accidentTxt2);
                final TextView accidentTxt3 = (TextView) dialog.findViewById(R.id.accidentTxt3);
                final TextView accidentTxt4 = (TextView) dialog.findViewById(R.id.accidentTxt4);
                final TextView accidentTxt5 = (TextView) dialog.findViewById(R.id.accidentTxt5);
                final TextView accidentTxt6 = (TextView) dialog.findViewById(R.id.accidentTxt6);

                accidentTxt1.setTextColor(Color.parseColor("#948e94"));
                accidentTxt2.setTextColor(Color.parseColor("#948e94"));
                accidentTxt3.setTextColor(Color.parseColor("#948e94"));
                accidentTxt4.setTextColor(Color.parseColor("#948e94"));
                accidentTxt5.setTextColor(Color.parseColor("#948e94"));
                accidentTxt6.setTextColor(Color.parseColor("#948e94"));

                switch (i) {
                    case 1:
                        accidentPic1.setImageResource(R.drawable.accident1_blue);
                        accidentTxt1.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 2:
                        accidentPic2.setImageResource(R.drawable.accident2_blue);
                        accidentTxt2.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 3:
                        accidentPic3.setImageResource(R.drawable.accident3_blue);
                        accidentTxt3.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 4:
                        accidentPic4.setImageResource(R.drawable.accident4_blue);
                        accidentTxt4.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 5:
                        accidentPic5.setImageResource(R.drawable.accident5_blue);
                        accidentTxt5.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 6:
                        accidentPic6.setImageResource(R.drawable.accident6_blue);
                        accidentTxt6.setTextColor(Color.parseColor("#006df0"));
                        break;
                }
            }
        });

        ImageButton checkpoint = (ImageButton) dialog.findViewById(R.id.checkpoint);
        checkpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialogMini = new Dialog(BusGps.this);
                dialogMini.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogMini.setContentView(R.layout.activity_dialog_shared_traffic_checkpoint);

                checkAccidentOrCheakpoint = false;

                final ScrollView scrollView = (ScrollView) dialogMini.findViewById(R.id.scrollView);

                btnClose = (ImageView) dialogMini.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogMini.cancel();
                    }
                });

                final EditText editText = (EditText) dialogMini.findViewById(R.id.editText);

                final TextView textAlert = (TextView) dialogMini.findViewById(R.id.textAlert);

                final LinearLayout checkPoint1 = (LinearLayout) dialogMini.findViewById(R.id.checkPoint1);
                checkPoint1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(1, dialogMini);
                        stausCheckAccidentOrCheakpointt = "ทำถนน";
                    }
                });

                final LinearLayout checkPoint2 = (LinearLayout) dialogMini.findViewById(R.id.checkPoint2);
                checkPoint2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(2, dialogMini);
                        stausCheckAccidentOrCheakpointt = "ก่อสร้างขนาดใหญ่";
                    }
                });

                final LinearLayout checkPoint3 = (LinearLayout) dialogMini.findViewById(R.id.checkPoint3);
                checkPoint3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setTypeaccident(3, dialogMini);
                        stausCheckAccidentOrCheakpointt = "อื่นๆ";
                    }
                });

                Button btnSend = (Button) dialogMini.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (checkAccidentOrCheakpoint == true) {
                            dialog.cancel();
                            dialogMini.cancel();
                            busType = "checkpoint";
                            selectTraffic = 7;
                            sharedBus(busType, "ก่อสร้าง", editText.getText().toString(), stausCheckAccidentOrCheakpointt);
                        } else {
                            textAlert.setTextColor(Color.parseColor("#f25d5d"));
                            textAlert.setText("กรุณาเลือกประเภทการก่อสร้าง");
                            scrollView.fullScroll(View.FOCUS_UP);
                        }
                    }
                });
                dialogMini.show();
            }

            private void setTypeaccident(int i, Dialog dialog) {
                checkAccidentOrCheakpoint = true;

                final ImageView checkPointPic1 = (ImageView) dialog.findViewById(R.id.checkPointPic1);
                final ImageView checkPointPic2 = (ImageView) dialog.findViewById(R.id.checkPointPic2);
                final ImageView checkPointPic3 = (ImageView) dialog.findViewById(R.id.checkPointPic3);

                checkPointPic1.setImageResource(R.drawable.checkpoint1_gray);
                checkPointPic2.setImageResource(R.drawable.checkpoint2_gray);
                checkPointPic3.setImageResource(R.drawable.accident6_gray);

                final TextView checkPointTxt1 = (TextView) dialog.findViewById(R.id.checkPointTxt1);
                final TextView checkPointTxt2 = (TextView) dialog.findViewById(R.id.checkPointTxt2);
                final TextView checkPointTxt3 = (TextView) dialog.findViewById(R.id.checkPointTxt3);

                checkPointTxt1.setTextColor(Color.parseColor("#948e94"));
                checkPointTxt2.setTextColor(Color.parseColor("#948e94"));
                checkPointTxt3.setTextColor(Color.parseColor("#948e94"));

                switch (i) {
                    case 1:
                        checkPointPic1.setImageResource(R.drawable.checkpoint1_blue);
                        checkPointTxt1.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 2:
                        checkPointPic2.setImageResource(R.drawable.checkpoint2_blue);
                        checkPointTxt2.setTextColor(Color.parseColor("#006df0"));
                        break;
                    case 3:
                        checkPointPic3.setImageResource(R.drawable.accident6_blue);
                        checkPointTxt3.setTextColor(Color.parseColor("#006df0"));
                        break;
                }
            }
        });

        ImageButton publicway = (ImageButton) dialog.findViewById(R.id.publicway);
        publicway.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBtnSharedBus == false) {

                    final Dialog dialogMini = new Dialog(BusGps.this);
                    dialogMini.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogMini.setContentView(R.layout.activity_dialog_shared_traffic_public);

                    btnClose = (ImageView) dialogMini.findViewById(R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogMini.cancel();
                        }
                    });

                    name = (EditText) dialogMini.findViewById(R.id.name);
                    detail = (EditText) dialogMini.findViewById(R.id.detail);
                    final TextView alert = (TextView) dialogMini.findViewById(R.id.alert);

                    de.hdodenhof.circleimageview.CircleImageView color1 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color1);
                    color1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#2196F3", alert, dialogMini, dialog);   //bluebold
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color2 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color2);
                    color2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#E91E63", alert, dialogMini, dialog);    // pink
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color3 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color3);
                    color3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#F4511E", alert, dialogMini, dialog);     //orange
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color4 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color4);
                    color4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#00ACC1", alert, dialogMini, dialog);      // cyan
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color5 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color5);
                    color5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#df3e3e", alert, dialogMini, dialog);       // red
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color6 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color6);
                    color6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#9C27B0", alert, dialogMini, dialog);       // pupple
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color7 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color7);
                    color7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#77cc1c", alert, dialogMini, dialog);       // green
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color8 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color8);
                    color8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#FFB300", alert, dialogMini, dialog);       // gold
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color9 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color9);
                    color9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#43759c", alert, dialogMini, dialog);       // bluebold
                        }
                    });

                    de.hdodenhof.circleimageview.CircleImageView color10 = (de.hdodenhof.circleimageview.CircleImageView) dialogMini.findViewById(R.id.color10);
                    color10.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setup("#717171", alert, dialogMini, dialog);       // gray
                        }
                    });

                    dialogMini.show();
                } else {
                    stopShared();
                }
            }

            void setup(String color, TextView alert, Dialog dialog, Dialog tempDialog) {

                if (name.getText().toString().length() > 0) {
                    tempBusType = "public";
                    busType = "public";
                    selectTraffic = 4;
                    sentService(busType, name.getText().toString(), "", "", detail.getText().toString(), color, "");
                    dialog.cancel();
                    tempDialog.cancel();
                } else {
                    alert.setText("โปรดระบุชื่อตำแหน่ง");
                    alert.setTextColor(Color.parseColor("#f25d5d"));
                }
            }
        });

        dialog.show();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null) {
            final Dialog dialog = new Dialog(BusGps.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_dialog_login_facebook);

            btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            LoginButton loginFacebookBtn = (LoginButton) dialog.findViewById(R.id.loginFacebookBtn);
            loginFacebookBtn.setReadPermissions("email,public_profile");//,user_friends");
            loginFacebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult login_result) {
                    GraphRequest data_request = GraphRequest.newMeRequest(
                            login_result.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject json_object,
                                        GraphResponse response) {

                                    dialog.cancel();
                                    dialogSharedFacebook();
                                }
                            });
                    Bundle permission_param = new Bundle();
                    permission_param.putString("fields", "id,name,email,gender"); //,picture.width(150).height(150)");
                    data_request.setParameters(permission_param);
                    data_request.executeAsync();
                }

                @Override
                public void onCancel() {
                    dialog.cancel();
                }

                @Override
                public void onError(FacebookException exception) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            dialogSharedFacebook();
        }
    }

    private void dialogSharedFacebook() {

        final String[] items = {"แชร์โพสต์", "ยกเลิก"}; // , "ถ่ายรูปแชร์", "เลือกจากคลังภาพ",

        AlertDialog.Builder builder = new AlertDialog.Builder(BusGps.this);
        builder.setTitle("แชร์จิตสาธารณะบนเฟซบุ๊ก");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (items[item]) {
                    case "แชร์โพสต์":

                       /* String _tempName = null;

                        switch (type) {
                            case "restroom":
                                _tempName = "ห้องน้ำ";
                                break;
                            case "pharmacy":
                                _tempName = "ร้านขายยา";
                                break;
                            case "veterinary_clinic":
                                _tempName = "รักษาสัตว์";
                                break;
                            case "officer":
                                _tempName = "จุดพักผ่อน";
                                break;
                            case "daily_home":
                                _tempName = "ที่พักรายวัน";
                                break;
                            case "garage":
                                _tempName = "ร้านปะยาง";
                                break;
                        }
*/
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Smile Map แอพจิตสาธารณะ")
                                /*    //.setImageUrl(Uri.parse("https://www.xxx11/114.png"))
                                    .setContentDescription(
                                            "แชร์ตำแหน่ง" + _tempName + " เรียบร้อยแล้ว")*/
                                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.blackcatwalk.sharingpower"))
                                    .build();

                            shareDialog.show(linkContent);
                        }
                        break;
                    case "ยกเลิก":
                        dialog.dismiss();
                        break;
                }

            }
        });
        builder.show();
    }

}
