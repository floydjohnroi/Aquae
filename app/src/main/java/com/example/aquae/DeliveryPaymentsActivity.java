package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            intent.putExtra("note", String.valueOf(note.getText()).trim());
            startActivity(intent);

        });

        addNew.setOnClickListener(v -> {

            View view = LayoutInflater.from(DeliveryPaymentsActivity.this).inflate(R.layout.address_dialog_view, null);
            View titleView = LayoutInflater.from(DeliveryPaymentsActivity.this).inflate(R.layout.custom_dialog_title, null);

            TextView title = titleView.findViewById(R.id.title);
            title.setText(R.string.add_new_address);

            TextInputEditText address = view.findViewById(R.id.address);

            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryPaymentsActivity.this, R.style.AlertDialogTheme);
            builder.setCustomTitle(titleView);
            builder.setView(view);

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.setPositiveButton("Add", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("customers")
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

                            add.add(String.valueOf(address.getText()));

                            addressModelList.clear();

                            dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).child("addresses").setValue(add);
                            Toast.makeText(DeliveryPaymentsActivity.this, "NEW ADDRESS ADDED", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }));


            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
