package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle;
    RecyclerView recyclerView;
    List<TrackOrderModel> trackOrderModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Order History");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("orders")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trackOrderModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if ("delivered".equals(snapshot.child("status").getValue()) ||
                                    "declined".equals(snapshot.child("status").getValue())) {
                                trackOrderModelList.add(new TrackOrderModel(
                                        String.valueOf(snapshot.child("order_id").getValue()),
                                        String.valueOf(snapshot.child("client_id").getValue()),
                                        String.valueOf(snapshot.child("order_time").getValue()),
                                        String.valueOf(snapshot.child("status").getValue()),
                                        String.valueOf(snapshot.child("delivery_address").getValue())
                                ));

                            }
                        }
                        recyclerView.setAdapter(new TrackOrderAdapter(OrderHistoryActivity.this, trackOrderModelList, "OrderHistory"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
