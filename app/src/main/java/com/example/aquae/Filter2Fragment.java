package com.example.aquae;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Filter2Fragment extends Fragment {

    RecyclerView recyclerView;
    List<ClientModel> clientModelList = new ArrayList<>();
    String isForDelivery;
    ClientAdapter clientAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter2, container, false);
        isForDelivery = null != getArguments() ? getArguments().getString("isForDelivery") : "";
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference()
                .child("clients")
                .orderByChild("status").equalTo("activate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        clientModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

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
                                    ""
                            ));

                        }

                        // ilis ug rate ma sort na ni

                        Collections.sort(clientModelList, new Comparator<ClientModel>() {
                            @Override
                            public int compare(ClientModel o1, ClientModel o2) {
                                return o2.getShip_fee().compareTo(o1.getShip_fee());
                            }
                        });

                        clientAdapter = new ClientAdapter(getContext(), clientModelList, isForDelivery);

                        recyclerView.setAdapter(clientAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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


        return view;
    }
}
