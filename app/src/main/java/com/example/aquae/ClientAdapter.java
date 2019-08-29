package com.example.aquae;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private Context context;
    private List<ClientModel> clientModelList;

    public ClientAdapter(Context context, List<ClientModel> clientModelList) {
        this.context = context;
        this.clientModelList = clientModelList;
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
        holder.station.setOnClickListener(v -> {

            Intent intent = new Intent(context, ClientActivity.class);
            intent.putExtra("client_id", clientModel.getClient_id());
            intent.putExtra("company", clientModel.getCompany());
            intent.putExtra("address", clientModel.getAddress());
            intent.putExtra("email", clientModel.getEmail());
            intent.putExtra("contact", clientModel.getContact());
            intent.putExtra("storeImage", clientModel.getStoreImage());
            intent.putExtra("water_type", clientModel.getWater_type());
            intent.putExtra("min_order", clientModel.getMin_order());
            intent.putExtra("max_order", clientModel.getMax_order());
            intent.putExtra("no_of_filter", clientModel.getNo_of_filter());
            intent.putExtra("ship_fee", clientModel.getShip_fee());

            context.startActivity(intent);
        });

        Picasso.get()
                .load(clientModel.getStoreImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.station_image_placeholder)
                .into(holder.storeImage);

    }


    @Override
    public int getItemCount() {
        return clientModelList.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView station;
        TextView company, address;
        ImageView storeImage;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);

            station = itemView.findViewById(R.id.station);
            company = itemView.findViewById(R.id.company);
            address = itemView.findViewById(R.id.address);
            storeImage = itemView.findViewById(R.id.storeImage);
        }
    }
}
