package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class LocationGps extends AppCompatActivity {

    // ------------------- Google_Map ---------------------//
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mCurrentLocation;
    private String mDuration;
    private String mDistance;
    private Marker mSelectedMaker;
    private MarkerOptions mMarker;
    private float mCurrentZoom = 30;

    // ----------------- User Interface -------------------//
    private SupportMapFragment mMapFragment;
    private ImageView btnClose;
    private ImageView btnCureentLocation;
    private TextView titleName;
    private CircleImageView btnShared;
    private Spinner spinner;
    private int mSelectTypeLocation = 0;

    // ------------------- Database -----------------------//
    private String type = "restroom";
    private String detailLocation;

    // ----------------- Url to database ------------------//
    private String setLocationUrl = "https://www.smilemap.me/android/set.php";
    private String getLocationUrl = "https://www.smilemap.me/android/get.php?main=location&sub=";
    private String tempUrl;

    // ----------- FIle System --------------//
    private static final String fileUserName = "Username.txt";
    private static final int readSize = 100; // Read 100byte
    private String username;

    private static final String fileSetting = "setting.txt";
    private String stausTaffic = "1";
    private String resultNearby = "55"; // nearby 50km
    private String tempResultNearby = null;
    private String maptype = "0"; // 0 = normal , 1 = map_satelltte
    private LocationGpsCustomSpinnerAdapter adapter;

    // ----------- Facebook --------------//
    ShareDialog shareDialog;
    int REQUEST_CAMERA = 0;
    int SELECT_FILE = 1;
    private CallbackManager callbackManager;
    private ImageView main;
    private ImageView userManualIm;

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        bindWidget();

        titleName.setText("สถานที่");

        userManualIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationGps.this, Tutorial.class).putExtra("type","location"));
            }
        });

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                     @Override
                                     public void onMapReady(GoogleMap googleMap) {
                                         mMap = googleMap;
                                         LatLng bangkok = new LatLng(14.76553, 100.53904);
                                         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangkok, 8));
                                         mMap.getUiSettings().setCompassEnabled(false);
                                         mMap.setTrafficEnabled(true);
                                         readFile(fileSetting);

                                         mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                             public boolean onMarkerClick(Marker marker) {

                                                 if (!marker.getTitle().equals("ตำแหน่งปัจจุบัน")) {

                                                     if (mSelectedMaker != null) {
                                                         mSelectedMaker.setIcon(BitmapDescriptorFactory.fromResource(getIdMarkerLocation()));
                                                     }

                                                     marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected));

                                                     Control.sDialog(LocationGps.this);
                                                     mSelectedMaker = marker;

                                                     DownloadTask downloadTask = new DownloadTask();
                                                     downloadTask.execute(Control.getDirectionsUrl(mCurrentLocation, marker.getPosition())); // Start downloading json data from Google Directions API

                                                     CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(mCurrentZoom).tilt(30).build();
                                                     mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                                 }
                                                 return true;
                                             }
                                         });

                                         mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                                                                               @Override
                                                                               public void onInfoWindowClick(Marker marker) {
                                                                                   Intent locationDetail = new Intent(getApplicationContext(), LocationDetail.class);
                                                                                   locationDetail.putExtra("type", type);
                                                                                   locationDetail.putExtra("detail", marker.getSnippet().substring(marker.getSnippet().indexOf("$") + 1, marker.getSnippet().length()));
                                                                                   locationDetail.putExtra("lat", marker.getPosition().latitude);
                                                                                   locationDetail.putExtra("lng", marker.getPosition().longitude);
                                                                                   locationDetail.putExtra("username", username);
                                                                                   locationDetail.putExtra("duration", mDuration);
                                                                                   locationDetail.putExtra("distance", mDistance);
                                                                                   startActivity(locationDetail);
                                                                               }
                                                                           }

                                         );

                                         mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                                                                            @Override
                                                                            public void onCameraChange(CameraPosition pos) {
                                                                                if (pos.zoom != mCurrentZoom) {
                                                                                    mCurrentZoom = pos.zoom;
                                                                                }
                                                                            }
                                                                        }

                                         );
                                     }
                                 }

        );

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                            @Override
                                            public void onConnected(Bundle bundle) {
                                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                                if (mLastLocation != null) {
                                                    mCurrentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                                                    if (mSelectedMaker == null) {
                                                        moveAnimateCamera(mCurrentLocation);
                                                        getDatabase();
                                                    }
                                                } else {
                                                    Control.alertCurrentGps(LocationGps.this);
                                                }
                                            }

                                            @Override
                                            public void onConnectionSuspended(int i) {
                                                mGoogleApiClient.connect();
                                            }
                                        }

                )
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                                   @Override
                                                   public void onConnectionFailed(ConnectionResult connectionResult) {
                                                       Control.alertCurrentGps(LocationGps.this);
                                                       mGoogleApiClient.connect();
                                                   }
                                               }

                )
                .build();

        btnShared.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (mCurrentLocation != null) {
                                                 sharedLocation();
                                             } else {
                                                 Control.alertCurrentGps(LocationGps.this);
                                             }
                                         }
                                     }

        );

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    readFile("setting.txt");

                    final Dialog dialog = new Dialog(LocationGps.this);
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


        btnCureentLocation.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      if (mCurrentLocation != null) {

                                                          mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                                          if (mLastLocation != null) {
                                                              mCurrentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                                          }

                                                          moveAnimateCamera(mCurrentLocation);
                                                      } else {
                                                          Control.alertCurrentGps(LocationGps.this);
                                                      }
                                                  }
                                              }

        );

        CustomSpinner location_data[] = new CustomSpinner[]{
                new CustomSpinner(R.drawable.spinner_search_gray, getString(R.string.select_location)),
                new CustomSpinner(R.drawable.location_spinner_restroom_gray, getString(R.string.restroom_name)),
                new CustomSpinner(R.drawable.location_spinner_medical_gray, getString(R.string.pharmacy_name)),
                new CustomSpinner(R.drawable.location_spinner_animal_gray, getString(R.string.veterinary_clinic_name)),
                new CustomSpinner(R.drawable.location_spinner_relax_gray, getString(R.string.officer_name)),
                new CustomSpinner(R.drawable.location_spinner_home_gray, getString(R.string.daily_home_name)),
                new CustomSpinner(R.drawable.location_spinner_garage_gray, getString(R.string.garage_name))};

        adapter = new

                LocationGpsCustomSpinnerAdapter(this, R.layout.activity_spinner,
                location_data);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        adapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

                                          {
                                              @Override
                                              public void onItemSelected(AdapterView<?> av, View v, int position, long id) {


                                                  if (position == 0) {
                                                      return;
                                                  }

                                                  if (mCurrentLocation != null) {

                                                      mSelectedMaker = null;

                                                      tempUrl = null;
                                                      mSelectTypeLocation = position - 1;

                                                      switch (position) {
                                                          case 1:
                                                              type = "restroom";
                                                              break;
                                                          case 2:
                                                              type = "pharmacy";
                                                              break;
                                                          case 3:
                                                              type = "veterinary_clinic";
                                                              break;
                                                          case 4:
                                                              type = "officer";
                                                              break;
                                                          case 5:
                                                              type = "daily_home";
                                                              break;
                                                          case 6:
                                                              type = "garage";
                                                              break;
                                                      }

                                                      Control.sDialog(LocationGps.this);
                                                      getDatabase();
                                                  } else {
                                                      Control.alertCurrentGps(LocationGps.this);
                                                  }
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> av) {
                                                  return;
                                              }
                                          }

        );
        readFile(fileUserName);
    }

    private void bindWidget() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        main = (ImageView) findViewById(R.id.main);
        userManualIm = (ImageView) findViewById(R.id.userManualIm);
        titleName = (TextView) findViewById(R.id.titleName);
        btnShared = (CircleImageView) findViewById(R.id.btnShared);
        btnCureentLocation = (ImageView) findViewById(R.id.btnCureentLocation);
        spinner = (Spinner) findViewById(R.id.spinner);
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
        Control.hDialog();
    }

    //  ------------------  Google map -----------------//

    private void addMarkerDatabase(double latitude, double longitude, String Locationdetail,
                                   String username, double _distance) {

        LatLng latLng = new LatLng(latitude, longitude);
        mMarker = new MarkerOptions();

        mMarker.position(latLng).title(String.valueOf("1") + "$" + username).snippet(_distance + "$" + Locationdetail);

     /*   if (latitude == mLatitudeClickMaker && longitude == mLongitudeClickMaker) {

            mMarker.icon(BitmapDescriptorFactory.fromResource(getIdMarkerSelected()));
            clickMaker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));

            mLatitudeClickMaker = 0;
            mLongitudeClickMaker = 0 ;
        } else {
            mMarker.icon(BitmapDescriptorFactory.fromResource(getIdMarkerLocation()));
        }*/

        mMarker.icon(BitmapDescriptorFactory.fromResource(getIdMarkerLocation()));

        mMap.addMarker(mMarker);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                View infoWindow;

                if (marker.getTitle().equals(getString(R.string.current_location))) {
                    LayoutInflater inflater = getLayoutInflater();
                    infoWindow = inflater.inflate(R.layout.activity_info_window, null);

                    TextView nameMarker = (TextView) infoWindow.findViewById(R.id.nameMarker);
                    nameMarker.setText(marker.getTitle());
                } else {

                    String distanceNumText = marker.getSnippet().substring(0, marker.getSnippet().indexOf("$"));
                    Double distanceNum = Double.parseDouble(distanceNumText);

                    if (distanceNum <= 1.0) {
                        switch (distanceNumText) {
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
                        mDistance = distanceNumText + " กม.";
                    }

                    LayoutInflater inflater = getLayoutInflater();
                    infoWindow = inflater.inflate(R.layout.activity_info_window_location, null);

                    String detailTxt = marker.getSnippet().substring(marker.getSnippet().indexOf("$") + 1, marker.getSnippet().length());

                    if (detailTxt.length() > 0) {
                        TextView detail = (TextView) infoWindow.findViewById(R.id.detail);
                        detail.setVisibility(View.VISIBLE);

                        if (detailTxt.length() > 30) {
                            detailTxt = detailTxt.substring(0, 28) + "...";
                        }
                        detail.setText(detailTxt);
                    }

                    TextView distanceTxt = (TextView) infoWindow.findViewById(R.id.distanceTxt);
                    distanceTxt.setText(mDistance);

                    TextView durationTxt = (TextView) infoWindow.findViewById(R.id.durationTxt);
                    durationTxt.setText(mDuration);
                }
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    public void addMarkerCurrent() {
        mMarker = new MarkerOptions();
        mMarker.position(mCurrentLocation).title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
        mMap.addMarker(mMarker);
    }

    public void moveAnimateCamera(LatLng latLung) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLung).zoom(14).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
/*
    private int getIdMarkerSelected() {

        int id = R.drawable.marker_location_toilet_selected;

        switch (mSelectTypeLocation) {
            case 1:
                id = R.drawable.marker_location_medicine_selected;
                break;
            case 2:
                id = R.drawable.marker_location_dog_selected;
                break;
            case 3:
                id = R.drawable.marker_location_relax_selected;
                break;
            case 4:
                id = R.drawable.marker_location_home_selected;
                break;
            case 5:
                id = R.drawable.marker_location_tires_selected;
                break;
        }
        return id;
    }*/

    private int getIdMarkerLocation() {

        int id = R.drawable.marker_location_toilet;

        switch (mSelectTypeLocation) {
            case 1:
                id = R.drawable.marker_location_medicine;
                break;
            case 2:
                id = R.drawable.marker_location_dog;
                break;
            case 3:
                id = R.drawable.marker_location_relax;
                break;
            case 4:
                id = R.drawable.marker_location_home;
                break;
            case 5:
                id = R.drawable.marker_location_tires;
                break;
        }
        return id;
    }

    // -------------------- Get Duration , Distance ---------------------------//

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

                    if (j == 0) {    // Get distance from the list
                        //distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get mDuration from the list
                        mDuration = (String) point.get("duration");
                        continue;
                    }
                }

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
                    mDuration = mDuration + "นาที";
                }
                mSelectedMaker.showInfoWindow();
                Control.hDialog();
            }
        }
    }


    //  ------------------  Database Mysql -----------------//

    public void getDatabase() {

        if (Control.checkInternet(this)) {

            if (mMarker != null) {
                mMap.clear();
            }

            addMarkerCurrent();

            tempUrl = null;
            tempUrl = getLocationUrl + type + "&lat=" + mCurrentLocation.latitude + "&lng="
                    + mCurrentLocation.longitude + "&distance=" + resultNearby;

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, tempUrl + "&ramdom=" + Control.randomNumber(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONArray ja = response.getJSONArray("shared_location");

                                String LocationDetail = null;
                                String username = null;
                                double longitude = 0;
                                double latitude = 0;
                                double distance = 0;
                                JSONObject jsonObject = null;
                                DecimalFormat decim = new DecimalFormat("0.0");

                                for (int i = 0; i < ja.length(); i++) {

                                    jsonObject = ja.getJSONObject(i);
                                    latitude = Double.parseDouble(jsonObject.getString("lat"));
                                    longitude = Double.parseDouble(jsonObject.getString("lng"));
                                    //username = jsonObject.getString("username");
                                    LocationDetail = jsonObject.getString("location_detail");
                                    distance = Double.parseDouble(decim.format(Double.parseDouble(jsonObject.getString("distance"))));

                                    addMarkerDatabase(latitude, longitude, LocationDetail, username, distance);
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

    @SuppressWarnings({"MissingPermission"})
    public void setDatabase() {

        readFile(fileUserName);

        if (Control.checkInternet(this)) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {

                mCurrentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest jor = new StringRequest(Request.Method.POST, setLocationUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                moveAnimateCamera(mCurrentLocation);
                                spinner.setSelection(++mSelectTypeLocation);
                                adapter.notifyDataSetChanged();
                                mSelectTypeLocation--;

                                selectImage();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Control.alertCurrentInternet(LocationGps.this);
                                Control.hDialog();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("main", "location");
                        params.put("username", username);
                        params.put("lat", Double.toString(mLastLocation.getLatitude()));
                        params.put("lng", Double.toString(mLastLocation.getLongitude()));
                        params.put("type", type);
                        params.put("location_detail", detailLocation);
                        return params;
                    }
                };
                jor.setShouldCache(false);
                requestQueue.add(jor);

            } else {
                Control.alertCurrentGps(this);
            }
        } else {
            Control.alertInternet(this);
        }
    }

    //  ------------------  User Interface -----------------//


    private void sharedLocation() {
        final Dialog dialog = new Dialog(LocationGps.this);
        dialog.setTitle(R.string.title_dialog_sharedbus);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_shared_location_menu);

        btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        ImageButton toilet = (ImageButton) dialog.findViewById(R.id.toilet);
        toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("ห้องน้ำ", dialog);
            }
        });

        ImageButton medicine = (ImageButton) dialog.findViewById(R.id.medicine);
        medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("ร้านขายยา", dialog);
            }
        });

        ImageButton dog = (ImageButton) dialog.findViewById(R.id.dog);
        dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("รักษาสัตว์", dialog);
            }
        });

        ImageButton home = (ImageButton) dialog.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("ที่พักรายวัน", dialog);
            }
        });

        ImageButton relax = (ImageButton) dialog.findViewById(R.id.relax);
        relax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("จุดพักผ่อน", dialog);
            }
        });

        ImageButton tires = (ImageButton) dialog.findViewById(R.id.tires);
        tires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailLocation("ร้านปะยาง", dialog);
            }
        });
        dialog.show();
    }

    public void setDetailLocation(final String tempHead, final Dialog tempDialog) {

        final Dialog dialog = new Dialog(LocationGps.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_shared_location_input);

        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        editText.setHint("กรุณากรอกรายละเอียดเกี่ยวกับสถานที่");

        final TextView head = (TextView) dialog.findViewById(R.id.head);
        head.setText(tempHead);

        btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        Button btnSend = (Button) dialog.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                switch (tempHead) {
                    case "ห้องน้ำ":
                        type = "restroom";
                        mSelectTypeLocation = 0;
                        break;
                    case "ร้านขายยา":
                        type = "pharmacy";
                        mSelectTypeLocation = 1;
                        break;
                    case "รักษาสัตว์":
                        type = "veterinary_clinic";
                        mSelectTypeLocation = 2;
                        break;
                    case "จุดพักผ่อน":
                        type = "officer";
                        mSelectTypeLocation = 3;
                        break;
                    case "ที่พักรายวัน":
                        type = "daily_home";
                        mSelectTypeLocation = 4;
                        break;
                    case "ร้านปะยาง":
                        type = "garage";
                        mSelectTypeLocation = 5;
                        break;
                }

                detailLocation = editText.getText().toString();
                setDatabase();
                dialog.cancel();
                tempDialog.cancel();
            }
        });
        dialog.show();
    }

    public void ShareDialog(Bitmap imagePath) {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(imagePath)
                .setCaption("Testing")
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        shareDialog.show(content);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ShareDialog(thumbnail);
    }


    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap thumbnail;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);

        ShareDialog(thumbnail);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE)

                onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA)

                onCaptureImageResult(data);
        }
    }

    private void selectImage() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();


        if (accessToken == null) {
            final Dialog dialog = new Dialog(LocationGps.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationGps.this);
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
                    case "ถ่ายรูปแชร์":
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                        break;
                    case "เลือกจากคลังภาพ":
                        Intent _intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        _intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(_intent, "Select File"),
                                SELECT_FILE);
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
