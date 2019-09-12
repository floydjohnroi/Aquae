package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TrackActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, orderId, orderTime, estTime, placedTime, placedDate, processedTime, processedDate, deliveredTime, deliveredDate;
    ImageView check, check1, check2, personnel_profile;
    LinearLayout dashed, dashed1, estLayout, notesLayout, placedLayout, processedLayout, deliveredLayout, historyLayout, declinedLayout;
    View line, line1;
    List<ScheduleProductModel> scheduleProductModelList = new ArrayList<>();
    RecyclerView recyclerView;
    TextView station, deliveryAddress, subtotal, deliveryFee, orderTotal, paymentMethod, notes, historyTime, personnel_name;
    MaterialButton contactStation, viewStation;
    String contactNumber, activity, personImage;
    LinearLayout personnelLayout;

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
        check = findViewById(R.id.check);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        dashed = findViewById(R.id.dashed);
        dashed1 = findViewById(R.id.dashed1);
        line = findViewById(R.id.line);
        line1 = findViewById(R.id.line1);
        placedTime = findViewById(R.id.placed_time);
        placedDate = findViewById(R.id.placed_date);
        processedTime = findViewById(R.id.processed_time);
        processedDate = findViewById(R.id.processed_date);
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
        placedLayout = findViewById(R.id.placed_layout);
        processedLayout = findViewById(R.id.processed_layout);
        deliveredLayout = findViewById(R.id.delivered_layout);
        historyLayout = findViewById(R.id.history_layout);
        historyTime = findViewById(R.id.history_time);
        declinedLayout = findViewById(R.id.declined_layout);
        personnelLayout = findViewById(R.id.personnel_layout);
        personnel_profile = findViewById(R.id.personnel_profile);
        personnel_name = findViewById(R.id.personnel_name);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));

        if ("OrderHistory".equals(activity)) {
            toolbarTitle.setText("Order History");
            estLayout.setVisibility(View.GONE);
            placedLayout.setVisibility(View.GONE);
            processedLayout.setVisibility(View.GONE);
            deliveredLayout.setVisibility(View.GONE);
            dashed.setVisibility(View.GONE);
            dashed1.setVisibility(View.GONE);
            contactStation.setVisibility(View.GONE);
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
                                }
                                else if ("accepted".equals(snapshot.child("status").getValue())) {
                                    check.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed.setVisibility(View.GONE);
                                    line.setVisibility(View.VISIBLE);
                                    placedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    placedDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));
                                } else if ("processing".equals(snapshot.child("status").getValue())) {
                                    check.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed.setVisibility(View.GONE);
                                    line.setVisibility(View.VISIBLE);
                                    placedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    placedDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));
                                    check1.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed1.setVisibility(View.GONE);
                                    line1.setVisibility(View.VISIBLE);
                                    processedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    processedDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));
                                } else if ("delivered".equals(snapshot.child("status").getValue())) {
                                    check.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed.setVisibility(View.GONE);
                                    line.setVisibility(View.VISIBLE);
                                    placedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    placedDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));
                                    check1.setImageResource(R.drawable.icon_stepview_completed);
                                    dashed1.setVisibility(View.GONE);
                                    line1.setVisibility(View.VISIBLE);
                                    processedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    processedDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));
                                    check2.setImageResource(R.drawable.icon_stepview_completed);
                                    deliveredTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));
                                    deliveredDate.setText(new SimpleDateFormat("MMM dd", Locale.getDefault()).format(new Date()));

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
                                            dialog.cancel();
                                        }
                                    });

                                    builder.setPositiveButton("send", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

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
                                                            Toast.makeText(getApplicationContext(), "Review Posted", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        }
                                                    });
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                }

                                paymentMethod.setText(String.valueOf(snapshot.child("payment").getValue()));

                            }
                            else {
                                if ("delivered".equals(snapshot.child("status").getValue())) {
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
                            int sub = Integer.parseInt(String.valueOf(snapshot.child("total_amount").getValue()))
                                    - Integer.parseInt(String.valueOf(snapshot.child("delivery_fee").getValue()));
                            subtotal.setText(String.valueOf(sub));
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
                                                personnel_name.setText(String.valueOf(snapshot.child("per_name").getValue()));
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
                                        String.valueOf(snapshot1.child("water_type").getValue())
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

                Picasso.get()
                        .load(personImage)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.refillssss)
                        .into(profile);

                name.setText(personnel_name.getText());

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


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
