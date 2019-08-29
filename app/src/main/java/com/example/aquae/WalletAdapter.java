package com.example.aquae;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private Context context;
    private List<WalletModel> walletModelList;
    OutputStream outputStream;

    public WalletAdapter(Context context, List<WalletModel> walletModelList)  {
        this.context = context;
        this.walletModelList = walletModelList;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recent_activities_layout, parent, false);

        return new WalletAdapter.WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        WalletModel walletModel = walletModelList.get(position);

        if (walletModel.getStatus().equals("approved")) {
            holder.mode.setText("CASH IN by PayPal");
            holder.reqDateLayout.setVisibility(View.GONE);
            holder.verifyLayout.setVisibility(View.GONE);
            holder.viewReceipt.setVisibility(View.GONE);
            holder.paymentDate.setText(walletModel.getPaymentDate());
            holder.trxCode.setText(walletModel.getTransactionCode());
        }
        else {
            holder.mode.setText("CASH IN by Palawan Pawnshop - Palawan Express Pera Padala");
            holder.payDayLayout.setVisibility(View.GONE);
            holder.trxCodeLayout.setVisibility(View.GONE);
            holder.reqDate.setText(walletModel.getRequestDate());
            holder.verified_date.setText(walletModel.getVerifiedDate());

            if (walletModel.getStatus().equals("request")) {
                holder.verifyLayout.setVisibility(View.GONE);
            }
        }

        holder.amount.setText(walletModel.amount);
        holder.cashInId.setText(walletModel.getId());
        holder.status.setText(walletModel.getStatus());

        holder.viewReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.receipt_dialog_layout, null);

                TextView textView = view.findViewById(R.id.transactionCode);
                ImageView imageView = view.findViewById(R.id.imageView);

                textView.setText(walletModel.getTransactionCode());

                Picasso.get()
                        .load(walletModel.getReceiptImage())
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.refillssss)
                        .into(imageView);

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setView(view);

                builder.setNegativeButton("CLOSE", (dialog, which) -> dialog.cancel());

                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();

                        File filePath = Environment.getExternalStorageDirectory();
                        File dir = new File(filePath.getAbsolutePath()+"/AQUAE/");
                        dir.mkdir();

                        File file = new File(dir, System.currentTimeMillis()+".jpg");
                        try {
                            outputStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                        Toast.makeText(context, "IMAGE SAVED", Toast.LENGTH_SHORT).show();

                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return walletModelList.size();
    }

    class WalletViewHolder extends RecyclerView.ViewHolder {

        TextView amount, cashInId, reqDate, verified_date, viewReceipt, status, mode, paymentDate, trxCode;
        LinearLayout reqDateLayout, verifyLayout, payDayLayout, trxCodeLayout;

        public WalletViewHolder(View view) {
            super(view);

            amount = view.findViewById(R.id.amount);
            cashInId = view.findViewById(R.id.cash_in_id);
            reqDate = view.findViewById(R.id.req_date);
            verified_date = view.findViewById(R.id.verified_date);
            status = view.findViewById(R.id.status);
            viewReceipt = view.findViewById(R.id.view_receipt);
            verifyLayout = view.findViewById(R.id.verifyLayout);
            mode = view.findViewById(R.id.mode);
            reqDateLayout = view.findViewById(R.id.reqDateLayout);
            payDayLayout = view.findViewById(R.id.payDayLayout);
            trxCodeLayout = view.findViewById(R.id.trxCodeLayout);
            paymentDate = view.findViewById(R.id.paymentDate);
            trxCode = view.findViewById(R.id.trxCode);

        }
    }

}
