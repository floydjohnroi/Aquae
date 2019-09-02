package com.example.aquae;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    MaterialButton removeAll;
    TextView toolbarTitle, total;
    RecyclerView recyclerView;
    List<CartModel> cartModelList = new ArrayList<>();
    CheckBox checkBoxAll;
    MaterialButton checkout, startShopping;
    ConstraintLayout cartLayout;
    LinearLayout emptyCart, selectAllLayout;
    CartAdapter cartAdapter;
    List<CartProductModel> cartProductModelList = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        checkBoxAll = findViewById(R.id.checkBoxAll);
        removeAll = findViewById(R.id.removeAll);
        total = findViewById(R.id.total);
        checkout = findViewById(R.id.checkout);
        emptyCart = findViewById(R.id.emptyCart);
        cartLayout = findViewById(R.id.cartLayout);
        selectAllLayout = findViewById(R.id.selectAllLayout);
        startShopping = findViewById(R.id.startShopping);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        String title = getIntent().getStringExtra("client")+" Cart";
        toolbarTitle.setText(title);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        checkBoxAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                removeAll.setEnabled(true);
                cartAdapter.selectAll();
            } else {
                removeAll.setEnabled(false);
                cartAdapter.unselectAll();
            }
        });

        removeAll.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Remove Items");
            builder.setMessage("These items will be removed. This action cannot be undone.");
            builder.setPositiveButton("REMOVE", (dialog, which) -> {
                FirebaseDatabase.getInstance().getReference().child("carts")
                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("client_id").getValue().equals(getIntent().getStringExtra("client_id"))) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                Toast.makeText(getApplicationContext(), "All item has been removed", Toast.LENGTH_SHORT).show();
                                cartAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
            builder.create().show();
        });

        startShopping.setOnClickListener(v -> {
//            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            finish();
        });

        checkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeliveryPaymentsActivity.class);
            intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
            intent.putExtra("client_address", getIntent().getStringExtra("client_address"));
            startActivity(intent);

//            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.full_screen_dialog1);
//
//            builder.setTitle("asd");
//            builder.setMessage("asd");
//
//            android.app.AlertDialog alertDialog = builder.create();
//            alertDialog.show();

        });

        DialogFragment dialogFragment = LoadingScreen.getInstance();

        dialogFragment.show(getSupportFragmentManager(), "cart_activity");
        dialogFragment.setCancelable(false);

