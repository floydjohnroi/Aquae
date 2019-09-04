package com.example.aquae;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FromGeoLocation {

    private static Bundle b;

    public static void getAddress(String locationAddress, Context context) {

                b = new Bundle();

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                Double latitude = null;
                Double longitude = null;

                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (latitude != null && longitude != null) {


                        b.putDouble("latitude", latitude);
                        b.putDouble("longitude", longitude);

                    }
                }
    }

    public static Bundle getFromGeoLocationBundle(){
        return b;
    }
}
