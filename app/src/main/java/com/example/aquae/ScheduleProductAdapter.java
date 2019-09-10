package com.example.aquae;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.util.List;

public class ScheduleProductAdapter extends RecyclerView.Adapter<ScheduleProductAdapter.ScheduleProductViewHolder> {

    private Context context;
    private List<ScheduleProductModel> scheduleProductModelList;

    public ScheduleProductAdapter(Context context, List<ScheduleProductModel> scheduleProductModelList) {
        this.context = context;
        this.scheduleProductModelList = scheduleProductModelList;
    }

    @NonNull
    @Override
    public ScheduleProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_schedule_product, parent, false);

        return new ScheduleProductAdapter.ScheduleProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleProductViewHolder holder, int position) {
        ScheduleProductModel scheduleProductModel = scheduleProductModelList.get(position);

        if ("0".equals(scheduleProductModel.getRefillPrice())) {
            holder.refillLayout.setVisibility(View.GONE);
        }
        if ("0".equals(scheduleProductModel.getPurchasePrice())) {
            holder.purchaseLayout.setVisibility(View.GONE);
        }

        holder.itemName.setText(capitalize(scheduleProductModel.getProduct()));
        holder.refillPrice.setText("₱"+scheduleProductModel.getRefillPrice());
        holder.purchasePrice.setText("₱"+scheduleProductModel.getPurchasePrice());
        holder.refillQty.setText(scheduleProductModel.getRefillQty());
        holder.purchaseQty.setText(scheduleProductModel.getPurchaseQty());
        holder.waterType.setText(scheduleProductModel.getWaterType());

    }

    @Override
    public int getItemCount() {
        return scheduleProductModelList.size();
    }

    class ScheduleProductViewHolder extends RecyclerView.ViewHolder {

        LinearLayout refillLayout, purchaseLayout;
        TextView itemName, waterType, refillPrice, purchasePrice, refillQty, purchaseQty;

        public ScheduleProductViewHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.item_name);
            waterType = itemView.findViewById(R.id.water_type);
            refillLayout = itemView.findViewById(R.id.refillLayout);
            purchaseLayout = itemView.findViewById(R.id.purchaseLayout);
            refillPrice = itemView.findViewById(R.id.refillPrice);
            purchasePrice = itemView.findViewById(R.id.purchasePrice);
            refillQty = itemView.findViewById(R.id.refillQty);
            purchaseQty = itemView.findViewById(R.id.purchaseQty);

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
