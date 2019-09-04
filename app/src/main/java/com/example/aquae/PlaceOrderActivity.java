package com.example.aquae;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PlaceOrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, orderTime, location, notes, client, km, distance, shipfee, total;
    RadioGroup paymentMethod;
    RadioButton method;
    String payment;
    MaterialButton placeOrder;
    ConstraintLayout placeOrderLayout;
    LinearLayout notesLayout;
    RecyclerView recyclerView;

    List<CheckOutProductModel> checkOutProductModelList = new ArrayList<>();

    int qtyr;
    int qtyp;

    Map<String, Object> items = new HashMap<>();

    Map<String, Object> maps = new HashMap<>();

    Map<String, Object> map = new HashMap<>();

    public Double toLatitude, toLongitude, fromLatitude, fromLongitude;

    int newT = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        orderTime = findViewById(R.id.order_time);
        paymentMethod = findViewById(R.id.paymentMethod);
        placeOrder = findViewById(R.id.placeOrder);
        location = findViewById(R.id.location);
        placeOrderLayout = findViewById(R.id.placeOrderLayout);
        notes = findViewById(R.id.notes);
        notesLayout = findViewById(R.id.notesLayout);
        client = findViewById(R.id.client);
        recyclerView = findViewById(R.id.recyclerView);
        km = findViewById(R.id.km);
        distance = findViewById(R.id.distance);
        shipfee = findViewById(R.id.shipfee);
        total = findViewById(R.id.total);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText(R.string.order_summary);

        MaterialCardView cardView = findViewById(R.id.materialCardView9);
        cardView.setBackgroundResource(R.drawable.card_background);

        String k = "DISTANCE <i>(km)</i>";
        km.setText(Html.fromHtml(k));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

//        ToGeoLocation.getAddress(getIntent().getStringExtra("delivery_address"), getApplicationContext());
//
//        FromGeoLocation.getAddress(getIntent().getStringExtra("client_address"), getApplicationContext());

        //new GetDistanceAsyncTask(17.4511252, 78.3748113, 17.4200841, 78.4442193);

//         fromLongitude = FromGeoLocation.getFromGeoLocationBundle().getDouble("longitude");
//        toLongitude = ToGeoLocation.getToGeoLocationBundle().getDouble("longitude");
//        fromLatitude = FromGeoLocation.getFromGeoLocationBundle().getDouble("latitude");
//        toLatitude = ToGeoLocation.getToGeoLocationBundle().getDouble("latitude");




        orderTime.setText(new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault()).format(new Date()));

        Thread thread = new Thread() {

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);

                        runOnUiThread(() -> orderTime.setText(new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault()).format(new Date())));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();

        location.setText(getIntent().getStringExtra("delivery_address"));

        if (!getIntent().getStringExtra("notes").isEmpty()) {
            notesLayout.setVisibility(View.VISIBLE);
            notes.setText(getIntent().getStringExtra("notes"));
        }

        method = (RadioButton) paymentMethod.getChildAt(0);
        method.setChecked(true);

        method = findViewById(paymentMethod.getCheckedRadioButtonId());
        payment = method.getText().toString();

        paymentMethod.setOnCheckedChangeListener((group, checkedId) -> {

            method = findViewById(checkedId);
            payment = method.getText().toString();

        });


        placeOrder.setOnClickListener(v -> {

            String tp = String.valueOf(total.getText()).replace("₱", "");
            String tps = tp.replace(".00", "");

            String id = FirebaseDatabase.getInstance().getReference().push().getKey();

            Map<String, Object> order = new HashMap<>();
            order.put("order_id", id);
            order.put("customer_id", new Session(getApplicationContext()).getId());
            order.put("client_id", getIntent().getStringExtra("client_id"));
            order.put("order_time", orderTime.getText());
            order.put("payment", payment);
            order.put("total_amount", tps);
            order.put("delivery_address", location.getText());
            order.put("items", items);
            order.put("status", "pending");


            if (payment.equals("Aquae Wallet")) {
                View view = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.wallet_dialog_layout, null);
                View titleView = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("Pay with Aquae Wallet");

                TextView balance = view.findViewById(R.id.balance);
                TextView totalPayment = view.findViewById(R.id.totalPayment);
                TextView textView2 = view.findViewById(R.id.textView2);


                totalPayment.setText(tps);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this, R.style.AlertDialogTheme);
                builder.setCustomTitle(titleView);
                builder.setView(view);

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (Integer.parseInt(String.valueOf(totalPayment.getText())) > Integer.parseInt(String.valueOf(balance.getText()))) {

                            Snackbar snackbar = Snackbar.make(placeOrderLayout, "Insufficient funds", 10000)
                                   .setAction("REQUEST", v1 -> startActivity(new Intent(PlaceOrderActivity.this, RequestCashInActivity.class)))
                                    .setActionTextColor(getResources().getColor(R.color.colorSnackBarAction));

                            View snackbarView = snackbar.getView();
                            snackbarView.setPadding(24, 24, 24, 24);
                            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setTextColor(getResources().getColor(R.color.colorWhite));

                            snackbar.show();
                        }
                        else {

                            FirebaseDatabase.getInstance().getReference().child("orders")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child(id).setValue(order);

//                                            FirebaseDatabase.getInstance().getReference().child("customers")
//                                                    .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
//                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//                                                                int w = Integer.parseInt(String.valueOf(snapshot.child("wallet").getValue())) - Integer.parseInt(tps);
//                                                                snapshot.getRef().child("wallet").setValue(String.valueOf(w));

//                                                                FirebaseDatabase.getInstance().getReference().child("clients")
//                                                                        .orderByChild("client_id").equalTo(getIntent().getStringExtra("client_id"))
//                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
//
//                                                                                    int w1 = Integer.parseInt(String.valueOf(snapshot1.child("wallet").getValue())) + Integer.parseInt(tps);
//                                                                                    snapshot1.getRef().child("wallet").setValue(String.valueOf(w1));

                                                                                    FirebaseDatabase.getInstance().getReference().child("carts")
                                                                                            .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
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
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                }
                                                                                            });

