package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView company, address, email, contact, toolbarTitle, waterTypes, type, nofilter, shipfee;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<ProductModel> productModelList = new ArrayList<>();
    List<String> quantityList = new ArrayList<>();
    DatabaseReference databaseReference;
    ImageView storeImage;
    LinearLayout noProductAvailable;
    String isForDelivery;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_client);
        isForDelivery = getIntent().getStringExtra("isForDelivery");
        MaterialCardView cardView = findViewById(R.id.materialCardView4);
        cardView.setBackgroundResource(R.drawable.card_background);
        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        company = findViewById(R.id.company);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        recyclerView = findViewById(R.id.recyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storeImage = findViewById(R.id.storeImage);
        waterTypes = findViewById(R.id.waterTypes);
        type = findViewById(R.id.type);
        nofilter = findViewById(R.id.nofilter);
        shipfee = findViewById(R.id.shipfee);
        noProductAvailable = findViewById(R.id.noProductAvailable);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_light);
        toolbarTitle.setVisibility(View.GONE);
        toolbarCard.setCardBackgroundColor(Color.TRANSPARENT);
        toolbarCard.setElevation(0);

        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Picasso.get()
                .load(getIntent().getStringExtra("storeImage"))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.station_image_placeholder)
                .into(storeImage);

        company.setText(getIntent().getStringExtra("company"));

        type.setText(getIntent().getStringExtra("water_type"));

        address.setText(getIntent().getStringExtra("address"));
        email.setText(getIntent().getStringExtra("email"));
        contact.setText(getIntent().getStringExtra("contact"));

        String n = getIntent().getStringExtra("no_of_filter")+" stages";
        nofilter.setText(n);

        String f = "₱"+getIntent().getStringExtra("ship_fee")+" / <i>(km)</i>";

        shipfee.setText(Html.fromHtml(f));

        String wts = getIntent().getStringExtra("water_type").replace(" & ", ", ");
        String[] wt = wts.split(", ");

        waterTypes.setText(wt[0]);

        Map<String, String> data = new HashMap<>();
        data.put("company", getIntent().getStringExtra("company"));
        data.put("client_id", getIntent().getStringExtra("client_id"));
        data.put("water_type", String.valueOf(waterTypes.getText()));
        data.put("ship_fee", getIntent().getStringExtra("ship_fee"));
        data.put("client_address", getIntent().getStringExtra("address"));

        waterTypes.setOnClickListener(v -> {
            View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog_view, null);
            View titleView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_title, null);

            ListView listView = view.findViewById(R.id.waterTypesList);
            TextView title = titleView.findViewById(R.id.title);

            title.setText("Water Type");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.list_content, wt);
            listView.setAdapter(adapter);

            listView.setAdapter(adapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setCustomTitle(titleView);
            builder.setView(view);

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            waterTypes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_up), null);

            builder.setOnCancelListener(dialog -> waterTypes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_down), null));

            AlertDialog alertDialog = builder.create();

            listView.setOnItemClickListener((parent, view1, position, id) -> {
                Object o = listView.getItemAtPosition(position);
                String str = (String) o;
                waterTypes.setText(str);

                data.put("water_type", String.valueOf(waterTypes.getText()));

                alertDialog.cancel();
            });

            alertDialog.show();
        });

        DialogFragment dialogFragment = LoadingScreen.getInstance();

        dialogFragment.show(getSupportFragmentManager(), "client_activity");
        dialogFragment.setCancelable(false);

        databaseReference.child("products").orderByChild("client_id").equalTo(getIntent().getStringExtra("client_id"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        productModelList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                for (DataSnapshot s : snap.getChildren()) {

                                    if (Objects.equals(s.child("status").getValue(), "available")) {

                                        Object refillPrice = 0;
                                        Object salePrice = 0;

                                        for (DataSnapshot d : s.child("service_types").getChildren()) {
                                            if (Objects.equals(d.getKey(), "refill")) {
                                                refillPrice = d.child("price").getValue();
                                            }

                                            if (Objects.equals(d.getKey(), "sale")) {
                                                salePrice = d.child("price").getValue();
                                            }
                                        }

                                        productModelList.add(new ProductModel(
                                                String.valueOf(s.getKey()),
                                                String.valueOf(s.child("product_image").getValue()),
                                                String.valueOf(refillPrice),
                                                String.valueOf(salePrice),
                                                String.valueOf(s.child("minimum_order").getValue()),
                                                String.valueOf(s.child("maximum_order").getValue())
                                        ));

                                        dialogFragment.dismiss();
                                    }

                                }

                            }

                        }


                        if (productModelList.size() == 0) {
                            noProductAvailable.setVisibility(View.VISIBLE);
                            dialogFragment.dismiss();
                        }
                        else {
                            noProductAvailable.setVisibility(View.GONE);
                        }

                        recyclerView.setAdapter(new ProductAdapter(ClientActivity.this, productModelList, data, isForDelivery));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.cart);
        item.setIcon(R.drawable.icon_cart_light);
        MenuItemCompat.setActionView(item, R.layout.cart_badge);
        RelativeLayout cart = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView cartIcon = cart.findViewById(R.id.cartIcon);
        final TextView badge = cart.findViewById(R.id.badge);

        String ref;

        if ("isForDelivery".equals(isForDelivery)) {
            cartIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_view_list_light));
            ref = "deliveries";
        }
        else {
            cartIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_cart_light));
            ref = "carts";
        }

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientActivity.this, CartActivity.class);
                intent.putExtra("client", getIntent().getStringExtra("company"));
                intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
                intent.putExtra("client_address", getIntent().getStringExtra("address"));
                intent.putExtra("ship_fee", getIntent().getStringExtra("ship_fee"));
                intent.putExtra("isForDelivery", isForDelivery);
                startActivity(intent);
            }
        });

        databaseReference.child(ref).orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
                                if (Integer.parseInt(String.valueOf(snapshot.child("products").getChildrenCount())) < 1) {
                                    badge.setVisibility(View.GONE);
                                }
                                else {
                                    badge.setVisibility(View.VISIBLE);
                                    badge.setText(String.valueOf(snapshot.child("products").getChildrenCount()));
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
