package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PlaceDetailsActivity extends Activity {

    private WebView mWvPlaceDetails;
    private Button btnNavigation;
    private String temp = null;
    private String type = null;
    private String tempType = null;
    private TextView typeName;

    // ----------------- Url to database ------------------//
    private String set = "https://www.smilemap.me/android/set.php";

    // ----------- FIle System --------------//
    private static final String fileUserName = "Username.txt";
    private static final int readSize = 100; // Read 100byte


    private String lat;
    private String lng;

    private String username;

    private double sizeInInches;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        sizeInInches = Control.getSizeScrren(this);

        Control.sDialog(this);

        // Getting place reference from the map
        String reference = getIntent().getStringExtra("reference");
        type = getIntent().getStringExtra("type");

        typeName = (TextView) findViewById(R.id.typeName);

        switch (type) {
            case "atm":
                tempType = "เอทีเอ็ม";
                break;
            case "bank":
                tempType = "ธนาคาร";
                break;
            case "bus_station":
                tempType = "ป้ายรถเมล์";
                break;
            case "doctor":
                tempType = "คลีนิค";
                break;
            case "police":
                tempType = "ตำรวจ";
                break;
            case "hospital":
                tempType = "โรงพยาบาล";
                break;
            case "restaurant":
                tempType = "อาหารและเครื่องดื่ม";
                break;
            case "cafe":
                tempType = "คาเฟ่";
                break;
            case "department_store":
                tempType = "ห้างสรรพสินค้า";
                break;
            case "shopping_mall":
                tempType = "ช้อปปิ้งมอลล์";
                break;
            case "grocery_or_supermarket":
                tempType = "ซุปเปอร์มาร์เก็ต";
                break;
            case "beauty_salon":
                tempType = "ร้านเสริมสวย";
                break;
            case "gym":
                tempType = "ยิม";
                break;
            case "post_office":
                tempType = "ที่ทำการไปรษณีย์";
                break;
            case "school":
                tempType = "โรงเรียน";
                break;
            case "university":
                tempType = "มหาวิทยาลัย";
                break;
            case "gas_station":
                tempType = "ปั้มน้ำมันและแก็ส";
                break;
            case "parking":
                tempType = "ที่จอดรถ";
                break;
            case "car_repair":
                tempType = "อู่ซ่อมรถ";
                break;
            case "restroom":
                tempType = "ห้องน้ำ";
                break;
            case "pharmacy":
                tempType = "ร้านขายยา";
                break;
            case "clinic":
                tempType = "คลีนิค";
                break;
            case "veterinary_clinic":
                tempType = "คลีนิครักษาสัตว์";
                break;
            case "daily_home":
                tempType = "ที่พักรายวัน";
                break;
            case "officer":
                tempType = "จัดพักผ่อน";
                break;
            case "garage":
                tempType = "ร้านปะยาง";
                break;
        }

        ImageView btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        mWvPlaceDetails = (WebView) findViewById(R.id.wv_place_details);
        mWvPlaceDetails.getSettings().setUseWideViewPort(false);

        btnNavigation = (Button) findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] lists = {"ขับรถ", "เดิน"};
                new AlertDialog.Builder(PlaceDetailsActivity.this).setTitle("เลือกประเภทการเดินทาง").setItems(lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String parse = "google.navigation:q=" + lat + "," + lng;

                        switch (which) {
                            case 0:
                                temp = parse + "&mode=d";
                                break;
                            case 1:
                                temp = parse + "&mode=w";
                                break;
                        }
                        dialog.cancel();

                        AlertDialog.Builder buildercheck = new AlertDialog.Builder(PlaceDetailsActivity.this);
                        buildercheck.setTitle(Html.fromHtml("<b>" + "โปรดตรวจสอบว่าท่านไม่ได้อยู่จุดอับสัญญาณ" + "</b>"));
                        buildercheck.setMessage("เมนูนำทางอาจใช้เวลาในการโหลดข้อมูลนาน เมื่อท่านอยู่จุดอับสัญญาณ");
                        buildercheck.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Uri gmmIntentUri = Uri.parse(temp);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = buildercheck.create();
                        alert.show();
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.parseColor("#147cce"));
                        pbutton.setTypeface(null, Typeface.BOLD);
                    }
                }).show();
            }
        });

        ImageView favoriteBtn = (ImageView) findViewById(R.id.favoriteBtn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(PlaceDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_dialog_favorite_detail);

                final EditText editDetailName = (EditText) dialog.findViewById(R.id.editDetailName);

                ImageView btnClose = (ImageView) dialog.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Button save = (Button) dialog.findViewById(R.id.btnSave);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            FileInputStream fIn = openFileInput(fileUserName);
                            InputStreamReader reader = new InputStreamReader(fIn);

                            char[] buffer = new char[readSize];
                            username = "";
                            int charReadCount;
                            while ((charReadCount = reader.read(buffer)) > 0) {
                                String readString = String.copyValueOf(buffer, 0, charReadCount);

                                username += readString;
                                buffer = new char[readSize];
                            }
                            reader.close();

                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }

                        final String detailName = editDetailName.getText().toString();

                        if (Control.checkInternet(PlaceDetailsActivity.this)) {

                            RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
                            StringRequest jor = new StringRequest(Request.Method.POST, set,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(getApplicationContext(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), "บันทึกไม่สำเร็จ", Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() {

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("main", "favorite");
                                    params.put("job", "savefavorite");
                                    params.put("username", username);
                                    params.put("lat", lat);
                                    params.put("lng", lng);
                                    params.put("detailnew", detailName);
                                    params.put("type", type);

                                    return params;
                                }
                            };
                            jor.setShouldCache(false);
                            requestQueue.add(jor);

                            dialog.cancel();
                        } else {
                            Control.hDialog();
                            Control.alertCurrentInternet(PlaceDetailsActivity.this);
                        }
                        dialog.cancel();
                    }
                });

                dialog.show();


            }
        });

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + reference);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY");
        sb.append("&language=th");

        // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place details
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
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

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class, to download Google Place Details
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {

            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Place Details in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

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

            String name = hPlaceDetails.get("name");
            //String vicinity = hPlaceDetails.get("vicinity");
            lat = hPlaceDetails.get("lat");
            lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            //String website = hPlaceDetails.get("website");
            //String url = hPlaceDetails.get("url");

            String mimeType = "text/html";
            String encoding = "utf-8";

            String data ;

            if (sizeInInches >= 9.5) {
                data = "<html>" +
                        "<body><h1><center>" + name + "</center></h1>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + lat + "," + lng + "</p>" +
                        "<h3><p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br></h3>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";
            } else if (sizeInInches >= 6.5) {
                data = "<html>" +
                        "<body><h1><center>" + name + "</center></h1>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + lat + "," + lng + "</p>" +
                        "<h3><p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br></h3>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";
            } else {
                data = "<html>" +
                        "<body><h2><center>" + name + "</center></h2>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + lat + "," + lng + "</p>" +
                        "<p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";

            }

            // Setting the data in WebView
            mWvPlaceDetails.loadDataWithBaseURL("", data, mimeType, encoding, "");
            typeName.setText("> "+tempType);

            Control.hDialog();
        }
    }
}