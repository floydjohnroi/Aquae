package com.example.aquae;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.TrackOrderViewHolder> {

    private Context context;
    private List<TrackOrderModel> trackOrderModelList;
    private String activity;

    public TrackOrderAdapter(Context context, List<TrackOrderModel> trackOrderModelList, String activity) {
        this.context = context;
        this.trackOrderModelList = trackOrderModelList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TrackOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if ("OrderHistory".equals(activity)) {
            view = inflater.inflate(R.layout.layout_order_history, parent, false);
        }
        else {
            view = inflater.inflate(R.layout.layout_track_order, parent, false);
        }

        return new TrackOrderAdapter.TrackOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackOrderViewHolder holder, int position) {
        TrackOrderModel trackOrderModel = trackOrderModelList.get(position);

//        FirebaseDatabase.getInstance().getReference().child("clients")
//                .orderByChild("client_id").equalTo(trackOrderModel.getClientId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            holder.clientName.setText(String.valueOf(snapshot.child("company").getValue()));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

        holder.clientName.setText(trackOrderModel.clientName);
        holder.orderId.setText(trackOrderModel.getOrderId());
        holder.orderTime.setText(trackOrderModel.getOrderTime());

        if ("confirmed".equals(trackOrderModel.getStatus())) {
            holder.status.setText("completed");
        }
        else {
            holder.status.setText(trackOrderModel.getStatus());
        }

        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrackActivity.class);
                intent.putExtra("order_id", trackOrderModel.getOrderId());
                intent.putExtra("delivery_address", trackOrderModel.getDeliveryAddress());
                intent.putExtra("client_id", trackOrderModel.getClientId());
                intent.putExtra("order_time", trackOrderModel.getOrderTime());
                intent.putExtra("activity", activity);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trackOrderModelList.size();
    }

    class TrackOrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderId, orderTime, clientName, status;
        MaterialCardView materialCardView;

        public TrackOrderViewHolder(View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            orderTime = itemView.findViewById(R.id.order_time);
            clientName = itemView.findViewById(R.id.client_name);
            status = itemView.findViewById(R.id.status);
            materialCardView = itemView.findViewById(R.id.materialCardView);

        }
    }
}
