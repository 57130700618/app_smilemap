package com.blackcatwalk.sharingpower;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

public class Group_detail_page2 extends AppCompatActivity implements LocationListener {

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

    private ArrayList<Double> lat;
    private ArrayList<Double> lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail_page2);
        getSupportActionBar().hide();

        Control.sDialog(this);

        lat = new ArrayList<Double>();
        lng = new ArrayList<Double>();

        lat = (ArrayList<Double>) getIntent().getSerializableExtra("lat");
        lng = (ArrayList<Double>) getIntent().getSerializableExtra("lng");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        mMap = googleMap;

                        LatLng bangkok = new LatLng(14.76553, 100.53904);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangkok, 8));
                        mMap.getUiSettings().setCompassEnabled(false);

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                return true;
                            }
                        });

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

                            if (marker != null) {
                                mMap.clear();
                            }
                            addMarkerCurrent(currentLatitude, currentLongitude);
                            addMarkerDatabase();
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

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        ImageView main = (ImageView) findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            }
        });

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("name"));

        de.hdodenhof.circleimageview.CircleImageView btnCureentLocation = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.btnCureentLocation);
        btnCureentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation != null) {
                    moveAnimateCamera(currentLocation);
                } else {
                    Control.alertCurrentGps(getApplication());
                }
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (marker != null) {
            mMap.clear();
        }
        addMarkerCurrent(currentLatitude, currentLongitude);
        addMarkerDatabase();
    }

    private void addMarkerDatabase() {
        for (int i = 0; i < lat.size(); i++) {
            LatLng latLng = new LatLng(lat.get(i), lng.get(i));
            marker = new MarkerOptions();
            marker.position(latLng);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(marker);
        }
        Control.hDialog();
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
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLung).zoom(13).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
