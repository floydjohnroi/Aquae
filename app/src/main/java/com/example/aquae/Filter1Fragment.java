package com.example.aquae;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Filter1Fragment extends Fragment {

    RecyclerView recyclerView;
//    Query databaseReference;
//    FirebaseRecyclerOptions<ClientModel> options;
//    FirebaseRecyclerAdapter<ClientModel, ClientAdapter> adapter;
    List<ClientModel> clientModelList = new ArrayList<>();
    String isForDelivery;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_filter1, container, false);
        isForDelivery = null != getArguments() ? getArguments().getString("isForDelivery") : "" ;
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        FirebaseDatabase.getInstance().getReference().child("clients").orderByChild("status").equalTo("activate")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        clientModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                if (data.child("store").getValue() != null) {
                                    clientModelList.add(new ClientModel(
                                            String.valueOf(snapshot.child("client_id").getValue()),
                                            String.valueOf(snapshot.child("company").getValue()),
                                            String.valueOf(snapshot.child("email").getValue()),
                                            String.valueOf(snapshot.child("password").getValue()),
                                            String.valueOf(snapshot.child("address").getValue()),
                                            String.valueOf(snapshot.child("contact").getValue()),
                                            String.valueOf(data.child("store").getValue()),
                                            String.valueOf(snapshot.child("water_type").getValue()),
                                            String.valueOf(snapshot.child("no_of_filter").getValue()),
                                            String.valueOf(snapshot.child("shipping_fee").getValue())
                                    ));
                                }
                            }
                        }
                        recyclerView.setAdapter(new ClientAdapter(getContext(), clientModelList, isForDelivery));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return view;
    }
}
