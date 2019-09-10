package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ScheduleActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, turn, schedule, station, deliveryAddress, deliveryFee, subtotal, total, notes, addNotes;
    Switch onOff;
    RecyclerView recyclerView;
    List<ScheduleProductModel> scheduleProductModelList = new ArrayList<>();
    MaterialButton removeSched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        turn = findViewById(R.id.turn);
        onOff = findViewById(R.id.on_off);
        schedule = findViewById(R.id.schedule);
        recyclerView = findViewById(R.id.recyclerView);
        station = findViewById(R.id.station);
        deliveryAddress = findViewById(R.id.delivery_address);
        deliveryFee = findViewById(R.id.fee);
        subtotal = findViewById(R.id.subtotal);
        total = findViewById(R.id.total_amount);
        notes = findViewById(R.id.notes);
        removeSched = findViewById(R.id.remove);
        addNotes = findViewById(R.id.add_notes);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Schedule");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        if ("off".equals(getIntent().getStringExtra("switch"))) {
            turn.setText("Turn schedule on");
        }
        else {
            turn.setText("Turn schedule off");
            onOff.setChecked(true);
        }

        station.setText(getIntent().getStringExtra("station"));

        if ("Everyday".equals(getIntent().getStringExtra("schedule"))) {
            schedule.setText(getIntent().getStringExtra("schedule"));
        }
        else {
            schedule.setText("Every "+getIntent().getStringExtra("schedule"));
        }

        FirebaseDatabase.getInstance().getReference().child("schedules")
                .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        scheduleProductModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if ("Sun – Mon – Tue – Wed – Thu – Fri – Sat".equals(snapshot.child("schedule").getValue())) {
                                schedule.setText("Everyday");
                            }
                            else if ("Mon – Tue – Wed – Thu – Fri".equals(snapshot.child("schedule").getValue())) {
                                schedule.setText("Weekdays");
                            }
                            else if ("Sun – Sat".equals(snapshot.child("schedule").getValue())) {
                                schedule.setText("Weekends");
                            }
                            else {
                                if (String.valueOf(snapshot.child("schedule").getValue()).length() > 3) {
                                    String finalStr = String.valueOf(snapshot.child("schedule").getValue()).substring(0, String.valueOf(snapshot.child("schedule").getValue()).lastIndexOf("–")) + "&" + String.valueOf(snapshot.child("schedule").getValue()).substring(String.valueOf(snapshot.child("schedule").getValue()).lastIndexOf("–") + 1);
                                    schedule.setText("Every "+finalStr.replace(" – ", ", "));
                                }
                                else {
                                    if ("Sun".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Sunday");
                                    }
                                    if ("Mon".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Monday");
                                    }
                                    if ("Tue".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Tuesday");
                                    }
                                    if ("Wed".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Wednesday");
                                    }
                                    if ("Thu".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Thursday");
                                    }
                                    if ("Fri".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Friday");
                                    }
                                    if ("Sat".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Saturday");
                                    }
                                }
                            }

                            if ("".equals(snapshot.child("notes").getValue())) {
                                notes.setVisibility(View.GONE);
                                addNotes.setVisibility(View.VISIBLE);
                            }
                            else {
                               notes.setVisibility(View.VISIBLE);
                               addNotes.setVisibility(View.GONE);
                               notes.setText(String.valueOf(snapshot.child("notes").getValue()));
                            }

                            deliveryAddress.setText(String.valueOf(snapshot.child("delivery_address").getValue()));
                            deliveryFee.setText(String.valueOf(snapshot.child("delivery_fee").getValue()));
                            int sub = Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()))
                                    - Integer.parseInt(String.valueOf(snapshot.child("delivery_fee").getValue()));
                            subtotal.setText(String.valueOf(sub));
                            String t = "₱<b>"+snapshot.child("total_amount").getValue()+"</b>";
                            total.setText(Html.fromHtml(t));

                            for (DataSnapshot snapshot1 : snapshot.child("items").getChildren()) {

                                Object refillPrice = 0;
                                Object purchasePrice = 0;
                                Object refillQty = 0;
                                Object purchaseQty = 0;

                                for (DataSnapshot e : snapshot1.getChildren()) {

                                    if (Objects.requireNonNull(e.getKey()).equals("refill")) {
                                        refillQty = e.child("quantity").getValue();
                                        refillPrice = e.child("price").getValue();
                                    }

                                    if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
                                        purchaseQty = e.child("quantity").getValue();
                                        purchasePrice = e.child("price").getValue();
                                    }

                                }

                                scheduleProductModelList.add(new ScheduleProductModel(
                                        String.valueOf(snapshot1.getKey()),
                                        String.valueOf(refillPrice),
                                        String.valueOf(purchasePrice),
                                        String.valueOf(refillQty),
                                        String.valueOf(purchaseQty),
                                        String.valueOf(snapshot1.child("water_type").getValue())
                                ));

                            }
                        }

                        recyclerView.setAdapter(new ScheduleProductAdapter(ScheduleActivity.this, scheduleProductModelList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        removeSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("schedules")
                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (getIntent().getStringExtra("schedule_id").equals(snapshot.child("schedule_id").getValue())) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                Toast.makeText(getApplicationContext(), "Schedule Removed", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turn.setText("Turn schedule off");
                    FirebaseDatabase.getInstance().getReference().child("schedules")
                            .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("switch").setValue("on");

                                        if (new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()).equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                            Toast.makeText(getApplicationContext(), "Schedule set today", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            if ("Sun".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Mon".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Tue".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Wed".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Thu".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Fri".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if ("Sat".equals(String.valueOf(snapshot.child("schedule").getValue()).substring(0, 3))) {
                                                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 6 day from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 5 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 4 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 3 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 2 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                                                    Toast.makeText(getApplicationContext(), "Schedule set 1 days from now", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                else {
                    turn.setText("Turn schedule on");
                    FirebaseDatabase.getInstance().getReference().child("schedules")
                            .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("switch").setValue("off");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.newnote_dialog_view, null);
                View titleView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("UPDATE NOTES");

                TextInputEditText newNotes = view.findViewById(R.id.new_notes);

                newNotes.setText(notes.getText());

                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("schedules")
                                .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().child("notes").setValue("");
                                        }
                                        Toast.makeText(ScheduleActivity.this, "Notes Removed", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                builder.setPositiveButton("Update", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("schedules")
                        .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (!Objects.requireNonNull(snapshot.child("notes").getValue()).equals(String.valueOf(newNotes.getText()))) {
                                        snapshot.getRef().child("notes").setValue(String.valueOf(newNotes.getText()));
                                        Toast.makeText(ScheduleActivity.this, "Notes Updated", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ScheduleActivity.this, "Notes Unchanged", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }));


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                newNotes.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().isEmpty()) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        });

        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.newnote_dialog_view, null);
                View titleView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("ADD NEW NOTES");

                TextInputEditText newNotes = view.findViewById(R.id.new_notes);

                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("ADD", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("schedules")
                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("notes").setValue(String.valueOf(newNotes.getText()));
                                }
                                Toast.makeText(ScheduleActivity.this, "New Notes Added", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }));


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                newNotes.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().isEmpty()) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                        }
                        else {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
