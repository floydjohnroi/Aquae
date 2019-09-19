package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DeliveryScheduleActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, today;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    List<ScheduleModel> scheduleModelList = new ArrayList<>();
    LinearLayout noSchedule;
    ScheduleAdapter scheduleAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        floatingActionButton = findViewById(R.id.floatingActionButton);
//        today = findViewById(R.id.today);
        recyclerView = findViewById(R.id.recyclerView);
//        noSchedule = findViewById(R.id.no_schedule);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Delivery Schedule");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.drawerContent, new DeliveryScheduleFragment()).commit();
        }

//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        today.setText(new SimpleDateFormat("E, MMM dd", Locale.getDefault()).format(new Date()));
//
//        Thread thread = new Thread() {
//
//            @Override
//            public void run() {
//                while (!isInterrupted()) {
//                    try {
//                        Thread.sleep(1000);
//
//                        runOnUiThread(() -> today.setText(new SimpleDateFormat("E, MMM dd", Locale.getDefault()).format(new Date())));
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        thread.start();
//
//        floatingActionButton.setOnClickListener(v -> {
//            startActivity(new Intent(DeliveryScheduleActivity.this, SelectStationActivity.class));
//        });
//
//        DialogFragment dialogFragment = LoadingScreen.getInstance();
//        dialogFragment.show(getSupportFragmentManager(), "delivery_schedule_activity");
//
//        FirebaseDatabase.getInstance().getReference().child("schedules")
//                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        scheduleModelList.clear();
//
//                        if (!dataSnapshot.exists()) {
//                            noSchedule.setVisibility(View.VISIBLE);
//                            dialogFragment.dismiss();
//                            recyclerView.setAdapter(new ScheduleAdapter(DeliveryScheduleActivity.this, scheduleModelList));
//                        }
//                        else {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                if ("scheduled".equals(snapshot.child("status").getValue())) {
//                                    FirebaseDatabase.getInstance().getReference().child("clients")
//                                            .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
//                                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
//                                                        scheduleModelList.add(new ScheduleModel(
//                                                                String.valueOf(snapshot.child("schedule_id").getValue()),
//                                                                String.valueOf(snapshot.child("client_id").getValue()),
//                                                                String.valueOf(snapshot.child("schedule").getValue()),
//                                                                String.valueOf(snapshot.child("switch").getValue()),
//                                                                String.valueOf(snapshot1.child("company").getValue())
//                                                        ));
//
//                                                        recyclerView.setAdapter(new ScheduleAdapter(DeliveryScheduleActivity.this, scheduleModelList));
//                                                        noSchedule.setVisibility(View.GONE);
//                                                        dialogFragment.dismiss();
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//
//                                }
//                                else {
//                                    dialogFragment.dismiss();
//                                }
//
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//
//                if (dy > 0) {
//                    floatingActionButton.setVisibility(View.GONE);
//                } else if (dy < 0) {
//                    floatingActionButton.setVisibility(View.VISIBLE);
//                } else {
//                    floatingActionButton.setVisibility(View.VISIBLE);
//                }
//            }
//        });


    }



    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(DeliveryScheduleActivity.this, HomeActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DeliveryScheduleActivity.this, HomeActivity.class));
    }
}
