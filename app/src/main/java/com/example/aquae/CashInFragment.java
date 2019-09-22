package com.example.aquae;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CashInFragment extends Fragment {

    TextInputEditText amount;
    MaterialButton next;
    RadioGroup paymentMethod;
    RadioButton method;
    TextView transacFee;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static PayPalConfiguration config;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cash_in, container, false);


        amount = view.findViewById(R.id.amount);
        next = view.findViewById(R.id.next);
        paymentMethod = view.findViewById(R.id.paymentMethod);
        transacFee = view.findViewById(R.id.transac_fee);

        transacFee.setText(Html.fromHtml("₱<b>0</b>"));

        method = (RadioButton) paymentMethod.getChildAt(0);
        method.setChecked(true);

        method = view.findViewById(paymentMethod.getCheckedRadioButtonId());

        paymentMethod.setOnCheckedChangeListener((group, checkedId) -> {

            method = view.findViewById(checkedId);

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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amt = String.valueOf(amount.getText()).replace("PHP ", "");
                String fee = String.valueOf(transacFee.getText()).replace("₱", "");

                int totalPayment = Integer.parseInt(amt) + Integer.parseInt(fee);

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
                        getPayment(String.valueOf(totalPayment));
                    }
                    else {
                        Intent intent = new Intent(getContext(), RequestCashInActivity.class);
                        intent.putExtra("amount", String.valueOf(totalPayment));
                        startActivity(intent);
                        amount.setText(null);
                    }

                }
            }
        });

        configPayPal();

        return view;
    }

    private void configPayPal() {

        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    }

    private void getPayment(String amt) {
        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        Objects.requireNonNull(getContext()).startService(intent);

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amt)), "PHP", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent pay = new Intent(getContext(), PaymentActivity.class);
        pay.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        pay.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(pay, REQUEST_CODE_PAYMENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {

                    try {

                        JSONObject jsonObject = new JSONObject(confirm.toJSONObject().toString(4));

                        FirebaseDatabase.getInstance().getReference().child("customers")
                                .orderByChild("customer_id").equalTo(new Session(getContext()).getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        int newBal;

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            newBal = Integer.parseInt(String.valueOf(snapshot.child("wallet").getValue()))
                                                    + Integer.parseInt(String.valueOf(amount.getText()).replace("PHP ", ""))
                                                    + Integer.parseInt(String.valueOf(transacFee.getText()).replace("₱", ""));

                                            int finalNewBal = newBal;

                                            snapshot.getRef().child("wallet").setValue(String.valueOf(newBal))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            String id = FirebaseDatabase.getInstance().getReference().push().getKey();

                                                            Map<String, String> data = new HashMap<>();
                                                            data.put("cash_in_id", Objects.requireNonNull(id));
                                                            data.put("customer_id", new Session(getContext()).getId());
                                                            data.put("amount", String.valueOf(finalNewBal));
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

                                                            amount.setText(null);
                                                            Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(getContext(), "New Balance: "+ finalNewBal +".00", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(), "Payment Canceled", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getContext(), "Invalid Payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        Objects.requireNonNull(getContext()).stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }
}