//        FirebaseDatabase.getInstance().getReference().child("carts")
//                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        cartModelList.clear();
//                        cartProductModelList.clear();
//
//                        if (!dataSnapshot.exists()) {
//                            emptyCart.setVisibility(View.VISIBLE);
//                            selectAllLayout.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.GONE);
//                            checkout.setEnabled(false);
//                            dialogFragment.dismiss();
//
//                            String s = "₱<b>0.00</b>";
//                            total.setText(Html.fromHtml(s));
//                        } else {
//                            emptyCart.setVisibility(View.GONE);
//                            selectAllLayout.setVisibility(View.VISIBLE);
//                            recyclerView.setVisibility(View.VISIBLE);
//                            checkout.setEnabled(true);
//
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                                for (DataSnapshot snap : snapshot.getChildren()) {
//
//                                    for (DataSnapshot s : snap.getChildren()) {
//
//                                        for (DataSnapshot d : s.getChildren()) {
//
//                                            FirebaseDatabase.getInstance().getReference().child("products")
//                                                    .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
//                                                    .addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                                                for (DataSnapshot snap : snapshot.child("types").getChildren()) {
//                                                                    if (Objects.equals(snap.getKey(), d.getKey())) {
//
//                                                                        Object refillQuantity = 0;
//                                                                        Object refillPrice = 0;
//                                                                        Object purchaseQuantity = 0;
//                                                                        Object purchasePrice = 0;
//
//
//                                                                        for (DataSnapshot e : d.getChildren()) {
//
//                                                                            if (Objects.requireNonNull(e.getKey()).equals("refill")) {
//                                                                                refillQuantity = e.child("quantity").getValue();
//                                                                               // refillPrice = e.child("price").getValue();
//                                                                            }
//
//                                                                            if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
//                                                                                purchaseQuantity = e.child("quantity").getValue();
//                                                                                //purchasePrice = e.child("price").getValue();
//                                                                            }
//
//                                                                        }
//
//                                                                        for (DataSnapshot j : snap.child("service_types").getChildren()) {
//                                                                            if (Objects.equals(j.getKey(), "refill")) {
//                                                                                refillPrice = j.child("price").getValue();
//                                                                            }
//
//                                                                            if (Objects.equals(j.getKey(), "sale")) {
//                                                                                purchasePrice = j.child("price").getValue();
//                                                                            }
//                                                                        }
//
//                                                                        cartProductModelList.add(new CartProductModel(
//                                                                                String.valueOf(snapshot.child("client_id").getValue()),
//                                                                                String.valueOf(s.getKey()),
//                                                                                String.valueOf(d.getKey()),
//                                                                                String.valueOf(refillQuantity),
//                                                                                String.valueOf(purchaseQuantity),
//                                                                                String.valueOf(d.child("water_type").getValue()),
//                                                                                String.valueOf(refillPrice),
//                                                                                String.valueOf(purchasePrice),
//                                                                                String.valueOf(snap.child("product_image").getValue()),
//                                                                                String.valueOf(d.child("subtotal").getValue())
//                                                                        ));
//
//                                                                        dialogFragment.dismiss();
//
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            cartAdapter.notifyDataSetChanged();
//
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
//
//                                        }
//
//                                    }
//                                }
//
//                                FirebaseDatabase.getInstance().getReference().child("clients")
//                                        .orderByChild("client_id").equalTo(String.valueOf(snapshot.child("client_id").getValue()))
//                                        .addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//                                                    cartModelList.add(new CartModel(
//                                                            String.valueOf(snapshot.child("client_id").getValue()),
//                                                            String.valueOf(data.child("company").getValue()),
//                                                            String.valueOf(data.child("minimum_order").getValue()),
//                                                            String.valueOf(data.child("maximum_order").getValue()),
//                                                            String.valueOf(data.child("shipping_fee").getValue())
//
//                                                    ));
//
//                                                    dialogFragment.dismiss();
//
//                                                }
//
//                                                cartAdapter.notifyDataSetChanged();
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                            }
//                        }
//
//                        cartAdapter.notifyDataSetChanged();
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//
//
//                });
//
//        cartAdapter = new CartAdapter(CartActivity.this, cartModelList, cartProductModelList, total, new CartAdapter.OnDataChangeListener() {
//            @Override
//            public void onChanged(double totalAmount) {
//                String s = "₱<b>" + String.format(Locale.getDefault(), "%.2f", totalAmount) + "</b>";
//                total.setText(Html.fromHtml(s));
//            }
//
//            @Override
//            public void isSelectedAll(boolean isChecked) {
//                for (int i = 0; i < cartModelList.size(); i++) {
//                    CartAdapter.CartViewHolder viewHolder = (CartAdapter.CartViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//                    if (viewHolder != null) {
//                        if (isChecked)
//                            viewHolder.checkBox.setChecked(true);
//                        else
//                            viewHolder.checkBox.setChecked(false);
//                    }
//                }
//
//            }
//        });


        FirebaseDatabase.getInstance().getReference().child("carts")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int t = 0;

                        cartProductModelList.clear();

                        if (!dataSnapshot.exists()) {
                            emptyCart.setVisibility(View.VISIBLE);
                            selectAllLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            checkout.setEnabled(false);
                            dialogFragment.dismiss();

                        } else {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if (Objects.equals(snapshot.child("client_id").getValue(), getIntent().getStringExtra("client_id"))) {

                                    emptyCart.setVisibility(View.GONE);
                                    selectAllLayout.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    checkout.setEnabled(true);

                                    for (DataSnapshot snap : snapshot.getChildren()) {

                                        if(snap.getChildrenCount() == 0) {
                                            emptyCart.setVisibility(View.VISIBLE);
                                            selectAllLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.GONE);
                                            checkout.setEnabled(false);
                                            dialogFragment.dismiss();
                                        }
                                        else {

                                            emptyCart.setVisibility(View.GONE);
                                            selectAllLayout.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            checkout.setEnabled(true);

                                            for (DataSnapshot s : snap.getChildren()) {

                                                for (DataSnapshot d : s.getChildren()) {

                                                    Object refillQuantity = 0;
                                                    Object refillPrice = 0;
                                                    Object purchaseQuantity = 0;
                                                    Object purchasePrice = 0;
                                                    Object subtotal = 0;


                                                    if (Objects.equals(d.child("status").getValue(), "check")) {
                                                        t += Integer.parseInt(String.valueOf(d.child("subtotal").getValue()));
                                                        subtotal = Integer.parseInt(String.valueOf(d.child("subtotal").getValue()));
                                                    }


                                                    for (DataSnapshot e : d.getChildren()) {

                                                        if (Objects.requireNonNull(e.getKey()).equals("refill")) {
                                                            refillQuantity = e.child("quantity").getValue();
                                                            refillPrice = e.child("price").getValue();
                                                        }

                                                        if (Objects.requireNonNull(e.getKey()).equals("purchase")) {
                                                            purchaseQuantity = e.child("quantity").getValue();
                                                            purchasePrice = e.child("price").getValue();
                                                        }

                                                    }

                                                    cartProductModelList.add(new CartProductModel(
                                                            String.valueOf(snapshot.child("client_id").getValue()),
                                                            String.valueOf(s.getKey()),
                                                            String.valueOf(d.getKey()),
                                                            String.valueOf(refillQuantity),
                                                            String.valueOf(purchaseQuantity),
                                                            String.valueOf(d.child("water_type").getValue()),
                                                            String.valueOf(refillPrice),
                                                            String.valueOf(purchasePrice),
                                                            String.valueOf(d.child("image").getValue()),
                                                            String.valueOf(subtotal),
                                                            String.valueOf(d.child("min_order").getValue()),
                                                            String.valueOf(d.child("max_order").getValue()),
                                                            String.valueOf(d.child("status").getValue())

                                                    ));

                                                    dialogFragment.dismiss();

                                                }
                                            }

                                        }
                                    }

                                }
                                else {
                                    emptyCart.setVisibility(View.VISIBLE);
                                    selectAllLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    checkout.setEnabled(false);
                                    dialogFragment.dismiss();
                                }
                            }
                        }

                        String st = "₱<b>"+t+ ".00</b>";
                        total.setText(Html.fromHtml(st));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });


        cartAdapter = new CartAdapter(CartActivity.this, cartProductModelList, new CartAdapter.OnDataChangeListener() {
            @Override
            public void onChanged(double totalAmount) {
                String s = "₱<b>" + String.format(Locale.getDefault(), "%.2f", totalAmount) + "</b>";
                total.setText(Html.fromHtml(s));
            }

            @Override
            public void isSelectedAll(boolean isChecked) {
                for (int i = 0; i < cartProductModelList.size(); i++) {
                    CartAdapter.CartViewHolder viewHolder = (CartAdapter.CartViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null) {
                        if (isChecked) {
                            viewHolder.checkBoxes.setChecked(true);
                        }
                        else {
                            viewHolder.checkBoxes.setChecked(false);
                        }
                    }
                }

            }
        });

        recyclerView.setAdapter(cartAdapter);

        checkBoxAll.setChecked(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
