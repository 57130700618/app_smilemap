package com.blackcatwalk.sharingpower;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MyService extends Service {

    private MyThread mThread;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long mUpdatareInterval = 20000;  // 1minute = 60000 milesecond
    private long uFastestInterval = 8000;
    private double mCurrentLatitude;
    private double mCurrentLongitude;

    //---------------- Data to/form Database ----------------
    private String mUsername;
    private String mType = "";
    private String mName = "";
    private String mCategory = "";
    private String mBusFree = "";
    private String mDetailBus = "";
    private String mColorMarker = "";
    private String mAmountPerson = "";
    private int mCountPoint = 0;

    @Override
    public void onCreate() {
        mUsername = Control.getUsername(this);
        getDatabase();
        mThread = new MyThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mThread.isAlive()) {
            mThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mThread.mFinish = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------

    private class MyThread extends Thread implements LocationListener {

        private static final int mDelay = 20000; // set time 20วิ
        private static final int mNoID = 0;
        private boolean mFinish = false;
        private NotificationCompat.Builder mNotifyBuilder;
        private NotificationManager mNotifyManager;
        private int mEndTime = 0;
        private int mCurrentTime = 0;
        private int mCountDontConnectGpsInternet = 0;

        public void run() {

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                        }
                    })
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            startLocationUpdates();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .build();

            mGoogleApiClient.connect();

            mEndTime = (Calendar.getInstance().get(Calendar.HOUR) * 60) + Calendar.getInstance().get(Calendar.MINUTE);
            mEndTime += 20; // + 20minute

            while (true) {
                try {
                    sleep(mDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mCurrentTime = (Calendar.getInstance().get(Calendar.HOUR) * 60) + Calendar.getInstance().get(Calendar.MINUTE);

                if (mEndTime == mCurrentTime) {
                    mEndTime = 0;
                    mEndTime = (Calendar.getInstance().get(Calendar.HOUR) * 60) + Calendar.getInstance().get(Calendar.MINUTE);
                    mEndTime += 20; // + 20minute

                    notification("ท่านกำลังแชร์ตำแหน่งการจราจรอยู่", "ทีมงานขอกราบขอบพระคุณจิตสาธารณะในครั้งนี้");
                }

                if (!Control.checkInternet(getApplicationContext()) || !Control.checkGPS(getApplicationContext())) {
                    mCountDontConnectGpsInternet++;
                    if (mCountDontConnectGpsInternet > 8) {
                        mCountDontConnectGpsInternet = 0;
                        notification("ตรวจสอบการแชร์ตำแหน่ง", "ท่านปิดการเชื่อมต่อเครือข่ายในขณะที่แชร์ตำแหน่งอยู่ กรุณาตรวจสอบการแชร์ตำแหน่งของท่าน");
                    }
                } else {
                    mCountDontConnectGpsInternet = 0;
                }

                if (mFinish) {
                    if (mGoogleApiClient.isConnected()) {
                        mType = "";
                        mName = "";
                        mCategory = "";
                        mBusFree = "";
                        mDetailBus = "";
                        mColorMarker = "";
                        mAmountPerson = "";

                        stopLocationUpdate();
                        mGoogleApiClient.disconnect();
                    }
                    return;
                }
            }
        }

        private void notification(String title, String detail) {

            Intent _intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent _pIntent = PendingIntent.getActivity(MyService.this, 0, _intent, 0);

            Bitmap _largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

            try {
                Uri _notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone _ringtone = RingtoneManager.getRingtone(getApplicationContext(), _notification);
                _ringtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mNotifyBuilder = new NotificationCompat.Builder(MyService.this)
                    .setTicker("Smile Map")
                    .setContentTitle(title)
                    .setContentText(detail)
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
                    .setLargeIcon(_largeIcon)
                    .setContentIntent(_pIntent)
                    .setAutoCancel(true);

            Notification _notification = mNotifyBuilder.build();

            mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyManager.notify(mNoID, _notification);
        }

        @SuppressWarnings({"MissingPermission"})
        protected void startLocationUpdates() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(mUpdatareInterval);
            mLocationRequest.setFastestInterval(uFastestInterval);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        private void stopLocationUpdate() {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLatitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();

            if (!mType.equals("")) {
                setDatabase();
            } else {
                getDatabase();
            }
        }
    }

    //----------------------------- Database ------------------------------

    public void getDatabase() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Control.getMGetDatabase()
                + "game&sub=" + mUsername + "&ramdom=" + Control.randomNumber(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray _ja = response.getJSONArray("temp");

                            for (int i = 0; i < _ja.length(); i++) {
                                JSONObject jsonObject = _ja.getJSONObject(i);

                                mUsername = jsonObject.getString("email");
                                mName = jsonObject.getString("name");
                                mType = jsonObject.getString("type");
                                mCategory = jsonObject.getString("category");
                                mBusFree = jsonObject.getString("bus_free");
                                mDetailBus = jsonObject.getString("bus_detail");
                                mAmountPerson = jsonObject.getString("amount_person");
                                mColorMarker = jsonObject.getString("color");
                            }
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
    }

    public void setDatabase() {

        if (mThread.mFinish == false) {

            RequestQueue _requestQueue = Volley.newRequestQueue(this);
            StringRequest _jor = new StringRequest(Request.Method.POST, Control.getMSetDatabase(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mCountPoint = 1;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("main", "bus");
                    params.put("username", mUsername);
                    params.put("lat", Double.toString(mCurrentLatitude));
                    params.put("lng", Double.toString(mCurrentLongitude));
                    params.put("name", mName);
                    params.put("type", mType);
                    params.put("category", mCategory);
                    params.put("amount_person", mAmountPerson);
                    params.put("bus_free", mBusFree);
                    params.put("bus_detail", mDetailBus);
                    params.put("color", mColorMarker);
                    params.put("point", String.valueOf(mCountPoint));
                    params.put("minute", String.valueOf(mCountPoint));

                    return params;
                }
            };
            _jor.setShouldCache(false);
            _requestQueue.add(_jor);
        }
    }
}




