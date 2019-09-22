package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScheduledDelivery extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, addNew, km, distance, shipfee, total, subtotal, delivery_fee;
    TextInputEditText setDays, notes;
    RecyclerView recyclerView, recyclerView1;
    List<AddressModel> addressModelList = new ArrayList<>();
    List<CheckOutProductModel> checkOutProductModelList = new ArrayList<>();
    MaterialButton setSchedule;
    Map<String, Object> items = new HashMap<>();
    LinearLayout subtotalLayout, deliveryFeeLayout, notesLayout;
    String addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_delivery);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        setDays = findViewById(R.id.set_days);
        recyclerView = findViewById(R.id.recyclerView);
        addNew = findViewById(R.id.add_new);
        recyclerView1 = findViewById(R.id.recyclerView1);
        km = findViewById(R.id.km);
        distance = findViewById(R.id.distance);
        shipfee = findViewById(R.id.shipfee);
        total = findViewById(R.id.total);
        subtotal = findViewById(R.id.subtotal);
        delivery_fee = findViewById(R.id.delivery_fee);
        setSchedule = findViewById(R.id.set_schedule);
        subtotalLayout = findViewById(R.id.subtotal_layout);
        deliveryFeeLayout = findViewById(R.id.delivery_fee_layout);
        notes = findViewById(R.id.notes);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Create Schedule");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView1.setLayoutManager(linearLayoutManager1);

        km.setText(Html.fromHtml("DISTANCE <i>(km)</i>"));

        shipfee.setText(Html.fromHtml("₱<b>" + getIntent().getStringExtra("ship_fee") + "</b>"));

        total.setText(Html.fromHtml("₱<b>0</b>"));

        FirebaseDatabase.getInstance().getReference().child("customers")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snap : snapshot.child("addresses").getChildren()) {
                                addressModelList.add(new AddressModel(
                                        String.valueOf(snap.getValue())
                                ));
                            }
                        }

                        recyclerView.setAdapter(new AddressAdapter(ScheduledDelivery.this, addressModelList, new AddressAdapter.AddressCallback() {
                            @Override
                            public void saveAddress(String addresses) {

                                addr = addresses;

                                RequestQueue queue = Volley.newRequestQueue(ScheduledDelivery.this);
                                String origin = getIntent().getStringExtra("client_address");
                                String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+ addresses +"&avoid=tolls|highways&key=AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY";
                                url = url.replaceAll(" ", "%20");

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {

                                    String dist = "";

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray legs = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                                        for (int i = 0; i < legs.length(); i++) {
                                            JSONObject leg = legs.getJSONObject(i);
                                            dist = leg.getJSONObject("distance").getString("text");
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    if ("".equals(dist)) {
                                        distance.setText("0 km");
                                    }
                                    else {
                                        String d = dist.replace(" km" ,"");
                                        String[] e = d.split("\\.");

                                        if (Integer.parseInt(String.valueOf(e[0])) > 5) {
                                            distance.setText(e[0]+" km");
                                            int fee = Integer.parseInt(getIntent().getStringExtra("ship_fee")) * Integer.parseInt(String.valueOf(e[0]));
                                            delivery_fee.setText(String.valueOf(fee));
                                            int newt = Integer.parseInt(String.valueOf(subtotal.getText())) + fee;
                                            total.setText(Html.fromHtml("₱<b>" + newt + "</b>"));
                                            subtotalLayout.setVisibility(View.VISIBLE);
                                            deliveryFeeLayout.setVisibility(View.VISIBLE);
                                            shipfee.setText(Html.fromHtml("₱<b>" + getIntent().getStringExtra("ship_fee") + "</b>"));
                                        }
                                        else {
                                            distance.setText(e[0]+" km");
                                            shipfee.setText(Html.fromHtml("<b>FREE</b>"));
                                            subtotalLayout.setVisibility(View.GONE);
                                            deliveryFeeLayout.setVisibility(View.GONE);
                                            total.setText(Html.fromHtml("₱<b>" + subtotal.getText() + "</b>"));
                                        }
                                    }


                                }, error -> Log.d("error", error.toString()));

                                queue.add(stringRequest);

                            }
                        }));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        setDays.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()));

        if (new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()).equals(String.valueOf(setDays.getText()))) {
            setSchedule.setEnabled(false);
        }

        setDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(ScheduledDelivery.this).inflate(R.layout.setdays_dialog_view, null);
                View titleView = LayoutInflater.from(ScheduledDelivery.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("Days");

                CheckBox sun = view.findViewById(R.id.sunday);
                CheckBox mon = view.findViewById(R.id.monday);
                CheckBox tue = view.findViewById(R.id.tuesday);
                CheckBox wed = view.findViewById(R.id.wednesday);
                CheckBox thu = view.findViewById(R.id.thursday);
                CheckBox fri = view.findViewById(R.id.friday);
                CheckBox sat = view.findViewById(R.id.saturday);

                if ("Sun".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    sun.setEnabled(false);
                }
                if ("Mon".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    mon.setEnabled(false);
                }
                if ("Tue".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    tue.setEnabled(false);
                }
                if ("Wed".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    wed.setEnabled(false);
                }
                if ("Thu".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    thu.setEnabled(false);
                }
                if ("Fri".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    fri.setEnabled(false);
                }
                if ("Sat".equals(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()))) {
                    sat.setEnabled(false);
                }

                String[] d = String.valueOf(setDays.getText()).split(" – ");

                for (int i = 0; i < d.length; i++) {
                    if (d[i].equals("Sun") && sun.isEnabled()) {
                        sun.setChecked(true);
                    }
                    if (d[i].equals("Mon") && mon.isEnabled()) {
                        mon.setChecked(true);
                    }
                    if (d[i].equals("Tue") && tue.isEnabled()) {
                        tue.setChecked(true);
                    }
                    if (d[i].equals("Wed") && wed.isEnabled()) {
                        wed.setChecked(true);
                    }
                    if (d[i].equals("Thu") && thu.isEnabled()) {
                        thu.setChecked(true);
                    }
                    if (d[i].equals("Fri") && fri.isEnabled()) {
                        fri.setChecked(true);
                    }
                    if (d[i].equals("Sat") && sat.isEnabled()) {
                        sat.setChecked(true);
                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduledDelivery.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String days = "";

                        if (sun.isChecked()) {
                            days += "Sun" + " – ";
                        }
                        if (mon.isChecked()) {
                            days += "Mon" + " – ";
                        }
                        if (tue.isChecked()) {
                            days += "Tue" + " – ";
                        }
                        if (wed.isChecked()) {
                            days += "Wed" + " – ";
                        }
                        if (thu.isChecked()) {
                            days += "Thu" + " – ";
                        }
                        if (fri.isChecked()) {
                            days += "Fri" + " – ";
                        }
                        if (sat.isChecked()) {
                            days += "Sat" + " – ";
                        }

                        if (!days.isEmpty()) {
                            String finalDay = days.substring(0, days.length() - 3);
                            setDays.setText(finalDay);
                            setSchedule.setEnabled(true);
                        } else {
                            setDays.setText(new SimpleDateFormat("EEE", Locale.ENGLISH).format(System.currentTimeMillis()));
                            setSchedule.setEnabled(false);
                        }

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY");
        }

        addNew.setOnClickListener(v -> {

            List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("PH")
                    .build(getApplicationContext());
            startActivityForResult(intent, 0);

//            View view = LayoutInflater.from(ScheduledDelivery.this).inflate(R.layout.address_dialog_view, null);
//            View titleView = LayoutInflater.from(ScheduledDelivery.this).inflate(R.layout.custom_dialog_title, null);
//
//            TextView title = titleView.findViewById(R.id.title);
//            title.setText(R.string.add_new_address);
//
//            TextInputEditText address = view.findViewById(R.id.address);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(ScheduledDelivery.this, R.style.AlertDialogTheme);
//            builder.setCustomTitle(titleView);
//            builder.setView(view);
//
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//
//            builder.setPositiveButton("Add", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("customers")
//                    .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            List<String> add = new ArrayList<>();
//
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                for (DataSnapshot snap : snapshot.child("addresses").getChildren()) {
//                                    add.add(String.valueOf(snap.getValue()));
//                                }
//                            }
//
//                            add.add(String.valueOf(address.getText()));
//
//                            addressModelList.clear();
//
//                            dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).child("addresses").setValue(add);
//                            Toast.makeText(ScheduledDelivery.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    }));
//
//
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();

        });


        setSchedule.setOnClickListener(v -> {

            String id = FirebaseDatabase.getInstance().getReference().push().getKey();

            Map<String, Object> schedule = new HashMap<>();
            schedule.put("schedule_id", String.valueOf(id));
            schedule.put("customer_id", new Session(getApplicationContext()).getId());
            schedule.put("client_id", getIntent().getStringExtra("client_id"));
            schedule.put("total_amount", String.valueOf(total.getText()).replace("₱", ""));
            schedule.put("delivery_address", addr);
            schedule.put("items", items);
            schedule.put("status", "pending");
            schedule.put("delivery_fee", String.valueOf(shipfee.getText()).replace("₱", ""));
            schedule.put("notes", String.valueOf(notes.getText()).trim());
            schedule.put("schedule", String.valueOf(setDays.getText()));
            schedule.put("switch", "on");
            schedule.put("distance", String.valueOf(distance.getText()).replace(" km", ""));

            FirebaseDatabase.getInstance().getReference().child("schedules")
                    .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                dataSnapshot.getRef().child(String.valueOf(id))
                                        .setValue(schedule)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseDatabase.getInstance().getReference().child("deliveries")
                                                        .orderByChild("customer_id").equalTo(String.valueOf(new Session(getApplicationContext()).getId()))
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                                    if (Objects.equals(snapshot2.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
                                                                        for (DataSnapshot snap : snapshot2.child("products").getChildren()) {
                                                                            for (DataSnapshot sn : snap.getChildren()) {
                                                                                if (Objects.equals(sn.child("status").getValue(), "check")) {
                                                                                    snap.getRef().removeValue();
                                                                                }
                                                                            }

                                                                        }
                                                                    }
                                                                }

//                                                                    Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
//                                                                    startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
//                                                                    finish();

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
                                                finish();

                                            }
                                        });
                            }
                            else {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    if (getIntent().getStringExtra("client_id").equals(snapshot1.child("client_id").getValue())) {
                                        if ("scheduled".equals(snapshot1.child("status").getValue())
                                                || "pending".equals(snapshot1.child("status").getValue())) {
                                            if (String.valueOf(setDays.getText()).equals(snapshot1.child("schedule").getValue())
                                                    && addr.equals(snapshot1.child("delivery_address").getValue())) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduledDelivery.this, R.style.AlertDialogTheme);
                                                builder.setTitle("Ooops!");
                                                builder.setMessage("You've already set this schedule. Please wait for the confirmation.");

                                                builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.create().show();
                                            } else {

                                                dataSnapshot.getRef().child(String.valueOf(id))
                                                        .setValue(schedule)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                FirebaseDatabase.getInstance().getReference().child("deliveries")
                                                                        .orderByChild("customer_id").equalTo(String.valueOf(snapshot1.child("customer_id").getValue()))
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                                                    if (Objects.equals(snapshot2.child("client_id").getValue(), snapshot1.child("client_id").getValue())) {
                                                                                        for (DataSnapshot snap : snapshot2.child("products").getChildren()) {
                                                                                            for (DataSnapshot sn : snap.getChildren()) {
                                                                                                if (Objects.equals(sn.child("status").getValue(), "check")) {
                                                                                                    snap.getRef().removeValue();
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }
                                                                                }

//                                                                            Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
//                                                                            startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
//                                                                            finish();

                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
                                                                finish();

                                                            }
                                                        });

                                            }
                                        }
                                    } else {
                                        dataSnapshot.getRef().child(String.valueOf(id))
                                                .setValue(schedule)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseDatabase.getInstance().getReference().child("deliveries")
                                                                .orderByChild("customer_id").equalTo(String.valueOf(snapshot1.child("customer_id").getValue()))
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                                            if (Objects.equals(snapshot2.child("client_id").getValue(), snapshot1.child("client_id").getValue())) {
                                                                                for (DataSnapshot snap : snapshot2.child("products").getChildren()) {
                                                                                    for (DataSnapshot sn : snap.getChildren()) {
                                                                                        if (Objects.equals(sn.child("status").getValue(), "check")) {
                                                                                            snap.getRef().removeValue();
                                                                                        }
                                                                                    }

                                                                                }
                                                                            }
                                                                        }

