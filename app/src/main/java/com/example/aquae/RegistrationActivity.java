package com.example.aquae;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    ConstraintLayout accountInformation, deliveryInformation;
    TextInputLayout usernameLayout, confirmPasswordLayout, phoneNumberLayout;
    TextInputEditText firstname, lastname, username, password, confirmPassword, phoneNumber, address, address1, address2;
    ImageView passwordToggle;
    MaterialButton signInInstead, next, back, register;
    TextView information, note;
    DatabaseReference databaseReference;
    Session session;
    boolean flag;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        session = new Session(getApplicationContext());

        MaterialCardView cardView = findViewById(R.id.materialCardView3);
        cardView.setBackgroundResource(R.drawable.card_background);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        usernameLayout = findViewById(R.id.usernameLayout);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPassword = findViewById(R.id.confirmPassword);
        passwordToggle = findViewById(R.id.passwordToggle);
        signInInstead = findViewById(R.id.signInInstead);
        next = findViewById(R.id.next);
        accountInformation = findViewById(R.id.accountInformation);
        deliveryInformation = findViewById(R.id.deliveryInformation);
        information = findViewById(R.id.information);
        phoneNumber = findViewById(R.id.phoneNumber);
        back = findViewById(R.id.back);
        address = findViewById(R.id.address);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        register = findViewById(R.id.register);
        phoneNumberLayout = findViewById(R.id.phoneNumberLayout);
        note = findViewById(R.id.note);

        firstname.addTextChangedListener(textWatcher);
        lastname.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        confirmPassword.addTextChangedListener(textWatcher);
        phoneNumber.addTextChangedListener(textWatcher);
        address.addTextChangedListener(textWatcher);

        passwordToggle.setOnClickListener(v -> passwordToggle());

        signInInstead.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });

        next.setOnClickListener(v -> {

            String uname = Objects.requireNonNull(username.getText()).toString().toLowerCase().trim();
            String pword = Objects.requireNonNull(password.getText()).toString().trim();
            String cpword = Objects.requireNonNull(confirmPassword.getText()).toString().trim();

            checkUsername(uname, pword, cpword);
        });

        back.setOnClickListener(v -> {
            information.setText("Account Information");
            accountInformation.setVisibility(View.VISIBLE);
            deliveryInformation.setVisibility(View.GONE);
            password.setText(null);
            confirmPassword.setText(null);
        });


        phoneNumber.setText("+63 ");

        Selection.setSelection(phoneNumber.getText(), phoneNumber.getText().length());

        phoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

                if (s.length() == 5) {
                    String[] st = s.toString().split(" ");
                    if (st[1].equals("0"))
                        phoneNumber.setText("");
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+63 ")) {
                    phoneNumber.setText("+63 ");
                    Selection.setSelection(phoneNumber.getText(), phoneNumber.getText().length());
                }
            }

        });


        register.setOnClickListener(v -> {

            String[] no = String.valueOf(phoneNumber.getText()).split(" ");

            if (!String.valueOf(no[1].charAt(0)).equals("9") || !String.valueOf(no[1]).matches("[0-9]+") || phoneNumber.getText().length() != 14) {
                phoneNumberLayout.setError("Invalid phone number. Try again.");
            }
            else {
                phoneNumberLayout.setError(null);

                List<String> addresses = new ArrayList<>();

                if (!String.valueOf(address.getText()).equals(""))
                    addresses.add(String.valueOf(address.getText()).trim());
                if (!String.valueOf(address1.getText()).equals(""))
                    addresses.add(String.valueOf(address1.getText()).trim());
                if (!String.valueOf(address2.getText()).equals(""))
                    addresses.add(String.valueOf(address2.getText()).trim());


                createUserAccount(
                        String.valueOf(firstname.getText()).toLowerCase().trim(),
                        String.valueOf(lastname.getText()).toLowerCase().trim(),
                        String.valueOf(username.getText()).trim(),
                        String.valueOf(password.getText()).trim(),
                        String.valueOf(phoneNumber.getText()).replace(" ", ""),
                        addresses

                );

            }

        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAuXUoYwfNp7P41GYhP3OShn3MAFd0s_CY");
        }

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("PH")
                        .build(getApplicationContext());
                startActivityForResult(intent, 0);

            }
        });

        address1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("PH")
                        .build(getApplicationContext());
                startActivityForResult(intent, 1);

            }
        });

        address2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Place.Field> fields = Collections.singletonList(Place.Field.ADDRESS);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("PH")
                        .build(getApplicationContext());
                startActivityForResult(intent, 2);

            }
        });


    }


    public void checkUsername(String uname, String pword, String cpword) {
        databaseReference.child("customers").orderByChild("username").equalTo(uname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    usernameLayout.setError("That username is taken. Try another.");
                    username.requestFocus();
                    password.setText(null);
                    confirmPasswordLayout.setError(null);
                    confirmPassword.setText(null);
                }
                else {
                    if (!pword.equals(cpword)) {
                        usernameLayout.setError(null);
                        confirmPasswordLayout.setError("Those passwords didn't match. Try again.");
                        confirmPassword.requestFocus();
                        confirmPassword.setText(null);
                    }
                    else {
                        information.setText("Delivery Information");
                        accountInformation.setVisibility(View.GONE);
                        deliveryInformation.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void createUserAccount(String fname, String lname, String uname, String pword, String pnum, List<String> addresses) {

            String id = databaseReference.push().getKey();

            Map<String, Object> data = new HashMap<>();
            data.put("customer_id", String.valueOf(id));
            data.put("firstname", fname);
            data.put("lastname", lname);
            data.put("username", uname);
            data.put("password", pword);
            data.put("phone_number", pnum);
            data.put("addresses", addresses);
            data.put("wallet", String.valueOf(50));

            databaseReference.child("customers").child(id)
                    .setValue(data)
                    .addOnSuccessListener(aVoid -> {

                        session.setId(id);
                        session.setFirstname(fname);
                        session.setLastname(lname);
                        session.setUsername(uname);
                        session.setPassword(pword);
                        session.setPhoneNumber(pnum);

                        Toast.makeText(RegistrationActivity.this, "ACCOUNT CREATED", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                        finish();
                    });

    }

    public void passwordToggle() {
        if (flag) {
            passwordToggle.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_off));
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            flag = false;
        }
        else {
            passwordToggle.setImageDrawable(getResources().getDrawable(R.drawable.icon_password_on));
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            flag = true;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            next.setEnabled(!Objects.requireNonNull(firstname.getText()).toString().trim().isEmpty() && !Objects.requireNonNull(lastname.getText()).toString().trim().isEmpty() && !Objects.requireNonNull(username.getText()).toString().trim().isEmpty() && !Objects.requireNonNull(password.getText()).toString().trim().isEmpty() && !Objects.requireNonNull(confirmPassword.getText()).toString().trim().isEmpty());

            register.setEnabled(!Objects.requireNonNull(phoneNumber.getText()).toString().equals("+63 ") && !Objects.requireNonNull(address.getText()).toString().trim().isEmpty());

//            if (!Objects.requireNonNull(address.getText()).toString().trim().isEmpty()) {
//                note.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            address.setText(String.valueOf(place.getAddress()));
        }
        else if (requestCode == 1 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            address1.setText(String.valueOf(place.getAddress()));
        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            address2.setText(String.valueOf(place.getAddress()));
        }
    }

    @Override
    public void onBackPressed() {

        if (deliveryInformation.getVisibility() == View.VISIBLE) {
            information.setText("Account Information");
            accountInformation.setVisibility(View.VISIBLE);
            deliveryInformation.setVisibility(View.GONE);
            password.setText(null);
            confirmPassword.setText(null);
        }
        else
            super.onBackPressed();
    }

}
