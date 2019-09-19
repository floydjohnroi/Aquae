package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScheduleActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, turn, schedule, station, deliveryAddress, deliveryFee, subtotal, total,
            notes, addNotes, shipfee, km, distance, perName;
    Switch onOff;
    RecyclerView recyclerView;
    List<ScheduleProductModel> scheduleProductModelList = new ArrayList<>();
    MaterialButton removeSched, skip, take;
    LinearLayout subtotalLayout, deliveryFeeLayout, reminderButtons, onoffLayout, personnelLayout, reminderNote;
    String activity, payment, perId, personImage, perContact;
    int qtyr, qtyp;
    ConstraintLayout placeOrderLayout;
    RadioGroup paymentMethod;
    RadioButton method;
    Map<String, Object> items = new HashMap<>();
    ImageView perProfile;


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
        subtotalLayout = findViewById(R.id.subtotal_layout);
        deliveryFeeLayout = findViewById(R.id.delivery_fee_layout);
        shipfee = findViewById(R.id.shipfee);
        km = findViewById(R.id.km);
        distance = findViewById(R.id.distance);
        activity = getIntent().getStringExtra("activity");
        reminderButtons = findViewById(R.id.reminder_buttons);
        onoffLayout = findViewById(R.id.onoff_layout);
        skip = findViewById(R.id.skip);
        take = findViewById(R.id.take);
        perProfile = findViewById(R.id.personnel_profile);
        perName = findViewById(R.id.personnel_name);
        personnelLayout = findViewById(R.id.personnel_layout);
        reminderNote = findViewById(R.id.reminder_note);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        if ("schedule".equals(activity)) {
            toolbarTitle.setText("Schedule");
            removeSched.setVisibility(View.VISIBLE);
            reminderButtons.setVisibility(View.GONE);
            onoffLayout.setVisibility(View.VISIBLE);
            reminderNote.setVisibility(View.GONE);

            if ("off".equals(getIntent().getStringExtra("switch"))) {
                turn.setText("Turn schedule on");
            }
            else {
                turn.setText("Turn schedule off");
                onOff.setChecked(true);
            }
        }
        else {
            toolbarTitle.setText("Reminder");
            removeSched.setVisibility(View.GONE);
            reminderButtons.setVisibility(View.VISIBLE);
            onoffLayout.setVisibility(View.GONE);
            reminderNote.setVisibility(View.VISIBLE);
        }

        km.setText(Html.fromHtml("DISTANCE <i>(km)</i>"));

        station.setText(getIntent().getStringExtra("station"));


        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "delivery_schedule_activity");

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
                                schedule.setText("Every Mon – Fri");
                            }
                            else if ("Sun – Sat".equals(snapshot.child("schedule").getValue())) {
                                schedule.setText("Every Sun & Sat");
                            }
                            else {
                                if (String.valueOf(snapshot.child("schedule").getValue()).length() > 3) {
                                    String finalStr = String.valueOf(snapshot.child("schedule").getValue()).substring(0, String.valueOf(snapshot.child("schedule").getValue()).lastIndexOf("–")) + "&" + String.valueOf(snapshot.child("schedule").getValue()).substring(String.valueOf(snapshot.child("schedule").getValue()).lastIndexOf("–") + 1);
                                    schedule.setText("Every "+finalStr.replace(" – ", ", "));
                                }
                                else {
                                    if ("Sun".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Sunday");
                                    }
                                    if ("Mon".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Monday");
                                    }
                                    if ("Tue".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Tuesday");
                                    }
                                    if ("Wed".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Wednesday");
                                    }
                                    if ("Thu".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Thursday");
                                    }
                                    if ("Fri".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Friday");
                                    }
                                    if ("Sat".equals(snapshot.child("schedule").getValue())) {
                                        schedule.setText("Every Saturday");
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
                            distance.setText(snapshot.child("distance").getValue()+" km");
                            if ("FREE".equals(snapshot.child("delivery_fee").getValue())) {
                                subtotalLayout.setVisibility(View.GONE);
                                deliveryFeeLayout.setVisibility(View.GONE);
                                shipfee.setText(Html.fromHtml("<b>"+snapshot.child("delivery_fee").getValue()+"</b>"));
                            }
                            else {
                                subtotalLayout.setVisibility(View.VISIBLE);
                                deliveryFeeLayout.setVisibility(View.VISIBLE);
                                int fee = Integer.parseInt(String.valueOf(snapshot.child("delivery_fee").getValue()))
                                        * Integer.parseInt(String.valueOf(snapshot.child("distance").getValue()));
                                deliveryFee.setText(String.valueOf(fee));
                                int sub = Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()))
                                        - Integer.parseInt(String.valueOf(snapshot.child("delivery_fee").getValue()));
                                subtotal.setText(String.valueOf(sub));
                                shipfee.setText(Html.fromHtml("₱<b>"+snapshot.child("delivery_fee").getValue()+"</b>"));
                            }
                            total.setText(Html.fromHtml("₱<b>"+snapshot.child("total_amount").getValue()+"</b>"));

                            for (DataSnapshot snapshot1 : snapshot.child("items").getChildren()) {

                                Map<String, Object> refill = new HashMap<>();
                                Map<String, Object> purchase = new HashMap<>();
                                Map<String, Object> map = new HashMap<>();

                                Object refillPrice = 0;
                                Object purchasePrice = 0;
                                Object refillQty = 0;
                                Object purchaseQty = 0;

                                for (DataSnapshot e : snapshot1.getChildren()) {

                                    if (Objects.requireNonNull(e.getKey()).equals("refill")) {
                                        refillQty = e.child("quantity").getValue();
                                        refillPrice = e.child("price").getValue();

                                        refill.put("quantity", String.valueOf(e.child("quantity").getValue()));
                                        refill.put("price", String.valueOf(e.child("price").getValue()));
                                        map.put("refill", refill);
                                    }

                                    if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
                                        purchaseQty = e.child("quantity").getValue();
                                        purchasePrice = e.child("price").getValue();

                                        purchase.put("quantity", String.valueOf(e.child("quantity").getValue()));
                                        purchase.put("price", String.valueOf(e.child("price").getValue()));
                                        map.put("purchase", purchase);
                                    }

                                }

                                items.put(Objects.requireNonNull(snapshot1.getKey()), map);

                                scheduleProductModelList.add(new ScheduleProductModel(
                                        String.valueOf(snapshot1.getKey()),
                                        String.valueOf(refillPrice),
                                        String.valueOf(purchasePrice),
                                        String.valueOf(refillQty),
                                        String.valueOf(purchaseQty),
                                        String.valueOf(snapshot1.child("water_type").getValue()),
                                        String.valueOf(snapshot1.child("image").getValue())
                                ));

                                qtyr += Integer.parseInt(String.valueOf(refillQty));
                                qtyp += Integer.parseInt(String.valueOf(purchaseQty));

                            }

                            FirebaseDatabase.getInstance().getReference()
                                    .child("personnels")
                                    .orderByChild("per_id").equalTo(String.valueOf(snapshot.child("per_id").getValue()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                Picasso.get()
                                                        .load(String.valueOf(snapshot1.child("pic").getValue()))
                                                        .fit()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.refillssss)
                                                        .into(perProfile);
                                                perName.setText(capitalize(String.valueOf(snapshot1.child("per_name").getValue())));

                                                perId = String.valueOf(snapshot1.child("per_id").getValue());
                                                personImage = String.valueOf(snapshot1.child("pic").getValue());
                                                perContact = String.valueOf(snapshot1.child("per_contact").getValue());

                                                dialogFragment.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            if ("schedule".equals(activity) && snapshot.child("message").getValue() != null) {
                                if (!"".equals(String.valueOf(snapshot.child("message").getValue())) ) {
                                    View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.message_dialog_view, null);
                                    View titleView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.custom_dialog_title, null);

                                    TextView title = titleView.findViewById(R.id.title);
                                    title.setText("MESSAGE");

                                    TextView msg = view.findViewById(R.id.message);

                                    msg.setText(String.valueOf(snapshot.child("message").getValue()));

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                                    builder.setCustomTitle(titleView);
                                    builder.setView(view);

                                    builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            snapshot.getRef().child("message").removeValue();
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                            }

                        }

                        recyclerView.setAdapter(new ScheduleProductAdapter(ScheduleActivity.this, scheduleProductModelList));
                        dialogFragment.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        personnelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.personnel_dialog_view, null);

                ImageView close = view.findViewById(R.id.close);
                ImageView profile = view.findViewById(R.id.profile);
                TextView name = view.findViewById(R.id.name);
                TextView contact = view.findViewById(R.id.contact);

                Picasso.get()
                        .load(personImage)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.refillssss)
                        .into(profile);

                name.setText(perName.getText());
                contact.setText(perContact);

                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                builder.setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

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


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("schedules")
                        .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().child("status")
                                            .setValue("scheduled")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    snapshot.getRef().child("remind_time")
                                                            .removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(), "Skipped Schedule", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            });

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.payment_method_dialog_view, null);
                View titleView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("PAYMENT METHOD");

                paymentMethod = view.findViewById(R.id.paymentMethod);

                method = (RadioButton) paymentMethod.getChildAt(0);
                method.setChecked(true);

                method = view.findViewById(paymentMethod.getCheckedRadioButtonId());
                payment = (String) method.getText();

                paymentMethod.setOnCheckedChangeListener((group, checkedId) -> {

                    method = view.findViewById(checkedId);
                    payment = (String) method.getText();

                });


                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                        Map<String, Object> order = new HashMap<>();
                        order.put("order_id", String.valueOf(id));
                        order.put("customer_id", new Session(getApplicationContext()).getId());
                        order.put("client_id", getIntent().getStringExtra("client_id"));
                        order.put("payment", payment);
                        order.put("total_amount", String.valueOf(total.getText()).replace("₱", ""));
                        order.put("delivery_address", String.valueOf(deliveryAddress.getText()));
                        order.put("items", items);
                        order.put("status", "schedule");
                        order.put("click", "1");
                        order.put("delivery_fee", String.valueOf(shipfee.getText()).replace("₱", ""));
                        order.put("notes", String.valueOf(notes.getText()));
                        order.put("per_id", perId);

                        View view = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.wallet_dialog_layout, null);
                        View titleView = LayoutInflater.from(ScheduleActivity.this).inflate(R.layout.custom_dialog_title, null);

                        TextView title = titleView.findViewById(R.id.title);
                        title.setText("Pay with Aquae Wallet");

                        TextView balance = view.findViewById(R.id.balance);
                        TextView totalPayment = view.findViewById(R.id.totalPayment);
                        TextView textView2 = view.findViewById(R.id.textView2);

                        totalPayment.setText(String.valueOf(total.getText()).replace("₱", ""));

                        String q = qtyr+qtyp+" items";
                        textView2.setText(q);

                        FirebaseDatabase.getInstance().getReference().child("customers")
                                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            balance.setText(String.valueOf(snapshot.child("wallet").getValue()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this, R.style.AlertDialogTheme);
                        builder.setCustomTitle(titleView);
                        builder.setView(view);

                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (Integer.parseInt(String.valueOf(totalPayment.getText())) > Integer.parseInt(String.valueOf(balance.getText()))) {

                                    Snackbar snackbar = Snackbar.make(placeOrderLayout, "Insufficient funds", 10000)
                                            .setAction("REQUEST", v1 -> startActivity(new Intent(ScheduleActivity.this, RequestCashInActivity.class)))
                                            .setActionTextColor(getResources().getColor(R.color.colorSnackBarAction));

                                    View snackbarView = snackbar.getView();
                                    snackbarView.setPadding(24, 24, 24, 24);
                                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                    textView.setTextColor(getResources().getColor(R.color.colorWhite));

                                    snackbar.show();
                                }
                                else {

                                    FirebaseDatabase.getInstance().getReference().child("orders")
                                            .child(id)
                                            .setValue(order)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseDatabase.getInstance().getReference().child("orders")
                                                            .orderByChild("order_id").equalTo(id)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                        FirebaseDatabase.getInstance().getReference().child("carts")
                                                                                .orderByChild("customer_id").equalTo(String.valueOf(snapshot.child("customer_id").getValue()))
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                                                                        for (DataSnapshot snapshot2 : dataSnapshot1.getChildren()) {
                                                                                            if (Objects.equals(snapshot2.child("client_id").getValue(), snapshot.child("client_id").getValue())) {
                                                                                                for (DataSnapshot snap : snapshot2.child("products").getChildren()) {
                                                                                                    for (DataSnapshot sn : snap.getChildren()) {
                                                                                                        if (Objects.equals(sn.child("status").getValue(), "check")) {
                                                                                                            snap.getRef().removeValue();
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                        FirebaseDatabase.getInstance().getReference().child("customers")
                                                                                                .orderByChild("customer_id").equalTo(String.valueOf(snapshot.child("customer_id").getValue()))
                                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                                                                            int newBal = Integer.parseInt(String.valueOf(snapshot1.child("wallet").getValue()))
                                                                                                                    - Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()));

                                                                                                            snapshot1.getRef().child("wallet").setValue(String.valueOf(newBal))
                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                                                                    .child("schedules")
                                                                                                                                    .orderByChild("schedule_id").equalTo(getIntent().getStringExtra("schedule_id"))
                                                                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                                        @Override
                                                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                            for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                                                                                                                snapshot2.getRef().child("status")
                                                                                                                                                        .setValue("scheduled")
                                                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                            @Override
                                                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                                                snapshot2.getRef().child("remind_time")
                                                                                                                                                                        .removeValue()
                                                                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                                                                Toast.makeText(ScheduleActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                                                                                                                                                                                Toast.makeText(ScheduleActivity.this, "NEW BALANCE : "+newBal, Toast.LENGTH_LONG).show();

                                                                                                                                                                                startActivity(new Intent(ScheduleActivity.this, HomeActivity.class));
                                                                                                                                                                                finish();
                                                                                                                                                                            }
                                                                                                                                                                        });
                                                                                                                                                            }
                                                                                                                                                        });
                                                                                                                                            }
                                                                                                                                        }

                                                                                                                                        @Override
                                                                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                    }
                                                                                                });

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                }
                                            });

                                }

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();


                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


    }

    private String capitalize(final String line) {

        String[] strings = line.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : strings) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            stringBuilder.append(cap).append(" ");
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
