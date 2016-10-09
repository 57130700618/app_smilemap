package com.blackcatwalk.sharingpower.google;

import android.content.Context;
import android.os.AsyncTask;

import com.blackcatwalk.sharingpower.BusGps;
import com.blackcatwalk.sharingpower.LocationGps;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class GoogleMapCalculateDistanceDuration {

    private final int mType;
    private Context mContext;

    public GoogleMapCalculateDistanceDuration(Context _context, int _type) {
        this.mContext = _context;
        this.mType = _type;
    }

    public void getDuration(LatLng _currentLocation, LatLng _position) {
        new DownloadTask().execute(getDirectionsUrl(_currentLocation, _position));
    }

    public String getDirectionsUrl(LatLng _origin, LatLng _dest) {

        StringBuilder _stringBuilder = new StringBuilder();
        ;

        _stringBuilder.append("https://maps.googleapis.com/maps/api/directions/" + "json" + "?");
        _stringBuilder.append("origin=" + _origin.latitude + "," + _origin.longitude);
        _stringBuilder.append("&destination=" + _dest.latitude + "," + _dest.longitude);
        _stringBuilder.append("&sensor=false");

        return _stringBuilder.toString();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String _data = "";
            try {
                _data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return _data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                new ParserTask().execute(result);
            }
        }
    }

    public String downloadUrl(String strUrl) throws IOException {
        String _data = "";
        InputStream _iStream = null;
        HttpURLConnection _urlConnection = null;
        try {
            URL url = new URL(strUrl);

            _urlConnection = (HttpURLConnection) url.openConnection();
            _urlConnection.connect();

            _iStream = _urlConnection.getInputStream();

            BufferedReader _bufferedReader = new BufferedReader(new InputStreamReader(_iStream));

            StringBuffer _stringBuffer = new StringBuffer();

            String _line = "";
            while ((_line = _bufferedReader.readLine()) != null) {
                _stringBuffer.append(_line);
            }

            _data = _stringBuffer.toString();

            _bufferedReader.close();

        } catch (Exception e) {
        } finally {
            _iStream.close();
            _urlConnection.disconnect();
        }
        return _data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject _jObject;
            List<List<HashMap<String, String>>> _routes = null;

            try {
                _jObject = new JSONObject(jsonData[0]);
                _routes = new JsonParserDirections().parse(_jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return _routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            if (result == null) {
                return;
            }

            String _duration = "";

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        continue;
                    } else if (j == 1) {
                        _duration = (String) point.get("duration");
                        break;
                    }
                }

                int _durationLength = _duration.length();
                if (_durationLength > 7) {
                    int _temp;
                    String temp = "";

                    _temp = _duration.indexOf("r");
                    temp = _duration.substring(_temp + 2, _durationLength - 4);

                    _temp = _duration.indexOf("h");
                    _temp = _temp - 1;
                    _duration = _duration.substring(0, _temp);
                    _duration = _duration + " ชม. ";

                    _duration = _duration + temp + "น.";
                } else {
                    _durationLength -= 4;
                    _duration = _duration.substring(0, _durationLength);
                    _duration = _duration + " นาที";
                }

                switch (mType) {
                    case 0:
                        ((LocationGps) mContext).showDuration(_duration);
                        break;
                    case 1:
                        ((BusGps) mContext).showDuration(_duration);
                        break;
                }
            }
        }
    }
}
