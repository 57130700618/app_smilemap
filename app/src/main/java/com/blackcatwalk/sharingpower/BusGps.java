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

import com.blackcatwalk.sharingpower.customAdapter.BusGpsCustomSpinnerAdapter;
import com.blackcatwalk.sharingpower.customAdapter.CustomSpinnerBusLocattionNerbyMenu;
import com.blackcatwalk.sharingpower.google.JsonParserDirections;
import com.blackcatwalk.sharingpower.utility.Control;
import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlFile;
import com.blackcatwalk.sharingpower.utility.ControlProgress;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BusGps extends AppCompatActivity implements LocationListener {

    private String mBusNo = "";
    private String mBusType = "bus";
    private String mTempUrl;

    private String mStausAlertUsermanual = "show";
    private String stausShared = "0+";  // 1 = showStausShared
    private String mStausTaffic = "1"; // 1 = showTraffic
    private String mResultNearby = "55"; // Nearby 50 km.
    private String mMaptype = "0"; // 0 = normal , 1 = map_satelltte

    private BusGpsCustomSpinnerAdapter mAdapter;

    private boolean mCheckAccidentOrCheakpoint;
    private String mStausCheckAccidentOrCheakpointt;

    private double mSizeInInches;

    private CallbackManager mCallbackManager;
    private ShareDialog mDialogFacebook;

    private ControlDatabase mControlDatabae;
    private ControlCheckConnect mControlCheckConnect;
    private ControlFile mControlFile;

    private StringBuilder mStringBuilder = new StringBuilder();

    // ----------------- Google_Map -------------------//
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mCurrentLatitude = 0;
    private double mCurrentLongitude = 0;
    private final long mUpdatareInterval = 120000;  // 1minute = 60000 milesecond
    private final long mFastestInterval = 90000;
    private String mDistance;
    private String mDuration;
    private MarkerOptions mMarker;
    private Marker mClickMaker;
    private float mCurrentZoom = 30;

    // --------------- User Interface -----------------//
    private Spinner mSpinner;
    private TextView mLabalName;
    private ImageView mUserManualIm;
    private ImageView mRefreshIm;
    private ImageView mSettingIm;
    private CircleImageView mSharedBusIm;
    private CircleImageView mStopSharedIm;
    private ImageView mCureentLocationIm;
    private SupportMapFragment mMapFragment;

    private boolean mCheckBtnSharedBus = false;    // true = show button navigator
    private int mSelectTraffic = 0;     // 0=bus, 1=brt, 2=bts, 3=boat, 4=checkpoint, 5=accident, 6=public

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide();

        mSizeInInches = new Control().getSizeScrren(this);
        mControlDatabae = new ControlDatabase(this);
        mControlCheckConnect = new ControlCheckConnect();
        mControlFile = new ControlFile();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mDialogFacebook = new ShareDialog(this);
        AppEventsLogger.activateApp(this);

        bindWidget();
        setUpMap();
        setUpEventWigget();
        setSpinnerMenu();
        readFileUserManualAndStatusShared();
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
        ControlProgress.hideDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            readFileSetting();
        }

        if (mGoogleApiClient.isConnected()) {
            mTempUrl = null;
            mTempUrl = mBusType + "&lat=" + mCurrentLatitude + "&lng=" + mCurrentLongitude + "&mDistance=" + mResultNearby;
            startLocationUpdates();
        }

        if (!mControlCheckConnect.checkInternet(this)) {
            mControlCheckConnect.alertInternet(this);
        }

        if (!mControlCheckConnect.checkGPS(this)) {
            mControlCheckConnect.alertGps(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdate();
        }
        ControlProgress.hideDialog();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //--------------------- File System --------------------------

    private void readFileUserManualAndStatusShared() {

        if (mControlFile.getFile(this, "userManual").equals("not")) {
            mStausAlertUsermanual = "not";
        }

        String _temp = mControlFile.getFile(this, "stausShared");

        if (!_temp.equals("not found")) {
            stausShared = _temp;
            if (stausShared.substring(0, 1).equals("1")) {    // Set staus shared
                mCheckBtnSharedBus = true;
                mStopSharedIm.setVisibility(View.VISIBLE);
                mBusType = stausShared.substring(2, stausShared.indexOf("*"));
                mSelectTraffic = Integer.parseInt(stausShared.substring(stausShared.indexOf("*") + 1, stausShared.length()));
            }
        }
    }

    private void readFileSetting() {

        String _temp = mControlFile.getFile(this, "setting");

        if (!_temp.equals("not found")) {
            mStausTaffic = _temp.substring(0, 1);
            mMaptype = _temp.substring(1, 2);
            mResultNearby = _temp.substring(2, _temp.length());

            if (mStausTaffic.equals("1")) {
                mMap.setTrafficEnabled(true);
            } else {
                mMap.setTrafficEnabled(false);
            }

            if (mMaptype.equals("0")) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        }
    }

    private void saveFileSetting() {
        mControlFile.setSetting(this, mStausTaffic, mMaptype, mResultNearby);
    }

    //------------------- Dialog Setting -------------------------

    private void dialogSetting() {
        if (mMap != null) {
            readFileSetting();

            final Dialog _dialogSetting = new Dialog(BusGps.this);
            _dialogSetting.requestWindowFeature(Window.FEATURE_NO_TITLE);
            _dialogSetting.setContentView(R.layout.activity_dialog_setting);

            ImageView _btnClose = (ImageView) _dialogSetting.findViewById(R.id.btnClose);
            _btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _dialogSetting.cancel();
                }
            });

            Switch _showTraffic = (Switch) _dialogSetting.findViewById(R.id.showTraffic);
            _showTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mStausTaffic = "1";
                        mMap.setTrafficEnabled(true);

                    } else {
                        mStausTaffic = "0";
                        mMap.setTrafficEnabled(false);
                    }
                    saveFileSetting();
                }
            });

            if (mStausTaffic.equals("0")) {
                _showTraffic.setChecked(false);
            }

            final TextView _resultNearbyTxt = (TextView) _dialogSetting.findViewById(R.id.resultNearbyTxt);

            SeekBar _seekBar = (SeekBar) _dialogSetting.findViewById(R.id.seekBar);
            _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {

                    switch (progresValue) {
                        case 0:
                            mResultNearby = "3";
                            break;
                        case 1:
                            mResultNearby = "6";
                            break;
                        case 2:
                            mResultNearby = "12";
                            break;
                        case 3:
                            mResultNearby = "23";
                            break;
                        case 4:
                            mResultNearby = "32";
                            break;
                        case 5:
                            mResultNearby = "55";
                            break;
                    }
                    saveFileSetting();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    showTxtkm(seekBar, _resultNearbyTxt);
                }
            });

            showTxtkm(_seekBar, _resultNearbyTxt);

            final RadioButton _satelltte = (RadioButton) _dialogSetting.findViewById(R.id.satelltte);
            final RadioButton _normal = (RadioButton) _dialogSetting.findViewById(R.id.normal);

            _normal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaptype = "0";
                    saveFileSetting();
                    _satelltte.setChecked(false);
                    _normal.setChecked(true);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            });

            _satelltte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaptype = "1";
                    saveFileSetting();
                    _satelltte.setChecked(true);
                    _normal.setChecked(false);
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            });

            if (mMaptype.equals("0")) {
                _normal.setChecked(true);
            } else {
                _satelltte.setChecked(true);
            }

            _dialogSetting.show();
        }
    }

    void showTxtkm(SeekBar seekBar, TextView resultNearbyTxt) {
        String mTempResultNearby = null;
        switch (mResultNearby) {
            case "3":
                seekBar.setProgress(0);
                mTempResultNearby = "2";
                break;
            case "6":
                seekBar.setProgress(1);
                mTempResultNearby = "5";
                break;
            case "12":
                seekBar.setProgress(2);
                mTempResultNearby = "10";
                break;
            case "23":
                seekBar.setProgress(3);
                mTempResultNearby = "20";
                break;
            case "32":
                seekBar.setProgress(4);
                mTempResultNearby = "30";
                break;
            case "55":
                seekBar.setProgress(5);
                mTempResultNearby = "50";
                break;
        }
        resultNearbyTxt.setText("(" + mTempResultNearby + " km)");
    }

    //--------------------   setUp    ----------------------------

    private void setUpEventWigget() {
        mLabalName.setText("การจราจร");

        mRefreshIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLatitude != 0 && mCurrentLongitude != 0) {
                    getDatabase();
                }else {
                    mControlCheckConnect.alertCurrentGps(BusGps.this);
                }
            }
        });

        mUserManualIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusGps.this, TutorialTraffic.class));
            }


        });

        mSettingIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting();
            }
        });

        mSharedBusIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLatitude != 0) {
                    dialogSharedMain();
                } else {
                    mControlCheckConnect.alertCurrentGps(BusGps.this);
                }
            }
        });

        mStopSharedIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopShared();
            }
        });

        mCureentLocationIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLatitude != 0) {
                    moveAnimateCamera();
                } else {
                    mControlCheckConnect.alertCurrentGps(BusGps.this);
                }
            }
        });
    }

    private void setSpinnerMenu() {

        CustomSpinnerBusLocattionNerbyMenu _bus_data[] = new CustomSpinnerBusLocattionNerbyMenu[]{
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.spinner_search_gray, getString(R.string.select_traffic)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_bus_gray, getString(R.string.bus_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_bts_gray, getString(R.string.bts_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_brt_gray, getString(R.string.brt_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_van_gray, getString(R.string.van_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_public_gray, getString(R.string.public_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_boat_gray, getString(R.string.boat_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_accident_gray, getString(R.string.accident_name)),
                new CustomSpinnerBusLocattionNerbyMenu(R.drawable.traffic_spinner_checkpoint_gray, getString(R.string.checkpoint_name))};

        mAdapter = new BusGpsCustomSpinnerAdapter(this, R.layout.activity_spinner, _bus_data);

        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int position, long id) {

                if (position == 0) {
                    return;
                }

                if (mCurrentLatitude != 0 && mCurrentLongitude != 0) {

                    mTempUrl = null;
                    mSelectTraffic = position - 1;

                    switch (position) {
                        case 1:
                            mBusType = "bus";
                            break;
                        case 2:
                            mBusType = "bts";
                            break;
                        case 3:
                            mBusType = "brt";
                            break;
                        case 4:
                            mBusType = "van";
                            break;
                        case 5:
                            mBusType = "public";
                            break;
                        case 6:
                            mBusType = "boat";
                            break;
                        case 7:
                            mBusType = "accident";
                            break;
                        case 8:
                            mBusType = "checkpoint";
                            break;
                    }

                    getDatabase();

                    if (mCheckBtnSharedBus == false) {
                        addMarkerCurrent();
                    } else {
                        moveAnimateCamera();
                    }
                } else {
                    mControlCheckConnect.alertCurrentGps(BusGps.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> av) {
                return;
            }
        });
    }

    private void setUpMap() {
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(14.76553, 100.53904), 8));
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.setTrafficEnabled(true);

                mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {

                        if (!marker.getTitle().equals("ตำแหน่งปัจจุบัน")) {
                            mClickMaker = marker;

                            ControlProgress.showProgressDialogDonTouch(BusGps.this);
                            new DownloadTask().execute(getDirectionsUrl(marker.getPosition()));

                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(marker.getPosition()).zoom(mCurrentZoom).tilt(30).build()));
                        }
                        return true;
                    }
                });

                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition pos) {
                        if (pos.zoom != mCurrentZoom) {
                            mCurrentZoom = pos.zoom;
                        }
                    }
                });

                mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        startActivity(new Intent(getApplicationContext(), TrafficDetail.class)
                                .putExtra("lat", marker.getPosition().latitude)
                                .putExtra("lng", marker.getPosition().longitude)
                                .putExtra("type", mSelectTraffic)
                                .putExtra("duration", mDuration)
                                .putExtra("distance", mDistance)
                                .putExtra("name", marker.getTitle().substring(0, marker.getTitle().length() - 1))
                                .putExtra("detail", marker.getSnippet()));
                    }
                });

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return setUpInfoWindow();
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });

                readFileSetting();
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
                        mControlCheckConnect.alertCurrentGps(BusGps.this);
                    }
                }).build();

    }

    private void getDatabase() {
        if (mMarker != null) {
            mMap.clear();
        }

        addMarkerCurrent();

        mTempUrl = null;
        mTempUrl = mBusType + "&lat=" + mCurrentLatitude + "&lng=" + mCurrentLongitude + "&distance=" + mResultNearby;

        mControlDatabae.getDatabaseBusGps(mTempUrl, mSelectTraffic);
    }

    //  ------------------  google map ---------------------------

    @SuppressWarnings({"MissingPermission"})
    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(mUpdatareInterval);
        mLocationRequest.setFastestInterval(mFastestInterval);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onLocationChanged(Location _location)  {
        mCurrentLatitude = _location.getLatitude();
        mCurrentLongitude = _location.getLongitude();
        getDatabase();
    }

    public void addMarkerDatabase(String _busNo, double _latitude, double _longitude, String _busTypeTemp,
                                  int _busFree, String _busDetail, String _tempUsername, String _color,
                                  double _distanceTemp, String _amountPerSon) {

        LatLng _latLng = new LatLng(_latitude, _longitude);
        mMarker = new MarkerOptions();

        IconGenerator _factory = new IconGenerator(this);
        Bitmap _icon;
        String _tempDistance = _distanceTemp + "$";

        // 0=bus, 1=bts, 2=brt, 3=van , 4=public, 5=boat, 6=accident, 7=checkpoint,
        switch (mSelectTraffic) {
            case 0:
                if (_busFree == 1) {
                    _factory.setColor(Color.parseColor("#FFFFB405"));    // color: gold
                    _busTypeTemp = _busTypeTemp + "(ฟรี)";
                } else if (_busTypeTemp.equals("รถธรรมดา")) {
                    _factory.setColor(Color.parseColor("#dc4545"));   // color: red
                } else {
                    _factory.setColor(Color.parseColor("#2196F3"));   // color: blue
                }
                _tempDistance = _tempDistance + _busTypeTemp + "%$&*-" + _amountPerSon;
                break;
            case 1:
                _factory.setColor(Color.parseColor("#2196F3"));   // color: blue
                _tempDistance = _tempDistance + "%$&*-" + _amountPerSon;
                break;
            case 2:
                _factory.setColor(Color.parseColor("#77cc1c"));  // color: green
                _tempDistance = _tempDistance + "%$&*-" + _amountPerSon;
                break;
            case 3:
                _factory.setColor(Color.parseColor("#FF21CCB5")); // color: bluegreen
                _tempDistance = _tempDistance + _busDetail + "%$&*-" + _amountPerSon;
                break;
            case 4:
                _factory.setColor(Color.parseColor(_color));    // custom color
                _tempDistance = _tempDistance + _busDetail;
                break;
            case 5:
                _factory.setColor(Color.parseColor("#1c68a4"));   // color: blueBold
                _tempDistance = _tempDistance + "%$&*-" + _amountPerSon;
                break;
            case 6:
                _factory.setColor(Color.parseColor("#d44444"));   // color: red
                _tempDistance = _tempDistance + _busDetail + "%$&*-" + _busTypeTemp;
                break;
            case 7:
                _factory.setColor(Color.parseColor("#EF6C00"));   // color: brow
                _tempDistance = _tempDistance + _busDetail + "%$&*-" + _busTypeTemp;
                break;
        }

        mMarker.snippet(_tempDistance);

        if (mSizeInInches >= 9.5) {
            _factory.setTextAppearance(R.style.iconGenTextTablet);
        } else if (mSizeInInches >= 6.5) {
            _factory.setTextAppearance(R.style.iconGenTextTablet);
        } else {
            _factory.setTextAppearance(R.style.iconGenText);
        }

        _icon = _factory.makeIcon(_busNo);

        mMarker.position(_latLng).title(_busNo + "$" + _tempUsername).icon(BitmapDescriptorFactory.fromBitmap(_icon)); //.fromResource(R.drawable.bus)

        mMap.addMarker(mMarker);
    }

    private void addMarkerCurrent() {

        mMarker = new MarkerOptions();
        mMarker.position(new LatLng(mCurrentLatitude, mCurrentLongitude)).title(getString(R.string.current_location));

        if (mCheckBtnSharedBus == false) {
            mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
        } else {
            mMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hide));
        }
        mMap.addMarker(mMarker);
        moveAnimateCamera();
    }

    private void moveAnimateCamera() {
        CameraPosition _cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mCurrentLatitude,mCurrentLongitude)).zoom(14).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(_cameraPosition));
    }

    private View setUpInfoWindow() {

        LayoutInflater _inflater = getLayoutInflater();
        View _infoWindow;

        if (mMarker.getTitle().substring(mMarker.getTitle().indexOf("$") + 1, mMarker.getTitle().length()).equals("ตำแหน่ง")) {

            _infoWindow = _inflater.inflate(R.layout.info_window, null);

            TextView _window = (TextView) _infoWindow.findViewById(R.id.window);
            _window.setText("ตำแหน่งที่ฉันแชร์");

            return _infoWindow;
        } else {

            _infoWindow = _inflater.inflate(R.layout.activity_info_window_traffic, null);

            TextView _category = (TextView) _infoWindow.findViewById(R.id.category);
            TextView _detail = (TextView) _infoWindow.findViewById(R.id.detail);

            String _tempType = null;
            String _detailTraffic = null;

            switch (mBusType) {
                case "bus":
                    _tempType = "รถเมล์" + mMarker.getSnippet().substring(mMarker.getSnippet().indexOf("$") + 3,
                            mMarker.getSnippet().indexOf("%$&*-", 0));
                    _detailTraffic = "จำนวนผู้โดยสาร: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length());
                    break;
                case "bts":
                    _tempType = "รถไฟฟ้าบีทีเอส";
                    _detail.setVisibility(View.GONE);
                    break;
                case "brt":
                    _tempType = "รถโดยสายบีอาที";
                    _detailTraffic = "จำนวนผู้โดยสาร: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length());
                    break;
                case "van":
                    _tempType = "รถตู้";
                    _detailTraffic = "จำนวนที่นั่ง: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length());
                    break;
                case "public":
                    _tempType = "ตำแหน่งสาธารณะ";
                    _detail.setVisibility(View.GONE);
                    break;
                case "boat":
                    _tempType = "เรือโดยสาร";
                    _detailTraffic = "จำนวนผู้โดยสาร: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length());
                    break;
                case "accident":
                    _tempType = "อุบัติเหตุ";
                    _detailTraffic = "ปักหมุดเวลา: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length());
                    break;
                case "checkpoint":
                    _tempType = "ก่อสร้าง";
                    _detailTraffic = "ปักหมุดวันที่: " + mMarker.getSnippet().substring
                            (mMarker.getSnippet().indexOf("%$&*-", 0) + 5, mMarker.getSnippet().length() - 6) + "...";
                    break;
            }

            _category.setText("ประเภท: " + _tempType);
            _detail.setText(_detailTraffic);

            setUpDistance();

            TextView _distanceTxt = (TextView) _infoWindow.findViewById(R.id.distanceTxt);
            _distanceTxt.setText(mDistance);

            TextView _durationTxt = (TextView) _infoWindow.findViewById(R.id.durationTxt);
            _durationTxt.setText(mDuration);

            return _infoWindow;
        }
    }

    private void setUpDistance() {
        String _distanceNumText = mMarker.getSnippet().substring(0, mMarker.getSnippet().indexOf("$"));
        Double _distanceNum = Double.parseDouble(_distanceNumText);

        if (_distanceNum <= 1.0) {
            switch (_distanceNumText) {
                case "0.0":
                    mDistance = "10 เมตร";
                    break;
                case "0.1":
                    mDistance = "100 เมตร";
                    break;
                case "0.2":
                    mDistance = "200 เมตร";
                    break;
                case "0.3":
                    mDistance = "300 เมตร";
                    break;
                case "0.4":
                    mDistance = "400 เมตร";
                    break;
                case "0.5":
                    mDistance = "500 เมตร";
                    break;
                case "0.6":
                    mDistance = "600 เมตร";
                    break;
                case "0.7":
                    mDistance = "700 เมตร";
                    break;
                case "0.8":
                    mDistance = "800 เมตร";
                    break;
                case "0.9":
                    mDistance = "900 เมตร";
                    break;
                case "1.0":
                    mDistance = "1 กม.";
                    break;
            }
        } else {
            mDistance = _distanceNumText + " กม.";
        }
    }

    // ****************************************************************************************************
    // ***************************         Shared Control          ****************************************
    // ****************************************************************************************************

    public void sharedBusGame(final String _typeBus, final String _busNo, final String _category,
                              final String _stausBusFree, final String _detail,
                              final String _colorMarker, final String _amountPerson) {

        mControlDatabae.setDatabaseSharedTableGame(Double.toString(mCurrentLatitude), Double.toString(mCurrentLongitude)
                , _typeBus, _busNo, _category, _stausBusFree, _detail, _colorMarker, _amountPerson);
    }

    private void sharedAccidentCheckpoint(String _busType, String _trafficName, String _detail, String _category) {
        mControlDatabae.setDatabaseSharedAccidentCheckpoint(Double.toString(mCurrentLatitude), Double.toString(mCurrentLongitude)
                , _busType, _trafficName, _detail, _category);
    }

    public void dialogUpdateCountPerons(final String _name, final String _type, final String _category, final String _busFree,
                                        final String _busDetail, final String _amountPerson, final String _color) {

        final Dialog _dialogUpdateCountPerons = new Dialog(BusGps.this);
        _dialogUpdateCountPerons.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogUpdateCountPerons.setContentView(R.layout.activity_dialog_count_persons);

        ImageView _btnClose = (ImageView) _dialogUpdateCountPerons.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _dialogUpdateCountPerons.cancel();
            }
        });

        Button _sentBtn = (Button) _dialogUpdateCountPerons.findViewById(R.id.sentBtn);
        _sentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopService(new Intent(BusGps.this, MyService.class));

                if (mBusType.equals("van")) {
                    if (mStringBuilder.toString().equals("น้อย")) {
                        mStringBuilder.setLength(0);
                        mStringBuilder.append("ว่าง");
                    } else {
                        mStringBuilder.setLength(0);
                        mStringBuilder.append("เต็ม");
                    }
                }

                sharedBusGame(_type, _name, _category, _busFree,
                        _busDetail, _color, mStringBuilder.toString());

                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDatabase();
                    }
                }, 10000);

                _dialogUpdateCountPerons.cancel();
            }
        });

        final RadioButton _empty = (RadioButton) _dialogUpdateCountPerons.findViewById(R.id.empty);
        final RadioButton _full = (RadioButton) _dialogUpdateCountPerons.findViewById(R.id.full);

        _empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder.setLength(0);
                mStringBuilder.append("น้อย");
                _full.setChecked(false);
                _empty.setChecked(true);
            }
        });

        _full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStringBuilder.setLength(0);
                mStringBuilder.append("เยอะ");
                _full.setChecked(true);
                _empty.setChecked(false);
            }
        });

        if (mBusType.equals("van")) {
            _empty.setText("ว่าง");
            _full.setText("เต็ม");
        } else {
            _empty.setText("น้อย");
            _full.setText("เยอะ");
        }

        if (_amountPerson.equals("น้อย") || _amountPerson.equals("ว่าง")) {
            _empty.setChecked(true);
        } else {
            _full.setChecked(true);
        }

        final TextView _typeTv = (TextView) _dialogUpdateCountPerons.findViewById(R.id.typeTv);

        switch (_type) {
            case "bus":
                _typeTv.setText("รถเมล์");
                break;
            case "bts":
                _typeTv.setText("บีทีเอส");
                break;
            case "brt":
                _typeTv.setText("บีอาที");
                break;
            case "van":
                _typeTv.setText("รถตู้");
                break;
            case "public":
                _typeTv.setText("สาธารณะ");
                break;
            case "boat":
                _typeTv.setText("เรือ");
                break;
        }

        _dialogUpdateCountPerons.show();
    }

    public void setupAfterSharedBus(String _typeBus) {

        stausShared = "1+" + _typeBus + "*" + String.valueOf(mSelectTraffic);
        mControlFile.setFile(this, stausShared, "stausShared");

        mCheckBtnSharedBus = true;
        mStopSharedIm.setVisibility(View.VISIBLE);

        startService(new Intent(getApplicationContext(), MyService.class));

        checkDialogPostFacebook();

        if (mStausAlertUsermanual.equals("show")) {
            final Dialog _dialogUsermanual = new Dialog(BusGps.this);
            _dialogUsermanual.requestWindowFeature(Window.FEATURE_NO_TITLE);
            _dialogUsermanual.setContentView(R.layout.activity_dialog_usermanual_bus);
            _dialogUsermanual.setCancelable(false);

            final CheckBox _checkBoxAlertUsermanual = (CheckBox) _dialogUsermanual.findViewById(R.id.checkBoxAlertUsermanual);

            Button _buttonOk = (Button) _dialogUsermanual.findViewById(R.id.btnOk);
            _buttonOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    _dialogUsermanual.cancel();

                    if (_checkBoxAlertUsermanual.isChecked()) {
                        mControlFile.setFile(getApplicationContext(), "not", "userManual");
                        mStausAlertUsermanual = "not";
                    }
                }
            });
            _dialogUsermanual.show();
        }
    }


    //------------------ Dialog Shared Main---------------------------------

    private void dialogSharedMain() {

        final Dialog _dialogSharedMain = new Dialog(BusGps.this);
        _dialogSharedMain.setTitle(R.string.title_dialog_sharedbus);
        _dialogSharedMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogSharedMain.setContentView(R.layout.activity_dialog_shared_traffic_menu);

        ImageView _btnClose = (ImageView) _dialogSharedMain.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogSharedMain.cancel();
            }
        });

        ImageButton _bus = (ImageButton) _dialogSharedMain.findViewById(R.id.bus);
        _bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    dialogBusShared(_dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton _bts = (ImageButton) _dialogSharedMain.findViewById(R.id.bts);
        _bts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    mSelectTraffic = 1;
                    mBusType = "bts";
                    dialogBtsBrtBoat("bts", _dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton _brt = (ImageButton) _dialogSharedMain.findViewById(R.id.brt);
        _brt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    mSelectTraffic = 2;
                    mBusType = "brt";
                    dialogBtsBrtBoat("brt", _dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton _van = (ImageButton) _dialogSharedMain.findViewById(R.id.van);
        _van.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    dialogVanShared(_dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });

        ImageButton _boat = (ImageButton) _dialogSharedMain.findViewById(R.id.boat);
        _boat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    mSelectTraffic = 5;
                    mBusType = "boat";
                    dialogBtsBrtBoat("boat", _dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });

        final ImageButton _accident = (ImageButton) _dialogSharedMain.findViewById(R.id.accident);
        _accident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAccidentShared(_dialogSharedMain);
            }
        });

        ImageButton _checkpoint = (ImageButton) _dialogSharedMain.findViewById(R.id.checkpoint);
        _checkpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCheckpointShared(_dialogSharedMain);
            }
        });

        ImageButton _publicway = (ImageButton) _dialogSharedMain.findViewById(R.id.publicway);
        _publicway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBtnSharedBus == false) {
                    dialogPublicShared(_dialogSharedMain);
                } else {
                    stopShared();
                }
            }
        });
        _dialogSharedMain.show();
    }

    //------------------ Dialog Bus Shared ---------------------------------

    private void dialogBusShared(final Dialog _dialogSharedMain) {
        final Dialog _dialogBusShared = new Dialog(BusGps.this);
        _dialogBusShared.setTitle(R.string.title_dialog_sharedbus);
        _dialogBusShared.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogBusShared.setContentView(R.layout.activity_dialog_shared_traffic_bus);

        ImageView _btnClose = (ImageView) _dialogBusShared.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogBusShared.cancel();
            }
        });

        String[] _bus_number = getResources().getStringArray(R.array.bus_number);
        ArrayAdapter<String> _adapter = new ArrayAdapter<String>(getApplication(), R.layout.spinner_item, _bus_number);

        final AutoCompleteTextView _autoComplete = (AutoCompleteTextView) _dialogBusShared.findViewById(R.id.autoComplete);
        _autoComplete.setAdapter(_adapter);
        _autoComplete.setThreshold(1);
        _autoComplete.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});

        final RadioButton _empty = (RadioButton) _dialogBusShared.findViewById(R.id.empty);
        final RadioButton _full = (RadioButton) _dialogBusShared.findViewById(R.id.full);

        _empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _full.setChecked(false);
                _empty.setChecked(true);
            }
        });

        _full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _empty.setChecked(false);
                _full.setChecked(true);
            }
        });


        Button _type1 = (Button) _dialogBusShared.findViewById(R.id.type1);
        _type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBusNo = _autoComplete.getText().toString();
                sharedBus(_full,"รถปรับอากาศ", _dialogBusShared, _dialogSharedMain);

            }
        });

        Button _type2 = (Button) _dialogBusShared.findViewById(R.id.type2);
        _type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBusNo = _autoComplete.getText().toString();
                sharedBus(_full,"รถธรรมดา", _dialogBusShared, _dialogSharedMain);
            }
        });
        _dialogBusShared.show();
    }

    void sharedBus(RadioButton _full, String _type, Dialog _dialogBusShared, Dialog _dialogSharedMain) {

        CheckBox _checkBoxBusFree = (CheckBox) _dialogBusShared.findViewById(R.id.checkBox);
        final TextView _textAlert = (TextView) _dialogBusShared.findViewById(R.id.textAlert);

        int _busFree = 0;

        if (mBusNo.length() <= 0) {
            _textAlert.setVisibility(View.VISIBLE);
        } else {
            String _stausBus;

            if (_full.isChecked()) {
                _stausBus = "เยอะ";
            } else {
                _stausBus = "น้อย";
            }

            _textAlert.setText("");
            if (_checkBoxBusFree.isChecked()) {
                _busFree = 1;
            } else {
                _busFree = 0;
            }
            mBusType = "bus";
            mSelectTraffic = 0;
            sharedBusGame(mBusType, mBusNo, _type, String.valueOf(_busFree), "", "", _stausBus);
            _dialogBusShared.cancel();
            _dialogSharedMain.cancel();
        }
    }

    //------------------ Dialog Bts Brt Boat----------------------------------

    private void dialogBtsBrtBoat(final String _type, final Dialog _tempDialog) {

        String[] _list = null;

        final Dialog _dialogBtsBrtBoat = new Dialog(BusGps.this);
        _dialogBtsBrtBoat.setTitle(R.string.title_dialog_sharedbus);
        _dialogBtsBrtBoat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogBtsBrtBoat.setContentView(R.layout.activity_dialog_shared_traffic_bts_brt_boat);

        ImageView _btnClose = (ImageView) _dialogBtsBrtBoat.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogBtsBrtBoat.cancel();
            }
        });

        TextView _head = (TextView) _dialogBtsBrtBoat.findViewById(R.id.head);

        final RadioButton _empty = (RadioButton) _dialogBtsBrtBoat.findViewById(R.id.empty);
        final RadioButton _full = (RadioButton) _dialogBtsBrtBoat.findViewById(R.id.full);

        _empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _full.setChecked(false);
                _empty.setChecked(true);
            }
        });

        _full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _empty.setChecked(false);
                _full.setChecked(true);
            }
        });

        switch (_type) {
            case "brt":
                _list = new String[]{"สาทร - ราชพฤกษ์", "ราชพฤกษ์ - สาทร"};
                _head.setText("บีอาที");
                break;
            case "bts":
                LinearLayout layoutCheckBox = (LinearLayout) _dialogBtsBrtBoat.findViewById(R.id.layoutCheckBox);
                layoutCheckBox.setVisibility(View.GONE);
                _list = new String[]{"หมอชิต - แบริ่ง", "แบริ่ง - หมอชิต",
                        "สนามกีฬาแห่งชาติ - บางหว้า", "บางหว้า - สนามกีฬาแห่งชาติ"};
                _head.setText("บีทีเอส");
                break;
            case "boat":
                _list = new String[]{"สะพานตากสิน - สะพานพระราม7", "สะพานพระราม7 - สะพานตากสิน",
                        "ท่าเรือวัดศรีบุญเรือง - ท่าเรือประตูน้ำ", "ท่าเรือประตูน้ำ - ท่าเรือวัดศรีบุญเรือง",
                        "ท่าเรือประตูน้ำ - ผ่านฟ้าลีลาส", "ผ่านฟ้าลีลาส - ท่าเรือประตูน้ำ"};
                _head.setText("เรือ");
                break;
        }

        final Spinner _spinner = (Spinner) _dialogBtsBrtBoat.findViewById(R.id.spinner);
        ArrayAdapter<String> _adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, _list);
        _spinner.setAdapter(_adapter);

        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (_type) {
                    case "brt":
                        switch (position) {
                            case 0:
                                mBusNo = "สาทร - ราชพฤกษ์";
                                break;
                            case 1:
                                mBusNo = "ราชพฤกษ์ - สาทร";
                                break;
                        }
                        break;
                    case "bts":
                        switch (position) {
                            case 0:
                                mBusNo = "หมอชิต - แบริ่ง";
                                break;
                            case 1:
                                mBusNo = "แบริ่ง - หมอชิต";
                                break;
                            case 2:
                                mBusNo = "สนามกีฬาแห่งชาติ - บางหว้า";
                                break;
                            case 3:
                                mBusNo = "บางหว้า - สนามกีฬาแห่งชาติ";
                                break;
                        }
                        break;
                    case "boat":
                        switch (position) {
                            case 0:
                                mBusNo = "สะพานตากสิน - บางโพ";
                                break;
                            case 1:
                                mBusNo = "บางโพ - สะพานตากสิน";
                                break;
                            case 2:
                                mBusNo = "ท่าเรือวัดศรีบุญเรือง - ท่าเรือประตูน้ำ";
                                break;
                            case 3:
                                mBusNo = "ท่าเรือประตูน้ำ - ท่าเรือวัดศรีบุญเรือง";
                                break;
                            case 4:
                                mBusNo = "ท่าเรือประตูน้ำ - ผ่านฟ้าลีลาส";
                                break;
                            case 5:
                                mBusNo = "ผ่านฟ้าลีลาส - ท่าเรือประตูน้ำ";
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button _btnShared = (Button) _dialogBtsBrtBoat.findViewById(R.id.btnShared);
        _btnShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String _stausBus;

                if (_full.isChecked()) {
                    _stausBus = "เยอะ";
                } else {
                    _stausBus = "น้อย";
                }
                sharedBusGame(mBusType, mBusNo, "", "", "", "", _stausBus);
                _dialogBtsBrtBoat.cancel();
                _tempDialog.cancel();
            }
        });
        _dialogBtsBrtBoat.show();
    }

    //------------------ Dialog Van Shared ----------------------------------

    private void dialogVanShared(final Dialog _dialogSharedMain) {
        final Dialog _dialogVanShared = new Dialog(BusGps.this);
        _dialogVanShared.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogVanShared.setContentView(R.layout.activity_dialog_shared_traffic_van);

        final ScrollView _scrollView = (ScrollView) _dialogVanShared.findViewById(R.id.scrollView);

        ImageView _btnClose = (ImageView) _dialogVanShared.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogVanShared.cancel();
            }
        });

        final EditText _name = (EditText) _dialogVanShared.findViewById(R.id.name);
        final EditText _editText = (EditText) _dialogVanShared.findViewById(R.id.editText);

        final TextView _textAlert = (TextView) _dialogVanShared.findViewById(R.id.textAlert);

        final RadioButton _empty = (RadioButton) _dialogVanShared.findViewById(R.id.empty);
        final RadioButton _full = (RadioButton) _dialogVanShared.findViewById(R.id.full);

        _empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _full.setChecked(false);
                _empty.setChecked(true);
            }
        });

        _full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _empty.setChecked(false);
                _full.setChecked(true);
            }
        });

        Button _btnSend = (Button) _dialogVanShared.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (_name.length() > 0) {
                    String _stausBus = "";
                    mBusType = "van";
                    mSelectTraffic = 3;

                    if (_full.isChecked()) {
                        _stausBus = "เต็ม";
                    } else {
                        _stausBus = "ว่าง";
                    }
                    sharedBusGame(mBusType, _name.getText().toString(), "", "", _editText.getText().toString(), "", _stausBus);
                    _dialogSharedMain.cancel();
                    _dialogVanShared.cancel();
                } else {
                    _textAlert.setVisibility(View.VISIBLE);
                    _scrollView.scrollTo(0, 0);

                }
            }
        });
        _dialogVanShared.show();
    }

    //------------------ Dialog Accident Shared -----------------------------

    private void dialogAccidentShared(final Dialog _dialogSharedMain) {
        final Dialog _dialogAccidentShared = new Dialog(BusGps.this);
        _dialogAccidentShared.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogAccidentShared.setContentView(R.layout.activity_dialog_shared_traffic_accident);

        mCheckAccidentOrCheakpoint = false;

        final ScrollView _scrollView = (ScrollView) _dialogAccidentShared.findViewById(R.id.scrollView);

        ImageView _btnClose = (ImageView) _dialogAccidentShared.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogAccidentShared.cancel();
            }
        });

        final EditText _editText = (EditText) _dialogAccidentShared.findViewById(R.id.editText);
        final TextView _textAlert = (TextView) _dialogAccidentShared.findViewById(R.id.textAlert);

        final LinearLayout _accident1 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident1);
        _accident1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(1, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "รถชนเล็กน้อย";
            }
        });

        final LinearLayout _accident2 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident2);
        _accident2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(2, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "รถชนสาหัส";
            }
        });

        final LinearLayout _accident3 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident3);
        _accident3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(3, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "รถชนคน";
            }
        });

        final LinearLayout _accident4 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident4);
        _accident4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(4, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "รถเสีย";
            }
        });

        final LinearLayout _accident5 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident5);
        _accident5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(5, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "มอเตอร์ไซค์ล้ม";
            }
        });

        final LinearLayout _accident6 = (LinearLayout) _dialogAccidentShared.findViewById(R.id.accident6);
        _accident6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeaccident(6, _dialogAccidentShared);
                mStausCheckAccidentOrCheakpointt = "อื่นๆ";
            }
        });

        Button _btnSend = (Button) _dialogAccidentShared.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCheckAccidentOrCheakpoint == true) {
                    _dialogSharedMain.cancel();
                    _dialogAccidentShared.cancel();
                    mBusType = "accident";
                    mSelectTraffic = 6;
                    sharedAccidentCheckpoint(mBusType, "อุบัติเหตุ", _editText.getText().toString(), mStausCheckAccidentOrCheakpointt);
                } else {
                    _textAlert.setTextColor(Color.parseColor("#f25d5d"));
                    _textAlert.setText("กรุณาเลือกประเภทอุบัติเหตุ");
                    _scrollView.scrollTo(0, 0);
                }
            }
        });
        _dialogAccidentShared.show();
    }

    private void setTypeaccident(int _casePicture, Dialog _dialogAccidentShared) {
        mCheckAccidentOrCheakpoint = true;

        final ImageView _accidentPic1 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic1);
        final ImageView _accidentPic2 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic2);
        final ImageView _accidentPic3 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic3);
        final ImageView _accidentPic4 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic4);
        final ImageView _accidentPic5 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic5);
        final ImageView _accidentPic6 = (ImageView) _dialogAccidentShared.findViewById(R.id.accidentPic6);

        _accidentPic1.setImageResource(R.drawable.accident1_gray);
        _accidentPic2.setImageResource(R.drawable.accident2_gray);
        _accidentPic3.setImageResource(R.drawable.accident3_gray);
        _accidentPic4.setImageResource(R.drawable.accident4_gray);
        _accidentPic5.setImageResource(R.drawable.accident5_gray);
        _accidentPic6.setImageResource(R.drawable.accident6_gray);

        final TextView _accidentTxt1 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt1);
        final TextView _accidentTxt2 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt2);
        final TextView _accidentTxt3 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt3);
        final TextView _accidentTxt4 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt4);
        final TextView _accidentTxt5 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt5);
        final TextView _accidentTxt6 = (TextView) _dialogAccidentShared.findViewById(R.id.accidentTxt6);

        _accidentTxt1.setTextColor(Color.parseColor("#948e94"));
        _accidentTxt2.setTextColor(Color.parseColor("#948e94"));
        _accidentTxt3.setTextColor(Color.parseColor("#948e94"));
        _accidentTxt4.setTextColor(Color.parseColor("#948e94"));
        _accidentTxt5.setTextColor(Color.parseColor("#948e94"));
        _accidentTxt6.setTextColor(Color.parseColor("#948e94"));

        switch (_casePicture) {
            case 1:
                _accidentPic1.setImageResource(R.drawable.accident1_blue);
                _accidentTxt1.setTextColor(Color.parseColor("#006df0"));
                break;
            case 2:
                _accidentPic2.setImageResource(R.drawable.accident2_blue);
                _accidentTxt2.setTextColor(Color.parseColor("#006df0"));
                break;
            case 3:
                _accidentPic3.setImageResource(R.drawable.accident3_blue);
                _accidentTxt3.setTextColor(Color.parseColor("#006df0"));
                break;
            case 4:
                _accidentPic4.setImageResource(R.drawable.accident4_blue);
                _accidentTxt4.setTextColor(Color.parseColor("#006df0"));
                break;
            case 5:
                _accidentPic5.setImageResource(R.drawable.accident5_blue);
                _accidentTxt5.setTextColor(Color.parseColor("#006df0"));
                break;
            case 6:
                _accidentPic6.setImageResource(R.drawable.accident6_blue);
                _accidentTxt6.setTextColor(Color.parseColor("#006df0"));
                break;
        }
    }

    //------------------ Dialog Checkpoint Shared ---------------------------

    private void dialogCheckpointShared(final Dialog _dialogSharedMain) {
        final Dialog _dialogSharedCheckpoint = new Dialog(BusGps.this);
        _dialogSharedCheckpoint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogSharedCheckpoint.setContentView(R.layout.activity_dialog_shared_traffic_checkpoint);

        mCheckAccidentOrCheakpoint = false;

        final ScrollView _scrollView = (ScrollView) _dialogSharedCheckpoint.findViewById(R.id.scrollView);

        ImageView _btnClose = (ImageView) _dialogSharedCheckpoint.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogSharedCheckpoint.cancel();
            }
        });

        final EditText _editText = (EditText) _dialogSharedCheckpoint.findViewById(R.id.editText);
        final TextView _textAlert = (TextView) _dialogSharedCheckpoint.findViewById(R.id.textAlert);

        final LinearLayout _checkPoint1 = (LinearLayout) _dialogSharedCheckpoint.findViewById(R.id.checkPoint1);
        _checkPoint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeCheckpoint(1, _dialogSharedCheckpoint);
                mStausCheckAccidentOrCheakpointt = "ทำถนน";
            }
        });

        final LinearLayout _checkPoint2 = (LinearLayout) _dialogSharedCheckpoint.findViewById(R.id.checkPoint2);
        _checkPoint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeCheckpoint(2, _dialogSharedCheckpoint);
                mStausCheckAccidentOrCheakpointt = "ก่อสร้างขนาดใหญ่";
            }
        });

        final LinearLayout _checkPoint3 = (LinearLayout) _dialogSharedCheckpoint.findViewById(R.id.checkPoint3);
        _checkPoint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypeCheckpoint(3, _dialogSharedCheckpoint);
                mStausCheckAccidentOrCheakpointt = "อื่นๆ";
            }
        });

        Button _btnSend = (Button) _dialogSharedCheckpoint.findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCheckAccidentOrCheakpoint == true) {
                    _dialogSharedMain.cancel();
                    _dialogSharedCheckpoint.cancel();
                    mBusType = "checkpoint";
                    mSelectTraffic = 7;
                    sharedAccidentCheckpoint(mBusType, "ก่อสร้าง", _editText.getText().toString(), mStausCheckAccidentOrCheakpointt);
                } else {
                    _textAlert.setTextColor(Color.parseColor("#f25d5d"));
                    _textAlert.setText("กรุณาเลือกประเภทการก่อสร้าง");
                    _scrollView.scrollTo(0, 0);
                }
            }
        });
        _dialogSharedCheckpoint.show();
    }

    private void setTypeCheckpoint(int i, Dialog _dialogSharedCheckpoint) {
        mCheckAccidentOrCheakpoint = true;

        final ImageView _checkPointPic1 = (ImageView) _dialogSharedCheckpoint.findViewById(R.id.checkPointPic1);
        final ImageView _checkPointPic2 = (ImageView) _dialogSharedCheckpoint.findViewById(R.id.checkPointPic2);
        final ImageView _checkPointPic3 = (ImageView) _dialogSharedCheckpoint.findViewById(R.id.checkPointPic3);

        _checkPointPic1.setImageResource(R.drawable.checkpoint1_gray);
        _checkPointPic2.setImageResource(R.drawable.checkpoint2_gray);
        _checkPointPic3.setImageResource(R.drawable.accident6_gray);

        final TextView _checkPointTxt1 = (TextView) _dialogSharedCheckpoint.findViewById(R.id.checkPointTxt1);
        final TextView _checkPointTxt2 = (TextView) _dialogSharedCheckpoint.findViewById(R.id.checkPointTxt2);
        final TextView _checkPointTxt3 = (TextView) _dialogSharedCheckpoint.findViewById(R.id.checkPointTxt3);

        _checkPointTxt1.setTextColor(Color.parseColor("#948e94"));
        _checkPointTxt2.setTextColor(Color.parseColor("#948e94"));
        _checkPointTxt3.setTextColor(Color.parseColor("#948e94"));

        switch (i) {
            case 1:
                _checkPointPic1.setImageResource(R.drawable.checkpoint1_blue);
                _checkPointTxt1.setTextColor(Color.parseColor("#006df0"));
                break;
            case 2:
                _checkPointPic2.setImageResource(R.drawable.checkpoint2_blue);
                _checkPointTxt2.setTextColor(Color.parseColor("#006df0"));
                break;
            case 3:
                _checkPointPic3.setImageResource(R.drawable.accident6_blue);
                _checkPointTxt3.setTextColor(Color.parseColor("#006df0"));
                break;
        }
    }

    //------------------ Dialog Public Shared -------------------------------

    private void dialogPublicShared(final Dialog _dialogSharedMain) {
        final Dialog _dialogSharedPublic = new Dialog(BusGps.this);
        _dialogSharedPublic.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogSharedPublic.setContentView(R.layout.activity_dialog_shared_traffic_public);

        ImageView _btnClose = (ImageView) _dialogSharedPublic.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogSharedPublic.cancel();
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color1 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color1);
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#2196F3", _dialogSharedPublic, _dialogSharedMain);   //bluebold
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color2 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color2);
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#E91E63", _dialogSharedPublic, _dialogSharedMain);    // pink
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color3 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color3);
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#F4511E", _dialogSharedPublic, _dialogSharedMain);     //orange
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color4 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color4);
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#00ACC1", _dialogSharedPublic, _dialogSharedMain);      // cyan
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color5 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color5);
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#df3e3e", _dialogSharedPublic, _dialogSharedMain);       // red
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color6 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color6);
        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#9C27B0", _dialogSharedPublic, _dialogSharedMain);       // pupple
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color7 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color7);
        color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#77cc1c", _dialogSharedPublic, _dialogSharedMain);       // green
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color8 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color8);
        color8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#FFB300", _dialogSharedPublic, _dialogSharedMain);       // gold
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color9 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color9);
        color9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#43759c", _dialogSharedPublic, _dialogSharedMain);       // bluebold
            }
        });

        de.hdodenhof.circleimageview.CircleImageView color10 = (de.hdodenhof.circleimageview.CircleImageView) _dialogSharedPublic.findViewById(R.id.color10);
        color10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup("#717171", _dialogSharedPublic, _dialogSharedMain);       // gray
            }
        });
        _dialogSharedPublic.show();
    }

    void setup(String _color, Dialog _dialogSharedPublic, Dialog _dialogSharedMain) {

        final EditText _dialogSharedPublicNameTv = (EditText) _dialogSharedPublic.findViewById(R.id.name);
        final EditText _dialogSharedPublicDetailTv = (EditText) _dialogSharedPublic.findViewById(R.id.detail);
        final TextView _dialogSharedPublicAlertTv = (TextView) _dialogSharedPublic.findViewById(R.id.alert);
        final ScrollView _scrollView = (ScrollView) _dialogSharedPublic.findViewById(R.id.scrollView);

        if (_dialogSharedPublicNameTv.getText().toString().length() > 0) {
            mBusType = "public";
            mSelectTraffic = 4;
            sharedBusGame(mBusType, _dialogSharedPublicNameTv.getText().toString(),
                    "", "", _dialogSharedPublicDetailTv.getText().toString(), _color, "");
            _dialogSharedPublic.cancel();
            _dialogSharedMain.cancel();
            _dialogSharedPublicAlertTv.setVisibility(View.GONE);
        } else {
            _dialogSharedPublicAlertTv.setVisibility(View.VISIBLE);
            _scrollView.scrollTo(0, 0);
        }
    }

    // ****************************************************************************************************
    // **************************             Stop Control            *************************************
    // ****************************************************************************************************

    public void stopShared() {
        if (!mBusType.equals("bts") && !mBusType.equals("public")) {
            final String[] lists = {"แก้ไขจำนวนผู้โดยสาร", "ยกเลิกการแชร์" ,"ปิดหน้าต่าง"};
            new AlertDialog.Builder(BusGps.this).setTitle("กำลังแชร์ตำแหน่งการจราจรอยู่").setItems(lists, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mControlDatabae.getDatabaseBusGpsStopShared();
                            break;
                        case 1:
                            dialogMakesureStopShared();
                            break;
                    }
                    dialog.cancel();
                }
            }).show();
        } else {
            dialogMakesureStopShared();
        }
    }

    public void dialogMakesureStopShared() {
        String _tempNameType = null;

        switch (mBusType) {
            case "bus":
                _tempNameType = "รถเมล์";
                break;
            case "bts":
                _tempNameType = "บีทีเอส";
                break;
            case "brt":
                _tempNameType = "บีอาที";
                break;
            case "van":
                _tempNameType = "รถตู้";
                break;
            case "public":
                _tempNameType = "สาธารณะ";
                break;
            case "boat":
                _tempNameType = "เรือ";
                break;
        }

        AlertDialog.Builder _builder = new AlertDialog.Builder(BusGps.this);
        _builder.setTitle("ท่านกำลังแชร์ตำแหน่ง" + _tempNameType + "อยู่");
        _builder.setMessage("ยกเลิกการแชร์ตำแหน่ง");
        _builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                stopService(new Intent(BusGps.this, MyService.class));

                mControlDatabae.setDatabaseBusGpsStopShared(stausShared.substring(2, stausShared.indexOf("*")));
                dialog.cancel();
            }
        });
        _builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = _builder.create();
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#147cce"));
        nbutton.setTypeface(null, Typeface.BOLD);
    }

    public void setupAlfetStopshared() {
        stausShared = "0+";
        mControlFile.setFile(this, stausShared, "stausShared");

        mCheckBtnSharedBus = false;
        mStopSharedIm.setVisibility(View.INVISIBLE);

        getDatabase();
        moveAnimateCamera();
    }

    // ****************************************************************************************************
    // **************************           facebook control            ***********************************
    // ****************************************************************************************************

    public void checkDialogPostFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            dialogLoginFacebook();
        } else {
            dialogPostFacebook();
        }

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                setItemSpinnerAndFeedDatabae();
                moveAnimateCamera();
            }
        }, 15000);
    }

    private void setItemSpinnerAndFeedDatabae() {
        mSpinner.setSelection(++mSelectTraffic);
        mAdapter.notifyDataSetChanged();
        mSelectTraffic--;
    }

    private void dialogLoginFacebook() {
        final Dialog _dialogPostFacebook = new Dialog(BusGps.this);
        _dialogPostFacebook.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialogPostFacebook.setContentView(R.layout.activity_dialog_login_facebook);

        ImageView _btnClose = (ImageView) _dialogPostFacebook.findViewById(R.id.btnClose);
        _btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialogPostFacebook.cancel();
            }
        });

        LoginButton _loginFacebookBtn = (LoginButton) _dialogPostFacebook.findViewById(R.id.loginFacebookBtn);
        _loginFacebookBtn.setReadPermissions("email,public_profile");//,user_friends");
        _loginFacebookBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                GraphRequest data_request = GraphRequest.newMeRequest(
                        login_result.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject json_object,
                                    GraphResponse response) {

                                _dialogPostFacebook.cancel();
                                dialogPostFacebook();
                            }
                        });
                Bundle permission_param = new Bundle();
                permission_param.putString("fields", "id,name,email,gender"); //,picture.width(150).height(150)");
                data_request.setParameters(permission_param);
                data_request.executeAsync();
            }

            @Override
            public void onCancel() {
                _dialogPostFacebook.cancel();
            }

            @Override
            public void onError(FacebookException exception) {
                _dialogPostFacebook.cancel();
            }
        });
        _dialogPostFacebook.show();
    }

    private void dialogPostFacebook() {

        final String[] _items = {"แชร์โพสต์", "ยกเลิก"};

        AlertDialog.Builder _builder = new AlertDialog.Builder(BusGps.this);
        _builder.setTitle("แชร์จิตสาธารณะบนเฟซบุ๊ก");
        _builder.setCancelable(false);
        _builder.setItems(_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (_items[item]) {
                    case "แชร์โพสต์":
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle("Smile Map แอพจิตสาธารณะ")
                                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.blackcatwalk.sharingpower"))
                                    .build();
                            mDialogFacebook.show(linkContent);
                        }
                        break;
                    case "ยกเลิก":
                        dialog.dismiss();
                        break;
                }
            }
        });
        _builder.show();
    }

    //--------------------------------------------------------------------------------------------------

    private void bindWidget() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSettingIm = (ImageView) findViewById(R.id.main);
        mRefreshIm = (ImageView) findViewById(R.id.refreshIm);
        mUserManualIm = (ImageView) findViewById(R.id.userManualIm);
        mLabalName = (TextView) findViewById(R.id.titleName);
        mSharedBusIm = (CircleImageView) findViewById(R.id.btnShared);
        mStopSharedIm = (CircleImageView) findViewById(R.id.btnStopShared);
        mCureentLocationIm = (ImageView) findViewById(R.id.btnCureentLocation);
    }

    // ****************************************************************************************************
    // **************************        Get Duration , Distance         **********************************
    // ****************************************************************************************************

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
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

    public String getDirectionsUrl(LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + mCurrentLatitude + "," + mCurrentLongitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.bus_spinner_bts_gray("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

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

            //mDistance = "";
            mDuration = "";

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

                    if (j == 0) {    // Get mDistance from the list
                        // mDistance = (String) point.get("mDistance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        mDuration = (String) point.get("duration");
                        break;
                    }
                }
                //int a = mDistance.length();
                //a -= 2;
                //mDistance = mDistance.substring(0, a);
                //mDistance = mDistance + "กม.";

                int a = mDuration.length();
                if (a > 7) {
                    int b;
                    String temp = "";

                    b = mDuration.indexOf("r");
                    temp = mDuration.substring(b + 2, a - 4);

                    b = mDuration.indexOf("h");
                    b = b - 1;
                    mDuration = mDuration.substring(0, b);
                    mDuration = mDuration + " ชม. ";

                    mDuration = mDuration + temp + "น.";
                } else {
                    a -= 4;
                    mDuration = mDuration.substring(0, a);
                    mDuration = mDuration + " นาที";
                }

                mClickMaker.showInfoWindow();
                ControlProgress.hideDialog();
            }
        }
    }
}


/*      ______________________________________________________
        | Windows Dialog　　　　　　　　　　　　    [－] [口] [X] |
        | ￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣￣ ￣￣|
        |　System detected that this Bug!                    |
        |  How do you do? 　　　　　　                         |
        |　 　               　＿＿＿＿＿＿　　                  |
        | 　 　               ｜　I die  |　　                 |
        |　 　　               ￣￣￣￣￣￣　　　　              |
        |＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿＿ _|
*/