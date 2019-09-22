package com.example.aquae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartProductModel> cartProductModelList;
    String isForDelivery, ref;
    int t;

//    private OnDataChangeListener mOnDataChangeListener;
//
//    public interface OnDataChangeListener {
//        void onChanged(int totalAmount);
//        void isSelectedAll(boolean isChecked);
//    }

    CartAdapter(Context context, List<CartProductModel> cartProductModelList, String isForDelivery) {
        this.context = context;
        this.cartProductModelList = cartProductModelList;
        this.isForDelivery = isForDelivery;
//        mOnDataChangeListener = onDataChangeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_product_layout, parent, false);

        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        final CartProductModel cartProductModel = cartProductModelList.get(position);

        if ("isForDelivery".equals(isForDelivery)) {
            ref = "deliveries";
        }
        else {
            ref = "carts";
        }

//        t += Integer.parseInt(cartProductModel.getSubtotal());

        holder.product.setText(capitalize(cartProductModel.getProduct()));

        Picasso.get()
                .load(cartProductModel.getImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.refillssss)
                .into(holder.productImage);

        holder.waterType.setText(cartProductModel.getWaterType());

        if (Integer.parseInt(cartProductModel.getRefillPrice()) == 0) {
            holder.refillLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        else {
            if (Integer.parseInt(cartProductModel.getRefillQuantity()) == Integer.parseInt(String.valueOf(cartProductModel.getMinOrder()))) {
                holder.minusRefill.setEnabled(false);
            }
        }

        if (Integer.parseInt(cartProductModel.getPurchasePrice()) == 0) {
            holder.purchaseLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        else {
            if (Integer.parseInt(cartProductModel.getPurchaseQuantity()) == Integer.parseInt(String.valueOf(cartProductModel.getMinOrder()))) {
                holder.minusPurchase.setEnabled(false);
            }
        }

        String rp = "₱"+cartProductModel.getRefillPrice();
        holder.refillPrice.setText(Html.fromHtml(rp));

        String pp = "₱"+cartProductModel.getPurchasePrice();
        holder.purchasePrice.setText(Html.fromHtml(pp));

        holder.quantityRefill.setText(cartProductModel.getRefillQuantity());
        holder.quantityPurchase.setText(cartProductModel.getPurchaseQuantity());

        holder.minusRefill.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityRefill.getText());

            i--;

            holder.quantityRefill.setText(String.valueOf(i));

            if (i == Integer.parseInt(String.valueOf(cartProductModel.getMinOrder()))) {
                holder.minusRefill.setEnabled(false);
            }
            else {
                holder.addRefill.setEnabled(true);
            }

            int ti = Integer.parseInt(cartProductModel.getSubtotal()) - Integer.parseInt(String.valueOf(holder.refillPrice.getText()).replace("₱", ""));

            FirebaseDatabase.getInstance().getReference().child(ref)
                    .orderByChild("customer_id").equalTo(new Session(context).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                    for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                        for (DataSnapshot s : snap.getChildren()) {
                                            if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                s.getRef().child("refill").child("quantity").setValue(holder.quantityRefill.getText());
                                                s.getRef().child("subtotal").setValue(String.valueOf(ti));
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        });



        holder.addRefill.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityRefill.getText());

            i++;

            holder.quantityRefill.setText(String.valueOf(i));

            holder.minusRefill.setEnabled(true);

            if (i == Integer.parseInt(String.valueOf(cartProductModel.getMaxOrder()))) {
                holder.addRefill.setEnabled(false);
            }

            int ti = Integer.parseInt(cartProductModel.getSubtotal()) + Integer.parseInt(String.valueOf(holder.refillPrice.getText()).replace("₱", ""));

            FirebaseDatabase.getInstance().getReference().child(ref)
                    .orderByChild("customer_id").equalTo(new Session(context).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                    for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                        for (DataSnapshot s : snap.getChildren()) {
                                            if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                s.getRef().child("refill").child("quantity").setValue(holder.quantityRefill.getText());
                                                s.getRef().child("subtotal").setValue(String.valueOf(ti));
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        });


        holder.minusPurchase.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityPurchase.getText());

            i--;

            holder.quantityPurchase.setText(String.valueOf(i));

            if (i == Integer.parseInt(String.valueOf(cartProductModel.getMinOrder()))) {
                holder.minusPurchase.setEnabled(false);
            }
            else {
                holder.addPurchase.setEnabled(true);
            }

            int ti = Integer.parseInt(cartProductModel.getSubtotal()) - Integer.parseInt(String.valueOf(holder.purchasePrice.getText()).replace("₱", ""));

            FirebaseDatabase.getInstance().getReference().child(ref)
                    .orderByChild("customer_id").equalTo(new Session(context).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                    for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                        for (DataSnapshot s : snap.getChildren()) {
                                            if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                s.getRef().child("purchase").child("quantity").setValue(holder.quantityPurchase.getText());
                                                s.getRef().child("subtotal").setValue(String.valueOf(ti));
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        });



        holder.addPurchase.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityPurchase.getText());

            i++;

            holder.quantityPurchase.setText(String.valueOf(i));

            holder.minusPurchase.setEnabled(true);

            if (i == Integer.parseInt(String.valueOf(cartProductModel.getMaxOrder()))) {
                holder.addPurchase.setEnabled(false);
            }

            int ti = Integer.parseInt(cartProductModel.getSubtotal()) + Integer.parseInt(String.valueOf(holder.purchasePrice.getText()).replace("₱", ""));

            FirebaseDatabase.getInstance().getReference().child(ref)
                    .orderByChild("customer_id").equalTo(new Session(context).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                    for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                        for (DataSnapshot s : snap.getChildren()) {
                                            if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                s.getRef().child("purchase").child("quantity").setValue(holder.quantityPurchase.getText());
                                                s.getRef().child("subtotal").setValue(String.valueOf(ti));
                                            }
                                        }
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                builder.setTitle("Remove "+capitalize(cartProductModel.getProduct()));
                builder.setMessage("This item will be removed. This action cannot be undone.");
                builder.setPositiveButton("REMOVE", (dialog, which) -> {

                    FirebaseDatabase.getInstance().getReference().child(ref)
                            .orderByChild("customer_id").equalTo(new Session(context).getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        dataSnapshot.getRef().orderByChild("client_id").equalTo(cartProductModel.getClient_id())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                                                if (Objects.equals(snap.getKey(), cartProductModel.getProduct_id())) {
                                                                    snap.getRef().removeValue();
                                                                }
                                                            }
                                                        }

                                                        Toast.makeText(context, capitalize(cartProductModel.getProduct())+" has been removed", Toast.LENGTH_SHORT).show();
                                                        notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        notifyDataSetChanged();
                    }
                });
                builder.create().show();
            }
        });

        //holder.checkBoxes.setChecked(true);

        if (cartProductModel.getStatus().equals("check")) {
            holder.checkBoxes.setChecked(true);

        }
        else {
            holder.checkBoxes.setChecked(false);

        }

        holder.checkBoxes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

