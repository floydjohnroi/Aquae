package com.example.aquae;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private List<ScheduleModel> scheduleModelList = new ArrayList<>();
    private LinearLayout noSchedule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        noSchedule = view.findViewById(R.id.no_schedule);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getChildFragmentManager(), "schedule_fragment");

        floatingActionButton.setOnClickListener(v ->
            startActivity(new Intent(getContext(), SelectStationActivity.class))
        );

        FirebaseDatabase.getInstance().getReference().child("schedules")
            .orderByChild("customer_id").equalTo(new Session(getContext()).getId())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    scheduleModelList.clear();
                    if (!dataSnapshot.exists()) {
                        noSchedule.setVisibility(View.VISIBLE);
                        dialogFragment.dismiss();
                    }
                    else {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if ("scheduled".equals(snapshot.child("status").getValue())
                                || "remind".equals(snapshot.child("status").getValue())) {

//                                FirebaseDatabase.getInstance().getReference().child("clients")
//                                        .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    scheduleModelList.add(new ScheduleModel(
                                                            String.valueOf(snapshot.child("schedule_id").getValue()),
                                                            String.valueOf(snapshot.child("client_id").getValue()),
                                                            String.valueOf(snapshot.child("schedule").getValue()),
                                                            String.valueOf(snapshot.child("switch").getValue())
                                                    ));
//                                                }

//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });

                            }
                            else {
                                noSchedule.setVisibility(View.VISIBLE);
                                dialogFragment.dismiss();
                            }
                        }

                        recyclerView.setAdapter(new ScheduleAdapter(getContext(), scheduleModelList));
                        noSchedule.setVisibility(View.GONE);
                        dialogFragment.dismiss();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    floatingActionButton.setVisibility(View.GONE);
                } else if (dy < 0) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}
