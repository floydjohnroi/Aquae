package com.example.aquae;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import android.widget.TextView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartModel> cartModelList;
    private List<CartProductModel> cartProductModelList;

    int total;

    private OnDataChangeListener mOnDataChangeListener;

    public interface OnDataChangeListener {
        void onChanged(double totalAmount);
        void isSelectedAll(boolean isChecked);
    }

    CartAdapter(Context context, List<CartModel> cartModelList, List<CartProductModel> cartProductModelList, CartAdapter.OnDataChangeListener onDataChangeListener) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.cartProductModelList = cartProductModelList;
        mOnDataChangeListener = onDataChangeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int position) {
        final CartModel cartModel = cartModelList.get(position);

        cartViewHolder.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        cartViewHolder.recyclerView.setLayoutManager(linearLayoutManager);

        List<CartProductModel> productList = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();

        Log.d("TEST", ""+cartProductModelList.size());

        for (int i = 0; i < cartProductModelList.size(); i++) {
            if (String.valueOf(cartProductModelList.get(i).getClient_id()).equals(cartModel.getClient_id())) {
                productList.add(cartProductModelList.get(i));

                data.put("min_order", cartModel.getMin_order());
                data.put("max_order", cartModel.getMax_order());
                data.put("ship_fee", cartModel.getShip_fee());

                total += Integer.parseInt(cartProductModelList.get(i).getSubtotal());

            }
        }

        mOnDataChangeListener.onChanged(total);



        cartViewHolder.recyclerView.setAdapter(new CartProductAdapter(context, productList, data));

        cartViewHolder.client.setText(cartModel.getClient());

        cartViewHolder.client.setOnClickListener(v -> {

//            FirebaseDatabase.getInstance().getReference().child("clients")
//                    .orderByChild("client_id").equalTo(cartModel.getClient_id())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                                Intent intent = new Intent(context, ClientActivity.class);
//                                intent.putExtra("client_id", String.valueOf(snapshot.child("client_id").getValue()));
//                                intent.putExtra("company", String.valueOf(snapshot.child("company").getValue()));
//                                intent.putExtra("address", String.valueOf(snapshot.child("address").getValue()));
//                                intent.putExtra("email", String.valueOf(snapshot.child("email").getValue()));
//                                intent.putExtra("contact", String.valueOf(snapshot.child("contact").getValue()));
//                                context.startActivity(intent);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

        });


        cartViewHolder.checkBox.setChecked(true);



    }


    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public void selectAll(){
        mOnDataChangeListener.isSelectedAll(true);
    }

    public void unselectAll(){ mOnDataChangeListener.isSelectedAll(false); }

    class CartViewHolder extends RecyclerView.ViewHolder{

        TextView client, quantityRefill, quantityPurchase;
        ImageView minusRefill, addRefill, minusPurchase, addPurchase;
        CheckBox checkBox;
        RecyclerView recyclerView;


        public CartViewHolder(View itemView) {
            super(itemView);

            client = itemView.findViewById(R.id.client);
            quantityRefill = itemView.findViewById(R.id.quantityRefill);
            quantityPurchase = itemView.findViewById(R.id.quantityPurchase);
            minusRefill = itemView.findViewById(R.id.minusRefill);
            addRefill = itemView.findViewById(R.id.addRefill);
            minusPurchase = itemView.findViewById(R.id.minusPurchase);
            addPurchase = itemView.findViewById(R.id.addPurchase);
            checkBox = itemView.findViewById(R.id.checkBox);
            recyclerView = itemView.findViewById(R.id.recyclerView);

        }
    }

}
