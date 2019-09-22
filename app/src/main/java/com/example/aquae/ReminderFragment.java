package com.example.aquae;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReminderFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ReminderModel> reminderModelList;
    private LinearLayout noReminder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        reminderModelList = new ArrayList<>();
        noReminder = view.findViewById(R.id.no_reminder);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getChildFragmentManager(), "reminder_fragment");

        FirebaseDatabase.getInstance().getReference()
                .child("schedules")
                .orderByChild("customer_id").equalTo(new Session(getContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reminderModelList.clear();
                        if (!dataSnapshot.exists()) {
                            noReminder.setVisibility(View.VISIBLE);
                            dialogFragment.dismiss();
                            recyclerView.setAdapter(new ReminderAdapter(getContext(), reminderModelList));
                        }
                        else {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if ("remind".equals(snapshot.child("status").getValue())) {
                                    FirebaseDatabase.getInstance().getReference().child("clients")
                                            .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                        reminderModelList.add(new ReminderModel(
                                                                String.valueOf(snapshot.child("schedule_id").getValue()),
                                                                String.valueOf(snapshot.child("client_id").getValue()),
                                                                String.valueOf(snapshot1.child("company").getValue()),
                                                                String.valueOf(snapshot.child("remind_time").getValue()),
                                                                String.valueOf(snapshot1.child("morning_pickup").getValue()),
                                                                String.valueOf(snapshot1.child("files").child("store").getValue())
                                                        ));

                                                        recyclerView.setAdapter(new ReminderAdapter(getContext(), reminderModelList));
                                                        noReminder.setVisibility(View.GONE);
                                                        dialogFragment.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                }
                                else {
                                    noReminder.setVisibility(View.VISIBLE);
                                    dialogFragment.dismiss();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        return view;
    }
}
