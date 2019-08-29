package com.example.aquae;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private Map<String, String> datas;

    public ProductAdapter(Context context, List<ProductModel> productModelList, Map<String, String> datas)  {
        this.context = context;
        this.productModelList = productModelList;
        this.datas = datas;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_layout, parent, false);

        return new ProductViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final ProductModel productModel = productModelList.get(position);

        Picasso.get()
                .load(productModel.getProductImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.refillssss)
                .into(holder.productImage);
        holder.productName.setText(capitalize(productModel.getProductName()));
        holder.product.setOnClickListener(v -> {

            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("client_id", datas.get("client_id"));
            intent.putExtra("company", datas.get("company"));
            intent.putExtra("product", productModel.getProductName());
            intent.putExtra("productImage", productModel.getProductImage());
            intent.putExtra("refillPrice", productModel.getRefillPrice());
            intent.putExtra("purchasePrice", productModel.getPurchasePrice());
            intent.putExtra("water_type", datas.get("water_type"));
            intent.putExtra("min_order", datas.get("min_order"));
            intent.putExtra("max_order", datas.get("max_order"));
            context.startActivity(intent);

        });


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
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView product;
        ImageView productImage;
        TextView productName, badge;

        public ProductViewHolder(View view) {
            super(view);

            product = view.findViewById(R.id.product);
            productImage = view.findViewById(R.id.productImage);
            productName = view.findViewById(R.id.productName);
            badge = view.findViewById(R.id.badge);
        }
    }
}
