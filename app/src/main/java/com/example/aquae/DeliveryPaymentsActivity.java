package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DeliveryPaymentsActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle, addNew;
    MaterialButton proceed;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    List<AddressModel> addressModelList = new ArrayList<>();
    EditText note;
    String address;

    TextInputEditText addr;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverypayments);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        proceed = findViewById(R.id.proceed);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerView);
        addNew = findViewById(R.id.add_new);
        note = findViewById(R.id.note);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText(R.string.delivery_payments);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseDatabase.getInstance().getReference().child("customers")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        addressModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot snap : snapshot.child("addresses").getChildren()) {
                                addressModelList.add(new AddressModel(
                                        String.valueOf(snap.getValue())
                                ));
                            }
                        }


                        recyclerView.setAdapter(new AddressAdapter(DeliveryPaymentsActivity.this, addressModelList, addresses -> address = addresses));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        proceed.setOnClickListener(v -> {

            Intent intent = new Intent(DeliveryPaymentsActivity.this, PlaceOrderActivity.class);
            intent.putExtra("delivery_address", address);
            intent.putExtra("notes", String.valueOf(note.getText()).trim());
            intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
            intent.putExtra("client_address", getIntent().getStringExtra("client_address"));
            intent.putExtra("ship_fee", getIntent().getStringExtra("ship_fee"));
            intent.putExtra("client", getIntent().getStringExtra("client"));
            startActivity(intent);

        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY");
        }

        addNew.setOnClickListener(v -> {

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("PH")
                    .build(getApplicationContext());
            startActivityForResult(intent, 0);

//            View view = LayoutInflater.from(DeliveryPaymentsActivity.this).inflate(R.layout.address_dialog_view, null);
//            View titleView = LayoutInflater.from(DeliveryPaymentsActivity.this).inflate(R.layout.custom_dialog_title, null);
//
//            TextView title = titleView.findViewById(R.id.title);
//            title.setText(R.string.add_new_address);
//
//            addr = view.findViewById(R.id.address);
//
//            addr.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                            .setCountry("PH")
//                            .build(getApplicationContext());
//                    startActivityForResult(intent, 0);
//                }
//            });
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryPaymentsActivity.this, R.style.AlertDialogTheme);
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
//                            add.add(String.valueOf(addr.getText()));
//
//                            addressModelList.clear();
//
//                            dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).child("addresses").setValue(add);
//                            Toast.makeText(DeliveryPaymentsActivity.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();
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
//
//            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//
//            addr.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if ("".contentEquals(s.toString().trim())) {
//                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
//                    }
//                    else {
//                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
       });

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

                            add.add(String.valueOf(place.getName()));

                            addressModelList.clear();

                            dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).child("addresses").setValue(add);
                            Toast.makeText(DeliveryPaymentsActivity.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();

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
