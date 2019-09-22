package com.example.aquae;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Filter3Fragment extends Fragment {

    RecyclerView recyclerView;
    List<ClientModel> clientModelList = new ArrayList<>();
    String isForDelivery;
    ClientAdapter clientAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filter3, container, false);
        isForDelivery = null != getArguments() ? getArguments().getString("isForDelivery") : "";
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
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
                                    String.valueOf(snapshot.child("express_charge").getValue()),
                                    "",
                                    ""
                            ));

                        }

                        clientAdapter = new ClientAdapter(getContext(), clientModelList, isForDelivery);
                        recyclerView.setAdapter(clientAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        searchFilter();

        return  view;
    }

    private void searchFilter() {

        if (!"isForDelivery".equals(isForDelivery)) {
            ((HomeActivity) Objects.requireNonNull(getActivity())).searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (clientAdapter != null) {
                        clientAdapter.getFilter().filter(newText);
                    }
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
        else {

            ((SelectStationActivity) Objects.requireNonNull(getActivity())).searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
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

        }

    }
}
