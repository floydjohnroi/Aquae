package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackOrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle;
    RecyclerView recyclerView;
    List<TrackOrderModel> trackOrderModelList = new ArrayList<>();
    LinearLayout emptyTrackOrder;
    MaterialButton startShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        emptyTrackOrder = findViewById(R.id.empty_trackorder);
        startShopping = findViewById(R.id.startShopping);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Track Order");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "track_order_activity");

        FirebaseDatabase.getInstance().getReference().child("orders")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trackOrderModelList.clear();
                        emptyTrackOrder.setVisibility(View.VISIBLE);

                        if (emptyTrackOrder.getVisibility() == View.VISIBLE) {
                            dialogFragment.dismiss();
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if ("pending".equals(snapshot.child("status").getValue()) ||
                                    "accepted".equals(snapshot.child("status").getValue()) ||
                                    "processing".equals(snapshot.child("status").getValue()) ||
                                    "delivered".equals(snapshot.child("status").getValue())) {

                                FirebaseDatabase.getInstance().getReference().child("clients")
                                        .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    trackOrderModelList.add(new TrackOrderModel(
                                                            String.valueOf(snapshot.child("order_id").getValue()),
                                                            String.valueOf(snapshot.child("client_id").getValue()),
                                                            String.valueOf(snapshot.child("order_time").getValue()),
                                                            String.valueOf(snapshot.child("status").getValue()),
                                                            String.valueOf(snapshot.child("delivery_address").getValue()),
                                                            String.valueOf(snapshot1.child("company").getValue())
                                                    ));

                                                    recyclerView.setAdapter(new TrackOrderAdapter(TrackOrderActivity.this, trackOrderModelList, "TrackOrder"));
                                                    emptyTrackOrder.setVisibility(View.GONE);
                                                    dialogFragment.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        startShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrackOrderActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
