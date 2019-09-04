package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;

    private List<CoordinatesModel> coordinatesModelList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        getCoordinates();

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            gMap.getUiSettings().setZoomControlsEnabled(true);
            gMap.getUiSettings().setCompassEnabled(true);
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setAllGesturesEnabled(true);
            gMap.setTrafficEnabled(true);
            startLocationUpdates();
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity())).requestLocationUpdates(mLocationRequest, locationCallback,
                Looper.myLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            try {
                onLocationChanged(locationResult.getLastLocation());
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onLocationResult(locationResult);
        }
    };

    private void onLocationChanged(Location location) throws IOException {
        gMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        gMap.addMarker(new MarkerOptions().position(latLng).title("Current"));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

        for (CoordinatesModel coordinatesModel: coordinatesModelList) {
            LatLng target = getLocationFromAddress(getContext(), coordinatesModel.getAddress());
            Location targetLocation = new Location("");
            String markerData = coordinatesModel.getName().concat(",").concat(coordinatesModel.getWaterType());
            if (target != null) {
                targetLocation.setLatitude(target.latitude);
                targetLocation.setLongitude(target.longitude);
                gMap.addMarker(new MarkerOptions().position(target).title(markerData));
                if (location.distanceTo(targetLocation) < (float) 1000) {
                    gMap.addMarker(new MarkerOptions().position(target).title(markerData));
                }
            }
        }

        LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity())).removeLocationUpdates(locationCallback);
    }

    private LatLng getLocationFromAddress(Context ctx, String address) throws IOException {
        Geocoder geocoder = new Geocoder(ctx);
        List<Address> addressList;
        LatLng latLng = null;
        addressList = geocoder.getFromLocationName(address, 5);
        if (addressList == null) {
            return null;
        }

        Address location = addressList.get(0);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        return latLng;
    }

    private void getCoordinates() {

        FirebaseDatabase.getInstance().getReference().child("clients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            coordinatesModelList.add(new CoordinatesModel(
                                   String.valueOf(snapshot.child("company").getValue()),
                                   String.valueOf(snapshot.child("water_type").getValue()),
                                   String.valueOf(snapshot.child("address").getValue())
                            ));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}

