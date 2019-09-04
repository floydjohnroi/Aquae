package com.example.aquae;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckOutProductAdapter extends RecyclerView.Adapter<CheckOutProductAdapter.CheckOutProductViewHolder> {

    private Context context;
    private List<CheckOutProductModel> checkOutProductModelList;

    CheckOutProductAdapter(Context context, List<CheckOutProductModel> checkOutProductModelList) {
        this.context = context;
        this.checkOutProductModelList = checkOutProductModelList;
    }

    @NonNull
    @Override
    public CheckOutProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.checkout_product_layout, parent, false);

        return new CheckOutProductAdapter.CheckOutProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutProductViewHolder holder, int position) {
        CheckOutProductModel checkOutProductModel = checkOutProductModelList.get(position);

        Picasso.get()
                .load(checkOutProductModel.getImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.refillssss)
                .into(holder.image);

        holder.product.setText(capitalize(checkOutProductModel.getProduct()));

        holder.waterType.setText(checkOutProductModel.getWaterType());

        if (Integer.parseInt(checkOutProductModel.getRefillPrice()) == 0) {
            holder.refillLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }

        if (Integer.parseInt(checkOutProductModel.getPurchasePrice()) == 0) {
            holder.purchaseLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }

        String rp = "₱<b>"+checkOutProductModel.getRefillPrice()+"</b>";
        holder.refillPrice.setText(Html.fromHtml(rp));

        String pp = "₱<b>"+checkOutProductModel.getPurchasePrice()+"</b>";
        holder.purchasePrice.setText(Html.fromHtml(pp));

        holder.qtyRefill.setText(checkOutProductModel.getRefillQuantity());
        holder.qtyPurchase.setText(checkOutProductModel.getPurchaseQuantity());
    }

    @Override
    public int getItemCount() {
        return checkOutProductModelList.size();
    }

    class CheckOutProductViewHolder extends RecyclerView.ViewHolder{

        TextView product, waterType, refillPrice, purchasePrice, qtyRefill, qtyPurchase;
        ImageView image;
        LinearLayout refillLayout, purchaseLayout;
        View divider;

        public CheckOutProductViewHolder(View view) {
            super(view);

            product = view.findViewById(R.id.product);
            waterType = view.findViewById(R.id.water_type);
            refillPrice = view.findViewById(R.id.refillPrice);
            purchasePrice = view.findViewById(R.id.purchasePrice);
            qtyRefill = view.findViewById(R.id.qtyRefill);
            qtyPurchase = view.findViewById(R.id.qtyPurchase);
            image = view.findViewById(R.id.productImage);
            refillLayout = view.findViewById(R.id.refillLayout);
            purchaseLayout = view.findViewById(R.id.purchaseLayout);
            divider = view.findViewById(R.id.divider);

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
