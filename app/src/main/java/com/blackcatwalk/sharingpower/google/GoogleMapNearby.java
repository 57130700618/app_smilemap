package com.blackcatwalk.sharingpower.google;


import android.content.Context;
import android.os.AsyncTask;

import com.blackcatwalk.sharingpower.CallEmergencyDetail;
import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlProgress;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GoogleMapNearby {

    private Context mContext;
    private int count = 0;
    private int size = 0;
    private final String mSettingGoogle = "&sensor=true&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY&language=th";

    public GoogleMapNearby(Context _context) {
        mContext = _context;
        new PlacesTask().execute(getSb().toString());
    }

    public class PlacesTask extends AsyncTask<String, Integer, String> {

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

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask..
            switch (((CallEmergencyDetail) mContext).getmClassReferrence()) {
                case "CallNearby":
                    new ParserTaskNearby().execute(result);
                    break;
                case "CallDetail":
                    new ParserTaskDetail().execute(result);
                    break;
                default:

            }
        }
    }

    public StringBuilder getSb() {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + ((CallEmergencyDetail) mContext).getmLatitude()
                + "," + ((CallEmergencyDetail) mContext).getmLongitude());
        sb.append("&radius=5000");
        sb.append("&types=" + ((CallEmergencyDetail) mContext).getmType());
        sb.append(mSettingGoogle);
        return sb;
    }

    private String downloadUrl(String s) {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(s);

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
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }


    //--------------------------------------------------------------------------------------


    // A class to parse the Google Places in JSON format
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
                        StringBuilder _sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
                        _sb.append("reference=" + hmPlace.get("reference"));
                        _sb.append(mSettingGoogle);

                        ((CallEmergencyDetail) mContext).setmClassReferrence("CallDetail");

                        // Creating locationSpinnerMinicalGray new non-ui thread task to download Google place details
                        //PlacesTask placesTask = new PlacesTask();
                        // Invokes the "doInBackground()" method of the class PlaceTask
                        new PlacesTask().execute(_sb.toString());
                        size++;
                    }
                } else {
                    ((CallEmergencyDetail) mContext).showAlertDialogNotFoundTelephone();
                    ControlProgress.hideDialog();
                }
            } else {
                ((CallEmergencyDetail) mContext).finish();
            }
        }
    }

    // A class to parse the Google Place Details in JSON format

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

            if (ControlCheckConnect.checkInternet(mContext)) {
                switch (((CallEmergencyDetail) mContext).getmType()) {
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
                    ((CallEmergencyDetail) mContext).mNameList.add(hPlaceDetails.get("name"));
                    ((CallEmergencyDetail) mContext).mTelList.add(hPlaceDetails.get("formatted_phone"));

                    count++;
                } else {
                    size--;
                }

                if (size == count) {
                    if (size != 0) {
                        count = 0;
                        size = 0;
                        ((CallEmergencyDetail) mContext).adapter.notifyDataSetChanged();
                    } else {
                        ((CallEmergencyDetail) mContext).showAlertDialogNotFoundTelephone();
                    }
                    ControlProgress.hideDialog();
                }
            } else {
                ((CallEmergencyDetail) mContext).finish();
            }
        }
    }

}





/*

  public class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = mControl.downloadUrl(url[0]);
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
                    new ParserTaskNearby().execute(result);
                    break;
                case "CallDetail":
                    new ParserTaskDetail().execute(result);
                    break;
            }
        }
    }

public class ParserTaskNearby extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

    JSONObject jObject;

    // Invoked by execute() method of this object
    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;
        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try {
            jObject = new JSONObject(jsonData[0]);

            // Getting the parsed data as a List construct
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
                    GoogleMapNearby.PlacesTask placesTask = new GoogleMapNearby.PlacesTask();
                    // Invokes the "doInBackground()" method of the class PlaceTask
                    placesTask.execute(sb.toString());
                    size++;
                }
            } else {
                showAlertDialogNotFoundTelephone();
                mControl.hideDialog();
            }
        }else {
            finish();
        }
    }
}

// A class to parse the Google Place Details in JSON format

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

        if(mControl.checkInternet(getApplicationContext())){
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
                mNameList.add(hPlaceDetails.get("name"));
                mTelList.add(hPlaceDetails.get("formatted_phone"));

                count++;
            } else {
                size--;
            }

            if (size == count) {
                if (size != 0) {
                    count = 0;
                    size = 0;
                    mAdapter.notifyDataSetChanged();
                } else {
                    showAlertDialogNotFoundTelephone();
                }
                mControl.hideDialog();
            }
        }else{
            finish();
        }
    }
}
*/