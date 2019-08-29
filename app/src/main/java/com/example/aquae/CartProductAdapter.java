package com.example.aquae;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartProductHolder> {

    private Context context;
    private List<CartProductModel> cartProductModelList;
    private Map<String, Object> datas;

    public CartProductAdapter(Context context, List<CartProductModel> cartProductModelList, Map<String, Object> datas) {
        this.context = context;
        this.cartProductModelList = cartProductModelList;
        this.datas = datas;
    }


    @NonNull
    @Override
    public CartProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_product_layout, parent, false);

        return new CartProductAdapter.CartProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductHolder holder, int position) {
        final CartProductModel cartProductModel = cartProductModelList.get(position);

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
            if (Integer.parseInt(cartProductModel.getRefillQuantity()) == Integer.parseInt(String.valueOf(datas.get("min_order")))) {
                holder.minusRefill.setEnabled(false);
            }
        }

        if (Integer.parseInt(cartProductModel.getPurchasePrice()) == 0) {
            holder.purchaseLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        else {
            if (Integer.parseInt(cartProductModel.getPurchaseQuantity()) == Integer.parseInt(String.valueOf(datas.get("min_order")))) {
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

            //sub = sub - Integer.parseInt(refillPrice.getText().toString());
            holder.quantityRefill.setText(String.valueOf(i));
            //String s12 = "\u20B1<b>"+sub+".00</b>";
            //subtotal.setText(Html.fromHtml(s12));
           // mOnDataChangeListener.onChanged(i);

            if (i == Integer.parseInt(String.valueOf(datas.get("min_order")))) {
                holder.minusRefill.setEnabled(false);
            }
            else {
                holder.addRefill.setEnabled(true);
            }

        });

        holder.addRefill.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityRefill.getText());

            i++;

            //subtotal = refillPrice * i;

            //total = total + refillPrice;

            // mOnDataChangeListener.onChanged(total);

            holder.quantityRefill.setText(String.valueOf(i));

            holder.minusRefill.setEnabled(true);

            if (i == Integer.parseInt(String.valueOf(datas.get("max_order")))) {
                holder.addRefill.setEnabled(false);
            }

        });


        holder.minusPurchase.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityPurchase.getText());

            i--;

            //subtotal = purchasePrice * i;

            //total = total - purchasePrice;

            //mOnDataChangeListener.onChanged(total);

            //sub = sub - Integer.parseInt(refillPrice.getText().toString());
            holder.quantityPurchase.setText(String.valueOf(i));
            //String s12 = "\u20B1<b>"+sub+".00</b>";
            //subtotal.setText(Html.fromHtml(s12));

            if (i == Integer.parseInt(String.valueOf(datas.get("min_order")))) {
                holder.minusPurchase.setEnabled(false);
            }
            else {
                holder.addPurchase.setEnabled(true);
            }

        });



        holder.addPurchase.setOnClickListener(v -> {

            int i = Integer.parseInt((String) holder.quantityPurchase.getText());

            i++;

            //subtotal = purchasePrice * i;

            //total = total + purchasePrice;

            holder.quantityPurchase.setText(String.valueOf(i));

            holder.minusPurchase.setEnabled(true);

            if (i == Integer.parseInt(String.valueOf(datas.get("max_order")))) {
                holder.addPurchase.setEnabled(false);
            }
            //mOnDataChangeListener.onChanged(total);

        });

        //holder.checkBoxes.setChecked(true);

        holder.checkBoxes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                //total = total - Integer.parseInt(cartProductModel.getSubtotal());

            }

            //mOnDataChangeListener.onChanged(total);

        });

        holder.remove.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("carts")
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
                }));


    }

    @Override
    public int getItemCount() {
        return cartProductModelList.size();
    }

    class CartProductHolder extends RecyclerView.ViewHolder {

        TextView product, quantityRefill, quantityPurchase, waterType, refillPrice, purchasePrice;
        ImageView minusRefill, minusPurchase, addRefill, addPurchase, productImage, remove;
        SwipeLayout swipeLayout;
        LinearLayout refillLayout, purchaseLayout;
        View divider;
        CheckBox checkBoxes;

        public CartProductHolder(View view) {
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
