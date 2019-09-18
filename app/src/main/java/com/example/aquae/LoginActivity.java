package com.example.aquae;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ConstraintSet constraintSet;
    ConstraintLayout rootView, contentView;
    ImageView logo;
    TextInputLayout usernameLayout, passwordLayout;
    TextInputEditText username, password;
    MaterialButton login, register, forgetPassword;
    DatabaseReference databaseReference;
    Session session;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorLight));
        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        session = new Session(getApplicationContext());

        rootView = findViewById(R.id.rootView);
        contentView = findViewById(R.id.contentView);
        logo = findViewById(R.id.logo);
        usernameLayout = findViewById(R.id.usernameLayout);
        username = findViewById(R.id.username);
        passwordLayout = findViewById(R.id.passwordLayout);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgotPassword);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            //keyboardHandler();
        });

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        login.setOnClickListener(v -> {

            String uname = Objects.requireNonNull(username.getText()).toString().toLowerCase().trim();
            final String pword = Objects.requireNonNull(password.getText()).toString().trim();

            databaseReference.child("customers").orderByChild("username").equalTo(uname)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {

                        usernameLayout.setErrorEnabled(false);

                        for(DataSnapshot data : dataSnapshot.getChildren()) {

                            if(!String.valueOf(data.child("password").getValue()).equals(pword)) {
                                passwordLayout.setError("Wrong password. Try again or click Forgot password to reset it.");
                                password.setText(null);
                            }
                            else {
                                session.setId(String.valueOf(data.getKey()));
                                session.setFirstname(String.valueOf(data.child("firstname").getValue()));
                                session.setLastname(String.valueOf(data.child("lastname").getValue()));
                                session.setUsername(String.valueOf(data.child("username").getValue()));
                                session.setPassword(String.valueOf(data.child("password").getValue()));
                                session.setPhoneNumber(String.valueOf(data.child("phone_number").getValue()));

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }
                        }
                    }
                    else {
                        usernameLayout.setError("Couldn't find your Aquae Account");
                        username.requestFocus();
                        passwordLayout.setErrorEnabled(false);
                        password.setText(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        register.setOnClickListener(v -> {

            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        });
    }

    public void keyboardHandler() {

        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rootView.getRootView().getHeight();
        int keypadHeight = screenHeight - rect.bottom;

        if (keypadHeight > screenHeight * 0.15) {
            logo.setVisibility(View.GONE);

            constraintSet = new ConstraintSet();
            constraintSet.clone(contentView);
            constraintSet.connect(R.id.usernameLayout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 96);
            constraintSet.connect(R.id.login, ConstraintSet.BOTTOM, R.id.forgotPassword, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.forgotPassword, ConstraintSet.TOP, R.id.login, ConstraintSet.BOTTOM, 0);
            constraintSet.connect(R.id.forgotPassword, ConstraintSet.BOTTOM, R.id.register, ConstraintSet.TOP, 8);
            constraintSet.connect(R.id.register, ConstraintSet.TOP, R.id.forgotPassword, ConstraintSet.BOTTOM, 8);
            constraintSet.connect(R.id.register, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 96);
            constraintSet.applyTo(contentView);
        }
        else {
            logo.setVisibility(View.VISIBLE);

            constraintSet = new ConstraintSet();
            constraintSet.clone(contentView);
            constraintSet.connect(R.id.usernameLayout, ConstraintSet.TOP, R.id.logo, ConstraintSet.BOTTOM, 96);
            constraintSet.connect(R.id.login, ConstraintSet.BOTTOM, R.id.register, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.register, ConstraintSet.TOP, R.id.login, ConstraintSet.BOTTOM, 8);
            constraintSet.connect(R.id.register, ConstraintSet.BOTTOM, R.id.forgotPassword, ConstraintSet.TOP, 8);
            constraintSet.connect(R.id.forgotPassword, ConstraintSet.TOP, R.id.register, ConstraintSet.BOTTOM, 8);
            constraintSet.connect(R.id.forgotPassword, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 96);
            constraintSet.applyTo(contentView);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            login.setEnabled(!Objects.requireNonNull(username.getText()).toString().trim().isEmpty() && !Objects.requireNonNull(password.getText()).toString().trim().isEmpty());

            if(!Objects.requireNonNull(password.getText()).toString().trim().isEmpty())
                passwordLayout.setPasswordVisibilityToggleEnabled(true);
            else {
                passwordLayout.setPasswordVisibilityToggleEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}