//                                                                                }
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                                            }
//                                                                        });

                                                                Toast.makeText(PlaceOrderActivity.this, "ORDER PLACED", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(PlaceOrderActivity.this, HomeActivity.class));
                                                                finish();
                                                                //Toast.makeText(PlaceOrderActivity.this, "NEW BALANCE : "+w, Toast.LENGTH_LONG).show();
//
//                                                            }
//
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }


                    }
                });



                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else {

                FirebaseDatabase.getInstance().getReference().child("orders")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().child(id).setValue(order);

                                FirebaseDatabase.getInstance().getReference().child("carts")
                                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                    if (Objects.equals(snapshot2.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {

                                                        for (DataSnapshot snap : snapshot2.child("products").getChildren()) {

                                                            if (snapshot2.child("products").getChildrenCount() == 1) {
                                                                snapshot2.getRef().removeValue();
                                                            }
                                                            else {
                                                                if (Objects.equals(snap.child("status").getValue(), "check")) {
                                                                    snap.getRef().removeValue();
                                                                }
                                                            }

                                                        }

                                                    }
                                                }

                                                Toast.makeText(PlaceOrderActivity.this, "ORDER PLACED", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(PlaceOrderActivity.this, HomeActivity.class));
                                                finish();

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


        });


        FirebaseDatabase.getInstance().getReference().child("carts")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int t = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
                                FirebaseDatabase.getInstance().getReference().child("clients")
                                        .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                    client.setText(String.valueOf(snap.child("company").getValue()));
                                                    String sf = "₱<b>" + snap.child("shipping_fee").getValue() + "</b>";
                                                    shipfee.setText(Html.fromHtml(sf));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    for (DataSnapshot s : shot.getChildren()) {
                                        for (DataSnapshot d : s.getChildren()) {
                                            if (Objects.equals(d.child("status").getValue(), "check")) {

                                                Object refillQuantity = 0;
                                                Object refillPrice = 0;
                                                Object purchaseQuantity = 0;
                                                Object purchasePrice = 0;

                                                map.put("water_type", Objects.requireNonNull(d.child("water_type").getValue()));

                                                for (DataSnapshot e : d.getChildren()) {

                                                    if (Objects.requireNonNull(e.getKey()).equals("refill")) {
                                                        refillQuantity = e.child("quantity").getValue();
                                                        refillPrice = e.child("price").getValue();

                                                        maps.put("quantity", Objects.requireNonNull(e.child("quantity").getValue()));
                                                        map.put("refill", maps);
                                                    }

                                                    if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
                                                        purchaseQuantity = e.child("quantity").getValue();
                                                        purchasePrice = e.child("price").getValue();

                                                        maps.put("quantity", Objects.requireNonNull(e.child("quantity").getValue()));
                                                        map.put("purchase", maps);
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

                                                qtyr += Integer.parseInt(String.valueOf(refillQuantity));
                                                qtyp += Integer.parseInt(String.valueOf(purchaseQuantity));

                                                t += Integer.parseInt(String.valueOf(d.child("subtotal").getValue()));
                                                String ft = "₱<b>" + t + ".00</b>";
                                                total.setText(Html.fromHtml(ft));

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

        recyclerView.setAdapter(new CheckOutProductAdapter(PlaceOrderActivity.this, checkOutProductModelList));


        RequestQueue queue = Volley.newRequestQueue(this);
        String origin =  getIntent().getStringExtra("client_address");
        String destination =  getIntent().getStringExtra("delivery_address");
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&avoid=tolls|highways&key=AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY";
        url = url.replaceAll(" ", "%20");

        String finalUrl = url;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("jsonObject", finalUrl);

                JSONObject jsonObject = new JSONObject(response);
                JSONArray legs = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                for (int i = 0; i < legs.length(); i++) {
                    JSONObject leg = legs.getJSONObject(i);
                    distance.setText(leg.getJSONObject("distance").getString("text"));

                    String[] dst = String.valueOf(distance.getText()).split("\\.");
                    String tp = String.valueOf(total.getText()).replace("₱", "");
                    String tps = tp.replace(".00", "");

                    newT = (Integer.parseInt(String.valueOf(shipfee.getText()).replace("₱", "")) * Integer.parseInt(dst[0])) + Integer.parseInt(tps);
                    String ft = "₱<b>" + newT + ".00</b>";
                    total.setText(Html.fromHtml(ft));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("error", error.toString());
        });

        queue.add(stringRequest);



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("DefaultLocale")
    public static String getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        // earth radius is in mile
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(lat_a))
                * Math.cos(Math.toRadians(lat_b)) * Math.sin(lngDiff / 2)
                * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;
        double kmConvertion = 1.6093;
        // return new Float(distance * meterConversion).floatValue();
        //return String.format("%.2f", (float) (distance * kmConvertion)) + " km";
        // return String.format("%.2f", distance)+" m";

        return (int) (distance * kmConvertion) + " km";
    }
}
