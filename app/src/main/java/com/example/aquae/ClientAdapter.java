package com.example.aquae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> implements Filterable {

    private Context context;
    private List<ClientModel> clientModelList;
    private List<ClientModel> clientModelListFull;
    String isForDelivery;

    String finalStr;
    String[] str;

    public ClientAdapter(Context context, List<ClientModel> clientModelList, String isForDelivery) {
        this.context = context;
        this.clientModelList = clientModelList;
        this.isForDelivery = isForDelivery;
        clientModelListFull = new ArrayList<>(clientModelList);
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.filter1_layout, parent, false);

        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        final ClientModel clientModel = clientModelList.get(position);

        holder.company.setText(clientModel.getCompany());
        holder.address.setText(clientModel.getAddress());

        finalStr = clientModel.getWater_type().substring(0, clientModel.getWater_type().length()-2);

        str = clientModel.getWater_type().split(", ");

        if (str.length > 1) {
            finalStr = finalStr.substring(0, finalStr.lastIndexOf(","))+" &"+finalStr.substring(finalStr.lastIndexOf(",")+1);
            holder.waterType.setText(finalStr);
        }
        else {
            holder.waterType.setText(finalStr);
        }


        holder.station.setOnClickListener(v -> {

            Intent intent = new Intent(context, ClientActivity.class);
            intent.putExtra("client_id", clientModel.getClient_id());
            intent.putExtra("company", clientModel.getCompany());
            intent.putExtra("address", clientModel.getAddress());
            intent.putExtra("email", clientModel.getEmail());
            intent.putExtra("contact", clientModel.getContact());
            intent.putExtra("storeImage", clientModel.getStoreImage());
            intent.putExtra("water_type", finalStr);
            intent.putExtra("no_of_filter", clientModel.getNo_of_filter());
            intent.putExtra("ship_fee", clientModel.getShip_fee());
            intent.putExtra("isForDelivery", isForDelivery);
            context.startActivity(intent);
        });

        Picasso.get()
                .load(clientModel.getStoreImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.station_image_placeholder)
                .into(holder.storeImage);

        if ("".equals(clientModel.getKmAway())) {
            holder.kmAwayLayout.setVisibility(View.GONE);
        }
        else {
            holder.kmAwayLayout.setVisibility(View.VISIBLE);
            holder.kmAway.setText(Html.fromHtml(clientModel.getKmAway()));
        }
    }


    @Override
    public int getItemCount() {
        return clientModelList.size();
    }

    @Override
    public Filter getFilter() {
        return clientFilter;
    }

    private Filter clientFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ClientModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(clientModelListFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ClientModel clientModel : clientModelListFull) {
                    if (clientModel.getCompany().toLowerCase().contains(filterPattern)) {
                        filteredList.add(clientModel);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clientModelList.clear();
            clientModelList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ClientViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView station;
        TextView company, address, waterType, kmAway;
        ImageView storeImage;
        LinearLayout kmAwayLayout;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);

            station = itemView.findViewById(R.id.station);
            company = itemView.findViewById(R.id.company);
            address = itemView.findViewById(R.id.address);
            storeImage = itemView.findViewById(R.id.storeImage);
            waterType = itemView.findViewById(R.id.water_type);
            kmAway = itemView.findViewById(R.id.km_away);
            kmAwayLayout = itemView.findViewById(R.id.km_away_layout);
        }
    }
}
