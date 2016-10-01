package com.blackcatwalk.sharingpower.google;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.blackcatwalk.sharingpower.CallEmergency;
import com.blackcatwalk.sharingpower.CallEmergencyDetail;
import com.blackcatwalk.sharingpower.utility.ControlCheckConnect;
import com.blackcatwalk.sharingpower.utility.ControlProgress;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GoogleMapNearby {

    private Context mContext;

    private DecimalFormat mDecimalFormat = new DecimalFormat("#.#");
    private double mDistance;
    private int mCount = 0;
    private int mSize = 0;

    private boolean mCheck;

    private Location mCurrentLocation = new Location("CurrentLocation");;
    private Location mLocationB = new Location("point B");

    private StringBuilder mStringBuilder = new StringBuilder();

    private final String mSettingGoogle = "&sensor=true&key=AIzaSyAwJatyU0DGovYEOKYYbDu2KzE0mKEzUPY&language=th";
    private String mType;

    public GoogleMapNearby(Context _context, double _latitude, double _longitude, String _type) {
        mContext = _context;

        mCurrentLocation.setLatitude(_latitude);
        mCurrentLocation.setLongitude(_longitude);

        mType = _type;

        setStringBuilder();
        new PlacesTask().execute(mStringBuilder.toString());
    }

    public class PlacesTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                return downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
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

    public void setStringBuilder() {
        mStringBuilder.setLength(0);
        mStringBuilder.append("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        mStringBuilder.append("location=" + mCurrentLocation.getLatitude()
                + "," + mCurrentLocation.getLongitude());
        mStringBuilder.append("&radius=5000");
        mStringBuilder.append("&types=" + mType);
        mStringBuilder.append(mSettingGoogle);
    }

    private String downloadUrl(String s) {
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(s);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            mStringBuilder.setLength(0);

            String line = "";
            while ((line = br.readLine()) != null) {
                mStringBuilder.append(line);
            }

            br.close();

            return mStringBuilder.toString();
        } catch (Exception e) {
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return "";
    }


    //--------------------------------------------------------------------------------------


    public class ParserTaskNearby extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            if (list != null) {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {

                        HashMap<String, String> hmPlace = list.get(i);
                        mStringBuilder.setLength(0);
                        mStringBuilder.append("https://maps.googleapis.com/maps/api/place/details/json?");
                        mStringBuilder.append("reference=" + hmPlace.get("reference"));
                        mStringBuilder.append(mSettingGoogle);

                        ((CallEmergencyDetail) mContext).setmClassReferrence("CallDetail");

                        new PlacesTask().execute(mStringBuilder.toString());
                        mSize++;
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

    public class ParserTaskDetail extends AsyncTask<String, Integer, HashMap<String, String>> {

        private JSONObject jObject;

        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);
            } catch (Exception e) {
            }
            return hPlaceDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {

            if (ControlCheckConnect.checkInternet(mContext)) {

                mLocationB.setLatitude(Double.parseDouble(hPlaceDetails.get("lat")));
                mLocationB.setLongitude(Double.parseDouble(hPlaceDetails.get("lng")));

                mDecimalFormat.setRoundingMode(RoundingMode.CEILING);
                mDistance = Double.parseDouble(mDecimalFormat.format(mCurrentLocation.distanceTo(mLocationB)/1000));

                mStringBuilder.setLength(0);
                mCheck = false;

                if (mDistance <= 1.0) {
                    if (mDistance == 0.0) {
                        mStringBuilder.append("10 เมตร");
                    } else if (mDistance == 0.1) {
                        mStringBuilder.append("100 เมตร");
                    } else if (mDistance == 0.2) {
                        mStringBuilder.append("200 เมตร");
                    } else if (mDistance == 0.3) {
                        mStringBuilder.append("300 เมตร");
                    } else if (mDistance == 0.4) {
                        mStringBuilder.append("400 เมตร");
                    } else if (mDistance == 0.5) {
                        mStringBuilder.append("500 เมตร");
                    } else if (mDistance == 0.6) {
                        mStringBuilder.append("600 เมตร");
                    } else if (mDistance == 0.7) {
                        mStringBuilder.append("700 เมตร");
                    } else if (mDistance == 0.8) {
                        mStringBuilder.append("800 เมตร");
                    } else if (mDistance == 0.9) {
                        mStringBuilder.append("900 เมตร");
                    } else if (mDistance == 1.0) {
                        mStringBuilder.append("1 กิโลเมตร");
                    }
                } else {
                    mStringBuilder.append(mDistance);
                    mStringBuilder.append(" กิโลเมตร");
                }

                switch (mType) {
                    case "hospital":
                        if (hPlaceDetails.get("name").substring(0, 4).equals("ร.พ.") ||
                                hPlaceDetails.get("name").substring(0, 9).equals("โรงพยาบาล") ||
                                hPlaceDetails.get("name").substring(0, 5).equals("ศูนย์")) {
                            mCheck = true;
                        }
                        break;
                    case "police":
                        if (hPlaceDetails.get("name").substring(0, 5).equals("สถานี") ||
                                hPlaceDetails.get("name").substring(0, 3).equals("สน.") ||
                                hPlaceDetails.get("name").substring(0, 5).equals("ศูนย์")) {
                            mCheck = true;
                        }
                        break;
                }

                if (!hPlaceDetails.get("formatted_phone").substring(0, 1).equals("-") && mCheck == true) {
                    ((CallEmergencyDetail) mContext).mListCallEmergency.add(new CallEmergency(
                            hPlaceDetails.get("name"),hPlaceDetails.get("formatted_phone")
                    ,String.valueOf(mStringBuilder.toString()),mCurrentLocation.distanceTo(mLocationB)));



                    mCount++;
                } else {
                    mSize--;
                }

                if (mSize == mCount) {
                    if (mSize != 0) {
                        mCount = 0;
                        mSize = 0;

                        Collections.sort(((CallEmergencyDetail) mContext).mListCallEmergency
                                , new GoogleMapNearby.SortTypeComparator());

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

    public class SortTypeComparator implements Comparator<CallEmergency> {
        @Override
        public int compare(CallEmergency o1, CallEmergency o2) {
            return Float.compare(o1.getmRealdistance(), o2.getmRealdistance());
        }
    }
}
