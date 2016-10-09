package com.blackcatwalk.sharingpower.google;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapAddress {

    public static String getAddress(Context context,double lat,double lng){


        Geocoder geocoder = new Geocoder(context, new Locale("th"));

        if(lat != 0 && lng !=0){
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                    }
                    return strReturnedAddress.toString();
                }
                else {
                    return "-";
                }
            } catch (IOException e) {}
        }
        return "-";
    }
}
