package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ManageAddressesActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle;
    RecyclerView recyclerView;
    List<AddressModel> addressModelList = new ArrayList<>();
    FloatingActionButton floatingActionButton;
    LinearLayout noAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_addresses);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        noAddress = findViewById(R.id.no_address);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Manage Address");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DialogFragment dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "manage_address_activity");

        FirebaseDatabase.getInstance().getReference().child("customers")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        addressModelList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!snapshot.child("addresses").exists()) {
                                noAddress.setVisibility(View.VISIBLE);
                                dialogFragment.dismiss();
                            }
                            else {
                                for (DataSnapshot snap : snapshot.child("addresses").getChildren()) {
                                    addressModelList.add(new AddressModel(
                                            String.valueOf(snap.getValue())
                                    ));
                                }
                                noAddress.setVisibility(View.GONE);
                                dialogFragment.dismiss();

                            }
                        }
                        recyclerView.setAdapter(new AddressesAdapter(ManageAddressesActivity.this, addressModelList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    floatingActionButton.setVisibility(View.GONE);
                } else if (dy < 0) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY");
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("PH")
                        .build(getApplicationContext());
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
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
                            Toast.makeText(ManageAddressesActivity.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();

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
