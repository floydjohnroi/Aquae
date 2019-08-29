package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView company, address, email, contact, toolbarTitle, waterTypes;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<ProductModel> productModelList = new ArrayList<>();
    List<String> quantityList = new ArrayList<>();
    DatabaseReference databaseReference;
    ImageView storeImage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_client);

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
        address.setText(getIntent().getStringExtra("address"));
        email.setText(getIntent().getStringExtra("email"));
        contact.setText(getIntent().getStringExtra("contact"));



        String[] wt = getIntent().getStringExtra("water_type").split(",");

        waterTypes.setText(wt[0]);

        Map<String, String> data = new HashMap<>();
        data.put("company", getIntent().getStringExtra("company"));
        data.put("client_id", getIntent().getStringExtra("client_id"));
        data.put("water_type", String.valueOf(waterTypes.getText()));
        data.put("min_order", getIntent().getStringExtra("min_order"));
        data.put("max_order", getIntent().getStringExtra("max_order"));
        data.put("ship_fee", getIntent().getStringExtra("ship_fee"));

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
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            for (DataSnapshot snap : snapshot.getChildren()) {

                                for (DataSnapshot s : snap.getChildren()) {

                                    for (DataSnapshot d: s.getChildren()) {

                                        Object refillPrice = 0;
                                        Object salePrice = 0;

                                        for (DataSnapshot e : d.getChildren()) {

                                            if (Objects.equals(e.getKey(), "refill")) {
                                                refillPrice = e.child("price").getValue();
                                            }

                                            if (Objects.equals(e.getKey(), "sale")) {
                                                salePrice = e.child("price").getValue();
                                            }

                                            productModelList.add(new ProductModel(
                                                    String.valueOf(s.getKey()),
                                                    String.valueOf(s.child("product_image").getValue()),
                                                    String.valueOf(refillPrice),
                                                    String.valueOf(salePrice)
                                            ));

                                            dialogFragment.dismiss();

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

        recyclerView.setAdapter(new ProductAdapter(ClientActivity.this, productModelList, data));

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
        cartIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_cart_light));
        cartIcon.setOnClickListener(v -> startActivity(new Intent(ClientActivity.this, CartActivity.class)));

        databaseReference.child("carts").orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int c = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                for (DataSnapshot d : data.getChildren()) {
                                    for (DataSnapshot e : d.getChildren()) {
                                        for (DataSnapshot f : e.getChildren()) {
                                            try {
                                                c += Integer.parseInt(String.valueOf(f.child("quantity").getValue()));
                                            } catch (NumberFormatException nfe) {
                                                nfe.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (c < 1) {
                            badge.setVisibility(View.GONE);
                        }
                        else if (c > 9) {
                            badge.setVisibility(View.VISIBLE);
                            badge.setText("9+");
                        }
                        else {
                            badge.setVisibility(View.VISIBLE);
                            badge.setText(String.valueOf(c));
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
