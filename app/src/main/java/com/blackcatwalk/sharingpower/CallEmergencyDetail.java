package com.blackcatwalk.sharingpower;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallEmergencyDetail extends AppCompatActivity {

    // ----------------- Google_Map -------------------//
    private GoogleApiClient mGoogleApiClient;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private Location mLastLocation;

    private int count = 0;
    private int size = 0;
    private String classReferrence = null;

    private ListView listView;
    private List<String> nameList = new ArrayList<String>();
    private List<String> telList = new ArrayList<String>();

    private String type;

    private CallEmergencyDetailCustomListAdapter adapter;

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_emergency_detail);
        getSupportActionBar().hide();

        sDialog();

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");

        TextView headName = (TextView) findViewById(R.id.headName);

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView1);

        adapter = new CallEmergencyDetailCustomListAdapter(CallEmergencyDetail.this, nameList, telList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telList.get(position)));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(CallEmergencyDetail.this);
                builder.setMessage(nameList.get(position));

                builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#147cce"));
                pbutton.setTypeface(null, Typeface.BOLD);

                return true;
            }
        });

        if (type.equals("general")) {
            headName.setText("สายด่วน แจ้งเหตุ");

            nameList.add("แจ้งเหตุด่วน-เหตุร้ายทุกชนิด");
            telList.add("191");

            nameList.add("หน่วยแพทย์ฉุกเฉิน(ทั่วไทย)");
            telList.add("1669");

            nameList.add("หน่วยแพทย์ฉุกเฉิน(กทม.)");
            telList.add("1646");

            nameList.add("หน่วยกู้ชีพ วชิรพยาบาล");
            telList.add("1554");

            //---------------------------------------

            nameList.add("กรมเจ้าท่า, เหตุด่วนทางน้ำ");
            telList.add("1199");

            nameList.add("สายด่วนตำรวจท่องเที่ยว");
            telList.add("1155");

            nameList.add("สายด่วนทางหลวง");
            telList.add("1586");

            nameList.add("รับแจ้งอัคคีภัย สัตว์เข้าบ้าน");
            telList.add("199");

            nameList.add("รับแจ้งรถหาย, ถูกขโมย");
            telList.add("1192");

            nameList.add("ศูนย์เตือนภัยพิบัติแห่งชาติ");
            telList.add("192");

            nameList.add("ศูนย์ควบคุมและสั่งการจราจร");
            telList.add("1197");

            //---------------------------------------

            nameList.add("สถานีวิทยุร่วมด้วยช่วยกัน");
            telList.add("1677");

            nameList.add("สถานีวิทยุ จส.100");
            telList.add("1586");

            adapter.notifyDataSetChanged();
            Control.hDialog();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected( Bundle bundle) {
                            mLastLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient);

                            if (mLastLocation != null) {

                                currentLatitude = mLastLocation.getLatitude();
                                currentLongitude = mLastLocation.getLongitude();

                                classReferrence = "CallNearby";

                                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                                sb.append("location=" + currentLatitude + "," + currentLongitude);
                                sb.append("&radius=5000");
                                sb.append("&types=" + type);
                                sb.append("&sensor=true");
                                sb.append("&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY");
                                sb.append("&language=th");

                                // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place json data
                                PlacesTask placesTask = new PlacesTask();
                                // Invokes the "doInBackground()" method of the class PlaceTask
                                placesTask.execute(sb.toString());
                            } else {
                                Control.hDialog();
                                final Dialog dialog = new Dialog(CallEmergencyDetail.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.activity_dialog_current_gps);
                                dialog.setCancelable(false);

                                Button btnClose = (Button) dialog.findViewById(R.id.btnClose);
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                                dialog.show();
                            }
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
                            Control.alertCurrentGps(CallEmergencyDetail.this);
                        }
                    })
                    .build();

            mGoogleApiClient.connect();

            if (type.equals("hospital")) {
                headName.setText("โรงพยาบาลโดยรอบ");
            } else {
                headName.setText("สถานีตำรวจโดยรอบ");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    public void sDialog() {
        Control.pDialog = new ProgressDialog(this,R.style.MyTheme);
        Control.pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        Control.pDialog.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.aaaaaaaaaaaaaaa));
        Control.pDialog.setCancelable(false);
        Control.pDialog.show();
    }

    private void alertBox() {

        String tempType = null;

        switch (type) {
            case "hospital":
                tempType = "โรงพยาบาล";
                break;
            case "police":
                tempType = "สถานีตำรวจ";
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ไม่พบข้อมูลเบอร์โทรติดต่อของ" + tempType + " รอบๆตัวท่าน");
        builder.setCancelable(false);

        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#147cce"));
        pbutton.setTypeface(null, Typeface.BOLD);
    }


    //----------------------------------- get Data ----------------------------------------------

    /**
     * A class, to download Google Places
     */
    public class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = Control.downloadUrl(url[0]);
            } catch (Exception e) {

            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask..
            switch (classReferrence) {
                case "CallNearby":
                    ParserTaskNearby ParserTaskNearby = new ParserTaskNearby();
                    ParserTaskNearby.execute(result);
                    break;
                case "CallDetail":
                    ParserTaskDetail ParserTaskDetail = new ParserTaskDetail();
                    ParserTaskDetail.execute(result);
                    break;
            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTaskNearby extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            if (list != null) {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {

                        // Getting a place from the places list
                        HashMap<String, String> hmPlace = list.get(i);
                        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
                        sb.append("reference=" + hmPlace.get("reference"));
                        sb.append("&sensor=true");
                        sb.append("&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY");
                        sb.append("&language=th");

                        classReferrence = "CallDetail";

                        // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place details
                        PlacesTask placesTask = new PlacesTask();
                        // Invokes the "doInBackground()" method of the class PlaceTask
                        placesTask.execute(sb.toString());
                        size++;
                    }
                } else {
                    alertBox();
                    Control.hDialog();
                }
            }else {
                finish();
            }
        }
    }

    /**
     * A class to parse the Google Place Details in JSON format
     */
    public class ParserTaskDetail extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;
        private boolean check = false;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            } catch (Exception e) {
            }
            return hPlaceDetails;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {

            if(Control.checkInternet(getApplicationContext())){
                switch (type) {
                    case "hospital":
                        if (hPlaceDetails.get("name").substring(0, 4).equals("ร.พ.") ||
                                hPlaceDetails.get("name").substring(0, 9).equals("โรงพยาบาล") ||
                                hPlaceDetails.get("name").substring(0, 5).equals("ศูนย์")) {
                            check = true;
                        }
                        break;
                    case "police":
                        if (hPlaceDetails.get("name").substring(0, 5).equals("สถานี") ||
                                hPlaceDetails.get("name").substring(0, 3).equals("สน.") ||
                                hPlaceDetails.get("name").substring(0, 5).equals("ศูนย์")) {
                            check = true;
                        }
                        break;
                }

                if (!hPlaceDetails.get("formatted_phone").substring(0, 1).equals("-") && check == true) {
                    nameList.add(hPlaceDetails.get("name"));
                    telList.add(hPlaceDetails.get("formatted_phone"));

                    count++;
                } else {
                    size--;
                }

                if (size == count) {
                    if (size != 0) {
                        count = 0;
                        size = 0;
                        adapter.notifyDataSetChanged();
                    } else {
                        alertBox();
                    }
                    Control.hDialog();
                }
            }else{
                finish();
            }
        }
    }
}
