package com.blackcatwalk.sharingpower;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

import java.util.ArrayList;

public class Group_detail_page1 extends AppCompatActivity implements LocationListener {

    // ------------------- Google_Map ---------------------//
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions marker;
    private LatLng currentLocation;
    private long updatareInterval = 220000;  // 1minute = 60000 milesecond
    private long fastestInterval = 200000;
    private double currentLatitude;
    private double currentLongitude;
    private Location mLastLocation;

    // ----------------- Url to database ------------------//
    private String url = "https://www.smilemap.me/android/get.php?main=group_detail_marker";
    private String tempUrl;

    private ArrayList<Double> lat;
    private ArrayList<Double> lng;
    private ArrayList<String> image;
    private ArrayList<String> locationDetail;
    private String nameHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail_page1);
        getSupportActionBar().hide();

        Control.sDialog(this);

        nameHead = getIntent().getStringExtra("name");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;

                LatLng bangkok = new LatLng(14.76553, 100.53904);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangkok, 8));
                mMap.getUiSettings().setCompassEnabled(false);

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        intentActivity();
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        intentActivity();
                        return true;
                    }
                });
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mLastLocation = LocationServices.FusedLocationApi
                                .getLastLocation(mGoogleApiClient);

                        if (mLastLocation != null) {
                            currentLatitude = mLastLocation.getLatitude();
                            currentLongitude = mLastLocation.getLongitude();

                            addMarkerCurrent(currentLatitude, currentLongitude);
                            getDatabase();
                        }
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Control.alertCurrentGps(getApplication());
                        mGoogleApiClient.connect();
                    }
                })
                .build();

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(nameHead);

        TextView detailGroup = (TextView) findViewById(R.id.detailGroup);
        detailGroup.setText(getIntent().getStringExtra("detail"));

        TextView admin = (TextView) findViewById(R.id.admin);
        admin.setText(getIntent().getStringExtra("admin"));

        final TextView date = (TextView) findViewById(R.id.date);
        date.setText(getIntent().getStringExtra("date"));

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        ImageView leaveGroup = (ImageView) findViewById(R.id.leaveGroup);
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Group_detail_page1.this);
                builder.setMessage("ท่านต้องการออกจากกลุ่มหรือไม่");
                builder.setPositiveButton("ออกจากกลุ่ม", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
        });

        LinearLayout addMarker = (LinearLayout) findViewById(R.id.addMarker);
        addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Group_add_marker.class);
                startActivity(intent);
            }
        });

        LinearLayout addPost = (LinearLayout) findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Group_add_post.class);
                startActivity(intent);
            }
        });

        LinearLayout mapZoom = (LinearLayout) findViewById(R.id.mapZoom);
        mapZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentActivity();
            }
        });

        Button activity = (Button) findViewById(R.id.activity);
        activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(getApplicationContext(), Group_add_marker.class);
                startActivity(intent);*/
            }
        });
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
        if (currentLocation != null) {
            moveAnimateCamera(currentLocation);
        }
        if (mGoogleApiClient.isConnected()) {
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
    //  ------------------  Google map -----------------//

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(updatareInterval);
        mLocationRequest.setFastestInterval(fastestInterval);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        addMarkerCurrent(currentLatitude, currentLongitude);
        getDatabase();
    }

    private void addMarkerDatabase(double latitude, double longitude) {

        LatLng latLng = new LatLng(latitude, longitude);
        marker = new MarkerOptions();
        marker.position(latLng);
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    public void addMarkerCurrent(double currentLatitude, double currentLongitude) {
        currentLocation = new LatLng(currentLatitude, currentLongitude);
        marker = new MarkerOptions();
        marker.position(currentLocation).title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_current));
        mMap.addMarker(marker);
        moveAnimateCamera(currentLocation);
    }

    public void moveAnimateCamera(LatLng latLung) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLung).zoom(11).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //  ------------------  Database Mysql -----------------//

    public void getDatabase() {

        if (Control.checkInternet(this)) {

            tempUrl = url + "&sub=" + nameHead + "&ramdom=" + Control.randomNumber();

            if (marker != null) {
                mMap.clear();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, tempUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONArray ja = response.getJSONArray("shared_location");

                                JSONObject jsonObject = null;

                                lat = new ArrayList<Double>();
                                lng = new ArrayList<Double>();
                                locationDetail = new ArrayList<String>();
                                image = new ArrayList<String>();

                                Double tempLat;
                                Double tempLng;

                                for (int i = 0; i < ja.length(); i++) {

                                    jsonObject = ja.getJSONObject(i);

                                    tempLat = Double.parseDouble(jsonObject.getString("lat"));
                                    tempLng = Double.parseDouble(jsonObject.getString("lng"));

                                    addMarkerDatabase(tempLat,tempLng);

                                    lat.add(tempLat);
                                    lng.add(tempLng);
                                    locationDetail.add(jsonObject.getString("location_detail"));
                                    image.add(jsonObject.getString("image"));
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

                        }
                    }
            );
            jor.setShouldCache(false);
            requestQueue.add(jor);
        } else {
            Control.alertInternet(this);
        }
    }

    //  ------------------  zzzzzzzzzzzzzzzz -----------------//

    private void intentActivity() {
        Intent intent = new Intent(getApplicationContext(), Group_detail_page2.class);
        intent.putExtra("name", nameHead);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }
}
