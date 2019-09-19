package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TrackActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, orderId, orderTime, estTime, acceptedTime, acceptedDate, outforpickupTime, outforpickupDate,
    preparingTime, preparingDate, outfordeliveryTime, outfordeliveryDate, deliveredTime, deliveredDate;
    ImageView acceptedCheck, outforpickupCheck, preparingCheck, outfordeliveryCheck, deliveredCheck, personnel_profile;
    LinearLayout dashed, dashed1, dashed2, dashed3, estLayout, notesLayout, historyLayout, declinedLayout,
    acceptedLayout, outforpickupLayout, preparingLayout, outfordeliveryLayout, deliveredLayout;
    View solid, solid1, solid2, solid3;
    List<ScheduleProductModel> scheduleProductModelList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView station, deliveryAddress, subtotal, deliveryFee, orderTotal, paymentMethod, notes, historyTime, personnel_name;
    MaterialButton contactStation, viewStation, cancelOrder;
    String contactNumber, activity, personImage, perContact;
    LinearLayout personnelLayout, trackorderLayout, cancelOrderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        orderId = findViewById(R.id.order_id);
        estTime = findViewById(R.id.est_time);
        orderTime = findViewById(R.id.order_time);
        dashed = findViewById(R.id.dashed);
        dashed1 = findViewById(R.id.dashed1);
        dashed2 = findViewById(R.id.dashed2);
        dashed3 = findViewById(R.id.dashed3);
        solid  = findViewById(R.id.solid);
        solid1 = findViewById(R.id.solid1);
        solid2 = findViewById(R.id.solid2);
        solid3 = findViewById(R.id.solid3);
        acceptedLayout = findViewById(R.id.accepted_layout);
        acceptedCheck = findViewById(R.id.accepted_check);
        acceptedTime = findViewById(R.id.accepted_time);
        acceptedDate = findViewById(R.id.accepted_date);
        outforpickupLayout = findViewById(R.id.outforpickup_layout);
        outforpickupCheck = findViewById(R.id.outforpickup_check);
        outforpickupTime = findViewById(R.id.outforpickup_time);
        outforpickupDate = findViewById(R.id.outforpickup_date);
        preparingLayout = findViewById(R.id.preparing_layout);
        preparingCheck = findViewById(R.id.preparing_check);
        preparingTime = findViewById(R.id.preparing_time);
        preparingDate = findViewById(R.id.preparing_date);
        outfordeliveryLayout = findViewById(R.id.outfordelivery_layout);
        outfordeliveryCheck = findViewById(R.id.outfordelivery_check);
        outfordeliveryTime = findViewById(R.id.outfordelivery_time);
        outfordeliveryDate = findViewById(R.id.outfordelivery_date);
        deliveredLayout = findViewById(R.id.delivered_layout);
        deliveredCheck = findViewById(R.id.delivered_check);
        deliveredTime = findViewById(R.id.delivered_time);
        deliveredDate = findViewById(R.id.delivered_date);
        estLayout = findViewById(R.id.est_layout);
        recyclerView = findViewById(R.id.recyclerView);
        station = findViewById(R.id.station);
        deliveryAddress = findViewById(R.id.delivery_address);
        subtotal = findViewById(R.id.subtotal);
        deliveryFee = findViewById(R.id.delivery_fee);
        orderTotal = findViewById(R.id.order_total);
        paymentMethod = findViewById(R.id.payment_method);
        notesLayout = findViewById(R.id.notes_layout);
        notes = findViewById(R.id.notes);
        contactStation = findViewById(R.id.contact_station);
        viewStation = findViewById(R.id.view_station);
        activity = getIntent().getStringExtra("activity");
        deliveredLayout = findViewById(R.id.delivered_layout);
        historyLayout = findViewById(R.id.history_layout);
        historyTime = findViewById(R.id.history_time);
        declinedLayout = findViewById(R.id.declined_layout);
        personnelLayout = findViewById(R.id.personnel_layout);
        personnel_profile = findViewById(R.id.personnel_profile);
        personnel_name = findViewById(R.id.personnel_name);
        trackorderLayout = findViewById(R.id.trackorder_layout);
        cancelOrder = findViewById(R.id.cancel_order);
        cancelOrderLayout = findViewById(R.id.cancel_order_layout);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));

        if ("OrderHistory".equals(activity)) {
            toolbarTitle.setText("Order History");
            estLayout.setVisibility(View.GONE);
            contactStation.setVisibility(View.GONE);
            trackorderLayout.setVisibility(View.GONE);
            cancelOrderLayout.setVisibility(View.GONE);
        }
        else {
            toolbarTitle.setText("Track Order");
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        orderId.setText(getIntent().getStringExtra("order_id"));
        orderTime.setText(getIntent().getStringExtra("order_time"));

        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "track_activity");

        FirebaseDatabase.getInstance().getReference().child("clients")
                .orderByChild("client_id").equalTo(getIntent().getStringExtra("client_id"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            contactNumber = String.valueOf(snapshot.child("contact").getValue());
                            station.setText(String.valueOf(snapshot.child("company").getValue()));

                            RequestQueue queue = Volley.newRequestQueue(TrackActivity.this);
                            String origin = (String) snapshot.child("address").getValue();
                            String destination =  getIntent().getStringExtra("delivery_address");
                            String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&avoid=tolls|highways&key=AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY";
                            url = url.replaceAll(" ", "%20");

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray legs = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                                    for (int i = 0; i < legs.length(); i++) {
                                        JSONObject leg = legs.getJSONObject(i);
                                        estTime.setText(leg.getJSONObject("duration").getString("text"));

                                        dialogFragment.dismiss();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }, error -> Log.d("error", error.toString()));

                            queue.add(stringRequest);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("orders")
                .orderByChild("order_id").equalTo(getIntent().getStringExtra("order_id"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        scheduleProductModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!"OrderHistory".equals(activity)) {
                                if ("pending".equals(snapshot.child("status").getValue())) {
                                    personnelLayout.setVisibility(View.GONE);
                                    cancelOrderLayout.setVisibility(View.VISIBLE);
                                }
                                else if ("accepted".equals(snapshot.child("status").getValue())) {
                                    if ("1".equals(snapshot.child("click").getValue())) {
                                        cancelOrderLayout.setVisibility(View.GONE);
                                        acceptedCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed.setVisibility(View.GONE);
                                        solid.setVisibility(View.VISIBLE);

                                        String[] accept = String.valueOf(snapshot.child("accepted_time").getValue()).split(" ");
                                        acceptedTime.setText(accept[4]+" "+accept[5]);
                                        acceptedDate.setText(accept[0]+" "+accept[1].replace(",", ""));
                                    }
                                    else if ("2".equals(snapshot.child("click").getValue()) || "3".equals(snapshot.child("click").getValue())) {
                                        cancelOrderLayout.setVisibility(View.GONE);
                                        acceptedCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed.setVisibility(View.GONE);
                                        solid.setVisibility(View.VISIBLE);
                                        String[] accept = String.valueOf(snapshot.child("accepted_time").getValue()).split(" ");
                                        acceptedTime.setText(accept[4]+" "+accept[5]);
                                        acceptedDate.setText(accept[0]+" "+accept[1].replace(",", ""));
                                        cancelOrderLayout.setVisibility(View.GONE);
                                        outforpickupCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed1.setVisibility(View.GONE);
                                        solid1.setVisibility(View.VISIBLE);

                                        String[] pickup = String.valueOf(snapshot.child("pickup_time").getValue()).split(" ");
                                        outforpickupTime.setText(pickup[4]+" "+pickup[5]);
                                        outforpickupDate.setText(pickup[0]+" "+pickup[1].replace(",", ""));
                                    }
                                }
                                else if ("processing".equals(snapshot.child("status").getValue())) {
                                    if ("1".equals(snapshot.child("click").getValue())) {
                                        cancelOrderLayout.setVisibility(View.GONE);
                                        acceptedCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed.setVisibility(View.GONE);
                                        solid.setVisibility(View.VISIBLE);
                                        String[] accept = String.valueOf(snapshot.child("accepted_time").getValue()).split(" ");
                                        acceptedTime.setText(accept[4]+" "+accept[5]);
                                        acceptedDate.setText(accept[0]+" "+accept[1].replace(",", ""));
                                        outforpickupCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed1.setVisibility(View.GONE);
                                        solid1.setVisibility(View.VISIBLE);

                                        String[] pickup = String.valueOf(snapshot.child("pickup_time").getValue()).split(" ");
                                        outforpickupTime.setText(pickup[4]+" "+pickup[5]);
                                        outforpickupDate.setText(pickup[0]+" "+pickup[1].replace(",", ""));

                                        preparingCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed2.setVisibility(View.GONE);
                                        solid2.setVisibility(View.VISIBLE);

                                        String[] prepare = String.valueOf(snapshot.child("prepare_time").getValue()).split(" ");
                                        preparingTime.setText(prepare[4]+" "+prepare[5]);
                                        preparingDate.setText(prepare[0]+" "+prepare[1].replace(",", ""));

                                    }
                                    else if ("2".equals(snapshot.child("click").getValue()) || "3".equals(snapshot.child("click").getValue())) {
                                        cancelOrderLayout.setVisibility(View.GONE);
                                        acceptedCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed.setVisibility(View.GONE);
                                        solid.setVisibility(View.VISIBLE);
                                        String[] accept = String.valueOf(snapshot.child("accepted_time").getValue()).split(" ");
                                        acceptedTime.setText(accept[4]+" "+accept[5]);
                                        acceptedDate.setText(accept[0]+" "+accept[1].replace(",", ""));
                                        outforpickupCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed1.setVisibility(View.GONE);
                                        solid1.setVisibility(View.VISIBLE);

                                        String[] pickup = String.valueOf(snapshot.child("pickup_time").getValue()).split(" ");
                                        outforpickupTime.setText(pickup[4]+" "+pickup[5]);
                                        outforpickupDate.setText(pickup[0]+" "+pickup[1].replace(",", ""));

                                        preparingCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed2.setVisibility(View.GONE);
                                        solid2.setVisibility(View.VISIBLE);

                                        String[] prepare = String.valueOf(snapshot.child("prepare_time").getValue()).split(" ");
                                        preparingTime.setText(prepare[4]+" "+prepare[5]);
                                        preparingDate.setText(prepare[0]+" "+prepare[1].replace(",", ""));

                                        outfordeliveryCheck.setImageResource(R.drawable.icon_stepview_completed);
                                        dashed3.setVisibility(View.GONE);
                                        solid3.setVisibility(View.VISIBLE);

                                        String[] deliver = String.valueOf(snapshot.child("deliver_time").getValue()).split(" ");
                                        outfordeliveryTime.setText(deliver[4]+" "+deliver[5]);
                                        outfordeliveryDate.setText(deliver[0]+" "+deliver[1].replace(",", ""));

                                    }
                                } else if ("delivered".equals(snapshot.child("status").getValue())) {
                                    cancelOrderLayout.setVisibility(View.GONE);
                                    acceptedCheck.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed.setVisibility(View.GONE);
                                    solid.setVisibility(View.VISIBLE);
                                    String[] accept = String.valueOf(snapshot.child("accepted_time").getValue()).split(" ");
                                    acceptedTime.setText(accept[4]+" "+accept[5]);
                                    acceptedDate.setText(accept[0]+" "+accept[1].replace(",", ""));
                                    outforpickupCheck.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed1.setVisibility(View.GONE);
                                    solid1.setVisibility(View.VISIBLE);
                                    String[] pickup = String.valueOf(snapshot.child("pickup_time").getValue()).split(" ");
                                    outforpickupTime.setText(pickup[4]+" "+pickup[5]);
                                    outforpickupDate.setText(pickup[0]+" "+pickup[1].replace(",", ""));
                                    preparingCheck.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed2.setVisibility(View.GONE);
                                    solid2.setVisibility(View.VISIBLE);
                                    String[] prepare = String.valueOf(snapshot.child("prepare_time").getValue()).split(" ");
                                    preparingTime.setText(prepare[4]+" "+prepare[5]);
                                    preparingDate.setText(prepare[0]+" "+prepare[1].replace(",", ""));
                                    outfordeliveryCheck.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed3.setVisibility(View.GONE);
                                    solid3.setVisibility(View.VISIBLE);
                                    String[] deliver = String.valueOf(snapshot.child("deliver_time").getValue()).split(" ");
                                    outfordeliveryTime.setText(deliver[4]+" "+deliver[5]);
                                    outfordeliveryDate.setText(deliver[0]+" "+deliver[1].replace(",", ""));
                                    deliveredCheck.setImageResource(R.drawable.icon_stepview_completed);

                                    String[] delivered = String.valueOf(snapshot.child("delivered_time").getValue()).split(" ");
                                    deliveredTime.setText(delivered[4]+" "+delivered[5]);
                                    deliveredDate.setText(delivered[0]+" "+delivered[1].replace(",", ""));


                                    View view = LayoutInflater.from(TrackActivity.this).inflate(R.layout.confimation_dialog_view, null);
                                    View titleView = LayoutInflater.from(TrackActivity.this).inflate(R.layout.custom_dialog_title, null);

                                    TextView title = titleView.findViewById(R.id.title);
                                    title.setText("CONFIRMATION");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this, R.style.AlertDialogTheme);
                                    builder.setCustomTitle(titleView);
                                    builder.setView(view);
                                    builder.setCancelable(false);

                                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            FirebaseDatabase.getInstance().getReference().child("orders")
                                                    .orderByChild("order_id").equalTo(String.valueOf(snapshot.child("order_id").getValue()))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                                snapshot1.getRef().child("status")
                                                                        .setValue("confirmed")
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                View view = LayoutInflater.from(TrackActivity.this).inflate(R.layout.ratefeedbacks_dialog_view, null);
                                                                                View titleView = LayoutInflater.from(TrackActivity.this).inflate(R.layout.custom_dialog_title, null);

                                                                                TextView title = titleView.findViewById(R.id.title);
                                                                                title.setText("RATES & FEEDBACKS");

                                                                                RatingBar ratingBar = view.findViewById(R.id.rating_bar);
                                                                                TextInputEditText feedbacks = view.findViewById(R.id.feedbacks);


                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this, R.style.AlertDialogTheme);
                                                                                builder.setCustomTitle(titleView);
                                                                                builder.setView(view);

                                                                                builder.setNegativeButton("not now", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        startActivity(new Intent(TrackActivity.this, HomeActivity.class));
                                                                                        finish();
                                                                                    }
                                                                                });

                                                                                builder.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                                        Toast.makeText(getApplicationContext(), "Review Posted", Toast.LENGTH_SHORT).show();

                                                                                        String id = FirebaseDatabase.getInstance().getReference().push().getKey();
                                                                                        Map<String, Object> map = new HashMap<>();
                                                                                        map.put("rating_id", id);
                                                                                        map.put("customer_id", new Session(getApplicationContext()).getId());
                                                                                        map.put("client_id", getIntent().getStringExtra("client_id"));
                                                                                        map.put("rate", String.valueOf(ratingBar.getRating()));
                                                                                        map.put("feedback", String.valueOf(feedbacks.getText()).trim());

                                                                                        FirebaseDatabase.getInstance().getReference().child("ratings")
                                                                                                .child(id).setValue(map)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        startActivity(new Intent(TrackActivity.this, HomeActivity.class));
                                                                                                        finish();
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });

                                                                                AlertDialog alertDialog = builder.create();
                                                                                alertDialog.show();
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

                                    if (!TrackActivity.this.isFinishing()) {
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }

                                }

                                paymentMethod.setText(String.valueOf(snapshot.child("payment").getValue()));

                            }
                            else {
                                if ("confirmed".equals(snapshot.child("status").getValue())) {
                                    historyLayout.setVisibility(View.VISIBLE);
                                    historyTime.setText(String.valueOf(snapshot.child("delivered_time").getValue()));
                                    paymentMethod.setText(String.valueOf(snapshot.child("payment").getValue()));
                                }
                                else if ("declined".equals(snapshot.child("status").getValue())) {
                                    declinedLayout.setVisibility(View.VISIBLE);
                                    String refund = snapshot.child("payment").getValue()+"\t\t\t\t\t<i>( Refunded\t\t—\t\t\t₱"+snapshot.child("total_amount").getValue()+" )</i>";
                                    paymentMethod.setText(Html.fromHtml(refund));
                                    personnelLayout.setVisibility(View.GONE);
                                }
                            }

                            deliveryAddress.setText(String.valueOf(snapshot.child("delivery_address").getValue()));
                            if ("FREE".equals(snapshot.child("delivery_fee").getValue())) {
                                subtotal.setText(String.valueOf(snapshot.child("total_amount").getValue()));
                            }
                            else {
                                int sub = Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()))
                                        - Integer.parseInt(String.valueOf(snapshot.child("delivery_fee").getValue()));
                                subtotal.setText(String.valueOf(sub));
                            }
                            deliveryFee.setText(String.valueOf(snapshot.child("delivery_fee").getValue()));
                            orderTotal.setText(String.valueOf(snapshot.child("total_amount").getValue()));


                            FirebaseDatabase.getInstance().getReference().child("personnels")
                                    .orderByChild("per_id").equalTo(String.valueOf(snapshot.child("per_id").getValue()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                personImage = String.valueOf(snapshot.child("pic").getValue());
                                                Picasso.get()
                                                        .load(String.valueOf(snapshot.child("pic").getValue()))
                                                        .fit()
                                                        .centerCrop()
                                                        .placeholder(R.drawable.refillssss)
                                                        .into(personnel_profile);
                                                personnel_name.setText(capitalize(String.valueOf(snapshot.child("per_name").getValue())));
                                                perContact = String.valueOf(snapshot.child("per_contact").getValue());

                                                dialogFragment.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                            if ("".equals(snapshot.child("notes").getValue())) {
                                notesLayout.setVisibility(View.GONE);
                            }
                            else {
                                notes.setText(String.valueOf(snapshot.child("notes").getValue()));
                            }

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
                                        String.valueOf(snapshot1.child("water_type").getValue()),
                                        String.valueOf(snapshot1.child("image").getValue())
                                ));

                            }
                        }
                        recyclerView.setAdapter(new ScheduleProductAdapter(TrackActivity.this, scheduleProductModelList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        contactStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(TrackActivity.this).inflate(R.layout.contactstation_dialog_view, null);
                View titleView = LayoutInflater.from(TrackActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("CONTACT STATION");

                TextView call = view.findViewById(R.id.call);
                TextView sms = view.findViewById(R.id.sms);

                AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Uri call = Uri.parse("tel:" + contactNumber);
                        Intent surf = new Intent(Intent.ACTION_DIAL, call);
                        startActivity(surf);

                    }
                });

                sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent send = new Intent(Intent.ACTION_SENDTO);
                        send.setData(Uri.parse("smsto:" + Uri.encode(contactNumber)));
                        startActivity(send);
                    }
                });

            }
        });

        viewStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackActivity.this, ClientActivity.class);

                FirebaseDatabase.getInstance().getReference().child("clients")
                        .orderByChild("client_id").equalTo(getIntent().getStringExtra("client_id"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Object image = null;
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        image = data.child("store").getValue();
                                    }

                                    String finalStr = String.valueOf(snapshot.child("water_type").getValue()).substring(0, String.valueOf(snapshot.child("water_type").getValue()).length()-2);
                                    String[] str = String.valueOf(snapshot.child("water_type").getValue()).split(", ");

                                    if (str.length > 1) {
                                        finalStr = finalStr.substring(0, finalStr.lastIndexOf(","))+" &"+finalStr.substring(finalStr.lastIndexOf(",")+1);
                                        intent.putExtra("water_type", finalStr);
                                    }
                                    else {
                                        intent.putExtra("water_type", finalStr);
                                    }

                                    intent.putExtra("client_id", String.valueOf(snapshot.child("client_id").getValue()));
                                    intent.putExtra("company",  String.valueOf(snapshot.child("company").getValue()));
                                    intent.putExtra("address", String.valueOf(snapshot.child("address").getValue()));
                                    intent.putExtra("email",  String.valueOf(snapshot.child("email").getValue()));
                                    intent.putExtra("contact", String.valueOf(snapshot.child("contact").getValue()));
                                    intent.putExtra("storeImage", String.valueOf(image));
                                    intent.putExtra("no_of_filter", String.valueOf(snapshot.child("no_of_filter").getValue()));
                                    intent.putExtra("ship_fee", String.valueOf(snapshot.child("shipping_fee").getValue()));
                                }

                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });

        personnelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(TrackActivity.this).inflate(R.layout.personnel_dialog_view, null);

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

                name.setText(personnel_name.getText());
                contact.setText(perContact);

                AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this, R.style.AlertDialogTheme);
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

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(TrackActivity.this, R.style.AlertDialogTheme);
                builder.setTitle("Cancel Order?");
                builder.setMessage("Station may accept your order soon. Are you sure you want to cancel?\n" +
                        "\nPayments made will be refunded upon cancellation.");

                builder.setNeutralButton("stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("yes, cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("orders")
                                .orderByChild("order_id").equalTo(getIntent().getStringExtra("order_id"))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            FirebaseDatabase.getInstance().getReference().child("customers")
                                                    .orderByChild("customer_id").equalTo(String.valueOf(snapshot.child("customer_id").getValue()))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                           for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                               int refund = Integer.parseInt(String.valueOf(snapshot1.child("wallet").getValue()))
                                                                       + Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()));

                                                               snapshot1.getRef().child("wallet")
                                                                       .setValue(String.valueOf(refund))
                                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {
                                                                               snapshot.getRef().removeValue()
                                                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                           @Override
                                                                                           public void onSuccess(Void aVoid) {
                                                                                               Toast.makeText(getApplicationContext(), "ORDER CANCELLED", Toast.LENGTH_SHORT).show();
                                                                                               startActivity(new Intent(TrackActivity.this, TrackOrderActivity.class));
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });

                builder.create().show();

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
