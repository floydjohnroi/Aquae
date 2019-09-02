package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, productName, quantityRefill, quantityPurchase, subtotal, refillPrice, purchasePrice, minOrder, maxOrder;
    ImageView minusRefill, addRefill, minusPurchase, addPurchase, productImage;
    MaterialButton addToCart, waterType;
    DatabaseReference databaseReference;
    CheckBox refillCheckBox, purchaseCheckBox;
    LinearLayout refillLayout, purchaseLayout;
    View divider;
    int qtyRefill;
    int qtyPurchase;
    int sub;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorLight));
        setContentView(R.layout.activity_order);

        toolbar = findViewById(R.id.toolbar);
        toolbarCard = findViewById(R.id.toolbarCard);
        toolbarTitle = findViewById(R.id.title);
        productName = findViewById(R.id.productName);
        minusRefill = findViewById(R.id.minusRefill);
        quantityRefill = findViewById(R.id.quantityRefill);
        addRefill = findViewById(R.id.addRefill);
        minusPurchase = findViewById(R.id.minusPurchase);
        quantityPurchase = findViewById(R.id.quantityPurchase);
        addPurchase = findViewById(R.id.addPurchase);
        subtotal = findViewById(R.id.subtotal);
        addToCart = findViewById(R.id.addToCart);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        waterType = findViewById(R.id.waterType);
        productImage = findViewById(R.id.productImage);
        refillPrice = findViewById(R.id.refillPrice);
        purchasePrice = findViewById(R.id.purchasePrice);
        refillCheckBox = findViewById(R.id.refillCheckBox);
        purchaseCheckBox = findViewById(R.id.purchaseCheckBox);
        refillLayout = findViewById(R.id.refillLayout);
        purchaseLayout = findViewById(R.id.purchaseLayout);
        divider = findViewById(R.id.divider1);
        minOrder = findViewById(R.id.minOrder);
        maxOrder = findViewById(R.id.maxOrder);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setElevation(0);
        toolbarCard.setCardBackgroundColor(Color.TRANSPARENT);
        toolbarTitle.setText(getIntent().getStringExtra("company"));
        toolbarTitle.setGravity(Gravity.CENTER);


        if (getIntent().getStringExtra("product") != null) {

            FirebaseDatabase.getInstance().getReference()
                    .child("carts").orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        for (DataSnapshot shot : snap.getChildren()) {
                                            for (DataSnapshot d : shot.getChildren()) {
                                                if (Objects.equals(d.getKey(), getIntent().getStringExtra("product"))) {
                                                    if (Objects.equals(d.child("water_type").getValue(), getIntent().getStringExtra("water_type"))) {

                                                        int rq = 0;
                                                        int pq = 0;


                                                        if (shot.hasChild(getIntent().getStringExtra("product"))) {
                                                            if (d.child("refill").child("quantity").getValue() != null) {
                                                                rq = Integer.parseInt(String.valueOf(d.child("refill").child("quantity").getValue()));
                                                            }

                                                            if (d.child("purchase").child("quantity").getValue() != null) {
                                                                pq = Integer.parseInt(String.valueOf(d.child("purchase").child("quantity").getValue()));
                                                            }
                                                        }

                                                        int tq = rq + pq;

                                                        String stq = tq + " " + capitalize(getIntent().getStringExtra("product")) + "("+d.child("water_type").getValue()+")";

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this, R.style.AlertDialogTheme);
                                                        builder.setTitle(stq);
                                                        builder.setMessage("This product is already in your cart.");
                                                        builder.setPositiveButton("VIEW CART", (dialog, which) -> {
                                                            Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                                                            intent.putExtra("client", getIntent().getStringExtra("company"));
                                                            intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
                                                            startActivity(intent);
                                                            finish();
                                                        });

                                                        builder.setOnKeyListener((dialog, keyCode, event) -> {

                                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                                dialog.cancel();
                                                                finish();
                                                            }

                                                            return true;
                                                        });

                                                        AlertDialog alertDialog = builder.create();
                                                        alertDialog.setCanceledOnTouchOutside(false);
                                                        alertDialog.show();

                                                    }
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

        }

        if (getIntent().getStringExtra("refillPrice").equals("0")) {
            refillLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("purchasePrice").equals("0")) {
            purchaseLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(getIntent().getStringExtra("productImage"))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.refillssss)
                .into(productImage);

        productName.setText(capitalize(getIntent().getStringExtra("product")));

        waterType.setText(getIntent().getStringExtra("water_type"));

        String min = getIntent().getStringExtra("min_order")+" <i>(qty)</i>";
        minOrder.setText(Html.fromHtml(min));

        String max = getIntent().getStringExtra("max_order")+" <i>(qty)</i>";
        maxOrder.setText(Html.fromHtml(max));


        refillPrice.setText(getIntent().getStringExtra("refillPrice"));

        purchasePrice.setText(getIntent().getStringExtra("purchasePrice"));

        minusRefill.setEnabled(false);
        minusPurchase.setEnabled(false);

        refillCheckBox.setChecked(true);
        purchaseCheckBox.setChecked(true);

        refillCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!purchaseCheckBox.isChecked() && !isChecked) {
                addToCart.setEnabled(false);
                sub = 0;
                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }
            else if (isChecked) {
                addToCart.setEnabled(true);

                sub = sub + (Integer.parseInt(getIntent().getStringExtra("refillPrice")) * Integer.parseInt(getIntent().getStringExtra("min_order")));

                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }
            else {
                sub = sub - (Integer.parseInt(getIntent().getStringExtra("refillPrice")) * Integer.parseInt(getIntent().getStringExtra("min_order")));

                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }

        });

        purchaseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!refillCheckBox.isChecked() && !isChecked) {
                addToCart.setEnabled(false);
                sub = 0;
                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }
            else if (isChecked) {
                addToCart.setEnabled(true);

                sub = sub + (Integer.parseInt(getIntent().getStringExtra("purchasePrice")) * Integer.parseInt(getIntent().getStringExtra("min_order")));

                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }
            else {
                sub = sub - (Integer.parseInt(getIntent().getStringExtra("purchasePrice")) * Integer.parseInt(getIntent().getStringExtra("min_order")));

                String s = "\u20B1<b>"+sub+".00</b>";
                subtotal.setText(Html.fromHtml(s));
            }

        });

        qtyRefill = Integer.parseInt(getIntent().getStringExtra("min_order"));
        qtyPurchase = Integer.parseInt(getIntent().getStringExtra("min_order"));

        quantityRefill.setText(getIntent().getStringExtra("min_order"));
        quantityPurchase.setText(getIntent().getStringExtra("min_order"));

        sub = (Integer.parseInt(getIntent().getStringExtra("refillPrice")) * Integer.parseInt(getIntent().getStringExtra("min_order"))) +
                (Integer.parseInt(getIntent().getStringExtra("purchasePrice")) * Integer.parseInt(getIntent().getStringExtra("min_order")));

        String s = "\u20B1<b>"+sub+".00</b>";
        subtotal.setText(Html.fromHtml(s));

        minusRefill.setOnClickListener(v -> {

            qtyRefill--;

            sub = sub - Integer.parseInt(refillPrice.getText().toString());
            quantityRefill.setText(String.valueOf(qtyRefill));
            String s12 = "\u20B1<b>"+sub+".00</b>";
            subtotal.setText(Html.fromHtml(s12));

            if (qtyRefill == Integer.parseInt(getIntent().getStringExtra("min_order"))) {
                minusRefill.setEnabled(false);
            }
            else {
                addRefill.setEnabled(true);
            }

        });

        addRefill.setOnClickListener(v -> {

            qtyRefill = Integer.parseInt((String) quantityRefill.getText());

            qtyRefill++;
            sub = sub + Integer.parseInt(refillPrice.getText().toString());
            quantityRefill.setText(String.valueOf(qtyRefill));
            String s13 = "\u20B1<b>"+sub+".00</b>";
            subtotal.setText(Html.fromHtml(s13));
            minusRefill.setEnabled(true);

            refillCheckBox.setChecked(true);

            if (qtyRefill == Integer.parseInt(getIntent().getStringExtra("max_order"))) {
                addRefill.setEnabled(false);
            }


        });

        minusPurchase.setOnClickListener(v -> {
            qtyPurchase--;

            sub = sub - Integer.parseInt(purchasePrice.getText().toString());
            quantityPurchase.setText(String.valueOf(qtyPurchase));
            String s12 = "\u20B1<b>"+sub+".00</b>";
            subtotal.setText(Html.fromHtml(s12));

            if (qtyPurchase == Integer.parseInt(getIntent().getStringExtra("min_order"))) {
                minusPurchase.setEnabled(false);
            }
            else {
                addPurchase.setEnabled(true);
            }
        });

        addPurchase.setOnClickListener(v -> {
            qtyPurchase = Integer.parseInt((String) quantityPurchase.getText());

            qtyPurchase++;
            sub = sub + Integer.parseInt(purchasePrice.getText().toString());
            quantityPurchase.setText(String.valueOf(qtyPurchase));
            String s13 = "\u20B1<b>"+sub+".00</b>";
            subtotal.setText(Html.fromHtml(s13));
            minusPurchase.setEnabled(true);

            purchaseCheckBox.setChecked(true);

            if (qtyPurchase == Integer.parseInt(getIntent().getStringExtra("max_order"))) {
                addPurchase.setEnabled(false);
            }

        });

        addToCart.setOnClickListener(v -> {

            final String id = Objects.requireNonNull(databaseReference.push().getKey());

            Map<String, String> refill = new HashMap<>();
            refill.put("price", getIntent().getStringExtra("refillPrice"));
            refill.put("quantity", String.valueOf(qtyRefill));

            Map<String, String> purchase = new HashMap<>();
            purchase.put("price", getIntent().getStringExtra("purchasePrice"));
            purchase.put("quantity", String.valueOf(qtyPurchase));

            String s1 = String.valueOf(subtotal.getText()).substring(1);
            String sub = s1.replace(".00", "");

            Map<String, Object> details = new HashMap<>();
            details.put("image", getIntent().getStringExtra("productImage"));
            details.put("min_order", getIntent().getStringExtra("min_order"));
            details.put("max_order", getIntent().getStringExtra("max_order"));
            details.put("subtotal", sub);
            details.put("water_type", getIntent().getStringExtra("water_type"));
            details.put("status", "check");
            if (refillCheckBox.isChecked() && refillLayout.getVisibility() == View.VISIBLE)
                details.put("refill", refill);
            if (purchaseCheckBox.isChecked() && purchaseLayout.getVisibility() == View.VISIBLE)
                details.put("purchase", purchase);

            final Map<String, Object> products = new HashMap<>();
            products.put(getIntent().getStringExtra("product").toLowerCase(), details);

            final Map<String, Object> prod = new HashMap<>();
            prod.put(String.valueOf(databaseReference.push().getKey()), products);


            final Map<String, Object> data = new HashMap<>();
            data.put("cart_id", id);
            data.put("customer_id", new Session(getApplicationContext()).getId());
            data.put("client_id", getIntent().getStringExtra("client_id"));
            data.put("products", prod);

            databaseReference.child("carts").orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.exists()) {
                                dataSnapshot.getRef().child(id).setValue(data);
                                Toast.makeText(OrderActivity.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                                dataSnapshot.getRef().orderByChild("client_id").equalTo(getIntent().getStringExtra("client_id"))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (!dataSnapshot.exists()) {
                                                    dataSnapshot.getRef().child(id).setValue(data);
                                                    Toast.makeText(OrderActivity.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {

                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                                                        dataSnapshot.getRef().child(snap.child("cart_id").getValue() + "/products/").push().setValue(products);
                                                        Toast.makeText(OrderActivity.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    }

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

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.cart);
        item.setIcon(R.drawable.icon_cart_dark);
        MenuItemCompat.setActionView(item, R.layout.cart_badge);
        RelativeLayout cart = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView cartIcon = cart.findViewById(R.id.cartIcon);
        final TextView badge = cart.findViewById(R.id.badge);
        cartIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_cart_dark));
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, CartActivity.class);
                intent.putExtra("client", getIntent().getStringExtra("company"));
                intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
                startActivity(intent);

            }
        });

        databaseReference.child("carts").orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int c = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