//                                                                    Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
//                                                                    startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
//                                                                    finish();

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                        Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
                                                        finish();

                                                    }
                                                });

                                    }
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//            String id = FirebaseDatabase.getInstance().getReference().push().getKey();
//
//            Map<String, Object> schedule = new HashMap<>();
//            schedule.put("schedule_id", String.valueOf(id));
//            schedule.put("customer_id", new Session(getApplicationContext()).getId());
//            schedule.put("client_id", getIntent().getStringExtra("client_id"));
//            schedule.put("total_amount", String.valueOf(total.getText()).replace("₱", ""));
//            schedule.put("delivery_address", addr);
//            schedule.put("items", items);
//            schedule.put("status", "pending");
//            schedule.put("delivery_fee", String.valueOf(shipfee.getText()).replace("₱", ""));
//            schedule.put("notes", String.valueOf(notes.getText()).trim());
//            schedule.put("schedule", String.valueOf(setDays.getText()));
//            schedule.put("switch", "on");
//            schedule.put("distance", String.valueOf(distance.getText()).replace(" km", ""));
//
//            FirebaseDatabase.getInstance().getReference().child("schedules")
//                    .child(String.valueOf(id))
//                    .setValue(schedule)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            FirebaseDatabase.getInstance().getReference().child("schedules")
//                                    .orderByChild("schedule_id").equalTo(id)
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                                FirebaseDatabase.getInstance().getReference().child("deliveries")
//                                                        .orderByChild("customer_id").equalTo(String.valueOf(snapshot.child("customer_id").getValue()))
//                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
//                                                                    if (Objects.equals(snapshot2.child("client_id").getValue(), snapshot.child("client_id").getValue())) {
//                                                                        for (DataSnapshot snap : snapshot2.child("products").getChildren()) {
//                                                                            for (DataSnapshot sn : snap.getChildren()) {
//                                                                                if (Objects.equals(sn.child("status").getValue(), "check")) {
//                                                                                    snap.getRef().removeValue();
//                                                                                }
//                                                                            }
//
//                                                                        }
//                                                                    }
//                                                                }
//
//                                                                Toast.makeText(ScheduledDelivery.this, "SCHEDULE SET", Toast.LENGTH_SHORT).show();
//                                                                startActivity(new Intent(ScheduledDelivery.this, DeliveryScheduleActivity.class));
//                                                                finish();
//
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//
//                        }
//                    });

        });


        FirebaseDatabase.getInstance().getReference().child("deliveries")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int t = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    for (DataSnapshot s : shot.getChildren()) {
                                        for (DataSnapshot d : s.getChildren()) {
                                            if (Objects.equals(d.child("status").getValue(), "check")) {

                                                Map<String, Object> refill = new HashMap<>();
                                                Map<String, Object> purchase = new HashMap<>();
                                                Map<String, Object> map = new HashMap<>();

                                                Object refillQuantity = 0;
                                                Object refillPrice = 0;
                                                Object purchaseQuantity = 0;
                                                Object purchasePrice = 0;

                                                map.put("water_type", Objects.requireNonNull(d.child("water_type").getValue()));
                                                map.put("image", String.valueOf(d.child("image").getValue()));

                                                for (DataSnapshot e : d.getChildren()) {

                                                    if (Objects.requireNonNull(e.getKey()).equals("refill")) {
                                                        refillQuantity = e.child("quantity").getValue();
                                                        refillPrice = e.child("price").getValue();

                                                        refill.put("quantity", Objects.requireNonNull(e.child("quantity").getValue()));
                                                        refill.put("price", Objects.requireNonNull(e.child("price").getValue()));
                                                        map.put("refill", refill);
                                                    }

                                                    if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
                                                        purchaseQuantity = e.child("quantity").getValue();
                                                        purchasePrice = e.child("price").getValue();

                                                        purchase.put("quantity", Objects.requireNonNull(e.child("quantity").getValue()));
                                                        purchase.put("price", Objects.requireNonNull(e.child("price").getValue()));
                                                        map.put("purchase", purchase);
                                                    }

                                                }

                                                checkOutProductModelList.add(new CheckOutProductModel(
                                                        String.valueOf(snapshot.child("client_id").getValue()),
                                                        String.valueOf(s.getKey()),
                                                        String.valueOf(d.getKey()),
                                                        String.valueOf(refillQuantity),
                                                        String.valueOf(purchaseQuantity),
                                                        String.valueOf(d.child("water_type").getValue()),
                                                        String.valueOf(refillPrice),
                                                        String.valueOf(purchasePrice),
                                                        String.valueOf(d.child("image").getValue()),
                                                        String.valueOf(d.child("subtotal").getValue())
                                                ));


                                                items.put(Objects.requireNonNull(d.getKey()), map);

                                                t += Integer.parseInt(String.valueOf(d.child("subtotal").getValue()));
                                                subtotal.setText(String.valueOf(t));

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        recyclerView1.setAdapter(new CheckOutProductAdapter(ScheduledDelivery.this, checkOutProductModelList));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
//            addr.setText(String.valueOf(place.getName()));

            FirebaseDatabase.getInstance().getReference().child("customers")
                    .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<String> add = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snap : snapshot.child("addresses").getChildren()) {
                                    add.add(String.valueOf(snap.getValue()));
                                }
                            }

                            add.add(String.valueOf(place.getAddress()));

                            addressModelList.clear();

                            dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).child("addresses").setValue(add);
                            Toast.makeText(ScheduledDelivery.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