//                    t -= Integer.parseInt(cartProductModel.getSubtotal());
//                    mOnDataChangeListener.onChanged(t);

                    FirebaseDatabase.getInstance().getReference().child(ref)
                        .orderByChild("customer_id").equalTo(new Session(context).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                        for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                            for (DataSnapshot s : snap.getChildren()) {
                                                if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                    s.getRef().child("status").setValue("uncheck");
//                                                    t = Integer.parseInt(cartProductModel.getSubtotal());
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                }
                else {


//                    t += Integer.parseInt(cartProductModel.getSubtotal());
//                    mOnDataChangeListener.onChanged(t);

                    FirebaseDatabase.getInstance().getReference().child(ref)
                            .orderByChild("customer_id").equalTo(new Session(context).getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (Objects.equals(snapshot.child("client_id").getValue(), cartProductModel.getClient_id())) {
                                            for (DataSnapshot snap : snapshot.child("products").getChildren()) {
                                                for (DataSnapshot s : snap.getChildren()) {
                                                    if (Objects.equals(s.getKey(), cartProductModel.getProduct())) {
                                                        s.getRef().child("status").setValue("check");
//                                                        t = Integer.parseInt(cartProductModel.getSubtotal());
                                                    }
                                                }
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


    }


    @Override
    public int getItemCount() {
        return cartProductModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

//    public void selectAll(){
//        mOnDataChangeListener.isSelectedAll(true);
//    }
//
//    public void unselectAll(){
//        mOnDataChangeListener.isSelectedAll(false);
//    }

    class CartViewHolder extends RecyclerView.ViewHolder{

        TextView product, quantityRefill, quantityPurchase, waterType, refillPrice, purchasePrice;
        ImageView minusRefill, minusPurchase, addRefill, addPurchase, productImage, remove;
        SwipeLayout swipeLayout;
        LinearLayout refillLayout, purchaseLayout;
        View divider;
        CheckBox checkBoxes;

        public CartViewHolder(View view) {
            super(view);

            product = view.findViewById(R.id.product);
            quantityRefill = view.findViewById(R.id.quantityRefill);
            quantityPurchase = view.findViewById(R.id.quantityPurchase);
            minusRefill = view.findViewById(R.id.minusRefill);
            minusPurchase = view.findViewById(R.id.minusPurchase);
            addRefill = view.findViewById(R.id.addRefill);
            addPurchase = view.findViewById(R.id.addPurchase);
            swipeLayout = view.findViewById(R.id.swipe);
            waterType = view.findViewById(R.id.water_type);
            refillLayout = view.findViewById(R.id.refillLayout);
            purchaseLayout = view.findViewById(R.id.purchaseLayout);
            divider = view.findViewById(R.id.divider);
            refillPrice = view.findViewById(R.id.refillPrice);
            purchasePrice = view.findViewById(R.id.purchasePrice);
            productImage = view.findViewById(R.id.productImage);
            checkBoxes = view.findViewById(R.id.checkBox);
            remove = view.findViewById(R.id.remove);

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

}
