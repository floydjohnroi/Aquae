package com.example.aquae;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RequestCashInActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialCardView toolbarCard;
    TextView toolbarTitle;
    TextInputEditText amount, transactionCode;
    MaterialButton next, back, sendRequest, upload;
    TextView step1, step2, totalAmount, step3, verify, payAmount, receiptError;
    ScrollView firstLayout, secondLayout;
    ImageView receipt;
    RadioGroup paymentMethod;
    RadioButton method;
    TextView transacFee;

    Uri resultUri;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;

    DialogFragment dialogFragment = LoadingScreen.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_cash_in);

        toolbarCard = findViewById(R.id.toolbarCard);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.title);
        amount = findViewById(R.id.amount);
        next = findViewById(R.id.next);
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        totalAmount = findViewById(R.id.totalAmount);
        step3 = findViewById(R.id.step3);
        verify = findViewById(R.id.verify);
        back = findViewById(R.id.back);
        firstLayout = findViewById(R.id.firstLayout);
        secondLayout = findViewById(R.id.secondLayout);
        payAmount = findViewById(R.id.payAmount);
        sendRequest = findViewById(R.id.sendRequest);
        transactionCode = findViewById(R.id.transactionCode);
        upload = findViewById(R.id.upload);
        receipt = findViewById(R.id.receipt);
        receiptError = findViewById(R.id.receiptError);
        paymentMethod = findViewById(R.id.paymentMethod);

        transacFee = findViewById(R.id.transac_fee);

        transacFee.setText(Html.fromHtml("₱<b>0</b>"));

        setSupportActionBar(toolbar);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.icon_back_dark);
        toolbarCard.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        toolbarTitle.setText("Cash In");

        step1.setText(Html.fromHtml("Proceed to any <b>Palawan Pawnshop - Palawan Express Pera Padala</b> branch."));
        step2.setText(Html.fromHtml("Fill out a <b>SEND MONEY Form</b> with the following details:"));
        step3.setText(Html.fromHtml("Complete your payment and upload photo of <b>SEND MONEY Receipt with the TRANSACTION CODE</b>. Please make sure that your transaction code is correct."));

        configPayPal();

        method = (RadioButton) paymentMethod.getChildAt(0);
        method.setChecked(true);

        method = findViewById(paymentMethod.getCheckedRadioButtonId());

        paymentMethod.setOnCheckedChangeListener((group, checkedId) -> {

            method = findViewById(checkedId);

        });

        amount.setText("PHP ");

        Selection.setSelection(amount.getText(), amount.getText().length());

        amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

                if (s.length() > 4) {
                    next.setEnabled(true);

                    int fee = Integer.parseInt(s.toString().replace("PHP ", ""));

                    if (fee > 99) {
                        transacFee.setText(Html.fromHtml("₱<b>"+ fee / 100 * 1 +"</b>"));
                    }
                }
                else {
                    next.setEnabled(false);

                    transacFee.setText(Html.fromHtml("₱<b>0</b>"));
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("PHP ")) {
                    amount.setText("PHP ");
                    Selection.setSelection(amount.getText(), amount.getText().length());
                }
            }

        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        if (getIntent().getStringExtra("amount") != null) {
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.VISIBLE);

            payAmount.setText(getIntent().getStringExtra("amount"));
            String t = getIntent().getStringExtra("amount") + ".00 PHP";
            totalAmount.setText(t);
            String vg = "Once we've verified your payment, PHP "+getIntent().getStringExtra("amount")+".00 will be credited to your Aquae Wallet";
            verify.setText(vg);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amt = String.valueOf(amount.getText()).replace("PHP ", "");

                if (!amt.matches("[0-9]+")) {
                    amount.setError("Invalid amount");
                }
                else if (Integer.parseInt(amt) < 100) {
                    amount.setError("Minimum amount allowed is ₱100.00");
                }
                else if (Integer.parseInt(amt) > 5000) {
                    amount.setError("Maximum amount allowed is ₱5000.00");
                }
                else {

                    if (method.getText().toString().equals("1")) {
                        getPayment(amt);
                    }
                    else {
                        firstLayout.setVisibility(View.GONE);
                        secondLayout.setVisibility(View.VISIBLE);

                        payAmount.setText(amt);
                        String t = amt + ".00 PHP";
                        totalAmount.setText(t);
                        String vg = "Once we've verified your payment, PHP "+amt+".00 will be credited to your Aquae Wallet";
                        verify.setText(vg);
                    }


                }
            }
        });

        transactionCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 16) {
                    sendRequest.setEnabled(true);
                }
                else {
                    sendRequest.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getStringExtra("amount") != null) {
                    finish();
                }
                else {

                    firstLayout.setVisibility(View.VISIBLE);
                    secondLayout.setVisibility(View.GONE);

                    receipt.setVisibility(View.GONE);
                    receipt.setImageURI(null);
                    receiptError.setVisibility(View.GONE);
                    transactionCode.setText(null);

                }

            }
        });

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (receipt.getVisibility() != View.VISIBLE) {
                    receiptError.setVisibility(View.VISIBLE);
                }
                else {
                    receiptError.setVisibility(View.GONE);

                    sendRequest.setText("SENDING");
                    sendRequest.setEnabled(false);

                    dialogFragment.show(getSupportFragmentManager(), "request_cash_in_activity");
                    dialogFragment.setCancelable(false);

                    Uri file = Uri.fromFile(new File(String.valueOf(resultUri)));
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("wallet_receipts/"+file.getLastPathSegment());
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

                                String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                                Map<String, String> data = new HashMap<>();
                                data.put("cash_in_id", Objects.requireNonNull(id));
                                data.put("customer_id", new Session(getApplicationContext()).getId());
                                data.put("amount", String.valueOf(payAmount.getText()));
                                data.put("transaction_code", String.valueOf(transactionCode.getText()));
                                data.put("receipt", String.valueOf(task.getResult()));
                                data.put("request_date", new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault()).format(new Date()));
                                data.put("verified_date", "date");
                                data.put("status", "request");

                                FirebaseDatabase.getInstance().getReference().child("cash_ins")
                                        .child(id)
                                        .setValue(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialogFragment.dismiss();
                                                Toast.makeText(getApplicationContext(), "REQUEST SENT",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });

                            }
                        }
                    });

                }

            }
        });

    }

    private void configPayPal() {

        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    }

    private void getPayment(String amt) {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amt)), "PHP", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent pay = new Intent(this, PaymentActivity.class);
        pay.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        pay.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(pay, REQUEST_CODE_PAYMENT);

    }

    public void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start( this);
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
                receipt.setVisibility(View.VISIBLE);
                receipt.setImageURI(resultUri);
                receiptError.setVisibility(View.GONE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, ""+result.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {

                    try {

                        JSONObject jsonObject = new JSONObject(confirm.toJSONObject().toString(4));

                        FirebaseDatabase.getInstance().getReference().child("customers")
                                .orderByChild("customer_id").equalTo(new Session(getApplicationContext()).getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        int newBal;

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            newBal = Integer.parseInt(String.valueOf(snapshot.child("wallet").getValue())) + Integer.parseInt(String.valueOf(amount.getText()).replace("PHP ", ""));

                                            int finalNewBal = newBal;

                                            snapshot.getRef().child("wallet").setValue(String.valueOf(newBal))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                                                            Map<String, String> data = new HashMap<>();
                                                            data.put("cash_in_id", Objects.requireNonNull(id));
                                                            data.put("customer_id", new Session(getApplicationContext()).getId());
                                                            data.put("amount", String.valueOf(amount.getText()).replace("PHP ", ""));
                                                            data.put("payment_date", new SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.getDefault()).format(new Date()));
                                                            try {
                                                                data.put("transaction_code", String.valueOf(jsonObject.getJSONObject("response").getString("id")));
                                                                data.put("status", String.valueOf(jsonObject.getJSONObject("response").getString("state")));
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                            FirebaseDatabase.getInstance().getReference().child("cash_ins")
                                                                    .child(id)
                                                                    .setValue(data);

                                                            Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(getApplicationContext(), "New Balance: "+ finalNewBal +".00", Toast.LENGTH_LONG).show();
                                                            finish();

                                                        }
                                                    });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Payment Canceled", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getApplicationContext(), "Invalid Payment", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}
