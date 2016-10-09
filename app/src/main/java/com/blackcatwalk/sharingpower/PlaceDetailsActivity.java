package com.blackcatwalk.sharingpower;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcatwalk.sharingpower.google.GoogleNavigator;
import com.blackcatwalk.sharingpower.google.PlaceDetailsJSONParser;
import com.blackcatwalk.sharingpower.utility.Control;
import com.blackcatwalk.sharingpower.utility.ControlDatabase;
import com.blackcatwalk.sharingpower.utility.ControlKeyboard;
import com.blackcatwalk.sharingpower.utility.ControlProgress;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PlaceDetailsActivity extends Activity {

    private ControlDatabase mControlDatabase;
    private String mLat;
    private String mLng;
    private double mSizeInInches;

    // ----------------------- User Intetface ------------
    private WebView mWebView;
    private Button mNavigatorBtn;
    private String mTypeEng = null;
    private String mTypeThai = null;
    private TextView mTypeNameTv;
    private ImageView mFavoriteBtn;
    private ImageView mBackIm;
    private ControlKeyboard mControlKeyboard;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        bindWidget();

        ControlProgress.showProgressDialogDonTouch(this);

        mSizeInInches = new Control().getSizeScrren(this);
        mControlDatabase = new ControlDatabase(this);
        mControlKeyboard = new ControlKeyboard();

        mTypeEng = getIntent().getStringExtra("type");

        setDataNearby();
        setTypeThai();

        mBackIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        mWebView.getSettings().setUseWideViewPort(false);

        mNavigatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GoogleNavigator().showDialogNavigator(PlaceDetailsActivity.this,String.valueOf(mLat),String.valueOf(mLng));
            }
        });

        mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFavorite();
            }
        });
    }

    private void setDataNearby() {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + getIntent().getStringExtra("reference"));
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY");
        sb.append("&language=th");

        // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place details
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sb.toString());
    }

    private void setTypeThai() {
        switch (mTypeEng) {
            case "atm":
                mTypeThai = "เอทีเอ็ม";
                break;
            case "bank":
                mTypeThai = "ธนาคาร";
                break;
            case "bus_station":
                mTypeThai = "ป้ายรถเมล์";
                break;
            case "doctor":
                mTypeThai = "คลีนิค";
                break;
            case "police":
                mTypeThai = "ตำรวจ";
                break;
            case "hospital":
                mTypeThai = "โรงพยาบาล";
                break;
            case "restaurant":
                mTypeThai = "อาหารและเครื่องดื่ม";
                break;
            case "cafe":
                mTypeThai = "คาเฟ่";
                break;
            case "department_store":
                mTypeThai = "ห้างสรรพสินค้า";
                break;
            case "shopping_mall":
                mTypeThai = "ช้อปปิ้งมอลล์";
                break;
            case "grocery_or_supermarket":
                mTypeThai = "ซุปเปอร์มาร์เก็ต";
                break;
            case "beauty_salon":
                mTypeThai = "ร้านเสริมสวย";
                break;
            case "gym":
                mTypeThai = "ยิม";
                break;
            case "post_office":
                mTypeThai = "ที่ทำการไปรษณีย์";
                break;
            case "school":
                mTypeThai = "โรงเรียน";
                break;
            case "university":
                mTypeThai = "มหาวิทยาลัย";
                break;
            case "gas_station":
                mTypeThai = "ปั้มน้ำมันและแก็ส";
                break;
            case "parking":
                mTypeThai = "ที่จอดรถ";
                break;
            case "car_repair":
                mTypeThai = "อู่ซ่อมรถ";
                break;
            case "restroom":
                mTypeThai = "ห้องน้ำ";
                break;
            case "pharmacy":
                mTypeThai = "ร้านขายยา";
                break;
            case "clinic":
                mTypeThai = "คลีนิค";
                break;
            case "veterinary_clinic":
                mTypeThai = "คลีนิครักษาสัตว์";
                break;
            case "daily_home":
                mTypeThai = "ที่พักรายวัน";
                break;
            case "officer":
                mTypeThai = "จัดพักผ่อน";
                break;
            case "garage":
                mTypeThai = "ร้านปะยาง";
                break;
        }
    }

    private void showDialogFavorite() {
        final Dialog _dialog = new Dialog(PlaceDetailsActivity.this);
        _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        _dialog.setContentView(R.layout.activity_dialog_favorite_detail);

        final EditText editDetailName = (EditText) _dialog.findViewById(R.id.editDetailName);

        ImageView btnClose = (ImageView) _dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog.cancel();
            }
        });

        Button save = (Button) _dialog.findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlKeyboard.hideKeyboard(PlaceDetailsActivity.this);
                mControlDatabase.setFavoriteDatabaseLocationDetail(String.valueOf(mLat).toString(),
                        String.valueOf(mLng).toString(), editDetailName.getText().toString(), mTypeEng);
                _dialog.cancel();
            }
        });

        _dialog.show();
    }

     // A class, to download Google Place Details
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) { }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    // A method to download json data from url
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

   // A class to parse the Google Place Details in JSON format
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
            mLat = hPlaceDetails.get("lat");
            mLng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            //String website = hPlaceDetails.get("website");
            //String url = hPlaceDetails.get("url");

            String mimeType = "text/html";
            String encoding = "utf-8";

            String data ;

            if (mSizeInInches >= 9.5) {
                data = "<html>" +
                        "<body><h1><center>" + name + "</center></h1>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + mLat + "," + mLng + "</p>" +
                        "<h3><p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br></h3>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";
            } else if (mSizeInInches >= 6.5) {
                data = "<html>" +
                        "<body><h1><center>" + name + "</center></h1>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + mLat + "," + mLng + "</p>" +
                        "<h3><p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br></h3>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";
            } else {
                data = "<html>" +
                        "<body><h2><center>" + name + "</center></h2>" +
                        //"<hr />" +
                        //"<p>LocationGps : " + mLat + "," + mLng + "</p>" +
                        "<p>ที่อยู่: " + formatted_address + "</p>" +
                        "<p>เบอร์โทรศัพท์: " + formatted_phone + "</p><br>" +
                        //"<p>เว็บไซค์ : " + website + "</p>" +

                        //"<p>เปิดการนำเส้นทางได้ที่ By Google MAP : <a href='" + url + "'>" + url + "</p>" +
                        "</body></html>";

            }

            // Setting the data in WebView
            mWebView.loadDataWithBaseURL("", data, mimeType, encoding, "");
            mTypeNameTv.setText("> "+ mTypeThai);

            ControlProgress.hideDialog();
        }
    }

    private void bindWidget() {
        mBackIm = (ImageView) findViewById(R.id.backIm);
        mFavoriteBtn = (ImageView) findViewById(R.id.favoriteBtn);
        mTypeNameTv = (TextView) findViewById(R.id.typeNameTv);
        mWebView = (WebView) findViewById(R.id.webView);
        mNavigatorBtn = (Button) findViewById(R.id.navigatorBtn);
    }
}