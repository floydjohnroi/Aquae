package com.example.aquae;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PlaceOrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, orderTime, location;
    RadioGroup paymentMethod;
    RadioButton method;
    String payment;
    MaterialButton placeOrder;
    ConstraintLayout placeOrderLayout;

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

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText(R.string.order_summary);


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

        method = (RadioButton) paymentMethod.getChildAt(0);
        method.setChecked(true);

        method = findViewById(paymentMethod.getCheckedRadioButtonId());
        payment = method.getText().toString();

        paymentMethod.setOnCheckedChangeListener((group, checkedId) -> {

            method = findViewById(checkedId);
            payment = method.getText().toString();

        });

        placeOrder.setOnClickListener(v -> {

            if (payment.equals("Aquae Wallet")) {
                View view = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.wallet_dialog_layout, null);
                View titleView = LayoutInflater.from(PlaceOrderActivity.this).inflate(R.layout.custom_dialog_title, null);

                TextView title = titleView.findViewById(R.id.title);
                title.setText("Pay with Aquae Wallet");

                TextView balance = view.findViewById(R.id.balance);
                TextView totalPayment = view.findViewById(R.id.totalPayment);

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

                            String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                            Map<String, String> order = new HashMap<>();
                            order.put("order_id", id);
                            order.put("customer_id", new Session(getApplicationContext()).getId());
                            order.put("order_time", (String) orderTime.getText());
                            order.put("payment", payment);
                            order.put("status", "pending");

                            FirebaseDatabase.getInstance().getReference().child("orders")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child(id).setValue(order);
                                            Toast.makeText(PlaceOrderActivity.this, "ORDER PLACED", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(PlaceOrderActivity.this, HomeActivity.class));
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
                String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                Map<String, String> order = new HashMap<>();
                order.put("order_id", id);
                order.put("customer_id", new Session(getApplicationContext()).getId());
                order.put("order_time", (String) orderTime.getText());
                order.put("payment", payment);
                order.put("status", "pending");

                FirebaseDatabase.getInstance().getReference().child("orders")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().child(id).setValue(order);
                                Toast.makeText(PlaceOrderActivity.this, "ORDER PLACED", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PlaceOrderActivity.this, HomeActivity.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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
