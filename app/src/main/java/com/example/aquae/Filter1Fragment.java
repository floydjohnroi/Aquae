package com.example.aquae;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Filter1Fragment extends Fragment {

    RecyclerView recyclerView;
    //    Query databaseReference;
//    FirebaseRecyclerOptions<ClientModel> options;
//    FirebaseRecyclerAdapter<ClientModel, ClientAdapter> adapter;
    List<ClientModel> clientModelList = new ArrayList<>();
    String isForDelivery;
    ClientAdapter clientAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filter1, container, false);
        isForDelivery = null != getArguments() ? getArguments().getString("isForDelivery") : "";
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(2000);


        LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity())).requestLocationUpdates(mLocationRequest, locationCallback,
                Looper.myLooper());


//        FirebaseDatabase.getInstance().getReference().child("clients").orderByChild("status").equalTo("activate")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        clientModelList.clear();
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                if (data.child("store").getValue() != null) {
//                                    clientModelList.add(new ClientModel(
//                                            String.valueOf(snapshot.child("client_id").getValue()),
//                                            String.valueOf(snapshot.child("company").getValue()),
//                                            String.valueOf(snapshot.child("email").getValue()),
//                                            String.valueOf(snapshot.child("password").getValue()),
//                                            String.valueOf(snapshot.child("address").getValue()),
//                                            String.valueOf(snapshot.child("contact").getValue()),
//                                            String.valueOf(data.child("store").getValue()),
//                                            String.valueOf(snapshot.child("water_type").getValue()),
//                                            String.valueOf(snapshot.child("no_of_filter").getValue()),
//                                            String.valueOf(snapshot.child("shipping_fee").getValue())
//                                    ));
//                                }
//                            }
//                        }
//                        recyclerView.setAdapter(new ClientAdapter(getContext(), clientModelList, isForDelivery));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

        if (!"isForDelivery".equals(isForDelivery)) {
            ((HomeActivity) Objects.requireNonNull(getActivity())).searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    clientAdapter.getFilter().filter(newText);
                    return false;
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    if (dy > 0) {
                        ((HomeActivity) Objects.requireNonNull(getActivity())).floatingActionButton.setVisibility(View.GONE);
                    } else if (dy < 0) {
                        ((HomeActivity) Objects.requireNonNull(getActivity())).floatingActionButton.setVisibility(View.VISIBLE);
                    } else {
                        ((HomeActivity) Objects.requireNonNull(getActivity())).floatingActionButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
//        else {
//
//            ((HomeActivity) Objects.requireNonNull(getActivity())).searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    clientAdapter.getFilter().filter(newText);
//                    return false;
//                }
//            });
//
//        }






        return view;
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

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;
        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        FirebaseDatabase.getInstance().getReference().child("clients")
                .orderByChild("status").equalTo("activate")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        clientModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
                            String origin =  addresses.get(0).getAddressLine(0);
                            String destination =  String.valueOf(snapshot.child("address").getValue());
                            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&avoid=tolls|highways&key=AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY";
                            url = url.replaceAll(" ", "%20");

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

                                String l = "";

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray legs = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                                    for (int i = 0; i < legs.length(); i++) {
                                        JSONObject leg = legs.getJSONObject(i);

                                         l = leg.getJSONObject("distance").getString("text").replace(" km", "");

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (!"".equals(l)) {
                                    String[] m = l.split("\\.");

                                    if (Integer.parseInt(m[0]) < 5) {

                                        clientModelList.add(new ClientModel(
                                                String.valueOf(snapshot.child("client_id").getValue()),
                                                String.valueOf(snapshot.child("company").getValue()),
                                                String.valueOf(snapshot.child("email").getValue()),
                                                String.valueOf(snapshot.child("password").getValue()),
                                                String.valueOf(snapshot.child("address").getValue()),
                                                String.valueOf(snapshot.child("contact").getValue()),
                                                String.valueOf(snapshot.child("files").child("store").getValue()),
                                                String.valueOf(snapshot.child("water_type").getValue()),
                                                String.valueOf(snapshot.child("no_of_filter").getValue()),
                                                String.valueOf(snapshot.child("shipping_fee").getValue()),
                                                String.valueOf(snapshot.child("express_charge").getValue()),
                                                l,
                                                ""
                                        ));

                                        Collections.sort(clientModelList, new Comparator<ClientModel>() {
                                            @Override
                                            public int compare(ClientModel o1, ClientModel o2) {
                                                return o1.getKmAway().compareTo(o2.getKmAway());
                                            }
                                        });

                                        clientAdapter = new ClientAdapter(getContext(), clientModelList, isForDelivery);
                                        recyclerView.setAdapter(clientAdapter);

                                    }

                                }

                            }, error -> Log.d("error", error.toString()));

                            queue.add(stringRequest);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity())).removeLocationUpdates(locationCallback);

    }

}
