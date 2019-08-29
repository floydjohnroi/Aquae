package com.example.aquae;

import android.os.Bundle;
import android.util.Log;
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

public class RecentActivitiesFragment extends Fragment {

    RecyclerView recyclerView;
    List<WalletModel> walletModelList = new ArrayList<>();
    LinearLayout noActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent_activities, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        noActivity = view.findViewById(R.id.noActivity);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DialogFragment dialogFragment = LoadingScreen.getInstance();

        dialogFragment.show(getChildFragmentManager(), "recent_activities");
        dialogFragment.setCancelable(false);

        FirebaseDatabase.getInstance().getReference().child("cash_ins")
                .orderByChild("customer_id").equalTo(new Session(getContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            noActivity.setVisibility(View.VISIBLE);
                            walletModelList.clear();
                            dialogFragment.dismiss();
                        }
                        else {

                            noActivity.setVisibility(View.GONE);

                            walletModelList.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                walletModelList.add(new WalletModel(
                                        String.valueOf(snapshot.child("amount").getValue()),
                                        String.valueOf(snapshot.child("cash_in_id").getValue()),
                                        String.valueOf(snapshot.child("request_date").getValue()),
                                        String.valueOf(snapshot.child("verified_date").getValue()),
                                        String.valueOf(snapshot.child("receipt").getValue()),
                                        String.valueOf(snapshot.child("status").getValue()),
                                        String.valueOf(snapshot.child("transaction_code").getValue()),
                                        String.valueOf(snapshot.child("payment_date").getValue())

                                ));

                            }

                            recyclerView.setAdapter(new WalletAdapter(getContext(), walletModelList));

                            dialogFragment.dismiss();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        return view;
    }
}
