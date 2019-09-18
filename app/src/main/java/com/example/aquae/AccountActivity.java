package com.example.aquae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard, manageAddresses;
    TextView toolbarTitle, changeProfile, confirmPasswordError, currentPasswordError, usernameError, numberError;
    TextInputLayout usernameLayout, numberLayout;
    TextInputEditText firstname, lastname, username, password, phoneNumber;
    MaterialButton editSave, cancelAction;
    ImageView profile;
    EditText currentPassword, newPassword, confirmPassword;
    Button positiveButton;
    Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phoneNumber = findViewById(R.id.phone_number);
        manageAddresses = findViewById(R.id.manage_addresses);
        editSave = findViewById(R.id.edit_save);
        changeProfile = findViewById(R.id.changeProfile);
        profile = findViewById(R.id.profile);
        usernameLayout = findViewById(R.id.username_layout);
        numberLayout = findViewById(R.id.number_layout);
        usernameError = findViewById(R.id.username_error);
        numberError = findViewById(R.id.number_error);
        cancelAction = findViewById(R.id.cancel_action);

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Account");

        FirebaseDatabase.getInstance().getReference().child("customers")
                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Picasso.get()
                                    .load(String.valueOf(snapshot.child("profile").getValue()))
                                    .fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.profile_image_placeholder)
                                    .into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        firstname.setText(capitalize(new Session(getApplicationContext()).getFirstname()));
        lastname.setText(capitalize(new Session(getApplicationContext()).getLastname()));
        username.setText(new Session(getApplicationContext()).getUsername());
        password.setText(new Session(getApplicationContext()).getPassword());
        phoneNumber.setText(new Session(getApplicationContext()).getPhoneNumber());

        manageAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, ManageAddressesActivity.class));
            }
        });

        final String prefix = "+63 " + new Session(getApplicationContext()).getPhoneNumber().substring(3);

        phoneNumber.setText(prefix);

        Selection.setSelection(phoneNumber.getText(), Objects.requireNonNull(phoneNumber.getText()).toString().length());

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

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getLayoutInflater().inflate(R.layout.layout_change_password, null);

                currentPassword = view.findViewById(R.id.current_password);
                newPassword = view.findViewById(R.id.new_password);
                confirmPassword = view.findViewById(R.id.confirm_password);
                currentPasswordError = view.findViewById(R.id.current_password_error);
                confirmPasswordError = view.findViewById(R.id.confirm_password_error);

                currentPassword.addTextChangedListener(passwordTextWatcher);
                newPassword.addTextChangedListener(passwordTextWatcher);
                confirmPassword.addTextChangedListener(passwordTextWatcher);

                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this, R.style.AlertDialogTheme);
                builder.setTitle("Change Password");
                builder.setView(view);
                builder.setPositiveButton("CHANGE", null);
                builder.setNegativeButton("CANCEL", null);
                builder.setCancelable(false);

                final AlertDialog alertDialog = builder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setEnabled(false);
                        positiveButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                if (!String.valueOf(currentPassword.getText()).trim().equals(new Session(getApplicationContext()).getPassword())) {
                                    currentPasswordError.setText("Incorrect current password.");
                                    newPassword.setText("");
                                    confirmPassword.setText("");
                                    currentPassword.setText("");
                                    currentPassword.requestFocus();
                                }
                                else {
                                    currentPasswordError.setText("");

                                    if (!String.valueOf(newPassword.getText()).trim().equals(String.valueOf(confirmPassword.getText()).trim())) {
                                        confirmPasswordError.setText("Those passwords didn't match. Try again.");
                                        confirmPassword.setText("");
                                    }
                                    else {
                                        confirmPasswordError.setText("");

                                        final Map<String, Object> newPass = new HashMap<>();
                                        newPass.put("password", String.valueOf(newPassword.getText()).trim());

                                        FirebaseDatabase.getInstance().getReference().child("customers")
                                                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        dataSnapshot.getRef().child(new Session(getApplicationContext()).getId()).updateChildren(newPass);

                                                        new Session(getApplicationContext()).setPassword(String.valueOf(newPassword.getText()).trim());

                                                        password.setText(new Session(getApplicationContext()).getPassword());

                                                        alertDialog.cancel();
                                                        closeKeyboard();

                                                        Toast.makeText(getApplicationContext(), "PASSWORD UPDATED", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                    }

                                }

                            }
                        });

                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });
                    }
                });

                alertDialog.show();

            }
        });

        cancelAction.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                editSave.setText("EDIT");
                editSave.setEnabled(true);
                editSave.setTextColor(getResources().getColor(R.color.colorWhite));

                firstname.setEnabled(false);
                lastname.setEnabled(false);
                phoneNumber.setEnabled(false);
                username.setEnabled(false);

                usernameError.setVisibility(View.GONE);
                numberError.setVisibility(View.GONE);

                profile.setForeground(null);
                changeProfile.setVisibility(View.GONE);

                FirebaseDatabase.getInstance().getReference().child("customers")
                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Picasso.get()
                                            .load(String.valueOf(snapshot.child("profile").getValue()))
                                            .fit()
                                            .centerCrop()
                                            .placeholder(R.drawable.profile_image_placeholder)
                                            .into(profile);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                cancelAction.setVisibility(View.GONE);

            }
        });



        editSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (editSave.getText().equals("EDIT")) {
                    editSave.setText("SAVE");
                    firstname.setEnabled(true);
                    lastname.setEnabled(true);
                    username.setEnabled(true);
                    phoneNumber.setEnabled(true);
                    profile.setForeground(getDrawable(R.drawable.transparent_card));
                    changeProfile.setVisibility(View.VISIBLE);
                    firstname.requestFocus(Objects.requireNonNull(firstname.getText()).toString().length());
                    editSave.setEnabled(false);
                    editSave.setTextColor(getColor(R.color.colorDisabled));
                    cancelAction.setVisibility(View.VISIBLE);
                }
                else {

                    closeKeyboard();

                    FirebaseDatabase.getInstance().getReference().child("customers")
                            .orderByChild("username").equalTo(String.valueOf(username.getText()).trim())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (String.valueOf(snapshot.child("username").getValue()).equals(new Session(getApplicationContext()).getUsername())) {

                                                usernameError.setVisibility(View.GONE);

                                                String[] no = String.valueOf(phoneNumber.getText()).split(" ");

                                                if (!String.valueOf(no[1].charAt(0)).equals("9") || !String.valueOf(no[1]).matches("[0-9]+")) {
                                                    numberError.setVisibility(View.VISIBLE);
                                                } else {

                                                    numberError.setVisibility(View.GONE);

                                                    DialogFragment dialogFragment = LoadingScreen.getInstance();
                                                    dialogFragment.show(getSupportFragmentManager(), "account_activity");

                                                    if (resultUri == null) {

                                                        editSave.setText("SAVING");
                                                        cancelAction.setVisibility(View.GONE);

                                                        final Map<String, Object> data = new HashMap<>();
                                                        data.put("firstname", String.valueOf(firstname.getText()).toLowerCase().trim());
                                                        data.put("lastname", String.valueOf(lastname.getText()).toLowerCase().trim());
                                                        data.put("phone_number", String.valueOf(phoneNumber.getText()).replace(" ", ""));
                                                        data.put("username", String.valueOf(username.getText()).trim());

                                                        FirebaseDatabase.getInstance().getReference().child("customers")
                                                                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        dataSnapshot.getRef()
                                                                                .child(new Session(getApplicationContext()).getId())
                                                                                .updateChildren(data)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        new Session(getApplicationContext()).setFirstname(String.valueOf(firstname.getText()).toLowerCase().trim());
                                                                                        new Session(getApplicationContext()).setLastname(String.valueOf(lastname.getText()).toLowerCase().trim());
                                                                                        new Session(getApplicationContext()).setUsername(String.valueOf(username.getText()).trim());
                                                                                        new Session(getApplicationContext()).setPhoneNumber(String.valueOf(phoneNumber.getText()).replace(" ", ""));

                                                                                        editSave.setText("EDIT");
                                                                                        firstname.setEnabled(false);
                                                                                        lastname.setEnabled(false);
                                                                                        username.setEnabled(false);
                                                                                        phoneNumber.setEnabled(false);
                                                                                        profile.setForeground(null);
                                                                                        changeProfile.setVisibility(View.GONE);

                                                                                        Toast.makeText(getApplicationContext(), "ACCOUNT UPDATED", Toast.LENGTH_SHORT).show();
                                                                                        dialogFragment.dismiss();
                                                                                    }
                                                                                });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                    } else {

                                                        editSave.setText("SAVING");
                                                        cancelAction.setVisibility(View.GONE);

                                                        Uri file = Uri.fromFile(new File(String.valueOf(resultUri)));
                                                        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("customers/" + file.getLastPathSegment());
                                                        ref.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                            @Override
                                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                                if (!task.isSuccessful()) {
                                                                    throw task.getException();
                                                                }
                                                                return ref.getDownloadUrl();
                                                            }
                                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull final Task<Uri> task) {
                                                                if (task.isSuccessful()) {

                                                                    final Map<String, Object> data = new HashMap<>();
                                                                    data.put("firstname", String.valueOf(firstname.getText()).toLowerCase().trim());
                                                                    data.put("lastname", String.valueOf(lastname.getText()).toLowerCase().trim());
                                                                    data.put("phone_number", String.valueOf(phoneNumber.getText()).replace(" ", ""));
                                                                    data.put("username", String.valueOf(username.getText()).trim());
                                                                    data.put("profile", String.valueOf(task.getResult()));

                                                                    FirebaseDatabase.getInstance().getReference().child("customers")
                                                                            .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    dataSnapshot.getRef()
                                                                                            .child(new Session(getApplicationContext()).getId())
                                                                                            .updateChildren(data)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    new Session(getApplicationContext()).setFirstname(String.valueOf(firstname.getText()).toLowerCase().trim());
                                                                                                    new Session(getApplicationContext()).setLastname(String.valueOf(lastname.getText()).toLowerCase().trim());
                                                                                                    new Session(getApplicationContext()).setUsername(String.valueOf(username.getText()).trim());
                                                                                                    new Session(getApplicationContext()).setPhoneNumber(String.valueOf(phoneNumber.getText()).replace(" ", ""));

                                                                                                    editSave.setText("EDIT");
                                                                                                    firstname.setEnabled(false);
                                                                                                    lastname.setEnabled(false);
                                                                                                    username.setEnabled(false);
                                                                                                    phoneNumber.setEnabled(false);

                                                                                                    profile.setForeground(null);
                                                                                                    changeProfile.setVisibility(View.GONE);

                                                                                                    Toast.makeText(getApplicationContext(), "ACCOUNT UPDATED", Toast.LENGTH_SHORT).show();
                                                                                                    dialogFragment.dismiss();
                                                                                                }
                                                                                            });

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                            else {
                                                usernameError.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                    else {

                                        String[] no = String.valueOf(phoneNumber.getText()).split(" ");

                                        if (!String.valueOf(no[1].charAt(0)).equals("9") || !String.valueOf(no[1]).matches("[0-9]+")) {
                                            numberError.setVisibility(View.VISIBLE);
                                        }
                                        else {

                                            numberError.setVisibility(View.GONE);

                                            DialogFragment dialogFragment = LoadingScreen.getInstance();
                                            dialogFragment.show(getSupportFragmentManager(), "account_activity");

                                            if (resultUri == null) {

                                                editSave.setText("SAVING");
                                                cancelAction.setVisibility(View.GONE);

                                                final Map<String, Object> data = new HashMap<>();
                                                data.put("firstname", String.valueOf(firstname.getText()).toLowerCase().trim());
                                                data.put("lastname", String.valueOf(lastname.getText()).toLowerCase().trim());
                                                data.put("phone_number", String.valueOf(phoneNumber.getText()).replace(" ", ""));
                                                data.put("username", String.valueOf(username.getText()).trim());

                                                FirebaseDatabase.getInstance().getReference().child("customers")
                                                        .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                dataSnapshot.getRef()
                                                                        .child(new Session(getApplicationContext()).getId())
                                                                        .updateChildren(data)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                new Session(getApplicationContext()).setFirstname(String.valueOf(firstname.getText()).toLowerCase().trim());
                                                                                new Session(getApplicationContext()).setLastname(String.valueOf(lastname.getText()).toLowerCase().trim());
                                                                                new Session(getApplicationContext()).setUsername(String.valueOf(username.getText()).trim());
                                                                                new Session(getApplicationContext()).setPhoneNumber(String.valueOf(phoneNumber.getText()).replace(" ", ""));

                                                                                editSave.setText("EDIT");
                                                                                firstname.setEnabled(false);
                                                                                lastname.setEnabled(false);
                                                                                username.setEnabled(false);
                                                                                phoneNumber.setEnabled(false);
                                                                                profile.setForeground(null);
                                                                                changeProfile.setVisibility(View.GONE);

                                                                                Toast.makeText(getApplicationContext(), "ACCOUNT UPDATED", Toast.LENGTH_SHORT).show();
                                                                                dialogFragment.dismiss();
                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                            } else {

                                                editSave.setText("SAVING");
                                                cancelAction.setVisibility(View.GONE);

                                                Uri file = Uri.fromFile(new File(String.valueOf(resultUri)));
                                                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("customers/" + file.getLastPathSegment());
                                                ref.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        return ref.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull final Task<Uri> task) {
                                                        if (task.isSuccessful()) {

                                                            final Map<String, Object> data = new HashMap<>();
                                                            data.put("firstname", String.valueOf(firstname.getText()).toLowerCase().trim());
                                                            data.put("lastname", String.valueOf(lastname.getText()).toLowerCase().trim());
                                                            data.put("phone_number", String.valueOf(phoneNumber.getText()).replace(" ", ""));
                                                            data.put("username", String.valueOf(username.getText()).trim());
                                                            data.put("profile", String.valueOf(task.getResult()));

                                                            FirebaseDatabase.getInstance().getReference().child("customers")
                                                                    .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            dataSnapshot.getRef()
                                                                                    .child(new Session(getApplicationContext()).getId())
                                                                                    .updateChildren(data)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            new Session(getApplicationContext()).setFirstname(String.valueOf(firstname.getText()).toLowerCase().trim());
                                                                                            new Session(getApplicationContext()).setLastname(String.valueOf(lastname.getText()).toLowerCase().trim());
                                                                                            new Session(getApplicationContext()).setUsername(String.valueOf(username.getText()).trim());
                                                                                            new Session(getApplicationContext()).setPhoneNumber(String.valueOf(phoneNumber.getText()).replace(" ", ""));

                                                                                            editSave.setText("EDIT");
                                                                                            firstname.setEnabled(false);
                                                                                            lastname.setEnabled(false);
                                                                                            username.setEnabled(false);
                                                                                            phoneNumber.setEnabled(false);

                                                                                            profile.setForeground(null);
                                                                                            changeProfile.setVisibility(View.GONE);

                                                                                            Toast.makeText(getApplicationContext(), "ACCOUNT UPDATED", Toast.LENGTH_SHORT).show();
                                                                                            dialogFragment.dismiss();
                                                                                        }
                                                                                    });

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });

                                            }


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }
        });

        firstname.addTextChangedListener(accountTextWatcher);
        lastname.addTextChangedListener(accountTextWatcher);
        phoneNumber.addTextChangedListener(accountTextWatcher);
        username.addTextChangedListener(accountTextWatcher);

    }

    private TextWatcher accountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (String.valueOf(firstname.getText()).toLowerCase().trim().equals(new Session(getApplicationContext()).getFirstname())
                    && String.valueOf(lastname.getText()).toLowerCase().trim().equals(new Session(getApplicationContext()).getLastname())
                    && String.valueOf(phoneNumber.getText()).equals(new Session(getApplicationContext()).getPhoneNumber())
                    && String.valueOf(username.getText()).trim().equals(new Session(getApplicationContext()).getUsername())) {
                editSave.setEnabled(false);
                editSave.setTextColor(getResources().getColor(R.color.colorDisabled));
            }
            else {

                if (String.valueOf(firstname.getText()).trim().equals("")
                        || String.valueOf(lastname.getText()).trim().equals("")
                        || String.valueOf(phoneNumber.getText()).equals("+63 ")
                        || String.valueOf(phoneNumber.getText()).length() != 14
                        || String.valueOf(username.getText()).trim().equals("")) {

                    editSave.setEnabled(false);
                    editSave.setTextColor(getResources().getColor(R.color.colorDisabled));
                }
                else {
                    editSave.setEnabled(true);
                    editSave.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (!String.valueOf(currentPassword.getText()).trim().equals("") && !String.valueOf(newPassword.getText()).trim().equals("") && !String.valueOf(confirmPassword.getText()).trim().equals("")) {
                positiveButton.setEnabled(true);
            }
            else {
                positiveButton.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void imagePicker() {
        CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 1);
            } catch (Exception e) {}
        } else {
            imagePicker();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profile.setImageURI(resultUri);
                editSave.setEnabled(true);
                editSave.setTextColor(getResources().getColor(R.color.colorWhite));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(), ""+result.getError(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imagePicker();
        } else {
            requestPermission();
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
}
